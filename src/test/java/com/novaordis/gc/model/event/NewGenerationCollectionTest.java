package com.novaordis.gc.model.event;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.parser.BeforeAfterMax;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class NewGenerationCollectionTest extends GCEventTest
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(NewGenerationCollectionTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testNGConstructor() throws Exception
    {
        Timestamp ts = new Timestamp("1.000", 0L, null, false);
        BeforeAfterMax ng = new BeforeAfterMax("2K->1K(3K)", -1L);
        BeforeAfterMax heap = new BeforeAfterMax("20K->10K(30K)", -1L);

        NewGenerationCollection e = new NewGenerationCollection(ts, 1L, ng, heap);

        assertEquals(1000L, e.getTime());
        assertEquals(1000L, e.getOffset());
        assertEquals(1L, e.getDuration());

        assertEquals(2048L, e.get(FieldType.NG_BEFORE).getValue());
        assertEquals(1024L, e.get(FieldType.NG_AFTER).getValue());
        assertEquals(3072L, e.get(FieldType.NG_CAPACITY).getValue());

        assertEquals(20480L, e.get(FieldType.HEAP_BEFORE).getValue());
        assertEquals(10240L, e.get(FieldType.HEAP_AFTER).getValue());
        assertEquals(30720L, e.get(FieldType.HEAP_CAPACITY).getValue());

        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, e.getCollectionType());
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, e.get(FieldType.COLLECTION_TYPE).getValue());

        log.debug(".");
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    @Override
    protected GCEvent getGCEventToTest(Timestamp t, long duration) throws Exception
    {
        return new NewGenerationCollection(t, duration, null, null);
    }

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



