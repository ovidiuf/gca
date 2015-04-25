package com.novaordis.gc.cli;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.parser.GCLogParser;
import com.novaordis.gc.parser.GCLogParserFactory;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Main
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Main.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception
    {
        Configuration c = null;

        try
        {
            c = new Configuration(args);

            Command cmd = c.getCommand();

            if (cmd == null)
            {
                displayHelp();
                return;
            }

            List<GCEvent> events = null;

            if (cmd.needsGcData())
            {
                File gcLogFile = c.getGCLogFile();
                Long timeOrigin = c.getTimeOrigin();

                GCLogParser gcParser = GCLogParserFactory.getParser(gcLogFile);

                events = gcParser.parse(timeOrigin);
            }

            cmd.execute(events);
        }
        catch(ParserException e)
        {
            File file = c == null ? null : c.getGCLogFile();
            long lineNumber = e.getLineNumber();
            String message = e.getMessage();

            System.err.println();
            System.err.println("[error]: " + (file == null ? "" : file.getName() + " line " + lineNumber + ": ") + message);
        }
        catch(UserErrorException e)
        {
            System.err.println();
            System.err.println("[error]: " + e.getMessage());
        }
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    private static void displayHelp()
    {
        BufferedReader r = null;

        String helpFileName = "USAGE.txt";

        try
        {
            InputStream is = Main.class.getClassLoader().getResourceAsStream(helpFileName);

            if (is == null)
            {
                log.error("cannot locate the " + helpFileName + " file");
                return;
            }

            r = new BufferedReader(new InputStreamReader(is));

            String line;

            while((line = r.readLine()) != null)
            {
                System.out.println(line);
            }
        }
        catch(Exception e)
        {
            log.error("failed to read the help file " + helpFileName, e);
        }
        finally
        {
            if (r != null)
            {
                try
                {
                    r.close();
                }
                catch(Exception e)
                {
                    log.warn("failed to close the help file", e);
                }
            }
        }
    }

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



