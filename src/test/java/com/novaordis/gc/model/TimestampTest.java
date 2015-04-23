package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class TimestampTest extends Assert
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(TimestampTest.class);

    public static final SimpleDateFormat REFERENCE_TIMESTAMP_FORMAT = new SimpleDateFormat("yy/MM/dd HH:mm:ss.SSS Z");

    public static final SimpleDateFormat TEST_DATE_FORMAT = new SimpleDateFormat("yy/MM/dd HH:mm:ss,SSS Z");


    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void basic() throws Exception
    {
        Timestamp ts = new Timestamp("1.001", 2L, null, false);

        assertEquals(1003L, ts.getTime());
        assertEquals(1001L, ts.getOffset());
        assertEquals("1.001", ts.getLiteral());
    }

    @Test
    public void testEquals_SameOffset() throws Exception
    {
        Timestamp ts = new Timestamp("1.000", 7L, null, false);
        Timestamp ts2 = new Timestamp("1.000", 7L, null, false);

        assertEquals(ts, ts2);
        assertEquals(ts2, ts);
    }

    @Test
    public void testEquals_SameExplicitTimeStamp() throws Exception
    {
        Timestamp ts = new Timestamp("2013-10-10T14:33:21.747-0500: 7.954", null, null, false);
        Timestamp ts2 = new Timestamp("2013-10-10T14:33:21.747-0500: 7.954", null, null, false);

        assertEquals(ts, ts2);
        assertEquals(ts2, ts);
    }

    @Test
    public void testEquals_SameActualTime() throws Exception
    {

        long origin = Timestamp.EXPLICIT_TIMESTAMP_FORMAT.parse("2001-01-01T01:01:01.000-0800").getTime();

        Timestamp ts = new Timestamp("2001-01-01T01:01:01.001-0800: 7.954", null, null, false);
        Timestamp ts2 = new Timestamp("0.001", origin, null, false);

        assertEquals(ts, ts2);
        assertEquals(ts2, ts);
    }

    @Test
    public void noExplicitTimestampAndNoTimeOriginMeansFailure() throws Exception
    {
         try
         {
             new Timestamp("1.000", null, null, false);
             fail("should fail, no time origin");
         }
         catch(UserErrorException e)
         {
             log.info(e.getMessage());
         }
    }

    // offset-to-milliseconds ------------------------------------------------------------------------------------------

    @Test
    public void offsetToMilliseconds() throws Exception
    {
        assertEquals(100101L, Timestamp.offsetToMilliseconds("100.101"));
    }

    @Test
    public void offsetToMilliseconds_2() throws Exception
    {
        assertEquals(100000L, Timestamp.offsetToMilliseconds("100.000"));
    }

    @Test
    public void offsetToMilliseconds_3() throws Exception
    {
        assertEquals(1L, Timestamp.offsetToMilliseconds("0.001"));
    }

    @Test
    public void offsetToMilliseconds_MissingDot() throws Exception
    {
        try
        {
            Timestamp.offsetToMilliseconds("10");
            fail("should have failed, dot is missing");
        }
        catch(Exception e)
        {
            log.info(e.getMessage());
        }
    }

    // synthetic constructor -------------------------------------------------------------------------------------------

    @Test
    public void syntheticConstructor() throws Exception
    {
        Timestamp t = new Timestamp(1L, 1L);
        assertEquals(1L, t.getTime());
        assertEquals(1L, t.getOffset());
        assertEquals("0.001", t.getLiteral());
    }

    @Test
    public void syntheticConstructor_2() throws Exception
    {
        Timestamp t = new Timestamp(11L, 10L);
        assertEquals(11L, t.getTime());
        assertEquals(10L, t.getOffset());
        assertEquals("0.010", t.getLiteral());
    }

    @Test
    public void syntheticConstructor_3() throws Exception
    {
        Timestamp t = new Timestamp(122L, 121L);
        assertEquals(122L, t.getTime());
        assertEquals(121L, t.getOffset());
        assertEquals("0.121", t.getLiteral());
    }

    @Test
    public void syntheticConstructor_4() throws Exception
    {
        Timestamp t = new Timestamp(1223L, 1222L);
        assertEquals(1223L, t.getTime());
        assertEquals(1222L, t.getOffset());
        assertEquals("1.222", t.getLiteral());
    }

    // explicit timestamp ----------------------------------------------------------------------------------------------

    @Test
    public void explicitTimestamp() throws Exception
    {
        Timestamp t = new Timestamp("2013-10-10T14:33:21.747-0500: 7.954", null, null, false);

        assertEquals("2013-10-10T14:33:21.747-0500", t.getExplicitTimestampLiteral());
        assertEquals("7.954", t.getLiteral());
        assertEquals(REFERENCE_TIMESTAMP_FORMAT.parse("13/10/10 14:33:21.747 -0500").getTime(), t.getTime());
    }

    @Test
    public void invalidExplicitTimestamp() throws Exception
    {
        // valid, but unhandled pattern
        Timestamp t = new Timestamp("2013-10-10 14:33:21.747: 7.954", null, null, false);

        assertEquals("2013-10-10 14:33:21.747", t.getExplicitTimestampLiteral());
        assertEquals("7.954", t.getLiteral());
        assertEquals(7954L, t.getTime());
    }
    @Test
    public void explicitTimestamp_BothExplicitAndOffsetPresentAndMatching() throws Exception
    {
        Long origin = REFERENCE_TIMESTAMP_FORMAT.parse("01/01/01 01:01:00.000 -0100").getTime();

        Timestamp t = new Timestamp("2001-01-01T01:01:01.001-0100: 1.001", origin, null, false);

        assertEquals("2001-01-01T01:01:01.001-0100", t.getExplicitTimestampLiteral());
        assertEquals("1.001", t.getLiteral());
        assertEquals(origin + 1001L, t.getTime());
    }

    @Test
    public void explicitTimestamp_BothExplicitAndOffsetPresentAndInConflict() throws Exception
    {
        Long origin = REFERENCE_TIMESTAMP_FORMAT.parse("01/01/01 01:01:00.000 -0100").getTime();

        // the mismatch is 1 ms
        Timestamp t = new Timestamp("2001-01-01T01:01:01.000-0100: 1.001", origin, null, false);

        assertEquals("2001-01-01T01:01:01.000-0100", t.getExplicitTimestampLiteral());
        assertEquals("1.001", t.getLiteral());
        assertEquals(origin + 1000L, t.getTime()); // the explicit timestamp takes precedence
    }

    @Test
    public void noExplicitTimestamp_NoTimeOrigin() throws Exception
    {
        try
        {
            new Timestamp("1.001", null, null, false);
            fail("should have failed");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // Timestamp.isTimestamp() protected -------------------------------------------------------------------------------

    @Test
    public void isTimestamp_null() throws Exception
    {
        assertFalse(Timestamp.isTimestamp(null));
    }

    @Test
    public void isTimestamp_Offset() throws Exception
    {
        assertTrue(Timestamp.isTimestamp("1.001"));
    }

    @Test
    public void isTimestamp_EXPLICIT_TIMESTAMP_FORMAT() throws Exception
    {
        assertTrue(Timestamp.isTimestamp("2014-08-14T01:12:28.621-0700"));
    }

    @Test
    public void isTimestamp_SomethingElse() throws Exception
    {
        assertFalse(Timestamp.isTimestamp("CMS"));
    }

    // find datestamp --------------------------------------------------------------------------------------------------

    @Test
    public void find_Datestamp_testNullArgument() throws Exception
    {
        try
        {
            Timestamp.find(null, 0);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void find_Datestamp_testNotMatching() throws Exception
    {
        String s = "blah";

        Timestamp r = Timestamp.find(s, 0);
        assertNull(r);
    }

    @Test
    public void find_Datestamp_testByItself() throws Exception
    {
        String s = "2014-08-14T01:53:16.892-0700";

        Timestamp r = Timestamp.find(s, 0);
        assertNotNull(r);

        assertEquals(0, r.getStartPosition());
        assertEquals(s.length(), r.getEndPosition());
        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,892 -0700").getTime(), r.getTime());
    }

    @Test
    public void find_Datestamp_testTrailingChar() throws Exception
    {
        String s = "2014-09-14T01:53:16.892-0700:";

        Timestamp r = Timestamp.find(s, 0);
        assertNotNull(r);

        assertEquals(0, r.getStartPosition());
        assertEquals(s.length() - 1, r.getEndPosition());
        assertEquals(TEST_DATE_FORMAT.parse("14/09/14 01:53:16,892 -0700").getTime(), r.getTime());
    }

    @Test
    public void find_Datestamp_testTwoOccurrencesOnSameLine() throws Exception
    {
        String s = "2014-08-14T01:53:16.892-0700: something blah blah2014-08-14T01:53:16.893-0700";

        Timestamp r = Timestamp.find(s, 0);
        assertNotNull(r);

        assertEquals(0, r.getStartPosition());
        assertEquals(28, r.getEndPosition());
        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,892 -0700").getTime(), r.getTime());

        r = Timestamp.find(s, r.getEndPosition());
        assertNotNull(r);

        assertEquals(49, r.getStartPosition());
        assertEquals(s.length(), r.getEndPosition());
        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,893 -0700").getTime(), r.getTime());
    }

    // find timestamp --------------------------------------------------------------------------------------------------

    @Test
    public void find_Timestamp_OffsetInternal() throws Exception
    {
        String s = "[GC 53254.235: [ParNew (promotion failed): 3976407K->4348148K(4373760K), 0.2367350 secs]53254.472: [CMS53258.238: [CMS-concurrent-mark: ...";

        int i = s.indexOf(":");
        Timestamp r = Timestamp.find(s, i);
        assertNotNull(r);

        assertEquals(53254472L, r.getOffset());
        assertEquals(88, r.getStartPosition());
        assertEquals(97, r.getEndPosition());
        assertEquals("53254.472", r.getLiteral());
    }

    @Test
    public void find_Timestamp_OffsetInternal1() throws Exception
    {
        String s = "]0.472 ...";

        Timestamp r = Timestamp.find(s, 0);
        assertNotNull(r);

        assertEquals(472L, r.getOffset());
        assertEquals(1, r.getStartPosition());
        assertEquals(6, r.getEndPosition());
        assertEquals("0.472", r.getLiteral());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



