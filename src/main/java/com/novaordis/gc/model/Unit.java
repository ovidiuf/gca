package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public enum Unit
{
    b(1L, "bytes"),
    K(1024L, "KB"),
    M(1024L * 1024L, "MB"),
    G(1024L * 1024L * 1024L, "GB"),

    ms(1L, "ms"),
    s(1000L, "sec");

    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    /**
     * An extended "valueOf(...)" that parses equivalent String representations for the same enum value.
     */
    public static Unit extendedValueOf(String os) throws UserErrorException
    {
        if (os == null)
        {
            return null;
        }

        String lcs = os.toLowerCase();

        if ("bytes".equals(lcs))
        {
            return b;
        }

        if ("kb".equals(lcs))
        {
            return K;
        }

        if ("mb".equals(lcs))
        {
            return M;
        }

        if ("gb".equals(lcs))
        {
            return G;
        }

        if ("sec".equals(lcs))
        {
            return s;
        }

        try
        {
            return valueOf(os);
        }
        catch(IllegalArgumentException e)
        {
            throw new UserErrorException("invalid unit: " + os, e);
        }
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    public long multiplier;

    public String label;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    Unit(long multiplier, String label)
    {
        this.multiplier = multiplier;
        this.label = label;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------

}
