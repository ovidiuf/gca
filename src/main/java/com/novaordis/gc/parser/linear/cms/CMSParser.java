package com.novaordis.gc.parser.linear.cms;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.cms.CMSConcurrentMark;
import com.novaordis.gc.model.event.cms.CMSConcurrentMarkStart;
import com.novaordis.gc.model.event.cms.CMSConcurrentPreclean;
import com.novaordis.gc.model.event.cms.CMSInitialMark;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.CurrentMax;
import com.novaordis.gc.parser.Duration;
import com.novaordis.gc.parser.GCEventParserBase;
import com.novaordis.gc.parser.ParserException;
import com.novaordis.gc.parser.linear.LineUtil;

import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CMSParser extends GCEventParserBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * This method is invoked on a line after it was established that line contains "CMS-initial-mark".
     */
    public static CMSInitialMark parseCMSInitialMark(Timestamp ts, String line, long lineNumber)
        throws ParserException
    {
        List<String> tokens = LineUtil.toSquareBracketTokens(line, lineNumber);

        String gcInfo = tokens.get(0);

        // sometimes we also get user/sys/real time information in the second token - but not always. We are
        // ignoring it anyway, as the same information is also included in gcInfo

        tokens = LineUtil.toSquareBracketTokens(gcInfo, lineNumber);

        String prefix = tokens.get(0);
        String ogString = tokens.get(1);
        String heapString = tokens.get(2);
        String durationString = tokens.get(3);

        // sanity check - prefix must be 'GC'

        if (!"GC".equals(prefix))
        {
            throw new ParserException("CMS-initial-mark line does not contain\"GC\"", lineNumber);
        }

        // get OG info - throw everything before "CMS-initial-mark:"

        int i = ogString.indexOf("CMS-initial-mark:");

        if (i == -1)
        {
            throw new ParserException("CMS-initial-mark line does not contain\"CMS-initial-mark:\"", lineNumber);
        }

        ogString = ogString.substring(i + "CMS-initial-mark:".length()).trim();

        CurrentMax og = new CurrentMax(ogString, lineNumber);
        CurrentMax heap = new CurrentMax(heapString, lineNumber);
        long duration = Duration.toLongMilliseconds(durationString, lineNumber);

        return new CMSInitialMark(ts, duration, og, heap);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // GCEventParser ---------------------------------------------------------------------------------------------------

    /**
     * Example of recognized line:
     *
     * [CMS-concurrent-mark-start]
     *
     * @see com.novaordis.gc.parser.GCEventParser#parse(com.novaordis.gc.model.Timestamp, String, long, GCEvent)
     */
    @Override
    public GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent current)
        throws ParserException
    {
        // all processing of the known CMS logging output is done in a try/catch block, so we can cleanly handle parsing
        // error (malformed CMS lines, for example)

        try
        {
            if (line.contains("CMS-initial-mark"))
            {
                // this is the beginning of a CMS cycle
                return parseCMSInitialMark(ts, line, lineNumber);
            }
            else if (line.startsWith("[CMS-concurrent-mark-start"))
            {
                return new CMSConcurrentMarkStart(ts);
            }
            else if (line.contains("CMS-concurrent-preclean"))
            {
                return new CMSConcurrentPreclean(ts);
            }
            else if (line.contains("CMS-concurrent-mark"))
            {
                return new CMSConcurrentMark(ts);
            }
            else
            {
                // we don't recognize this line as CMS logging output
                return null;
            }
        }
        catch(Exception e)
        {
            throw new ParserException(
                    "invalid/unrecognized CMS line: \"" + line + "\"" +
                            (e.getMessage() != null ? " (" + e.getClass().getName() + ": " + e.getMessage() +
                                ") at line " + lineNumber: ""), e, lineNumber);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



