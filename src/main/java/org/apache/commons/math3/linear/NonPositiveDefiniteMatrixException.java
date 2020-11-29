package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NonPositiveDefiniteMatrixException extends NumberIsTooSmallException {
    private static final long serialVersionUID = 1641613838113738061L;
    private final int index;
    private final double threshold;

    public NonPositiveDefiniteMatrixException(double wrong, int index2, double threshold2) {
        super(Double.valueOf(wrong), Double.valueOf(threshold2), false);
        this.index = index2;
        this.threshold = threshold2;
        ExceptionContext context = getContext();
        context.addMessage(LocalizedFormats.NOT_POSITIVE_DEFINITE_MATRIX, new Object[0]);
        context.addMessage(LocalizedFormats.ARRAY_ELEMENT, Double.valueOf(wrong), Integer.valueOf(index2));
    }

    public int getRow() {
        return this.index;
    }

    public int getColumn() {
        return this.index;
    }

    public double getThreshold() {
        return this.threshold;
    }
}
