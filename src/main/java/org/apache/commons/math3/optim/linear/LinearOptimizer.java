package org.apache.commons.math3.optim.linear;

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;

public abstract class LinearOptimizer extends MultivariateOptimizer {
    private LinearObjectiveFunction function;
    private Collection<LinearConstraint> linearConstraints;
    private boolean nonNegative;

    protected LinearOptimizer() {
        super(null);
    }

    /* access modifiers changed from: protected */
    public boolean isRestrictedToNonNegative() {
        return this.nonNegative;
    }

    /* access modifiers changed from: protected */
    public LinearObjectiveFunction getFunction() {
        return this.function;
    }

    /* access modifiers changed from: protected */
    public Collection<LinearConstraint> getConstraints() {
        return Collections.unmodifiableCollection(this.linearConstraints);
    }

    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public PointValuePair optimize(OptimizationData... optData) throws TooManyIterationsException {
        return super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (OptimizationData data : optData) {
            if (data instanceof LinearObjectiveFunction) {
                this.function = (LinearObjectiveFunction) data;
            } else if (data instanceof LinearConstraintSet) {
                this.linearConstraints = ((LinearConstraintSet) data).getConstraints();
            } else if (data instanceof NonNegativeConstraint) {
                this.nonNegative = ((NonNegativeConstraint) data).isRestrictedToNonNegative();
            }
        }
    }
}
