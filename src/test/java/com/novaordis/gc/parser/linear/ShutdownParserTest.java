package com.novaordis.gc.parser.linear;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.*;
import com.novaordis.gc.parser.TimeOrigin;
import com.novaordis.utilities.Files;
import com.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ShutdownParserTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ShutdownParserTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    @Test
    public void testNormalBehavior() throws Exception
    {
        String[] lines = new String[]
                {
                        "Heap",
                        " PSYoungGen      total 1926336K, used 1370287K [0x0000000780000000, 0x0000000800000000, 0x0000000800000000)",
                        "  eden space 1756096K, 70% used [0x0000000780000000,0x00000007cb99bc00,0x00000007eb2f0000)",
                        "  from space 170240K, 77% used [0x00000007eb2f0000,0x00000007f3380020,0x00000007f5930000)",
                        "   to   space 168960K, 0% used [0x00000007f5b00000,0x00000007f5b00000,0x0000000800000000)",
                        " PSOldGen        total 4194304K, used 832880K [0x0000000680000000, 0x0000000780000000, 0x0000000780000000)",
                        "   object space 4194304K, 19% used [0x0000000680000000,0x00000006b2d5c3a8,0x0000000780000000)",
                        " PSPermGen       total 265536K, used 265525K [0x0000000660000000, 0x0000000670350000, 0x0000000680000000)",
                        "   object space 265536K, 99% used [0x0000000660000000,0x000000067034d518,0x0000000670350000)"
                };

        ShutdownParser p = new ShutdownParser();

        Shutdown se = (Shutdown)p.parse(null, lines[0], 7L, null);

        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(0, se.getLines().size());

        GCEvent gce;

        gce = p.parse(null, lines[1], 11L, se);
        assertEquals(se, gce);
        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(1, se.getLines().size());

        gce = p.parse(null, lines[2], 21L, se);
        assertEquals(se, gce);
        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(2, se.getLines().size());

        gce = p.parse(null, lines[3], 31L, se);
        assertEquals(se, gce);
        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(3, se.getLines().size());

        gce = p.parse(null, lines[4], 41L, se);
        assertEquals(se, gce);
        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(4, se.getLines().size());

        gce = p.parse(null, lines[5], 51L, se);
        assertEquals(se, gce);
        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(5, se.getLines().size());

        gce = p.parse(null, lines[6], 61L, se);
        assertEquals(se, gce);
        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(6, se.getLines().size());

        gce = p.parse(null, lines[7], 71L, se);
        assertEquals(se, gce);
        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(7, se.getLines().size());

        gce = p.parse(null, lines[8], 81L, se);

        assertEquals(se, gce);
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());
        assertEquals(8, se.getLines().size());

        ShutdownParser parser = (ShutdownParser)se.getActiveParser();
        // we test for non-null because we want to know the state we end up with - normally we should clean up the associated parser, as
        // no more lines are expected, but we leave it there. This is a small leak.
        assertNotNull(parser);
    }

    @Test
    public void testWrongEventType() throws Exception
    {
        String[] lines = new String[]
                {
                        "Heap",
                        " PSYoungGen      total 1926336K, used 1370287K [0x0000000780000000, 0x0000000800000000, 0x0000000800000000)",
                        "  eden space 1756096K, 70% used [0x0000000780000000,0x00000007cb99bc00,0x00000007eb2f0000)",
                        "  from space 170240K, 77% used [0x00000007eb2f0000,0x00000007f3380020,0x00000007f5930000)",
                        "   to   space 168960K, 0% used [0x00000007f5b00000,0x00000007f5b00000,0x0000000800000000)",
                        " PSOldGen        total 4194304K, used 832880K [0x0000000680000000, 0x0000000780000000, 0x0000000780000000)",
                        "   object space 4194304K, 19% used [0x0000000680000000,0x00000006b2d5c3a8,0x0000000780000000)",
                        " PSPermGen       total 265536K, used 265525K [0x0000000660000000, 0x0000000670350000, 0x0000000680000000)",
                        "   object space 265536K, 99% used [0x0000000660000000,0x000000067034d518,0x0000000670350000)"
                };

        ShutdownParser p = new ShutdownParser();

        Shutdown se = (Shutdown)p.parse(null, lines[0], 7L, null);

        assertEquals(p, se.getActiveParser());
        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertNull(se.getOffset());
        assertNull(se.getTime());
        assertEquals(0L, se.getDuration());

        Timestamp ts = new Timestamp(1001L);

        try
        {
            p.parse(null, lines[1], 11L, new FullCollection(ts, 1L, null, null, null, null, false));
            fail("should fail with IllegalArgumentException, can't accept a non-Shutdown event in continuation");
        }
        catch(IllegalArgumentException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void incompleteShutdownEventAtTheBottomOfTheFile() throws Exception
    {
        // this simulates an incompletely-written SHUTDOWN event

        String content =
                "5.585: [GC [PSYoungGen: 660620K->72778K(1835008K)] 660620K->72778K(6029312K), 0.0751010 secs] [Times: user=0.19 sys=0.02, real=0.08 secs]\n" +
                "Heap\n" +
                " PSYoungGen      total 1926336K, used 1370287K [0x0000000780000000, 0x0000000800000000, 0x0000000800000000)\n" +
                "  eden space 1756096K, 70% used [0x0000000780000000,0x00000007cb99bc00,0x00000007eb2f0000)\n" +
                "  from space 170240K, 77% used [0x00000007eb2f0000,0x00000007f3380020,0x00000007f5930000)\n" +
                "   to   space 168960K, 0% used [0x00000007f5b00000,0x00000007f5b00000,0x0000000800000000)\n" +
                " PSOldGen        t";

        File f =  new File(Tests.getScratchDirectory(), "test.log");
        Files.write(f, content);

        InputStream fis = new FileInputStream(f);
        InputStreamReader isr = new InputStreamReader(fis);

        LinearScanParser p = new LinearScanParser(isr);
        p.installDefaultPipeline();

        List<GCEvent> events = p.parse(new TimeOrigin(1L));

        assertEquals(2, events.size());

        GCEvent e = events.get(0);
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, e.getCollectionType());

        e = events.get(1);
        assertEquals(CollectionType.SHUTDOWN, e.getCollectionType());

        Shutdown se = (Shutdown)e;

        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());

        assertEquals(0L, se.getDuration());
        assertNull(se.getOffset());
        assertNull(se.getTime());

        ShutdownParser parser = (ShutdownParser)se.getActiveParser();
        // we test for non-null because we want to know the state we end up with - normally we should clean up the associated parser, as
        // no more lines are expected, but we leave it there. This is a small leak.
        assertNotNull(parser);

        fis.close();
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



