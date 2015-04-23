package com.novaordis.gc.parser.linear.cms;

import com.novaordis.gc.model.Field;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.cms.CMSConcurrentMarkStart;
import com.novaordis.gc.model.event.cms.CMSInitialMark;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CMSParserTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CMSParserTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // parseCMSInitialMark() ---------------------------------------------------------------------------------------------------------------

    @Test
    public void initialMark() throws Exception
    {
        String line = "[GC [1 CMS-initial-mark: 0K(6291456K)] 268502K(8178944K), 0.1010040 secs] [Times: user=0.10 sys=0.00, real=0.10 secs]";

        Timestamp ts = new Timestamp(0L, 1L);

        CMSInitialMark cmsim = CMSParser.parseCMSInitialMark(ts, line, -1L, null);

        assertNotNull(cmsim);
        assertEquals(1L, cmsim.getOffset());
        assertEquals(101L, cmsim.getDuration());

        Field og = cmsim.get(FieldType.OG);
        assertNotNull(og);
        assertEquals(0L, og.getValue());

        Field ogc = cmsim.get(FieldType.OG_CAPACITY);
        assertNotNull(ogc);
        assertEquals(6291456L * 1024, ogc.getValue());

        Field heap = cmsim.get(FieldType.HEAP);
        assertNotNull(heap);
        assertEquals(268502L * 1024, heap.getValue());

        Field heapc = cmsim.get(FieldType.HEAP_CAPACITY);
        assertNotNull(heapc);
        assertEquals(8178944L * 1024, heapc.getValue());
    }

    @Test
    public void initialMark_Brief() throws Exception
    {
        String line = "[GC [1 CMS-initial-mark: 0K(2088960K)] 322K(2097088K), 0.0020626 secs]";

        Timestamp ts = new Timestamp(0L, 1L);

        CMSInitialMark cmsim = CMSParser.parseCMSInitialMark(ts, line, -1L, null);

        assertNotNull(cmsim);
        assertEquals(1L, cmsim.getOffset());
        assertEquals(2L, cmsim.getDuration());

        Field og = cmsim.get(FieldType.OG);
        assertNotNull(og);
        assertEquals(0L, og.getValue());

        Field ogc = cmsim.get(FieldType.OG_CAPACITY);
        assertNotNull(ogc);
        assertEquals(2088960L * 1024, ogc.getValue());

        Field heap = cmsim.get(FieldType.HEAP);
        assertNotNull(heap);
        assertEquals(322L * 1024, heap.getValue());

        Field heapc = cmsim.get(FieldType.HEAP_CAPACITY);
        assertNotNull(heapc);
        assertEquals(2097088L * 1024, heapc.getValue());
    }

    // CMS-concurrent-mark-start tests ---------------------------------------------------------------------------------

    @Test
    public void concurrentMarkStart() throws Exception
    {
        String line = "[CMS-concurrent-mark-start]";

        CMSParser p = new CMSParser();

        Timestamp ts = new Timestamp(0L, 1L);

        CMSConcurrentMarkStart cms = (CMSConcurrentMarkStart)p.parse(ts, line, -1, null, null);

        assertNotNull(cms);
        assertEquals(1L, cms.getOffset());
        assertEquals(0L, cms.getDuration());

        log.debug(".");
    }

    // two events on the same line, the second is CMS ------------------------------------------------------------------

    @Test
    public void secondEventOnLine_CMSConcurrentPreclean() throws Exception
    {
        String line = "[CMS2014-08-14T01:12:29.867-0700: 27038.083: [CMS-concurrent-preclean: 4.167/17.484 secs] [Times: user=21.55 sys=2.82, real=17.48 secs]";


    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



