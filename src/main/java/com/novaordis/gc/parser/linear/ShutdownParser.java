package com.novaordis.gc.parser.linear;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.Shutdown;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.GCEventParserBase;
import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ShutdownParser extends GCEventParserBase
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ShutdownParser.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // GCEventParser -----------------------------------------------------------------------------------------------------------------------

    /**
     * Example of recognized sequence of lines:
     *
     * Heap
     *  PSYoungGen      total 1926336K, used 1370287K [0x0000000780000000, 0x0000000800000000, 0x0000000800000000)
     *   eden space 1756096K, 70% used [0x0000000780000000,0x00000007cb99bc00,0x00000007eb2f0000)
     *   from space 170240K, 77% used [0x00000007eb2f0000,0x00000007f3380020,0x00000007f5930000)
     *   to   space 168960K, 0% used [0x00000007f5b00000,0x00000007f5b00000,0x0000000800000000)
     *  PSOldGen        total 4194304K, used 832880K [0x0000000680000000, 0x0000000780000000, 0x0000000780000000)
     *   object space 4194304K, 19% used [0x0000000680000000,0x00000006b2d5c3a8,0x0000000780000000)
     *  PSPermGen       total 265536K, used 265525K [0x0000000660000000, 0x0000000670350000, 0x0000000680000000)
     *   object space 265536K, 99% used [0x0000000660000000,0x000000067034d518,0x0000000670350000)
     *
     * @see com.novaordis.gc.parser.GCEventParser#parse(com.novaordis.gc.model.Timestamp, String, long, GCEvent, File)
     */
    @Override
    public GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent event, File gcFile) throws ParserException
    {
        if (event == null)
        {
            // we're just starting
            if (!"Heap".equals(line.trim()))
            {
                // it's not a Shutdown event, drop it
                return null;
            }

            // it's a shutdown event
            return new Shutdown(ts, this);
        }
        else
        {
            // we're adding lines to a current SHUTDOWN event
            if (!(event instanceof Shutdown))
            {
                throw new IllegalArgumentException(
                        "attempt to parse a SHUTDOWN event line (\"" + line + "\") with a non-SHUTDOWN event (" + event + ")");
            }

            Shutdown crt = (Shutdown)event;

            // TODO for the time being we ignore the content of all lines

            crt.addLine(line);

            return crt;
        }
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



