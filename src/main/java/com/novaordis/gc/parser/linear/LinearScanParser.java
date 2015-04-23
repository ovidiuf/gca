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
import java.io.File;
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

    // optional - for reporting purposes only, can be null.
    private File gcFile;
    private boolean suppressTimestampWarning;

    // patterns to detect multi-line events
    private List<Pattern> multiLineEventPatterns;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * Results in an empty pipeline parser. To configure the pipeline, use installDefaultPipeline() or
     * installPipeline(...)
     *
     * The parse() method will close the reader upon completion, whether the execution is successful or not.
     *
     * @param gcFile file name for reporting purposes.
     *
     * @see com.novaordis.gc.parser.linear.LinearScanParser#installDefaultPipeline()
     * @see com.novaordis.gc.parser.linear.LinearScanParser#installPipeline(com.novaordis.gc.parser.GCEventParser...)
     */
    public LinearScanParser(Reader reader, File gcFile, boolean suppressTimestampWarning)
    {
        this.reader = reader;
        this.gcFile = gcFile;
        this.suppressTimestampWarning  = suppressTimestampWarning;
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

            String event;
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
                        throw new UserErrorException("multi-line events with more than two lines not supported at this time, line " + lineNumber);
                    }
                    currentLine = readAheadLine;
                    lineNumber ++;
                    continue;
                }

                if (isTheSecondLineOfTheEvent(readAheadLine))
                {
                    event = currentLine + readAheadLine;
                    currentLine = null;
                }
                else
                {
                    event = currentLine;
                    currentLine = readAheadLine;
                }

                if (lastLineException != null)
                {
                    // last line generated an exception, stop parsing and bubble the exception up
                    throw lastLineException;
                }

                try
                {
                    processEvent(event, lineNumber++, timeOrigin, gcEvents, processorPipeline, gcFile, suppressTimestampWarning);
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
        installPipeline(new NewGenerationCollectionParser(), new FullCollectionParser(), new CMSParser(), new ShutdownParser());
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
     * Either adds a new GC event at the end of the list or modifies an existing event, by adding more information to
     * it.
     *
     * @param gcFile for reporting purposes only, it can be null.
     *
     * @throws Exception
     */
    private static void processEvent(String line, long lineNumber, Long timeOrigin,
                                     List<GCEvent> events, GCEventParser processorPipeline,
                                     File gcFile, boolean suppressTimestampWarning) throws Exception
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

        // 5.268: [Full GC (System) [PSYoungGen: 72810K->0K(1835008K)] [PSOldGen: 0K->72190K(4194304K)] 72810K->72190K(6029312K) [PSPermGen: 29283K->29283K(59136K)], 0.2507000 secs] [Times: user=0.24 sys=0.01, real=0.25 secs]
        //
        // or
        //
        // 2013-10-10T14:33:21.747-0500: 7.954: [Full GC 7.954: [CMS: 0K->16259K(2516608K), 0.3846450 secs] 238663K->16259K(3145152K), [CMS Perm : 21247K->21233K(21248K)], 0.3848730 secs] [Times: user=0.34 sys=0.05, real=0.39 secs]
        //
        // or
        //
        //
        // Heap
        //  PSYoungGen      total 1926336K, used 1370287K [0x0000000780000000, 0x0000000800000000, 0x0000000800000000)
        //  ...

        // strip off time stamp

        Timestamp ts = null;

        int i = line.indexOf(": ");

        if (i != -1)
        {
            // look for offset, but not if we have a [.. between i and i2
            int i2 = line.indexOf(": ", i + 2);

            if (i2 != -1 &&
                line.charAt(i2 - 1) >= '0' &&
                line.charAt(i2 - 1) <= '9' &&
                (line.indexOf('[', i) == -1 ||
                    line.indexOf('[', i) > i2))
            {
                i = i2;
            }

            // explicit timestamp and/or offset identified
            String s = line.substring(0, i);

            if (Timestamp.isTimestamp(s))
            {
                ts = new Timestamp(s, timeOrigin, gcFile, suppressTimestampWarning);
                line = line.substring(i + ": ".length()).trim();
            }

            // it is fine to leave timestamp un-initialized in certain conditions, there are timestamp-less lines that
            // contain ":", for example "CMS: abort preclean due to time ...". Simply leave the timestamp
            // non-initialized and pass the line to the processor pipeline - if we find a processor for it, the
            // processor will know what to do with it.
        }

        if (ts == null && !events.isEmpty())
        {
            // assign an estimated timestamp, use the last event timestamp, it will be overridden later if necessary
            GCEvent last = events.get(events.size() - 1);
            ts = new Timestamp(last.getTime(), last.getOffset());
        }

        // some GC events (such as SHUTDOWN) do not have a timestamp, so a null ts is legal

        // look up an appropriate parser - it's either one from the processing pipeline or the parser associated with
        // the last event, in the case of a multi-line event

        GCEvent crtEvent;
        GCEventParser crtParser;

        if (events.isEmpty() ||
            ((crtParser = (crtEvent = events.get(events.size() - 1)).getActiveParser()) == null))
        {
            // no active parser associated with the last event, nullify the "current event" and use the pipeline for
            // parsing
            crtEvent = null;
            crtParser = processorPipeline;
        }

        while (crtParser != null)
        {
            GCEvent event = crtParser.parse(ts, line, lineNumber, crtEvent, gcFile);

            if (event != null)
            {
                // add it, unless it is already there
                if (!event.equals(crtEvent))
                {
                    events.add(event);
                }

                // if there is "line" left, it means there are more then one event per line, so recursively parse them
                String lineLeft = event.getNextEventRenderingOnTheSameLine();
                processEvent(lineLeft, lineNumber, timeOrigin, events, processorPipeline, gcFile, suppressTimestampWarning);
                return;
            }
            else
            {
                // try the next one
                crtParser = crtParser.getNext();
            }
        }

        // if we reached the bottom of the GCEventParser pipeline, we don't know how to parse this log entry, bail out
        log.warn("don't know to parse line " + lineNumber + ": \"" + line + "\"");
    }

    // Inner classes ---------------------------------------------------------------------------------------------------
}



