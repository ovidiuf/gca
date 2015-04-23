package com.novaordis.gc.model;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.series.metric.DoubleHeader;
import com.novaordis.series.metric.DoubleMetric;
import com.novaordis.series.metric.LongHeader;
import com.novaordis.series.metric.LongMetric;
import com.novaordis.series.metric.StringHeader;
import com.novaordis.series.metric.StringMetric;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CollectionTypeTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CollectionTypeTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // fromCommandLineLabel ----------------------------------------------------------------------------------------------------------------

    @Test
    public void testNullCommandLineLabel() throws Exception
    {
        assertNull(CollectionType.fromCommandLineLabel(null));
        log.debug(".");
    }

    @Test
    public void testUnrecognizableCommandLineLabel() throws Exception
    {
        assertNull(CollectionType.fromCommandLineLabel("I am sure there's no such collection type"));
    }

    @Test
    public void testCommandLineLabel_Full_1() throws Exception
    {
        assertEquals(CollectionType.FULL_COLLECTION, CollectionType.fromCommandLineLabel("full"));
    }

    @Test
    public void testCommandLineLabel_Full_2() throws Exception
    {
        assertEquals(CollectionType.FULL_COLLECTION, CollectionType.fromCommandLineLabel("Full"));
    }

    @Test
    public void testCommandLineLabel_Full_3() throws Exception
    {
        assertEquals(CollectionType.FULL_COLLECTION, CollectionType.fromCommandLineLabel("FULL"));
    }

    @Test
    public void testCommandLineLabel_Ng_1() throws Exception
    {
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, CollectionType.fromCommandLineLabel("ng"));
    }

    @Test
    public void testCommandLineLabel_Ng_2() throws Exception
    {
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, CollectionType.fromCommandLineLabel("Ng"));
    }

    @Test
    public void testCommandLineLabel_Ng_3() throws Exception
    {
        assertEquals(CollectionType.NEW_GENERATION_COLLECTION, CollectionType.fromCommandLineLabel("NG"));
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



