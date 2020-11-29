package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MaxCountExceededException extends MathIllegalStateException {
    private static final long serialVersionUID = 4330003017885151975L;
    private final Number max;

    public MaxCountExceededException(Number max2) {
        this(LocalizedFormats.MAX_COUNT_EXCEEDED, max2, new Object[0]);
    }

    public MaxCountExceededException(Localizable specific, Number max2, Object... args) {
        getContext().addMessage(specific, max2, args);
        this.max = max2;
    }

    public Number getMax() {
        return this.max;
    }
}
