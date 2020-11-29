package org.apache.commons.math3.util;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.random.RandomGenerator;

public class RandomPivotingStrategy implements PivotingStrategyInterface, Serializable {
    private static final long serialVersionUID = 20140713;
    private final RandomGenerator random;

    public RandomPivotingStrategy(RandomGenerator random2) {
        this.random = random2;
    }

    @Override // org.apache.commons.math3.util.PivotingStrategyInterface
    public int pivotIndex(double[] work, int begin, int end) throws MathIllegalArgumentException {
        MathArrays.verifyValues(work, begin, end - begin);
        return this.random.nextInt((end - begin) - 1) + begin;
    }
}
