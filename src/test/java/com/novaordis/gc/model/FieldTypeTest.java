package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.event.FullCollection;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.BeforeAfterMax;
import com.novaordis.series.metric.DoubleHeader;
import com.novaordis.series.metric.DoubleMetric;
import com.novaordis.series.metric.LongHeader;
import com.novaordis.series.metric.LongMetric;
import com.novaordis.series.metric.StringHeader;
import com.novaordis.series.metric.StringMetric;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class FieldTypeTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(FieldTypeTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // toHeader() --------------------------------------------------------------------------------------------------------------------------

    @Test
    public void toHeader_OFFSET() throws Exception
    {
        StringHeader h = (StringHeader)FieldType.OFFSET.toHeader(null);
        assertEquals(FieldType.OFFSET.label, h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_COLLECTION_TYPE() throws Exception
    {
        StringHeader h = (StringHeader)FieldType.COLLECTION_TYPE.toHeader(null);
        assertEquals(FieldType.COLLECTION_TYPE.label, h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_DURATION() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.DURATION.toHeader(null);
        assertEquals(FieldType.DURATION.label + " (" + Unit.ms.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_NG_BEFORE() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.NG_BEFORE.toHeader(null);
        assertEquals(FieldType.NG_BEFORE.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_NG_AFTER() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.NG_AFTER.toHeader(null);
        assertEquals(FieldType.NG_AFTER.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_NG_CAPACITY() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.NG_CAPACITY.toHeader(null);
        assertEquals(FieldType.NG_CAPACITY.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_NG_HEAP_BEFORE() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.HEAP_BEFORE.toHeader(null);
        assertEquals(FieldType.HEAP_BEFORE.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_NG_HEAP_AFTER() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.HEAP_AFTER.toHeader(null);
        assertEquals(FieldType.HEAP_AFTER.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_NG_HEAP_CAPACITY() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.HEAP_CAPACITY.toHeader(null);
        assertEquals(FieldType.HEAP_CAPACITY.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_OG_BEFORE() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.OG_BEFORE.toHeader(null);
        assertEquals(FieldType.OG_BEFORE.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_OG_AFTER() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.OG_AFTER.toHeader(null);
        assertEquals(FieldType.OG_AFTER.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_OG_CAPACITY() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.OG_CAPACITY.toHeader(null);
        assertEquals(FieldType.OG_CAPACITY.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_PG_BEFORE() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.PG_BEFORE.toHeader(null);
        assertEquals(FieldType.PG_BEFORE.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_PG_AFTER() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.PG_AFTER.toHeader(null);
        assertEquals(FieldType.PG_AFTER.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_PG_CAPACITY() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.PG_CAPACITY.toHeader(null);
        assertEquals(FieldType.PG_CAPACITY.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_HEAP_BEFORE() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.HEAP_BEFORE.toHeader(null);
        assertEquals(FieldType.HEAP_BEFORE.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_HEAP_AFTER() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.HEAP_AFTER.toHeader(null);
        assertEquals(FieldType.HEAP_AFTER.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    @Test
    public void toHeader_HEAP_CAPACITY() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.HEAP_CAPACITY.toHeader(null);
        assertEquals(FieldType.HEAP_CAPACITY.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat());
    }

    // toHeader() and unit conversion ------------------------------------------------------------------------------------------------------

    @Test
    public void toHeader_WeDonTConvertNonMemory() throws Exception
    {
        try
        {
            FieldType.DURATION.toHeader(Unit.b);
            fail("because we don't convert non-MEMORY types");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toHeader_NG_BEFORE_Unit_byte() throws Exception
    {
        LongHeader h = (LongHeader)FieldType.NG_BEFORE.toHeader(Unit.b);
        assertEquals(FieldType.NG_BEFORE.label + " (" + Unit.b.label + ")", h.getLabel());
        assertNull(h.getFormat()); // no special format
    }

    @Test
    public void toHeader_NG_BEFORE_Unit_Kilobyte() throws Exception
    {
        DoubleHeader h = (DoubleHeader)FieldType.NG_BEFORE.toHeader(Unit.K);
        assertEquals(FieldType.NG_BEFORE.label + " (" + Unit.K.label + ")", h.getLabel());
        assertEquals(Configuration.MEMORY_FORMAT, h.getFormat());
    }

    @Test
    public void toHeader_NG_BEFORE_Unit_Megabyte() throws Exception
    {
        DoubleHeader h = (DoubleHeader)FieldType.NG_BEFORE.toHeader(Unit.M);
        assertEquals(FieldType.NG_BEFORE.label + " (" + Unit.M.label + ")", h.getLabel());
        assertEquals(Configuration.MEMORY_FORMAT, h.getFormat());
    }

    @Test
    public void toHeader_NG_BEFORE_Unit_Gigabyte() throws Exception
    {
        DoubleHeader h = (DoubleHeader)FieldType.NG_BEFORE.toHeader(Unit.G);
        assertEquals(FieldType.NG_BEFORE.label + " (" + Unit.G.label + ")", h.getLabel());
        assertEquals(Configuration.MEMORY_FORMAT, h.getFormat());
    }

    @Test
    public void toHeader_MismatchedUnit_MemoryToMs() throws Exception
    {
        try
        {
            FieldType.NG_BEFORE.toHeader(Unit.s);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void toHeader_MismatchedUnit_MemoryToS() throws Exception
    {
        try
        {
            FieldType.NG_BEFORE.toHeader(Unit.ms);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // toMetric() String types -------------------------------------------------------------------------------------------------------------

    @Test
    public void testOFFSET_NullValue() throws Exception
    {
        try
        {
            FieldType.OFFSET.toMetric(null, null);
            fail("should have failed with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testOFFSET_NonString() throws Exception
    {
        try
        {
            FieldType.OFFSET.toMetric(10L, null);
            fail("should have failed with type mismatch");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testOFFSET_String() throws Exception
    {
        StringMetric m = (StringMetric)FieldType.OFFSET.toMetric("3177.325", null);

        assertEquals("3177.325", m.getString());
    }

    // toMetric() CollectionType -----------------------------------------------------------------------------------------------------------

    @Test
    public void testCOLLECTION_TYPE_NullValue() throws Exception
    {
        try
        {
            FieldType.COLLECTION_TYPE.toMetric(null, null);
            fail("should have failed with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testCOLLECTION_TYPE_NonCollectionType() throws Exception
    {
        try
        {
            FieldType.COLLECTION_TYPE.toMetric(10L, null);
            fail("should have failed with type mismatch");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testCOLLECTION_TYPE_Full() throws Exception
    {
        StringMetric m = (StringMetric)FieldType.COLLECTION_TYPE.toMetric(CollectionType.FULL_COLLECTION, null);
        assertEquals("FULL", m.getString());
    }

    @Test
    public void testCOLLECTION_TYPE_NG() throws Exception
    {
        StringMetric m = (StringMetric)FieldType.COLLECTION_TYPE.toMetric(CollectionType.NEW_GENERATION_COLLECTION, null);
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION.label, m.getString());
    }

    // toMetric() Long types ---------------------------------------------------------------------------------------------------------------

    @Test
    public void testDURATION_NullValue() throws Exception
    {
        try
        {
            FieldType.DURATION.toMetric(null, null);
            fail("should have failed with IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testDURATION_NonLong() throws Exception
    {
        try
        {
            FieldType.DURATION.toMetric("something", null);
            fail("should have failed with type mismatch");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testDURATION_Long() throws Exception
    {
        LongMetric m = (LongMetric)FieldType.DURATION.toMetric(777L, null);
        assertEquals(777L, m.getLong());
    }

    // toMetric() Unit conversion tests ----------------------------------------------------------------------------------------------------

    @Test
    public void invalidConversionUnit() throws Exception
    {
        try
        {
            FieldType.OFFSET.toMetric("blah", Unit.K);
            fail("should fail, cannot convert String to KB");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }

    }

    @Test
    public void testNG_BEFORE_NoConversion() throws Exception
    {
        LongMetric m = (LongMetric)FieldType.NG_BEFORE.toMetric(1024L, null);
        assertEquals(1024L, m.getLong());
    }

    @Test
    public void testNG_BEFORE_ConversionToBytes() throws Exception
    {
        LongMetric m = (LongMetric)FieldType.NG_BEFORE.toMetric(111L, Unit.b);
        assertEquals(111, m.getLong());
    }

    @Test
    public void testNG_BEFORE_ConversionToKilobytes() throws Exception
    {
        DoubleMetric m = (DoubleMetric)FieldType.NG_BEFORE.toMetric(1024L, Unit.K);
        assertEquals(1.00, m.getDouble(), 0.001);
    }

    @Test
    public void testNG_BEFORE_ConversionToMegabytes() throws Exception
    {
        DoubleMetric m = (DoubleMetric)FieldType.NG_BEFORE.toMetric(1024L * 1024L, Unit.M);
        assertEquals(1.00, m.getDouble(), 0.001);
    }

    @Test
    public void testNG_BEFORE_ConversionToGigabytes() throws Exception
    {
        DoubleMetric m = (DoubleMetric)FieldType.NG_BEFORE.toMetric(1024L * 1024L * 1024L, Unit.G);
        assertEquals(1.00, m.getDouble(), 0.001);
    }

    // Double types ------------------------------------------------------------------------------------------------------------------------

    // TODO - add double types tests when I have a double FieldType.

    // evaluate() --------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testEvaluate_OFFSET_null() throws Exception
    {
        assertNull(FieldType.OFFSET.evaluate(null));
    }

    @Test
    public void testEvaluate_OFFSET_GCEvent_Contains() throws Exception
    {
        GCEvent e = new FullCollection(new Timestamp("1.012", 0L, null, false), 1L, null, null, null, null, false);
        Value v = FieldType.OFFSET.evaluate(e);
        assertNotNull(v);
        assertEquals("1.012", v.getValue());
        assertEquals(FieldType.OFFSET, v.getType());
    }

    @Test
    public void testEvaluate_NG_BEFORE_null() throws Exception
    {
        assertNull(FieldType.NG_BEFORE.evaluate(null));
    }

    @Test
    public void testEvaluate_NG_BEFORE_GCEvent_DoesNotContain() throws Exception
    {
        GCEvent e = new FullCollection(new Timestamp("1.012", 0L, null, false), 1L, null, null, null, null, false);
        Value v = FieldType.NG_BEFORE.evaluate(e);
        assertNull(v);
    }

    @Test
    public void testEvaluate_NG_BEFORE_GCEvent_Contains() throws Exception
    {
        GCEvent e = new FullCollection(new Timestamp("1.012", 0L, null, false), 1L, new BeforeAfterMax(2, 1, 3, Unit.G), null, null, null, false);
        Value v = FieldType.NG_BEFORE.evaluate(e);
        assertNotNull(v);
        assertEquals(2L * 1024 * 1024 * 1024, v.getValue());
        assertEquals(FieldType.NG_BEFORE, v.getType());
    }

    @Test
    public void testEvaluate_OG_AFTER_null() throws Exception
    {
        assertNull(FieldType.OG_AFTER.evaluate(null));
    }

    @Test
    public void testEvaluate_OG_AFTER_GCEvent_DoesNotContain() throws Exception
    {
        GCEvent e = new FullCollection(new Timestamp("1.012", 0L, null, false), 1L, null, null, null, null, false);
        Value v = FieldType.OG_AFTER.evaluate(e);
        assertNull(v);
    }

    @Test
    public void testEvaluate_OG_AFTER_GCEvent_Contains() throws Exception
    {
        GCEvent e = new FullCollection(new Timestamp("1.012", 0L, null, false), 1L, null, new BeforeAfterMax(10, 7, 15, Unit.K), null, null, false);
        Value v = FieldType.OG_AFTER.evaluate(e);
        assertNotNull(v);
        assertEquals(7L * 1024, v.getValue());
        assertEquals(FieldType.OG_AFTER, v.getType());
    }

    @Test
    public void testEvaluate_PG_CAPACITY_null() throws Exception
    {
        assertNull(FieldType.PG_CAPACITY.evaluate(null));
    }

    @Test
    public void testEvaluate_PG_CAPACITY_GCEvent_DoesNotContain() throws Exception
    {
        GCEvent e = new FullCollection(new Timestamp("1.012", 0L, null, false), 1L, null, null, null, null, false);
        Value v = FieldType.PG_CAPACITY.evaluate(e);
        assertNull(v);
    }

    @Test
    public void testEvaluate_PG_CAPACITY_GCEvent_Contains() throws Exception
    {
        GCEvent e = new FullCollection(new Timestamp("1.012", 0L, null, false), 1L, null, null, new BeforeAfterMax(11, 13, 17, Unit.M), null, false);
        Value v = FieldType.PG_CAPACITY.evaluate(e);
        assertNotNull(v);
        assertEquals(17L * 1024 * 1024, v.getValue());
        assertEquals(FieldType.PG_CAPACITY, v.getType());
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



