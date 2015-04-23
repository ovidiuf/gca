package com.novaordis.gc.parser.linear;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.event.NewGenerationCollection;
import com.novaordis.gc.parser.BeforeAfterMax;
import com.novaordis.gc.parser.GCEventParserBase;
import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * New Generation collection event parser.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class NewGenerationCollectionParser extends GCEventParserBase
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(NewGenerationCollectionParser.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // GCEventParser -----------------------------------------------------------------------------------------------------------------------

    /**
     * Example of recognized line:
     *
     * Parallel collector:
     *
     * [GC [PSYoungGen: 1868896K->53713K(1973376K)] 2794287K->979111K(6167680K), 0.0251580 secs] [Times: user=0.14 sys=0.00, real=0.02 secs]
     * [GC-- [PSYoungGen: 1295427K->1295427K(1398144K)] 4370140K->5405298K(5592448K), 0.1567310 secs] [Times: user=1.15 sys=0.01, real=0.16 secs]
     *
     * CMS collector:
     *
     * [GC 1.985: [ParNew: 136320K->6357K(153344K), 0.0083580 secs] 136320K->6357K(4177280K), 0.0085020 secs] [Times: user=0.05 sys=0.01, real=0.01 secs]
     * [GC2014-08-13T21:55:10.974-0700: 15199.189: [ParNew: 430920K->12879K(471872K), 0.0136920 secs] 750819K->333153K(4141888K) icms_dc=0 , 0.0137990 secs] [Times: user=0.07 sys=0.00, real=0.01 secs]
     *
     * Note that at the time of writing, I did not know what the difference between "GC" and "GC--" is, and I am
     * considering them equivalent.
     *
     *
     * @param gcFile - for logging purposes only, can be null.
     *
     * @see com.novaordis.gc.parser.GCEventParser#parse(com.novaordis.gc.model.Timestamp, String, long, GCEvent, File)
     */
    @Override
    public GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent current, File gcFile) throws ParserException
    {
        // all processing is done in a try/catch block, so we can cleanly handle parsing error (unrecognized lines,
        // for example)

        try
        {
            if (!line.startsWith("[GC"))
            {
                return null;
            }

            String nextEventOnTheSameLine = null;

            // look for multiple events on the same line, start the search after the next "[". We start from
            // there to avoid situations where the timestamp is glued to "GC", like here:
            //   [GC2014-08-14T01:53:16.892-0700: 29485.108: [ParNew: ...
            //   [GC 53233.950: [ParNew: ...
            //   [GC2014-08-14T08:38:27.033-0700: 53795.248: [ParNew: ...

            int i = line.indexOf('[', 1);
            i = i == -1 ? "[GC".length() + 2 : i;
            Timestamp nextTs = Timestamp.find(line, i);

            if (nextTs != null)
            {
                // we do have multiple events
                int nextEventIndex = nextTs.getStartPosition();
                nextEventOnTheSameLine = line.substring(nextEventIndex);
                line = line.substring(0, nextEventIndex);
            }

            List<String> tokens = LineUtil.toSquareBracketTokens(line, lineNumber);

            // only use the first token, go down one level

            String gc = tokens.get(0);
            tokens = LineUtil.toSquareBracketTokens(gc, lineNumber);

            String hs = tokens.get(0);

            if (hs.matches("GC +\\d+\\.\\d+:"))
            {
                // for CMS collector lines, verify that line start offset precedes embedded offset
                hs = hs.replaceFirst("GC +", "");
                hs = hs.substring(0, hs.length() - 1);
                Timestamp embedded = new Timestamp(hs, 0L, gcFile, false);

                if (ts.getOffset() > embedded.getOffset())
                {
                    throw new Exception("embedded offset " + hs + " precedes line start offset " + ts.getLiteral());
                }
            }
            else if ((nextTs = Timestamp.find(hs, 0)) != null)
            {
                // for CMS new generation collections where PrintGCDateStamps was specified verify that line start
                // offset precedes embedded offset
                hs = hs.substring(nextTs.getEndPosition() + 1).trim();
                hs = hs.substring(0, hs.length() - 1);
                Timestamp embedded = new Timestamp(hs, 0L, gcFile, false);

                if (ts.getOffset() > embedded.getOffset())
                {
                    throw new Exception("embedded offset " + hs + " precedes line start offset " + ts.getLiteral());
                }
            }

            //
            // PSYoungGen: 1868896K->53713K(1973376K)
            //   or
            // ParNew: 136320K->6357K(153344K), 0.0083580 secs
            //

            String notes = null;
            String ngs = tokens.get(1);

            // sanity check - if we don't recognize the format, discard the line

            if (ngs.startsWith("PSYoungGen: "))
            {
                // a Parallel collector format
                ngs = ngs.substring("PSYoungGen: ".length());
            }
            else if (ngs.startsWith("ParNew"))
            {
                if (ngs.startsWith("ParNew: "))
                {
                    // CMS
                    ngs = ngs.substring("ParNew: ".length());
                }
                else if (ngs.startsWith("ParNew (promotion failed): "))
                {
                    // record a new generation collection event that does not collect anything and mark it with
                    // a "promotion failed" badge
                    ngs = ngs.substring("ParNew (promotion failed): ".length());
                    notes = "promotion failed";
                }
                else
                {
                    throw new Exception("unknown CMS new generation line: \"" + line + "\"");
                }

                // TODO -  we're discarding a duration here, not sure what that represents
                ngs = ngs.replaceFirst(",.*", "");
            }
            else if (line.contains("CMS") || line.contains("YG occupancy"))
            {
                // TODO this discards a lot of cases - address
                return null;
            }
            else
            {
                throw new Exception("expecting \"PSYoungGen:|ParNew ...\" and got \"" + ngs + "\"");
            }


            BeforeAfterMax ng = new BeforeAfterMax(ngs, lineNumber);
            BeforeAfterMax heap = null;
            long duration = 0L;

            //
            // verify we have "entire heap" ("1696478K->1696127K(5916352K)") and duration information and if we do,
            // record that as well
            //

            if (tokens.size() > 2)
            {
                String heaps = tokens.get(2);
                heap = new BeforeAfterMax(heaps, lineNumber);

                //
                // duration
                //

                String durations = tokens.get(3);
                int j = durations.indexOf(",");
                if (j != -1)
                {
                    durations = durations.substring(j + 1);
                }
                j = durations.indexOf("secs");
                if (j != -1)
                {
                    durations = durations.substring(0, j);
                }
                durations = durations.trim();

                duration = Math.round(Float.parseFloat(durations) * 1000);
            }

            NewGenerationCollection event =
                new NewGenerationCollection(ts, duration, ng, heap, notes, nextEventOnTheSameLine);

            log.debug(event);
            return event;
        }
        catch(Exception e)
        {
            throw new ParserException(
                    "invalid/unrecognized New Generation Collection line: \"" + line + "\"" +
                            (e.getMessage() != null ? ", " + e.getMessage() : ""), e, lineNumber);
        }
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



