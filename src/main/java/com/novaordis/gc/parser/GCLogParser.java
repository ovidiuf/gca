package com.novaordis.gc.parser;

import com.novaordis.gc.model.event.GCEvent;
import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public interface GCLogParser
{
    /**
     * @param timeOrigin must be not null, even if the wrapped time origin value is null.
     */
    List<GCEvent> parse(TimeOrigin timeOrigin) throws Exception;
}
