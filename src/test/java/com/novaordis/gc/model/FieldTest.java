package com.novaordis.gc.model;

import com.novaordis.series.metric.DoubleMetric;
import com.novaordis.series.metric.LongMetric;
import com.novaordis.series.metric.StringMetric;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class FieldTest extends ValueTest
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(FieldTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void offsetToMetric() throws Exception
    {
        Value f = new Field(FieldType.OFFSET, "1.001");
        StringMetric m = (StringMetric)f.toMetric(null);
        assertEquals("1.001", m.getString());
    }

    @Test
    public void collectionTypeToMetric() throws Exception
    {
        Value f = new Field(FieldType.COLLECTION_TYPE, CollectionType.NEW_GENERATION_COLLECTION);
        StringMetric m = (StringMetric)f.toMetric(null);
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION.label, m.getString());
    }

    @Test
    public void durationToMetric() throws Exception
    {
        Value f = new Field(FieldType.DURATION, 100L);
        LongMetric m = (LongMetric)f.toMetric(null);
        assertEquals(100L, m.getLong());
    }

    @Test
    public void ngAfterToMetric() throws Exception
    {
        Value f = new Field(FieldType.NG_AFTER, 1L);
        LongMetric m = (LongMetric)f.toMetric(null);
        assertEquals(1L, m.getLong());
    }

    @Test
    public void ngAfterToMetric_UnitConversion() throws Exception
    {
        Value f = new Field(FieldType.NG_AFTER, 2L * 1024 * 1024);
        DoubleMetric m = (DoubleMetric)f.toMetric(Unit.M);
        assertEquals(2.0, m.getDouble(), 0.001);
    }

    @Test
    public void toMetric_MismatchedUnit_MemoryToMs() throws Exception
    {
        Value f = new Field(FieldType.NG_AFTER, 1L);

        try
        {
            f.toMetric(Unit.s);
            fail("should fail with UserErrorException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toMetric_MismatchedUnit_MemoryToS() throws Exception
    {
        Value f = new Field(FieldType.NG_AFTER, 1L);

        try
        {
            f.toMetric(Unit.ms);
            fail("should fail with UserErrorException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    // TODO - add double types tests when I have a double FieldType

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    @Override
    protected Field getValueToTest(Long l) throws Exception
    {
        return new Field(FieldType.DURATION, l);
    }

    @Override
    protected Field getValueToTest(Double d) throws Exception
    {
        return new Field(FieldType.SYNTHETIC_DOUBLE, d);
    }

    @Override
    protected Field getValueToTest(String s) throws Exception
    {
        return new Field(FieldType.OFFSET, s);
    }

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



