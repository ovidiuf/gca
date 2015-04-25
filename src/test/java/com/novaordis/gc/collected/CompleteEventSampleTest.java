package com.novaordis.gc.collected;

import com.novaordis.gc.model.Field;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.event.*;
import com.novaordis.gc.parser.GCLogParser;
import com.novaordis.gc.parser.GCLogParserFactory;
import com.novaordis.gc.parser.TimeOrigin;
import com.novaordis.gc.parser.linear.LinearScanParser;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A real abbreviated gc log
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class CompleteEventSampleTest
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(CompleteEventSampleTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @Test
    public void allValuesSanity() throws Exception
    {
        InputStream is = CompleteEventSampleTest.class.getClassLoader().getResourceAsStream("collected/1.log");
        assertNotNull(is);

        InputStreamReader isr = new InputStreamReader(is);

        GCLogParser p = GCLogParserFactory.getParser(isr);
        assertTrue(p instanceof LinearScanParser);

        List<GCEvent> events = p.parse(new TimeOrigin(0L));

        assertEquals(9, events.size());

        NewGenerationCollection nc;
        FullCollection fc;
        Field f;

        nc = (NewGenerationCollection)events.get(0);

        assertEquals(76L, nc.getDuration());
        assertEquals(4911L, nc.getOffset().longValue());

        f = nc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 660688, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_AFTER);
        assertEquals(1024L * 72899, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1835008, ((Long)f.getValue()).longValue());

        f = nc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 660688, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 72899, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 6029312, ((Long)f.getValue()).longValue());

        fc = (FullCollection)events.get(1);

        assertEquals(244L, fc.getDuration());
        assertEquals(4987L, fc.getOffset().longValue());

        f = fc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 72899, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_AFTER);
        //noinspection PointlessArithmeticExpression
        assertEquals(1024L * 0, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1835008, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_BEFORE);
        //noinspection PointlessArithmeticExpression
        assertEquals(1024L * 0, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_AFTER);
        assertEquals(1024L * 72243, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_CAPACITY);
        assertEquals(1024L * 4194304, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 72899, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 72243, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 6029312, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_BEFORE);
        assertEquals(1024L * 29282, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_AFTER);
        assertEquals(1024L * 29282, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_CAPACITY);
        assertEquals(1024L * 59136, ((Long)f.getValue()).longValue());

        nc = (NewGenerationCollection)events.get(2);

        assertEquals(171L, nc.getDuration());
        assertEquals(11645L, nc.getOffset().longValue());

        f = nc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 1572864, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_AFTER);
        assertEquals(1024L * 262134, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1835008, ((Long)f.getValue()).longValue());

        f = nc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 1645107, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 347275, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 6029312, ((Long)f.getValue()).longValue());

        fc = (FullCollection)events.get(3);

        assertEquals(2662L, fc.getDuration());
        assertEquals(3605261L, fc.getOffset().longValue());

        f = fc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 128278, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_AFTER);
        //noinspection PointlessArithmeticExpression
        assertEquals(1024L * 0, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1856576, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_BEFORE);
        assertEquals(1024L * 2343450, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_AFTER);
        assertEquals(1024L * 1122860, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_CAPACITY);
        assertEquals(1024L * 4194304, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 2471728, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 1122860, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 6050880, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_BEFORE);
        assertEquals(1024L * 256350, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_AFTER);
        assertEquals(1024L * 256350, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_CAPACITY);
        assertEquals(1024L * 524288, ((Long)f.getValue()).longValue());

        nc = (NewGenerationCollection)events.get(4);

        assertEquals(205L, nc.getDuration());
        assertEquals(4631564L, nc.getOffset().longValue());

        f = nc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 1438615, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_AFTER);
        assertEquals(1024L * 292432, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1688448, ((Long)f.getValue()).longValue());

        f = nc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 5446401, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 4367869, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 5882752, ((Long)f.getValue()).longValue());

        fc = (FullCollection)events.get(5);

        assertEquals(2560L, fc.getDuration());
        assertEquals(4631769L, fc.getOffset().longValue());

        f = fc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 292432, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_AFTER);
        //noinspection PointlessArithmeticExpression
        assertEquals(1024L * 0, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1688448, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_BEFORE);
        assertEquals(1024L * 4075437, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_AFTER);
        assertEquals(1024L * 1460875, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_CAPACITY);
        assertEquals(1024L * 4194304, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 4367869, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 1460875, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 5882752, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_BEFORE);
        assertEquals(1024L * 257876, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_AFTER);
        assertEquals(1024L * 257876, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_CAPACITY);
        assertEquals(1024L * 524288, ((Long)f.getValue()).longValue());

        //K->K(K),  secs] [Times: user=0.47 sys=0.00, real=0.06 secs]

        nc = (NewGenerationCollection)events.get(6);

        assertEquals(62L, nc.getDuration());
        assertEquals(203963710L, nc.getOffset().longValue());

        f = nc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 868933, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_AFTER);
        assertEquals(1024L * 362313, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1398144, ((Long)f.getValue()).longValue());

        f = nc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 4333802, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 4037180, ((Long)f.getValue()).longValue());
        f = nc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 5592448, ((Long)f.getValue()).longValue());

        fc = (FullCollection)events.get(7);

        assertEquals(2839L, fc.getDuration());
        assertEquals(203963772L, fc.getOffset().longValue());

        f = fc.get(FieldType.NG_BEFORE);
        assertEquals(1024L * 362313, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_AFTER);
        //noinspection PointlessArithmeticExpression
        assertEquals(1024L * 0, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.NG_CAPACITY);
        assertEquals(1024L * 1398144, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_BEFORE);
        assertEquals(1024L * 3674867, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_AFTER);
        assertEquals(1024L * 1941180, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.OG_CAPACITY);
        assertEquals(1024L * 4194304, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_BEFORE);
        assertEquals(1024L * 4037180, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_AFTER);
        assertEquals(1024L * 1941180, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.HEAP_CAPACITY);
        assertEquals(1024L * 5592448, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_BEFORE);
        assertEquals(1024L * 301435, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_AFTER);
        assertEquals(1024L * 301388, ((Long)f.getValue()).longValue());
        f = fc.get(FieldType.PG_CAPACITY);
        assertEquals(1024L * 301632, ((Long)f.getValue()).longValue());

        Shutdown se = (Shutdown)events.get(8);

        assertEquals(0L, se.getDuration());
        assertNull(se.getOffset());
        assertNull(se.getTime());

        log.debug(".");
    }

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------

}


