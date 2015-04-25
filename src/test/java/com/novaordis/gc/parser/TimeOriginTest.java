package com.novaordis.gc.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class TimeOriginTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception
    {
        TimeOrigin to = new TimeOrigin();
        assertNull(to.get());
        assertFalse(to.isInitialized());
    }

    @Test
    public void constructorWithNullArgument() throws Exception
    {
        TimeOrigin to = new TimeOrigin(null);
        assertNull(to.get());
        assertFalse(to.isInitialized());
    }

    @Test
    public void constructorWithNonNullArgument() throws Exception
    {
        TimeOrigin to = new TimeOrigin(1L);
        assertEquals(1L, to.get().longValue());
        assertTrue(to.isInitialized());
    }

    // initialize() ----------------------------------------------------------------------------------------------------

    @Test
    public void alreadyInitialized() throws Exception
    {
        TimeOrigin to = new TimeOrigin(1L);
        assertTrue(to.isInitialized());

        to.initialize(2L);

        assertEquals(1L, to.get().longValue());
        assertTrue(to.isInitialized());
    }

    @Test
    public void initialize() throws Exception
    {
        TimeOrigin to = new TimeOrigin();
        assertFalse(to.isInitialized());

        to.initialize(2L);

        assertEquals(2L, to.get().longValue());
        assertTrue(to.isInitialized());

        to.initialize(3L);

        assertEquals(2L, to.get().longValue());
        assertTrue(to.isInitialized());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



