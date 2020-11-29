package org.apache.commons.math3.ode.nonstiff;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.util.FastMath;

public abstract class EmbeddedRungeKuttaIntegrator extends AdaptiveStepsizeIntegrator {

    /* renamed from: a */
    private final double[][] f297a;

    /* renamed from: b */
    private final double[] f298b;

    /* renamed from: c */
    private final double[] f299c;
    private final double exp = (-1.0d / ((double) getOrder()));
    private final boolean fsal;
    private double maxGrowth;
    private double minReduction;
    private final RungeKuttaStepInterpolator prototype;
    private double safety;

    /* access modifiers changed from: protected */
    public abstract double estimateError(double[][] dArr, double[] dArr2, double[] dArr3, double d);

    public abstract int getOrder();

    protected EmbeddedRungeKuttaIntegrator(String name, boolean fsal2, double[] c, double[][] a, double[] b, RungeKuttaStepInterpolator prototype2, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
        super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.fsal = fsal2;
        this.f299c = c;
        this.f297a = a;
        this.f298b = b;
        this.prototype = prototype2;
        setSafety(0.9d);
        setMinReduction(0.2d);
        setMaxGrowth(10.0d);
    }

    protected EmbeddedRungeKuttaIntegrator(String name, boolean fsal2, double[] c, double[][] a, double[] b, RungeKuttaStepInterpolator prototype2, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
        super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.fsal = fsal2;
        this.f299c = c;
        this.f297a = a;
        this.f298b = b;
        this.prototype = prototype2;
        setSafety(0.9d);
        setMinReduction(0.2d);
        setMaxGrowth(10.0d);
    }

    public double getSafety() {
        return this.safety;
    }

    public void setSafety(double safety2) {
        this.safety = safety2;
    }

    @Override // org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator, org.apache.commons.math3.ode.AbstractIntegrator
    public void integrate(ExpandableStatefulODE equations, double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        sanityChecks(equations, t);
        setEquations(equations);
        boolean forward = t > equations.getTime();
        double[] y0 = equations.getCompleteState();
        double[] y = (double[]) y0.clone();
        int stages = this.f299c.length + 1;
        double[][] yDotK = (double[][]) Array.newInstance(Double.TYPE, stages, y.length);
        double[] yTmp = (double[]) y0.clone();
        double[] yDotTmp = new double[y.length];
        RungeKuttaStepInterpolator interpolator = (RungeKuttaStepInterpolator) this.prototype.copy();
        interpolator.reinitialize(this, yTmp, yDotK, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        interpolator.storeTime(equations.getTime());
        this.stepStart = equations.getTime();
        double hNew = 0.0d;
        boolean firstTime = true;
        initIntegration(equations.getTime(), y0, t);
        this.isLastStep = false;
        do {
            interpolator.shift();
            double error = 10.0d;
            while (error >= 1.0d) {
                if (firstTime || !this.fsal) {
                    computeDerivatives(this.stepStart, y, yDotK[0]);
                }
                if (firstTime) {
                    double[] scale = new double[this.mainSetDimension];
                    if (this.vecAbsoluteTolerance == null) {
                        for (int i = 0; i < scale.length; i++) {
                            scale[i] = this.scalAbsoluteTolerance + (this.scalRelativeTolerance * FastMath.abs(y[i]));
                        }
                    } else {
                        for (int i2 = 0; i2 < scale.length; i2++) {
                            scale[i2] = this.vecAbsoluteTolerance[i2] + (this.vecRelativeTolerance[i2] * FastMath.abs(y[i2]));
                        }
                    }
                    hNew = initializeStep(forward, getOrder(), scale, this.stepStart, y, yDotK[0], yTmp, yDotK[1]);
                    firstTime = false;
                }
                this.stepSize = hNew;
                if (forward) {
                    if (this.stepStart + this.stepSize >= t) {
                        this.stepSize = t - this.stepStart;
                    }
                } else if (this.stepStart + this.stepSize <= t) {
                    this.stepSize = t - this.stepStart;
                }
                for (int k = 1; k < stages; k++) {
                    for (int j = 0; j < y0.length; j++) {
                        double sum = this.f297a[k - 1][0] * yDotK[0][j];
                        for (int l = 1; l < k; l++) {
                            sum += this.f297a[k - 1][l] * yDotK[l][j];
                        }
                        yTmp[j] = y[j] + (this.stepSize * sum);
                    }
                    computeDerivatives(this.stepStart + (this.f299c[k - 1] * this.stepSize), yTmp, yDotK[k]);
                }
                for (int j2 = 0; j2 < y0.length; j2++) {
                    double sum2 = this.f298b[0] * yDotK[0][j2];
                    for (int l2 = 1; l2 < stages; l2++) {
                        sum2 += this.f298b[l2] * yDotK[l2][j2];
                    }
                    yTmp[j2] = y[j2] + (this.stepSize * sum2);
                }
                error = estimateError(yDotK, y, yTmp, this.stepSize);
                if (error >= 1.0d) {
                    hNew = filterStep(this.stepSize * FastMath.min(this.maxGrowth, FastMath.max(this.minReduction, this.safety * FastMath.pow(error, this.exp))), forward, false);
                }
            }
            interpolator.storeTime(this.stepStart + this.stepSize);
            System.arraycopy(yTmp, 0, y, 0, y0.length);
            System.arraycopy(yDotK[stages - 1], 0, yDotTmp, 0, y0.length);
            this.stepStart = acceptStep(interpolator, y, yDotTmp, t);
            System.arraycopy(y, 0, yTmp, 0, y.length);
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                if (this.fsal) {
                    System.arraycopy(yDotTmp, 0, yDotK[0], 0, y0.length);
                }
                double scaledH = this.stepSize * FastMath.min(this.maxGrowth, FastMath.max(this.minReduction, this.safety * FastMath.pow(error, this.exp)));
                double nextT = this.stepStart + scaledH;
                hNew = filterStep(scaledH, forward, forward ? nextT >= t : nextT <= t);
                double filteredNextT = this.stepStart + hNew;
                if (forward ? filteredNextT >= t : filteredNextT <= t) {
                    hNew = t - this.stepStart;
                }
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y);
        resetInternalState();
    }

    public double getMinReduction() {
        return this.minReduction;
    }

    public void setMinReduction(double minReduction2) {
        this.minReduction = minReduction2;
    }

    public double getMaxGrowth() {
        return this.maxGrowth;
    }

    public void setMaxGrowth(double maxGrowth2) {
        this.maxGrowth = maxGrowth2;
    }
}
