package com.novaordis.gc.cli.command.tccs;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.event.GCEvent;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Converts time information from milliseconds to a log timestamp. See USAGE.txt for details.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class TotsCommand extends TimestampConversionCommand
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private long timestamp;
    private SimpleDateFormat outputTimestampFormat;

    // Constructors ----------------------------------------------------------------------------------------------------

    public TotsCommand(Configuration c, Iterator<String> args) throws UserErrorException
    {
        super(c, args);

        // we expect the timestamp and then the timestamp format

        if (!args.hasNext())
        {
            throw new UserErrorException("expecting a millisecond timestamp");
        }

        String timestampLiteral = args.next();

        try
        {
            timestamp = Long.parseLong(timestampLiteral);
        }
        catch(Exception e)
        {
            throw new UserErrorException("'" + timestampLiteral + "' cannot be converted to long", e);
        }

        if (!args.hasNext())
        {
            throw new UserErrorException("expecting the output format for the timestamp that has just been entered");
        }

        timestampFormatLiteral = args.next();

        boolean quoted = false;

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

        try
        {
            outputTimestampFormat = new SimpleDateFormat(timestampFormatLiteral);
        }
        catch(Exception e)
        {
            throw new UserErrorException(
                "the format that has been provided '" + timestampFormatLiteral +
                    "' does not seem to be a valid timestamp format", e);
        }
    }

    // TimestampConversionCommand overrides ----------------------------------------------------------------------------

    @Override
    public void execute(List<GCEvent> events) throws Exception
    {
        System.out.println(outputTimestampFormat.format(timestamp));
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



