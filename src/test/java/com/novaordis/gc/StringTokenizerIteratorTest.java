package com.novaordis.gc;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class StringTokenizerIteratorTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(StringTokenizerIteratorTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testSimplest() throws Exception
    {
        StringTokenizerIterator sti = new StringTokenizerIterator("a,b,c", ",");

        assertTrue(sti.hasNext());
        assertEquals("a", sti.next());
        assertTrue(sti.hasNext());
        assertEquals("b", sti.next());
        assertTrue(sti.hasNext());
        assertEquals("c", sti.next());
        assertFalse(sti.hasNext());
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



