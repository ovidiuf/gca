package com.novaordis.gc.collected;

import com.novaordis.gc.model.Field;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.event.NewGenerationCollection;
import com.novaordis.gc.model.event.cms.CMSConcurrentMark;
import com.novaordis.gc.model.event.cms.CMSConcurrentPreclean;
import com.novaordis.gc.parser.GCLogParserFactory;
import com.novaordis.gc.parser.linear.LinearScanParser;
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
public class TwoEventsOnTheSameLineTest extends Assert
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void twoEventsOnTheSameLine() throws Exception
    {
        InputStream is = TwoEventsOnTheSameLineTest.class.getClassLoader().
            getResourceAsStream("collected/two-events-on-the-same-line.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);

        LinearScanParser p = (LinearScanParser)GCLogParserFactory.getParser(isr, null, false);

        List<GCEvent> events = p.parse(0L);

        // the important thing is the parser does not fail ...

        assertEquals(2, events.size());

        // 2014-08-14T01:12:28.621-0700: 27036.837: [GC2014-08-14T01:12:28.622-0700: 27036.837: [ParNew (promotion failed): 471872K->471872K(471872K), 0.3931530 secs]

        NewGenerationCollection e = (NewGenerationCollection)events.get(0);

        assertEquals(27036837L, e.getOffset().longValue());

        Field f = e.get(FieldType.NOTES);
        assertEquals("promotion failed", f.getValue());

        f = e.get(FieldType.NG_BEFORE);
        assertEquals(471872L * 1024, f.getValue());

        f = e.get(FieldType.NG_AFTER);
        assertEquals(471872L * 1024, f.getValue());

        f = e.get(FieldType.NG_CAPACITY);
        assertEquals(471872L * 1024, f.getValue());

        f = e.get(FieldType.DURATION);
        assertEquals(393L, f.getValue());

        CMSConcurrentPreclean e2 = (CMSConcurrentPreclean)events.get(1);
        assertNotNull(e2);
    }

    @Test
    public void twoEventsOnTheSameLine2() throws Exception
    {
        InputStream is = TwoEventsOnTheSameLineTest.class.getClassLoader().
            getResourceAsStream("collected/two-events-on-the-same-line-2.log");

        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);

        LinearScanParser p = (LinearScanParser)GCLogParserFactory.getParser(isr, null, false);

        List<GCEvent> events = p.parse(0L);

        // the important thing is the parser does not fail ...

        assertEquals(2, events.size());

        NewGenerationCollection e = (NewGenerationCollection)events.get(0);

        assertEquals(53254235L, e.getOffset().longValue());
        Field f = e.get(FieldType.NOTES);
        assertNotNull(f);
        assertEquals("promotion failed", f.getValue());

        f = e.get(FieldType.NG_BEFORE);
        assertEquals(4071840768L, f.getValue());

        f = e.get(FieldType.NG_AFTER);
        assertEquals(4452503552L, f.getValue());

        f = e.get(FieldType.NG_CAPACITY);
        assertEquals(4478730240L, f.getValue());

        CMSConcurrentMark e2 = (CMSConcurrentMark)events.get(1);
        assertNotNull(e2);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



