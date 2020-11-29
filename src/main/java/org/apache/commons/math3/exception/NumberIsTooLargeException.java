package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NumberIsTooLargeException extends MathIllegalNumberException {
    private static final long serialVersionUID = 4330003017885151975L;
    private final boolean boundIsAllowed;
    private final Number max;

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public NumberIsTooLargeException(Number wrong, Number max2, boolean boundIsAllowed2) {
        this(boundIsAllowed2 ? LocalizedFormats.NUMBER_TOO_LARGE : LocalizedFormats.NUMBER_TOO_LARGE_BOUND_EXCLUDED, wrong, max2, boundIsAllowed2);
    }

    public NumberIsTooLargeException(Localizable specific, Number wrong, Number max2, boolean boundIsAllowed2) {
        super(specific, wrong, max2);
        this.max = max2;
        this.boundIsAllowed = boundIsAllowed2;
    }

    public boolean getBoundIsAllowed() {
        return this.boundIsAllowed;
    }

    public Number getMax() {
        return this.max;
    }
}
