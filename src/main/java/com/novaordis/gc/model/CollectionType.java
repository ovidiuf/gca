package com.novaordis.gc.model;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 *
 * TODO: not all enum elements defined below are collection types, some are collection event types.
 */
public enum CollectionType
{
    FULL_COLLECTION("FULL", "full"),
    NEW_GENERATION_COLLECTION("NEW GENERATION", "ng"),
    SHUTDOWN("SHUTDOWN", "shutdown"),

    CMS_INITIAL_MARK("CMS INITIAL MARK", "cms-initial-mark"),
    CMS_CONCURRENT_MARK_START("CMS CONCURRENT MARK START", "cms-concurrent-mark-start"),
    CMS_CONCURRENT_PRECLEAN("CMS CONCURRENT PRECLEAN", "cms-concurrent-preclean"),
    CMS_CONCURRENT_MARK("CMS CONCURRENT MARK", "cms-concurrent-mark");

    // Constants ---------------------------------------------------------------------------------------------------------------------------

    /**
     * All known collection types - command line label.
     */
    public static final String ALL = "all";

    // Static ------------------------------------------------------------------------------------------------------------------------------

    /**
     * @return comma separated list of command line labels.
     */
    public static String getCommandLineLabels()
    {
        StringBuilder sb = new StringBuilder();
        CollectionType[] cts = CollectionType.values();

        for(int i = 0; i < cts.length; i ++)
        {
            sb.append('"').append(cts[i].commandLineLabel).append('"');

            if (i < cts.length - 1)
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    /**
     * @return a CollectionType instance if the appropriate command line label is provided, or null if no CollectionType can be identified
     *         in the string
     */
    public static CollectionType fromCommandLineLabel(String s)
    {
        if (s == null)
        {
            return null;
        }

        s = s.toLowerCase();

        if ("full".equals(s))
        {
            return FULL_COLLECTION;
        }
        else if ("ng".equals(s))
        {
            return NEW_GENERATION_COLLECTION;
        }
        else if ("cms-initial-mark".equals(s))
        {
            return CMS_INITIAL_MARK;
        }

        return null;
    }

    // Attributes --------------------------------------------------------------------------------------------------------------------------

    /**
     * The human readable label - this is how this field will be represented externally, in CSV files for example.
     */
    public String label;

    /**
     * The label used by the CLI to identify this field.
     */
    public String commandLineLabel;

    // Constructors ------------------------------------------------------------------------------------------------------------------------

    CollectionType(String label, String commandLineLabel)
    {
        this.label = label;
        this.commandLineLabel = commandLineLabel;
    }

    // Public ------------------------------------------------------------------------------------------------------------------------------

    // Package protected -------------------------------------------------------------------------------------------------------------------

    // Protected ---------------------------------------------------------------------------------------------------------------------------

    // Private -----------------------------------------------------------------------------------------------------------------------------

    // Inner classes -----------------------------------------------------------------------------------------------------------------------



}
