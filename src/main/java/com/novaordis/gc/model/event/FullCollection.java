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
public class FullCollection extends GCEventBase
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    private boolean system;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public FullCollection(Timestamp ts, long duration,
                          BeforeAfterMax ng, BeforeAfterMax og, BeforeAfterMax pg, BeforeAfterMax heap,
                          boolean system)
    {
        super(ts, duration);
        this.system = system;
        setCollectionType(CollectionType.FULL_COLLECTION);

        if (ng != null)
        {
            setField(FieldType.NG_BEFORE, new Field(FieldType.NG_BEFORE, Util.convertToBytes(ng.getUnit(), ng.getBefore())));
            setField(FieldType.NG_AFTER, new Field(FieldType.NG_AFTER, Util.convertToBytes(ng.getUnit(), ng.getAfter())));
            setField(FieldType.NG_CAPACITY, new Field(FieldType.NG_CAPACITY, Util.convertToBytes(ng.getUnit(), ng.getMax())));
        }

        if (og != null)
        {
            setField(FieldType.OG_BEFORE, new Field(FieldType.OG_BEFORE, Util.convertToBytes(og.getUnit(), og.getBefore())));
            setField(FieldType.OG_AFTER, new Field(FieldType.OG_AFTER, Util.convertToBytes(og.getUnit(), og.getAfter())));
            setField(FieldType.OG_CAPACITY, new Field(FieldType.OG_CAPACITY, Util.convertToBytes(og.getUnit(), og.getMax())));
        }

        if (pg != null)
        {
            setField(FieldType.PG_BEFORE, new Field(FieldType.PG_BEFORE, Util.convertToBytes(pg.getUnit(), pg.getBefore())));
            setField(FieldType.PG_AFTER, new Field(FieldType.PG_AFTER, Util.convertToBytes(pg.getUnit(), pg.getAfter())));
            setField(FieldType.PG_CAPACITY, new Field(FieldType.PG_CAPACITY, Util.convertToBytes(pg.getUnit(), pg.getMax())));
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
        return CollectionType.FULL_COLLECTION;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    /**
     * A "system" Full GC (advertised in log as "Full GC (System)".
     */
    public boolean isSystem()
    {
        return system;
    }

    @Override
    public String toString()
    {
        return "FULL GC " + Configuration.TIMESTAMP_DISPLAY_FORMAT.format(getTime()) + " (" + getDuration() + " ms)";
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



