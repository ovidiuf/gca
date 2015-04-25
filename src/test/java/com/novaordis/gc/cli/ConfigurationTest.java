package com.novaordis.gc.cli;

import com.novaordis.gc.UserErrorException;
import com.novaordis.gc.cli.command.TestCommand;
import com.novaordis.utilities.Files;
import com.novaordis.utilities.testing.Tests;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class ConfigurationTest extends Assert
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(ConfigurationTest.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    @After
    public void scratchCleanup() throws Exception
    {
        Tests.cleanup();
    }

    // time origin tests -------------------------------------------------------------------------------------------------------------------

    @Test
    public void testTimeOrigin_NoTimeStamp1() throws Exception
    {
        String[] args = new String[] {"--time-origin"};

        try
        {
            new Configuration(args);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testTimeOrigin_NoTimeStamp2() throws Exception
    {
        String[] args = new String[] {"-t"};

        try
        {
            new Configuration(args);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testTimeOrigin_InvalidTimeStamp1() throws Exception
    {
        String[] args = new String[] {"--time-origin", "blah"};

        try
        {
            new Configuration(args);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testTimeOrigin_InvalidTimeStamp2() throws Exception
    {
        String[] args = new String[] {"-t", "blah"};

        try
        {
            new Configuration(args);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testTimeOrigin_ValidTimeStamp1() throws Exception
    {
        File f =  new File(Tests.getScratchDirectory(), "test.log");
        Files.write(f, "test");

        String[] args = new String[] {"--time-origin", "12/20/11 00:01:02,003", f.getAbsolutePath(), "test"};

        Configuration c = new Configuration(args);

        assertEquals(Configuration.COMMAND_LINE_TIME_ORIGIN_FORMAT2.parse("12/20/11 00:01:02,003").getTime(), c.getTimeOrigin().longValue());
    }

    @Test
    public void testTimeOrigin_ValidTimeStamp2() throws Exception
    {
        File f =  new File(Tests.getScratchDirectory(), "test.log");
        Files.write(f, "test");

        String[] args = new String[] {"-t", "12/20/11 00:01:02,004", f.getAbsolutePath(), "test"};

        Configuration c = new Configuration(args);

        assertEquals(Configuration.COMMAND_LINE_TIME_ORIGIN_FORMAT2.parse("12/20/11 00:01:02,004").getTime(), c.getTimeOrigin().longValue());
    }

    @Test
    public void testTimeOrigin_IncompleteTimeStamp() throws Exception
    {
        File f =  new File(Tests.getScratchDirectory(), "test.log");
        Files.write(f, "test");

        String[] args = new String[] {"-t", "12/20/11"};

        try
        {
            new Configuration(args);
            fail("should have failed because of incomplete timestamp");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testTimeOrigin_SpaceSeparatedTimeStamp() throws Exception
    {
        File f =  new File(Tests.getScratchDirectory(), "test.log");
        Files.write(f, "test");

        String[] args = new String[] {"-t", "12/20/11", "00:01:02,004", f.getAbsolutePath(), "test"};

        Configuration c = new Configuration(args);

        assertEquals(Configuration.COMMAND_LINE_TIME_ORIGIN_FORMAT2.parse("12/20/11 00:01:02,004").getTime(), c.getTimeOrigin().longValue());
    }

    // target file tests -------------------------------------------------------------------------------------------------------------------

    @Test
    public void testFile_FileDoesNotExist() throws Exception
    {
        String[] args = new String[] {"/there/is/no/such/file.log", "test"};

        try
        {
            new Configuration(args);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    @Test
    public void testFile() throws Exception
    {
        File scratchDir = Tests.getScratchDirectory();
        File f = new File(scratchDir, "test.log");
        Files.write(f, "test");

        assertTrue(f.isFile());
        assertTrue(f.canRead());

        String[] args = new String[] {"-t", "10/10/10 10:10:10,100", f.getAbsolutePath(), "test"};

        Configuration c = new Configuration(args);

        assertEquals(f.getAbsoluteFile(), c.getGCLogFile());
        assertTrue(c.getCommand() instanceof TestCommand);
    }

    // inferred time origin --------------------------------------------------------------------------------------------

    @Test
    public void inferredTimeOrigin1() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "test/ap23-gc.log.29-Dec-11-0808");
        Files.write(f, "test");

        String[] args = new String[] {f.getAbsolutePath(), "test"};

        Configuration c = new Configuration(args);

        assertEquals(Configuration.COMMAND_LINE_TIME_ORIGIN_FORMAT.parse("12/29/11 08:08:00,000").getTime(), c.getTimeOrigin().longValue());
    }

    @Test
    public void inferredTimeOrigin2() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "test/ap23-gc.log.28-Dec-11-080855");
        Files.write(f, "test");

        assertTrue(f.isFile());
        assertTrue(f.canRead());

        String[] args = new String[] {f.getAbsolutePath(), "test"};

        Configuration c = new Configuration(args);

        assertEquals(Configuration.COMMAND_LINE_TIME_ORIGIN_FORMAT.parse("12/28/11 08:08:55,000").getTime(),
            c.getTimeOrigin().longValue());
    }

    @Test
    public void fileNameToTimeOrigin_NullName() throws Exception
    {
        assertNull(Configuration.fileNameToTimeOrigin(null));
    }

    @Test
    public void fileNameToTimeOrigin_Three() throws Exception
    {
        Long time = Configuration.fileNameToTimeOrigin("time-origin.log.04-May-11-032854");
        String s = new SimpleDateFormat("yy-MM-dd hh-mm-ss a").format(time);
        assertEquals("11-05-04 03-28-54 AM", s);
    }

    @Test
    public void fileNameToTimeOrigin_Fifteen() throws Exception
    {
        Long time = Configuration.fileNameToTimeOrigin("time-origin.log.04-May-11-152854");
        String s = new SimpleDateFormat("yy-MM-dd hh-mm-ss a").format(time);
        assertEquals("11-05-04 03-28-54 PM", s);
    }

    @Test
    public void fileNameToTimeOrigin_ST() throws Exception
    {
        Long time = Configuration.fileNameToTimeOrigin("garbage_collection.log-08-28-2013_13-14-31");
        String s = new SimpleDateFormat("yy-MM-dd hh-mm-ss a").format(time);
        assertEquals("13-08-28 01-14-31 PM", s);
    }

    //
    // gc.log.24Apr15130823
    //

    @Test
    public void fileNameToTimeOrigin_ddmmmyyHHMMSS() throws Exception
    {
        Long time = Configuration.fileNameToTimeOrigin("gc.log.24Apr15130823");
        String s = new SimpleDateFormat("yy-MM-dd hh-mm-ss a").format(time);
        assertEquals("15-04-24 01-08-23 PM", s);
    }

    // no time origin --------------------------------------------------------------------------------------------------

    @Test
    public void noTimeOriginOnCommandLineOrFileName() throws Exception
    {
        File f = new File(Tests.getScratchDirectory(), "test/test.log");
        Files.write(f, "test");

        String[] args = new String[] {f.getAbsolutePath(), "test"};

        Configuration c = new Configuration(args);
        assertNull(c.getTimeOrigin());
    }

    // out of place arguments ------------------------------------------------------------------------------------------

    @Test
    public void outOfPlaceArgument() throws Exception
    {
        String[] args = new String[] {"ap33-gc.log.14-Nov-13-015223", "-c", "ng export", "--fields", "", "heap-after"};

        try
        {
            new Configuration(args);
            fail("should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
            assertEquals("command line argument \"-c\" does not makes sense in the location it was specified", e.getMessage());
        }
    }

    // toTimeOrigin() --------------------------------------------------------------------------------------------------

    @Test
    public void commandLineTimeOrigin() throws Exception
    {
        String s = "01/09/15 5:38:02";

        Date d = Configuration.COMMAND_LINE_TIME_ORIGIN_FORMAT.parse(s);

        long time = Configuration.toTimeOrigin(s);
        assertEquals(d.getTime(), time);
    }

    @Test
    public void commandLineTimeOrigin2() throws Exception
    {
        String s = "01/09/15 5:38:02,125";

        Date d = Configuration.COMMAND_LINE_TIME_ORIGIN_FORMAT2.parse(s);

        long time = Configuration.toTimeOrigin(s);
        assertEquals(d.getTime(), time);
    }

    @Test
    public void commandLineTimeOrigin_InvalidFormat() throws Exception
    {
        try
        {
            Configuration.toTimeOrigin("blah");
            fail("Should fail with UserErrorException");
        }
        catch(UserErrorException e)
        {
            log.info(e.getMessage());
        }
    }

    public static final String COMMAND_LINE_TIME_ORIGIN_SPEC = "MM/dd/yy HH:mm:ss,SSS";
    public static final SimpleDateFormat COMMAND_LINE_TIME_ORIGIN_FORMAT =
        new SimpleDateFormat(COMMAND_LINE_TIME_ORIGIN_SPEC);


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------
}



