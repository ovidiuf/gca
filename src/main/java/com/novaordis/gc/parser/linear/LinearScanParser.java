package com.novaordis.gc.parser.linear;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.parser.GCEventParser;
import com.novaordis.gc.parser.GCLogParser;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.parser.ParserException;
import com.novaordis.gc.parser.linear.cms.CMSParser;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The linear scan parser receives the reader at construction time, and automatically closes it after the parsing
 * complete. It can thus be used only a single time.
 *
 * This is the preferred parser.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class LinearScanParser implements GCLogParser
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LinearScanParser.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private GCEventParser processorPipeline;

    private Reader reader;

    // patterns to detect multi-line events
    private List<Pattern> multiLineEventPatterns;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Results in an empty pipeline parser. To configure the pipeline, use installDefaultPipeline() or
     * installPipeline(...)
     *
     * The parse() method will close the reader upon completion, whether the execution is successful or not.
     *
     * @see com.novaordis.gc.parser.linear.LinearScanParser#installDefaultPipeline()
     * @see com.novaordis.gc.parser.linear.LinearScanParser#installPipeline(com.novaordis.gc.parser.GCEventParser...)
     */
    public LinearScanParser(Reader reader)
    {
        this.reader = reader;
        this.multiLineEventPatterns = new ArrayList<Pattern>();
    }

    // GCLogParser implementation --------------------------------------------------------------------------------------

    /**
     * This method will close the reader upon completion, successful or not.
     *
     * <b>Multi-line events</b>
     *
     * There are two mechanisms to deal with multi-line events: GCEvent.getActiveParser() (used by Shutdown, so far)
     * and the read-ahead parsing (used by the '(concurrent mode failure)' events so far). Both are valid and time will
     * decide whether we should keep both or refactor and coalesce.
     *
     * For getActiveParser():
     *
     * @see com.novaordis.gc.model.event.GCEvent#getActiveParser()
     */
    @Override
    public List<GCEvent> parse(Long timeOrigin) throws Exception
    {
        List<GCEvent> gcEvents = new ArrayList<GCEvent>();

        BufferedReader br = null;

        ParserException lastLineException = null;

        try
        {
            br = new BufferedReader(reader);

            String events;
            String readAheadLine;
            String currentLine = null;
            boolean done = false;
            long lineNumber = 0;

            //
            // we're doing "read ahead" where we read a line in advance but we parse the current line - this is because
            // the GC logger spreads events over two lines, and it's easier to aggregate the content and parse it as
            // a unit than to use the GCEvent.getActiveParser() mechanism.
            //

            while(!done)
            {
                readAheadLine = br.readLine();

                if (readAheadLine == null)
                {
                    //
                    // there are no more lines in the file, process the current line and exit the loop
                    //

                    done = true;
                }

                if (currentLine == null)
                {
                    if (isTheSecondLineOfTheEvent(readAheadLine))
                    {
                        // this detects a currently unsupported pattern of more than two-line multi-line events
                        throw new UserErrorException(
                            "multi-line events with more than two lines not supported at this time, line " + lineNumber);
                    }
                    currentLine = readAheadLine;
                    lineNumber ++;
                    continue;
                }

                if (isTheSecondLineOfTheEvent(readAheadLine))
                {
                    events = currentLine + readAheadLine;
                    currentLine = null;
                }
                else
                {
                    events = currentLine;
                    currentLine = readAheadLine;
                }

                if (lastLineException != null)
                {
                    // last line generated an exception, stop parsing and bubble the exception up
                    throw lastLineException;
                }

                try
                {
                    processLine(events, lineNumber++, timeOrigin, gcEvents, processorPipeline);
                }
                catch(ParserException e)
                {
                    // we don't bubble the exception up right away, we wait until we read the next line; this way we
                    // simply ignore (and warn about) incomplete last lines
                    lastLineException = e;
                }
            }

            if (lastLineException != null)
            {
                // the last line of the file generated a parsing failure, this is common as the JVM might not have
                // finished writing it
                log.warn(lastLineException.getMessage());
            }

            log.debug("parsing done");
        }
        finally
        {
            if (br != null)
            {
                br.close();
            }
        }

        return gcEvents;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void installDefaultPipeline()
    {
        // place the CMS parser on the first position in line, to pick the CMS events that start with [GS ...
        installPipeline(
            new CMSParser(),
            new NewGenerationCollectionParser(),
            new FullCollectionParser(),
            new ShutdownParser());
    }

    public void addSecondLinePattern(Pattern p)
    {
        multiLineEventPatterns.add(p);
    }

    /**
     * @return the underlying list so handle with care.
     */
    public List<Pattern> getSecondLinePatterns()
    {
        return multiLineEventPatterns;
    }

    /**
     * Currently there's no relationship between the line content and the event type. If such a relationship can be
     * determined in the future, refactor this method.
     *
     * @param s may be null, which should not break the call.
     */
    public boolean isTheSecondLineOfTheEvent(String s)
    {
        if (s == null)
        {
            return false;
        }

        for(Pattern p: multiLineEventPatterns)
        {
            if (p.matcher(s).matches())
            {
                return true;
            }
        }

        return false;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * Assembles the pipeline from the given parsers, by linking the parsers together, and installs it.
     */
    void installPipeline(GCEventParser... parsers)
    {
        processorPipeline = null;

        if (parsers == null)
        {
            return;
        }

        for(GCEventParser p: parsers)
        {
            if (processorPipeline == null)
            {
                processorPipeline = p;
            }
            else
            {
                GCEventParser last = processorPipeline;
                while(last.getNext() != null)
                {
                    last = last.getNext();
                }

                last.setNext(p);
            }

            // insurance that we're not part of some other chain
            p.setNext(null);
        }
    }

    /**
     * May return null.
     */
    GCEventParser getPipeline()
    {
        return processorPipeline;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * Parse a line, which may contain multiple GC events.
     *
     * @throws Exception
     * @throws com.novaordis.gc.UserErrorException
     */
    private static void processLine(String line, long lineNumber, Long timeOrigin,
                                    List<GCEvent> events, GCEventParser processorPipeline) throws Exception
    {
        if (line == null)
        {
            // recursion exit
            return;
        }

        if (events == null)
        {
            throw new IllegalArgumentException("null events list");
        }

        // identify timestamps and break the line in pieces so each of the piece starts with a timestamp;
        // this is necessary because sometimes we encounter more than one timestamped event per line

        int from = 0;

        while(from < line.length())
        {
            int fragmentStart, fragmentEnd;

            Timestamp ts = Timestamp.find(line, from, lineNumber);
            Timestamp ts2 = null;

            if (ts == null)
            {
                fragmentStart = from;
            }
            else
            {
                fragmentStart = ts.getEndPosition();
                ts2 = Timestamp.find(line, ts.getEndPosition(), lineNumber);
            }

            if (ts2 != null)
            {
                // multiple events on the same line
                fragmentEnd = ts2.getStartPosition();
            }
            else
            {
                // no more events on this line
                fragmentEnd = line.length();
            }

            String eventFragment = line.substring(fragmentStart, fragmentEnd);

            adjustTimeOriginOnTimeStamps(timeOrigin, ts, ts2, lineNumber);

            parseEvent(ts, eventFragment, events, processorPipeline, lineNumber);

            from = fragmentEnd;
        }
    }

    /**
     * Extracts the event from the given fragment. Either add a new GC event at the end of the list or modify an
     * existing event, by adding more information to it.
     *
     * @param ts - the timestamp. Some GC events (such as SHUTDOWN) do not have a timestamp, so a null ts is legal.
     * @param eventFragment - guaranteed to contain data for a <b>single</b> GC event. If we identify a timestamp
     *                      in it, then there's something is wrong.
     */
    private static void parseEvent(Timestamp ts, String eventFragment, List<GCEvent> events,
                                   GCEventParser processorPipeline, long lineNumber) throws Exception
    {
        // look up an appropriate parser - it's either one from the processing pipeline or the parser associated
        // with the last event, in the case of a multi-line event

        GCEvent crtEvent;
        GCEventParser crtParser;

        if (events.isEmpty() || ((crtParser = (crtEvent = events.get(events.size() - 1)).getActiveParser()) == null))
        {
            // no active parser associated with the last event, nullify the "current event" and use the pipeline for
            // parsing
            crtEvent = null;
            crtParser = processorPipeline;
        }

        while (crtParser != null)
        {
            GCEvent event = crtParser.parse(ts, eventFragment, lineNumber, crtEvent);

            if (event != null)
            {
                // add it, unless it is already there

                if (!event.equals(crtEvent))
                {
                    events.add(event);
                }

                return;
            }
            else
            {
                // try the next one
                crtParser = crtParser.getNext();
            }
        }

        // we reached the bottom of the GCEventParser pipeline,  we weren't able to find any event in the fragment,
        // we don't know how to parse this log entry, bail out
        log.warn("don't know to parse line " + lineNumber + ", fragment \"" + eventFragment + "\"");
    }

    /**
     * We handle this in a separate method to be able to consistently catch NullPointerException in case we don't
     * have a time origin and the time stamps need it - we need to turn this into an user error, which will bubble up
     * all the way to CLI.
     */
    private static void adjustTimeOriginOnTimeStamps(Long timeOrigin, Timestamp ts, Timestamp ts2, long lineNumber)
        throws UserErrorException
    {
        try
        {
            if (ts != null)
            {
                // adjust time if necessary, otherwise it'll be a noop
                ts.applyTimeOrigin(timeOrigin);
            }

            if (ts2 != null)
            {
                // adjust time if necessary, otherwise it'll be a noop
                ts2.applyTimeOrigin(timeOrigin);
            }
        }
        catch(NullPointerException e)
        {
            throw new UserErrorException("the GC event specified on line " + lineNumber + " needs a time origin, which is not specified. See the 'Time Origin' section of the documentation");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------
}
