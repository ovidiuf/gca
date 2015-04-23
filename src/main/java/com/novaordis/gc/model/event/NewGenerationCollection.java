package com.novaordis.gc.model.event;

import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.Field;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Util;
import com.novaordis.gc.parser.BeforeAfterMax;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class NewGenerationCollection extends GCEventBase
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public NewGenerationCollection(Timestamp ts, long duration, BeforeAfterMax ng, BeforeAfterMax heap)
    {
        this(ts, duration, ng, heap, null); // no notes
    }

    public NewGenerationCollection(Timestamp ts, long duration, BeforeAfterMax ng, BeforeAfterMax heap, String notes)
    {
        this(ts, duration, ng, heap, notes, null); // no event on the same line
    }

    public NewGenerationCollection(Timestamp ts, long duration, BeforeAfterMax ng, BeforeAfterMax heap,
                                   String notes, String nextEventOnTheSameLine)
    {
        super(ts, duration, notes, nextEventOnTheSameLine);

        setCollectionType(CollectionType.NEW_GENERATION_COLLECTION);

        if (ng != null)
        {
            setField(FieldType.NG_BEFORE, new Field(FieldType.NG_BEFORE, Util.convertToBytes(ng.getUnit(), ng.getBefore())));
            setField(FieldType.NG_AFTER, new Field(FieldType.NG_AFTER, Util.convertToBytes(ng.getUnit(), ng.getAfter())));
            setField(FieldType.NG_CAPACITY, new Field(FieldType.NG_CAPACITY, Util.convertToBytes(ng.getUnit(), ng.getMax())));
        }

        if (heap != null)
        {
            setField(FieldType.HEAP_BEFORE, new Field(FieldType.HEAP_BEFORE, Util.convertToBytes(heap.getUnit(), heap.getBefore())));
            setField(FieldType.HEAP_AFTER, new Field(FieldType.HEAP_AFTER, Util.convertToBytes(heap.getUnit(), heap.getAfter())));
            setField(FieldType.HEAP_CAPACITY, new Field(FieldType.HEAP_CAPACITY, Util.convertToBytes(heap.getUnit(), heap.getMax())));
        }
    }

    // GCEvent implementation --------------------------------------------------------------------------------------------------------------

    @Override
    public CollectionType getCollectionType()
    {
        return CollectionType.NEW_GENERATION_COLLECTION;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "NG Collection " + Configuration.TIMESTAMP_DISPLAY_FORMAT.format(getTime()) + " (" + getDuration() + " ms)";
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



