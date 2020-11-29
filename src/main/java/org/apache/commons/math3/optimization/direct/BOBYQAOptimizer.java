package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.MultivariateOptimizer;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class BOBYQAOptimizer extends BaseAbstractMultivariateSimpleBoundsOptimizer<MultivariateFunction> implements MultivariateOptimizer {
    public static final double DEFAULT_INITIAL_RADIUS = 10.0d;
    public static final double DEFAULT_STOPPING_RADIUS = 1.0E-8d;
    private static final double HALF = 0.5d;
    public static final int MINIMUM_PROBLEM_DIMENSION = 2;
    private static final double MINUS_ONE = -1.0d;
    private static final double ONE = 1.0d;
    private static final double ONE_OVER_A_THOUSAND = 0.001d;
    private static final double ONE_OVER_EIGHT = 0.125d;
    private static final double ONE_OVER_FOUR = 0.25d;
    private static final double ONE_OVER_TEN = 0.1d;
    private static final double SIXTEEN = 16.0d;
    private static final double TEN = 10.0d;
    private static final double TWO = 2.0d;
    private static final double TWO_HUNDRED_FIFTY = 250.0d;
    private static final double ZERO = 0.0d;
    private ArrayRealVector alternativeNewPoint;
    private Array2DRowRealMatrix bMatrix;
    private double[] boundDifference;
    private ArrayRealVector currentBest;
    private ArrayRealVector fAtInterpolationPoints;
    private ArrayRealVector gradientAtTrustRegionCenter;
    private double initialTrustRegionRadius;
    private Array2DRowRealMatrix interpolationPoints;
    private boolean isMinimize;
    private ArrayRealVector lagrangeValuesAtNewPoint;
    private ArrayRealVector lowerDifference;
    private ArrayRealVector modelSecondDerivativesParameters;
    private ArrayRealVector modelSecondDerivativesValues;
    private ArrayRealVector newPoint;
    private final int numberOfInterpolationPoints;
    private ArrayRealVector originShift;
    private final double stoppingTrustRegionRadius;
    private ArrayRealVector trialStepPoint;
    private int trustRegionCenterInterpolationPointIndex;
    private ArrayRealVector trustRegionCenterOffset;
    private ArrayRealVector upperDifference;
    private Array2DRowRealMatrix zMatrix;

    public BOBYQAOptimizer(int numberOfInterpolationPoints2) {
        this(numberOfInterpolationPoints2, 10.0d, 1.0E-8d);
    }

    public BOBYQAOptimizer(int numberOfInterpolationPoints2, double initialTrustRegionRadius2, double stoppingTrustRegionRadius2) {
        super(null);
        this.numberOfInterpolationPoints = numberOfInterpolationPoints2;
        this.initialTrustRegionRadius = initialTrustRegionRadius2;
        this.stoppingTrustRegionRadius = stoppingTrustRegionRadius2;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer
    public PointValuePair doOptimize() {
        double[] lowerBound = getLowerBound();
        double[] upperBound = getUpperBound();
        setup(lowerBound, upperBound);
        this.isMinimize = getGoalType() == GoalType.MINIMIZE;
        this.currentBest = new ArrayRealVector(getStartPoint());
        double value = bobyqa(lowerBound, upperBound);
        double[] dataRef = this.currentBest.getDataRef();
        if (!this.isMinimize) {
            value = -value;
        }
        return new PointValuePair(dataRef, value);
    }

    private double bobyqa(double[] lowerBound, double[] upperBound) {
        printMethod();
        int n = this.currentBest.getDimension();
        for (int j = 0; j < n; j++) {
            double boundDiff = this.boundDifference[j];
            this.lowerDifference.setEntry(j, lowerBound[j] - this.currentBest.getEntry(j));
            this.upperDifference.setEntry(j, upperBound[j] - this.currentBest.getEntry(j));
            if (this.lowerDifference.getEntry(j) >= (-this.initialTrustRegionRadius)) {
                if (this.lowerDifference.getEntry(j) >= 0.0d) {
                    this.currentBest.setEntry(j, lowerBound[j]);
                    this.lowerDifference.setEntry(j, 0.0d);
                    this.upperDifference.setEntry(j, boundDiff);
                } else {
                    this.currentBest.setEntry(j, lowerBound[j] + this.initialTrustRegionRadius);
                    this.lowerDifference.setEntry(j, -this.initialTrustRegionRadius);
                    this.upperDifference.setEntry(j, FastMath.max(upperBound[j] - this.currentBest.getEntry(j), this.initialTrustRegionRadius));
                }
            } else if (this.upperDifference.getEntry(j) <= this.initialTrustRegionRadius) {
                if (this.upperDifference.getEntry(j) <= 0.0d) {
                    this.currentBest.setEntry(j, upperBound[j]);
                    this.lowerDifference.setEntry(j, -boundDiff);
                    this.upperDifference.setEntry(j, 0.0d);
                } else {
                    this.currentBest.setEntry(j, upperBound[j] - this.initialTrustRegionRadius);
                    this.lowerDifference.setEntry(j, FastMath.min(lowerBound[j] - this.currentBest.getEntry(j), -this.initialTrustRegionRadius));
                    this.upperDifference.setEntry(j, this.initialTrustRegionRadius);
                }
            }
        }
        return bobyqb(lowerBound, upperBound);
    }

    /* JADX WARNING: Removed duplicated region for block: B:130:0x0693  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0697  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x06e9  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x077b  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x07e5  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x08b9  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x091a  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x09c6  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x0a54  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0a5e  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0a64  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x020f  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x1157  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x118d  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x11cc  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x11fc  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x1242  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x02e5  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x02f7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private double bobyqb(double[] r151, double[] r152) {
        /*
        // Method dump skipped, instructions count: 4886
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.optimization.direct.BOBYQAOptimizer.bobyqb(double[], double[]):double");
    }

    private double[] altmov(int knew, double adelt) {
        double cauchy;
        double vlag;
        printMethod();
        int n = this.currentBest.getDimension();
        int npt = this.numberOfInterpolationPoints;
        ArrayRealVector glag = new ArrayRealVector(n);
        ArrayRealVector hcol = new ArrayRealVector(npt);
        ArrayRealVector work1 = new ArrayRealVector(n);
        ArrayRealVector work2 = new ArrayRealVector(n);
        for (int k = 0; k < npt; k++) {
            hcol.setEntry(k, 0.0d);
        }
        int max = (npt - n) - 1;
        for (int j = 0; j < max; j++) {
            double tmp = this.zMatrix.getEntry(knew, j);
            for (int k2 = 0; k2 < npt; k2++) {
                hcol.setEntry(k2, hcol.getEntry(k2) + (this.zMatrix.getEntry(k2, j) * tmp));
            }
        }
        double alpha = hcol.getEntry(knew);
        double ha = HALF * alpha;
        for (int i = 0; i < n; i++) {
            glag.setEntry(i, this.bMatrix.getEntry(knew, i));
        }
        for (int k3 = 0; k3 < npt; k3++) {
            double tmp2 = 0.0d;
            for (int j2 = 0; j2 < n; j2++) {
                tmp2 += this.interpolationPoints.getEntry(k3, j2) * this.trustRegionCenterOffset.getEntry(j2);
            }
            double tmp3 = tmp2 * hcol.getEntry(k3);
            for (int i2 = 0; i2 < n; i2++) {
                glag.setEntry(i2, glag.getEntry(i2) + (this.interpolationPoints.getEntry(k3, i2) * tmp3));
            }
        }
        double presav = 0.0d;
        double step = Double.NaN;
        int ksav = 0;
        int ibdsav = 0;
        double stpsav = 0.0d;
        for (int k4 = 0; k4 < npt; k4++) {
            if (k4 != this.trustRegionCenterInterpolationPointIndex) {
                double dderiv = 0.0d;
                double distsq = 0.0d;
                for (int i3 = 0; i3 < n; i3++) {
                    double tmp4 = this.interpolationPoints.getEntry(k4, i3) - this.trustRegionCenterOffset.getEntry(i3);
                    dderiv += glag.getEntry(i3) * tmp4;
                    distsq += tmp4 * tmp4;
                }
                double subd = adelt / FastMath.sqrt(distsq);
                double slbd = -subd;
                int ilbd = 0;
                int iubd = 0;
                double sumin = FastMath.min((double) ONE, subd);
                for (int i4 = 0; i4 < n; i4++) {
                    double tmp5 = this.interpolationPoints.getEntry(k4, i4) - this.trustRegionCenterOffset.getEntry(i4);
                    if (tmp5 > 0.0d) {
                        if (slbd * tmp5 < this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            slbd = (this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp5;
                            ilbd = (-i4) - 1;
                        }
                        if (subd * tmp5 > this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            subd = FastMath.max(sumin, (this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp5);
                            iubd = i4 + 1;
                        }
                    } else if (tmp5 < 0.0d) {
                        if (slbd * tmp5 > this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            slbd = (this.upperDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp5;
                            ilbd = i4 + 1;
                        }
                        if (subd * tmp5 < this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) {
                            subd = FastMath.max(sumin, (this.lowerDifference.getEntry(i4) - this.trustRegionCenterOffset.getEntry(i4)) / tmp5);
                            iubd = (-i4) - 1;
                        }
                    }
                }
                step = slbd;
                int isbd = ilbd;
                if (k4 == knew) {
                    double diff = dderiv - ONE;
                    vlag = slbd * (dderiv - (slbd * diff));
                    double d1 = subd * (dderiv - (subd * diff));
                    if (FastMath.abs(d1) > FastMath.abs(vlag)) {
                        step = subd;
                        vlag = d1;
                        isbd = iubd;
                    }
                    double d2 = HALF * dderiv;
                    if ((d2 - (diff * slbd)) * (d2 - (diff * subd)) < 0.0d) {
                        double d5 = (d2 * d2) / diff;
                        if (FastMath.abs(d5) > FastMath.abs(vlag)) {
                            step = d2 / diff;
                            vlag = d5;
                            isbd = 0;
                        }
                    }
                } else {
                    double vlag2 = slbd * (ONE - slbd);
                    double tmp6 = subd * (ONE - subd);
                    if (FastMath.abs(tmp6) > FastMath.abs(vlag2)) {
                        step = subd;
                        vlag2 = tmp6;
                        isbd = iubd;
                    }
                    if (subd > HALF && FastMath.abs(vlag2) < ONE_OVER_FOUR) {
                        step = HALF;
                        vlag2 = ONE_OVER_FOUR;
                        isbd = 0;
                    }
                    vlag = vlag2 * dderiv;
                }
                double tmp7 = (ONE - step) * step * distsq;
                double predsq = vlag * vlag * ((vlag * vlag) + (ha * tmp7 * tmp7));
                if (predsq > presav) {
                    presav = predsq;
                    ksav = k4;
                    stpsav = step;
                    ibdsav = isbd;
                }
            }
        }
        for (int i5 = 0; i5 < n; i5++) {
            this.newPoint.setEntry(i5, FastMath.max(this.lowerDifference.getEntry(i5), FastMath.min(this.upperDifference.getEntry(i5), this.trustRegionCenterOffset.getEntry(i5) + ((this.interpolationPoints.getEntry(ksav, i5) - this.trustRegionCenterOffset.getEntry(i5)) * stpsav))));
        }
        if (ibdsav < 0) {
            this.newPoint.setEntry((-ibdsav) - 1, this.lowerDifference.getEntry((-ibdsav) - 1));
        }
        if (ibdsav > 0) {
            this.newPoint.setEntry(ibdsav - 1, this.upperDifference.getEntry(ibdsav - 1));
        }
        double bigstp = adelt + adelt;
        int iflag = 0;
        double csave = 0.0d;
        while (true) {
            double wfixsq = 0.0d;
            double ggfree = 0.0d;
            for (int i6 = 0; i6 < n; i6++) {
                double glagValue = glag.getEntry(i6);
                work1.setEntry(i6, 0.0d);
                if (FastMath.min(this.trustRegionCenterOffset.getEntry(i6) - this.lowerDifference.getEntry(i6), glagValue) > 0.0d || FastMath.max(this.trustRegionCenterOffset.getEntry(i6) - this.upperDifference.getEntry(i6), glagValue) < 0.0d) {
                    work1.setEntry(i6, bigstp);
                    ggfree += glagValue * glagValue;
                }
            }
            if (ggfree == 0.0d) {
                return new double[]{alpha, 0.0d};
            }
            double tmp1 = (adelt * adelt) - 0.0d;
            if (tmp1 > 0.0d) {
                step = FastMath.sqrt(tmp1 / ggfree);
                double ggfree2 = 0.0d;
                for (int i7 = 0; i7 < n; i7++) {
                    if (work1.getEntry(i7) == bigstp) {
                        double tmp22 = this.trustRegionCenterOffset.getEntry(i7) - (glag.getEntry(i7) * step);
                        if (tmp22 <= this.lowerDifference.getEntry(i7)) {
                            work1.setEntry(i7, this.lowerDifference.getEntry(i7) - this.trustRegionCenterOffset.getEntry(i7));
                            double d12 = work1.getEntry(i7);
                            wfixsq += d12 * d12;
                        } else if (tmp22 >= this.upperDifference.getEntry(i7)) {
                            work1.setEntry(i7, this.upperDifference.getEntry(i7) - this.trustRegionCenterOffset.getEntry(i7));
                            double d13 = work1.getEntry(i7);
                            wfixsq += d13 * d13;
                        } else {
                            double d14 = glag.getEntry(i7);
                            ggfree2 += d14 * d14;
                        }
                    }
                }
            }
            double gw = 0.0d;
            for (int i8 = 0; i8 < n; i8++) {
                double glagValue2 = glag.getEntry(i8);
                if (work1.getEntry(i8) == bigstp) {
                    work1.setEntry(i8, (-step) * glagValue2);
                    this.alternativeNewPoint.setEntry(i8, FastMath.max(this.lowerDifference.getEntry(i8), FastMath.min(this.upperDifference.getEntry(i8), this.trustRegionCenterOffset.getEntry(i8) + work1.getEntry(i8))));
                } else if (work1.getEntry(i8) == 0.0d) {
                    this.alternativeNewPoint.setEntry(i8, this.trustRegionCenterOffset.getEntry(i8));
                } else if (glagValue2 > 0.0d) {
                    this.alternativeNewPoint.setEntry(i8, this.lowerDifference.getEntry(i8));
                } else {
                    this.alternativeNewPoint.setEntry(i8, this.upperDifference.getEntry(i8));
                }
                gw += work1.getEntry(i8) * glagValue2;
            }
            double curv = 0.0d;
            for (int k5 = 0; k5 < npt; k5++) {
                double tmp8 = 0.0d;
                for (int j3 = 0; j3 < n; j3++) {
                    tmp8 += this.interpolationPoints.getEntry(k5, j3) * work1.getEntry(j3);
                }
                curv += hcol.getEntry(k5) * tmp8 * tmp8;
            }
            if (iflag == 1) {
                curv = -curv;
            }
            if (curv <= (-gw) || curv >= (-gw) * (ONE + FastMath.sqrt(TWO))) {
                double d15 = gw + (HALF * curv);
                cauchy = d15 * d15;
            } else {
                double scale = (-gw) / curv;
                for (int i9 = 0; i9 < n; i9++) {
                    this.alternativeNewPoint.setEntry(i9, FastMath.max(this.lowerDifference.getEntry(i9), FastMath.min(this.upperDifference.getEntry(i9), this.trustRegionCenterOffset.getEntry(i9) + (work1.getEntry(i9) * scale))));
                }
                double d16 = HALF * gw * scale;
                cauchy = d16 * d16;
            }
            if (iflag == 0) {
                for (int i10 = 0; i10 < n; i10++) {
                    glag.setEntry(i10, -glag.getEntry(i10));
                    work2.setEntry(i10, this.alternativeNewPoint.getEntry(i10));
                }
                csave = cauchy;
                iflag = 1;
            } else {
                if (csave > cauchy) {
                    for (int i11 = 0; i11 < n; i11++) {
                        this.alternativeNewPoint.setEntry(i11, work2.getEntry(i11));
                    }
                    cauchy = csave;
                }
                return new double[]{alpha, cauchy};
            }
        }
    }

    private void prelim(double[] lowerBound, double[] upperBound) {
        printMethod();
        int n = this.currentBest.getDimension();
        int npt = this.numberOfInterpolationPoints;
        int ndim = this.bMatrix.getRowDimension();
        double rhosq = this.initialTrustRegionRadius * this.initialTrustRegionRadius;
        double recip = ONE / rhosq;
        int np = n + 1;
        for (int j = 0; j < n; j++) {
            this.originShift.setEntry(j, this.currentBest.getEntry(j));
            for (int k = 0; k < npt; k++) {
                this.interpolationPoints.setEntry(k, j, 0.0d);
            }
            for (int i = 0; i < ndim; i++) {
                this.bMatrix.setEntry(i, j, 0.0d);
            }
        }
        int max = (n * np) / 2;
        for (int i2 = 0; i2 < max; i2++) {
            this.modelSecondDerivativesValues.setEntry(i2, 0.0d);
        }
        for (int k2 = 0; k2 < npt; k2++) {
            this.modelSecondDerivativesParameters.setEntry(k2, 0.0d);
            int max2 = npt - np;
            for (int j2 = 0; j2 < max2; j2++) {
                this.zMatrix.setEntry(k2, j2, 0.0d);
            }
        }
        int ipt = 0;
        int jpt = 0;
        double fbeg = Double.NaN;
        do {
            int nfm = getEvaluations();
            int nfx = nfm - n;
            int nfmm = nfm - 1;
            int nfxm = nfx - 1;
            double stepa = 0.0d;
            double stepb = 0.0d;
            if (nfm > n * 2) {
                int tmp1 = (nfm - np) / n;
                jpt = (nfm - (tmp1 * n)) - n;
                ipt = jpt + tmp1;
                if (ipt > n) {
                    jpt = ipt - n;
                    ipt = jpt;
                }
                int iptMinus1 = ipt - 1;
                int jptMinus1 = jpt - 1;
                this.interpolationPoints.setEntry(nfm, iptMinus1, this.interpolationPoints.getEntry(ipt, iptMinus1));
                this.interpolationPoints.setEntry(nfm, jptMinus1, this.interpolationPoints.getEntry(jpt, jptMinus1));
            } else if (nfm >= 1 && nfm <= n) {
                stepa = this.initialTrustRegionRadius;
                if (this.upperDifference.getEntry(nfmm) == 0.0d) {
                    stepa = -stepa;
                }
                this.interpolationPoints.setEntry(nfm, nfmm, stepa);
            } else if (nfm > n) {
                stepa = this.interpolationPoints.getEntry(nfx, nfxm);
                stepb = -this.initialTrustRegionRadius;
                if (this.lowerDifference.getEntry(nfxm) == 0.0d) {
                    stepb = FastMath.min(TWO * this.initialTrustRegionRadius, this.upperDifference.getEntry(nfxm));
                }
                if (this.upperDifference.getEntry(nfxm) == 0.0d) {
                    stepb = FastMath.max(-2.0d * this.initialTrustRegionRadius, this.lowerDifference.getEntry(nfxm));
                }
                this.interpolationPoints.setEntry(nfm, nfxm, stepb);
            }
            for (int j3 = 0; j3 < n; j3++) {
                this.currentBest.setEntry(j3, FastMath.min(FastMath.max(lowerBound[j3], this.originShift.getEntry(j3) + this.interpolationPoints.getEntry(nfm, j3)), upperBound[j3]));
                if (this.interpolationPoints.getEntry(nfm, j3) == this.lowerDifference.getEntry(j3)) {
                    this.currentBest.setEntry(j3, lowerBound[j3]);
                }
                if (this.interpolationPoints.getEntry(nfm, j3) == this.upperDifference.getEntry(j3)) {
                    this.currentBest.setEntry(j3, upperBound[j3]);
                }
            }
            double objectiveValue = computeObjectiveValue(this.currentBest.toArray());
            double f = this.isMinimize ? objectiveValue : -objectiveValue;
            int numEval = getEvaluations();
            this.fAtInterpolationPoints.setEntry(nfm, f);
            if (numEval == 1) {
                fbeg = f;
                this.trustRegionCenterInterpolationPointIndex = 0;
            } else if (f < this.fAtInterpolationPoints.getEntry(this.trustRegionCenterInterpolationPointIndex)) {
                this.trustRegionCenterInterpolationPointIndex = nfm;
            }
            if (numEval > (n * 2) + 1) {
                this.zMatrix.setEntry(0, nfxm, recip);
                this.zMatrix.setEntry(nfm, nfxm, recip);
                this.zMatrix.setEntry(ipt, nfxm, -recip);
                this.zMatrix.setEntry(jpt, nfxm, -recip);
                this.modelSecondDerivativesValues.setEntry(((((ipt - 1) * ipt) / 2) + jpt) - 1, (((fbeg - this.fAtInterpolationPoints.getEntry(ipt)) - this.fAtInterpolationPoints.getEntry(jpt)) + f) / (this.interpolationPoints.getEntry(nfm, ipt - 1) * this.interpolationPoints.getEntry(nfm, jpt - 1)));
            } else if (numEval >= 2 && numEval <= n + 1) {
                this.gradientAtTrustRegionCenter.setEntry(nfmm, (f - fbeg) / stepa);
                if (npt < numEval + n) {
                    double oneOverStepA = ONE / stepa;
                    this.bMatrix.setEntry(0, nfmm, -oneOverStepA);
                    this.bMatrix.setEntry(nfm, nfmm, oneOverStepA);
                    this.bMatrix.setEntry(npt + nfmm, nfmm, -0.5d * rhosq);
                }
            } else if (numEval >= n + 2) {
                double tmp = (f - fbeg) / stepb;
                double diff = stepb - stepa;
                this.modelSecondDerivativesValues.setEntry((((nfx + 1) * nfx) / 2) - 1, (TWO * (tmp - this.gradientAtTrustRegionCenter.getEntry(nfxm))) / diff);
                this.gradientAtTrustRegionCenter.setEntry(nfxm, ((this.gradientAtTrustRegionCenter.getEntry(nfxm) * stepb) - (tmp * stepa)) / diff);
                if (stepa * stepb < 0.0d && f < this.fAtInterpolationPoints.getEntry(nfm - n)) {
                    this.fAtInterpolationPoints.setEntry(nfm, this.fAtInterpolationPoints.getEntry(nfm - n));
                    this.fAtInterpolationPoints.setEntry(nfm - n, f);
                    if (this.trustRegionCenterInterpolationPointIndex == nfm) {
                        this.trustRegionCenterInterpolationPointIndex = nfm - n;
                    }
                    this.interpolationPoints.setEntry(nfm - n, nfxm, stepb);
                    this.interpolationPoints.setEntry(nfm, nfxm, stepa);
                }
                this.bMatrix.setEntry(0, nfxm, (-(stepa + stepb)) / (stepa * stepb));
                this.bMatrix.setEntry(nfm, nfxm, -0.5d / this.interpolationPoints.getEntry(nfm - n, nfxm));
                this.bMatrix.setEntry(nfm - n, nfxm, (-this.bMatrix.getEntry(0, nfxm)) - this.bMatrix.getEntry(nfm, nfxm));
                this.zMatrix.setEntry(0, nfxm, FastMath.sqrt(TWO) / (stepa * stepb));
                this.zMatrix.setEntry(nfm, nfxm, FastMath.sqrt(HALF) / rhosq);
                this.zMatrix.setEntry(nfm - n, nfxm, (-this.zMatrix.getEntry(0, nfxm)) - this.zMatrix.getEntry(nfm, nfxm));
            }
        } while (getEvaluations() < npt);
    }

    /* JADX WARNING: Removed duplicated region for block: B:110:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0435  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x01c7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private double[] trsbox(double r102, org.apache.commons.math3.linear.ArrayRealVector r104, org.apache.commons.math3.linear.ArrayRealVector r105, org.apache.commons.math3.linear.ArrayRealVector r106, org.apache.commons.math3.linear.ArrayRealVector r107, org.apache.commons.math3.linear.ArrayRealVector r108) {
        /*
        // Method dump skipped, instructions count: 2798
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.optimization.direct.BOBYQAOptimizer.trsbox(double, org.apache.commons.math3.linear.ArrayRealVector, org.apache.commons.math3.linear.ArrayRealVector, org.apache.commons.math3.linear.ArrayRealVector, org.apache.commons.math3.linear.ArrayRealVector, org.apache.commons.math3.linear.ArrayRealVector):double[]");
    }

    private void update(double beta, double denom, int knew) {
        printMethod();
        int n = this.currentBest.getDimension();
        int npt = this.numberOfInterpolationPoints;
        int nptm = (npt - n) - 1;
        ArrayRealVector work = new ArrayRealVector(npt + n);
        double ztest = 0.0d;
        for (int k = 0; k < npt; k++) {
            for (int j = 0; j < nptm; j++) {
                ztest = FastMath.max(ztest, FastMath.abs(this.zMatrix.getEntry(k, j)));
            }
        }
        double ztest2 = ztest * 1.0E-20d;
        for (int j2 = 1; j2 < nptm; j2++) {
            if (FastMath.abs(this.zMatrix.getEntry(knew, j2)) > ztest2) {
                double d2 = this.zMatrix.getEntry(knew, 0);
                double d3 = this.zMatrix.getEntry(knew, j2);
                double d4 = FastMath.sqrt((d2 * d2) + (d3 * d3));
                double d5 = this.zMatrix.getEntry(knew, 0) / d4;
                double d6 = this.zMatrix.getEntry(knew, j2) / d4;
                for (int i = 0; i < npt; i++) {
                    double d7 = (this.zMatrix.getEntry(i, 0) * d5) + (this.zMatrix.getEntry(i, j2) * d6);
                    this.zMatrix.setEntry(i, j2, (this.zMatrix.getEntry(i, j2) * d5) - (this.zMatrix.getEntry(i, 0) * d6));
                    this.zMatrix.setEntry(i, 0, d7);
                }
            }
            this.zMatrix.setEntry(knew, j2, 0.0d);
        }
        for (int i2 = 0; i2 < npt; i2++) {
            work.setEntry(i2, this.zMatrix.getEntry(knew, 0) * this.zMatrix.getEntry(i2, 0));
        }
        double alpha = work.getEntry(knew);
        double tau = this.lagrangeValuesAtNewPoint.getEntry(knew);
        this.lagrangeValuesAtNewPoint.setEntry(knew, this.lagrangeValuesAtNewPoint.getEntry(knew) - ONE);
        double sqrtDenom = FastMath.sqrt(denom);
        double d1 = tau / sqrtDenom;
        double d22 = this.zMatrix.getEntry(knew, 0) / sqrtDenom;
        for (int i3 = 0; i3 < npt; i3++) {
            this.zMatrix.setEntry(i3, 0, (this.zMatrix.getEntry(i3, 0) * d1) - (this.lagrangeValuesAtNewPoint.getEntry(i3) * d22));
        }
        for (int j3 = 0; j3 < n; j3++) {
            int jp = npt + j3;
            work.setEntry(jp, this.bMatrix.getEntry(knew, j3));
            double d32 = ((this.lagrangeValuesAtNewPoint.getEntry(jp) * alpha) - (work.getEntry(jp) * tau)) / denom;
            double d42 = (((-beta) * work.getEntry(jp)) - (this.lagrangeValuesAtNewPoint.getEntry(jp) * tau)) / denom;
            for (int i4 = 0; i4 <= jp; i4++) {
                this.bMatrix.setEntry(i4, j3, this.bMatrix.getEntry(i4, j3) + (this.lagrangeValuesAtNewPoint.getEntry(i4) * d32) + (work.getEntry(i4) * d42));
                if (i4 >= npt) {
                    this.bMatrix.setEntry(jp, i4 - npt, this.bMatrix.getEntry(i4, j3));
                }
            }
        }
    }

    private void setup(double[] lowerBound, double[] upperBound) {
        printMethod();
        int dimension = getStartPoint().length;
        if (dimension < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(dimension), 2, true);
        }
        int[] nPointsInterval = {dimension + 2, ((dimension + 2) * (dimension + 1)) / 2};
        if (this.numberOfInterpolationPoints < nPointsInterval[0] || this.numberOfInterpolationPoints > nPointsInterval[1]) {
            throw new OutOfRangeException(LocalizedFormats.NUMBER_OF_INTERPOLATION_POINTS, Integer.valueOf(this.numberOfInterpolationPoints), Integer.valueOf(nPointsInterval[0]), Integer.valueOf(nPointsInterval[1]));
        }
        this.boundDifference = new double[dimension];
        double requiredMinDiff = TWO * this.initialTrustRegionRadius;
        double minDiff = Double.POSITIVE_INFINITY;
        for (int i = 0; i < dimension; i++) {
            this.boundDifference[i] = upperBound[i] - lowerBound[i];
            minDiff = FastMath.min(minDiff, this.boundDifference[i]);
        }
        if (minDiff < requiredMinDiff) {
            this.initialTrustRegionRadius = minDiff / 3.0d;
        }
        this.bMatrix = new Array2DRowRealMatrix(this.numberOfInterpolationPoints + dimension, dimension);
        this.zMatrix = new Array2DRowRealMatrix(this.numberOfInterpolationPoints, (this.numberOfInterpolationPoints - dimension) - 1);
        this.interpolationPoints = new Array2DRowRealMatrix(this.numberOfInterpolationPoints, dimension);
        this.originShift = new ArrayRealVector(dimension);
        this.fAtInterpolationPoints = new ArrayRealVector(this.numberOfInterpolationPoints);
        this.trustRegionCenterOffset = new ArrayRealVector(dimension);
        this.gradientAtTrustRegionCenter = new ArrayRealVector(dimension);
        this.lowerDifference = new ArrayRealVector(dimension);
        this.upperDifference = new ArrayRealVector(dimension);
        this.modelSecondDerivativesParameters = new ArrayRealVector(this.numberOfInterpolationPoints);
        this.newPoint = new ArrayRealVector(dimension);
        this.alternativeNewPoint = new ArrayRealVector(dimension);
        this.trialStepPoint = new ArrayRealVector(dimension);
        this.lagrangeValuesAtNewPoint = new ArrayRealVector(this.numberOfInterpolationPoints + dimension);
        this.modelSecondDerivativesValues = new ArrayRealVector(((dimension + 1) * dimension) / 2);
    }

    /* access modifiers changed from: private */
    public static String caller(int n) {
        StackTraceElement e = new Throwable().getStackTrace()[n];
        return e.getMethodName() + " (at line " + e.getLineNumber() + ")";
    }

    private static void printState(int s) {
    }

    private static void printMethod() {
    }

    private static class PathIsExploredException extends RuntimeException {
        private static final String PATH_IS_EXPLORED = "If this exception is thrown, just remove it from the code";
        private static final long serialVersionUID = 745350979634801853L;

        PathIsExploredException() {
            super("If this exception is thrown, just remove it from the code " + BOBYQAOptimizer.caller(3));
        }
    }
}
