package com.novaordis.gc.cli;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public class Util
{
    // Constants ---------------------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Util.class);

    // Static ------------------------------------------------------------------------------------------------------------------------------

    /**
     * @return null if the metadata file is not found or there was a read failure (also a log warning is generated).
     */
    public static String getVersion()
    {
        return getReleaseMetadata("version");
    }

    /**
     * @return null if the metadata file is not found or there was a read failure (also a log warning is generated).
     */
    public static String getReleaseDate()
    {
        return getReleaseMetadata("release_date");
    }

    /**
     * @return null if the metadata file is not found or there was a read failure (also a log warning is generated).
     */
    public static String getReleaseMetadata(String propertyName)
    {
        String releaseMetadataFileName = "VERSION";

        ClassLoader cl = Util.class.getClassLoader();

        InputStream is = cl.getResourceAsStream(releaseMetadataFileName);

        if (is == null)
        {
            log.warn("release metadata file \"" + releaseMetadataFileName + "\" not found on the classpath");
            return null;
        }

        Properties properties = new Properties();

        try
        {
            properties.load(is);
        }
        catch(IOException e)
        {
            log.warn("failed to read the release metadata file \"" + releaseMetadataFileName + "\"", e);
            return null;
        }

        String value = properties.getProperty(propertyName);

        if (value == null)
        {
            log.warn("no '" + propertyName + "' property found in \"" + releaseMetadataFileName + "\"");
            return null;
        }

        return value;
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------

}

