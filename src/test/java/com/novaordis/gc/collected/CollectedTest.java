package com.novaordis.gc.collected;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.event.*;
import com.novaordis.gc.parser.GCLogParser;
import com.novaordis.gc.parser.GCLogParserFactory;
import com.novaordis.gc.parser.TimeOrigin;
import com.novaordis.gc.parser.linear.LinearScanParser;
import com.novaordis.gc.parser.linear.ShutdownParser;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A collection of tests created based on real use cases.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CollectedTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CollectedTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    /**
     * Sometimes it happens that the last line of the gc log file is incomplete because, most likely, the VM was in process of writing it
     * when we collected the file. We need to detect this situation and simply ignore (maybe issue a simple warning).
     */
    @Test
    public void lastLineIncomplete() throws Exception
    {
        InputStream is = CollectedTest.class.getClassLoader().getResourceAsStream("collected/incomplete-last-line.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);
        GCLogParser p = GCLogParserFactory.getParser(isr);
        assertTrue(p instanceof LinearScanParser);

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        // the important thing is the parser does not fail ...

        assertEquals(1, events.size());

        log.debug(".");
    }

    /**
     * A clean shutdown of the JVM is captured in the GC log, this test makes sure the event is parsed and stored correctly.
     */
    @Test
    public void logUpdatedOnShutdown() throws Exception
    {
        InputStream is = CollectedTest.class.getClassLoader().getResourceAsStream("collected/log-updated-on-shutdown.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);
        GCLogParser p = GCLogParserFactory.getParser(isr);
        assertTrue(p instanceof LinearScanParser);

        List<GCEvent> events = p.parse(new TimeOrigin(1L));

        assertEquals(3, events.size());

        GCEvent e = events.get(0);
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, e.getCollectionType());

        e = events.get(1);
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, e.getCollectionType());

        e = events.get(2);
        assertEquals(CollectionType.SHUTDOWN, e.getCollectionType());

        Shutdown se = (Shutdown)e;

        assertEquals(CollectionType.SHUTDOWN, se.getCollectionType());
        assertEquals(0L, se.getDuration());
        assertNull(se.getTime());
        assertNull(se.getOffset());

        ShutdownParser sp = (ShutdownParser)se.getActiveParser();
        // we test for non-null because we want to know the state we end up with - normally we should clean up the associated parser, as
        // no more lines are expected, but we leave it there. This is a small leak.
        assertNotNull(sp);
    }

    @Test
    public void doubleTimeStampOnMinorCollection_WithTimeOrigin() throws Exception
    {
        InputStream is = CollectedTest.class.getClassLoader().getResourceAsStream("collected/double-time-stamp-on-minor-collection.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);
        GCLogParser p = GCLogParserFactory.getParser(isr);
        assertTrue(p instanceof LinearScanParser);

        TimeOrigin timeOrigin = new TimeOrigin(1L);

        List<GCEvent> events = p.parse(timeOrigin);

        assertEquals(1, events.size());

        GCEvent e = events.get(0);
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, e.getCollectionType());

        assertEquals(53233950L, e.getOffset().longValue());
        assertEquals(timeOrigin.get() + 53233950L, e.getTime().longValue());
    }

    @Test
    public void doubleTimeStampOnMinorCollection_WithNoTimeOrigin() throws Exception
    {
        InputStream is = CollectedTest.class.getClassLoader().getResourceAsStream("collected/double-time-stamp-on-minor-collection.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);
        GCLogParser p = GCLogParserFactory.getParser(isr);
        assertTrue(p instanceof LinearScanParser);

        TimeOrigin timeOrigin = new TimeOrigin();

        try
        {
            //noinspection ConstantConditions
            p.parse(timeOrigin);
            fail("should have failed because we have no time origin");
        }
        catch(Exception e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void cms_ng_rescan() throws Exception
    {
        InputStream is = CollectedTest.class.getClassLoader().getResourceAsStream("collected/cms-ng-rescan.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);
        GCLogParser p = GCLogParserFactory.getParser(isr);
        assertTrue(p instanceof LinearScanParser);

        TimeOrigin timeOrigin = new TimeOrigin();

        List<GCEvent> events = p.parse(timeOrigin);

        //
        // we're skipping all the events on this line for the time being, we'll come back to it and we'll have to
        // update the test
        //

        assertTrue(events.isEmpty());
    }

    @Test
    public void collected2() throws Exception
    {
        InputStream is = CollectedTest.class.getClassLoader().getResourceAsStream("collected/2.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);
        GCLogParser p = GCLogParserFactory.getParser(isr);
        assertTrue(p instanceof LinearScanParser);

        TimeOrigin timeOrigin = new TimeOrigin();

        List<GCEvent> events = p.parse(timeOrigin);

        //
        // we're skipping all the events on this line for the time being, we'll come back to it and we'll have to
        // update the test
        //

        assertTrue(events.isEmpty());
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



