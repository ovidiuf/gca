package com.novaordis.gc.parser;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ParserException extends Exception
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ParserException.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private long lineNumber;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public ParserException(String message, Throwable cause, long lineNumber)
    {
        super(message, cause);
        this.lineNumber = lineNumber;
    }

    public ParserException(String message, long lineNumber)
    {
        super(message);
        this.lineNumber = lineNumber;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    public long getLineNumber()
    {
        return lineNumber;
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



