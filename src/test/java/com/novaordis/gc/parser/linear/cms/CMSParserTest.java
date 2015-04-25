package com.novaordis.gc.parser.linear.cms;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.cms.CMSConcurrentMarkStart;
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
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CMSParserTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // CMS-concurrent-mark-start tests ---------------------------------------------------------------------------------

    @Test
    public void concurrentMarkStart() throws Exception
    {
        String line = "[CMS-concurrent-mark-start]";

        CMSParser p = new CMSParser();

        Timestamp ts = new Timestamp(1L);

        CMSConcurrentMarkStart cms = (CMSConcurrentMarkStart)p.parse(ts, line, -1, null);

        assertNotNull(cms);
        assertEquals(1L, cms.getOffset().longValue());
        assertEquals(0L, cms.getDuration());

        log.debug(".");
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



