package com.novaordis.gc.parser;

import com.novaordis.gc.model.Timestamp;
import com.novaordis.gc.model.event.GCEvent;

import java.io.File;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public interface GCEventParser
{
    /**
     * If null, we're at the end of the pipeline.
     */
    GCEventParser getNext();

    void setNext(GCEventParser p);

    /**
     * @param current - the "current" event - non-null in case of the multi-line events, such as "SHUTDOWN". For all
     *                single-line events or for the first line of a multi-line event, passing null here is normal.
     *
     * @param gcFile - for logging purposes only, can be safely null.
     *
     * @return null if we don't know how to parse. Non-null if we 1) either parsed and generated a new event or
     *         2) we parsed it and added the newly generated information to the passed 'current' event.
     *
     * @throws Exception if we identified the event, but the log contains errors that prevent us from parsing it.
     */
    GCEvent parse(Timestamp ts, String line, long lineNumber, GCEvent current, File gcFile) throws Exception;

}
