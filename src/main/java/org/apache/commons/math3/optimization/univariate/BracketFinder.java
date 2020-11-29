package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.Incrementor;

@Deprecated
public class BracketFinder {
    private static final double EPS_MIN = 1.0E-21d;
    private static final double GOLD = 1.618034d;
    private final Incrementor evaluations;
    private double fHi;
    private double fLo;
    private double fMid;
    private final double growLimit;

    /* renamed from: hi */
    private double f345hi;

    /* renamed from: lo */
    private double f346lo;
    private double mid;

    public BracketFinder() {
        this(100.0d, 50);
    }

    public BracketFinder(double growLimit2, int maxEvaluations) {
        this.evaluations = new Incrementor();
        if (growLimit2 <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(growLimit2));
        } else if (maxEvaluations <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(maxEvaluations));
        } else {
            this.growLimit = growLimit2;
            this.evaluations.setMaximalCount(maxEvaluations);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00a4, code lost:
        r38 = r40;
        r40 = r24;
        r6 = r8;
        r8 = r12;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0124  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x012d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void search(org.apache.commons.math3.analysis.UnivariateFunction r36, org.apache.commons.math3.optimization.GoalType r37, double r38, double r40) {
        /*
        // Method dump skipped, instructions count: 445
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.optimization.univariate.BracketFinder.search(org.apache.commons.math3.analysis.UnivariateFunction, org.apache.commons.math3.optimization.GoalType, double, double):void");
    }

    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }

    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    public double getLo() {
        return this.f346lo;
    }

    public double getFLo() {
        return this.fLo;
    }

    public double getHi() {
        return this.f345hi;
    }

    public double getFHi() {
        return this.fHi;
    }

    public double getMid() {
        return this.mid;
    }

    public double getFMid() {
        return this.fMid;
    }

    private double eval(UnivariateFunction f, double x) {
        try {
            this.evaluations.incrementCount();
            return f.value(x);
        } catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
    }
}
