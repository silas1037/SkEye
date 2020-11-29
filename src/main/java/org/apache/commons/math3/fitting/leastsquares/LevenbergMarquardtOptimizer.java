package org.apache.commons.math3.fitting.leastsquares;

import java.util.Arrays;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.Precision;

public class LevenbergMarquardtOptimizer implements LeastSquaresOptimizer {
    private static final double TWO_EPS = (2.0d * Precision.EPSILON);
    private final double costRelativeTolerance;
    private final double initialStepBoundFactor;
    private final double orthoTolerance;
    private final double parRelativeTolerance;
    private final double qrRankingThreshold;

    public LevenbergMarquardtOptimizer() {
        this(100.0d, 1.0E-10d, 1.0E-10d, 1.0E-10d, Precision.SAFE_MIN);
    }

    public LevenbergMarquardtOptimizer(double initialStepBoundFactor2, double costRelativeTolerance2, double parRelativeTolerance2, double orthoTolerance2, double qrRankingThreshold2) {
        this.initialStepBoundFactor = initialStepBoundFactor2;
        this.costRelativeTolerance = costRelativeTolerance2;
        this.parRelativeTolerance = parRelativeTolerance2;
        this.orthoTolerance = orthoTolerance2;
        this.qrRankingThreshold = qrRankingThreshold2;
    }

    public LevenbergMarquardtOptimizer withInitialStepBoundFactor(double newInitialStepBoundFactor) {
        return new LevenbergMarquardtOptimizer(newInitialStepBoundFactor, this.costRelativeTolerance, this.parRelativeTolerance, this.orthoTolerance, this.qrRankingThreshold);
    }

    public LevenbergMarquardtOptimizer withCostRelativeTolerance(double newCostRelativeTolerance) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, newCostRelativeTolerance, this.parRelativeTolerance, this.orthoTolerance, this.qrRankingThreshold);
    }

    public LevenbergMarquardtOptimizer withParameterRelativeTolerance(double newParRelativeTolerance) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, this.costRelativeTolerance, newParRelativeTolerance, this.orthoTolerance, this.qrRankingThreshold);
    }

    public LevenbergMarquardtOptimizer withOrthoTolerance(double newOrthoTolerance) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, this.costRelativeTolerance, this.parRelativeTolerance, newOrthoTolerance, this.qrRankingThreshold);
    }

    public LevenbergMarquardtOptimizer withRankingThreshold(double newQRRankingThreshold) {
        return new LevenbergMarquardtOptimizer(this.initialStepBoundFactor, this.costRelativeTolerance, this.parRelativeTolerance, this.orthoTolerance, newQRRankingThreshold);
    }

    public double getInitialStepBoundFactor() {
        return this.initialStepBoundFactor;
    }

    public double getCostRelativeTolerance() {
        return this.costRelativeTolerance;
    }

    public double getParameterRelativeTolerance() {
        return this.parRelativeTolerance;
    }

    public double getOrthoTolerance() {
        return this.orthoTolerance;
    }

    public double getRankingThreshold() {
        return this.qrRankingThreshold;
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer
    public LeastSquaresOptimizer.Optimum optimize(LeastSquaresProblem problem) {
        int nR = problem.getObservationSize();
        int nC = problem.getParameterSize();
        Incrementor iterationCounter = problem.getIterationCounter();
        Incrementor evaluationCounter = problem.getEvaluationCounter();
        ConvergenceChecker<LeastSquaresProblem.Evaluation> checker = problem.getConvergenceChecker();
        int solvedCols = FastMath.min(nR, nC);
        double[] lmDir = new double[nC];
        double lmPar = 0.0d;
        double delta = 0.0d;
        double xNorm = 0.0d;
        double[] diag = new double[nC];
        double[] oldX = new double[nC];
        double[] oldRes = new double[nR];
        double[] qtf = new double[nR];
        double[] work1 = new double[nC];
        double[] work2 = new double[nC];
        double[] work3 = new double[nC];
        evaluationCounter.incrementCount();
        LeastSquaresProblem.Evaluation current = problem.evaluate(problem.getStart());
        double[] currentResiduals = current.getResiduals().toArray();
        double currentCost = current.getCost();
        double[] currentPoint = current.getPoint().toArray();
        boolean firstIteration = true;
        loop0:
        while (true) {
            iterationCounter.incrementCount();
            InternalData internalData = qrDecomposition(current.getJacobian(), solvedCols);
            double[][] weightedJacobian = internalData.weightedJacobian;
            int[] permutation = internalData.permutation;
            double[] diagR = internalData.diagR;
            double[] jacNorm = internalData.jacNorm;
            double[] weightedResidual = currentResiduals;
            for (int i = 0; i < nR; i++) {
                qtf[i] = weightedResidual[i];
            }
            qTy(qtf, internalData);
            for (int k = 0; k < solvedCols; k++) {
                int pk = permutation[k];
                weightedJacobian[k][pk] = diagR[pk];
            }
            if (firstIteration) {
                double xNorm2 = 0.0d;
                for (int k2 = 0; k2 < nC; k2++) {
                    double dk = jacNorm[k2];
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
                for (int j = 0; j < solvedCols; j++) {
                    int pj = permutation[j];
                    double s = jacNorm[pj];
                    if (s != 0.0d) {
                        double sum = 0.0d;
                        for (int i2 = 0; i2 <= j; i2++) {
                            sum += weightedJacobian[i2][pj] * qtf[i2];
                        }
                        maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * currentCost));
                    }
                }
            }
            if (maxCosine <= this.orthoTolerance) {
                return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
            }
            for (int j2 = 0; j2 < nC; j2++) {
                diag[j2] = FastMath.max(diag[j2], jacNorm[j2]);
            }
            double ratio = 0.0d;
            while (true) {
                if (ratio < 1.0E-4d) {
                    for (int j3 = 0; j3 < solvedCols; j3++) {
                        int pj2 = permutation[j3];
                        oldX[pj2] = currentPoint[pj2];
                    }
                    weightedResidual = oldRes;
                    oldRes = weightedResidual;
                    lmPar = determineLMParameter(qtf, delta, diag, internalData, solvedCols, work1, work2, work3, lmDir, lmPar);
                    double lmNorm = 0.0d;
                    for (int j4 = 0; j4 < solvedCols; j4++) {
                        int pj3 = permutation[j4];
                        lmDir[pj3] = -lmDir[pj3];
                        currentPoint[pj3] = oldX[pj3] + lmDir[pj3];
                        double s2 = diag[pj3] * lmDir[pj3];
                        lmNorm += s2 * s2;
                    }
                    double lmNorm2 = FastMath.sqrt(lmNorm);
                    if (firstIteration) {
                        delta = FastMath.min(delta, lmNorm2);
                    }
                    evaluationCounter.incrementCount();
                    current = problem.evaluate(new ArrayRealVector(currentPoint));
                    currentResiduals = current.getResiduals().toArray();
                    currentCost = current.getCost();
                    currentPoint = current.getPoint().toArray();
                    double actRed = -1.0d;
                    if (0.1d * currentCost < currentCost) {
                        double r = currentCost / currentCost;
                        actRed = 1.0d - (r * r);
                    }
                    for (int j5 = 0; j5 < solvedCols; j5++) {
                        int pj4 = permutation[j5];
                        double dirJ = lmDir[pj4];
                        work1[j5] = 0.0d;
                        for (int i3 = 0; i3 <= j5; i3++) {
                            work1[i3] = work1[i3] + (weightedJacobian[i3][pj4] * dirJ);
                        }
                    }
                    double coeff1 = 0.0d;
                    for (int j6 = 0; j6 < solvedCols; j6++) {
                        coeff1 += work1[j6] * work1[j6];
                    }
                    double pc2 = currentCost * currentCost;
                    double coeff12 = coeff1 / pc2;
                    double coeff2 = ((lmPar * lmNorm2) * lmNorm2) / pc2;
                    double preRed = coeff12 + (2.0d * coeff2);
                    double dirDer = -(coeff12 + coeff2);
                    ratio = preRed == 0.0d ? 0.0d : actRed / preRed;
                    if (ratio <= 0.25d) {
                        double tmp = actRed < 0.0d ? (0.5d * dirDer) / ((0.5d * actRed) + dirDer) : 0.5d;
                        if (0.1d * currentCost >= currentCost || tmp < 0.1d) {
                            tmp = 0.1d;
                        }
                        delta = tmp * FastMath.min(delta, 10.0d * lmNorm2);
                        lmPar /= tmp;
                    } else if (lmPar == 0.0d || ratio >= 0.75d) {
                        delta = 2.0d * lmNorm2;
                        lmPar *= 0.5d;
                    }
                    if (ratio >= 1.0E-4d) {
                        firstIteration = false;
                        double xNorm3 = 0.0d;
                        for (int k3 = 0; k3 < nC; k3++) {
                            double xK = diag[k3] * currentPoint[k3];
                            xNorm3 += xK * xK;
                        }
                        xNorm = FastMath.sqrt(xNorm3);
                        if (checker != null && checker.converged(iterationCounter.getCount(), current, current)) {
                            return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
                        }
                    } else {
                        currentCost = currentCost;
                        for (int j7 = 0; j7 < solvedCols; j7++) {
                            int pj5 = permutation[j7];
                            currentPoint[pj5] = oldX[pj5];
                        }
                        weightedResidual = oldRes;
                        oldRes = weightedResidual;
                        current = current;
                    }
                    if ((FastMath.abs(actRed) > this.costRelativeTolerance || preRed > this.costRelativeTolerance || ratio > 2.0d) && delta > this.parRelativeTolerance * xNorm) {
                        if (FastMath.abs(actRed) <= TWO_EPS && preRed <= TWO_EPS && ratio <= 2.0d) {
                            throw new ConvergenceException(LocalizedFormats.TOO_SMALL_COST_RELATIVE_TOLERANCE, Double.valueOf(this.costRelativeTolerance));
                        } else if (delta <= TWO_EPS * xNorm) {
                            throw new ConvergenceException(LocalizedFormats.TOO_SMALL_PARAMETERS_RELATIVE_TOLERANCE, Double.valueOf(this.parRelativeTolerance));
                        } else if (maxCosine <= TWO_EPS) {
                            throw new ConvergenceException(LocalizedFormats.TOO_SMALL_ORTHOGONALITY_TOLERANCE, Double.valueOf(this.orthoTolerance));
                        }
                    }
                }
            }
        }
        return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
    }

    /* access modifiers changed from: private */
    public static class InternalData {
        private final double[] beta;
        private final double[] diagR;
        private final double[] jacNorm;
        private final int[] permutation;
        private final int rank;
        private final double[][] weightedJacobian;

        InternalData(double[][] weightedJacobian2, int[] permutation2, int rank2, double[] diagR2, double[] jacNorm2, double[] beta2) {
            this.weightedJacobian = weightedJacobian2;
            this.permutation = permutation2;
            this.rank = rank2;
            this.diagR = diagR2;
            this.jacNorm = jacNorm2;
            this.beta = beta2;
        }
    }

    private double determineLMParameter(double[] qy, double delta, double[] diag, InternalData internalData, int solvedCols, double[] work1, double[] work2, double[] work3, double[] lmDir, double lmPar) {
        double[][] weightedJacobian = internalData.weightedJacobian;
        int[] permutation = internalData.permutation;
        int rank = internalData.rank;
        double[] diagR = internalData.diagR;
        int nC = weightedJacobian[0].length;
        for (int j = 0; j < rank; j++) {
            lmDir[permutation[j]] = qy[j];
        }
        for (int j2 = rank; j2 < nC; j2++) {
            lmDir[permutation[j2]] = 0.0d;
        }
        for (int k = rank - 1; k >= 0; k--) {
            int pk = permutation[k];
            double ypk = lmDir[pk] / diagR[pk];
            for (int i = 0; i < k; i++) {
                int i2 = permutation[i];
                lmDir[i2] = lmDir[i2] - (weightedJacobian[i][pk] * ypk);
            }
            lmDir[pk] = ypk;
        }
        double dxNorm = 0.0d;
        for (int j3 = 0; j3 < solvedCols; j3++) {
            int pj = permutation[j3];
            double s = diag[pj] * lmDir[pj];
            work1[pj] = s;
            dxNorm += s * s;
        }
        double dxNorm2 = FastMath.sqrt(dxNorm);
        double fp = dxNorm2 - delta;
        if (fp <= 0.1d * delta) {
            return 0.0d;
        }
        double parl = 0.0d;
        if (rank == solvedCols) {
            for (int j4 = 0; j4 < solvedCols; j4++) {
                int pj2 = permutation[j4];
                work1[pj2] = work1[pj2] * (diag[pj2] / dxNorm2);
            }
            double sum2 = 0.0d;
            for (int j5 = 0; j5 < solvedCols; j5++) {
                int pj3 = permutation[j5];
                double sum = 0.0d;
                for (int i3 = 0; i3 < j5; i3++) {
                    sum += weightedJacobian[i3][pj3] * work1[permutation[i3]];
                }
                double s2 = (work1[pj3] - sum) / diagR[pj3];
                work1[pj3] = s2;
                sum2 += s2 * s2;
            }
            parl = fp / (delta * sum2);
        }
        double sum22 = 0.0d;
        for (int j6 = 0; j6 < solvedCols; j6++) {
            int pj4 = permutation[j6];
            double sum3 = 0.0d;
            for (int i4 = 0; i4 <= j6; i4++) {
                sum3 += weightedJacobian[i4][pj4] * qy[i4];
            }
            double sum4 = sum3 / diag[pj4];
            sum22 += sum4 * sum4;
        }
        double gNorm = FastMath.sqrt(sum22);
        double paru = gNorm / delta;
        if (paru == 0.0d) {
            paru = Precision.SAFE_MIN / FastMath.min(delta, 0.1d);
        }
        double lmPar2 = FastMath.min(paru, FastMath.max(lmPar, parl));
        if (lmPar2 == 0.0d) {
            lmPar2 = gNorm / dxNorm2;
        }
        for (int countdown = 10; countdown >= 0; countdown--) {
            if (lmPar2 == 0.0d) {
                lmPar2 = FastMath.max(Precision.SAFE_MIN, 0.001d * paru);
            }
            double sPar = FastMath.sqrt(lmPar2);
            for (int j7 = 0; j7 < solvedCols; j7++) {
                int pj5 = permutation[j7];
                work1[pj5] = diag[pj5] * sPar;
            }
            determineLMDirection(qy, work1, work2, internalData, solvedCols, work3, lmDir);
            double dxNorm3 = 0.0d;
            for (int j8 = 0; j8 < solvedCols; j8++) {
                int pj6 = permutation[j8];
                double s3 = diag[pj6] * lmDir[pj6];
                work3[pj6] = s3;
                dxNorm3 += s3 * s3;
            }
            double dxNorm4 = FastMath.sqrt(dxNorm3);
            fp = dxNorm4 - delta;
            if (FastMath.abs(fp) <= 0.1d * delta || (parl == 0.0d && fp <= fp && fp < 0.0d)) {
                return lmPar2;
            }
            for (int j9 = 0; j9 < solvedCols; j9++) {
                int pj7 = permutation[j9];
                work1[pj7] = (work3[pj7] * diag[pj7]) / dxNorm4;
            }
            for (int j10 = 0; j10 < solvedCols; j10++) {
                int pj8 = permutation[j10];
                work1[pj8] = work1[pj8] / work2[j10];
                double tmp = work1[pj8];
                for (int i5 = j10 + 1; i5 < solvedCols; i5++) {
                    int i6 = permutation[i5];
                    work1[i6] = work1[i6] - (weightedJacobian[i5][pj8] * tmp);
                }
            }
            double sum23 = 0.0d;
            for (int j11 = 0; j11 < solvedCols; j11++) {
                double s4 = work1[permutation[j11]];
                sum23 += s4 * s4;
            }
            double correction = fp / (delta * sum23);
            if (fp > 0.0d) {
                parl = FastMath.max(parl, lmPar2);
            } else if (fp < 0.0d) {
                paru = FastMath.min(paru, lmPar2);
            }
            lmPar2 = FastMath.max(parl, lmPar2 + correction);
        }
        return lmPar2;
    }

    private void determineLMDirection(double[] qy, double[] diag, double[] lmDiag, InternalData internalData, int solvedCols, double[] work, double[] lmDir) {
        double cos;
        double sin;
        int[] permutation = internalData.permutation;
        double[][] weightedJacobian = internalData.weightedJacobian;
        double[] diagR = internalData.diagR;
        for (int j = 0; j < solvedCols; j++) {
            int pj = permutation[j];
            for (int i = j + 1; i < solvedCols; i++) {
                weightedJacobian[i][pj] = weightedJacobian[j][permutation[i]];
            }
            lmDir[j] = diagR[pj];
            work[j] = qy[j];
        }
        for (int j2 = 0; j2 < solvedCols; j2++) {
            double dpj = diag[permutation[j2]];
            if (dpj != 0.0d) {
                Arrays.fill(lmDiag, j2 + 1, lmDiag.length, 0.0d);
            }
            lmDiag[j2] = dpj;
            double qtbpj = 0.0d;
            for (int k = j2; k < solvedCols; k++) {
                int pk = permutation[k];
                if (lmDiag[k] != 0.0d) {
                    double rkk = weightedJacobian[k][pk];
                    if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
                        double cotan = rkk / lmDiag[k];
                        sin = 1.0d / FastMath.sqrt(1.0d + (cotan * cotan));
                        cos = sin * cotan;
                    } else {
                        double tan = lmDiag[k] / rkk;
                        cos = 1.0d / FastMath.sqrt(1.0d + (tan * tan));
                        sin = cos * tan;
                    }
                    weightedJacobian[k][pk] = (cos * rkk) + (lmDiag[k] * sin);
                    double temp = (work[k] * cos) + (sin * qtbpj);
                    qtbpj = ((-sin) * work[k]) + (cos * qtbpj);
                    work[k] = temp;
                    for (int i2 = k + 1; i2 < solvedCols; i2++) {
                        double rik = weightedJacobian[i2][pk];
                        double temp2 = (cos * rik) + (lmDiag[i2] * sin);
                        lmDiag[i2] = ((-sin) * rik) + (lmDiag[i2] * cos);
                        weightedJacobian[i2][pk] = temp2;
                    }
                }
            }
            lmDiag[j2] = weightedJacobian[j2][permutation[j2]];
            weightedJacobian[j2][permutation[j2]] = lmDir[j2];
        }
        int nSing = solvedCols;
        for (int j3 = 0; j3 < solvedCols; j3++) {
            if (lmDiag[j3] == 0.0d && nSing == solvedCols) {
                nSing = j3;
            }
            if (nSing < solvedCols) {
                work[j3] = 0.0d;
            }
        }
        if (nSing > 0) {
            for (int j4 = nSing - 1; j4 >= 0; j4--) {
                int pj2 = permutation[j4];
                double sum = 0.0d;
                for (int i3 = j4 + 1; i3 < nSing; i3++) {
                    sum += weightedJacobian[i3][pj2] * work[i3];
                }
                work[j4] = (work[j4] - sum) / lmDiag[j4];
            }
        }
        for (int j5 = 0; j5 < lmDir.length; j5++) {
            lmDir[permutation[j5]] = work[j5];
        }
    }

    private InternalData qrDecomposition(RealMatrix jacobian, int solvedCols) throws ConvergenceException {
        double[][] weightedJacobian = jacobian.scalarMultiply(-1.0d).getData();
        int nR = weightedJacobian.length;
        int nC = weightedJacobian[0].length;
        int[] permutation = new int[nC];
        double[] diagR = new double[nC];
        double[] jacNorm = new double[nC];
        double[] beta = new double[nC];
        for (int k = 0; k < nC; k++) {
            permutation[k] = k;
            double norm2 = 0.0d;
            for (int i = 0; i < nR; i++) {
                double akk = weightedJacobian[i][k];
                norm2 += akk * akk;
            }
            jacNorm[k] = FastMath.sqrt(norm2);
        }
        for (int k2 = 0; k2 < nC; k2++) {
            int nextColumn = -1;
            double ak2 = Double.NEGATIVE_INFINITY;
            for (int i2 = k2; i2 < nC; i2++) {
                double norm22 = 0.0d;
                for (int j = k2; j < nR; j++) {
                    double aki = weightedJacobian[j][permutation[i2]];
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
                return new InternalData(weightedJacobian, permutation, k2, diagR, jacNorm, beta);
            }
            int pk = permutation[nextColumn];
            permutation[nextColumn] = permutation[k2];
            permutation[k2] = pk;
            double akk2 = weightedJacobian[k2][pk];
            double alpha = akk2 > 0.0d ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
            double betak = 1.0d / (ak2 - (akk2 * alpha));
            beta[pk] = betak;
            diagR[pk] = alpha;
            double[] dArr = weightedJacobian[k2];
            dArr[pk] = dArr[pk] - alpha;
            for (int dk = (nC - 1) - k2; dk > 0; dk--) {
                double gamma = 0.0d;
                for (int j2 = k2; j2 < nR; j2++) {
                    gamma += weightedJacobian[j2][pk] * weightedJacobian[j2][permutation[k2 + dk]];
                }
                double gamma2 = gamma * betak;
                for (int j3 = k2; j3 < nR; j3++) {
                    double[] dArr2 = weightedJacobian[j3];
                    int i3 = permutation[k2 + dk];
                    dArr2[i3] = dArr2[i3] - (weightedJacobian[j3][pk] * gamma2);
                }
            }
        }
        return new InternalData(weightedJacobian, permutation, solvedCols, diagR, jacNorm, beta);
    }

    private void qTy(double[] y, InternalData internalData) {
        double[][] weightedJacobian = internalData.weightedJacobian;
        int[] permutation = internalData.permutation;
        double[] beta = internalData.beta;
        int nR = weightedJacobian.length;
        int nC = weightedJacobian[0].length;
        for (int k = 0; k < nC; k++) {
            int pk = permutation[k];
            double gamma = 0.0d;
            for (int i = k; i < nR; i++) {
                gamma += weightedJacobian[i][pk] * y[i];
            }
            double gamma2 = gamma * beta[pk];
            for (int i2 = k; i2 < nR; i2++) {
                y[i2] = y[i2] - (weightedJacobian[i2][pk] * gamma2);
            }
        }
    }
}
