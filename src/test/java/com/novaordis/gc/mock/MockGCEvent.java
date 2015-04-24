package com.novaordis.gc.mock;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.GCEventParser;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Time;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class MockGCEvent implements GCEvent
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockGCEvent.class);

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
    public long getTime()
    {
        return timestamp == null ? -1L : timestamp.getTime();
    }

    @Override
    public long getDuration()
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public long getOffset()
    {
        return -1L;
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



