package com.novaordis.gc.model.event;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.parser.GCEventParser;
import com.novaordis.gc.parser.linear.ShutdownParser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * TODO: this probably deserves a base class shared with other multi-line events.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Shutdown implements GCEvent
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ShutdownParser activeParser;
    private long offset;
    private long time;

    private List<String> lines;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Shutdown(Timestamp ts, ShutdownParser parser)
    {
        this.activeParser = parser;
        this.time = ts.getTime();
        this.offset = ts.getOffset();
        this.lines = new ArrayList<String>();
    }

    // GCEvent overrides -----------------------------------------------------------------------------------------------

    @Override
    public long getTime()
    {
        return time;
    }

    /**
     * There is no actual shutdown duration, it conventionally returns 0.
     */
    @Override
    public long getDuration()
    {
        return 0L;
    }

    @Override
    public long getOffset()
    {
        return offset;
    }

    @Override
    public CollectionType getCollectionType()
    {
        return CollectionType.SHUTDOWN;
    }

    @Override
    public Value get(FieldType t)
    {
        throw new RuntimeException("NOT YET IMPLEMENTED");
    }

    @Override
    public GCEventParser getActiveParser()
    {
        return activeParser;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "SHUTDOWN";
    }

    public void setActiveParser(ShutdownParser p)
    {
        this.activeParser = p;
    }

    /**
     * This method was added as a temporary way of getting a Shutdown parsed fast. Eventually it should be replaced with more semantically
     * relevant methods and go away.
     *
     * TODO: refactor out this method.
     */
    public void addLine(String line)
    {
        lines.add(line);
    }

    /**
     * This method was added as a temporary way of getting a Shutdown parsed fast. Eventually it should be replaced with more semantically
     * relevant methods and go away.
     *
     * TODO: refactor out this method.
     */
    public List<String> getLines()
    {
        return lines;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



