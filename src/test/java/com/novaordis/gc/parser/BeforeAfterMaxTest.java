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
public class BeforeAfterMaxTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(BeforeAfterMaxTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testInvalidFormat() throws Exception
    {
        try
        {
            new BeforeAfterMax("blah", 1L);
            fail("should fail");
        }
        catch(ParserException e)
        {
            log.info(e.getMessage());
            assertEquals(1L, e.getLineNumber());
        }
    }

    @Test
    public void testInconsistentUnit() throws Exception
    {
        try
        {
            new BeforeAfterMax("72803K->72243K(6029312M)", 2L);
            fail("should fail");
        }
        catch(ParserException e)
        {
            log.info(e.getMessage());
            assertEquals(2L, e.getLineNumber());
        }
    }

    @Test
    public void testValidSample() throws Exception
    {
        BeforeAfterMax bam = new BeforeAfterMax("72803K->72243K(6029312K)", 3L);

        assertEquals(72803, bam.getBefore());
        assertEquals(72243, bam.getAfter());
        assertEquals(6029312, bam.getMax());
        assertEquals(Unit.K, bam.getUnit());
    }

    @Test
    public void testValidSampleZero() throws Exception
    {
        BeforeAfterMax bam = new BeforeAfterMax("0K->72243K(4194304K)", 4L);

        assertEquals(0, bam.getBefore());
        assertEquals(72243, bam.getAfter());
        assertEquals(4194304, bam.getMax());
        assertEquals(Unit.K, bam.getUnit());
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



