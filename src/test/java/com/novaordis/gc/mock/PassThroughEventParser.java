package com.novaordis.gc.mock;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.GCEventParser;

/**
 * An event parser that simply wraps the line it receives into a MockCGEvent and returns it.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class PassThroughEventParser implements GCEventParser
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private GCEventParser next;

    // Constructors ----------------------------------------------------------------------------------------------------

    // GCEventParser implementation ------------------------------------------------------------------------------------

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
        return new MockGCEvent(ts, line, lineNumber);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



