package com.novaordis.gc.model.expression;

import com.novaordis.gc.model.FieldCategory;
import com.novaordis.gc.model.Unit;
import com.novaordis.gc.model.Value;
import com.novaordis.gc.model.event.GCEvent;
import com.novaordis.series.Header;

/**
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public interface Expression
{
    Header toHeader(Unit targetUnit) throws Exception;

    /**
     * May return null if the expression is non-dimensional.
     */
    FieldCategory getCategory();

    /**
     * Returns evaluation of this expression on the given GCEvent instance.
     *
     * A null argument is guaranteed to produce a null result.
     *
     * If this expression cannot be evaluated in the context of the given GCEvent (for example, the expression contain fields not present
     * in the GCEvent, the invocation returns null.
     */
    Value evaluate(GCEvent e) throws Exception;
}
