package com.novaordis.gc.mock;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.GCEventParser;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class MockGCEventParser implements GCEventParser
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private GCEventParser next;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // GCEventParser implementation --------------------------------------------------------------------------------------------------------

    @Override
    public GCEventParser getNext()
    {
        return next;
    }

    @Override
    public void setNext(GCEventParser p)
    {
        this.next = p;
    }

    @Override
    public GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent current) throws Exception
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



