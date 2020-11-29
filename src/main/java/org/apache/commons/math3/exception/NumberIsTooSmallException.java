package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NumberIsTooSmallException extends MathIllegalNumberException {
    private static final long serialVersionUID = -6100997100383932834L;
    private final boolean boundIsAllowed;
    private final Number min;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public NumberIsTooSmallException(Number wrong, Number min2, boolean boundIsAllowed2) {
        this(boundIsAllowed2 ? LocalizedFormats.NUMBER_TOO_SMALL : LocalizedFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED, wrong, min2, boundIsAllowed2);
    }

    public NumberIsTooSmallException(Localizable specific, Number wrong, Number min2, boolean boundIsAllowed2) {
        super(specific, wrong, min2);
        this.min = min2;
        this.boundIsAllowed = boundIsAllowed2;
    }

    public boolean getBoundIsAllowed() {
        return this.boundIsAllowed;
    }

    public Number getMin() {
        return this.min;
    }
}
