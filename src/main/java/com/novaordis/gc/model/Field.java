package com.novaordis.gc.model;

import com.novaordis.series.Metric;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Field implements Value
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private FieldType type;
    private Object value;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    /**
     * @exception IllegalArgumentException on null type or value
     */
    public Field(FieldType type, Object value)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("null type");
        }

        if (value == null)
        {
            throw new IllegalArgumentException("null value");
        }

        this.type = type;
        this.value = value;
    }

    // Value implementation ----------------------------------------------------------------------------------------------------------------

    @Override
    public FieldType getType()
    {
        return type;
    }

    @Override
    public Object getValue()
    {
        return value;
    }

    /**
     * @see FieldType#toMetric(Object, Unit)
     */
    @Override
    public Metric toMetric(Unit unit)
    {
        return type.toMetric(value, unit);
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return value + "[" + type + "]";
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



