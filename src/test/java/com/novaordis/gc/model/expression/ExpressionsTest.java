package com.novaordis.gc.model.expression;

import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.event.FullCollection;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.BeforeAfterMax;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ExpressionsTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ExpressionsTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testOffset() throws Exception
    {
        Expression e = Expressions.parse(FieldType.OFFSET.commandLineLabel);
        assertEquals(FieldType.OFFSET, e);
        log.debug(".");
    }

    @Test
    public void testCollectionType() throws Exception
    {
        Expression e = Expressions.parse(FieldType.COLLECTION_TYPE.commandLineLabel);
        assertEquals(FieldType.COLLECTION_TYPE, e);
    }

    @Test
    public void testDURATION() throws Exception
    {
        Expression e = Expressions.parse(FieldType.DURATION.commandLineLabel);
        assertEquals(FieldType.DURATION, e);
    }

    @Test
    public void testNG_BEFORE() throws Exception
    {
        Expression e = Expressions.parse(FieldType.NG_BEFORE.commandLineLabel);
        assertEquals(FieldType.NG_BEFORE, e);
    }

    @Test
    public void testNG_AFTER() throws Exception
    {
        Expression e = Expressions.parse(FieldType.NG_AFTER.commandLineLabel);
        assertEquals(FieldType.NG_AFTER, e);
    }

    @Test
    public void testNG_CAPACITY() throws Exception
    {
        Expression e = Expressions.parse(FieldType.NG_CAPACITY.commandLineLabel);
        assertEquals(FieldType.NG_CAPACITY, e);
    }
    @Test
    public void testNG_HEAP_BEFORE() throws Exception
    {
        Expression e = Expressions.parse(FieldType.HEAP_BEFORE.commandLineLabel);
        assertEquals(FieldType.HEAP_BEFORE, e);
    }
    @Test
    public void testNG_HEAP_AFTER() throws Exception
    {
        Expression e = Expressions.parse(FieldType.HEAP_AFTER.commandLineLabel);
        assertEquals(FieldType.HEAP_AFTER, e);
    }
    @Test
    public void testNG_HEAP_CAPACITY() throws Exception
    {
        Expression e = Expressions.parse(FieldType.HEAP_CAPACITY.commandLineLabel);
        assertEquals(FieldType.HEAP_CAPACITY, e);
    }
    @Test
    public void testOG_BEFORE() throws Exception
    {
        Expression e = Expressions.parse(FieldType.OG_BEFORE.commandLineLabel);
        assertEquals(FieldType.OG_BEFORE, e);
    }
    @Test
    public void testOG_AFTER() throws Exception
    {
        Expression e = Expressions.parse(FieldType.OG_AFTER.commandLineLabel);
        assertEquals(FieldType.OG_AFTER, e);
    }
    @Test
    public void testOG_CAPACITY() throws Exception
    {
        Expression e = Expressions.parse(FieldType.OG_CAPACITY.commandLineLabel);
        assertEquals(FieldType.OG_CAPACITY, e);
    }
    @Test
    public void testPG_BEFORE() throws Exception
    {
        Expression e = Expressions.parse(FieldType.PG_BEFORE.commandLineLabel);
        assertEquals(FieldType.PG_BEFORE, e);
    }
    @Test
    public void testPG_AFTER() throws Exception
    {
        Expression e = Expressions.parse(FieldType.PG_AFTER.commandLineLabel);
        assertEquals(FieldType.PG_AFTER, e);
    }

    @Test
    public void testPG_CAPACITY() throws Exception
    {
        Expression e = Expressions.parse(FieldType.PG_CAPACITY.commandLineLabel);
        assertEquals(FieldType.PG_CAPACITY, e);
    }

    @Test
    public void testHEAP_BEFORE() throws Exception
    {
        Expression e = Expressions.parse(FieldType.HEAP_BEFORE.commandLineLabel);
        assertEquals(FieldType.HEAP_BEFORE, e);
    }

    @Test
    public void testHEAP_AFTER() throws Exception
    {
        Expression e = Expressions.parse(FieldType.HEAP_AFTER.commandLineLabel);
        assertEquals(FieldType.HEAP_AFTER, e);
    }

    @Test
    public void testHEAP_CAPACITY() throws Exception
    {
        Expression e = Expressions.parse(FieldType.HEAP_CAPACITY.commandLineLabel);
        assertEquals(FieldType.HEAP_CAPACITY, e);
    }

    // multi-field expressions -------------------------------------------------------------------------------------------------------------

    @Test
    public void testMultiFieldExpression() throws Exception
    {
        Expression e = Expressions.parse("og-after/og-capacity*100");
        assertNotNull(e);

        GCEvent gce = new FullCollection(new Timestamp("0.001", 0L, null, false),
                1L, null, new BeforeAfterMax(1, 2, 8, Unit.G), null, null, false);
        SyntheticValue v = (SyntheticValue)e.evaluate(gce);
        assertEquals(25.00D, (Double)v.getValue(), 0.001);
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



