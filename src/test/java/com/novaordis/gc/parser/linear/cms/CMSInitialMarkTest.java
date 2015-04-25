package com.novaordis.gc.parser.linear.cms;

import com.novaordis.gc.model.Field;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.event.cms.CMSInitialMark;
import com.novaordis.gc.parser.GCLogParserFactory;
import com.novaordis.gc.parser.linear.LinearScanParser;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CMSInitialMarkTest extends Assert
{
    // Constants -------------------------------------------------------------------------------------------------------


    private static final Logger log = Logger.getLogger(CMSInitialMarkTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void CMSInitialMark_WithTheEntireParser() throws Exception
    {
        String line = "1118543.850: [GC [1 CMS-initial-mark: 3988076K(5535744K)] 4248384K(7610880K), 0.2210670 secs] [Times: user=0.22 sys=0.00, real=0.22 secs]";

        LinearScanParser p = (LinearScanParser)GCLogParserFactory.getParser(new StringReader(line));

        List<GCEvent> events = p.parse(0L);

        assertEquals(1, events.size());

        CMSInitialMark e = (CMSInitialMark)events.get(0);

        assertEquals(1118543850L, e.getTime().longValue());
        assertEquals(1118543850L, e.getOffset().longValue());

        Field og = e.get(FieldType.OG);
        assertEquals(3988076L * 1024, og.getValue());

        Field ogc = e.get(FieldType.OG_CAPACITY);
        assertEquals(5535744L * 1024, ogc.getValue());

        Field heap = e.get(FieldType.HEAP);
        assertEquals(4248384L * 1024, heap.getValue());

        Field heapc = e.get(FieldType.HEAP_CAPACITY);
        assertNotNull(heapc);
        assertEquals(7610880L * 1024, heapc.getValue());

        log.debug(".");
    }

    @Test
    public void CMSInitialMark() throws Exception
    {
        String line = "[GC [1 CMS-initial-mark: 0K(6291456K)] 268502K(8178944K), 0.1010040 secs] [Times: user=0.10 sys=0.00, real=0.10 secs]";

        Timestamp ts = new Timestamp(0L, 1L);

        CMSInitialMark e = CMSParser.parseCMSInitialMark(ts, line, -1L, null);

        assertNotNull(e);
        assertEquals(1L, e.getOffset().longValue());
        assertEquals(101L, e.getDuration());

        Field og = e.get(FieldType.OG);
        assertEquals(0L, og.getValue());

        Field ogc = e.get(FieldType.OG_CAPACITY);
        assertEquals(6291456L * 1024, ogc.getValue());

        Field heap = e.get(FieldType.HEAP);
        assertEquals(268502L * 1024, heap.getValue());

        Field heapc = e.get(FieldType.HEAP_CAPACITY);
        assertEquals(8178944L * 1024, heapc.getValue());
    }

    @Test
    public void initialMark_Brief() throws Exception
    {
        String line = "[GC [1 CMS-initial-mark: 0K(2088960K)] 322K(2097088K), 0.0020626 secs]";

        Timestamp ts = new Timestamp(0L, 1L);

        CMSInitialMark e = CMSParser.parseCMSInitialMark(ts, line, -1L, null);

        assertNotNull(e);
        assertEquals(1L, e.getOffset().longValue());
        assertEquals(2L, e.getDuration());

        Field og = e.get(FieldType.OG);
        assertNotNull(og);
        assertEquals(0L, og.getValue());

        Field ogc = e.get(FieldType.OG_CAPACITY);
        assertEquals(2088960L * 1024, ogc.getValue());

        Field heap = e.get(FieldType.HEAP);
        assertEquals(322L * 1024, heap.getValue());

        Field heapc = e.get(FieldType.HEAP_CAPACITY);
        assertEquals(2097088L * 1024, heapc.getValue());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



