package com.novaordis.gc.cli.command.tccs;

import com.novaordis.gc.cli.Configuration;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class TotsCommandTest extends TimestampConversionCommandTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(TotsCommandTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void basic() throws Exception
    {
        long timestamp = 1429215123000L;
        String[] arga = {Long.toString(timestamp), "\"MMM", "dd", "yyyy", "HH:mm:ssZ\""};
        List<String> args = new ArrayList<String>(Arrays.asList(arga));

        TotsCommand tots = getCommandToTest(null, args.iterator());

        String timestampFormat = tots.getTimestampFormat();
        assertEquals("MMM dd yyyy HH:mm:ssZ", timestampFormat);

        // the command will write to stdout so hijack it for a short while

        PrintStream defaultPrintStream = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        PrintStream testPrintStream = new PrintStream(baos);
        System.setOut(testPrintStream);

        try
        {
            tots.execute(null);
        }
        finally
        {
            // restore the default print stream
            System.setOut(defaultPrintStream);
        }

        byte[] ba = baos.toByteArray();
        String s = new String(ba).trim();
        log.info(s);

        SimpleDateFormat format = new SimpleDateFormat(timestampFormat);
        String timestampString = format.format(timestamp);

        assertEquals(timestampString, s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TotsCommand getCommandToTest(Configuration c, Iterator<String> args) throws Exception
    {
        return new TotsCommand(c, args);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



