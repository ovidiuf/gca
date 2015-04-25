package com.novaordis.gc.parser;

import com.novaordis.gc.parser.linear.LinearScanParser;
import com.novaordis.utilities.Files;
import com.novaordis.utilities.testing.Tests;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class GCLogParserFactoryTest
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    @Test
    public void validateExpectationsOnDefaultParser() throws Exception
    {
        File d = Tests.getScratchDir();
        File f = new File(d, "test");
        Files.write(f, "");

        GCLogParser p = GCLogParserFactory.getParser(f);

        assertTrue(p instanceof LinearScanParser);

        LinearScanParser lsp = (LinearScanParser)p;

        // verify the patterns

        List<Pattern> patterns = lsp.getSecondLinePatterns();
        assertEquals(1, patterns.size());

        assertTrue(lsp.isTheSecondLineOfTheEvent(" (concurrent mode failure): 3667441K->779130K(3670016K), 6.2096720 secs] 4045730K->779130K(4141888K), [CMS Perm : 93567K->92821K(131072K)] icms_dc=100 , 6.6030840 secs] [Times: user=2.31 sys=0.23, real=6.61 secs]"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



