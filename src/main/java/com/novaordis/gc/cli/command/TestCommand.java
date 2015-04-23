package com.novaordis.gc.cli.command;

import com.novaordis.gc.cli.Command;
import com.novaordis.gc.model.event.GCEvent;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class TestCommand implements Command
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(TestCommand.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Command implementation --------------------------------------------------------------------------------------------------------------

    @Override
    public boolean needsGcData()
    {
        return true;
    }

    @Override
    public void execute(List<GCEvent> events) throws Exception
    {
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



