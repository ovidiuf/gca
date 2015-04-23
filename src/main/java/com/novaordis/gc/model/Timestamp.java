package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;
import org.apache.log4j.Logger;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private static final Pattern DATESTAMP_PATTERN = Pattern.compile(
        "\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d-\\d\\d\\d\\d");
    private static final String DATESTAMP_FORMAT_LITERAL = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final SimpleDateFormat DATESTAMP_FORMAT = new SimpleDateFormat(DATESTAMP_FORMAT_LITERAL);

    // note that offset values are detected only if they end in ':', ' ':
    private static final Pattern OFFSET_PATTERN = Pattern.compile("\\d+\\.\\d\\d\\d[: ]");
    public static final String OFFSET_FORMAT_LITERAL = "#0.000";
    public static final DecimalFormat OFFSET_FORMAT = new DecimalFormat(OFFSET_FORMAT_LITERAL);

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

    public static long offsetToMilliseconds(String offset) throws Exception
    {
        long result;

        int i = offset.indexOf(".");

        if (i == -1)
        {
            throw new Exception("invalid timestamp '" + offset + "'");
        }

        String secsString = offset.substring(0, i);
        long secs = Long.parseLong(secsString);
        String msecsString = offset.substring(i + 1);
        int msecs = Integer.parseInt(msecsString);

        result = secs * 1000L + msecs;

        return result;
    }

    /**
     * Finds the first occurrence of the "PrintGCDateStamps" timestamp pattern or the offset pattern starting at the
     * given index. Returns the corresponding timestamp instance or null no time stamp is found.
     *
     * @exception java.lang.IllegalArgumentException on null string.
     * @exception java.lang.IllegalStateException if the found date stamp cannot be parsed with
     *            "yyyy-MM-dd'T'HH:mm:ss.SSS"
     *
     */
    public static Timestamp find(String s, int index)
    {
        if (s == null)
        {
            throw new IllegalArgumentException("null argument");
        }

        s = s.substring(index);

        Matcher dateMatcher = DATESTAMP_PATTERN.matcher(s);

        if (dateMatcher.find(0))
        {
            int start = dateMatcher.start();
            int end = dateMatcher.end();
            Date d;

            try
            {
                s = s.substring(start, end);
                d = DATESTAMP_FORMAT.parse(s);
            }
            catch(ParseException e)
            {
                throw new IllegalStateException(
                    "date stamp \"" + s + "\" cannot be parsed using format " + DATESTAMP_FORMAT_LITERAL);
            }

            return new Timestamp(s, d.getTime(), start + index, end + index);
        }

        Matcher offsetMatcher = OFFSET_PATTERN.matcher(s);

        if (offsetMatcher.find(0))
        {
            int start = offsetMatcher.start();
            int end = offsetMatcher.end() - 1; // the last character is a non-digit
            long v;

            try
            {
                s = s.substring(start, end);
                int i = s.indexOf(".");

                v = Integer.parseInt(s.substring(i + 1));
                v += Long.parseLong(s.substring(0, i)) * 1000L;
            }
            catch(Exception e)
            {
                throw new IllegalStateException(
                    "offset \"" + s + "\" cannot be parsed using format " + OFFSET_FORMAT_LITERAL);
            }

            Timestamp t = new Timestamp(s, v, start + index, end + index);
            t.setOffset(v);
            return t;
        }

        return null;
    }



    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private String literal;
    private String explicitTimestampLiteral;
    private long offset;
    private long explicitTimestampTime;
    private long time;
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

        this.offset = offsetToMilliseconds(this.literal);


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

    private Timestamp(String literal, long time, int startPosition, int endPosition)
    {
        this.literal = literal;
        this.time = time;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    /**
     * Returns the timestamp as recorded in the original GC log file.
     */
    public String getLiteral()
    {
        return literal;
    }

    /**
     * Returns the explicit timestamp literal, as recorded in the original GC log file, if the explicit timestamp is present, or null
     * otherwise.
     */
    public String getExplicitTimestampLiteral()
    {
        return explicitTimestampLiteral;
    }

    /**
     * @return a positive value if the explicit timestamp has been found in the file and it has a valid value, -1 otherwise.
     */
    public long getExplicitTimestampTime()
    {
        return explicitTimestampTime;
    }

    public long getTime()
    {
        return time;
    }

    public long getOffset()
    {
        return offset;
    }

    public int getStartPosition()
    {
        return startPosition;
    }

    public int getEndPosition()
    {
        return endPosition;
    }

    @Override
    public String toString()
    {
        return explicitTimestampLiteral != null ? explicitTimestampLiteral : literal;
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

        Timestamp that = (Timestamp)o;

        return time == that.time;
    }

    @Override
    public int hashCode()
    {
        return 17 + 5 * (int)time;
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



