package com.novaordis.gc.cli.command.tccs;

import com.novaordis.gc.cli.Command;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.event.GCEvent;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Converts time information from log timestamps to milliseconds and back.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public abstract class TimestampConversionCommand implements Command
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(TimestampConversionCommand.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    protected Configuration configuration;
    protected String timestampFormatLiteral;

    // Constructors ----------------------------------------------------------------------------------------------------

    public TimestampConversionCommand(Configuration c, Iterator<String> args)
    {
        this.configuration = c;
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public boolean needsGcData()
    {
        return false;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String getTimestampFormat()
    {
        return timestampFormatLiteral;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



