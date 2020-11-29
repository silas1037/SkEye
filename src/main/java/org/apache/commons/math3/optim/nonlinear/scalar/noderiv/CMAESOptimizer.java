package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class CMAESOptimizer extends MultivariateOptimizer {

    /* renamed from: B */
    private RealMatrix f316B;

    /* renamed from: BD */
    private RealMatrix f317BD;

    /* renamed from: C */
    private RealMatrix f318C;

    /* renamed from: D */
    private RealMatrix f319D;

    /* renamed from: cc */
    private double f320cc;
    private double ccov1;
    private double ccov1Sep;
    private double ccovmu;
    private double ccovmuSep;
    private final int checkFeasableCount;
    private double chiN;

    /* renamed from: cs */
    private double f321cs;
    private double damps;
    private RealMatrix diagC;
    private RealMatrix diagD;
    private int diagonalOnly;
    private int dimension;
    private double[] fitnessHistory;
    private final boolean generateStatistics;
    private int historySize;
    private double[] inputSigma;
    private final boolean isActiveCMA;
    private boolean isMinimize = true;
    private int iterations;
    private int lambda;
    private double logMu2;
    private final int maxIterations;

    /* renamed from: mu */
    private int f322mu;
    private double mueff;
    private double normps;

    /* renamed from: pc */
    private RealMatrix f323pc;

    /* renamed from: ps */
    private RealMatrix f324ps;
    private final RandomGenerator random;
    private double sigma;
    private final List<RealMatrix> statisticsDHistory = new ArrayList();
    private final List<Double> statisticsFitnessHistory = new ArrayList();
    private final List<RealMatrix> statisticsMeanHistory = new ArrayList();
    private final List<Double> statisticsSigmaHistory = new ArrayList();
    private final double stopFitness;
    private double stopTolFun;
    private double stopTolHistFun;
    private double stopTolUpX;
    private double stopTolX;
    private RealMatrix weights;
    private RealMatrix xmean;

    public CMAESOptimizer(int maxIterations2, double stopFitness2, boolean isActiveCMA2, int diagonalOnly2, int checkFeasableCount2, RandomGenerator random2, boolean generateStatistics2, ConvergenceChecker<PointValuePair> checker) {
        super(checker);
        this.maxIterations = maxIterations2;
        this.stopFitness = stopFitness2;
        this.isActiveCMA = isActiveCMA2;
        this.diagonalOnly = diagonalOnly2;
        this.checkFeasableCount = checkFeasableCount2;
        this.random = random2;
        this.generateStatistics = generateStatistics2;
    }

    public List<Double> getStatisticsSigmaHistory() {
        return this.statisticsSigmaHistory;
    }

    public List<RealMatrix> getStatisticsMeanHistory() {
        return this.statisticsMeanHistory;
    }

    public List<Double> getStatisticsFitnessHistory() {
        return this.statisticsFitnessHistory;
    }

    public List<RealMatrix> getStatisticsDHistory() {
        return this.statisticsDHistory;
    }

    public static class Sigma implements OptimizationData {
        private final double[] sigma;

        public Sigma(double[] s) throws NotPositiveException {
            for (int i = 0; i < s.length; i++) {
                if (s[i] < 0.0d) {
                    throw new NotPositiveException(Double.valueOf(s[i]));
                }
            }
            this.sigma = (double[]) s.clone();
        }

        public double[] getSigma() {
            return (double[]) this.sigma.clone();
        }
    }

    public static class PopulationSize implements OptimizationData {
        private final int lambda;

        public PopulationSize(int size) throws NotStrictlyPositiveException {
            if (size <= 0) {
                throw new NotStrictlyPositiveException(Integer.valueOf(size));
            }
            this.lambda = size;
        }

        public int getPopulationSize() {
            return this.lambda;
        }
    }

    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public PointValuePair optimize(OptimizationData... optData) throws TooManyEvaluationsException, DimensionMismatchException {
        return super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x048e, code lost:
        r48.iterations++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x049a, code lost:
        r40 = -r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x02c5, code lost:
        if (r26 >= r48.dimension) goto L_0x0303;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x02d9, code lost:
        if ((r48.sigma * r32[r26]) > r48.stopTolUpX) goto L_0x0161;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x02db, code lost:
        r26 = r26 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0303, code lost:
        r22 = min(r48.fitnessHistory);
        r24 = max(r48.fitnessHistory);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x031b, code lost:
        if (r48.iterations <= 2) goto L_0x0337;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0335, code lost:
        if ((org.apache.commons.math3.util.FastMath.max(r24, r38) - org.apache.commons.math3.util.FastMath.min(r22, r14)) < r48.stopTolFun) goto L_0x0161;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0348, code lost:
        if (r48.iterations <= r48.fitnessHistory.length) goto L_0x0356;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0354, code lost:
        if ((r24 - r22) < r48.stopTolHistFun) goto L_0x0161;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x036f, code lost:
        if ((max(r48.diagD) / min(r48.diagD)) > 1.0E7d) goto L_0x0161;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0375, code lost:
        if (getConvergenceChecker() == null) goto L_0x03a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0377, code lost:
        r4 = r6.getColumn(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0384, code lost:
        if (r48.isMinimize == false) goto L_0x049a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0386, code lost:
        r40 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0388, code lost:
        r13 = new org.apache.commons.math3.optim.PointValuePair(r4, r40);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x038d, code lost:
        if (r29 == null) goto L_0x03a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x03a1, code lost:
        if (getConvergenceChecker().converged(r48.iterations, r13, r29) != false) goto L_0x0161;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x03a3, code lost:
        r29 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x03c0, code lost:
        if (r16 != r0[r8[(int) (0.1d + (((double) r48.lambda) / 4.0d))]]) goto L_0x03e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x03c2, code lost:
        r48.sigma *= org.apache.commons.math3.util.FastMath.exp(0.2d + (r48.f321cs / r48.damps));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x03f1, code lost:
        if (r48.iterations <= 2) goto L_0x042e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0405, code lost:
        if ((org.apache.commons.math3.util.FastMath.max(r24, r14) - org.apache.commons.math3.util.FastMath.min(r22, r14)) != 0.0d) goto L_0x042e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0407, code lost:
        r48.sigma *= org.apache.commons.math3.util.FastMath.exp(0.2d + (r48.f321cs / r48.damps));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x042e, code lost:
        push(r48.fitnessHistory, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0439, code lost:
        if (r48.generateStatistics == false) goto L_0x048e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x043b, code lost:
        r48.statisticsSigmaHistory.add(java.lang.Double.valueOf(r48.sigma));
        r48.statisticsFitnessHistory.add(java.lang.Double.valueOf(r14));
        r48.statisticsMeanHistory.add(r48.xmean.transpose());
        r48.statisticsDHistory.add(r48.diagD.transpose().scalarMultiply(100000.0d));
     */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.optim.PointValuePair doOptimize() {
        /*
        // Method dump skipped, instructions count: 1183
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer.doOptimize():org.apache.commons.math3.optim.PointValuePair");
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (OptimizationData data : optData) {
            if (data instanceof Sigma) {
                this.inputSigma = ((Sigma) data).getSigma();
            } else if (data instanceof PopulationSize) {
                this.lambda = ((PopulationSize) data).getPopulationSize();
            }
        }
        checkParameters();
    }

    private void checkParameters() {
        double[] init = getStartPoint();
        double[] lB = getLowerBound();
        double[] uB = getUpperBound();
        if (this.inputSigma == null) {
            return;
        }
        if (this.inputSigma.length != init.length) {
            throw new DimensionMismatchException(this.inputSigma.length, init.length);
        }
        for (int i = 0; i < init.length; i++) {
            if (this.inputSigma[i] > uB[i] - lB[i]) {
                throw new OutOfRangeException(Double.valueOf(this.inputSigma[i]), 0, Double.valueOf(uB[i] - lB[i]));
            }
        }
    }

    private void initializeCMA(double[] guess) {
        if (this.lambda <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(this.lambda));
        }
        double[][] sigmaArray = (double[][]) Array.newInstance(Double.TYPE, guess.length, 1);
        for (int i = 0; i < guess.length; i++) {
            sigmaArray[i][0] = this.inputSigma[i];
        }
        RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
        this.sigma = max(insigma);
        this.stopTolUpX = 1000.0d * max(insigma);
        this.stopTolX = 1.0E-11d * max(insigma);
        this.stopTolFun = 1.0E-12d;
        this.stopTolHistFun = 1.0E-13d;
        this.f322mu = this.lambda / 2;
        this.logMu2 = FastMath.log(((double) this.f322mu) + 0.5d);
        this.weights = log(sequence(1.0d, (double) this.f322mu, 1.0d)).scalarMultiply(-1.0d).scalarAdd(this.logMu2);
        double sumw = 0.0d;
        double sumwq = 0.0d;
        for (int i2 = 0; i2 < this.f322mu; i2++) {
            double w = this.weights.getEntry(i2, 0);
            sumw += w;
            sumwq += w * w;
        }
        this.weights = this.weights.scalarMultiply(1.0d / sumw);
        this.mueff = (sumw * sumw) / sumwq;
        this.f320cc = (4.0d + (this.mueff / ((double) this.dimension))) / (((double) (this.dimension + 4)) + ((2.0d * this.mueff) / ((double) this.dimension)));
        this.f321cs = (this.mueff + 2.0d) / ((((double) this.dimension) + this.mueff) + 3.0d);
        this.damps = ((1.0d + (2.0d * FastMath.max(0.0d, FastMath.sqrt((this.mueff - 1.0d) / ((double) (this.dimension + 1))) - 1.0d))) * FastMath.max(0.3d, 1.0d - (((double) this.dimension) / (1.0E-6d + ((double) this.maxIterations))))) + this.f321cs;
        this.ccov1 = 2.0d / (((((double) this.dimension) + 1.3d) * (((double) this.dimension) + 1.3d)) + this.mueff);
        this.ccovmu = FastMath.min(1.0d - this.ccov1, (2.0d * ((this.mueff - 2.0d) + (1.0d / this.mueff))) / (((double) ((this.dimension + 2) * (this.dimension + 2))) + this.mueff));
        this.ccov1Sep = FastMath.min(1.0d, (this.ccov1 * (((double) this.dimension) + 1.5d)) / 3.0d);
        this.ccovmuSep = FastMath.min(1.0d - this.ccov1, (this.ccovmu * (((double) this.dimension) + 1.5d)) / 3.0d);
        this.chiN = FastMath.sqrt((double) this.dimension) * ((1.0d - (1.0d / (4.0d * ((double) this.dimension)))) + (1.0d / ((21.0d * ((double) this.dimension)) * ((double) this.dimension))));
        this.xmean = MatrixUtils.createColumnRealMatrix(guess);
        this.diagD = insigma.scalarMultiply(1.0d / this.sigma);
        this.diagC = square(this.diagD);
        this.f323pc = zeros(this.dimension, 1);
        this.f324ps = zeros(this.dimension, 1);
        this.normps = this.f324ps.getFrobeniusNorm();
        this.f316B = eye(this.dimension, this.dimension);
        this.f319D = ones(this.dimension, 1);
        this.f317BD = times(this.f316B, repmat(this.diagD.transpose(), this.dimension, 1));
        this.f318C = this.f316B.multiply(diag(square(this.f319D)).multiply(this.f316B.transpose()));
        this.historySize = ((int) (((double) (this.dimension * 30)) / ((double) this.lambda))) + 10;
        this.fitnessHistory = new double[this.historySize];
        for (int i3 = 0; i3 < this.historySize; i3++) {
            this.fitnessHistory[i3] = Double.MAX_VALUE;
        }
    }

    private boolean updateEvolutionPaths(RealMatrix zmean, RealMatrix xold) {
        this.f324ps = this.f324ps.scalarMultiply(1.0d - this.f321cs).add(this.f316B.multiply(zmean).scalarMultiply(FastMath.sqrt(this.f321cs * (2.0d - this.f321cs) * this.mueff)));
        this.normps = this.f324ps.getFrobeniusNorm();
        boolean hsig = (this.normps / FastMath.sqrt(1.0d - FastMath.pow(1.0d - this.f321cs, this.iterations * 2))) / this.chiN < 1.4d + (2.0d / (((double) this.dimension) + 1.0d));
        this.f323pc = this.f323pc.scalarMultiply(1.0d - this.f320cc);
        if (hsig) {
            this.f323pc = this.f323pc.add(this.xmean.subtract(xold).scalarMultiply(FastMath.sqrt((this.f320cc * (2.0d - this.f320cc)) * this.mueff) / this.sigma));
        }
        return hsig;
    }

    private void updateCovarianceDiagonalOnly(boolean hsig, RealMatrix bestArz) {
        this.diagC = this.diagC.scalarMultiply((hsig ? 0.0d : this.ccov1Sep * this.f320cc * (2.0d - this.f320cc)) + ((1.0d - this.ccov1Sep) - this.ccovmuSep)).add(square(this.f323pc).scalarMultiply(this.ccov1Sep)).add(times(this.diagC, square(bestArz).multiply(this.weights)).scalarMultiply(this.ccovmuSep));
        this.diagD = sqrt(this.diagC);
        if (this.diagonalOnly > 1 && this.iterations > this.diagonalOnly) {
            this.diagonalOnly = 0;
            this.f316B = eye(this.dimension, this.dimension);
            this.f317BD = diag(this.diagD);
            this.f318C = diag(this.diagC);
        }
    }

    private void updateCovariance(boolean hsig, RealMatrix bestArx, RealMatrix arz, int[] arindex, RealMatrix xold) {
        double oldFac;
        double negccov = 0.0d;
        if (this.ccov1 + this.ccovmu > 0.0d) {
            RealMatrix arpos = bestArx.subtract(repmat(xold, 1, this.f322mu)).scalarMultiply(1.0d / this.sigma);
            RealMatrix roneu = this.f323pc.multiply(this.f323pc.transpose()).scalarMultiply(this.ccov1);
            if (hsig) {
                oldFac = 0.0d;
            } else {
                oldFac = this.ccov1 * this.f320cc * (2.0d - this.f320cc);
            }
            double oldFac2 = oldFac + ((1.0d - this.ccov1) - this.ccovmu);
            if (this.isActiveCMA) {
                negccov = (((1.0d - this.ccovmu) * 0.25d) * this.mueff) / (FastMath.pow((double) (this.dimension + 2), 1.5d) + (2.0d * this.mueff));
                RealMatrix arzneg = selectColumns(arz, MathArrays.copyOf(reverse(arindex), this.f322mu));
                RealMatrix arnorms = sqrt(sumRows(square(arzneg)));
                int[] idxnorms = sortedIndices(arnorms.getRow(0));
                RealMatrix arnormsInv = selectColumns(divide(selectColumns(arnorms, reverse(idxnorms)), selectColumns(arnorms, idxnorms)), inverse(idxnorms));
                double negcovMax = 0.33999999999999997d / square(arnormsInv).multiply(this.weights).getEntry(0, 0);
                if (negccov > negcovMax) {
                    negccov = negcovMax;
                }
                RealMatrix artmp = this.f317BD.multiply(times(arzneg, repmat(arnormsInv, this.dimension, 1)));
                RealMatrix Cneg = artmp.multiply(diag(this.weights)).multiply(artmp.transpose());
                this.f318C = this.f318C.scalarMultiply(oldFac2 + (0.5d * negccov)).add(roneu).add(arpos.scalarMultiply(this.ccovmu + (0.5d * negccov)).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose()))).subtract(Cneg.scalarMultiply(negccov));
            } else {
                this.f318C = this.f318C.scalarMultiply(oldFac2).add(roneu).add(arpos.scalarMultiply(this.ccovmu).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose())));
            }
        }
        updateBD(negccov);
    }

    private void updateBD(double negccov) {
        if (this.ccov1 + this.ccovmu + negccov > 0.0d && (((((double) this.iterations) % 1.0d) / ((this.ccov1 + this.ccovmu) + negccov)) / ((double) this.dimension)) / 10.0d < 1.0d) {
            this.f318C = triu(this.f318C, 0).add(triu(this.f318C, 1).transpose());
            EigenDecomposition eig = new EigenDecomposition(this.f318C);
            this.f316B = eig.getV();
            this.f319D = eig.getD();
            this.diagD = diag(this.f319D);
            if (min(this.diagD) <= 0.0d) {
                for (int i = 0; i < this.dimension; i++) {
                    if (this.diagD.getEntry(i, 0) < 0.0d) {
                        this.diagD.setEntry(i, 0, 0.0d);
                    }
                }
                double tfac = max(this.diagD) / 1.0E14d;
                this.f318C = this.f318C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
                this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
            }
            if (max(this.diagD) > 1.0E14d * min(this.diagD)) {
                double tfac2 = (max(this.diagD) / 1.0E14d) - min(this.diagD);
                this.f318C = this.f318C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac2));
                this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac2));
            }
            this.diagC = diag(this.f318C);
            this.diagD = sqrt(this.diagD);
            this.f317BD = times(this.f316B, repmat(this.diagD.transpose(), this.dimension, 1));
        }
    }

    private static void push(double[] vals, double val) {
        for (int i = vals.length - 1; i > 0; i--) {
            vals[i] = vals[i - 1];
        }
        vals[0] = val;
    }

    private int[] sortedIndices(double[] doubles) {
        DoubleIndex[] dis = new DoubleIndex[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            dis[i] = new DoubleIndex(doubles[i], i);
        }
        Arrays.sort(dis);
        int[] indices = new int[doubles.length];
        for (int i2 = 0; i2 < doubles.length; i2++) {
            indices[i2] = dis[i2].index;
        }
        return indices;
    }

    private double valueRange(ValuePenaltyPair[] vpPairs) {
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.MAX_VALUE;
        for (ValuePenaltyPair vpPair : vpPairs) {
            if (vpPair.value > max) {
                max = vpPair.value;
            }
            if (vpPair.value < min) {
                min = vpPair.value;
            }
        }
        return max - min;
    }

    /* access modifiers changed from: private */
    public static class DoubleIndex implements Comparable<DoubleIndex> {
        private final int index;
        private final double value;

        DoubleIndex(double value2, int index2) {
            this.value = value2;
            this.index = index2;
        }

        public int compareTo(DoubleIndex o) {
            return Double.compare(this.value, o.value);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof DoubleIndex) {
                return Double.compare(this.value, ((DoubleIndex) other).value) == 0;
            }
            return false;
        }

        public int hashCode() {
            long bits = Double.doubleToLongBits(this.value);
            return (int) (((1438542 ^ (bits >>> 32)) ^ bits) & -1);
        }
    }

    /* access modifiers changed from: private */
    public static class ValuePenaltyPair {
        private double penalty;
        private double value;

        ValuePenaltyPair(double value2, double penalty2) {
            this.value = value2;
            this.penalty = penalty2;
        }
    }

    /* access modifiers changed from: private */
    public class FitnessFunction {
        private final boolean isRepairMode = true;

        FitnessFunction() {
        }

        public ValuePenaltyPair value(double[] point) {
            double value;
            double penalty = 0.0d;
            if (this.isRepairMode) {
                double[] repaired = repair(point);
                value = CMAESOptimizer.this.computeObjectiveValue(repaired);
                penalty = penalty(point, repaired);
            } else {
                value = CMAESOptimizer.this.computeObjectiveValue(point);
            }
            if (!CMAESOptimizer.this.isMinimize) {
                value = -value;
            }
            if (!CMAESOptimizer.this.isMinimize) {
                penalty = -penalty;
            }
            return new ValuePenaltyPair(value, penalty);
        }

        public boolean isFeasible(double[] x) {
            double[] lB = CMAESOptimizer.this.getLowerBound();
            double[] uB = CMAESOptimizer.this.getUpperBound();
            for (int i = 0; i < x.length; i++) {
                if (x[i] < lB[i] || x[i] > uB[i]) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private double[] repair(double[] x) {
            double[] lB = CMAESOptimizer.this.getLowerBound();
            double[] uB = CMAESOptimizer.this.getUpperBound();
            double[] repaired = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                if (x[i] < lB[i]) {
                    repaired[i] = lB[i];
                } else if (x[i] > uB[i]) {
                    repaired[i] = uB[i];
                } else {
                    repaired[i] = x[i];
                }
            }
            return repaired;
        }

        private double penalty(double[] x, double[] repaired) {
            double penalty = 0.0d;
            for (int i = 0; i < x.length; i++) {
                penalty += FastMath.abs(x[i] - repaired[i]);
            }
            return CMAESOptimizer.this.isMinimize ? penalty : -penalty;
        }
    }

    private static RealMatrix log(RealMatrix m) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), m.getColumnDimension());
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = FastMath.log(m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix sqrt(RealMatrix m) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), m.getColumnDimension());
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = FastMath.sqrt(m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix square(RealMatrix m) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), m.getColumnDimension());
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                double e = m.getEntry(r, c);
                d[r][c] = e * e;
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix times(RealMatrix m, RealMatrix n) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), m.getColumnDimension());
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = m.getEntry(r, c) * n.getEntry(r, c);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix divide(RealMatrix m, RealMatrix n) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), m.getColumnDimension());
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = m.getEntry(r, c) / n.getEntry(r, c);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix selectColumns(RealMatrix m, int[] cols) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), cols.length);
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < cols.length; c++) {
                d[r][c] = m.getEntry(r, cols[c]);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix triu(RealMatrix m, int k) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), m.getColumnDimension());
        int r = 0;
        while (r < m.getRowDimension()) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = r <= c - k ? m.getEntry(r, c) : 0.0d;
            }
            r++;
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix sumRows(RealMatrix m) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, 1, m.getColumnDimension());
        for (int c = 0; c < m.getColumnDimension(); c++) {
            double sum = 0.0d;
            for (int r = 0; r < m.getRowDimension(); r++) {
                sum += m.getEntry(r, c);
            }
            d[0][c] = sum;
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix diag(RealMatrix m) {
        if (m.getColumnDimension() == 1) {
            double[][] d = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), m.getRowDimension());
            for (int i = 0; i < m.getRowDimension(); i++) {
                d[i][i] = m.getEntry(i, 0);
            }
            return new Array2DRowRealMatrix(d, false);
        }
        double[][] d2 = (double[][]) Array.newInstance(Double.TYPE, m.getRowDimension(), 1);
        for (int i2 = 0; i2 < m.getColumnDimension(); i2++) {
            d2[i2][0] = m.getEntry(i2, i2);
        }
        return new Array2DRowRealMatrix(d2, false);
    }

    private static void copyColumn(RealMatrix m1, int col1, RealMatrix m2, int col2) {
        for (int i = 0; i < m1.getRowDimension(); i++) {
            m2.setEntry(i, col2, m1.getEntry(i, col1));
        }
    }

    private static RealMatrix ones(int n, int m) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, n, m);
        for (int r = 0; r < n; r++) {
            Arrays.fill(d[r], 1.0d);
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix eye(int n, int m) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, n, m);
        for (int r = 0; r < n; r++) {
            if (r < m) {
                d[r][r] = 1.0d;
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix zeros(int n, int m) {
        return new Array2DRowRealMatrix(n, m);
    }

    private static RealMatrix repmat(RealMatrix mat, int n, int m) {
        int rd = mat.getRowDimension();
        int cd = mat.getColumnDimension();
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, n * rd, m * cd);
        for (int r = 0; r < n * rd; r++) {
            for (int c = 0; c < m * cd; c++) {
                d[r][c] = mat.getEntry(r % rd, c % cd);
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static RealMatrix sequence(double start, double end, double step) {
        int size = (int) (((end - start) / step) + 1.0d);
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, size, 1);
        double value = start;
        for (int r = 0; r < size; r++) {
            d[r][0] = value;
            value += step;
        }
        return new Array2DRowRealMatrix(d, false);
    }

    private static double max(RealMatrix m) {
        double max = -1.7976931348623157E308d;
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                double e = m.getEntry(r, c);
                if (max < e) {
                    max = e;
                }
            }
        }
        return max;
    }

    private static double min(RealMatrix m) {
        double min = Double.MAX_VALUE;
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                double e = m.getEntry(r, c);
                if (min > e) {
                    min = e;
                }
            }
        }
        return min;
    }

    private static double max(double[] m) {
        double max = -1.7976931348623157E308d;
        for (int r = 0; r < m.length; r++) {
            if (max < m[r]) {
                max = m[r];
            }
        }
        return max;
    }

    private static double min(double[] m) {
        double min = Double.MAX_VALUE;
        for (int r = 0; r < m.length; r++) {
            if (min > m[r]) {
                min = m[r];
            }
        }
        return min;
    }

    private static int[] inverse(int[] indices) {
        int[] inverse = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            inverse[indices[i]] = i;
        }
        return inverse;
    }

    private static int[] reverse(int[] indices) {
        int[] reverse = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            reverse[i] = indices[(indices.length - i) - 1];
        }
        return reverse;
    }

    private double[] randn(int size) {
        double[] randn = new double[size];
        for (int i = 0; i < size; i++) {
            randn[i] = this.random.nextGaussian();
        }
        return randn;
    }

    private RealMatrix randn1(int size, int popSize) {
        double[][] d = (double[][]) Array.newInstance(Double.TYPE, size, popSize);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < popSize; c++) {
                d[r][c] = this.random.nextGaussian();
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
}
