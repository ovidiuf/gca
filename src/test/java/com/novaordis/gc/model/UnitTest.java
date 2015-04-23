package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class UnitTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(UnitTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void nullConversion() throws Exception
    {
        assertNull(Unit.extendedValueOf(null));
    }

    @Test
    public void bytes() throws Exception
    {
        assertEquals(Unit.b, Unit.extendedValueOf("b"));
    }

    @Test
    public void bytes2() throws Exception
    {
        assertEquals(Unit.b, Unit.extendedValueOf("bytes"));
    }

    @Test
    public void kb() throws Exception
    {
        assertEquals(Unit.K, Unit.extendedValueOf("K"));
    }

    @Test
    public void kb2() throws Exception
    {
        assertEquals(Unit.K, Unit.extendedValueOf("kb"));
    }

    @Test
    public void mb() throws Exception
    {
        assertEquals(Unit.M, Unit.extendedValueOf("M"));
    }

    @Test
    public void mb2() throws Exception
    {
        assertEquals(Unit.M, Unit.extendedValueOf("mb"));
    }

    @Test
    public void gb() throws Exception
    {
        assertEquals(Unit.G, Unit.extendedValueOf("G"));
    }

    @Test
    public void gb2() throws Exception
    {
        assertEquals(Unit.G, Unit.extendedValueOf("gb"));
    }

    @Test
    public void secs() throws Exception
    {
        assertEquals(Unit.s, Unit.extendedValueOf("s"));
    }

    @Test
    public void secs2() throws Exception
    {
        assertEquals(Unit.s, Unit.extendedValueOf("sec"));
    }

    @Test
    public void unknownUnitThrowsUserErrorException() throws Exception
    {
        try
        {
            Unit.extendedValueOf("no-such-unit");
            fail("should have thrown UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



