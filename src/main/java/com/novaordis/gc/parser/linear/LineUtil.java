package com.novaordis.gc.parser.linear;

import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class LineUtil
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LineUtil.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Breaks down a square-bracket bounded string into tokens.
     *
     * Segments bounded by two square-bracket segments ( ...] this string [ ... ) or by a square-bracket segment and the
     * string external edges ( "...] this string" are also considered square-bracket segments.
     *
     * For these segments, comma is also considered a separator. Example: TODO
     *
     * The method deals gracefully with the special case of unbalanced brackets due to GC starting a new line in the
     * middle of the current line.
     */
    public static List<String> toSquareBracketTokens(String s, long lineNumber) throws ParserException
    {
        List<String> result = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(s, "[]", true);

        StringBuffer sb = null;
        String crt = null;
        int nesting = 0;

        while (st.hasMoreTokens())
        {
            crt = st.nextToken();

            if ("[".equals(crt))
            {
                nesting++;

                if (nesting > 1)
                {
                    sb.append(crt);
                }
                else
                {
                    sb = new StringBuffer();
                }
            }
            else if ("]".equals(crt))
            {
                nesting --;

                if (nesting < 0)
                {
                    // unbalanced brackets
                    throw new ParserException("unbalanced brackets", lineNumber);
                }
                else if (nesting == 0)
                {
                    // do not break on comma (for the time being at least)
                    result.add(sb.toString());
                    sb = null;
                }
                else
                {
                    sb.append(crt);
                }
            }
            else if (sb == null)
            {
                crt = crt.trim();
                if (crt.length() != 0)
                {
                    // break on comma
                    if (crt.contains(","))
                    {
                        for(StringTokenizer st2 = new StringTokenizer(crt, ","); st2.hasMoreTokens(); )
                        {
                            result.add(st2.nextToken().trim());
                        }
                    }
                    else
                    {
                        result.add(crt);
                    }
                }
            }
            else
            {
                sb.append(crt);
            }
        }

        // unclosed brackets - this is a valid case when GC cuts a line short and continues with the next event on the
        // same line

        if ("]".equals(crt) && sb != null)
        {
            result.add(sb.toString());
        }

        return result;
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    private LineUtil()
    {
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



