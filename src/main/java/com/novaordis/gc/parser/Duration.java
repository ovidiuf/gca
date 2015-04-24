package com.novaordis.gc.parser;

/**
 * Static utility that converts "0.2210670 secs" into duration information (long milliseconds)
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 *  Copyright 2013 Nova Ordis LLC
 */
public class Duration
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Static utility that converts "0.2210670 secs" into duration information (long milliseconds), performing all
     * necessary rounding.
     *
     * @throws ParserException
     */
    public static long toLongMilliseconds(String s, long lineNumber) throws ParserException
    {
        // we currently only handle 'secs', everything else is handled as a parsing error

        if (!s.endsWith(" secs"))
        {
            throw new ParserException("we can only handle 'secs', but we got \"" + s + "\"", lineNumber);
        }

        s = s.substring(0, s.length() - " secs".length()).trim();
        //noinspection UnnecessaryLocalVariable
        long duration = Math.round(Float.parseFloat(s) * 1000);
        return duration;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



