package com.novaordis.gc.cli;

import com.novaordis.gc.cli.command.ExportCommand;
import com.novaordis.gc.cli.command.InfoCommand;
import com.novaordis.gc.cli.command.TestCommand;
import com.novaordis.gc.cli.command.VersionCommand;

import java.util.Iterator;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CommandFactory
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    /**
     * @return null if we don't have an associated command.
     */
    public static Command getCommand(String name, Iterator<String> args, Configuration c) throws Exception
    {
        if ("test".equals(name))
        {
            return new TestCommand();
        }
        else if ("version".equals(name))
        {
            return new VersionCommand(c, args);
        }
        else if ("info".equals(name))
        {
            // this is the default command
            return new InfoCommand(c, args);
        }
        else if ("export".equals(name))
        {
            return new ExportCommand(c, args);
        }

        return null;
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



