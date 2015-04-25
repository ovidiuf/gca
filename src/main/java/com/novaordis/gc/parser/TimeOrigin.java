package com.novaordis.gc.parser;

/**
 * A mutable (once) time origin that is maintained during a parsing cycle. It is necessary because we might need to
 * infer it at a certain point during parsing and then use it going forward.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 *  Copyright 2013 Nova Ordis LLC
 */
public class TimeOrigin
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Long timeOrigin;

    // Constructors ----------------------------------------------------------------------------------------------------

    public TimeOrigin()
    {
        this(null);
    }

    public TimeOrigin(Long timeOrigin)
    {
        this.timeOrigin = timeOrigin;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return null if there is no know time origin.
     */
    public Long get()
    {
        return timeOrigin;
    }

    public boolean isInitialized()
    {
        return timeOrigin != null;
    }

    /**
     * The operation is idempotent - the instance can be initialized only once - only if the wrapped value is null.
     * All other subsequent invocations will be noops.
     */
    public void initialize(Long value)
    {
        if (timeOrigin == null)
        {
            timeOrigin = value;
        }
    }

    @Override
    public String toString()
    {
        return timeOrigin == null ? "UNINITIALIZED" : timeOrigin.toString();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



