package com.novaordis.gc.model.expression;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.FieldCategory;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.series.Header;
import com.novaordis.series.metric.DoubleHeader;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ExpressionTree implements Expression
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Operator operator;
    private Expression left;
    private Expression right;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public ExpressionTree(String s) throws Exception
    {
        int i;

        outer: for(i = s.length() - 1; i >= 0; i --)
        {
            for(Operator o: Operator.values())
            {
                String fragment = s.substring(i);
                if (fragment.startsWith(o.symbol))
                {
                    operator = o;
                    break outer;
                }
            }
        }

        if (operator != null)
        {

            left = new ExpressionTree(s.substring(0, i));

            // if we don't have an operator, optimize the expression to eliminate an unnecessary ExpressionTree instance
            if (((ExpressionTree)left).getOperator() == null)
            {
                left = ((ExpressionTree)left).getLeft();
            }

            right = new ExpressionTree(s.substring(i + operator.symbol.length()));

            // if we don't have an operator, optimize the expression to eliminate an unnecessary ExpressionTree instance
            if (((ExpressionTree)right).getOperator() == null)
            {
                right = ((ExpressionTree)right).getLeft();
            }
        }
        else
        {
            // attempt identify individual field types

            for(FieldType ft: FieldType.values())
            {
                if (ft.commandLineLabel.equals(s))
                {
                    left = ft;
                    return;
                }
            }

            // attempt to identify numeric constants

            try
            {
                left = new Constant(Long.parseLong(s));
                return;
            }
            catch(Exception e)
            {
                // conversion failed, it's fine, it's not a long, try double
                try
                {
                    left = new Constant(Double.parseDouble(s));
                    return;
                }
                catch(Exception e2)
                {
                    // conversion failed
                }
            }

            throw new UserErrorException("invalid expression \"" + s + "\"");
        }
    }

    // Expression implementation -----------------------------------------------------------------------------------------------------------

    @Override
    public Header toHeader(Unit targetUnit) throws Exception
    {
        if (operator == null)
        {
            return left.toHeader(targetUnit);
        }
        else
        {
            Header leftHeader = left.toHeader(null);
            Class leftHeaderType = leftHeader.getType();
            Header rightHeader = right.toHeader(null);
            Class rightHeaderType = rightHeader.getType();

            if (Operator.DIVISION.equals(operator))
            {
                if ((Double.class.equals(leftHeaderType) || Long.class.equals(leftHeaderType)) &&
                        (Double.class.equals(rightHeaderType) || Long.class.equals(rightHeaderType)))
                {
                    return new DoubleHeader(leftHeader.getName() + "/" + rightHeader.getName(), null,
                            Configuration.DOUBLE_TWO_DIGITS_AFTER_DECIMAL_SEPARATOR);
                }
                else
                {
                    throw new RuntimeException("NOT YET IMPLEMENTED: don't know the type of the resulting header");
                }
            }
            else if (Operator.MULTIPLICATION.equals(operator))
            {
                if (Double.class.equals(leftHeaderType) || Double.class.equals(rightHeaderType))
                {
                    return new DoubleHeader(leftHeader.getName() + "*" + rightHeader.getName(), null,
                            Configuration.DOUBLE_TWO_DIGITS_AFTER_DECIMAL_SEPARATOR);
                }
                else
                {
                    throw new RuntimeException("NOT YET IMPLEMENTED: don't know the type of the resulting header (2)");
                }
            }
            else
            {
                throw new RuntimeException("NOT YET IMPLEMENTED: " + operator);
            }
        }
    }

    @Override
    public FieldCategory getCategory()
    {
        if (operator == null)
        {
            return left.getCategory();
        }
        else
        {
            if (Operator.DIVISION.equals(operator))
            {
                if (left.getCategory() == null || left.getCategory().equals(right.getCategory()))
                {
                    return null;
                }
                else
                {
                    throw new RuntimeException("NOT YET IMPLEMENTED - division");
                }
            }
            else if (Operator.MULTIPLICATION.equals(operator))
            {
                // not really implemented
                return null;
            }
            else
            {
                throw new RuntimeException("NOT YET IMPLEMENTED: " + operator);
            }
        }
    }

    /**
     * @see Expression#evaluate(com.novaordis.gc.model.event.GCEvent)
     */
    @Override
    public Value evaluate(GCEvent e) throws Exception
    {
        if (operator == null)
        {
            return left.evaluate(e);
        }
        else
        {
            Value lv = left.evaluate(e);
            Value rv = right.evaluate(e);

            if (lv == null || rv == null)
            {
                // null means "missing" - cannot perform an operation where one of the operands is missing ...
                return null;
            }

            Object lo = lv.getValue(); // never null, according to the contract
            Object ro = rv.getValue(); // never null, according to the contract

            // TODO - what follows is a lot of heuristics - improve it ...

            if (Operator.DIVISION.equals(operator) || Operator.MULTIPLICATION.equals(operator))
            {
                double lod;
                double rod;

                if (lo instanceof Double)
                {
                    lod = (Double)lo;
                }
                else if (lo instanceof Long)
                {
                    lod = ((Long) lo).doubleValue();
                }
                else
                {
                    throw new RuntimeException("NOT YET IMPLEMENTED: " + lo);
                }

                if (ro instanceof Double)
                {
                    rod = (Double)ro;
                }
                else if (ro instanceof Long)
                {
                    rod = ((Long)ro).doubleValue();
                }
                else
                {
                    throw new RuntimeException("NOT YET IMPLEMENTED: " + ro);
                }

                if (operator.equals(Operator.DIVISION))
                {
                    return new SyntheticValue(lod / rod);
                }
                else
                {
                    return new SyntheticValue(lod * rod);
                }
            }
            else
            {
                throw new RuntimeException("NOT YET IMPLEMENTED");
            }
        }
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    /**
     * May return null if there's no operator.
     */
    public Operator getOperator()
    {
        return operator;
    }

    /**
     * @return the left operand. Never returns null.
     */
    public Expression getLeft()
    {
        return left;
    }

    /**
     * @return the right operand. May return null.
     */
    public Expression getRight()
    {
        return right;
    }

    @Override
    public String toString()
    {
        if (operator == null)
        {
            if (left == null)
            {
                return "null";
            }

            return left.toString();
        }
        else
        {
            return (left == null ? "null" : left.toString()) + operator.symbol + (right == null ? "null" : right.toString());
        }
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



