package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.util.FastMath;

public abstract class RungeKuttaIntegrator extends AbstractIntegrator {

    /* renamed from: a */
    private final double[][] f307a;

    /* renamed from: b */
    private final double[] f308b;

    /* renamed from: c */
    private final double[] f309c;
    private final RungeKuttaStepInterpolator prototype;
    private final double step;

    protected RungeKuttaIntegrator(String name, double[] c, double[][] a, double[] b, RungeKuttaStepInterpolator prototype2, double step2) {
        super(name);
        this.f309c = c;
        this.f307a = a;
        this.f308b = b;
        this.prototype = prototype2;
        this.step = FastMath.abs(step2);
    }

    @Override // org.apache.commons.math3.ode.AbstractIntegrator
    public void integrate(ExpandableStatefulODE equations, double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        sanityChecks(equations, t);
        setEquations(equations);
        boolean forward = t > equations.getTime();
        double[] y0 = equations.getCompleteState();
        double[] y = (double[]) y0.clone();
        int stages = this.f309c.length + 1;
        double[][] yDotK = new double[stages][];
        for (int i = 0; i < stages; i++) {
            yDotK[i] = new double[y0.length];
        }
        double[] yTmp = (double[]) y0.clone();
        double[] yDotTmp = new double[y0.length];
        RungeKuttaStepInterpolator interpolator = (RungeKuttaStepInterpolator) this.prototype.copy();
        interpolator.reinitialize(this, yTmp, yDotK, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        interpolator.storeTime(equations.getTime());
        this.stepStart = equations.getTime();
        if (forward) {
            if (this.stepStart + this.step >= t) {
                this.stepSize = t - this.stepStart;
            } else {
                this.stepSize = this.step;
            }
        } else if (this.stepStart - this.step <= t) {
            this.stepSize = t - this.stepStart;
        } else {
            this.stepSize = -this.step;
        }
        initIntegration(equations.getTime(), y0, t);
        this.isLastStep = false;
        do {
            interpolator.shift();
            computeDerivatives(this.stepStart, y, yDotK[0]);
            for (int k = 1; k < stages; k++) {
                for (int j = 0; j < y0.length; j++) {
                    double sum = this.f307a[k - 1][0] * yDotK[0][j];
                    for (int l = 1; l < k; l++) {
                        sum += this.f307a[k - 1][l] * yDotK[l][j];
                    }
                    yTmp[j] = y[j] + (this.stepSize * sum);
                }
                computeDerivatives(this.stepStart + (this.f309c[k - 1] * this.stepSize), yTmp, yDotK[k]);
            }
            for (int j2 = 0; j2 < y0.length; j2++) {
                double sum2 = this.f308b[0] * yDotK[0][j2];
                for (int l2 = 1; l2 < stages; l2++) {
                    sum2 += this.f308b[l2] * yDotK[l2][j2];
                }
                yTmp[j2] = y[j2] + (this.stepSize * sum2);
            }
            interpolator.storeTime(this.stepStart + this.stepSize);
            System.arraycopy(yTmp, 0, y, 0, y0.length);
            System.arraycopy(yDotK[stages - 1], 0, yDotTmp, 0, y0.length);
            this.stepStart = acceptStep(interpolator, y, yDotTmp, t);
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                double nextT = this.stepStart + this.stepSize;
                if (forward ? nextT >= t : nextT <= t) {
                    this.stepSize = t - this.stepStart;
                }
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y);
        this.stepStart = Double.NaN;
        this.stepSize = Double.NaN;
    }

    public double[] singleStep(FirstOrderDifferentialEquations equations, double t0, double[] y0, double t) {
        double[] y = (double[]) y0.clone();
        int stages = this.f309c.length + 1;
        double[][] yDotK = new double[stages][];
        for (int i = 0; i < stages; i++) {
            yDotK[i] = new double[y0.length];
        }
        double[] yTmp = (double[]) y0.clone();
        double h = t - t0;
        equations.computeDerivatives(t0, y, yDotK[0]);
        for (int k = 1; k < stages; k++) {
            for (int j = 0; j < y0.length; j++) {
                double sum = this.f307a[k - 1][0] * yDotK[0][j];
                for (int l = 1; l < k; l++) {
                    sum += this.f307a[k - 1][l] * yDotK[l][j];
                }
                yTmp[j] = y[j] + (h * sum);
            }
            equations.computeDerivatives((this.f309c[k - 1] * h) + t0, yTmp, yDotK[k]);
        }
        for (int j2 = 0; j2 < y0.length; j2++) {
            double sum2 = this.f308b[0] * yDotK[0][j2];
            for (int l2 = 1; l2 < stages; l2++) {
                sum2 += this.f308b[l2] * yDotK[l2][j2];
            }
            y[j2] = y[j2] + (h * sum2);
        }
        return y;
    }
}
