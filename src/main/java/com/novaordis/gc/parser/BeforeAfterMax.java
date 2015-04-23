package com.novaordis.gc.parser;

import com.novaordis.gc.model.Unit;

/**
 * Wrapper around constructs similar to "1663616K->1696127K(4194304K)".
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 *  Copyright 2013 Nova Ordis LLC
 */
public class BeforeAfterMax
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Unit unit;

    private long before;
    private long after;
    private long max;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public BeforeAfterMax(String s, long lineNumber) throws ParserException
    {
        int i = s.indexOf("->");
        int j = s.indexOf("(");
        int k = s.indexOf(")");

        if (i == -1 || j == -1 || k == -1)
        {
            throw new ParserException("\"" + s + "\" not in before->after(max) format", lineNumber);
        }

        String befores = s.substring(0, i);

        unit = Unit.valueOf(befores.substring(befores.length() - 1));
        befores = befores.substring(0, befores.length() - 1);
        before = Long.parseLong(befores);

        String afters = s.substring(i + "->".length(), j);
        Unit unit2 = Unit.valueOf(afters.substring(afters.length() - 1));

        if (!unit.equals(unit2))
        {
            throw new ParserException("different units: " + unit + ", " + unit2, lineNumber);
        }

        afters = afters.substring(0, afters.length() - 1);
        after = Long.parseLong(afters);

        String maxs = s.substring(j + 1, k);
        unit2 = Unit.valueOf(maxs.substring(maxs.length() - 1));

        if (!unit.equals(unit2))
        {
            throw new ParserException("different units: " + unit + ", " + unit2, lineNumber);
        }

        maxs = maxs.substring(0, maxs.length() - 1);
        max = Long.parseLong(maxs);
    }

    public BeforeAfterMax(long before, long after, long max, Unit unit)
    {
        this.before = before;
        this.after = after;
        this.max = max;
        this.unit = unit;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    /**
     * @return usually "K".
     */
    public Unit getUnit()
    {
        return unit;
    }

    public long getBefore()
    {
        return before;
    }

    public long getAfter()
    {
        return after;
    }

    public long getMax()
    {
        return max;
    }

    @Override
    public String toString()
    {
        return before + unit.toString() + "->" + after + unit + "(" + max + unit + ")";
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



