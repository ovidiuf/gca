package com.novaordis.gc.model;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class UtilTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(UtilTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testConvertToBytes_K() throws Exception
    {
        assertEquals(1024000L, Util.convertToBytes(Unit.K, 1000));
    }

    @Test
    public void testConvertToBytes_b() throws Exception
    {
        assertEquals(1000L, Util.convertToBytes(Unit.b, 1000));
    }

    @Test
    public void testConvertToBytes_M() throws Exception
    {
        assertEquals(1024L * 1024L, Util.convertToBytes(Unit.M, 1));
    }

    @Test
    public void testConvertToBytes_G() throws Exception
    {
        assertEquals(1024L * 1024L * 1024L, Util.convertToBytes(Unit.G, 1));
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



