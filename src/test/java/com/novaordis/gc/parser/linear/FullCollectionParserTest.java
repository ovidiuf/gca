package com.novaordis.gc.parser.linear;

import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.FullCollection;
import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class FullCollectionParserTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(FullCollectionParserTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testUnrecognizedFullGCLine() throws Exception
    {
        String line = "[Full GC (System) this is something we did not see so far]";

        FullCollectionParser p = new FullCollectionParser();

        try
        {
            p.parse(null, line, 78, null, null);
            fail("should have failed with unrecognized format");
        }
        catch(ParserException e)
        {
            assertEquals(78, e.getLineNumber());
            log.info(e.getMessage());
        }
    }

    @Test
    public void testValidSampleSystem() throws Exception
    {
        String line = "[Full GC (System) [PSYoungGen: 32861K->0K(1722048K)] [PSOldGen: 1663616K->1696127K(4194304K)] 1696478K->1696127K(5916352K) [PSPermGen: 292408K->292408K(292416K)], 2.4516460 secs] [Times: user=2.54 sys=0.00, real=2.45 secs]";

        FullCollectionParser p = new FullCollectionParser();

        Timestamp ts = new Timestamp("1.000", 0L, null, false);

        FullCollection e = (FullCollection)p.parse(ts, line, -1, null, null);

        assertNotNull(e);

        assertEquals(1000L, e.getTime());
        assertEquals(1000L, e.getOffset());

        assertEquals(32861L * 1024, e.get(FieldType.NG_BEFORE).getValue());
        assertEquals(0L, e.get(FieldType.NG_AFTER).getValue());
        assertEquals(1722048L * 1024, e.get(FieldType.NG_CAPACITY).getValue());

        assertEquals(1663616L * 1024, e.get(FieldType.OG_BEFORE).getValue());
        assertEquals(1696127L * 1024, e.get(FieldType.OG_AFTER).getValue());
        assertEquals(4194304L * 1024, e.get(FieldType.OG_CAPACITY).getValue());

        assertEquals(292408L * 1024, e.get(FieldType.PG_BEFORE).getValue());
        assertEquals(292408L * 1024, e.get(FieldType.PG_AFTER).getValue());
        assertEquals(292416L * 1024, e.get(FieldType.PG_CAPACITY).getValue());

        assertEquals(1696478L * 1024, e.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(1696127L * 1024, e.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(5916352L * 1024, e.get(FieldType.HEAP_CAPACITY).getValue());

        assertEquals(2452, e.getDuration());

        assertTrue(e.isSystem());
    }

    @Test
    public void testValidSampleNonSystem() throws Exception
    {
        String line = "[Full GC [PSYoungGen: 1080K->1K(1398144K)] [PSOldGen: 4037629K->895254K(4194303K)] 4038710K->895254K(5592448K) [PSPermGen: 270279K->270279K(270336K)], 1.6447130 secs] [Times: user=1.64 sys=0.00, real=1.65 secs]";

        FullCollectionParser p = new FullCollectionParser();

        Timestamp ts = new Timestamp("1.000", 0L, null, false);

        FullCollection e = (FullCollection)p.parse(ts, line, -1, null, null);

        assertNotNull(e);

        assertEquals(1000L, e.getTime());
        assertEquals(1000L, e.getOffset());

        assertEquals(1080L * 1024, e.get(FieldType.NG_BEFORE).getValue());
        //noinspection PointlessArithmeticExpression
        assertEquals(1L * 1024, e.get(FieldType.NG_AFTER).getValue());
        assertEquals(1398144L * 1024, e.get(FieldType.NG_CAPACITY).getValue());

        assertEquals(4037629L * 1024, e.get(FieldType.OG_BEFORE).getValue());
        assertEquals(895254L * 1024, e.get(FieldType.OG_AFTER).getValue());
        assertEquals(4194303L * 1024, e.get(FieldType.OG_CAPACITY).getValue());

        assertEquals(270279L * 1024, e.get(FieldType.PG_BEFORE).getValue());
        assertEquals(270279L * 1024, e.get(FieldType.PG_AFTER).getValue());
        assertEquals(270336L * 1024, e.get(FieldType.PG_CAPACITY).getValue());

        assertEquals(4038710L * 1024, e.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(895254L * 1024, e.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(5592448L * 1024, e.get(FieldType.HEAP_CAPACITY).getValue());

        assertEquals(1645, e.getDuration());

        assertFalse(e.isSystem());
    }

    @Test
    public void validSampleCMS() throws Exception
    {
        String line =
            "[Full GC 154707.021: [CMS: 2505848K->1625816K(3355456K), 9.6611880 secs] 3343143K->1625816K(4193536K), [CMS Perm : 175715K->174292K(262144K)], 9.6618080 secs] [Times: user=9.65 sys=0.00, real=9.66 secs]";

        FullCollectionParser p = new FullCollectionParser();

        Timestamp ts = new Timestamp("1.000", 0L, null, false);

        FullCollection e = (FullCollection)p.parse(ts, line, -1, null, null);

        assertNotNull(e);

        assertEquals(1000L, e.getTime());
        assertEquals(1000L, e.getOffset());

        assertNull(e.get(FieldType.NG_BEFORE));
        assertNull(e.get(FieldType.NG_AFTER));
        assertNull(e.get(FieldType.NG_CAPACITY));

        assertEquals(2505848L * 1024, e.get(FieldType.OG_BEFORE).getValue());
        assertEquals(1625816L * 1024, e.get(FieldType.OG_AFTER).getValue());
        assertEquals(3355456L * 1024, e.get(FieldType.OG_CAPACITY).getValue());

        assertEquals(175715L * 1024, e.get(FieldType.PG_BEFORE).getValue());
        assertEquals(174292L * 1024, e.get(FieldType.PG_AFTER).getValue());
        assertEquals(262144L * 1024, e.get(FieldType.PG_CAPACITY).getValue());

        assertEquals(3343143L * 1024, e.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(1625816L * 1024, e.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(4193536L * 1024, e.get(FieldType.HEAP_CAPACITY).getValue());

        assertEquals(9662, e.getDuration());

        assertFalse(e.isSystem());
    }

    @Test
    public void collectedSample() throws Exception
    {
        String line =
            "[Full GC (System) [PSYoungGen: 25762K->0K(887808K)] [ParOldGen: 377824K->217543K(1398144K)] 403587K->217543K(2285952K) [PSPermGen: 149513K->143503K(149696K)], 1.8638674 secs] [Times: user=5.80 sys=0.00, real=1.86 secs]";

        FullCollectionParser p = new FullCollectionParser();

        Timestamp ts = new Timestamp("1.000", 0L, null, false);

        FullCollection e = (FullCollection)p.parse(ts, line, -1, null, null);

        assertNotNull(e);

        assertEquals(1000L, e.getTime());
        assertEquals(1000L, e.getOffset());

        assertEquals(25762L * 1024, e.get(FieldType.NG_BEFORE).getValue());
        //noinspection PointlessArithmeticExpression
        assertEquals(0L * 1024, e.get(FieldType.NG_AFTER).getValue());
        assertEquals(887808L * 1024, e.get(FieldType.NG_CAPACITY).getValue());

        assertEquals(377824L * 1024, e.get(FieldType.OG_BEFORE).getValue());
        assertEquals(217543L * 1024, e.get(FieldType.OG_AFTER).getValue());
        assertEquals(1398144L * 1024, e.get(FieldType.OG_CAPACITY).getValue());

        assertEquals(149513L * 1024, e.get(FieldType.PG_BEFORE).getValue());
        assertEquals(143503L * 1024, e.get(FieldType.PG_AFTER).getValue());
        assertEquals(149696L * 1024, e.get(FieldType.PG_CAPACITY).getValue());

        assertEquals(403587L * 1024, e.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(217543L * 1024, e.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(2285952L * 1024, e.get(FieldType.HEAP_CAPACITY).getValue());

        assertEquals(1864, e.getDuration());

        assertTrue(e.isSystem());
    }

    @Test
    public void collectedSample2() throws Exception
    {
        // these two lines are always encountered together so the read-ahead scanner will coalesce them
//
//        53365.009: [Full GC 53365.009: [CMS53369.873: [CMS-concurrent-mark: 5.274/5.371 secs] [Times: user=12.80 sys=0.36, real=5.37 secs]
//         (concurrent mode failure): 11628290K->11661304K(11666432K), 23.5081640 secs] 15947092K->12535361K(16040192K), [CMS Perm : 117651K->117651K(208152K)], 23.5087140 secs] [Times: user=27.85 sys=0.31, real=23.51 secs]

        String line = "[Full GC 53365.009: [CMS53369.873: [CMS-concurrent-mark: 5.274/5.371 secs] [Times: user=12.80 sys=0.36, real=5.37 secs] (concurrent mode failure): 11628290K->11661304K(11666432K), 23.5081640 secs] 15947092K->12535361K(16040192K), [CMS Perm : 117651K->117651K(208152K)], 23.5087140 secs] [Times: user=27.85 sys=0.31, real=23.51 secs]";

        FullCollectionParser p = new FullCollectionParser();

        Timestamp ts = new Timestamp("1.000", 0L, null, false);

        FullCollection e = (FullCollection)p.parse(ts, line, -1, null, null);

        // for the time being, the parser simply ignores
        // [CMS-concurrent-mark: 5.274/5.371 secs] [Times: user=12.80 sys=0.36, real=5.37 secs] (concurrent mode failure): 11628290K->11661304K(11666432K), 23.5081640 secs]

        assertNotNull(e);

        assertEquals(1000L, e.getTime());
        assertEquals(1000L, e.getOffset());

        assertNull(e.get(FieldType.NG_BEFORE));
        assertNull(e.get(FieldType.NG_AFTER));
        assertNull(e.get(FieldType.NG_CAPACITY));

        assertNull(e.get(FieldType.OG_BEFORE));
        assertNull(e.get(FieldType.OG_AFTER));
        assertNull(e.get(FieldType.OG_CAPACITY));

        assertEquals(117651L * 1024, e.get(FieldType.PG_BEFORE).getValue());
        assertEquals(117651L * 1024, e.get(FieldType.PG_AFTER).getValue());
        assertEquals(208152L * 1024, e.get(FieldType.PG_CAPACITY).getValue());

        assertEquals(15947092L * 1024, e.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(12535361L * 1024, e.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(16040192L * 1024, e.get(FieldType.HEAP_CAPACITY).getValue());

        assertEquals(23509, e.getDuration());

        assertFalse(e.isSystem());
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



