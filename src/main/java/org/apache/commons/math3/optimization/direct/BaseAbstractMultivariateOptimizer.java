package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optimization.BaseMultivariateOptimizer;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleBounds;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.util.Incrementor;

@Deprecated
public abstract class BaseAbstractMultivariateOptimizer<FUNC extends MultivariateFunction> implements BaseMultivariateOptimizer<FUNC> {
    private ConvergenceChecker<PointValuePair> checker;
    protected final Incrementor evaluations;
    private MultivariateFunction function;
    private GoalType goal;
    private double[] lowerBound;
    private double[] start;
    private double[] upperBound;

    /* access modifiers changed from: protected */
    public abstract PointValuePair doOptimize();

    @Deprecated
    protected BaseAbstractMultivariateOptimizer() {
        this(new SimpleValueChecker());
    }

    protected BaseAbstractMultivariateOptimizer(ConvergenceChecker<PointValuePair> checker2) {
        this.evaluations = new Incrementor();
        this.checker = checker2;
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public ConvergenceChecker<PointValuePair> getConvergenceChecker() {
        return this.checker;
    }

    /* access modifiers changed from: protected */
    public double computeObjectiveValue(double[] point) {
        try {
            this.evaluations.incrementCount();
            return this.function.value(point);
        } catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
    }

    @Override // org.apache.commons.math3.optimization.BaseMultivariateOptimizer
    @Deprecated
    public PointValuePair optimize(int maxEval, FUNC f, GoalType goalType, double[] startPoint) {
        return optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint));
    }

    public PointValuePair optimize(int maxEval, FUNC f, GoalType goalType, OptimizationData... optData) {
        return optimizeInternal(maxEval, f, goalType, optData);
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public PointValuePair optimizeInternal(int maxEval, FUNC f, GoalType goalType, double[] startPoint) {
        return optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint));
    }

    /* access modifiers changed from: protected */
    public PointValuePair optimizeInternal(int maxEval, FUNC f, GoalType goalType, OptimizationData... optData) throws TooManyEvaluationsException {
        this.evaluations.setMaximalCount(maxEval);
        this.evaluations.resetCount();
        this.function = f;
        this.goal = goalType;
        parseOptimizationData(optData);
        checkParameters();
        return doOptimize();
    }

    private void parseOptimizationData(OptimizationData... optData) {
        for (OptimizationData data : optData) {
            if (data instanceof InitialGuess) {
                this.start = ((InitialGuess) data).getInitialGuess();
            } else if (data instanceof SimpleBounds) {
                SimpleBounds bounds = (SimpleBounds) data;
                this.lowerBound = bounds.getLower();
                this.upperBound = bounds.getUpper();
            }
        }
    }

    public GoalType getGoalType() {
        return this.goal;
    }

    public double[] getStartPoint() {
        if (this.start == null) {
            return null;
        }
        return (double[]) this.start.clone();
    }

    public double[] getLowerBound() {
        if (this.lowerBound == null) {
            return null;
        }
        return (double[]) this.lowerBound.clone();
    }

    public double[] getUpperBound() {
        if (this.upperBound == null) {
            return null;
        }
        return (double[]) this.upperBound.clone();
    }

    private void checkParameters() {
        if (this.start != null) {
            int dim = this.start.length;
            if (this.lowerBound != null) {
                if (this.lowerBound.length != dim) {
                    throw new DimensionMismatchException(this.lowerBound.length, dim);
                }
                for (int i = 0; i < dim; i++) {
                    double v = this.start[i];
                    double lo = this.lowerBound[i];
                    if (v < lo) {
                        throw new NumberIsTooSmallException(Double.valueOf(v), Double.valueOf(lo), true);
                    }
                }
            }
            if (this.upperBound != null) {
                if (this.upperBound.length != dim) {
                    throw new DimensionMismatchException(this.upperBound.length, dim);
                }
                for (int i2 = 0; i2 < dim; i2++) {
                    double v2 = this.start[i2];
                    double hi = this.upperBound[i2];
                    if (v2 > hi) {
                        throw new NumberIsTooLargeException(Double.valueOf(v2), Double.valueOf(hi), true);
                    }
                }
            }
            if (this.lowerBound == null) {
                this.lowerBound = new double[dim];
                for (int i3 = 0; i3 < dim; i3++) {
                    this.lowerBound[i3] = Double.NEGATIVE_INFINITY;
                }
            }
            if (this.upperBound == null) {
                this.upperBound = new double[dim];
                for (int i4 = 0; i4 < dim; i4++) {
                    this.upperBound[i4] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }
}
