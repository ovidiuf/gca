package com.novaordis.gc.model.event;

import com.novaordis.gc.model.CollectionType;
import com.novaordis.gc.model.FieldType;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.parser.GCEventParser;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public interface GCEvent
{
    /**
     * @return the event timestamp in milliseconds. There are events (such as Shutdown) for which there is no timestamp
     *         so it may return null.
     */
    Long getTime();

    /**
     * In milliseconds.
     */
    long getDuration();

    /**
     * @return the offset to time origin (in milliseconds). Appears as "3229.871:" in logs. There are events for which
     *         the offset is not specified, so the method may return null.
     */
    Long getOffset();

    CollectionType getCollectionType();

    /**
     * Returns the value of a field obtained from parsing or null. Fields representing memory values are always
     * maintained internally in bytes.
     *
     * @return the corresponding field instance or null if there isn't one.
     */
    Value get(FieldType t);

    /**
     * <b>Multi-line events</b>
     *
     * There are two mechanisms to deal with multi-line events: GCEvent.getActiveParser() (used by Shutdown, so far)
     * and the read-ahead parsing (used by the '(concurrent mode failure)' events so far). Both are valid and time will
     * decide whether we should keep both or refactor and coalesce.
     *
     * For read-ahead parsing:
     *
     * @see com.novaordis.gc.parser.linear.LinearScanParser#parse(Long timeOrigin)
     *
     * @return the active parser in case of a multi-line events. A null result here means there's no active parser for
     * ths event.
     */
    GCEventParser getActiveParser();

}
