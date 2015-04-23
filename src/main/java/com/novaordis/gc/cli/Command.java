package com.novaordis.gc.cli;

import com.novaordis.gc.model.event.GCEvent;

import java.util.List;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public interface Command
{
    /**
     * Some commands (such as "version", etc) do not need GC data, or even a GC file to look at. Based on this indication, we can
     * optimize processing.
     */
    boolean needsGcData();

    void execute(List<GCEvent> events) throws Exception;
}
