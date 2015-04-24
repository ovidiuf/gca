package com.novaordis.gc.model.event;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.Field;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.parser.GCEventParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public abstract class GCEventBase implements GCEvent
{
    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Field time;
    private Field offset;
    private Field duration;
    private Field collectionType;

    private Map<FieldType, Field> fields;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected GCEventBase(Timestamp ts, long duration)
    {
        this(ts, duration, null); // no notes
    }

    protected GCEventBase(Timestamp ts, long duration, String notes)
    {
        long timeValue = ts.getTime() == null ? -1L : ts.getTime();
        this.time = new Field(FieldType.TIME, timeValue);

        String offsetLiteral = ts.getOffsetLiteral();
        if (offsetLiteral != null)
        {
            this.offset = new Field(FieldType.OFFSET, offsetLiteral);
        }

        this.duration = new Field(FieldType.DURATION, duration);

        this.fields = new HashMap<FieldType, Field>();

        if (notes != null)
        {
            fields.put(FieldType.NOTES, new Field(FieldType.NOTES, notes));
        }
    }

    // GCEvent overrides -----------------------------------------------------------------------------------------------

    @Override
    public long getTime()
    {
        return (Long)time.getValue();
    }

    /**
     * @see GCEvent#getDuration()
     */
    @Override
    public long getDuration()
    {
        return (Long)duration.getValue();
    }

    @Override
    public long getOffset()
    {
        // the offset is maintained as the original String, as we mostly need it for String searches; so every time we need it as a
        // long (milliseconds), we convert it - it should not be very often.

        String s = (String)offset.getValue();

        if (s == null)
        {
            return -1L;
        }

        try
        {
            return Timestamp.offsetToLong(s, null);
        }
        catch(Exception e)
        {
            // we should never get here, the offset was previously parsed and vetted, if we get here we're an invalid state
            throw new IllegalStateException(e);
        }
    }

    /**
     * @see GCEvent#get(com.novaordis.gc.model.FieldType)
     */
    @Override
    public Field get(FieldType t)
    {
        // time, offset and duration are not maintained in the map

        if (FieldType.TIME.equals(t))
        {
            return time;
        }
        else if (FieldType.OFFSET.equals(t))
        {
            return offset;
        }
        else //noinspection IfMayBeConditional
            if (FieldType.DURATION.equals(t))
        {
            return duration;
        }
        else //noinspection IfMayBeConditional
             if (FieldType.COLLECTION_TYPE.equals(t))
        {
            return collectionType;
        }
        else
        {
            return fields.get(t);
        }
    }

    @Override
    public GCEventParser getActiveParser()
    {
        return null;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // TODO case for TIME/OFFSET/DURATION - for the time being we don't support adding them this way
    protected void setField(FieldType t, Field f)
    {
        if (FieldType.TIME.equals(t) || FieldType.OFFSET.equals(t) || FieldType.DURATION.equals(t))
        {
            throw new IllegalArgumentException("NOT YET IMPLEMENTED");
        }

        fields.put(t, f);
    }

    protected void setCollectionType(CollectionType ct)
    {
        this.collectionType = new Field(FieldType.COLLECTION_TYPE, ct);
    }

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



