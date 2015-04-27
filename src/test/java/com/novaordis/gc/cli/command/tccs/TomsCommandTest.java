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
public class TomsCommandTest extends TimestampConversionCommandTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(TomsCommandTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void basic() throws Exception
    {
        String[] arga = {"\"Apr", "16", "2015", "20:12:03+0000\"", "\"MMM", "dd", "yyyy", "HH:mm:ssZ\""};
        List<String> args = new ArrayList<String>(Arrays.asList(arga));

        TomsCommand toms = getCommandToTest(null, args.iterator());

        String timestampFormat = toms.getTimestampFormat();
        assertEquals("MMM dd yyyy HH:mm:ssZ", timestampFormat);

        // the command will write to stdout so hijack it for a short while

        PrintStream defaultPrintStream = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        PrintStream testPrintStream = new PrintStream(baos);
        System.setOut(testPrintStream);

        try
        {
            toms.execute(null);
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
        long timestamp = format.parse("Apr 16 2015 20:12:03+0000").getTime();

        assertEquals(timestamp, Long.parseLong(s));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TomsCommand getCommandToTest(Configuration c, Iterator<String> args) throws Exception
    {
        return new TomsCommand(c, args);
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



