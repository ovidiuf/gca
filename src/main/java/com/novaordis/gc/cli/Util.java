package com.novaordis.gc.cli;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Util
{
    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Util.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static void timeConversion() throws Exception
    {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date d = format.parse("1/9/2015 5:38:02 AM");

        System.out.println(format.format(d.getTime() - 23766));
    }

    public static void displayContentFromClasspath(String fileName)
    {
        BufferedReader br = null;

        try
        {
            InputStream is = Main.class.getClassLoader().getResourceAsStream(fileName);

            if (is != null)
            {
                br = new BufferedReader(new InputStreamReader(is));

                String line;
                while((line = br.readLine()) != null)
                {
                    System.out.println(line);
                }
            }

            return;
        }
        catch(Exception e)
        {
            // swallow for the time being
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("failed to load the '" + fileName + "' file content from classpath");
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

