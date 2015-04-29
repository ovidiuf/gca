package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.expression.Expression;
import com.novaordis.series.Header;
import com.novaordis.series.Metric;
import com.novaordis.series.metric.DoubleHeader;
import com.novaordis.series.metric.DoubleMetric;
import com.novaordis.series.metric.LongHeader;
import com.novaordis.series.metric.LongMetric;
import com.novaordis.series.metric.StringHeader;
import com.novaordis.series.metric.StringMetric;

import java.util.Date;

/**
 * Data types extracted from a GC log.
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public enum FieldType implements Expression
{
    SYNTHETIC_DOUBLE("Synthetic Double", "synthetic_double", null, Double.class, null),

    TIME("Time", "time", null, Date.class, null),

    // offset (in seconds) from the time origin; maintained as String, in the original format present in the GC file,
    // for a more convenient string search
    OFFSET("GC File Offset", "offset", null, String.class, null),

    // CollectionType
    COLLECTION_TYPE("Collection Type", "collection-type", null, CollectionType.class, null),

    // event duration (in ms)
    DURATION("Duration", "duration", Unit.ms, Long.class, null),

    // long value (in bytes)
    NG_BEFORE("New Generation Initial Occupancy", "ng-before", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    NG_AFTER("New Generation Final Occupancy", "ng-after", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    NG("New Generation Current Occupancy", "ng", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    NG_CAPACITY("New Generation Capacity", "ng-capacity", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    OG_BEFORE("Old Generation Initial Occupancy", "og-before", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    OG_AFTER("Old Generation Final Occupancy", "og-after", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    OG("Old Generation Current Occupancy", "og", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    OG_CAPACITY("Old Generation Capacity", "og-capacity", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    PG_BEFORE("Permanent Generation Initial Occupancy", "pg-before", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    PG_AFTER("Permanent Generation Final Occupancy", "pg-after", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    PG("Permanent Generation Current Occupancy", "pg", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    PG_CAPACITY("Permanent Generation Capacity", "pg-capacity", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    HEAP_BEFORE("Heap (NG and OG) Initial Occupancy", "heap-before", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    HEAP_AFTER("Heap (NG and OG) Final Occupancy", "heap-after", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    HEAP("Heap (NG and OG) Current Occupancy", "heap", Unit.b, Long.class, FieldCategory.MEMORY),

    // long value (in bytes)
    HEAP_CAPACITY("Heap (NG and OG) Capacity", "heap-capacity", Unit.b, Long.class, FieldCategory.MEMORY),

    // special event notes, such as "promotion failed" etc.
    NOTES("Notes", "notes", null, String.class, null),

    // String. There are situations when the leading timestamp and the embedded timestamp do not match
    // (example: "598272.974: [GC 598272.975: [ParNew: ..."), so we use the leading timestamp as reference as part of
    // the timestamp state but we keep the embedded offset literal around, just in case.
    //
    EMBEDDED_TIMESTAMP_LITERAL("", "", null, String.class, null);

    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    /**
     * The human readable label - this is how this field will be represented externally, in CSV files for example.
     */
    public String label;

    /**
     * The label used by the CLI to identify this field.
     */
    public String commandLineLabel;

    public Class type;

    /**
     * One of FieldCategory.MEMORY, ... etc.
     */
    public FieldCategory category;

    public Unit defaultUnit;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    FieldType(String label, String commandLineLabel, Unit defaultUnit, Class type, FieldCategory category)
    {
        this.label = label;
        this.commandLineLabel = commandLineLabel;
        this.defaultUnit = defaultUnit;
        this.type = type;
        this.category = category;
    }

    // Expression implementation -----------------------------------------------------------------------------------------------------------

    @Override
    public Header toHeader(Unit targetUnit) throws Exception
    {
        if (targetUnit != null)
        {
            // for the time being, we can only convert MEMORY, anything else will throw an exception
            if (!FieldCategory.MEMORY.equals(category))
            {
                throw new IllegalArgumentException("cannot convert " + this + " values to " + targetUnit);
            }

            if (!Long.class.equals(type))
            {
                throw new IllegalStateException(this + " belongs to the MEMORY category hence should be a Long");
            }

            if (FieldCategory.MEMORY.equals(category) &&
                    (!Unit.b.equals(targetUnit) && !Unit.K.equals(targetUnit) && !Unit.M.equals(targetUnit) && !Unit.G.equals(targetUnit)))
            {
                throw new UserErrorException("incompatible unit " + targetUnit);
            }

            return
                    Unit.b.equals(targetUnit) ?
                            new LongHeader(label, targetUnit.label) :
                            new DoubleHeader(label, targetUnit.label, Configuration.MEMORY_FORMAT); // fractional memory should be displayed
                                                                                                    // using the preferred memory format
        }


        if (Date.class.equals(type) || String.class.equals(type) || CollectionType.class.equals(type))
        {
            return new StringHeader(label, defaultUnit == null ? null : defaultUnit.label);
        }

        if (Long.class.equals(type))
        {
            return new LongHeader(label, defaultUnit == null ? null : defaultUnit.label);
        }

        if (Double.class.equals(type))
        {
            return new DoubleHeader(label, defaultUnit == null ? null : defaultUnit.label);
        }

        throw new Exception("NOT YET IMPLEMENTED");
    }

    /**
     * @see Expression#evaluate(com.novaordis.gc.model.event.GCEvent)
     */
    @Override
    public Value evaluate(GCEvent e) throws Exception
    {
        if (e == null)
        {
            return null;
        }

        return e.get(this);
    }

    @Override
    public FieldCategory getCategory()
    {
        return category;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    /**
     * If the FieldType's category is FieldCategory.MEMORY and we convert (targetUnit not null) we will get DoubleMetric instead of
     * LongMetric.
     *
     * @param value - must be an instance of the Class type, otherwise InvalidArgumentException is thrown.
     *
     * @param targetUnit - the unit we want the result metric to be expressed in. If null, no conversion will be performed. If the
     *        unit cannot be applied to the type, IllegalArgumentException will be thrown.
     *
     * @throws IllegalArgumentException on null or type-mismatched values, or the metric cannot be applied to this type, or a unit that
     *         cannot be applied to this type.
     */
    public Metric toMetric(Object value, Unit targetUnit)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(this + " cannot to convert " + value + " to a metric");
        }

        if (!type.equals(value.getClass()))
        {
            throw new IllegalArgumentException(this + " cannot to convert " + value + "(" + value.getClass() + ") to a metric");
        }

        // for the time being, we can only convert MEMORY, anything else will throw an exception
        if (targetUnit != null && !FieldCategory.MEMORY.equals(category))
        {
            throw new IllegalArgumentException("cannot convert " + this + " values to " + targetUnit);
        }

        if (targetUnit != null && FieldCategory.MEMORY.equals(category) &&
                (!Unit.b.equals(targetUnit) && !Unit.K.equals(targetUnit) && !Unit.M.equals(targetUnit) && !Unit.G.equals(targetUnit)))
        {
            throw new IllegalArgumentException("incompatible unit " + targetUnit);
        }

        if (String.class.equals(type))
        {
            return new StringMetric((String)value);
        }
        else if (CollectionType.class.equals(type))
        {
            return new StringMetric(((CollectionType)value).label);
        }
        else if (Long.class.equals(type))
        {
            Metric result;

            //noinspection IfMayBeConditional
            if (targetUnit == null || Unit.b.equals(targetUnit))
            {
                // no conversion
                result = new LongMetric((Long)value);
            }
            else
            {
                // conversion - we already insured we're FieldCategory.MEMORY
                result = new DoubleMetric(((Long)value).doubleValue() / targetUnit.multiplier);
            }

            return result;
        }
        else if (Double.class.equals(type))
        {
            return new DoubleMetric((Double)value);
        }
        else
        {
            throw new RuntimeException("NOT YET IMPLEMENTED: " + type);
        }
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------

}
