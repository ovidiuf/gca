package com.novaordis.gc.model;

import com.novaordis.series.Metric;

/**
 * The result of an Expression evaluation.
 *
 * @see com.novaordis.gc.model.expression.Expression
 *
 * @author <a href="mailto:ovidiu@novaordis.com">Ovidiu Feodorov</a>
 *
 * Copyright 2013 Nova Ordis LLC
 */
public interface Value
{
    /**
     * May return null if this is a synthetic value that does not correspond to any specific FieldType.
     */
    FieldType getType();

    /**
     * @return a non-null value.
     */
    Object getValue();

    /**
     * @see FieldType#toMetric(Object, Unit)
     */
    Metric toMetric(Unit unit) throws Exception;

}
