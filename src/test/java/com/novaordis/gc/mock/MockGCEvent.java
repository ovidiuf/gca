package com.novaordis.gc.mock;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.GCEventParser;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class MockGCEvent implements GCEvent
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Timestamp timestamp;
    private String line;
    private long lineNumber;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockGCEvent(Timestamp ts, String line, long lineNumber)
    {
        this.timestamp = ts;
        this.line = line;
        this.lineNumber = lineNumber;
    }

    // GCEvent implementation ------------------------------------------------------------------------------------------

    @Override
    public Long getTime()
    {
        return timestamp == null ? null : timestamp.getTime();
    }

    @Override
    public long getDuration()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public Long getOffset()
    {
        return null;
    }

    @Override
    public CollectionType getCollectionType()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public Value get(FieldType t)
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public GCEventParser getActiveParser()
    {
        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getLine()
    {
        return line;
    }

    public long getLineNumber()
    {
        return lineNumber;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



