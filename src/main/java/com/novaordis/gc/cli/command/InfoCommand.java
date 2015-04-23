package com.novaordis.gc.cli.command;

import com.novaordis.gc.cli.Command;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.event.*;
import com.novaordis.gc.model.event.cms.CMSEvent;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;

/**
 * Summarizes the content of the GC log file.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class InfoCommand implements Command
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(InfoCommand.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Configuration c;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public InfoCommand(Configuration c, Iterator<String> args)
    {
        this.c = c;
        log.debug("ignored " + args);
    }

    // Command implementation --------------------------------------------------------------------------------------------------------------

    @Override
    public boolean needsGcData()
    {
        return true;
    }

    @Override
    public void execute(List<GCEvent> events) throws Exception
    {
        int fullCCnt = 0;
        int ngCCnt = 0;
        int sdCnt = 0;
        String algorithm = "Parallel";
        long tfc = 0L;

        for(GCEvent e: events)
        {
            if (e instanceof FullCollection)
            {
                fullCCnt ++;
                tfc += e.getDuration();
            }
            else if (e instanceof NewGenerationCollection)
            {
                ngCCnt ++;
            }
            else if (e instanceof Shutdown)
            {
                sdCnt ++;
            }
            else if (e instanceof CMSEvent)
            {
                algorithm = "CMS";
            }
        }

        long begin = events.get(0).getTime();
        long end = events.get(events.size() - 1).getTime();
        long elapsedMs = end - begin;

        System.out.println("");
        System.out.println("File:                           " + c.getGCLogFile().getAbsolutePath());
        Long timeOrigin = c.getTimeOrigin();
        System.out.println("Time Origin:                    " +
                (timeOrigin == null ? "N/A" : Configuration.TIMESTAMP_DISPLAY_FORMAT.format(c.getTimeOrigin())));
        System.out.println("Beginning:                      " + Configuration.TIMESTAMP_DISPLAY_FORMAT.format(begin));
        System.out.println("End:                            " + Configuration.TIMESTAMP_DISPLAY_FORMAT.format(end));
        System.out.println("GC collection algorithm:        " + algorithm);
        System.out.println("New generation collections:     " + ngCCnt);
        System.out.println("Full collections:               " + fullCCnt);
        System.out.println("Shutdown events:                " + sdCnt);
        System.out.println("Elapsed time:                   " +
                Configuration.DURATION_SECONDS_FORMAT.format((float) elapsedMs / 1000) + " seconds");
        System.out.println("Time spent in full collections: " +
                Configuration.DURATION_SECONDS_FORMAT.format((float)tfc / 1000) + " seconds");
        System.out.println("Percentage:                     " +
                Configuration.PERCENTAGE_FORMAT.format(((float) tfc / (float) elapsedMs) * 100) + "%");
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



