package com.novaordis.gc.parser;

import com.novaordis.gc.model.Unit;

/**
 * Wrapper around constructs similar to "1696127K(4194304K)".
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 *  Copyright 2013 Nova Ordis LLC
 */
public class CurrentMax
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Unit unit;

    private long current;
    private long max;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public CurrentMax(String s, long lineNumber) throws ParserException
    {
        int i = s.indexOf("(");
        int j = s.indexOf(")");

        if (i == -1 || j == -1 )
        {
            throw new ParserException("\"" + s + "\" not in current(max) format", lineNumber);
        }

        String currents = s.substring(0, i);

        unit = Unit.valueOf(currents.substring(currents.length() - 1));
        currents = currents.substring(0, currents.length() - 1);
        current = Long.parseLong(currents);

        String maxs = s.substring(i + 1, j);
        Unit unit2 = Unit.valueOf(maxs.substring(maxs.length() - 1));

        if (!unit.equals(unit2))
        {
            throw new ParserException("different units: " + unit + ", " + unit2, lineNumber);
        }

        maxs = maxs.substring(0, maxs.length() - 1);
        max = Long.parseLong(maxs);
    }

    public CurrentMax(long current, long max, Unit unit)
    {
        this.current = current;
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

    public long getCurrent()
    {
        return current;
    }

    public long getMax()
    {
        return max;
    }

    @Override
    public String toString()
    {
        return "" + current + unit + "(" + max + unit + ")";
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



