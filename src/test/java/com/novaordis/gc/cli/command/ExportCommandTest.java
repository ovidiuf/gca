package com.novaordis.gc.cli.command;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.Configuration;
import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.expression.Expression;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.event.FullCollection;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.gc.model.event.NewGenerationCollection;
import com.novaordis.gc.parser.BeforeAfterMax;
import com.novaordis.series.Header;
import com.novaordis.series.Metric;
import com.novaordis.series.Row;
import com.novaordis.series.Series;
import com.novaordis.series.metric.LongHeader;
import com.novaordis.series.metric.LongMetric;
import com.novaordis.series.metric.StringHeader;
import com.novaordis.series.metric.StringMetric;
import com.novaordis.utilities.Files;
import com.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ExportCommandTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ExportCommandTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    @Test
    public void constructorNoArguments() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export"
                };

        Configuration c = new Configuration(args);

        ExportCommand ec = (ExportCommand)c.getCommand();

        File of = ec.getOutputFile();
        assertNull(of); // write to stdout

        assertEquals(Unit.M, ec.getOutputMemoryUnit());

        Set<CollectionType> ct = ec.getCollectionTypes();
        assertEquals(1, ct.size());
        assertTrue(ct.contains(CollectionType.FULL_COLLECTION));

        List<Expression> expressions = ec.getExpressions();

        assertEquals(FieldType.OFFSET, expressions.get(0));
        assertEquals(FieldType.COLLECTION_TYPE, expressions.get(1));
        assertEquals(FieldType.DURATION, expressions.get(2));
        assertEquals(FieldType.OG_CAPACITY, expressions.get(3));
        assertEquals(FieldType.OG_AFTER, expressions.get(4));

        log.debug(".");
    }

    @Test
    public void constructor_Output() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--output",
                        "./test.csv"
                };

        Configuration c = new Configuration(args);

        ExportCommand ec = (ExportCommand)c.getCommand();

        File of = ec.getOutputFile();
        assertEquals(new File("./test.csv"), of);
    }

    @Test
    public void constructor_Output_2() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-o",
                        "./test.csv"
                };

        Configuration c = new Configuration(args);

        ExportCommand ec = (ExportCommand)c.getCommand();

        File of = ec.getOutputFile();
        assertEquals(new File("./test.csv"), of);
    }

    @Test
    public void constructor_Output_LastArgument() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--output"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_Output_ImmediatelyFollowedBySwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--output",
                        "--unit"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_Unit() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--unit",
                        "kb"
                };

        Configuration c = new Configuration(args);

        ExportCommand ec = (ExportCommand)c.getCommand();

        assertEquals(Unit.K, ec.getOutputMemoryUnit());
    }

    @Test
    public void constructor_Unit_2() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-u",
                        "KB"
                };

        Configuration c = new Configuration(args);

        ExportCommand ec = (ExportCommand)c.getCommand();

        assertEquals(Unit.K, ec.getOutputMemoryUnit());
    }

    @Test
    public void constructor_Unit_LastArgument() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--unit"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void constructor_Unit_ImmediatelyFollowedBySwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--unit",
                        "--output"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // collection-type specification -------------------------------------------------------------------------------------------------------

    @Test
    public void testConstructor_NoArgumentFollowingCollectionType_LongSwitch() throws Exception
    {
        String[] args = new String[]
                {
                        "/does/not/matter",
                        "export",
                        "--collection-type"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because nothing follows --collection-type");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_NoArgumentFollowingCollectionType_ShortSwitch() throws Exception
    {
        String[] args = new String[]
                {
                        "/does/not/matter",
                        "export",
                        "-c"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because nothing follows --collection-type");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_Full() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "full"
                };

        Configuration c = new Configuration(args);

        Set<CollectionType> cts = ((ExportCommand)c.getCommand()).getCollectionTypes();
        assertEquals(1, cts.size());
        assertTrue(cts.contains(CollectionType.FULL_COLLECTION));
    }

    @Test
    public void testConstructor_Ng() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "ng"
                };

        Configuration c = new Configuration(args);

        Set<CollectionType> cts = ((ExportCommand)c.getCommand()).getCollectionTypes();
        assertEquals(1, cts.size());
        assertTrue(cts.contains(CollectionType.NEW_GENERATION_COLLECTION));
    }

    @Test
    public void testConstructor_Full_Comma_Ng() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "full,",
                        "ng"
                };

        Configuration c = new Configuration(args);

        Set<CollectionType> cts = ((ExportCommand)c.getCommand()).getCollectionTypes();
        assertEquals(2, cts.size());
        assertTrue(cts.contains(CollectionType.FULL_COLLECTION));
        assertTrue(cts.contains(CollectionType.NEW_GENERATION_COLLECTION));
    }

    @Test
    public void testConstructor_Full_Space_Comma_Ng() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "full",
                        ",",
                        "ng"
                };

        Configuration c = new Configuration(args);

        Set<CollectionType> cts = ((ExportCommand)c.getCommand()).getCollectionTypes();
        assertEquals(2, cts.size());
        assertTrue(cts.contains(CollectionType.FULL_COLLECTION));
        assertTrue(cts.contains(CollectionType.NEW_GENERATION_COLLECTION));
    }

    @Test
    public void testConstructor_Duplicates() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "full,",
                        "ng,",
                        "full,",
                        "ng,"
                };

        Configuration c = new Configuration(args);

        Set<CollectionType> cts = ((ExportCommand)c.getCommand()).getCollectionTypes();
        assertEquals(2, cts.size());
        assertTrue(cts.contains(CollectionType.FULL_COLLECTION));
        assertTrue(cts.contains(CollectionType.NEW_GENERATION_COLLECTION));
    }

    @Test
    public void testConstructor_All() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "all"
                };

        Configuration c = new Configuration(args);

        Set<CollectionType> cts = ((ExportCommand)c.getCommand()).getCollectionTypes();
        assertEquals(7, cts.size());
        assertTrue(cts.contains(CollectionType.FULL_COLLECTION));
        assertTrue(cts.contains(CollectionType.NEW_GENERATION_COLLECTION));
        assertTrue(cts.contains(CollectionType.SHUTDOWN));
        assertTrue(cts.contains(CollectionType.CMS_CONCURRENT_MARK_START));
        assertTrue(cts.contains(CollectionType.CMS_CONCURRENT_PRECLEAN));
        assertTrue(cts.contains(CollectionType.CMS_CONCURRENT_MARK));
    }

    @Test
    public void testConstructor_All_And_Duplicates() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "full,",
                        "all,",
                        "ng,",
                        "full,",
                        "ng,"
                };

        Configuration c = new Configuration(args);

        Set<CollectionType> cts = ((ExportCommand)c.getCommand()).getCollectionTypes();
        assertEquals(7, cts.size());
        assertTrue(cts.contains(CollectionType.FULL_COLLECTION));
        assertTrue(cts.contains(CollectionType.NEW_GENERATION_COLLECTION));
        assertTrue(cts.contains(CollectionType.SHUTDOWN));
        assertTrue(cts.contains(CollectionType.CMS_CONCURRENT_MARK_START));
        assertTrue(cts.contains(CollectionType.CMS_CONCURRENT_PRECLEAN));
        assertTrue(cts.contains(CollectionType.CMS_CONCURRENT_MARK));
    }

    @Test
    public void testConstructor_NonCollectionTypeAfterCollectionTypeSwitch_LongSwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--collection-type",
                        "something"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because unknown argument follows --collection-type");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_NonCollectionTypeAfterCollectionTypeSwitch_ShortSwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-c",
                        "something"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because unknown argument follows -c");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    // fields specification ----------------------------------------------------------------------------------------------------------------

    @Test
    public void testConstructor_NoArgumentFollowingFields_LongSwitch() throws Exception
    {
        String[] args = new String[]
                {
                        "/does/not/matter",
                        "export",
                        "--fields"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because nothing follows --fields");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_NoArgumentFollowingFields_ShortSwitch() throws Exception
    {
        String[] args = new String[]
                {
                        "/does/not/matter",
                        "export",
                        "-f"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because nothing follows --fields");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_Fields_LongSwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--fields",
                        "pg-before,",
                        "pg-capacity"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(2, exp.size());
        assertEquals(FieldType.PG_BEFORE, exp.get(0));
        assertEquals(FieldType.PG_CAPACITY, exp.get(1));
    }

    @Test
    public void testConstructor_Fields_ShortSwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "collection-type,",
                        "duration"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(2, exp.size());
        assertEquals(FieldType.COLLECTION_TYPE, exp.get(0));
        assertEquals(FieldType.DURATION, exp.get(1));
    }

    @Test
    public void testConstructor_NonFieldAfterFieldsTypeSwitch_LongSwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "--fields",
                        "something"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because unknown argument follows --fields");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_NonFieldAfterFieldsTypeSwitch_ShortSwitch() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "something"
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, because unknown argument follows -f");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_Fields_CommaSeparatedNoSpace() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "ng-before,og-before"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(2, exp.size());
        assertEquals(FieldType.NG_BEFORE, exp.get(0));
        assertEquals(FieldType.OG_BEFORE, exp.get(1));
    }

    @Test
    public void testConstructor_Fields_CommaSeparatedNoSpace2() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "ng-before,og-before,pg-before"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(3, exp.size());
        assertEquals(FieldType.NG_BEFORE, exp.get(0));
        assertEquals(FieldType.OG_BEFORE, exp.get(1));
        assertEquals(FieldType.PG_BEFORE, exp.get(2));
    }

    @Test
    public void testConstructor_Fields_CombinationOfCommaSeparatedNoSpaceAndSpace() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "ng-after",
                        "ng-before,og-before,pg-before",
                        "pg-after"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(5, exp.size());
        assertEquals(FieldType.NG_AFTER, exp.get(0));
        assertEquals(FieldType.NG_BEFORE, exp.get(1));
        assertEquals(FieldType.OG_BEFORE, exp.get(2));
        assertEquals(FieldType.PG_BEFORE, exp.get(3));
        assertEquals(FieldType.PG_AFTER, exp.get(4));
    }

    @Test
    public void testConstructor_Fields_CombinationOfCommaSeparatedNoSpaceAndSpace2() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "ng-after",
                        ",ng-before,",
                        "pg-after"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(3, exp.size());
        assertEquals(FieldType.NG_AFTER, exp.get(0));
        assertEquals(FieldType.NG_BEFORE, exp.get(1));
        assertEquals(FieldType.PG_AFTER, exp.get(2));
    }

    @Test
    public void testConstructor_Fields_MultipleLeadingCommas() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        ",,,ng-after",
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(1, exp.size());
        assertEquals(FieldType.NG_AFTER, exp.get(0));
    }

    @Test
    public void testConstructor_Fields_MultipleTrailingCommas() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "ng-after,,,",
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(1, exp.size());
        assertEquals(FieldType.NG_AFTER, exp.get(0));
    }

    @Test
    public void testConstructor_Fields_MultipleCommasNoField() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        ",,,",
                };

        try
        {
            new Configuration(args);
            fail("should have failed with UserErrorException, no fields specified");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testConstructor_Fields_GroupsOfCommaSeparated() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "ng-after,ng-before,ng-capacity,",
                        "og-after,og-before,og-capacity",
                        "pg-after"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(7, exp.size());
        assertEquals(FieldType.NG_AFTER, exp.get(0));
        assertEquals(FieldType.NG_BEFORE, exp.get(1));
        assertEquals(FieldType.NG_CAPACITY, exp.get(2));
        assertEquals(FieldType.OG_AFTER, exp.get(3));
        assertEquals(FieldType.OG_BEFORE, exp.get(4));
        assertEquals(FieldType.OG_CAPACITY, exp.get(5));
        assertEquals(FieldType.PG_AFTER, exp.get(6));
    }

    @Test
    public void testConstructor_Fields_GroupsOfCommaSeparated2() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "ng-after,ng-before,ng-capacity,",
                        "pg-before",
                        "og-after,og-before,og-capacity",
                        "pg-after"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(8, exp.size());
        assertEquals(FieldType.NG_AFTER, exp.get(0));
        assertEquals(FieldType.NG_BEFORE, exp.get(1));
        assertEquals(FieldType.NG_CAPACITY, exp.get(2));
        assertEquals(FieldType.PG_BEFORE, exp.get(3));
        assertEquals(FieldType.OG_AFTER, exp.get(4));
        assertEquals(FieldType.OG_BEFORE, exp.get(5));
        assertEquals(FieldType.OG_CAPACITY, exp.get(6));
        assertEquals(FieldType.PG_AFTER, exp.get(7));
    }

    @Test
    public void testConstructor_Fields_HeapFields() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "/tmp/gc.12-Jan-11-0000");
        Files.write(f, "some data");

        String[] args = new String[]
                {
                        f.getAbsolutePath(),
                        "export",
                        "-f",
                        "heap-before",
                        "heap-after",
                        "heap-capacity"
                };

        Configuration c = new Configuration(args);

        List<Expression> exp = ((ExportCommand)c.getCommand()).getExpressions();
        assertEquals(3, exp.size());
        assertEquals(FieldType.HEAP_BEFORE, exp.get(0));
        assertEquals(FieldType.HEAP_AFTER, exp.get(1));
        assertEquals(FieldType.HEAP_CAPACITY, exp.get(2));
    }

    // toSeries() tests --------------------------------------------------------------------------------------------------------------------

    @Test
    public void toSeries_OffsetOnly() throws Exception
    {
        ExportCommand ec = new ExportCommand();

        List<Expression> toBeExported = Arrays.asList((Expression)FieldType.OFFSET);
        ec.setExpressions(toBeExported);

        Set<CollectionType> collectionTypes = new HashSet<CollectionType>();
        collectionTypes.add(CollectionType.FULL_COLLECTION);
        ec.setCollectionTypes(collectionTypes);

        List<GCEvent> events = new ArrayList<GCEvent>();
        events.add(new FullCollection(
                new Timestamp("101.101", 0L, null, false), 1001L,
                new BeforeAfterMax(-1L, -1L, -1L, Unit.b), new BeforeAfterMax(-1L, -1L, -1L, Unit.b),
                new BeforeAfterMax(-1L, -1L, -1L, Unit.b), new BeforeAfterMax(-1L, -1L, -1L, Unit.b), false));

        Series s = ec.toSeries(events);

        List<Header> headers = s.getHeaders();
        assertEquals(1, headers.size());

        assertTrue(headers.get(0) instanceof StringHeader);
        assertEquals(FieldType.OFFSET.label, headers.get(0).getLabel());

        assertEquals(1, s.getCount());

        Iterator<Row> ri = s.iterator();
        assertTrue(ri.hasNext());

        Row r = ri.next();

        assertFalse(ri.hasNext());

        assertEquals(101101L, r.getTime());

        List<Metric> metrics = r.getMetrics();

        assertEquals(1, metrics.size());

        StringMetric m = (StringMetric)metrics.get(0);
        assertEquals("101.101", m.getString());
    }

    @Test
    public void toSeries_AllMetricTypes_Full_and_NG() throws Exception
    {
        ExportCommand ec = new ExportCommand();

        List<Expression> toBeExported = Arrays.asList((Expression)
                FieldType.OFFSET,
                FieldType.COLLECTION_TYPE,
                FieldType.DURATION,
                FieldType.OG_AFTER,
                FieldType.NG_AFTER);

        ec.setExpressions(toBeExported);

        Set<CollectionType> collectionTypes = new HashSet<CollectionType>();
        collectionTypes.add(CollectionType.FULL_COLLECTION);
        collectionTypes.add(CollectionType.NEW_GENERATION_COLLECTION);
        ec.setCollectionTypes(collectionTypes);

        List<GCEvent> events = new ArrayList<GCEvent>();

        events.add(new FullCollection(
                new Timestamp("101.101", 0L, null, false), 1001L,
                new BeforeAfterMax(-1L, 1L, -1L, Unit.b), new BeforeAfterMax(-1L, 10L, -1L, Unit.b),
                new BeforeAfterMax(-1L, -1L, -1L, Unit.b), new BeforeAfterMax(-1L, -1L, -1L, Unit.b), false));

        events.add(new NewGenerationCollection(
                new Timestamp("101.102", 0L, null, false), 1002L,
                new BeforeAfterMax(-1L, 2L, -1L, Unit.b), new BeforeAfterMax(-1L, -1L, -1L, Unit.b)));

        Series s = ec.toSeries(events);

        List<Header> headers = s.getHeaders();
        assertEquals(5, headers.size());

        assertTrue(headers.get(0) instanceof StringHeader);
        assertEquals(FieldType.OFFSET.label, headers.get(0).getLabel());
        assertTrue(headers.get(1) instanceof StringHeader);
        assertEquals(FieldType.COLLECTION_TYPE.label, headers.get(1).getLabel());
        assertTrue(headers.get(2) instanceof LongHeader);
        assertEquals(FieldType.DURATION.label + " (ms)", headers.get(2).getLabel());
        assertTrue(headers.get(3) instanceof LongHeader);
        assertEquals(FieldType.OG_AFTER.label + " (bytes)", headers.get(3).getLabel());
        assertTrue(headers.get(4) instanceof LongHeader);
        assertEquals(FieldType.NG_AFTER.label + " (bytes)", headers.get(4).getLabel());

        assertEquals(2, s.getCount());

        Iterator<Row> ri = s.iterator();
        assertTrue(ri.hasNext());

        Row r = ri.next();

        assertTrue(ri.hasNext());

        assertEquals(101101L, r.getTime());

        List<Metric> metrics = r.getMetrics();

        assertEquals(5, metrics.size());

        StringMetric sm = (StringMetric)metrics.get(0);
        assertEquals("101.101", sm.getString());

        sm = (StringMetric)metrics.get(1);
        assertEquals("FULL", sm.getString());

        LongMetric lm = (LongMetric)metrics.get(2);
        assertEquals(1001L, lm.getLong());

        lm = (LongMetric)metrics.get(3);
        assertEquals(10L, lm.getLong());

        lm = (LongMetric)metrics.get(4);
        assertEquals(1L, lm.getLong());

        r = ri.next();

        assertFalse(ri.hasNext());

        assertEquals(101102L, r.getTime());

        metrics = r.getMetrics();

        assertEquals(5, metrics.size());

        sm = (StringMetric)metrics.get(0);
        assertEquals("101.102", sm.getString());

        sm = (StringMetric)metrics.get(1);
        assertEquals("NEW GENERATION", sm.getString());

        lm = (LongMetric)metrics.get(2);
        assertEquals(1002L, lm.getLong());

        Metric m = metrics.get(3);
        assertEquals(Metric.EMPTY_METRIC, m);

        lm = (LongMetric)metrics.get(4);
        assertEquals(2L, lm.getLong());
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------
}



