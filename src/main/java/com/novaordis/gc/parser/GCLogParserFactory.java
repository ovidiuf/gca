package com.novaordis.gc.parser;

import com.novaordis.gc.parser.linear.LinearScanParser;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class GCLogParserFactory
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(GCLogParserFactory.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    public static GCLogParser getParser(File f, boolean suppressTimestampWarning) throws Exception
    {
        FileReader r = new FileReader(f);
        return getParser(r, f, suppressTimestampWarning);
    }

    public static GCLogParser getParser(Reader r, File f, boolean suppressTimestampWarning) throws Exception
    {
        LinearScanParser p = new LinearScanParser(r, f, suppressTimestampWarning);
        p.installDefaultPipeline();
        p.addSecondLinePattern(Pattern.compile("\\s*\\(concurrent mode failure\\).*"));
        return p;
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    private GCLogParserFactory()
    {
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



