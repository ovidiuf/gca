package com.novaordis.gc.mock;

import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class MockReader extends Reader
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(MockReader.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private volatile boolean closed;
    private Reader delegate;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public MockReader(String content)
    {
        delegate = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    public boolean isClosed()
    {
        return closed;
    }

    // Reader implementation ---------------------------------------------------------------------------------------------------------------

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        return delegate.read(cbuf, off, len);
    }

    @Override
    public void close() throws IOException
    {
        closed = true;
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



