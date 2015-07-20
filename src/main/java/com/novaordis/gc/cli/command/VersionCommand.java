package com.novaordis.gc.cli.command;

import com.novaordis.gc.cli.Command;
import com.novaordis.gc.cli.Util;
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

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public VersionCommand(Iterator<String> args)
    {
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
        String s = Util.getVersion() + " released on " + Util.getReleaseDate();
        System.out.println(s);
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



