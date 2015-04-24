package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates the offset information present at the beginning of most lines.
 *
 * The offsetLiteral member preserves it as it appears in the file, the value can ge accessed with getLiteral().
 *
 * It also encapsulates date and location information obtained as result of parsing -XX:+PrintGCDateStamps date stamps:
 *
 * 2014-08-14T01:53:16.892-0700
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Timestamp
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Timestamp.class);

    public static final DecimalFormat LITERAL_FORMAT = new DecimalFormat("0.000");
    public static final SimpleDateFormat EXPLICIT_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");

    //

    public static final String DATESTAMP_FORMAT_LITERAL = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ";
    public static final SimpleDateFormat DATESTAMP_FORMAT = new SimpleDateFormat(DATESTAMP_FORMAT_LITERAL);

    public static final String OFFSET_FORMAT_LITERAL = "#0.000";
    public static final DecimalFormat OFFSET_FORMAT = new DecimalFormat(OFFSET_FORMAT_LITERAL);

    private static final Pattern OFFSET_PATTERN = Pattern.compile("\\d+\\.\\d\\d\\d: ");
    private static final Pattern DATESTAMP_PATTERN = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d-\\d\\d\\d\\d: ");
    private static final Pattern COMBINED_PATTERN = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d-\\d\\d\\d\\d: \\d+\\.\\d\\d\\d: ");

    // start with the combined pattern, to make sure it is found first
    private static final Pattern[] TIMESTAMP_PATTERNS = new Pattern[]
        { COMBINED_PATTERN, OFFSET_PATTERN,  DATESTAMP_PATTERN };

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Returns true is the string is either an offset ("53233.950") or an explicit timestamp format.
     */
    public static boolean isTimestamp(String s)
    {
        if (s == null)
        {
            return false;
        }

        try
        {
            return OFFSET_FORMAT.parse(s) != null || EXPLICIT_TIMESTAMP_FORMAT.parse(s) != null;
        }
        catch(Exception e)
        {
            return false;
        }

    }

    public static long offsetToLong(String offset, Long lineNumber) throws ParserException
    {
        int i = offset.indexOf(".");

        if (i == -1)
        {
            throw new ParserException("invalid timestamp '" + offset + "'", lineNumber);
        }

        String secsString = offset.substring(0, i);
        String msecsString = offset.substring(i + 1);

        try
        {
            long secs = Long.parseLong(secsString);
            int msecs = Integer.parseInt(msecsString);

            //noinspection UnnecessaryLocalVariable
            long result = secs * 1000L + msecs;
            return result;
        }
        catch(Exception e)
        {
            throw new ParserException(
                "offset \"" + offset + "\" cannot be parsed using format " + OFFSET_FORMAT_LITERAL, lineNumber);

        }
    }

    public static long dateStampToTime(String dateStamp, Long lineNumber) throws ParserException
    {
        try
        {
            //noinspection UnnecessaryLocalVariable
            long value = DATESTAMP_FORMAT.parse(dateStamp).getTime();
            return value;
        }
        catch(Exception e)
        {
            throw new ParserException(
                "date stamp \"" + dateStamp + "\" cannot be parsed using format " + DATESTAMP_FORMAT, lineNumber);

        }
    }


    /**
     * Finds the first occurrence of a timestamp in line. The timestamp can be:
     *
     * 1. the offset timestamp pattern "27036.837:"
     * 2. the "PrintGCDateStamps" timestamp pattern "2014-08-14T01:12:28.621-0700:"
     * 3. the combined "PrintGCDateStamps"/offset timestamp pattern "2014-08-14T01:12:28.621-0700: 27036.837:"
     *
     * Note that a valid timestamp occurs only at the beginning of the line or it is preceded by one of the following:
     * ']'
     *
     * @return a timestamp instance or null no time stamp is found.
     *
     * @exception java.lang.IllegalArgumentException on null string.
     *
     * @exception java.lang.IllegalStateException if the found date stamp cannot be parsed with
     *            "yyyy-MM-dd'T'HH:mm:ss.SSS"
     */
    public static Timestamp find(String s, int index, long lineNumber) throws ParserException
    {
        if (s == null)
        {
            throw new IllegalArgumentException("null argument");
        }

        Character precedingChar = null;
        if (index > 0)
        {
            precedingChar = s.charAt(index - 1);
        }

        String original = s;
        s = s.substring(index);

        int restartFrom = -1;
        Timestamp result = null;

        for(Pattern timestampPattern : TIMESTAMP_PATTERNS)
        {
            Matcher m = timestampPattern.matcher(s);

            if (m.find())
            {

                int start = m.start();
                int end = m.end(); // the end index falls after the ": "

                if (start > 0)
                {
                    precedingChar = s.charAt(start - 1);
                }

                if (precedingChar != null && precedingChar != ']')
                {
                    // a pattern was found but it does not qualify, so restart the search from this position
                    restartFrom = end;
                    break;
                }

                String literal = s.substring(start, end - 2);

                if (OFFSET_PATTERN.equals(timestampPattern))
                {
                    long offset = offsetToLong(literal, lineNumber);
                    result = new Timestamp(literal, null, offset, start + index, end + index);
                }
                else if (DATESTAMP_PATTERN.equals(timestampPattern))
                {
                    long time = dateStampToTime(literal, lineNumber);
                    result = new Timestamp(literal, time, null, start + index, end + index);
                }
                else if (COMBINED_PATTERN.equals(timestampPattern))
                {
                    int colonIndex = literal.lastIndexOf(':');
                    String dateStampLiteral = literal.substring(0, colonIndex);
                    String offsetLiteral = literal.substring(colonIndex + 2);
                    long time = dateStampToTime(dateStampLiteral, lineNumber);
                    long offset = offsetToLong(offsetLiteral, lineNumber);
                    literal = dateStampLiteral + " " + offsetLiteral;
                    result = new Timestamp(literal, time, offset, start + index, end + index);
                }
                else
                {
                    throw new IllegalStateException("we don't know how to handle this pattern " + timestampPattern);
                }

                break;
            }
        }

        if (restartFrom != -1)
        {
            return find(original, restartFrom + index, lineNumber);
        }

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private String literal;
    private String explicitTimestampLiteral;
    private Long time;
    private Long offset;
    private long explicitTimestampTime;
    private boolean suppressTimestampWarning;

    // the location in the original string where the time stamp started
    private int startPosition;

    // the location in the original string where the time stamp ended - the index of the last character plus 1;
    // it may or may not be a valid string index - it is not a valid string index if the string ends with the date
    // stamp in question
    private int endPosition;


    // Constructors ------------------------------------------------------------------------------------------------------------------------

    /**
     * @param rawTimestamp - the raw timestamp can ba a GC file offset in the format "999.999" or an explicit timestamp and an offset,
     *                       separated by column.
     *
     * @param gcLogFile - for logging purposes only, it can safely be null
     */
    public Timestamp(String rawTimestamp, Long timeOrigin, File gcLogFile, boolean suppressTimestampWarning) throws Exception
    {
        this.suppressTimestampWarning = suppressTimestampWarning;
        this.time = timeOrigin == null ?  0L : timeOrigin;

        this.explicitTimestampTime = -1;

        int i = rawTimestamp.indexOf(": ");

        if (i != -1)
        {
            // we got the explicit timestamp and the offset
            this.explicitTimestampLiteral = rawTimestamp.substring(0, i);
            this.literal = rawTimestamp.substring(i + 2);

            // extract explicit timestamp
            try
            {
                this.explicitTimestampTime = EXPLICIT_TIMESTAMP_FORMAT.parse(explicitTimestampLiteral).getTime();
            }
            catch(Exception e)
            {
                // we failed extracting the explicit timestamp, which means we encountered a format never seen before - or the GC file
                // is simply incorrect
                log.warn("unrecognized explicit timestamp \"" + explicitTimestampLiteral + "\", ignoring ...");
            }
        }
        else
        {
            // no explicit timestamp, if we do not have a time origin, fail

            if (timeOrigin == null)
            {
                throw new UserErrorException("no time origin was specified for " +
                        (gcLogFile == null ? "this file" : gcLogFile.toString()) +
                        ", and the file does not contain explicit timestamps, use -o|--time-origin or name the file according to a known pattern.");
            }

            this.literal = rawTimestamp;
        }

        this.offset = offsetToLong(this.literal, null);


        if (explicitTimestampTime != -1)
        {
            // explicit timestamp present in the log file, use this with priority
            this.time = explicitTimestampTime;

            // if we have both the explicit timestamp AND the offset + time origin, make sure they match or warn
            if (timeOrigin != null)
            {
                long diff = Math.abs(explicitTimestampTime - timeOrigin - offset);

                if (diff != 0 && !suppressTimestampWarning)
                {
                    log.warn("the explicit timestamp \"" + explicitTimestampLiteral +
                            "\" does not match the time calculated based on time origin and offset, the difference is " + diff + " ms");
                }
            }
        }
        else
        {
            this.time = this.time + offset;
        }
    }

    public Timestamp(long time, long offset)
    {
        this.offset = offset;
        this.time = time;
        this.literal = LITERAL_FORMAT.format(((double)offset)/1000);
    }

    /**
     * @param literal must be in sync with time/offset
     */
    public Timestamp(String literal, Long time, Long offset, int startPosition, int endPosition)
    {
        if (offset != null)
        {
            if (literal == null)
            {
                throw new IllegalArgumentException("null literal for a " + offset + " offset");
            }
        }

        this.literal = literal;
        this.time = time;
        this.offset = offset;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Returns the timestamp as recorded in the original GC log file (but without the colon and the trailing space).
     * For example, if the timestamp is "1234.343: ", the literal is "1234.343".
     */
    public String getLiteral()
    {
        return literal;
    }

    /**
     * May return null, in cases there's an offset, but not a time origin.
     */
    public Long getTime()
    {
        return time;
    }

    /**
     * @return may return null if the log file does not contain offsets.
     */
    public Long getOffset()
    {
        return offset;
    }

    /**
     * @return may return null if the log file does not contain offsets.
     */
    public String getOffsetLiteral()
    {
        if (offset == null)
        {
            return null;
        }

        int i = literal.indexOf(' ');

        if (i == -1)
        {
            return literal;
        }

        return literal.substring(i + 1);
    }

    public int getStartPosition()
    {
        return startPosition;
    }

    /**
     * The index of the first character *after* the timestamp pattern (the timestamp pattern includes the column and
     * the trailing space)
     */
    public int getEndPosition()
    {
        return endPosition;
    }

    /**
     * Update the time by taking into account the time origin. This is especially useful if we only have the offset.
     *
     * In case the time is already set, the invocation is ignored. TODO: this is unusual, reconsider this
     *
     */
    public void applyTimeOrigin(long timeOrigin)
    {
        if (time != null)
        {
            log.warn("applying time origin " + timeOrigin + " while the time is already set to " + time + ", IGNORED");
            return;
        }

        if (offset == null)
        {
            throw new IllegalStateException("null offset, cannot calculate the time");
        }

        time = offset + timeOrigin;
    }

    @Override
    public String toString()
    {
        return explicitTimestampLiteral != null ? explicitTimestampLiteral : literal;
    }


    /**
     * Returns the explicit timestamp literal, as recorded in the original GC log file, if the explicit timestamp is
     * present, or null otherwise.
     */
    public String getExplicitTimestampLiteral()
    {
        return explicitTimestampLiteral;
    }

    /**
     * @return a positive value if the explicit timestamp has been found in the file and it has a valid value,
     * -1 otherwise.
     */
    public long getExplicitTimestampTime()
    {
        return explicitTimestampTime;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof Timestamp))
        {
            return false;
        }

        if (this.time == null)
        {
            return false;
        }

        Timestamp that = (Timestamp)o;

        return time.equals(that.time);
    }

    @Override
    public int hashCode()
    {
        return 17 + 5 * (int)(time == null ? 0L : time);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setOffset(long offset)
    {
        this.offset = offset;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



