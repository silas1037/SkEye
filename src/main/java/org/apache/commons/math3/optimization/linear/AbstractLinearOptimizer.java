package org.apache.commons.math3.optimization.linear;

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;

@Deprecated
public abstract class AbstractLinearOptimizer implements LinearOptimizer {
    public static final int DEFAULT_MAX_ITERATIONS = 100;
    private LinearObjectiveFunction function;
    private GoalType goal;
    private int iterations;
    private Collection<LinearConstraint> linearConstraints;
    private int maxIterations;
    private boolean nonNegative;

    /* access modifiers changed from: protected */
    public abstract PointValuePair doOptimize() throws MathIllegalStateException;

    protected AbstractLinearOptimizer() {
        setMaxIterations(100);
    }

    /* access modifiers changed from: protected */
    public boolean restrictToNonNegative() {
        return this.nonNegative;
    }

    /* access modifiers changed from: protected */
    public GoalType getGoalType() {
        return this.goal;
    }

    /* access modifiers changed from: protected */
    public LinearObjectiveFunction getFunction() {
        return this.function;
    }

    /* access modifiers changed from: protected */
    public Collection<LinearConstraint> getConstraints() {
        return Collections.unmodifiableCollection(this.linearConstraints);
    }

    @Override // org.apache.commons.math3.optimization.linear.LinearOptimizer
    public void setMaxIterations(int maxIterations2) {
        this.maxIterations = maxIterations2;
    }

    @Override // org.apache.commons.math3.optimization.linear.LinearOptimizer
    public int getMaxIterations() {
        return this.maxIterations;
    }

    @Override // org.apache.commons.math3.optimization.linear.LinearOptimizer
    public int getIterations() {
        return this.iterations;
    }

    /* access modifiers changed from: protected */
    public void incrementIterationsCounter() throws MaxCountExceededException {
        int i = this.iterations + 1;
        this.iterations = i;
        if (i > this.maxIterations) {
            throw new MaxCountExceededException(Integer.valueOf(this.maxIterations));
        }
    }

    @Override // org.apache.commons.math3.optimization.linear.LinearOptimizer
    public PointValuePair optimize(LinearObjectiveFunction f, Collection<LinearConstraint> constraints, GoalType goalType, boolean restrictToNonNegative) throws MathIllegalStateException {
        this.function = f;
        this.linearConstraints = constraints;
        this.goal = goalType;
        this.nonNegative = restrictToNonNegative;
        this.iterations = 0;
        return doOptimize();
    }
}
