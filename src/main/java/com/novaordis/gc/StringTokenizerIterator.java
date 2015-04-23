package com.novaordis.gc;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * StringTokenizer to Iterator adapter.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class StringTokenizerIterator implements Iterator<String>
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private StringTokenizer st = null;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    /**
     * By default it does NOT return the delimiters.
     */
    public StringTokenizerIterator(String s, String separators)
    {
        st = new StringTokenizer(s, separators);
    }

    // Iterator implementation -------------------------------------------------------------------------------------------------------------

    @Override
    public boolean hasNext()
    {
        return st.hasMoreTokens();
    }

    @Override
    public String next()
    {
        return st.nextToken();
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



