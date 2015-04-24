package com.novaordis.gc.parser;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class DurationTest extends Assert
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(DurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void testDuration() throws Exception
    {
        long ms = Duration.toLongMilliseconds("0.2210670 secs", -1L);
        assertEquals(221L, ms);
    }

    @Test
    public void weOnlyHandleSecs() throws Exception
    {
        try
        {
            Duration.toLongMilliseconds("0.2210670 years", 7L);
            fail("should have failed because we only handle 'secs'");
        }
        catch(ParserException e)
        {
            log.info(e.getMessage());
            assertEquals(7L, e.getLineNumber());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



