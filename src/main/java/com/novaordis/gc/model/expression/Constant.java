package com.novaordis.gc.model.expression;

import com.novaordis.gc.model.FieldCategory;
import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.series.Header;
import com.novaordis.series.metric.DoubleHeader;
import com.novaordis.series.metric.LongHeader;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Constant implements Expression
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Value value;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public Constant(Object o)
    {
        this.value = new SyntheticValue(o);
    }

    // Expression implementation -----------------------------------------------------------------------------------------------------------

    @Override
    public Header toHeader(Unit targetUnit) throws Exception
    {
        Object o = getValue();

        if (o instanceof Long)
        {
            return new LongHeader(o.toString());
        }
        else if (o instanceof Double)
        {
            return new DoubleHeader(o.toString());
        }
        else
        {
            throw new Exception("NOT YET IMPLEMENTED");
        }
    }

    @Override
    public FieldCategory getCategory()
    {
        return null;
    }

    @Override
    public Value evaluate(GCEvent e) throws Exception
    {
        return value;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    public Object getValue()
    {
        return value == null ? null : value.getValue();
    }

    @Override
    public String toString()
    {
        return value == null ? "null" : value.toString();
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



