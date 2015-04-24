package com.novaordis.gc.model;

import com.novaordis.gc.parser.ParserException;
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

    // offsetToLong() --------------------------------------------------------------------------------------------------

    @Test
    public void offsetToLong() throws Exception
    {
        assertEquals(100101L, Timestamp.offsetToLong("100.101", 7L));
    }

    @Test
    public void offsetToLong_2() throws Exception
    {
        assertEquals(100000L, Timestamp.offsetToLong("100.000", 7L));
    }

    @Test
    public void offsetToLong_3() throws Exception
    {
        assertEquals(1L, Timestamp.offsetToLong("0.001", 7L));
    }

    @Test
    public void offsetToLong_MissingDot() throws Exception
    {
        try
        {
            Timestamp.offsetToLong("10", 7L);
            fail("should have failed, dot is missing");
        }
        catch(ParserException e)
        {
            log.info(e.getMessage());
            assertEquals(7L, e.getLineNumber());
        }
    }

    // dateStampToTime() -----------------------------------------------------------------------------------------------

    @Test
    public void dateStampToTime() throws Exception
    {
        long time = Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:29.867-0700").getTime();
        assertEquals(time, Timestamp.dateStampToTime("2014-08-14T01:12:29.867-0700", 7L));
    }

    @Test
    public void dateStampToTime_InvalidFormat() throws Exception
    {
        try
        {
            Timestamp.dateStampToTime("blah", 7L);
            fail("should have failed, invalid format");
        }
        catch(ParserException e)
        {
            log.info(e.getMessage());
            assertEquals(7L, e.getLineNumber());
        }
    }

    // find ------------------------------------------------------------------------------------------------------------

    @Test
    public void find_NullArgument() throws Exception
    {
        try
        {
            Timestamp.find(null, 0, -1L);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void find_offset_BeginningOfLine_SmallValue() throws Exception
    {
        String s = "5.837: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(5837L, t.getOffset().longValue());
        assertNull(t.getTime());
        assertEquals(0, t.getStartPosition());
        assertEquals(7, t.getEndPosition());
        assertEquals("5.837", t.getLiteral());
    }

    @Test
    public void find_offset_BeginningOfLine() throws Exception
    {
        String s = "27036.837: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(27036837L, t.getOffset().longValue());
        assertNull(t.getTime());
        assertEquals(0, t.getStartPosition());
        assertEquals(11, t.getEndPosition());
        assertEquals("27036.837", t.getLiteral());
    }

    @Test
    public void find_offset_MiddleOfLine() throws Exception
    {
        String s = "something something]4575.001: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(4575001L, t.getOffset().longValue());
        assertNull(t.getTime());
        assertEquals(20, t.getStartPosition());
        assertEquals(30, t.getEndPosition());
        assertEquals("4575.001", t.getLiteral());
    }

    @Test
    public void find_offset_MiddleOfLine_NotQualifying() throws Exception
    {
        String s = "GC4575.001: something";
        assertNull(Timestamp.find(s, 0, 7L));
    }

    @Test
    public void find_offset_MiddleOfLine_NotQualifying_NonZeroIndex() throws Exception
    {
        String s = "C4575.001: something";
        assertNull(Timestamp.find(s, 1, 7L));
    }

    @Test
    public void find_offset_BeginningOfLineAndMiddleOfLine() throws Exception
    {
        String s = "7456465.646: something something]7456465.647: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(7456465646L, t.getOffset().longValue());
        assertNull(t.getTime());
        assertEquals(0, t.getStartPosition());
        assertEquals(13, t.getEndPosition());
        assertEquals("7456465.646", t.getLiteral());

        Timestamp t2 = Timestamp.find(s, t.getEndPosition(), 7L);

        assertEquals(7456465647L, t2.getOffset().longValue());
        assertNull(t2.getTime());
        assertEquals(33, t2.getStartPosition());
        assertEquals(46, t2.getEndPosition());
        assertEquals("7456465.647", t2.getLiteral());
    }

    @Test
    public void find_offset_InvalidOneFollowedByValidOne() throws Exception
    {
        String s = "GC7.777: blah blah]9.999: this last one should be valid";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(9999L, t.getOffset().longValue());
        assertNull(t.getTime());
        assertEquals(19, t.getStartPosition());
        assertEquals(26, t.getEndPosition());
        assertEquals("9.999", t.getLiteral());

        assertNull(Timestamp.find(s, t.getEndPosition(), 7L));
    }

    @Test
    public void find_offset_ThreeOccurrences() throws Exception
    {
        String s = "7456465.646: something something]7456465.647: something]7456465.648: something else";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(7456465646L, t.getOffset().longValue());
        assertNull(t.getTime());
        assertEquals(0, t.getStartPosition());
        assertEquals(13, t.getEndPosition());
        assertEquals("7456465.646", t.getLiteral());

        Timestamp t2 = Timestamp.find(s, t.getEndPosition(), 7L);

        assertEquals(7456465647L, t2.getOffset().longValue());
        assertNull(t2.getTime());
        assertEquals(33, t2.getStartPosition());
        assertEquals(46, t2.getEndPosition());
        assertEquals("7456465.647", t2.getLiteral());

        Timestamp t3 = Timestamp.find(s, t2.getEndPosition(), 7L);

        assertEquals(7456465648L, t3.getOffset().longValue());
        assertNull(t3.getTime());
        assertEquals(56, t3.getStartPosition());
        assertEquals(69, t3.getEndPosition());
        assertEquals("7456465.648", t3.getLiteral());
    }

    @Test
    public void find_PrintGCDateStamps_BeginningOfLine() throws Exception
    {
        String s = "2014-08-14T01:12:28.621-0700: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertNull(t.getOffset());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:28.621-0700").getTime(), t.getTime().longValue());
        assertEquals(0, t.getStartPosition());
        assertEquals(30, t.getEndPosition());
        assertEquals("2014-08-14T01:12:28.621-0700", t.getLiteral());
    }

    @Test
    public void find_PrintGCDateStamps_MiddleOfLine() throws Exception
    {
        String s = "something something]2014-08-14T01:14:28.000-0700: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertNull(t.getOffset());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:14:28.000-0700").getTime(), t.getTime().longValue());
        assertEquals(20, t.getStartPosition());
        assertEquals(50, t.getEndPosition());
        assertEquals("2014-08-14T01:14:28.000-0700", t.getLiteral());
    }

    @Test
    public void find_PrintGCDateStamps_MiddleOfLine_NotQualifying() throws Exception
    {
        String s = "GC2014-08-14T01:14:28.000-0700: something";
        assertNull(Timestamp.find(s, 0, 7L));
    }

    @Test
    public void find_PrintGCDateStamps_MiddleOfLine_NotQualifying_NonZeroIndex() throws Exception
    {
        String s = "C2014-08-14T01:14:28.000-0700: something";
        assertNull(Timestamp.find(s, 1, 7L));
    }

    @Test
    public void find_PrintGCDateStamps_BeginningOfLineAndMiddleOfLine() throws Exception
    {
        String s = "2014-08-14T01:12:28.621-0700: something something]2014-08-14T01:12:28.622-0700: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertNull(t.getOffset());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:28.621-0700").getTime(), t.getTime().longValue());
        assertEquals(0, t.getStartPosition());
        assertEquals(30, t.getEndPosition());
        assertEquals("2014-08-14T01:12:28.621-0700", t.getLiteral());

        Timestamp t2 = Timestamp.find(s, t.getEndPosition(), 7L);

        assertNull(t2.getOffset());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:28.622-0700").getTime(), t2.getTime().longValue());
        assertEquals(50, t2.getStartPosition());
        assertEquals(80, t2.getEndPosition());
        assertEquals("2014-08-14T01:12:28.622-0700", t2.getLiteral());
    }

    @Test
    public void find_PrintGCDateStamps_InvalidOneFollowedByValidOne() throws Exception
    {
        String s = "GC2014-08-14T01:12:28.621-0700: blah blah]2015-01-01T01:01:01.111-0700: this last one should be valid";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertNull(t.getOffset());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2015-01-01T01:01:01.111-0700").getTime(), t.getTime().longValue());
        assertEquals(42, t.getStartPosition());
        assertEquals(72, t.getEndPosition());
        assertEquals("2015-01-01T01:01:01.111-0700", t.getLiteral());

        assertNull(Timestamp.find(s, t.getEndPosition(), 7L));
    }

    @Test
    public void find_PrintGCDateStampsAndOffset_BeginningOfLine() throws Exception
    {
        String s = "2014-08-14T01:12:28.621-0700: 27036.837: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(27036837L, t.getOffset().longValue());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:28.621-0700").getTime(), t.getTime().longValue());
        assertEquals(0, t.getStartPosition());
        assertEquals(41, t.getEndPosition());
        assertEquals("2014-08-14T01:12:28.621-0700 27036.837", t.getLiteral());
    }

    @Test
    public void find_PrintGCDateStampsAndOffset_MiddleOfLine() throws Exception
    {
        String s = "something something]2014-08-14T01:12:28.620-0700: 27036.838: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(27036838L, t.getOffset().longValue());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:28.620-0700").getTime(), t.getTime().longValue());
        assertEquals(20, t.getStartPosition());
        assertEquals(61, t.getEndPosition());
        assertEquals("2014-08-14T01:12:28.620-0700 27036.838", t.getLiteral());
    }

    @Test
    public void find_PrintGCDateStampsAndOffset_BeginningOfLineAndMiddleOfLine() throws Exception
    {
        String s = "2014-08-14T01:12:28.621-0700: 27036.837: something something]2014-08-14T01:12:28.622-0700: 27036.838: something";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(27036837L, t.getOffset().longValue());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:28.621-0700").getTime(), t.getTime().longValue());
        assertEquals(0, t.getStartPosition());
        assertEquals(41, t.getEndPosition());
        assertEquals("2014-08-14T01:12:28.621-0700 27036.837", t.getLiteral());

        Timestamp t2 = Timestamp.find(s, t.getEndPosition(), 7L);

        assertEquals(27036838L, t2.getOffset().longValue());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:28.622-0700").getTime(), t2.getTime().longValue());
        assertEquals(61, t2.getStartPosition());
        assertEquals(102, t2.getEndPosition());
        assertEquals("2014-08-14T01:12:28.622-0700 27036.838", t2.getLiteral());
    }

    @Test
    public void find_PrintGCDateStampsAndOffset_InvalidOneFollowedByValidOne() throws Exception
    {
        String s = "GC2014-08-14T01:12:28.621-0700: 27036.837: blah blah]2015-01-01T01:01:01.111-0700: 11111.111: this last one should be valid";

        Timestamp t = Timestamp.find(s, 0, 7L);

        assertEquals(11111111L, t.getOffset().longValue());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2015-01-01T01:01:01.111-0700").getTime(), t.getTime().longValue());
        assertEquals(53, t.getStartPosition());
        assertEquals(94, t.getEndPosition());
        assertEquals("2015-01-01T01:01:01.111-0700 11111.111", t.getLiteral());

        assertNull(Timestamp.find(s, t.getEndPosition(), 7L));
    }

    @Test
    public void find_Collected() throws Exception
    {
        String s = "2014-08-14T01:12:28.621-0700: 27036.837: [GC2014-08-14T01:12:28.622-0700: 27036.837: [ParNew (promotion failed): 471872K->471872K(471872K), 0.3931530 secs]2014-08-14T01:12:29.015-0700: 27037.231: [CMS2014-08-14T01:12:29.867-0700: 27038.083: [CMS-concurrent-preclean: 4.167/17.484 secs] [Times: user=21.55 sys=2.82, real=17.48 secs] (concurrent mode failure): 3667441K->779130K(3670016K), 6.2096720 secs] 4045730K->779130K(4141888K), [CMS Perm : 93567K->92821K(131072K)] icms_dc=100 , 6.6030840 secs] [Times: user=2.31 sys=0.23, real=6.61 secs]";

        Timestamp t = Timestamp.find(s, 41, 7L);

        assertEquals(27037231L, t.getOffset().longValue());
        assertEquals(
            Timestamp.DATESTAMP_FORMAT.parse("2014-08-14T01:12:29.015-0700").getTime(), t.getTime().longValue());
        assertEquals(155, t.getStartPosition());
        assertEquals(196, t.getEndPosition());
        assertEquals("2014-08-14T01:12:29.015-0700 27037.231", t.getLiteral());

        assertNull(Timestamp.find(s, t.getEndPosition(), 7L));
    }

    // applyTimeOrigin() -----------------------------------------------------------------------------------------------

    @Test
    public void applyTimeOrigin_OnlyOffset() throws Exception
    {
        Timestamp ts = new Timestamp("1.111", null, 1111L, -1, -1);

        assertNull(ts.getTime());

        ts.applyTimeOrigin(1L);

        Long time = ts.getTime();
        assertEquals(1112L, time.longValue());
    }

    @Test
    public void applyTimeOrigin_TimeAlreadySet() throws Exception
    {
        Timestamp ts = new Timestamp("this would be a date stamp corresponding to 1001L", 1001L, null, -1, -1);

        assertEquals(1001L, ts.getTime().longValue());
        assertNull(ts.getOffset());

        // this is a noop
        ts.applyTimeOrigin(1L);

        assertEquals(1001L, ts.getTime().longValue());
        assertNull(ts.getOffset());
    }

    @Test
    public void applyTimeOrigin_TimeNotSetButNoOffset() throws Exception
    {
        Timestamp ts = new Timestamp("blah", null, null, -1, -1);

        assertNull(ts.getOffset());
        assertNull(ts.getTime());

        try
        {
            ts.applyTimeOrigin(1L);
            fail("this should fail with IllegalStateException, null offset");
        }
        catch(IllegalStateException e)
        {
            log.info(e.getMessage());
        }
    }

    // getOffsetLiteral() ----------------------------------------------------------------------------------------------

    @Test
    public void getOffsetLiteral_NullOffset() throws Exception
    {
        Timestamp ts = new Timestamp("blah", null, null, -1, -1);

        assertNull(ts.getOffset());
        assertNull(ts.getOffsetLiteral());
    }

    @Test
    public void getOffsetLiteral() throws Exception
    {
        Timestamp ts = new Timestamp("1.001", null, 1001L, -1, -1);

        assertEquals(1001L, ts.getOffset().longValue());
        assertEquals("1.001", ts.getOffsetLiteral());
    }

//    @Test
//    public void find_Datestamp_testNotMatching() throws Exception
//    {
//        String s = "blah";
//
//        Timestamp r = Timestamp.find(s, 0, 7L);
//        assertNull(r);
//    }
//
//    @Test
//    public void find_Datestamp_testByItself() throws Exception
//    {
//        String s = "2014-08-14T01:53:16.892-0700";
//
//        Timestamp r = Timestamp.find(s, 0, 7L);
//        assertNotNull(r);
//
//        assertEquals(0, r.getStartPosition());
//        assertEquals(s.length(), r.getEndPosition());
//        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,892 -0700").getTime(), r.getTime().longValue());
//    }
//
//    @Test
//    public void find_Datestamp_testTrailingChar() throws Exception
//    {
//        String s = "2014-09-14T01:53:16.892-0700:";
//
//        Timestamp r = Timestamp.find(s, 0, 7L);
//        assertNotNull(r);
//
//        assertEquals(0, r.getStartPosition());
//        assertEquals(s.length() - 1, r.getEndPosition());
//        assertEquals(TEST_DATE_FORMAT.parse("14/09/14 01:53:16,892 -0700").getTime(), r.getTime().longValue());
//    }
//
//    @Test
//    public void find_Datestamp_testTwoOccurrencesOnSameLine() throws Exception
//    {
//        String s = "2014-08-14T01:53:16.892-0700: something blah blah2014-08-14T01:53:16.893-0700";
//
//        Timestamp r = Timestamp.find(s, 0, 7L);
//        assertNotNull(r);
//
//        assertEquals(0, r.getStartPosition());
//        assertEquals(28, r.getEndPosition());
//        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,892 -0700").getTime(), r.getTime().longValue());
//
//        r = Timestamp.find(s, r.getEndPosition(), 7L);
//        assertNotNull(r);
//
//        assertEquals(49, r.getStartPosition());
//        assertEquals(s.length(), r.getEndPosition());
//        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,893 -0700").getTime(), r.getTime().longValue());
//    }
//
//    @Test
//    public void find_Timestamp_OffsetInternal() throws Exception
//    {
//        String s = "[GC 53254.235: [ParNew (promotion failed): 3976407K->4348148K(4373760K), 0.2367350 secs]53254.472: [CMS53258.238: [CMS-concurrent-mark: ...";
//
//        int i = s.indexOf(":");
//        Timestamp r = Timestamp.find(s, i, 7L);
//        assertNotNull(r);
//
//        assertEquals(53254472L, r.getOffset().longValue());
//        assertEquals(88, r.getStartPosition());
//        assertEquals(97, r.getEndPosition());
//        assertEquals("53254.472", r.getLiteral());
//    }
//
//    @Test
//    public void find_Timestamp_OffsetInternal1() throws Exception
//    {
//        String s = "]0.472 ...";
//
//        Timestamp r = Timestamp.find(s, 0, 7L);
//        assertNotNull(r);
//
//        assertEquals(472L, r.getOffset().longValue());
//        assertEquals(1, r.getStartPosition());
//        assertEquals(6, r.getEndPosition());
//        assertEquals("0.472", r.getLiteral());
//    }



//
//    // Timestamp.isTimestamp() protected -------------------------------------------------------------------------------
//
//    @Test
//    public void isTimestamp_null() throws Exception
//    {
//        assertFalse(Timestamp.isTimestamp(null));
//    }
//
//    @Test
//    public void isTimestamp_Offset() throws Exception
//    {
//        assertTrue(Timestamp.isTimestamp("1.001"));
//    }
//
//    @Test
//    public void isTimestamp_EXPLICIT_TIMESTAMP_FORMAT() throws Exception
//    {
//        assertTrue(Timestamp.isTimestamp("2014-08-14T01:12:28.621-0700"));
//    }
//
//    @Test
//    public void isTimestamp_SomethingElse() throws Exception
//    {
//        assertFalse(Timestamp.isTimestamp("CMS"));
//    }










    //    @Test
//    public void basic() throws Exception
//    {
//        Timestamp ts = new Timestamp("1.001", 2L, null, false);
//
//        assertEquals(1003L, ts.getTime().longValue());
//        assertEquals(1001L, ts.getOffset().longValue());
//        assertEquals("1.001", ts.getLiteral());
//    }
//
//    @Test
//    public void testEquals_SameOffset() throws Exception
//    {
//        Timestamp ts = new Timestamp("1.000", 7L, null, false);
//        Timestamp ts2 = new Timestamp("1.000", 7L, null, false);
//
//        assertEquals(ts, ts2);
//        assertEquals(ts2, ts);
//    }
//
//    @Test
//    public void testEquals_SameExplicitTimeStamp() throws Exception
//    {
//        Timestamp ts = new Timestamp("2013-10-10T14:33:21.747-0500: 7.954", null, null, false);
//        Timestamp ts2 = new Timestamp("2013-10-10T14:33:21.747-0500: 7.954", null, null, false);
//
//        assertEquals(ts, ts2);
//        assertEquals(ts2, ts);
//    }
//
//    @Test
//    public void testEquals_SameActualTime() throws Exception
//    {
//
//        long origin = Timestamp.EXPLICIT_TIMESTAMP_FORMAT.parse("2001-01-01T01:01:01.000-0800").getTime();
//
//        Timestamp ts = new Timestamp("2001-01-01T01:01:01.001-0800: 7.954", null, null, false);
//        Timestamp ts2 = new Timestamp("0.001", origin, null, false);
//
//        assertEquals(ts, ts2);
//        assertEquals(ts2, ts);
//    }
//
//    @Test
//    public void noExplicitTimestampAndNoTimeOriginMeansFailure() throws Exception
//    {
//         try
//         {
//             new Timestamp("1.000", null, null, false);
//             fail("should fail, no time origin");
//         }
//         catch(UserErrorException e)
//         {
//             log.info(e.getMessage());
//         }
//    }
//
//    // synthetic constructor -------------------------------------------------------------------------------------------
//
//    @Test
//    public void syntheticConstructor() throws Exception
//    {
//        Timestamp t = new Timestamp(1L, 1L);
//        assertEquals(1L, t.getTime().longValue());
//        assertEquals(1L, t.getTime().longValue());
//        assertEquals("0.001", t.getLiteral());
//    }
//
//    @Test
//    public void syntheticConstructor_2() throws Exception
//    {
//        Timestamp t = new Timestamp(11L, 10L);
//        assertEquals(11L, t.getTime().longValue());
//        assertEquals(10L, t.getTime().longValue());
//        assertEquals("0.010", t.getLiteral());
//    }
//
//    @Test
//    public void syntheticConstructor_3() throws Exception
//    {
//        Timestamp t = new Timestamp(122L, 121L);
//        assertEquals(122L, t.getTime().longValue());
//        assertEquals(121L, t.getTime().longValue());
//        assertEquals("0.121", t.getLiteral());
//    }
//
//    @Test
//    public void syntheticConstructor_4() throws Exception
//    {
//        Timestamp t = new Timestamp(1223L, 1222L);
//        assertEquals(1223L, t.getTime().longValue());
//        assertEquals(1222L, t.getTime().longValue());
//        assertEquals("1.222", t.getLiteral());
//    }
//
//    // explicit timestamp ----------------------------------------------------------------------------------------------
//
//    @Test
//    public void explicitTimestamp() throws Exception
//    {
//        Timestamp t = new Timestamp("2013-10-10T14:33:21.747-0500: 7.954", null, null, false);
//
//        assertEquals("2013-10-10T14:33:21.747-0500", t.getExplicitTimestampLiteral());
//        assertEquals("7.954", t.getLiteral());
//        assertEquals(REFERENCE_TIMESTAMP_FORMAT.parse("13/10/10 14:33:21.747 -0500").getTime(), t.getTime().longValue());
//    }
//
//    @Test
//    public void invalidExplicitTimestamp() throws Exception
//    {
//        // valid, but unhandled pattern
//        Timestamp t = new Timestamp("2013-10-10 14:33:21.747: 7.954", null, null, false);
//
//        assertEquals("2013-10-10 14:33:21.747", t.getExplicitTimestampLiteral());
//        assertEquals("7.954", t.getLiteral());
//        assertEquals(7954L, t.getTime().longValue());
//    }
//    @Test
//    public void explicitTimestamp_BothExplicitAndOffsetPresentAndMatching() throws Exception
//    {
//        Long origin = REFERENCE_TIMESTAMP_FORMAT.parse("01/01/01 01:01:00.000 -0100").getTime();
//
//        Timestamp t = new Timestamp("2001-01-01T01:01:01.001-0100: 1.001", origin, null, false);
//
//        assertEquals("2001-01-01T01:01:01.001-0100", t.getExplicitTimestampLiteral());
//        assertEquals("1.001", t.getLiteral());
//        assertEquals(origin + 1001L, t.getTime().longValue());
//    }
//
//    @Test
//    public void explicitTimestamp_BothExplicitAndOffsetPresentAndInConflict() throws Exception
//    {
//        Long origin = REFERENCE_TIMESTAMP_FORMAT.parse("01/01/01 01:01:00.000 -0100").getTime();
//
//        // the mismatch is 1 ms
//        Timestamp t = new Timestamp("2001-01-01T01:01:01.000-0100: 1.001", origin, null, false);
//
//        assertEquals("2001-01-01T01:01:01.000-0100", t.getExplicitTimestampLiteral());
//        assertEquals("1.001", t.getLiteral());
//        assertEquals(origin + 1000L, t.getTime().longValue()); // the explicit timestamp takes precedence
//    }
//
//    @Test
//    public void noExplicitTimestamp_NoTimeOrigin() throws Exception
//    {
//        try
//        {
//            new Timestamp("1.001", null, null, false);
//            fail("should have failed");
//        }
//        catch(UserErrorException e)
//        {
//            log.info(e.getMessage());
//        }
//    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



