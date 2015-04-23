package com.novaordis.gc.model;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public abstract class ValueTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ValueTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // getValue() never returns null -------------------------------------------------------------------------------------------------------

    @Test
    public void getValueNeverReturnsNull() throws Exception
    {
        try
        {
            getValueToTest((Long)null);
            fail("should fail, no null-wrapping value allowed");
        }
        catch(Exception e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void getValueNeverReturnsNull_Long() throws Exception
    {
        Value v = getValueToTest(10L);
        assertNotNull(v.getValue());
    }

    @Test
    public void getValueNeverReturnsNull_Double() throws Exception
    {
        Value v = getValueToTest(10.1D);
        assertNotNull(v.getValue());
    }

    @Test
    public void getValueNeverReturnsNull_String() throws Exception
    {
        Value v = getValueToTest("test");
        assertNotNull(v.getValue());
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    /**
     * The subclass will wrap the appropriate Value instance around the primitive passed as argument.
     */
    protected abstract Value getValueToTest(Long value) throws Exception;

    /**
     * The subclass will wrap the appropriate Value instance around the primitive passed as argument.
     */
    protected abstract Value getValueToTest(Double value) throws Exception;

    /**
     * The subclass will wrap the appropriate Value instance around the primitive passed as argument.
     */
    protected abstract Value getValueToTest(String value) throws Exception;

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



