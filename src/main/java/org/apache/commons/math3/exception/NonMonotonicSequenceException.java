package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;

public class NonMonotonicSequenceException extends MathIllegalNumberException {
    private static final long serialVersionUID = 3596849179428944575L;
    private final MathArrays.OrderDirection direction;
    private final int index;
    private final Number previous;
    private final boolean strict;

    public NonMonotonicSequenceException(Number wrong, Number previous2, int index2) {
        this(wrong, previous2, index2, MathArrays.OrderDirection.INCREASING, true);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NonMonotonicSequenceException(Number wrong, Number previous2, int index2, MathArrays.OrderDirection direction2, boolean strict2) {
        super(direction2 == MathArrays.OrderDirection.INCREASING ? strict2 ? LocalizedFormats.NOT_STRICTLY_INCREASING_SEQUENCE : LocalizedFormats.NOT_INCREASING_SEQUENCE : strict2 ? LocalizedFormats.NOT_STRICTLY_DECREASING_SEQUENCE : LocalizedFormats.NOT_DECREASING_SEQUENCE, wrong, previous2, Integer.valueOf(index2), Integer.valueOf(index2 - 1));
        this.direction = direction2;
        this.strict = strict2;
        this.index = index2;
        this.previous = previous2;
    }

    public MathArrays.OrderDirection getDirection() {
        return this.direction;
    }

    public boolean getStrict() {
        return this.strict;
    }

    public int getIndex() {
        return this.index;
    }

    public Number getPrevious() {
        return this.previous;
    }
}
