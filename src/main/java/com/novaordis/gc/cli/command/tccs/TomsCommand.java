package com.novaordis.gc.cli.command.tccs;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.event.GCEvent;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Converts time information from log timestamps to milliseconds. See USAGE.txt for details.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class TomsCommand extends TimestampConversionCommand
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long timestamp;

    // Constructors ----------------------------------------------------------------------------------------------------

    public TomsCommand(Configuration c, Iterator<String> args) throws UserErrorException
    {
        super(c, args);

        // we expect the timestamp and then the timestamp format

        if (!args.hasNext())
        {
            throw new UserErrorException("expecting a timestamp");
        }

        String timestampLiteral = args.next();

        boolean quoted = false;

        if (timestampLiteral.startsWith("\""))
        {
            // close quotes

            quoted = true;

            String next;

            while(!(next = args.next()).endsWith("\""))
            {
                timestampLiteral += " " + next;
            }

            timestampLiteral += " " + next;
        }

        if (quoted)
        {
            timestampLiteral = timestampLiteral.substring(1);
            timestampLiteral = timestampLiteral.substring(0, timestampLiteral.length() - 1);
        }

        if (!args.hasNext())
        {
            throw new UserErrorException("expecting the format for the timestamp that has just been entered");
        }

        timestampFormatLiteral = args.next();

        quoted = false;

        if (timestampFormatLiteral.startsWith("\""))
        {
            // close quotes

            quoted = true;

            String next;

            while(!(next = args.next()).endsWith("\""))
            {
                timestampFormatLiteral += " " + next;
            }

            timestampFormatLiteral += " " + next;
        }

        if (quoted)
        {
            timestampFormatLiteral = timestampFormatLiteral.substring(1);
            timestampFormatLiteral = timestampFormatLiteral.substring(0, timestampFormatLiteral.length() - 1);
        }

        SimpleDateFormat timestampFormat;

        try
        {
            timestampFormat = new SimpleDateFormat(timestampFormatLiteral);
        }
        catch(Exception e)
        {
            throw new UserErrorException(
                "the format that has been provided '" + timestampFormatLiteral +
                    "' does not seem to be a valid timestamp format", e);
        }

        try
        {
            timestamp = timestampFormat.parse(timestampLiteral).getTime();
        }
        catch(Exception e)
        {
            throw new UserErrorException(
                "the timestamp that has been provided '" + timestampLiteral +
                    "' cannot be converted to a timestamp using the format '" + timestampFormatLiteral + "'", e);
        }
    }

    // TimestampConversionCommand overrides ----------------------------------------------------------------------------

    @Override
    public void execute(List<GCEvent> events) throws Exception
    {
        System.out.println(Long.toString(timestamp));
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



