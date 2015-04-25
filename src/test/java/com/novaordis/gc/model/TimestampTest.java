package com.novaordis.gc.model;

import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class TimestampTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(TimestampTest.class);

    public static final SimpleDateFormat REFERENCE_TIMESTAMP_FORMAT = new SimpleDateFormat("yy/MM/dd HH:mm:ss.SSS Z");

    public static final SimpleDateFormat TEST_DATE_FORMAT = new SimpleDateFormat("yy/MM/dd HH:mm:ss,SSS Z");

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_offset() throws Exception
    {
        Timestamp ts = new Timestamp("1.001", null, 1001L, 5, 12);

        assertNull(ts.getTime());
        assertEquals(1001L, ts.getOffset().longValue());
        assertEquals("1.001", ts.getLiteral());
        assertEquals("1.001", ts.getOffsetLiteral());
        assertNull(ts.getDateStampLiteral());
        assertEquals(5, ts.getStartPosition());
        assertEquals(12, ts.getEndPosition());
    }

    @Test
    public void constructor_offset_mismatch() throws Exception
    {
        try
        {
            new Timestamp("1.001", null, 1002L, 5, 12);
            fail("should have failed with IllegalArgumentException, literal/offset mismatch");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_offset_invalidPosition() throws Exception
    {
        try
        {
            new Timestamp("1.001", null, 1001L, -1, 12);
            fail("should have failed with IllegalArgumentException because the start position is invalid");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_offset_invalidStartEndPositionDifference() throws Exception
    {
        try
        {
            new Timestamp("1.001", null, 1001L, 1, 777);
            fail("should have failed with IllegalArgumentException because the difference between start position and end position is invalid");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_dateStamp() throws Exception
    {
        String literal = "2015-01-01T01:01:01.001-0700";
        long time = Timestamp.DATESTAMP_FORMAT.parse(literal).getTime();

        Timestamp ts = new Timestamp(literal, time, null, 0, 30);

        assertEquals(time, ts.getTime().longValue());
        assertNull(ts.getOffset());
        assertEquals(literal, ts.getLiteral());
        assertNull(ts.getOffsetLiteral());
        assertEquals(literal, ts.getDateStampLiteral());
        assertEquals(0, ts.getStartPosition());
        assertEquals(30, ts.getEndPosition());
    }

    @Test
    public void constructor_dateStamp_mismatch() throws Exception
    {
        String literal = "2015-01-01T01:01:01.001-0700";
        long time = Timestamp.DATESTAMP_FORMAT.parse(literal).getTime();

        try
        {
            new Timestamp(literal, time + 1, null, 0, 30);
            fail("should have failed with IllegalArgumentException, literal/time mismatch");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_combined() throws Exception
    {
        String literal = "2015-01-01T01:01:01.001-0700 1.001";
        long time = Timestamp.DATESTAMP_FORMAT.parse(literal.substring(0, literal.indexOf(' '))).getTime();
        long offset = Timestamp.offsetToLong(literal.substring(literal.indexOf(' ') + 1), null);

        Timestamp ts = new Timestamp(literal, time, offset, 0, 37);

        assertEquals(time, ts.getTime().longValue());
        assertEquals(offset, ts.getOffset().longValue());
        assertEquals(literal, ts.getLiteral());
        assertEquals("1.001", ts.getOffsetLiteral());
        assertEquals("2015-01-01T01:01:01.001-0700", ts.getDateStampLiteral());
        assertEquals(0, ts.getStartPosition());
        assertEquals(37, ts.getEndPosition());
    }

    @Test
    public void constructor_combined_mismatch_time() throws Exception
    {
        String literal = "2015-01-01T01:01:01.001-0700 1.001";
        long time = Timestamp.DATESTAMP_FORMAT.parse(literal.substring(0, literal.indexOf(' '))).getTime();

        try
        {
            new Timestamp(literal, time + 1, 1001L, 0, 37);
            fail("should have failed with IllegalArgumentException, literal/time mismatch");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_combined_mismatch_offset() throws Exception
    {
        String literal = "2015-01-01T01:01:01.001-0700 1.001";
        long time = Timestamp.DATESTAMP_FORMAT.parse(literal.substring(0, literal.indexOf(' '))).getTime();

        try
        {
            new Timestamp(literal, time, 1002L, 0, 37);
            fail("should have failed with IllegalArgumentException, literal/offset mismatch");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void syntheticConstructor() throws Exception
    {
        Timestamp t = new Timestamp(1L);
        assertEquals(1L, t.getOffset().longValue());
        assertNull(t.getTime());
        assertEquals("0.001", t.getLiteral());
        assertEquals("0.001", t.getOffsetLiteral());
        assertNull(t.getDateStampLiteral());
    }

    // equals() --------------------------------------------------------------------------------------------------------

    @Test
    public void testEquals_SyntheticConstructor() throws Exception
    {
        Timestamp ts = new Timestamp(1001L);
        ts.applyTimeOrigin(0);

        Timestamp ts2 = new Timestamp(1001L);
        ts2.applyTimeOrigin(0);

        assertEquals(ts, ts2);
        assertEquals(ts2, ts);
    }

    @Test
    public void testEquals() throws Exception
    {
        String literal = "2015-01-01T01:01:01.001-0700 1.111";
        long time = Timestamp.DATESTAMP_FORMAT.parse(literal.substring(0, literal.indexOf(' '))).getTime();
        long offset = Timestamp.offsetToLong(literal.substring(literal.indexOf(' ') + 1), null);

        Timestamp ts = new Timestamp(literal, time, offset, 0, 37);

        String literal2 = "2015-01-01T01:01:01.001-0700 2.222";
        long time2 = Timestamp.DATESTAMP_FORMAT.parse(literal.substring(0, literal.indexOf(' '))).getTime();
        long offset2 = Timestamp.offsetToLong(literal.substring(literal.indexOf(' ') + 1), null);

        Timestamp ts2 = new Timestamp(literal, time, offset, 0, 37);

        // timestamps are equal if the time is equal - even for different offsets

        assertEquals(ts, ts2);
        assertEquals(ts2, ts);
    }

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

    // longToOffsetLiteral() -------------------------------------------------------------------------------------------

    @Test
    public void longToOffsetLiteral() throws Exception
    {
        assertEquals("0.001", Timestamp.longToOffsetLiteral(1L));
    }

    @Test
    public void longToOffsetLiteral2() throws Exception
    {
        assertEquals("0.010", Timestamp.longToOffsetLiteral(10L));
    }

    @Test
    public void longToOffsetLiteral3() throws Exception
    {
        assertEquals("123.456", Timestamp.longToOffsetLiteral(123456L));
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

    @Test
    public void find_nothing() throws Exception
    {
        String s = "blah";
        Timestamp r = Timestamp.find(s, 0, 7L);
        assertNull(r);
    }

    @Test
    public void find_PrintGCDateStampsAndOffset_byItself() throws Exception
    {
        String s = "2014-08-14T01:53:16.892-0700: ";

        Timestamp r = Timestamp.find(s, 0, 7L);
        assertNotNull(r);

        assertEquals(0, r.getStartPosition());
        assertEquals(s.length(), r.getEndPosition());
        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,892 -0700").getTime(), r.getTime().longValue());
    }

    @Test
    public void find_PrintGCDateStampsAndOffset_testTwoOccurrencesOnSameLine() throws Exception
    {
        String s = "2014-08-14T01:53:16.892-0700: something blah blah]2014-08-14T01:53:16.893-0700: blah";

        Timestamp r = Timestamp.find(s, 0, 7L);
        assertNotNull(r);

        assertEquals(0, r.getStartPosition());
        assertEquals(30, r.getEndPosition());
        assertNull(r.getOffsetLiteral());
        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,892 -0700").getTime(), r.getTime().longValue());

        r = Timestamp.find(s, r.getEndPosition(), 7L);
        assertNotNull(r);

        assertEquals(50, r.getStartPosition());
        assertEquals(80, r.getEndPosition());
        assertNull(r.getOffsetLiteral());
        assertEquals(TEST_DATE_FORMAT.parse("14/08/14 01:53:16,893 -0700").getTime(), r.getTime().longValue());
    }

    @Test
    public void find_offset() throws Exception
    {
        String s = "[GC 53254.235: [ParNew (promotion failed): 3976407K->4348148K(4373760K), 0.2367350 secs]53254.472: [CMS53258.238: [CMS-concurrent-mark: ...";

        Timestamp r = Timestamp.find(s, 0, 7L);
        assertNotNull(r);

        assertEquals(53254472L, r.getOffset().longValue());
        assertEquals(88, r.getStartPosition());
        assertEquals(99, r.getEndPosition());
        assertEquals("53254.472", r.getLiteral());
        assertEquals("53254.472", r.getOffsetLiteral());
    }

    @Test
    public void find_offset_2() throws Exception
    {
        String s = "]0.472: ...";

        Timestamp r = Timestamp.find(s, 0, 7L);
        assertNotNull(r);

        assertEquals(472L, r.getOffset().longValue());
        assertEquals(1, r.getStartPosition());
        assertEquals(8, r.getEndPosition());
        assertEquals("0.472", r.getLiteral());
        assertEquals("0.472", r.getOffsetLiteral());
    }

    // applyTimeOrigin() -----------------------------------------------------------------------------------------------

    @Test
    public void applyTimeOrigin_OnlyOffset() throws Exception
    {
        Timestamp ts = new Timestamp("1.111", null, 1111L, 0, "1.111".length() + 2);

        assertNull(ts.getTime());

        Timestamp ts2 = ts.applyTimeOrigin(1L);

        Long time = ts.getTime();
        assertEquals(1112L, time.longValue());

        assertTrue(ts == ts2);
    }

    @Test
    public void applyTimeOrigin_TimeAlreadySet() throws Exception
    {
        String literal = Timestamp.DATESTAMP_FORMAT.format(1001L);
        Timestamp ts = new Timestamp(literal, 1001L, null, 0, literal.length() + 2);

        assertEquals(1001L, ts.getTime().longValue());
        assertNull(ts.getOffset());

        // this is a noop
        Timestamp ts2 = ts.applyTimeOrigin(1L);

        assertEquals(1001L, ts.getTime().longValue());
        assertNull(ts.getOffset());

        assertTrue(ts == ts2);
    }

    // getOffsetLiteral() ----------------------------------------------------------------------------------------------

    @Test
    public void getOffsetLiteral_NullOffset() throws Exception
    {
        String literal = "2014-08-14T01:12:28.621-0700";
        Timestamp ts = new Timestamp(
            literal, Timestamp.DATESTAMP_FORMAT.parse(literal).getTime(), null, 0, literal.length() + 2);

        assertNull(ts.getOffset());
        assertNull(ts.getOffsetLiteral());
    }

    @Test
    public void getOffsetLiteral() throws Exception
    {
        Timestamp ts = new Timestamp("1.001", null, 1001L, 0, "1.001".length() + 2);

        assertEquals(1001L, ts.getOffset().longValue());
        assertEquals("1.001", ts.getOffsetLiteral());
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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



