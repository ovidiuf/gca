package com.novaordis.gc.parser;

import com.novaordis.gc.model.Unit;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CurrentMaxTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CurrentMaxTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testInvalidFormat() throws Exception
    {
        try
        {
            new CurrentMax("blah", 7L);
            fail("should fail");
        }
        catch(ParserException e)
        {
            log.info(e.getMessage());
            assertEquals(7L, e.getLineNumber());
        }
    }

    @Test
    public void testInconsistentUnit() throws Exception
    {
        try
        {
            new CurrentMax("72243K(6029312M)", 8L);
            fail("should fail");
        }
        catch(ParserException e)
        {
            log.info(e.getMessage());
            assertEquals(8L, e.getLineNumber());
        }
    }

    @Test
    public void testValidSample() throws Exception
    {
        CurrentMax cm = new CurrentMax("72243K(6029312K)", 9L);

        assertEquals(72243L, cm.getCurrent());
        assertEquals(6029312, cm.getMax());
        assertEquals(Unit.K, cm.getUnit());
    }

    @Test
    public void testValidSampleZero() throws Exception
    {
        CurrentMax cm = new CurrentMax("0K(4194304K)", 10L);

        assertEquals(0, cm.getCurrent());
        assertEquals(4194304, cm.getMax());
        assertEquals(Unit.K, cm.getUnit());
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



