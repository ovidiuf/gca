package com.novaordis.gc.model.expression;

import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.Value;
import com.novaordis.series.Metric;
import com.novaordis.series.metric.DoubleMetric;
import com.novaordis.series.metric.LongMetric;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class SyntheticValue implements Value
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(SyntheticValue.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private Object value;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    /**
     * @exception IllegalArgumentException on null value.
     */
    public SyntheticValue(Object value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("null value");
        }

        this.value = value;
    }

    // Value implementation ----------------------------------------------------------------------------------------------------------------

    @Override
    public FieldType getType()
    {
        return null;
    }

    @Override
    public Object getValue()
    {
        return value;
    }

    @Override
    public Metric toMetric(Unit unit) throws Exception
    {
        // unit is ignored, we don't have FieldType information to do the conversion

        if (value instanceof Long)
        {
            return new LongMetric((Long)value);
        }
        else if (value instanceof Double)
        {
            return new DoubleMetric((Double)value);
        }
        else
        {
            throw new RuntimeException("NOT YET IMPLEMENTED");
        }
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    public String toString()
    {
        return value == null ? "null" : value.toString();
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



