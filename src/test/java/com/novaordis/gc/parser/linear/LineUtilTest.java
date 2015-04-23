package com.novaordis.gc.parser.linear;

import com.novaordis.gc.parser.ParserException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class LineUtilTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(LineUtilTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    //
    // Synthetic data ----------------------------------------------------------------------------------------------------------------------
    //

    @Test
    public void tesToSquareBracketTokens_StartsWithUnguardedString() throws Exception
    {
        String s = " blah blah [something]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(2, tokens.size());

        assertEquals("blah blah", tokens.get(0));
        assertEquals("something", tokens.get(1));
    }

    @Test
    public void tesToSquareBracketTokens_GuardedNoSpace() throws Exception
    {
        String s = "[something1] [something2]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(2, tokens.size());

        assertEquals("something1", tokens.get(0));
        assertEquals("something2", tokens.get(1));
    }

    @Test
    public void tesToSquareBracketTokens_GuardedSpace() throws Exception
    {
        String s = " [something1] [something2]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(2, tokens.size());

        assertEquals("something1", tokens.get(0));
        assertEquals("something2", tokens.get(1));
    }

    @Test
    public void testOneTopLevel() throws Exception
    {
        String s = "[something]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(1, tokens.size());

        assertEquals("something", tokens.get(0));
    }

    @Test
    public void testOneTopLevelAndEnclosed() throws Exception
    {
        String s = "[A [B]]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(1, tokens.size());

        assertEquals("A [B]", tokens.get(0));
    }

    @Test
    public void tesToSquareBracketTokens_OneLevelOfNesting() throws Exception
    {
        String s = " [A] [[a] [b] [c] ] [D] [ e [f] [g[h]] ]  end";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(5, tokens.size());

        assertEquals("A", tokens.get(0));
        assertEquals("[a] [b] [c] ", tokens.get(1));
        assertEquals("D", tokens.get(2));
        assertEquals(" e [f] [g[h]] ", tokens.get(3));
        assertEquals("end", tokens.get(4));
    }

    @Test
    public void tesToSquareBracketTokens_SeveralLevelsOfNesting() throws Exception
    {
        String s = "[[[[A]]]] [[[B]]]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(2, tokens.size());

        assertEquals("[[[A]]]", tokens.get(0));
        assertEquals("[[B]]", tokens.get(1));
    }

    @Test
    public void tesToSquareBracketTokens_UnbalancedBrackets() throws Exception
    {
        String s = "[a]]";

        try
        {
            LineUtil.toSquareBracketTokens(s, 77);
            fail("should fail, unbalanced brackets");
        }
        catch(ParserException e)
        {
            assertEquals(77, e.getLineNumber());
            log.info(e.getMessage());
        }
    }

    @Test
    public void tesToSquareBracketTokens_CommaOutsideBrackets() throws Exception
    {
        String s = " [A], b, c";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(3, tokens.size());

        assertEquals("A", tokens.get(0));
        assertEquals("b", tokens.get(1));
        assertEquals("c", tokens.get(2));
    }

    @Test
    public void tesToSquareBracketTokens_CommaInsideBrackets() throws Exception
    {
        String s = " [A, B] c";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(2, tokens.size());

        assertEquals("A, B", tokens.get(0));
        assertEquals("c", tokens.get(1));
    }

    @Test
    public void testImpliedClosingBracket() throws Exception
    {
        String s = "[A [B]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        // the closing bracket is implied - this is what happens when GC does not properly close a line and continues
        // with the next event on the same line
        assertEquals(1, tokens.size());

        String s2 = tokens.get(0);

        assertEquals(s2, "A [B]");
    }

    //
    // Full Samples ------------------------------------------------------------------------------------------------------------------------
    //

    @Test
    public void tesToSquareBracketTokens_Sample1() throws Exception
    {
        String s = "[Full GC (System) [PSYoungGen: 32861K->0K(1722048K)] [PSOldGen: 1663616K->1696127K(4194304K)] 1696478K->1696127K(5916352K) [PSPermGen: 292408K->292408K(292416K)], 2.4516460 secs] [Times: user=2.54 sys=0.00, real=2.45 secs]";

        List<String> tokens = LineUtil.toSquareBracketTokens(s, -1);

        assertEquals(2, tokens.size());

        String s2 = tokens.get(0);

        assertEquals(s2, "Full GC (System) [PSYoungGen: 32861K->0K(1722048K)] [PSOldGen: 1663616K->1696127K(4194304K)] 1696478K->1696127K(5916352K) [PSPermGen: 292408K->292408K(292416K)], 2.4516460 secs");

        String s3 = tokens.get(1);

        assertEquals("Times: user=2.54 sys=0.00, real=2.45 secs", s3);

        List<String> tokens2 = LineUtil.toSquareBracketTokens(s2, -1);

        assertEquals(6, tokens2.size());
        assertEquals("Full GC (System)", tokens2.get(0));
        assertEquals("PSYoungGen: 32861K->0K(1722048K)", tokens2.get(1));
        assertEquals("PSOldGen: 1663616K->1696127K(4194304K)", tokens2.get(2));
        assertEquals("1696478K->1696127K(5916352K)", tokens2.get(3));
        assertEquals("PSPermGen: 292408K->292408K(292416K)", tokens2.get(4));
        assertEquals("2.4516460 secs", tokens2.get(5));
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



