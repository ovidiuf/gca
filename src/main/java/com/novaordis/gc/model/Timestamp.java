package com.novaordis.gc.model;

import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;

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

    public static final String DATESTAMP_FORMAT_LITERAL = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ";
    public static final SimpleDateFormat DATESTAMP_FORMAT = new SimpleDateFormat(DATESTAMP_FORMAT_LITERAL);

    public static final String OFFSET_FORMAT_LITERAL = "#0.000";
    public static final DecimalFormat OFFSET_FORMAT = new DecimalFormat(OFFSET_FORMAT_LITERAL);

    public static final Pattern OFFSET_PATTERN = Pattern.compile("\\d+\\.\\d\\d\\d: ");
    public static final Pattern DATESTAMP_PATTERN = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d-\\d\\d\\d\\d: ");
    public static final Pattern COMBINED_PATTERN = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d-\\d\\d\\d\\d: \\d+\\.\\d\\d\\d: ");

    // start with the combined pattern, to make sure it is found first
    private static final Pattern[] TIMESTAMP_PATTERNS = new Pattern[]
        { COMBINED_PATTERN, OFFSET_PATTERN,  DATESTAMP_PATTERN };

    // Static ----------------------------------------------------------------------------------------------------------

    public static String longToOffsetLiteral(long offset)
    {
        String s = Long.toString(offset);
        int count = 4 - s.length();
        for(int i = 0; i < count; i ++)
        {
            s = "0" + s;
        }

        //noinspection UnnecessaryLocalVariable
        String result = s.substring(0, s.length() - 3) + "." + s.substring(s.length() - 3);
        return result;
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

    private Long time;
    private Long offset;
    private String literal;
    private String dateStampLiteral;
    private String offsetLiteral;

    // the location in the original string where the time stamp started
    private int startPosition;

    // the location in the original string where the time stamp ended - the index of the last character plus 1;
    // it may or may not be a valid string index - it is not a valid string index if the string ends with the date
    // stamp in question
    private int endPosition;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * For testing only.
     *
     * If you want a Timestamp with the time correctly initialized, use this construct:
     *
     * <code>
     *
     *      Timestamp ts = new Timestamp(...).applyTimeOrigin(...);
     *
     * </code>
     */
    public Timestamp(long offset)
    {
        this.offset = offset;
        this.offsetLiteral = longToOffsetLiteral(offset);
        this.literal = offsetLiteral;
    }

    /**
     * This constructor is used by the static factories. Even if the parameters are supposed to be correct, we
     * perform internal sanity checks.
     *
     * Protected for testing.
     *
     * @param startPosition - must be a valid position (>=0) otherwise the constructor throws
     *                      an IllegalArgumentException
     *
     * @param literal must be in sync with time/offset
     *
     * @exception java.lang.IllegalArgumentException
     */
    Timestamp(String literal, Long time, Long offset, int startPosition, int endPosition)
    {
        this.literal = literal;
        this.time = time;
        this.offset = offset;

        processLiteral(literal, time, offset);

        if (startPosition < 0)
        {
            throw new IllegalArgumentException("invalid start position " + startPosition);
        }

        int span = (time != null && offset != null) ? 3 : 2;

        if (endPosition - startPosition != literal.length() + span)
        {
            // "1.000: "
            throw new IllegalArgumentException(
                "the difference between end position (" + endPosition + ") and start position (" + startPosition +
                    ") should be the length of '" + literal + "' + " + span + " but it isn't");
        }

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
        return offsetLiteral;
    }

    /**
     * @return may return null if the log file does not contain date stamps.
     */
    public String getDateStampLiteral()
    {
        return dateStampLiteral;
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
     * @param timeOrigin the time origin to apply to this time stamp. May be prepared to handle null. Depending
     *        on the state of the timestamp, that may be OK or not. For example, if the timestamp has its time
     *        set, the invocation is ignored. If the timestamp does not have the time set, and it gets null, it will
     *        throw NullPointerException.
     *
     * @return itself, to allow constructs like this:
     *
     * Timestamp ts = new Timestamp(...).applyTimeOrigin(...);
     *
     * @exception java.lang.IllegalStateException if both the time and the offset are null - we cannot calculate the
     *            time.
     * @exception java.lang.NullPointerException if the time is not set, but we get a null time origin.
     *
     *
     */
    public Timestamp applyTimeOrigin(Long timeOrigin)
    {
        if (time != null)
        {
            log.debug("applying time origin " + timeOrigin + " while the time is already set to " + time + ", ignored");
            return this;
        }

        if (offset == null)
        {
            throw new IllegalStateException("null offset, cannot calculate the time");
        }

        time = offset + timeOrigin;
        return this;
    }

    @Override
    public String toString()
    {
        return literal;
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

    private void processLiteral(String literal, Long time, Long offset)
    {
        if (literal == null)
        {
            throw new IllegalArgumentException("null literal");
        }

        if (time != null && offset != null)
        {
            // combined literal
            this.dateStampLiteral = literal.substring(0, literal.indexOf(' '));
            this.offsetLiteral = literal.substring(literal.indexOf(' ') + 1);
        }
        else if (time != null)
        {
            // date stamp literal
            this.dateStampLiteral = literal;

        }
        else if (offset != null)
        {
            // offset literal
            this.offsetLiteral = literal;

        }
        else
        {
            throw new IllegalArgumentException("both offset and time are null");
        }

        // check whether that data stamp literal matches the time

        long tmp;

        if (dateStampLiteral != null)
        {
            try
            {
                tmp = DATESTAMP_FORMAT.parse(dateStampLiteral).getTime();
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("invalid time literal '" + literal + "'", e);
            }

            //noinspection ConstantConditions
            if (tmp != time)
            {
                throw new IllegalArgumentException("date stamp literal '" + dateStampLiteral + "' does not match time " + time);
            }
        }

        // check whether the offset literal matches the offset

        if (offsetLiteral != null)
        {
            try
            {
                tmp = offsetToLong(offsetLiteral, null);
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException("invalid offset literal '" + literal + "'", e);
            }

            //noinspection ConstantConditions
            if (tmp != offset)
            {
                throw new IllegalArgumentException("offset literal '" + offsetLiteral + "' does not match offset " + offset);
            }
        }

    }

    // Inner classes ---------------------------------------------------------------------------------------------------
}



