package org.apache.commons.math3.ode.nonstiff;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.FastMath;

public class GraggBulirschStoerIntegrator extends AdaptiveStepsizeIntegrator {
    private static final String METHOD_NAME = "Gragg-Bulirsch-Stoer";
    private double[][] coeff;
    private int[] costPerStep;
    private double[] costPerTimeUnit;
    private int maxChecks;
    private int maxIter;
    private int maxOrder;
    private int mudif;
    private double[] optimalStep;
    private double orderControl1;
    private double orderControl2;
    private boolean performTest;
    private int[] sequence;
    private double stabilityReduction;
    private double stepControl1;
    private double stepControl2;
    private double stepControl3;
    private double stepControl4;
    private boolean useInterpolationError;

    public GraggBulirschStoerIntegrator(double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
        super(METHOD_NAME, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        setStabilityCheck(true, -1, -1, -1.0d);
        setControlFactors(-1.0d, -1.0d, -1.0d, -1.0d);
        setOrderControl(-1, -1.0d, -1.0d);
        setInterpolationControl(true, -1);
    }

    public GraggBulirschStoerIntegrator(double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
        super(METHOD_NAME, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        setStabilityCheck(true, -1, -1, -1.0d);
        setControlFactors(-1.0d, -1.0d, -1.0d, -1.0d);
        setOrderControl(-1, -1.0d, -1.0d);
        setInterpolationControl(true, -1);
    }

    public void setStabilityCheck(boolean performStabilityCheck, int maxNumIter, int maxNumChecks, double stepsizeReductionFactor) {
        this.performTest = performStabilityCheck;
        if (maxNumIter <= 0) {
            maxNumIter = 2;
        }
        this.maxIter = maxNumIter;
        if (maxNumChecks <= 0) {
            maxNumChecks = 1;
        }
        this.maxChecks = maxNumChecks;
        if (stepsizeReductionFactor < 1.0E-4d || stepsizeReductionFactor > 0.9999d) {
            this.stabilityReduction = 0.5d;
        } else {
            this.stabilityReduction = stepsizeReductionFactor;
        }
    }

    public void setControlFactors(double control1, double control2, double control3, double control4) {
        if (control1 < 1.0E-4d || control1 > 0.9999d) {
            this.stepControl1 = 0.65d;
        } else {
            this.stepControl1 = control1;
        }
        if (control2 < 1.0E-4d || control2 > 0.9999d) {
            this.stepControl2 = 0.94d;
        } else {
            this.stepControl2 = control2;
        }
        if (control3 < 1.0E-4d || control3 > 0.9999d) {
            this.stepControl3 = 0.02d;
        } else {
            this.stepControl3 = control3;
        }
        if (control4 < 1.0001d || control4 > 999.9d) {
            this.stepControl4 = 4.0d;
        } else {
            this.stepControl4 = control4;
        }
    }

    public void setOrderControl(int maximalOrder, double control1, double control2) {
        if (maximalOrder <= 6 || maximalOrder % 2 != 0) {
            this.maxOrder = 18;
        }
        if (control1 < 1.0E-4d || control1 > 0.9999d) {
            this.orderControl1 = 0.8d;
        } else {
            this.orderControl1 = control1;
        }
        if (control2 < 1.0E-4d || control2 > 0.9999d) {
            this.orderControl2 = 0.9d;
        } else {
            this.orderControl2 = control2;
        }
        initializeArrays();
    }

    @Override // org.apache.commons.math3.ode.AbstractIntegrator, org.apache.commons.math3.ode.ODEIntegrator
    public void addStepHandler(StepHandler handler) {
        super.addStepHandler(handler);
        initializeArrays();
    }

    @Override // org.apache.commons.math3.ode.AbstractIntegrator, org.apache.commons.math3.ode.ODEIntegrator
    public void addEventHandler(EventHandler function, double maxCheckInterval, double convergence, int maxIterationCount, UnivariateSolver solver) {
        super.addEventHandler(function, maxCheckInterval, convergence, maxIterationCount, solver);
        initializeArrays();
    }

    private void initializeArrays() {
        int size = this.maxOrder / 2;
        if (this.sequence == null || this.sequence.length != size) {
            this.sequence = new int[size];
            this.costPerStep = new int[size];
            this.coeff = new double[size][];
            this.costPerTimeUnit = new double[size];
            this.optimalStep = new double[size];
        }
        for (int k = 0; k < size; k++) {
            this.sequence[k] = (k * 4) + 2;
        }
        this.costPerStep[0] = this.sequence[0] + 1;
        for (int k2 = 1; k2 < size; k2++) {
            this.costPerStep[k2] = this.costPerStep[k2 - 1] + this.sequence[k2];
        }
        int k3 = 0;
        while (k3 < size) {
            this.coeff[k3] = k3 > 0 ? new double[k3] : null;
            for (int l = 0; l < k3; l++) {
                double ratio = ((double) this.sequence[k3]) / ((double) this.sequence[(k3 - l) - 1]);
                this.coeff[k3][l] = 1.0d / ((ratio * ratio) - 1.0d);
            }
            k3++;
        }
    }

    public void setInterpolationControl(boolean useInterpolationErrorForControl, int mudifControlParameter) {
        this.useInterpolationError = useInterpolationErrorForControl;
        if (mudifControlParameter <= 0 || mudifControlParameter >= 7) {
            this.mudif = 4;
        } else {
            this.mudif = mudifControlParameter;
        }
    }

    private void rescale(double[] y1, double[] y2, double[] scale) {
        if (this.vecAbsoluteTolerance == null) {
            for (int i = 0; i < scale.length; i++) {
                scale[i] = this.scalAbsoluteTolerance + (this.scalRelativeTolerance * FastMath.max(FastMath.abs(y1[i]), FastMath.abs(y2[i])));
            }
            return;
        }
        for (int i2 = 0; i2 < scale.length; i2++) {
            scale[i2] = this.vecAbsoluteTolerance[i2] + (this.vecRelativeTolerance[i2] * FastMath.max(FastMath.abs(y1[i2]), FastMath.abs(y2[i2])));
        }
    }

    private boolean tryStep(double t0, double[] y0, double step, int k, double[] scale, double[][] f, double[] yMiddle, double[] yEnd, double[] yTmp) throws MaxCountExceededException, DimensionMismatchException {
        int n = this.sequence[k];
        double subStep = step / ((double) n);
        double subStep2 = 2.0d * subStep;
        double t = t0 + subStep;
        for (int i = 0; i < y0.length; i++) {
            yTmp[i] = y0[i];
            yEnd[i] = y0[i] + (f[0][i] * subStep);
        }
        computeDerivatives(t, yEnd, f[1]);
        for (int j = 1; j < n; j++) {
            if (j * 2 == n) {
                System.arraycopy(yEnd, 0, yMiddle, 0, y0.length);
            }
            t += subStep;
            for (int i2 = 0; i2 < y0.length; i2++) {
                double middle = yEnd[i2];
                yEnd[i2] = yTmp[i2] + (f[j][i2] * subStep2);
                yTmp[i2] = middle;
            }
            computeDerivatives(t, yEnd, f[j + 1]);
            if (this.performTest && j <= this.maxChecks && k < this.maxIter) {
                double initialNorm = 0.0d;
                for (int l = 0; l < scale.length; l++) {
                    double ratio = f[0][l] / scale[l];
                    initialNorm += ratio * ratio;
                }
                double deltaNorm = 0.0d;
                for (int l2 = 0; l2 < scale.length; l2++) {
                    double ratio2 = (f[j + 1][l2] - f[0][l2]) / scale[l2];
                    deltaNorm += ratio2 * ratio2;
                }
                if (deltaNorm > 4.0d * FastMath.max(1.0E-15d, initialNorm)) {
                    return false;
                }
            }
        }
        for (int i3 = 0; i3 < y0.length; i3++) {
            yEnd[i3] = 0.5d * (yTmp[i3] + yEnd[i3] + (f[n][i3] * subStep));
        }
        return true;
    }

    private void extrapolate(int offset, int k, double[][] diag, double[] last) {
        for (int j = 1; j < k; j++) {
            for (int i = 0; i < last.length; i++) {
                diag[(k - j) - 1][i] = diag[k - j][i] + (this.coeff[k + offset][j - 1] * (diag[k - j][i] - diag[(k - j) - 1][i]));
            }
        }
        for (int i2 = 0; i2 < last.length; i2++) {
            last[i2] = diag[0][i2] + (this.coeff[k + offset][k - 1] * (diag[0][i2] - last[i2]));
        }
    }

    @Override // org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator, org.apache.commons.math3.ode.AbstractIntegrator
    public void integrate(ExpandableStatefulODE equations, double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        double tol;
        int optimalIter;
        double[] dArr;
        double[] dArr2;
        sanityChecks(equations, t);
        setEquations(equations);
        boolean forward = t > equations.getTime();
        double[] y0 = equations.getCompleteState();
        double[] y = (double[]) y0.clone();
        double[] yDot0 = new double[y.length];
        double[] y1 = new double[y.length];
        double[] yTmp = new double[y.length];
        double[] yTmpDot = new double[y.length];
        double[][] diagonal = new double[(this.sequence.length - 1)][];
        double[][] y1Diag = new double[(this.sequence.length - 1)][];
        for (int k = 0; k < this.sequence.length - 1; k++) {
            diagonal[k] = new double[y.length];
            y1Diag[k] = new double[y.length];
        }
        double[][][] fk = new double[this.sequence.length][][];
        for (int k2 = 0; k2 < this.sequence.length; k2++) {
            fk[k2] = new double[(this.sequence[k2] + 1)][];
            fk[k2][0] = yDot0;
            for (int l = 0; l < this.sequence[k2]; l++) {
                fk[k2][l + 1] = new double[y0.length];
            }
        }
        if (y != y0) {
            System.arraycopy(y0, 0, y, 0, y0.length);
        }
        double[] yDot1 = new double[y0.length];
        double[][] yMidDots = (double[][]) Array.newInstance(Double.TYPE, (this.sequence.length * 2) + 1, y0.length);
        double[] scale = new double[this.mainSetDimension];
        rescale(y, y, scale);
        if (this.vecRelativeTolerance == null) {
            tol = this.scalRelativeTolerance;
        } else {
            tol = this.vecRelativeTolerance[0];
        }
        int targetIter = FastMath.max(1, FastMath.min(this.sequence.length - 2, (int) FastMath.floor(0.5d - (0.6d * FastMath.log10(FastMath.max(1.0E-10d, tol))))));
        AbstractStepInterpolator interpolator = new GraggBulirschStoerStepInterpolator(y, yDot0, y1, yDot1, yMidDots, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        interpolator.storeTime(equations.getTime());
        this.stepStart = equations.getTime();
        double hNew = 0.0d;
        double maxError = Double.MAX_VALUE;
        boolean previousRejected = false;
        boolean firstTime = true;
        boolean newStep = true;
        boolean firstStepAlreadyComputed = false;
        initIntegration(equations.getTime(), y0, t);
        this.costPerTimeUnit[0] = 0.0d;
        this.isLastStep = false;
        do {
            boolean reject = false;
            if (newStep) {
                interpolator.shift();
                if (!firstStepAlreadyComputed) {
                    computeDerivatives(this.stepStart, y, yDot0);
                }
                if (firstTime) {
                    hNew = initializeStep(forward, (targetIter * 2) + 1, scale, this.stepStart, y, yDot0, yTmp, yTmpDot);
                }
                newStep = false;
            }
            this.stepSize = hNew;
            if ((forward && this.stepStart + this.stepSize > t) || (!forward && this.stepStart + this.stepSize < t)) {
                this.stepSize = t - this.stepStart;
            }
            double nextT = this.stepStart + this.stepSize;
            this.isLastStep = forward ? nextT >= t : nextT <= t;
            int k3 = -1;
            boolean loop = true;
            while (loop) {
                k3++;
                double d = this.stepStart;
                double d2 = this.stepSize;
                double[][] dArr3 = fk[k3];
                if (k3 == 0) {
                    dArr = yMidDots[0];
                } else {
                    dArr = diagonal[k3 - 1];
                }
                if (k3 == 0) {
                    dArr2 = y1;
                } else {
                    dArr2 = y1Diag[k3 - 1];
                }
                if (!tryStep(d, y, d2, k3, scale, dArr3, dArr, dArr2, yTmp)) {
                    hNew = FastMath.abs(filterStep(this.stepSize * this.stabilityReduction, forward, false));
                    reject = true;
                    loop = false;
                } else if (k3 > 0) {
                    extrapolate(0, k3, y1Diag, y1);
                    rescale(y, y1, scale);
                    double error = 0.0d;
                    for (int j = 0; j < this.mainSetDimension; j++) {
                        double e = FastMath.abs(y1[j] - y1Diag[0][j]) / scale[j];
                        error += e * e;
                    }
                    double error2 = FastMath.sqrt(error / ((double) this.mainSetDimension));
                    if (error2 <= 1.0E15d && (k3 <= 1 || error2 <= maxError)) {
                        maxError = FastMath.max(4.0d * error2, 1.0d);
                        double exp = 1.0d / ((double) ((k3 * 2) + 1));
                        double fac = this.stepControl2 / FastMath.pow(error2 / this.stepControl1, exp);
                        double pow = FastMath.pow(this.stepControl3, exp);
                        this.optimalStep[k3] = FastMath.abs(filterStep(this.stepSize * FastMath.max(pow / this.stepControl4, FastMath.min(1.0d / pow, fac)), forward, true));
                        this.costPerTimeUnit[k3] = ((double) this.costPerStep[k3]) / this.optimalStep[k3];
                        switch (k3 - targetIter) {
                            case DescriptiveStatistics.INFINITE_WINDOW:
                                if (targetIter > 1 && !previousRejected) {
                                    if (error2 <= 1.0d) {
                                        loop = false;
                                        break;
                                    } else {
                                        double ratio = (((double) this.sequence[targetIter]) * ((double) this.sequence[targetIter + 1])) / ((double) (this.sequence[0] * this.sequence[0]));
                                        if (error2 > ratio * ratio) {
                                            reject = true;
                                            loop = false;
                                            targetIter = k3;
                                            if (targetIter > 1 && this.costPerTimeUnit[targetIter - 1] < this.orderControl1 * this.costPerTimeUnit[targetIter]) {
                                                targetIter--;
                                            }
                                            hNew = this.optimalStep[targetIter];
                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            case 0:
                                if (error2 <= 1.0d) {
                                    loop = false;
                                    break;
                                } else {
                                    double ratio2 = ((double) this.sequence[k3 + 1]) / ((double) this.sequence[0]);
                                    if (error2 > ratio2 * ratio2) {
                                        reject = true;
                                        loop = false;
                                        if (targetIter > 1 && this.costPerTimeUnit[targetIter - 1] < this.orderControl1 * this.costPerTimeUnit[targetIter]) {
                                            targetIter--;
                                        }
                                        hNew = this.optimalStep[targetIter];
                                        break;
                                    } else {
                                        break;
                                    }
                                }
                            case 1:
                                if (error2 > 1.0d) {
                                    reject = true;
                                    if (targetIter > 1 && this.costPerTimeUnit[targetIter - 1] < this.orderControl1 * this.costPerTimeUnit[targetIter]) {
                                        targetIter--;
                                    }
                                    hNew = this.optimalStep[targetIter];
                                }
                                loop = false;
                                break;
                            default:
                                if ((firstTime || this.isLastStep) && error2 <= 1.0d) {
                                    loop = false;
                                    break;
                                }
                        }
                    } else {
                        hNew = FastMath.abs(filterStep(this.stepSize * this.stabilityReduction, forward, false));
                        reject = true;
                        loop = false;
                    }
                }
            }
            if (!reject) {
                computeDerivatives(this.stepStart + this.stepSize, y1, yDot1);
            }
            double hInt = getMaxStep();
            if (!reject) {
                for (int j2 = 1; j2 <= k3; j2++) {
                    extrapolate(0, j2, diagonal, yMidDots[0]);
                }
                int mu = ((k3 * 2) - this.mudif) + 3;
                for (int l2 = 0; l2 < mu; l2++) {
                    int l22 = l2 / 2;
                    double factor = FastMath.pow(0.5d * ((double) this.sequence[l22]), l2);
                    int middleIndex = fk[l22].length / 2;
                    for (int i = 0; i < y0.length; i++) {
                        yMidDots[l2 + 1][i] = fk[l22][middleIndex + l2][i] * factor;
                    }
                    for (int j3 = 1; j3 <= k3 - l22; j3++) {
                        double factor2 = FastMath.pow(0.5d * ((double) this.sequence[j3 + l22]), l2);
                        int middleIndex2 = fk[l22 + j3].length / 2;
                        for (int i2 = 0; i2 < y0.length; i2++) {
                            diagonal[j3 - 1][i2] = fk[l22 + j3][middleIndex2 + l2][i2] * factor2;
                        }
                        extrapolate(l22, j3, diagonal, yMidDots[l2 + 1]);
                    }
                    for (int i3 = 0; i3 < y0.length; i3++) {
                        double[] dArr4 = yMidDots[l2 + 1];
                        dArr4[i3] = dArr4[i3] * this.stepSize;
                    }
                    for (int j4 = (l2 + 1) / 2; j4 <= k3; j4++) {
                        for (int m = fk[j4].length - 1; m >= (l2 + 1) * 2; m--) {
                            for (int i4 = 0; i4 < y0.length; i4++) {
                                double[] dArr5 = fk[j4][m];
                                dArr5[i4] = dArr5[i4] - fk[j4][m - 2][i4];
                            }
                        }
                    }
                }
                if (mu >= 0) {
                    GraggBulirschStoerStepInterpolator gbsInterpolator = (GraggBulirschStoerStepInterpolator) interpolator;
                    gbsInterpolator.computeCoefficients(mu, this.stepSize);
                    if (this.useInterpolationError) {
                        double interpError = gbsInterpolator.estimateError(scale);
                        hInt = FastMath.abs(this.stepSize / FastMath.max(FastMath.pow(interpError, 1.0d / ((double) (mu + 4))), 0.01d));
                        if (interpError > 10.0d) {
                            hNew = hInt;
                            reject = true;
                        }
                    }
                }
            }
            if (!reject) {
                interpolator.storeTime(this.stepStart + this.stepSize);
                this.stepStart = acceptStep(interpolator, y1, yDot1, t);
                interpolator.storeTime(this.stepStart);
                System.arraycopy(y1, 0, y, 0, y0.length);
                System.arraycopy(yDot1, 0, yDot0, 0, y0.length);
                firstStepAlreadyComputed = true;
                if (k3 == 1) {
                    optimalIter = 2;
                    if (previousRejected) {
                        optimalIter = 1;
                    }
                } else if (k3 <= targetIter) {
                    optimalIter = k3;
                    if (this.costPerTimeUnit[k3 - 1] < this.orderControl1 * this.costPerTimeUnit[k3]) {
                        optimalIter = k3 - 1;
                    } else if (this.costPerTimeUnit[k3] < this.orderControl2 * this.costPerTimeUnit[k3 - 1]) {
                        optimalIter = FastMath.min(k3 + 1, this.sequence.length - 2);
                    }
                } else {
                    optimalIter = k3 - 1;
                    if (k3 > 2 && this.costPerTimeUnit[k3 - 2] < this.orderControl1 * this.costPerTimeUnit[k3 - 1]) {
                        optimalIter = k3 - 2;
                    }
                    if (this.costPerTimeUnit[k3] < this.orderControl2 * this.costPerTimeUnit[optimalIter]) {
                        optimalIter = FastMath.min(k3, this.sequence.length - 2);
                    }
                }
                if (previousRejected) {
                    targetIter = FastMath.min(optimalIter, k3);
                    hNew = FastMath.min(FastMath.abs(this.stepSize), this.optimalStep[targetIter]);
                } else {
                    if (optimalIter <= k3) {
                        hNew = this.optimalStep[optimalIter];
                    } else if (k3 >= targetIter || this.costPerTimeUnit[k3] >= this.orderControl2 * this.costPerTimeUnit[k3 - 1]) {
                        hNew = filterStep((this.optimalStep[k3] * ((double) this.costPerStep[optimalIter])) / ((double) this.costPerStep[k3]), forward, false);
                    } else {
                        hNew = filterStep((this.optimalStep[k3] * ((double) this.costPerStep[optimalIter + 1])) / ((double) this.costPerStep[k3]), forward, false);
                    }
                    targetIter = optimalIter;
                }
                newStep = true;
            }
            hNew = FastMath.min(hNew, hInt);
            if (!forward) {
                hNew = -hNew;
            }
            firstTime = false;
            if (reject) {
                this.isLastStep = false;
                previousRejected = true;
            } else {
                previousRejected = false;
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y);
        resetInternalState();
    }
}
