package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class FixedGenerationCount implements StoppingCondition {
    private final int maxGenerations;
    private int numGenerations = 0;

    public FixedGenerationCount(int maxGenerations2) throws NumberIsTooSmallException {
        if (maxGenerations2 <= 0) {
            throw new NumberIsTooSmallException(Integer.valueOf(maxGenerations2), 1, true);
        }
        this.maxGenerations = maxGenerations2;
    }

    @Override // org.apache.commons.math3.genetics.StoppingCondition
    public boolean isSatisfied(Population population) {
        if (this.numGenerations >= this.maxGenerations) {
            return true;
        }
        this.numGenerations++;
        return false;
    }

    public int getNumGenerations() {
        return this.numGenerations;
    }
}
