package com.novaordis.gc.model.expression;

import com.novaordis.gc.model.FieldCategory;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.model.event.FullCollection;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.BeforeAfterMax;
import com.novaordis.series.metric.DoubleHeader;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ExpressionTreeTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ExpressionTreeTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testNoOperator() throws Exception
    {
        ExpressionTree t = new ExpressionTree("og-after");

        assertNull(t.getOperator());

        // left

        Expression e = t.getLeft();
        assertNotNull(e);
        assertEquals(FieldType.OG_AFTER, e);

        // right

        assertNull(t.getRight());

        // entire expression

        assertEquals(FieldCategory.MEMORY, t.getCategory());
        DoubleHeader h = (DoubleHeader)t.toHeader(Unit.K);
        assertEquals(FieldType.OG_AFTER.label, h.getName());
        assertEquals("KB", h.getMeasureUnit());
        assertEquals("Old Generation Final Occupancy (KB)", h.getLabel());

        GCEvent gce = new FullCollection(new Timestamp("1.000", 1L, null, false),
                1L, null, new BeforeAfterMax(3, 2, 4, Unit.K), null, null, false);

        Value v = t.evaluate(gce);
        assertEquals(2L * 1024, ((Long) v.getValue()).longValue());
        assertEquals(FieldType.OG_AFTER, v.getType());

        log.debug(".");
    }

    @Test
    public void testOneOperator() throws Exception
    {
        ExpressionTree t = new ExpressionTree("og-after/og-capacity");

        assertEquals(Operator.DIVISION, t.getOperator());

        // left

        Expression left = t.getLeft();
        assertNotNull(left);
        assertEquals(FieldType.OG_AFTER, left);

        // right

        Expression right = t.getRight();
        assertNotNull(right);
        assertEquals(FieldType.OG_CAPACITY, right);

        // entire expression

        assertEquals(null, t.getCategory());
        DoubleHeader h = (DoubleHeader)t.toHeader(Unit.K);
        assertEquals("Old Generation Final Occupancy/Old Generation Capacity", h.getName());
        assertEquals("Old Generation Final Occupancy/Old Generation Capacity", h.getLabel());
        assertNull(h.getMeasureUnit());

        GCEvent gce = new FullCollection(new Timestamp("1.000", 1L, null, false),
                1L, null, new BeforeAfterMax(3, 2, 4, Unit.K), null, null, false);

        Value v = t.evaluate(gce);
        assertEquals(0.5, (Double)v.getValue(), 0.0001);
        assertNull(v.getType());
    }

    @Test
    public void testTwoOperators() throws Exception
    {
        ExpressionTree t = new ExpressionTree("og-after/og-capacity*100");

        assertEquals(Operator.MULTIPLICATION, t.getOperator());

        // left

        Expression left = t.getLeft();
        assertNotNull(left);

        ExpressionTree leftTree = (ExpressionTree)left;
        Operator leftOperand = leftTree.getOperator();
        assertEquals(Operator.DIVISION, leftOperand);
        Expression leftTreeLeftOperand = leftTree.getLeft();
        FieldType ft = (FieldType)leftTreeLeftOperand;
        assertEquals(FieldType.OG_AFTER, ft);
        Expression leftTreeRightOperand = leftTree.getRight();
        FieldType ft2 = (FieldType)leftTreeRightOperand;
        assertEquals(FieldType.OG_CAPACITY, ft2);

        // right

        Expression right = t.getRight();
        assertNotNull(right);
        Constant c = (Constant)right;
        assertEquals(100L, c.getValue());

        // entire expression

        assertEquals(null, t.getCategory());
        DoubleHeader h = (DoubleHeader)t.toHeader(Unit.K);
        assertEquals("Old Generation Final Occupancy/Old Generation Capacity*100", h.getName());
        assertEquals("Old Generation Final Occupancy/Old Generation Capacity*100", h.getLabel());
        assertNull(h.getMeasureUnit());

        GCEvent gce = new FullCollection(new Timestamp("1.000", 1L, null, false),
                1L, null, new BeforeAfterMax(3, 2, 4, Unit.K), null, null, false);

        Value v = t.evaluate(gce);
        assertEquals(50.0D, (Double)v.getValue(), 0.0001);
        assertNull(v.getType());
    }

    // evaluate() --------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testEvaluate_OneOperator_OneOperandCannotBeEvaluated() throws Exception
    {
        ExpressionTree t = new ExpressionTree("og-after/ng-capacity");

        GCEvent gce = new FullCollection(new Timestamp("1.000", 1L, null, false),
                1L, null, new BeforeAfterMax(3, 2, 4, Unit.K), null, null, false);

        assertNull(t.evaluate(gce));
    }

    @Test
    public void testEvaluate_OneOperator_TwoOperandsCannotBeEvaluated() throws Exception
    {
        ExpressionTree t = new ExpressionTree("og-after/ng-capacity");

        GCEvent gce = new FullCollection(new Timestamp("1.000", 1L, null, false),
                1L, null, null, null, null, false);

        assertNull(t.evaluate(gce));
    }

    // toHeader() --------------------------------------------------------------------------------------------------------------------------

    /**
     * This test insures that a expression result that is a double produces a header that requests two decimal display.
     */
    @Test
    public void testDoubleExpressionDisplaysWithTwoDecimals() throws Exception
    {
        ExpressionTree t = new ExpressionTree("synthetic_double*1");

        DoubleHeader h = (DoubleHeader)t.toHeader(null);
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



