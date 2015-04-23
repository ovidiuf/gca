package com.novaordis.gc.cli.command;

import com.novaordis.gc.StringTokenizerIterator;
import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Command;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.*;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.expression.Expression;
import com.novaordis.gc.model.expression.Expressions;
import com.novaordis.series.Header;
import com.novaordis.series.LinkedListSeries;
import com.novaordis.series.Metric;
import com.novaordis.series.Series;
import com.novaordis.series.csv.CsvOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Exports the parsed content into a CSV file.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ExportCommand implements Command
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ExportCommand.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private File output;
    private Unit outputMemoryUnit;
    private Set<CollectionType> collectionTypes;

    // it does NOT contain the timestamp - the timestamp is handled separately, as is a required element and always on the first position
    // in the row
    private List<Expression> expressionsToBeExported;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public ExportCommand(Configuration c, Iterator<String> args) throws UserErrorException
    {
        this();

        String crt = null;

        //noinspection LoopStatementThatDoesntLoop
        while(crt != null || args.hasNext())
        {
            if (crt == null)
            {
                // read the next argument from the stream, unless the previous processing sequence did that already
                crt = args.next();
            }

            if ("-o".equals(crt) || "--output".equals(crt))
            {
                String arg = insureNotLastOrFollowedBySwitch(crt, args);
                output = new File(arg);
            }
            else if ("-u".equals(crt) || "--unit".equals(crt))
            {
                String arg = insureNotLastOrFollowedBySwitch(crt, args);
                outputMemoryUnit = Unit.extendedValueOf(arg);
            }
            else if ("-c".equals(crt) || "--collection-type".equals(crt))
            {
                crt = processCollectionTypeArguments(args);
                continue;
            }
            else if ("-f".equals(crt) || "--fields".equals(crt))
            {
                crt = processFieldsArguments(args);

                if (expressionsToBeExported.isEmpty())
                {
                    throw new UserErrorException("-f|--fields should be followed by a comma-separated combination of field definitions");
                }

                continue;
            }
            else
            {
                throw new UserErrorException("unknown export option: " + crt);
            }

            crt = null;
        }

        // don't do anything if output is null, we will simply write at stdout ...

        if (outputMemoryUnit == null)
        {
            outputMemoryUnit = Unit.M;
        }

        if (collectionTypes.isEmpty())
        {
            collectionTypes.add(CollectionType.FULL_COLLECTION);
        }

        // if we did not specify types, install default
        if (expressionsToBeExported.isEmpty())
        {
            // only timestamp
            expressionsToBeExported.add(FieldType.OFFSET);
            expressionsToBeExported.add(FieldType.COLLECTION_TYPE);
            expressionsToBeExported.add(FieldType.DURATION);
            expressionsToBeExported.add(FieldType.OG_CAPACITY);
            expressionsToBeExported.add(FieldType.OG_AFTER);
        }

        log.debug(this + " constructed, configuration: " + c);
    }

    /**
     * Package-exposed for testing
     */
    ExportCommand()
    {
        this.collectionTypes = new HashSet<CollectionType>();
        this.expressionsToBeExported = new ArrayList<Expression>();
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
        log.debug("executing " + this);

        // filter interesting events into a series

        Series s = toSeries(events);

        // write

        OutputStream os = null;

        try
        {
            os = output == null ? System.out : new FileOutputStream(output);

            CsvOutput o = new CsvOutput(os);
            o.write(s);

            if (output != null)
            {
                log.info(output + " written");
            }
        }
        finally
        {
            if (os != null)
            {
                os.close();
            }
        }
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    public File getOutputFile()
    {
        return output;
    }

    public Unit getOutputMemoryUnit()
    {
        return outputMemoryUnit;
    }

    public Set<CollectionType> getCollectionTypes()
    {
        return collectionTypes;
    }

    public List<Expression> getExpressions()
    {
        return expressionsToBeExported;
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    /**
     * Package-exposed for testing.
     */
    void setExpressions(List<Expression> es)
    {
        this.expressionsToBeExported = es;
    }

    /**
     * Package-exposed for testing.
     */
    void setCollectionTypes(Set<CollectionType> cts)
    {
        this.collectionTypes = cts;
    }

    /**
     * Convert the list of GC events to a generic series, after filtering the events we're not interested in and performing any unit
     * conversions that might be required.
     *
     * Package-exposed for testing.
     */
    Series toSeries(List<GCEvent> events) throws Exception
    {
        Series s = new LinkedListSeries();
        List<Header> headers = new ArrayList<Header>();

        for(Expression exp: expressionsToBeExported)
        {
            // we only perform unit conversion for memory values so far
            Unit toUnit = FieldCategory.MEMORY.equals(exp.getCategory()) ? outputMemoryUnit : null;
            Header h = exp.toHeader(toUnit);
            headers.add(h);
        }

        s.setHeaders(headers);

        for(GCEvent e: events)
        {
            // drop events we're not interested in ...

            if (!collectionTypes.contains(e.getCollectionType()))
            {
                continue;
            }

            // add fields

            List<Metric> metrics = new ArrayList<Metric>();

            for(Expression exp: expressionsToBeExported)
            {
                Metric m = Metric.EMPTY_METRIC;

                Value v = exp.evaluate(e);

                // we only perform unit conversion for memory values so far
                Unit toUnit = FieldCategory.MEMORY.equals(exp.getCategory()) ? outputMemoryUnit : null;

                if (v != null)
                {
                    m = v.toMetric(toUnit);
                }

                metrics.add(m);
            }

            try
            {
                s.add(e.getTime(), metrics);
            }
            catch(Exception e2)
            {
                throw new UserErrorException(
                    "failed to add the following event to the series: " + e + ", offset " + e.getOffset() + ". Underlying cause: " + e2.getMessage(), e2);
            }
        }

        // enforce our own timestamp format
        s.setTimestampFormat(Configuration.TIMESTAMP_DISPLAY_FORMAT);

        return s;
    }

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    /**
     * Performs a series of validations common to all switches - insures that the switch is followed by an argument and it is NOT followed
     * by other switch.
     *
     * @return the validated next argument.
     *
     * @throws UserErrorException
     */
    private String insureNotLastOrFollowedBySwitch(String crtSwitch, Iterator<String> args) throws UserErrorException
    {
        if (!args.hasNext())
        {
            throw new UserErrorException(crtSwitch + " should be followed by an argument");
        }

        String arg = args.next();

        if (arg.startsWith("-"))
        {
            throw new UserErrorException(crtSwitch + " should be followed by an argument, not by " + arg);
        }

        return arg;
    }

    /**
     * The iterator is set to return the immediately next argument after "-c|--collection-type".
     */
    private String processCollectionTypeArguments(Iterator<String> args) throws UserErrorException
    {
        if (!args.hasNext())
        {
            throw new UserErrorException(
                    "-c|--collection-type should be followed by a comma-separated combination of " +
                            CollectionType.getCommandLineLabels() + " or \"all\"");
        }

        while(args.hasNext())
        {
            String s = args.next();
            if (s.endsWith(","))
            {
                s = s.substring(0, s.length() - 1);

                if (s.length() == 0)
                {
                    // comma by itself, ignore
                    continue;
                }
            }

            CollectionType t;

            if ((t = CollectionType.fromCommandLineLabel(s)) != null)
            {
                // is a collection type
                collectionTypes.add(t);
            }
            else if (CollectionType.ALL.equalsIgnoreCase(s))
            {
                collectionTypes.addAll(Arrays.asList(CollectionType.values()));
            }
            else
            {
                // not a collection type, return control to the upper layer
                return s;
            }
        }

        return null;
    }

    /**
     * The iterator is set to return the immediately next argument after "-f|--fields".
     *
     * Side-effecy: updates the content of expressionsToBeExported list.
     */
    private String processFieldsArguments(Iterator<String> args) throws UserErrorException
    {
        Iterator<String> prevArgIterator = null;
        Iterator<String> crtArgIterator = args;
        String arg;

        outer: while(true)
        {
            if (!crtArgIterator.hasNext())
            {
                if (prevArgIterator != null)
                {
                    crtArgIterator = prevArgIterator;
                    prevArgIterator = null;
                    continue;
                }

                break;
            }

            arg = crtArgIterator.next();

            while (arg.endsWith(","))
            {
                arg = arg.substring(0, arg.length() - 1);

                if (arg.length() == 0)
                {
                    // comma by itself, ignore
                    continue outer;
                }
            }

            while (arg.startsWith(","))
            {
                arg = arg.substring(1);

                if (arg.length() == 0)
                {
                    // comma by itself, ignore
                    continue outer;
                }
            }

            if (arg.contains(","))
            {
                prevArgIterator = crtArgIterator;
                crtArgIterator = new StringTokenizerIterator(arg, ",");
                continue;
            }

            Expression e;

            if ((e = Expressions.parse(arg)) != null)
            {
                expressionsToBeExported.add(e);
            }
            else
            {
                // could not identify a field expression
                return arg;
            }
        }

        return null;
    }

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



