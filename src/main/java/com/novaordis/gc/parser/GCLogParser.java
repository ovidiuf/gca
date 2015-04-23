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
    List<GCEvent> parse(Long timeOrigin) throws Exception;
}
