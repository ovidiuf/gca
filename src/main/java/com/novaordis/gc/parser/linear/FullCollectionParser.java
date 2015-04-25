package com.novaordis.gc.parser.linear;

import com.novaordis.gc.model.event.FullCollection;
import com.novaordis.gc.parser.BeforeAfterMax;
import com.novaordis.gc.parser.GCEventParser;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.parser.GCEventParserBase;
import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class FullCollectionParser extends GCEventParserBase
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(FullCollectionParser.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // GCEventParser -----------------------------------------------------------------------------------------------------------------------

    /**
     * Example of recognized line:
     *
     * [Full GC (System) [PSYoungGen: 32861K->0K(1722048K)] [PSOldGen: 1663616K->1696127K(4194304K)] 1696478K->1696127K(5916352K) [PSPermGen: 292408K->292408K(292416K)], 2.4516460 secs] [Times: user=2.54 sys=0.00, real=2.45 secs]
     * [Full GC [PSYoungGen: 1080K->1K(1398144K)] [PSOldGen: 4037629K->895254K(4194303K)] 4038710K->895254K(5592448K) [PSPermGen: 270279K->270279K(270336K)], 1.6447130 secs] [Times: user=1.64 sys=0.00, real=1.65 secs]
     * [Full GC (System) [PSYoungGen: 25762K->0K(887808K)] [ParOldGen: 377824K->217543K(1398144K)] 403587K->217543K(2285952K) [PSPermGen: 149513K->143503K(149696K)], 1.8638674 secs] [Times: user=5.80 sys=0.00, real=1.86 secs]
     *
     * @see GCEventParser#parse(com.novaordis.gc.model.Timestamp, String, long, GCEvent)
     */
    @Override
    public GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent current) throws ParserException
    {
        if (!line.startsWith("[Full GC"))
        {
            return null;
        }

        // all processing is done in a try/catch block, so we can cleanly handle parsing error (unrecognized lines, for example)

        try
        {
            List<String> tokens = LineUtil.toSquareBracketTokens(line, lineNumber);

            // only use the first token, go down one level

            if (tokens.isEmpty())
            {
                throw new ParserException("incomplete line " + lineNumber + ": \"" + line + "\"", lineNumber);
            }

            String fullGcTok = tokens.get(0);

            tokens = LineUtil.toSquareBracketTokens(fullGcTok, lineNumber);

            boolean system = false;

            int crtTokenIndex = 0;

            String header = tokens.get(crtTokenIndex ++);

            if (header.startsWith("Full GC (System)"))
            {
                system = true;
            }

            BeforeAfterMax ng = null, og = null;

            String tok = tokens.get(crtTokenIndex ++);

            // sanity check - if we don't recognize the format, discard the line ...
            if (tok.startsWith("PSYoungGen: "))
            {

                //
                // PSYoungGen: 32861K->0K(1722048K)
                //

                String ngs = tok;
                ngs = ngs.substring("PSYoungGen: ".length());

                ng = new BeforeAfterMax(ngs, lineNumber);

                //
                // PSOldGen: 1663616K->1696127K(4194304K)
                //

                String ogs = tokens.get(crtTokenIndex ++);

                // sanity check - if we don't recognize the format, discard the line
                int tokenLength;
                if (ogs.startsWith("PSOldGen: "))
                {
                    tokenLength = "PSOldGen: ".length();
                }
                else if (ogs.startsWith("ParOldGen: "))
                {
                    tokenLength = "ParOldGen: ".length();
                }
                else
                {
                    throw new Exception("expecting \"PSOldGen:|ParOldGen: ...\" and got \"" + ogs + "\"");
                }

                ogs = ogs.substring(tokenLength);
                og = new BeforeAfterMax(ogs, lineNumber);
            }
            else if (tok.startsWith("CMS: "))
            {
                //
                // CMS: 468402K->442325K(2516608K), 2.3616630 secs
                //

                String cmsOg = tok;
                cmsOg = cmsOg.substring("CMS: ".length());

                int i = cmsOg.indexOf(", ");

                // currently we discard the time information that comes right after the before/after group and comma

                if (i < 0)
                {
                    throw new Exception("(FCP-1): we don't know how to handle a non-comma CMS segment in line: " + line);
                }

                cmsOg = cmsOg.substring(0, i);

                og = new BeforeAfterMax(cmsOg, lineNumber);
            }
            else if (tok.contains("CMS-concurrent-mark"))
            {
                // TODO always found in this configuration, we will need to parse coalesced lines

//        53365.009: [Full GC 53365.009: [CMS53369.873: [CMS-concurrent-mark: 5.274/5.371 secs] [Times: user=12.80 sys=0.36, real=5.37 secs]
//         (concurrent mode failure): 11628290K->11661304K(11666432K), 23.5081640 secs] 15947092K->12535361K(16040192K), [CMS Perm : 117651K->117651K(208152K)], 23.5087140 secs] [Times: user=27.85 sys=0.31, real=23.51 secs]
                log.debug("ENCOUNTERED FULL COLLECTION THAT CONTAINS CMS-concurrent-mark");
                // the code below will parse
            }
            else
            {
                throw new Exception("expecting \"PSYoungGen:|CMS: ...\" and got \"" + tok + "\"");
            }

            //
            // Entire heap "1696478K->1696127K(5916352K)"
            //

            String heaps = tokens.get(crtTokenIndex ++);
            BeforeAfterMax heap = new BeforeAfterMax(heaps, lineNumber);

            //
            // PSPermGen: 292408K->292408K(292416K)
            //
            // or
            //
            // CMS Perm : 58673K->58575K(58800K)
            //

            String pgs = tokens.get(crtTokenIndex ++);

            // sanity check - if we don't recognize the format, discard the line ...
            if (!pgs.startsWith("PSPermGen: ") && !pgs.startsWith("CMS Perm : "))
            {
                throw new Exception("expecting \"PSPermGen:|CMS Perm : ...\" and got \"" + pgs + "\"");
            }

            int i = pgs.indexOf(": ");

            pgs = pgs.substring(i + ": ".length());
            BeforeAfterMax pg = new BeforeAfterMax(pgs, lineNumber);

            //
            // total time
            //

            String durations = tokens.get(crtTokenIndex);
            i = durations.indexOf("secs");
            if (i != -1)
            {
                durations = durations.substring(0, i);
            }
            durations = durations.trim();

            long duration = Math.round(Float.parseFloat(durations) * 1000);

            FullCollection event = new FullCollection(ts, duration, ng, og, pg, heap, system);
            log.debug(event);
            return event;
        }
        catch(ParserException e)
        {
            // just rethrow
            throw e;
        }
        catch(Exception e)
        {
            throw new ParserException(
                    "invalid/unrecognized Full Collection line: \"" + line + "\"" +
                    (e.getMessage() != null ? " (" + e.getClass().getName() + ": " + e.getMessage() + ") at line " + lineNumber: ""),
                    e, lineNumber);
        }
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



