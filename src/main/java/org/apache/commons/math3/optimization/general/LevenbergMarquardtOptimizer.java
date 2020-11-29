package org.apache.commons.math3.optimization.general;

import java.util.Arrays;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

@Deprecated
public class LevenbergMarquardtOptimizer extends AbstractLeastSquaresOptimizer {
    private double[] beta;
    private final double costRelativeTolerance;
    private double[] diagR;
    private final double initialStepBoundFactor;
    private double[] jacNorm;
    private double[] lmDir;
    private double lmPar;
    private final double orthoTolerance;
    private final double parRelativeTolerance;
    private int[] permutation;
    private final double qrRankingThreshold;
    private int rank;
    private int solvedCols;
    private double[][] weightedJacobian;
    private double[] weightedResidual;

    public LevenbergMarquardtOptimizer() {
        this(100.0d, 1.0E-10d, 1.0E-10d, 1.0E-10d, Precision.SAFE_MIN);
    }

    public LevenbergMarquardtOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
        this(100.0d, checker, 1.0E-10d, 1.0E-10d, 1.0E-10d, Precision.SAFE_MIN);
    }

    public LevenbergMarquardtOptimizer(double initialStepBoundFactor2, ConvergenceChecker<PointVectorValuePair> checker, double costRelativeTolerance2, double parRelativeTolerance2, double orthoTolerance2, double threshold) {
        super(checker);
        this.initialStepBoundFactor = initialStepBoundFactor2;
        this.costRelativeTolerance = costRelativeTolerance2;
        this.parRelativeTolerance = parRelativeTolerance2;
        this.orthoTolerance = orthoTolerance2;
        this.qrRankingThreshold = threshold;
    }

    public LevenbergMarquardtOptimizer(double costRelativeTolerance2, double parRelativeTolerance2, double orthoTolerance2) {
        this(100.0d, costRelativeTolerance2, parRelativeTolerance2, orthoTolerance2, Precision.SAFE_MIN);
    }

    public LevenbergMarquardtOptimizer(double initialStepBoundFactor2, double costRelativeTolerance2, double parRelativeTolerance2, double orthoTolerance2, double threshold) {
        super(null);
        this.initialStepBoundFactor = initialStepBoundFactor2;
        this.costRelativeTolerance = costRelativeTolerance2;
        this.parRelativeTolerance = parRelativeTolerance2;
        this.orthoTolerance = orthoTolerance2;
        this.qrRankingThreshold = threshold;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateVectorOptimizer
    public PointVectorValuePair doOptimize() {
        int nR = getTarget().length;
        double[] currentPoint = getStartPoint();
        int nC = currentPoint.length;
        this.solvedCols = FastMath.min(nR, nC);
        this.diagR = new double[nC];
        this.jacNorm = new double[nC];
        this.beta = new double[nC];
        this.permutation = new int[nC];
        this.lmDir = new double[nC];
        double delta = 0.0d;
        double xNorm = 0.0d;
        double[] diag = new double[nC];
        double[] oldX = new double[nC];
        double[] oldRes = new double[nR];
        double[] oldObj = new double[nR];
        double[] qtf = new double[nR];
        double[] work1 = new double[nC];
        double[] work2 = new double[nC];
        double[] work3 = new double[nC];
        RealMatrix weightMatrixSqrt = getWeightSquareRoot();
        double[] currentObjective = computeObjectiveValue(currentPoint);
        double[] currentResiduals = computeResiduals(currentObjective);
        PointVectorValuePair current = new PointVectorValuePair(currentPoint, currentObjective);
        double currentCost = computeCost(currentResiduals);
        this.lmPar = 0.0d;
        boolean firstIteration = true;
        int iter = 0;
        ConvergenceChecker<PointVectorValuePair> checker = getConvergenceChecker();
        loop0:
        while (true) {
            iter++;
            qrDecomposition(computeWeightedJacobian(currentPoint));
            this.weightedResidual = weightMatrixSqrt.operate(currentResiduals);
            for (int i = 0; i < nR; i++) {
                qtf[i] = this.weightedResidual[i];
            }
            qTy(qtf);
            for (int k = 0; k < this.solvedCols; k++) {
                int pk = this.permutation[k];
                this.weightedJacobian[k][pk] = this.diagR[pk];
            }
            if (firstIteration) {
                double xNorm2 = 0.0d;
                for (int k2 = 0; k2 < nC; k2++) {
                    double dk = this.jacNorm[k2];
                    if (dk == 0.0d) {
                        dk = 1.0d;
                    }
                    double xk = dk * currentPoint[k2];
                    xNorm2 += xk * xk;
                    diag[k2] = dk;
                }
                xNorm = FastMath.sqrt(xNorm2);
                if (xNorm == 0.0d) {
                    delta = this.initialStepBoundFactor;
                } else {
                    delta = this.initialStepBoundFactor * xNorm;
                }
            }
            double maxCosine = 0.0d;
            if (currentCost != 0.0d) {
                for (int j = 0; j < this.solvedCols; j++) {
                    int pj = this.permutation[j];
                    double s = this.jacNorm[pj];
                    if (s != 0.0d) {
                        double sum = 0.0d;
                        for (int i2 = 0; i2 <= j; i2++) {
                            sum += this.weightedJacobian[i2][pj] * qtf[i2];
                        }
                        maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * currentCost));
                    }
                }
            }
            if (maxCosine <= this.orthoTolerance) {
                setCost(currentCost);
                this.point = current.getPoint();
                return current;
            }
            for (int j2 = 0; j2 < nC; j2++) {
                diag[j2] = FastMath.max(diag[j2], this.jacNorm[j2]);
            }
            double ratio = 0.0d;
            while (true) {
                if (ratio < 1.0E-4d) {
                    for (int j3 = 0; j3 < this.solvedCols; j3++) {
                        int pj2 = this.permutation[j3];
                        oldX[pj2] = currentPoint[pj2];
                    }
                    double[] tmpVec = this.weightedResidual;
                    this.weightedResidual = oldRes;
                    oldRes = tmpVec;
                    oldObj = currentObjective;
                    determineLMParameter(qtf, delta, diag, work1, work2, work3);
                    double lmNorm = 0.0d;
                    for (int j4 = 0; j4 < this.solvedCols; j4++) {
                        int pj3 = this.permutation[j4];
                        this.lmDir[pj3] = -this.lmDir[pj3];
                        currentPoint[pj3] = oldX[pj3] + this.lmDir[pj3];
                        double s2 = diag[pj3] * this.lmDir[pj3];
                        lmNorm += s2 * s2;
                    }
                    double lmNorm2 = FastMath.sqrt(lmNorm);
                    if (firstIteration) {
                        delta = FastMath.min(delta, lmNorm2);
                    }
                    currentObjective = computeObjectiveValue(currentPoint);
                    currentResiduals = computeResiduals(currentObjective);
                    current = new PointVectorValuePair(currentPoint, currentObjective);
                    currentCost = computeCost(currentResiduals);
                    double actRed = -1.0d;
                    if (0.1d * currentCost < currentCost) {
                        double r = currentCost / currentCost;
                        actRed = 1.0d - (r * r);
                    }
                    for (int j5 = 0; j5 < this.solvedCols; j5++) {
                        int pj4 = this.permutation[j5];
                        double dirJ = this.lmDir[pj4];
                        work1[j5] = 0.0d;
                        for (int i3 = 0; i3 <= j5; i3++) {
                            work1[i3] = work1[i3] + (this.weightedJacobian[i3][pj4] * dirJ);
                        }
                    }
                    double coeff1 = 0.0d;
                    for (int j6 = 0; j6 < this.solvedCols; j6++) {
                        coeff1 += work1[j6] * work1[j6];
                    }
                    double pc2 = currentCost * currentCost;
                    double coeff12 = coeff1 / pc2;
                    double coeff2 = ((this.lmPar * lmNorm2) * lmNorm2) / pc2;
                    double preRed = coeff12 + (2.0d * coeff2);
                    double dirDer = -(coeff12 + coeff2);
                    ratio = preRed == 0.0d ? 0.0d : actRed / preRed;
                    if (ratio <= 0.25d) {
                        double tmp = actRed < 0.0d ? (0.5d * dirDer) / ((0.5d * actRed) + dirDer) : 0.5d;
                        if (0.1d * currentCost >= currentCost || tmp < 0.1d) {
                            tmp = 0.1d;
                        }
                        delta = tmp * FastMath.min(delta, 10.0d * lmNorm2);
                        this.lmPar /= tmp;
                    } else if (this.lmPar == 0.0d || ratio >= 0.75d) {
                        delta = 2.0d * lmNorm2;
                        this.lmPar *= 0.5d;
                    }
                    if (ratio >= 1.0E-4d) {
                        firstIteration = false;
                        double xNorm3 = 0.0d;
                        for (int k3 = 0; k3 < nC; k3++) {
                            double xK = diag[k3] * currentPoint[k3];
                            xNorm3 += xK * xK;
                        }
                        xNorm = FastMath.sqrt(xNorm3);
                        if (checker != null && checker.converged(iter, current, current)) {
                            setCost(currentCost);
                            this.point = current.getPoint();
                            return current;
                        }
                    } else {
                        currentCost = currentCost;
                        for (int j7 = 0; j7 < this.solvedCols; j7++) {
                            int pj5 = this.permutation[j7];
                            currentPoint[pj5] = oldX[pj5];
                        }
                        double[] tmpVec2 = this.weightedResidual;
                        this.weightedResidual = oldRes;
                        oldRes = tmpVec2;
                        currentObjective = oldObj;
                        oldObj = currentObjective;
                        current = new PointVectorValuePair(currentPoint, currentObjective);
                    }
                    if ((FastMath.abs(actRed) > this.costRelativeTolerance || preRed > this.costRelativeTolerance || ratio > 2.0d) && delta > this.parRelativeTolerance * xNorm) {
                        if (FastMath.abs(actRed) <= 2.2204E-16d && preRed <= 2.2204E-16d && ratio <= 2.0d) {
                            throw new ConvergenceException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE, Double.valueOf(this.costRelativeTolerance));
                        } else if (delta <= 2.2204E-16d * xNorm) {
                            throw new ConvergenceException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, Double.valueOf(this.parRelativeTolerance));
                        } else if (maxCosine <= 2.2204E-16d) {
                            throw new ConvergenceException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE, Double.valueOf(this.orthoTolerance));
                        }
                    }
                }
            }
        }
        setCost(currentCost);
        this.point = current.getPoint();
        return current;
    }

    private void determineLMParameter(double[] qy, double delta, double[] diag, double[] work1, double[] work2, double[] work3) {
        int nC = this.weightedJacobian[0].length;
        for (int j = 0; j < this.rank; j++) {
            this.lmDir[this.permutation[j]] = qy[j];
        }
        for (int j2 = this.rank; j2 < nC; j2++) {
            this.lmDir[this.permutation[j2]] = 0.0d;
        }
        for (int k = this.rank - 1; k >= 0; k--) {
            int pk = this.permutation[k];
            double ypk = this.lmDir[pk] / this.diagR[pk];
            for (int i = 0; i < k; i++) {
                double[] dArr = this.lmDir;
                int i2 = this.permutation[i];
                dArr[i2] = dArr[i2] - (this.weightedJacobian[i][pk] * ypk);
            }
            this.lmDir[pk] = ypk;
        }
        double dxNorm = 0.0d;
        for (int j3 = 0; j3 < this.solvedCols; j3++) {
            int pj = this.permutation[j3];
            double s = diag[pj] * this.lmDir[pj];
            work1[pj] = s;
            dxNorm += s * s;
        }
        double dxNorm2 = FastMath.sqrt(dxNorm);
        double fp = dxNorm2 - delta;
        if (fp <= 0.1d * delta) {
            this.lmPar = 0.0d;
            return;
        }
        double parl = 0.0d;
        if (this.rank == this.solvedCols) {
            for (int j4 = 0; j4 < this.solvedCols; j4++) {
                int pj2 = this.permutation[j4];
                work1[pj2] = work1[pj2] * (diag[pj2] / dxNorm2);
            }
            double sum2 = 0.0d;
            for (int j5 = 0; j5 < this.solvedCols; j5++) {
                int pj3 = this.permutation[j5];
                double sum = 0.0d;
                for (int i3 = 0; i3 < j5; i3++) {
                    sum += this.weightedJacobian[i3][pj3] * work1[this.permutation[i3]];
                }
                double s2 = (work1[pj3] - sum) / this.diagR[pj3];
                work1[pj3] = s2;
                sum2 += s2 * s2;
            }
            parl = fp / (delta * sum2);
        }
        double sum22 = 0.0d;
        for (int j6 = 0; j6 < this.solvedCols; j6++) {
            int pj4 = this.permutation[j6];
            double sum3 = 0.0d;
            for (int i4 = 0; i4 <= j6; i4++) {
                sum3 += this.weightedJacobian[i4][pj4] * qy[i4];
            }
            double sum4 = sum3 / diag[pj4];
            sum22 += sum4 * sum4;
        }
        double gNorm = FastMath.sqrt(sum22);
        double paru = gNorm / delta;
        if (paru == 0.0d) {
            paru = 2.2251E-308d / FastMath.min(delta, 0.1d);
        }
        this.lmPar = FastMath.min(paru, FastMath.max(this.lmPar, parl));
        if (this.lmPar == 0.0d) {
            this.lmPar = gNorm / dxNorm2;
        }
        for (int countdown = 10; countdown >= 0; countdown--) {
            if (this.lmPar == 0.0d) {
                this.lmPar = FastMath.max(2.2251E-308d, 0.001d * paru);
            }
            double sPar = FastMath.sqrt(this.lmPar);
            for (int j7 = 0; j7 < this.solvedCols; j7++) {
                int pj5 = this.permutation[j7];
                work1[pj5] = diag[pj5] * sPar;
            }
            determineLMDirection(qy, work1, work2, work3);
            double dxNorm3 = 0.0d;
            for (int j8 = 0; j8 < this.solvedCols; j8++) {
                int pj6 = this.permutation[j8];
                double s3 = diag[pj6] * this.lmDir[pj6];
                work3[pj6] = s3;
                dxNorm3 += s3 * s3;
            }
            double dxNorm4 = FastMath.sqrt(dxNorm3);
            fp = dxNorm4 - delta;
            if (FastMath.abs(fp) <= 0.1d * delta) {
                return;
            }
            if (parl != 0.0d || fp > fp || fp >= 0.0d) {
                for (int j9 = 0; j9 < this.solvedCols; j9++) {
                    int pj7 = this.permutation[j9];
                    work1[pj7] = (work3[pj7] * diag[pj7]) / dxNorm4;
                }
                for (int j10 = 0; j10 < this.solvedCols; j10++) {
                    int pj8 = this.permutation[j10];
                    work1[pj8] = work1[pj8] / work2[j10];
                    double tmp = work1[pj8];
                    for (int i5 = j10 + 1; i5 < this.solvedCols; i5++) {
                        int i6 = this.permutation[i5];
                        work1[i6] = work1[i6] - (this.weightedJacobian[i5][pj8] * tmp);
                    }
                }
                double sum23 = 0.0d;
                for (int j11 = 0; j11 < this.solvedCols; j11++) {
                    double s4 = work1[this.permutation[j11]];
                    sum23 += s4 * s4;
                }
                double correction = fp / (delta * sum23);
                if (fp > 0.0d) {
                    parl = FastMath.max(parl, this.lmPar);
                } else if (fp < 0.0d) {
                    paru = FastMath.min(paru, this.lmPar);
                }
                this.lmPar = FastMath.max(parl, this.lmPar + correction);
            } else {
                return;
            }
        }
    }

    private void determineLMDirection(double[] qy, double[] diag, double[] lmDiag, double[] work) {
        double cos;
        double sin;
        for (int j = 0; j < this.solvedCols; j++) {
            int pj = this.permutation[j];
            for (int i = j + 1; i < this.solvedCols; i++) {
                this.weightedJacobian[i][pj] = this.weightedJacobian[j][this.permutation[i]];
            }
            this.lmDir[j] = this.diagR[pj];
            work[j] = qy[j];
        }
        for (int j2 = 0; j2 < this.solvedCols; j2++) {
            double dpj = diag[this.permutation[j2]];
            if (dpj != 0.0d) {
                Arrays.fill(lmDiag, j2 + 1, lmDiag.length, 0.0d);
            }
            lmDiag[j2] = dpj;
            double qtbpj = 0.0d;
            for (int k = j2; k < this.solvedCols; k++) {
                int pk = this.permutation[k];
                if (lmDiag[k] != 0.0d) {
                    double rkk = this.weightedJacobian[k][pk];
                    if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
                        double cotan = rkk / lmDiag[k];
                        sin = 1.0d / FastMath.sqrt(1.0d + (cotan * cotan));
                        cos = sin * cotan;
                    } else {
                        double tan = lmDiag[k] / rkk;
                        cos = 1.0d / FastMath.sqrt(1.0d + (tan * tan));
                        sin = cos * tan;
                    }
                    this.weightedJacobian[k][pk] = (cos * rkk) + (lmDiag[k] * sin);
                    double temp = (work[k] * cos) + (sin * qtbpj);
                    qtbpj = ((-sin) * work[k]) + (cos * qtbpj);
                    work[k] = temp;
                    for (int i2 = k + 1; i2 < this.solvedCols; i2++) {
                        double rik = this.weightedJacobian[i2][pk];
                        double temp2 = (cos * rik) + (lmDiag[i2] * sin);
                        lmDiag[i2] = ((-sin) * rik) + (lmDiag[i2] * cos);
                        this.weightedJacobian[i2][pk] = temp2;
                    }
                }
            }
            lmDiag[j2] = this.weightedJacobian[j2][this.permutation[j2]];
            this.weightedJacobian[j2][this.permutation[j2]] = this.lmDir[j2];
        }
        int nSing = this.solvedCols;
        for (int j3 = 0; j3 < this.solvedCols; j3++) {
            if (lmDiag[j3] == 0.0d && nSing == this.solvedCols) {
                nSing = j3;
            }
            if (nSing < this.solvedCols) {
                work[j3] = 0.0d;
            }
        }
        if (nSing > 0) {
            for (int j4 = nSing - 1; j4 >= 0; j4--) {
                int pj2 = this.permutation[j4];
                double sum = 0.0d;
                for (int i3 = j4 + 1; i3 < nSing; i3++) {
                    sum += this.weightedJacobian[i3][pj2] * work[i3];
                }
                work[j4] = (work[j4] - sum) / lmDiag[j4];
            }
        }
        for (int j5 = 0; j5 < this.lmDir.length; j5++) {
            this.lmDir[this.permutation[j5]] = work[j5];
        }
    }

    private void qrDecomposition(RealMatrix jacobian) throws ConvergenceException {
        this.weightedJacobian = jacobian.scalarMultiply(-1.0d).getData();
        int nR = this.weightedJacobian.length;
        int nC = this.weightedJacobian[0].length;
        for (int k = 0; k < nC; k++) {
            this.permutation[k] = k;
            double norm2 = 0.0d;
            for (int i = 0; i < nR; i++) {
                double akk = this.weightedJacobian[i][k];
                norm2 += akk * akk;
            }
            this.jacNorm[k] = FastMath.sqrt(norm2);
        }
        for (int k2 = 0; k2 < nC; k2++) {
            int nextColumn = -1;
            double ak2 = Double.NEGATIVE_INFINITY;
            for (int i2 = k2; i2 < nC; i2++) {
                double norm22 = 0.0d;
                for (int j = k2; j < nR; j++) {
                    double aki = this.weightedJacobian[j][this.permutation[i2]];
                    norm22 += aki * aki;
                }
                if (Double.isInfinite(norm22) || Double.isNaN(norm22)) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, Integer.valueOf(nR), Integer.valueOf(nC));
                }
                if (norm22 > ak2) {
                    nextColumn = i2;
                    ak2 = norm22;
                }
            }
            if (ak2 <= this.qrRankingThreshold) {
                this.rank = k2;
                return;
            }
            int pk = this.permutation[nextColumn];
            this.permutation[nextColumn] = this.permutation[k2];
            this.permutation[k2] = pk;
            double akk2 = this.weightedJacobian[k2][pk];
            double alpha = akk2 > 0.0d ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
            double betak = 1.0d / (ak2 - (akk2 * alpha));
            this.beta[pk] = betak;
            this.diagR[pk] = alpha;
            double[] dArr = this.weightedJacobian[k2];
            dArr[pk] = dArr[pk] - alpha;
            for (int dk = (nC - 1) - k2; dk > 0; dk--) {
                double gamma = 0.0d;
                for (int j2 = k2; j2 < nR; j2++) {
                    gamma += this.weightedJacobian[j2][pk] * this.weightedJacobian[j2][this.permutation[k2 + dk]];
                }
                double gamma2 = gamma * betak;
                for (int j3 = k2; j3 < nR; j3++) {
                    double[] dArr2 = this.weightedJacobian[j3];
                    int i3 = this.permutation[k2 + dk];
                    dArr2[i3] = dArr2[i3] - (this.weightedJacobian[j3][pk] * gamma2);
                }
            }
        }
        this.rank = this.solvedCols;
    }

    private void qTy(double[] y) {
        int nR = this.weightedJacobian.length;
        int nC = this.weightedJacobian[0].length;
        for (int k = 0; k < nC; k++) {
            int pk = this.permutation[k];
            double gamma = 0.0d;
            for (int i = k; i < nR; i++) {
                gamma += this.weightedJacobian[i][pk] * y[i];
            }
            double gamma2 = gamma * this.beta[pk];
            for (int i2 = k; i2 < nR; i2++) {
                y[i2] = y[i2] - (this.weightedJacobian[i2][pk] * gamma2);
            }
        }
    }
}
