package com.novaordis.gc.parser.linear.cms;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.cms.CMSConcurrentMark;
import com.novaordis.gc.model.event.cms.CMSConcurrentMarkStart;
import com.novaordis.gc.model.event.cms.CMSConcurrentPreclean;
import com.novaordis.gc.model.event.cms.CMSInitialMark;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.CurrentMax;
import com.novaordis.gc.parser.GCEventParserBase;
import com.novaordis.gc.parser.ParserException;
import com.novaordis.gc.parser.linear.LineUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CMSParser extends GCEventParserBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CMSParser.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * This method is invoked on a line after it was established that line contains "CMS-initial-mark".
     *
     * @param gcFile - for logging purposes only, can be safely null.
     *
     */
    public static CMSInitialMark parseCMSInitialMark(Timestamp ts, String line, long lineNumber, File gcFile)
        throws ParserException
    {
        List<String> tokens = LineUtil.toSquareBracketTokens(line, lineNumber);

        String s = tokens.get(0);

        // GC [1 CMS-initial-mark: 0K(6291456K)] 268502K(8178944K), 0.1010040 secs
        if (!s.startsWith("GC [1 CMS-initial-mark: "))
        {
            throw new ParserException("CMS-initial-mark line does not contain with \"GC [1 CMS-initial-mark: \"", lineNumber);
        }

        s = s.substring("GC [1 CMS-initial-mark: ".length());

        int i = s.indexOf(']');

        if (i < 0)
        {
            throw new ParserException("unexpected CMS-initial-mark line structure PCMIM-1", lineNumber);
        }

        CurrentMax og = new CurrentMax(s.substring(0, i), lineNumber);

        s = s.substring(i + 1);

        i = s.indexOf(',');

        if (i < 0)
        {
            throw new ParserException("unexpected CMS-initial-mark line structure PCMIM-2", lineNumber);
        }

        CurrentMax h = new CurrentMax(s.substring(0, i).trim(), lineNumber);

        //
        // duration
        //

        String durations = s.substring(i + 1);
        i = durations.indexOf("secs");
        if (i != -1)
        {
            durations = durations.substring(0, i);
        }
        durations = durations.trim();

        long duration = Math.round(Float.parseFloat(durations) * 1000);

        return new CMSInitialMark(ts, duration, og, h);
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // GCEventParser ---------------------------------------------------------------------------------------------------

    /**
     * Example of recognized line:
     *
     * [CMS-concurrent-mark-start]
     *
     * @see com.novaordis.gc.parser.GCEventParser#parse(com.novaordis.gc.model.Timestamp, String, long, GCEvent, File)
     */
    @Override
    public GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent current, File gcFile) throws ParserException
    {
        // all processing of the known CMS logging output is done in a try/catch block, so we can cleanly handle parsing error
        // (malformed CMS lines, for example)

        try
        {
            if (line.contains("CMS-initial-mark"))
            {
                // this is the beginning of a CMS cycle
                return parseCMSInitialMark(ts, line, lineNumber, gcFile);
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

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



