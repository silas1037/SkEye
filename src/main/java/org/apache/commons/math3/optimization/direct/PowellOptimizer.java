package org.apache.commons.math3.optimization.direct;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.MultivariateOptimizer;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.univariate.BracketFinder;
import org.apache.commons.math3.optimization.univariate.BrentOptimizer;
import org.apache.commons.math3.optimization.univariate.SimpleUnivariateValueChecker;
import org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class PowellOptimizer extends BaseAbstractMultivariateOptimizer<MultivariateFunction> implements MultivariateOptimizer {
    private static final double MIN_RELATIVE_TOLERANCE = (2.0d * FastMath.ulp(1.0d));
    private final double absoluteThreshold;
    private final LineSearch line;
    private final double relativeThreshold;

    public PowellOptimizer(double rel, double abs, ConvergenceChecker<PointValuePair> checker) {
        this(rel, abs, FastMath.sqrt(rel), FastMath.sqrt(abs), checker);
    }

    public PowellOptimizer(double rel, double abs, double lineRel, double lineAbs, ConvergenceChecker<PointValuePair> checker) {
        super(checker);
        if (rel < MIN_RELATIVE_TOLERANCE) {
            throw new NumberIsTooSmallException(Double.valueOf(rel), Double.valueOf(MIN_RELATIVE_TOLERANCE), true);
        } else if (abs <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(abs));
        } else {
            this.relativeThreshold = rel;
            this.absoluteThreshold = abs;
            this.line = new LineSearch(lineRel, lineAbs);
        }
    }

    public PowellOptimizer(double rel, double abs) {
        this(rel, abs, null);
    }

    public PowellOptimizer(double rel, double abs, double lineRel, double lineAbs) {
        this(rel, abs, lineRel, lineAbs, null);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer
    public PointValuePair doOptimize() {
        PointValuePair previous;
        PointValuePair current;
        GoalType goal = getGoalType();
        double[] guess = getStartPoint();
        int n = guess.length;
        double[][] direc = (double[][]) Array.newInstance(Double.TYPE, n, n);
        for (int i = 0; i < n; i++) {
            direc[i][i] = 1.0d;
        }
        ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        double[] x = guess;
        double fVal = computeObjectiveValue(x);
        double[] x1 = (double[]) x.clone();
        int iter = 0;
        while (true) {
            iter++;
            double delta = 0.0d;
            int bigInd = 0;
            for (int i2 = 0; i2 < n; i2++) {
                double[] d = MathArrays.copyOf(direc[i2]);
                UnivariatePointValuePair optimum = this.line.search(x, d);
                fVal = optimum.getValue();
                x = newPointAndDirection(x, d, optimum.getPoint())[0];
                if (fVal - fVal > delta) {
                    delta = fVal - fVal;
                    bigInd = i2;
                }
            }
            boolean stop = 2.0d * (fVal - fVal) <= (this.relativeThreshold * (FastMath.abs(fVal) + FastMath.abs(fVal))) + this.absoluteThreshold;
            previous = new PointValuePair(x1, fVal);
            current = new PointValuePair(x, fVal);
            if (!stop && checker != null) {
                stop = checker.converged(iter, previous, current);
            }
            if (stop) {
                break;
            }
            double[] d2 = new double[n];
            double[] x2 = new double[n];
            for (int i3 = 0; i3 < n; i3++) {
                d2[i3] = x[i3] - x1[i3];
                x2[i3] = (2.0d * x[i3]) - x1[i3];
            }
            x1 = (double[]) x.clone();
            double fX2 = computeObjectiveValue(x2);
            if (fVal > fX2) {
                double temp = (fVal - fVal) - delta;
                double temp2 = fVal - fX2;
                if (((2.0d * ((fVal + fX2) - (2.0d * fVal))) * (temp * temp)) - ((delta * temp2) * temp2) < 0.0d) {
                    UnivariatePointValuePair optimum2 = this.line.search(x, d2);
                    fVal = optimum2.getValue();
                    double[][] result = newPointAndDirection(x, d2, optimum2.getPoint());
                    x = result[0];
                    int lastInd = n - 1;
                    direc[bigInd] = direc[lastInd];
                    direc[lastInd] = result[1];
                }
            }
        }
        return goal == GoalType.MINIMIZE ? fVal < fVal ? current : previous : fVal <= fVal ? previous : current;
    }

    private double[][] newPointAndDirection(double[] p, double[] d, double optimum) {
        int n = p.length;
        double[] nP = new double[n];
        double[] nD = new double[n];
        for (int i = 0; i < n; i++) {
            nD[i] = d[i] * optimum;
            nP[i] = p[i] + nD[i];
        }
        return new double[][]{nP, nD};
    }

    private class LineSearch extends BrentOptimizer {
        private static final double ABS_TOL_UNUSED = Double.MIN_VALUE;
        private static final double REL_TOL_UNUSED = 1.0E-15d;
        private final BracketFinder bracket = new BracketFinder();

        LineSearch(double rel, double abs) {
            super(1.0E-15d, ABS_TOL_UNUSED, new SimpleUnivariateValueChecker(rel, abs));
        }

        public UnivariatePointValuePair search(final double[] p, final double[] d) {
            final int n = p.length;
            UnivariateFunction f = new UnivariateFunction() {
                /* class org.apache.commons.math3.optimization.direct.PowellOptimizer.LineSearch.C03171 */

                @Override // org.apache.commons.math3.analysis.UnivariateFunction
                public double value(double alpha) {
                    double[] x = new double[n];
                    for (int i = 0; i < n; i++) {
                        x[i] = p[i] + (d[i] * alpha);
                    }
                    return PowellOptimizer.this.computeObjectiveValue(x);
                }
            };
            GoalType goal = PowellOptimizer.this.getGoalType();
            this.bracket.search(f, goal, 0.0d, 1.0d);
            return optimize(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, f, goal, this.bracket.getLo(), this.bracket.getHi(), this.bracket.getMid());
        }
    }
}
