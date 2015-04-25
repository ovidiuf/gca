package com.novaordis.gc.model.event;

import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public abstract class GCEventTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(GCEventTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void testTimestampDuration() throws Exception
    {
        Timestamp ts = new Timestamp("100.100", 10L, null, false);

        GCEvent e = getGCEventToTest(ts, 7L);

        // time (dedicated accessor and generic field)
        assertEquals(100110L, e.getTime().longValue());
        assertEquals(100110L, ((Long)e.get(FieldType.TIME).getValue()).longValue());

        // offset (dedicated accessor and generic field)
        assertEquals(100100L, e.getOffset().longValue());
        assertEquals("100.100", e.get(FieldType.OFFSET).getValue());

        // duration (dedicated accessor and generic field)
        assertEquals(7L, e.getDuration());
        assertEquals(7L, ((Long)e.get(FieldType.DURATION).getValue()).longValue());

        log.debug(".");
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    protected abstract GCEvent getGCEventToTest(Timestamp t, long duration) throws Exception;

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



