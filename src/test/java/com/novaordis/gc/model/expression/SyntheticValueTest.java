package com.novaordis.gc.model.expression;

import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.ValueTest;
import com.novaordis.series.metric.DoubleMetric;
import com.novaordis.series.metric.LongMetric;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class SyntheticValueTest extends ValueTest
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SyntheticValueTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testToMetric_Long_NullUnit() throws Exception
    {
        Object value = 10L;
        SyntheticValue v = new SyntheticValue(value);

        LongMetric m = (LongMetric)v.toMetric(null);
        assertEquals(Long.class, m.getType());
        assertEquals(10L, m.getLong());

        log.debug(".");
    }

    @Test
    public void testToMetric_Long_SomeUnit() throws Exception
    {
        Object value = 10L;
        SyntheticValue v = new SyntheticValue(value);

        // Unit should be ignored
        LongMetric m = (LongMetric)v.toMetric(Unit.ms);
        assertEquals(Long.class, m.getType());
        assertEquals(10L, m.getLong());
    }

    @Test
    public void testToMetric_Double_NullUnit() throws Exception
    {
        Object value = 10.1D;
        SyntheticValue v = new SyntheticValue(value);
        DoubleMetric m = (DoubleMetric)v.toMetric(null);
        assertEquals(Double.class, m.getType());
        assertEquals(10.1D, m.getDouble(), 0.0001);
    }

    @Test
    public void testToMetric_Double_SomeUnit() throws Exception
    {
        Object value = 10.1D;
        SyntheticValue v = new SyntheticValue(value);

        // Unit should be ignored
        DoubleMetric m = (DoubleMetric)v.toMetric(Unit.ms);
        assertEquals(Double.class, m.getType());
        assertEquals(10.1D, m.getDouble(), 0.0001);
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    @Override
    protected SyntheticValue getValueToTest(Long l) throws Exception
    {
        return new SyntheticValue(l);
    }

    @Override
    protected SyntheticValue getValueToTest(Double d) throws Exception
    {
        return new SyntheticValue(d);
    }

    @Override
    protected SyntheticValue getValueToTest(String s) throws Exception
    {
        return new SyntheticValue(s);
    }

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



