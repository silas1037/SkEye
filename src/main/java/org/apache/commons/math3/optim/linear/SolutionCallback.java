package org.apache.commons.math3.optim.linear;

import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;

public class SolutionCallback implements OptimizationData {
    private SimplexTableau tableau;

    /* access modifiers changed from: package-private */
    public void setTableau(SimplexTableau tableau2) {
        this.tableau = tableau2;
    }

    public PointValuePair getSolution() {
        if (this.tableau != null) {
            return this.tableau.getSolution();
        }
        return null;
    }

    public boolean isSolutionOptimal() {
        if (this.tableau != null) {
            return this.tableau.isOptimal();
        }
        return false;
    }
}
