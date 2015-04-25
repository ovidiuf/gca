package com.novaordis.gc.parser.linear;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.mock.MockGCEvent;
import com.novaordis.gc.mock.MockGCEventParser;
import com.novaordis.gc.mock.MockReader;
import com.novaordis.gc.mock.PassThroughEventParser;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.FullCollection;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.event.NewGenerationCollection;
import com.novaordis.gc.parser.GCEventParser;
import com.novaordis.gc.parser.GCLogParser;
import com.novaordis.gc.parser.GCLogParserFactory;
import com.novaordis.gc.parser.TimeOrigin;
import com.novaordis.gc.parser.linear.cms.CMSParser;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class LinearScanParserTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LinearScanParserTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void fullAndNewGenerationCollectionCombination() throws Exception
    {
        String s =
            "4.751: [GC [PSYoungGen: 660640K->72890K(1835008K)] 660640K->72890K(6029312K), 0.0515050 secs] [Times: user=0.21 sys=0.09, real=0.05 secs] \n" +
                "4.803: [Full GC (System) [PSYoungGen: 72890K->0K(1835008K)] [PSOldGen: 0K->72238K(4194304K)] 72890K->72238K(6029312K) [PSPermGen: 29286K->29286K(59136K)], 0.2635970 secs] [Times: user=0.24 sys=0.03, real=0.27 secs] \n" +
                "13.989: [GC [PSYoungGen: 1572864K->262124K(1835008K)] 1645102K->347686K(6029312K), 0.1698390 secs] [Times: user=0.77 sys=0.45, real=0.17 secs] \n";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        GCLogParser p = GCLogParserFactory.getParser(r);
        assertTrue(p instanceof LinearScanParser);

        List<GCEvent> events = p.parse(new TimeOrigin(1000L));

        r.close();

        assertEquals(3, events.size());

        NewGenerationCollection ngc = (NewGenerationCollection)events.get(0);
        assertEquals(5751, ngc.getTime().longValue());
        assertEquals(52, ngc.getDuration());

        assertEquals(660640L * 1024, ngc.get(FieldType.NG_BEFORE).getValue());
        assertEquals(72890L * 1024, ngc.get(FieldType.NG_AFTER).getValue());
        assertEquals(1835008L * 1024, ngc.get(FieldType.NG_CAPACITY).getValue());

        FullCollection fc = (FullCollection)events.get(1);
        assertEquals(5803, fc.getTime().longValue());
        assertEquals(264, fc.getDuration());

        assertEquals(0L, fc.get(FieldType.OG_BEFORE).getValue());
        assertEquals(72238L * 1024, fc.get(FieldType.OG_AFTER).getValue());
        assertEquals(4194304L * 1024, fc.get(FieldType.OG_CAPACITY).getValue());

        NewGenerationCollection ngc2 = (NewGenerationCollection)events.get(2);
        assertEquals(14989, ngc2.getTime().longValue());
        assertEquals(170, ngc2.getDuration());

        assertEquals(1572864L * 1024, ngc2.get(FieldType.NG_BEFORE).getValue());
        assertEquals(262124L * 1024, ngc2.get(FieldType.NG_AFTER).getValue());
        assertEquals(1835008L * 1024, ngc2.get(FieldType.NG_CAPACITY).getValue());

        log.debug(".");
    }

    @Test
    public void fullAndNewGenerationCollectionCombination_ExplicitTimeStamps() throws Exception
    {
        String s =

            "2013-10-10T14:34:08.387-0500: 54.593: [GC 54.594: [ParNew: 628032K->0K(628608K), 0.1742570 secs] 1023334K->468402K(3145216K), 0.1744410 secs] [Times: user=0.77 sys=0.13, real=0.17 secs]\n" +
                "2013-10-10T14:34:12.370-0500: 58.577: [Full GC 58.577: [CMS: 468402K->442325K(2516608K), 2.3616630 secs] 839600K->442325K(3145280K), [CMS Perm : 58673K->58575K(58800K)], 2.3620480 secs] [Times: user=2.65 sys=0.00, real=2.36 secs]\n" +
                "2013-10-10T14:34:26.055-0500: 72.261: [GC 72.261: [ParNew: 628352K->0K(628928K), 0.2128820 secs] 1070677K->531674K(3145536K), 0.2132190 secs] [Times: user=1.00 sys=0.14, real=0.21 secs]\n";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        GCLogParser p = GCLogParserFactory.getParser(r);
        assertTrue(p instanceof LinearScanParser);

        List<GCEvent> events = p.parse(new TimeOrigin(1000L));

        r.close();

        assertEquals(3, events.size());

        NewGenerationCollection ngc = (NewGenerationCollection)events.get(0);

        assertEquals(1381433648387L, ngc.getTime().longValue());
        assertEquals(174, ngc.getDuration());

        assertEquals(628032L * 1024, ngc.get(FieldType.NG_BEFORE).getValue());
        assertEquals(0L, ngc.get(FieldType.NG_AFTER).getValue());
        assertEquals(628608L * 1024, ngc.get(FieldType.NG_CAPACITY).getValue());

        assertEquals(1023334L * 1024, ngc.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(468402L * 1024, ngc.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(3145216L * 1024, ngc.get(FieldType.HEAP_CAPACITY).getValue());

        FullCollection fc = (FullCollection)events.get(1);

        assertEquals(1381433652370L, fc.getTime().longValue());
        assertEquals(2362, fc.getDuration());

        assertEquals(468402L * 1024, fc.get(FieldType.OG_BEFORE).getValue());
        assertEquals(442325L * 1024, fc.get(FieldType.OG_AFTER).getValue());
        assertEquals(2516608L * 1024, fc.get(FieldType.OG_CAPACITY).getValue());

        NewGenerationCollection ngc2 = (NewGenerationCollection)events.get(2);

        assertEquals(1381433666055L, ngc2.getTime().longValue());
        assertEquals(213, ngc2.getDuration());

        assertEquals(628352L * 1024, ngc2.get(FieldType.NG_BEFORE).getValue());
        assertEquals(0L, ngc2.get(FieldType.NG_AFTER).getValue());
        assertEquals(628928L * 1024, ngc2.get(FieldType.NG_CAPACITY).getValue());

        assertEquals(1070677L * 1024, ngc2.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(531674L * 1024, ngc2.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(3145536L * 1024, ngc2.get(FieldType.HEAP_CAPACITY).getValue());
    }

    @Test
    public void makeSureTheReaderIsClosedAfterParsing() throws Exception
    {
        MockReader r = new MockReader(
            "4.751: [GC [PSYoungGen: 660640K->72890K(1835008K)] 660640K->72890K(6029312K), 0.0515050 secs] [Times: user=0.21 sys=0.09, real=0.05 secs]");

        GCLogParser p = GCLogParserFactory.getParser(r);
        assertTrue(p instanceof LinearScanParser);

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        assertTrue(r.isClosed());

        assertEquals(1, events.size());
    }

    @Test
    public void nonTimestampedCMSAbortLine() throws Exception
    {
        MockReader r = new MockReader(
            " CMS: abort preclean due to time 29.020: [CMS-concurrent-abortable-preclean: 4.456/5.072 secs] [Times: user=8.52 sys=0.09, real=5.07 secs] ");

        GCLogParser p = GCLogParserFactory.getParser(r);
        assertTrue(p instanceof LinearScanParser);

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        assertTrue(r.isClosed());

        // the parser will issue a warning, but won't throw exception
        assertEquals(0, events.size());
    }

    // pipeline installation tests -------------------------------------------------------------------------------------

    @Test
    public void installEmptyPipeline() throws Exception
    {
        MockReader r = new MockReader("");

        LinearScanParser p = new LinearScanParser(r);

        assertNull(p.getPipeline());

        p.installPipeline();

        assertNull(p.getPipeline());
    }

    @Test
    public void installEmptyPipelineWhileThereIsAlreadyAPipeline() throws Exception
    {
        MockReader r = new MockReader("");

        LinearScanParser p = new LinearScanParser(r);

        assertNull(p.getPipeline());

        p.installPipeline(new MockGCEventParser());

        assertNotNull(p.getPipeline());

        p.installPipeline();

        assertNull(p.getPipeline());
    }

    @Test
    public void installOneParser() throws Exception
    {
        MockReader r = new MockReader("");

        LinearScanParser p = new LinearScanParser(r);

        assertNull(p.getPipeline());

        MockGCEventParser mep = new MockGCEventParser();
        // "taint" it to verify that the pipeline assembly process cleans it
        mep.setNext(new MockGCEventParser());

        assertNotNull(mep.getNext());

        p.installPipeline(mep);

        assertEquals(mep, p.getPipeline());
        assertNull(mep.getNext());
    }

    @Test
    public void installTwoParsers() throws Exception
    {
        MockReader r = new MockReader("");

        LinearScanParser p = new LinearScanParser(r);

        assertNull(p.getPipeline());

        MockGCEventParser mep = new MockGCEventParser();
        // "taint" it to verify that the pipeline assembly process cleans it
        mep.setNext(new MockGCEventParser());
        assertNotNull(mep.getNext());

        MockGCEventParser mep2 = new MockGCEventParser();
        // "taint" it to verify that the pipeline assembly process cleans it
        mep2.setNext(new MockGCEventParser());
        assertNotNull(mep2.getNext());

        p.installPipeline(mep, mep2);

        assertEquals(mep, p.getPipeline());
        //noinspection ConstantConditions
        assertEquals(mep2, p.getPipeline().getNext());
        assertNull(p.getPipeline().getNext().getNext());
    }

    @Test
    public void installDefaultPipeline() throws Exception
    {
        MockReader r = new MockReader("");

        LinearScanParser p = new LinearScanParser(r);

        p.installDefaultPipeline();

        GCEventParser ep = p.getPipeline();

        assertTrue(ep instanceof CMSParser);

        ep = ep.getNext();
        assertTrue(ep instanceof NewGenerationCollectionParser);

        ep = ep.getNext();
        assertTrue(ep instanceof FullCollectionParser);

        ep = ep.getNext();
        assertTrue(ep instanceof ShutdownParser);

        assertNull(ep.getNext());
    }

    // read-ahead parsing ----------------------------------------------------------------------------------------------

    @Test
    public void testReadAhead_NoMultiLineEvents() throws Exception
    {
        String s =
            "a\n" +
                "b\n" +
                "c";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        LinearScanParser p = new LinearScanParser(r);
        p.installPipeline(new PassThroughEventParser());

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        r.close();

        assertEquals(3, events.size());

        MockGCEvent e = (MockGCEvent)events.get(0);
        assertEquals("a", e.getLine());
        assertEquals(1, e.getLineNumber());

        MockGCEvent e2 = (MockGCEvent)events.get(1);
        assertEquals("b", e2.getLine());
        assertEquals(2, e2.getLineNumber());

        MockGCEvent e3 = (MockGCEvent)events.get(2);
        assertEquals("c", e3.getLine());
        assertEquals(3, e3.getLineNumber());
    }

    @Test
    public void testReadAhead_OneMultiLineEvent() throws Exception
    {
        String s =
            "a\n" +
                "b\n" +
                " continuation of the previous line\n" +
                "c";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        LinearScanParser p = new LinearScanParser(r);
        p.installPipeline(new PassThroughEventParser());
        p.addSecondLinePattern(Pattern.compile("^\\s*continuation of the previous line"));

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        r.close();

        assertEquals(3, events.size());

        MockGCEvent e = (MockGCEvent)events.get(0);
        assertEquals("a", e.getLine());
        assertEquals(1, e.getLineNumber());

        MockGCEvent e2 = (MockGCEvent)events.get(1);
        assertEquals("b continuation of the previous line", e2.getLine());
        assertEquals(2, e2.getLineNumber());

        MockGCEvent e3 = (MockGCEvent)events.get(2);
        assertEquals("c", e3.getLine());
        assertEquals(4, e3.getLineNumber());
    }

    @Test
    public void testReadAhead_TwoMultiLineEvent() throws Exception
    {
        String s =
            "a\n" +
                "b\n" +
                " continuation of the previous line\n" +
                "c\n" +
                " continuation of the previous line\n" +
                "d\n";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        LinearScanParser p = new LinearScanParser(r);
        p.installPipeline(new PassThroughEventParser());
        p.addSecondLinePattern(Pattern.compile("^\\s*continuation of the previous line"));

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        r.close();

        assertEquals(4, events.size());

        MockGCEvent e = (MockGCEvent)events.get(0);
        assertEquals("a", e.getLine());
        assertEquals(1, e.getLineNumber());

        MockGCEvent e2 = (MockGCEvent)events.get(1);
        assertEquals("b continuation of the previous line", e2.getLine());
        assertEquals(2, e2.getLineNumber());

        MockGCEvent e3 = (MockGCEvent)events.get(2);
        assertEquals("c continuation of the previous line", e3.getLine());
        assertEquals(4, e3.getLineNumber());

        MockGCEvent e4 = (MockGCEvent)events.get(3);
        assertEquals("d", e4.getLine());
        assertEquals(6, e4.getLineNumber());
    }

    @Test
    public void testReadAhead_MultiLineEventOnFirstLine() throws Exception
    {
        String s =
            "a\n" +
                " continuation of the previous line\n" +
                "c";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        LinearScanParser p = new LinearScanParser(r);
        p.installPipeline(new PassThroughEventParser());
        p.addSecondLinePattern(Pattern.compile("^\\s*continuation of the previous line"));

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        r.close();

        assertEquals(2, events.size());

        MockGCEvent e = (MockGCEvent)events.get(0);
        assertEquals("a continuation of the previous line", e.getLine());
        assertEquals(1, e.getLineNumber());

        MockGCEvent e3 = (MockGCEvent)events.get(1);
        assertEquals("c", e3.getLine());
        assertEquals(3, e3.getLineNumber());
    }

    @Test
    public void testReadAhead_MultiLineEventOnLastLine() throws Exception
    {
        String s =
            "a\n" +
                " continuation of the previous line";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        LinearScanParser p = new LinearScanParser(r);
        p.installPipeline(new PassThroughEventParser());
        p.addSecondLinePattern(Pattern.compile("^\\s*continuation of the previous line"));

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        r.close();

        assertEquals(1, events.size());

        MockGCEvent e = (MockGCEvent)events.get(0);
        assertEquals("a continuation of the previous line", e.getLine());
        assertEquals(1, e.getLineNumber());
    }

    @Test
    public void testReadAhead_TwoSuccessiveMultiLineLines() throws Exception
    {
        String s =
            "a\n" +
                " continuation of the previous line\n" +
                " continuation of the previous line";

        Reader r = new InputStreamReader(new ByteArrayInputStream(s.getBytes()));

        LinearScanParser p = new LinearScanParser(r);
        p.installPipeline(new PassThroughEventParser());
        p.addSecondLinePattern(Pattern.compile("^\\s*continuation of the previous line"));

        assertTrue(p.isTheSecondLineOfTheEvent(" continuation of the previous line"));

        try
        {
            p.parse(new TimeOrigin(0L));
            fail("should have failed with UserErrorException, two successive 'multi-lines'");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // isTheSecondLineOfTheEvent() tests -------------------------------------------------------------------------------

    @Test
    public void isTheSecondLineOfTheEvent_NullString() throws Exception
    {
        LinearScanParser p = new LinearScanParser(null);

        assertTrue(p.getSecondLinePatterns().isEmpty());

        assertFalse(p.isTheSecondLineOfTheEvent(null));
    }

    @Test
    public void isTheSecondLineOfTheEvent_ValidMatchAndNoMatch() throws Exception
    {
        LinearScanParser p = new LinearScanParser(null);

        p.addSecondLinePattern(Pattern.compile("^ [abc] $"));
        p.addSecondLinePattern(Pattern.compile("^ [xyz] $"));

        assertEquals(2, p.getSecondLinePatterns().size());

        assertTrue(p.isTheSecondLineOfTheEvent(" b "));
        assertFalse(p.isTheSecondLineOfTheEvent(" m "));
        assertTrue(p.isTheSecondLineOfTheEvent(" z "));
    }

    @Test
    public void isTheSecondLineOfTheEvent_Match() throws Exception
    {
        LinearScanParser p = new LinearScanParser(null);

        p.addSecondLinePattern(Pattern.compile("\\s*\\(concurrent mode failure\\).*"));

        assertEquals(1, p.getSecondLinePatterns().size());

        assertTrue(p.isTheSecondLineOfTheEvent(" (concurrent mode failure): 3667441K->779130K(3670016K), 6.2096720 secs] 4045730K->779130K(4141888K), [CMS Perm : 93567K->92821K(131072K)] icms_dc=100 , 6.6030840 secs] [Times: user=2.31 sys=0.23, real=6.61 secs]"));

        log.debug(".");
    }

    // applyTimeOriginOnTimeStamps() -----------------------------------------------------------------------------------

    @Test
    public void applyTimeOriginOnTimeStamps_NullTimeOriginOnDateStampTimestamps() throws Exception
    {
        TimeOrigin timeOrigin = new TimeOrigin();

        String line = "2015-01-01T01:01:01.111-0700: [blah]2015-02-02T02:02:02.222-0700: [blah2]";
        Timestamp ts = Timestamp.find(line, 0, 7L);
        Timestamp ts2 = Timestamp.find(line, ts.getEndPosition(), 7L);


        LinearScanParser.applyTimeOriginOnTimeStamps(timeOrigin, ts, ts2, 7L);

        // this is a noop
        assertEquals(ts.getTime().longValue(), Timestamp.DATESTAMP_FORMAT.parse("2015-01-01T01:01:01.111-0700").getTime());
        assertEquals(ts2.getTime().longValue(), Timestamp.DATESTAMP_FORMAT.parse("2015-02-02T02:02:02.222-0700").getTime());
    }

    @Test
    public void applyTimeOriginOnTimeStamps_NullTimeOriginOnMixedTimestamps() throws Exception
    {
        TimeOrigin timeOrigin = new TimeOrigin();

        String line = "2015-01-01T01:01:01.111-0700: 1.000: [blah]2.000: [blah2]";

        Timestamp ts = Timestamp.find(line, 0, 7L);
        Timestamp ts2 = Timestamp.find(line, ts.getEndPosition(), 7L);

        long time = ts.getTime();
        assertNull(ts2.getTime());

        //
        // we make sure we use the time from the first time stamp
        //

        LinearScanParser.applyTimeOriginOnTimeStamps(timeOrigin, ts, ts2, 7L);

        long timeAfter = ts.getTime();

        // make sure nothing happened to ts
        assertEquals(time, timeAfter);

        long time2After = ts2.getTime();

        assertEquals(time2After, Timestamp.DATESTAMP_FORMAT.parse("2015-01-01T01:01:02.111-0700").getTime());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



