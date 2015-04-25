package com.novaordis.gc.parser.linear;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.event.NewGenerationCollection;
import com.novaordis.gc.parser.BeforeAfterMax;
import com.novaordis.gc.parser.Duration;
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
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(NewGenerationCollectionParser.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // GCEventParser ---------------------------------------------------------------------------------------------------

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
     * @see com.novaordis.gc.parser.GCEventParser#parse(com.novaordis.gc.model.Timestamp, String, long, GCEvent, File)
     */
    @Override
    public GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent current) throws ParserException
    {
        // all processing is done in a try/catch block, so we can cleanly handle parsing error (unrecognized lines,
        // for example)

        try
        {
            if (!line.startsWith("[GC"))
            {
                return null;
            }

            List<String> tokens = LineUtil.toSquareBracketTokens(line, lineNumber);

            // only use the first token, go down one level

            String gc = tokens.get(0);
            tokens = LineUtil.toSquareBracketTokens(gc, lineNumber);

            String hs = tokens.get(0);

            // sanity check: verify that line start offset precedes embedded offset
            Timestamp embeddedOffset;
            if (hs.startsWith("GC ") &&
                ((embeddedOffset = Timestamp.find(hs.substring("GC ".length()) + " ", 0, lineNumber)) != null))
            {
                if (ts.getOffset() > embeddedOffset.getOffset())
                {
                    throw new ParserException(
                        "embedded offset " + hs + " precedes line start offset " + ts.getLiteral(), lineNumber);
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

                // introduce duration back into the token list so we can process it with the standard code
                String duration = ngs.substring(ngs.indexOf(',') + 1).trim();
                // fill for heap
                tokens.add(null);
                // then add duration
                tokens.add(duration);

                ngs = ngs.replaceFirst(",.*", "");
            }
            else if (line.contains("YG occupancy"))
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

                if (heaps != null)
                {
                    heap = new BeforeAfterMax(heaps, lineNumber);
                }
            }

            if (tokens.size() > 3)
            {
                //
                // duration
                //

                String durationString = tokens.get(3);
                duration = Duration.toLongMilliseconds(durationString, lineNumber);
            }

            NewGenerationCollection event = new NewGenerationCollection(ts, duration, ng, heap, notes);

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

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



