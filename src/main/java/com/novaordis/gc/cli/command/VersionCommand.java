package com.novaordis.gc.cli.command;

import com.novaordis.gc.cli.Command;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.event.GCEvent;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Displays gc-analyzer version.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class VersionCommand implements Command
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(VersionCommand.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Configuration c;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public VersionCommand(Configuration c, Iterator<String> args)
    {
        this.c = c;
        log.debug("ignored " + args);
    }

    // Command implementation --------------------------------------------------------------------------------------------------------------

    @Override
    public boolean needsGcData()
    {
        return false;
    }

    @Override
    public void execute(List<GCEvent> events) throws Exception
    {
        System.out.println("");
        System.out.println(Configuration.VERSION);
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



