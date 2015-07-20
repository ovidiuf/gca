package com.novaordis.gc.cli;

import com.novaordis.gc.cli.command.ExportCommand;
import com.novaordis.gc.cli.command.InfoCommand;
import com.novaordis.gc.cli.command.TestCommand;
import com.novaordis.gc.cli.command.VersionCommand;
import com.novaordis.gc.cli.command.tccs.TomsCommand;
import com.novaordis.gc.cli.command.tccs.TotsCommand;

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
            return new VersionCommand(args);
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
        else if ("toms".equals(name) || "tots".equals(name))
        {
            if (name.equals("toms"))
            {
                return new TomsCommand(c, args);
            }
            else
            {
                return new TotsCommand(c, args);
            }
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



