package com.novaordis.gc.model.expression;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.model.FieldType;

/**
 * Collection of static utilities related to expressions.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Expressions
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    /**
     * If no valid expression can be extracted from the given string, the method returns null.
     *
     * @throws UserErrorException
     */
    public static Expression parse(String s) throws UserErrorException
    {
        try
        {
            ExpressionTree t = new ExpressionTree(s);

            if (t.getOperator() == null)
            {
                return t.getLeft();
            }
            else
            {
                return t;
            }
        }
        catch(UserErrorException e)
        {
            // these bubble up
            throw e;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    private Expressions()
    {
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



