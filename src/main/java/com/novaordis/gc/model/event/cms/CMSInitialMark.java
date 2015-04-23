package com.novaordis.gc.model.event.cms;

import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.Field;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Util;
import com.novaordis.gc.parser.CurrentMax;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CMSInitialMark extends CMSEvent
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    public CMSInitialMark(Timestamp ts, long duration, CurrentMax og, CurrentMax heap)
    {
        super(ts, duration);
        setField(FieldType.OG, new Field(FieldType.OG, Util.convertToBytes(og.getUnit(), og.getCurrent())));
        setField(FieldType.OG_CAPACITY, new Field(FieldType.OG_CAPACITY, Util.convertToBytes(og.getUnit(), og.getMax())));
        setField(FieldType.HEAP, new Field(FieldType.HEAP, Util.convertToBytes(og.getUnit(), heap.getCurrent())));
        setField(FieldType.HEAP_CAPACITY, new Field(FieldType.HEAP_CAPACITY, Util.convertToBytes(og.getUnit(), heap.getMax())));
    }

    // GCEvent implementation --------------------------------------------------------------------------------------------------------------

    @Override
    public CollectionType getCollectionType()
    {
        return CollectionType.CMS_INITIAL_MARK;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "CMS-initial-mark " + Configuration.TIMESTAMP_DISPLAY_FORMAT.format(getTime()) + " (" + getDuration() + " ms)";
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



