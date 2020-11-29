package org.apache.commons.math3.optimization.direct;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.MultivariateOptimizer;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class CMAESOptimizer extends BaseAbstractMultivariateSimpleBoundsOptimizer<MultivariateFunction> implements MultivariateOptimizer {
    public static final int DEFAULT_CHECKFEASABLECOUNT = 0;
    public static final int DEFAULT_DIAGONALONLY = 0;
    public static final boolean DEFAULT_ISACTIVECMA = true;
    public static final int DEFAULT_MAXITERATIONS = 30000;
    public static final RandomGenerator DEFAULT_RANDOMGENERATOR = new MersenneTwister();
    public static final double DEFAULT_STOPFITNESS = 0.0d;

    /* renamed from: B */
    private RealMatrix f327B;

    /* renamed from: BD */
    private RealMatrix f328BD;

    /* renamed from: C */
    private RealMatrix f329C;

    /* renamed from: D */
    private RealMatrix f330D;

    /* renamed from: cc */
    private double f331cc;
    private double ccov1;
    private double ccov1Sep;
    private double ccovmu;
    private double ccovmuSep;
    private int checkFeasableCount;
    private double chiN;

    /* renamed from: cs */
    private double f332cs;
    private double damps;
    private RealMatrix diagC;
    private RealMatrix diagD;
    private int diagonalOnly;
    private int dimension;
    private double[] fitnessHistory;
    private boolean generateStatistics;
    private int historySize;
    private double[] inputSigma;
    private boolean isActiveCMA;
    private boolean isMinimize;
    private int iterations;
    private int lambda;
    private double logMu2;
    private int maxIterations;

    /* renamed from: mu */
    private int f333mu;
    private double mueff;
    private double normps;

    /* renamed from: pc */
    private RealMatrix f334pc;

    /* renamed from: ps */
    private RealMatrix f335ps;
    private RandomGenerator random;
    private double sigma;
    private List<RealMatrix> statisticsDHistory;
    private List<Double> statisticsFitnessHistory;
    private List<RealMatrix> statisticsMeanHistory;
    private List<Double> statisticsSigmaHistory;
    private double stopFitness;
    private double stopTolFun;
    private double stopTolHistFun;
    private double stopTolUpX;
    private double stopTolX;
    private RealMatrix weights;
    private RealMatrix xmean;

    @Deprecated
    public CMAESOptimizer() {
        this(0);
    }

    @Deprecated
    public CMAESOptimizer(int lambda2) {
        this(lambda2, null, DEFAULT_MAXITERATIONS, 0.0d, true, 0, 0, DEFAULT_RANDOMGENERATOR, false, null);
    }

    @Deprecated
    public CMAESOptimizer(int lambda2, double[] inputSigma2) {
        this(lambda2, inputSigma2, DEFAULT_MAXITERATIONS, 0.0d, true, 0, 0, DEFAULT_RANDOMGENERATOR, false);
    }

    @Deprecated
    public CMAESOptimizer(int lambda2, double[] inputSigma2, int maxIterations2, double stopFitness2, boolean isActiveCMA2, int diagonalOnly2, int checkFeasableCount2, RandomGenerator random2, boolean generateStatistics2) {
        this(lambda2, inputSigma2, maxIterations2, stopFitness2, isActiveCMA2, diagonalOnly2, checkFeasableCount2, random2, generateStatistics2, new SimpleValueChecker());
    }

    @Deprecated
    public CMAESOptimizer(int lambda2, double[] inputSigma2, int maxIterations2, double stopFitness2, boolean isActiveCMA2, int diagonalOnly2, int checkFeasableCount2, RandomGenerator random2, boolean generateStatistics2, ConvergenceChecker<PointValuePair> checker) {
        super(checker);
        this.diagonalOnly = 0;
        this.isMinimize = true;
        this.generateStatistics = false;
        this.statisticsSigmaHistory = new ArrayList();
        this.statisticsMeanHistory = new ArrayList();
        this.statisticsFitnessHistory = new ArrayList();
        this.statisticsDHistory = new ArrayList();
        this.lambda = lambda2;
        this.inputSigma = inputSigma2 == null ? null : (double[]) inputSigma2.clone();
        this.maxIterations = maxIterations2;
        this.stopFitness = stopFitness2;
        this.isActiveCMA = isActiveCMA2;
        this.diagonalOnly = diagonalOnly2;
        this.checkFeasableCount = checkFeasableCount2;
        this.random = random2;
        this.generateStatistics = generateStatistics2;
    }

    public CMAESOptimizer(int maxIterations2, double stopFitness2, boolean isActiveCMA2, int diagonalOnly2, int checkFeasableCount2, RandomGenerator random2, boolean generateStatistics2, ConvergenceChecker<PointValuePair> checker) {
        super(checker);
        this.diagonalOnly = 0;
        this.isMinimize = true;
        this.generateStatistics = false;
        this.statisticsSigmaHistory = new ArrayList();
        this.statisticsMeanHistory = new ArrayList();
        this.statisticsFitnessHistory = new ArrayList();
        this.statisticsDHistory = new ArrayList();
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

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer
    public PointValuePair optimizeInternal(int maxEval, MultivariateFunction f, GoalType goalType, OptimizationData... optData) {
        parseOptimizationData(optData);
        return super.optimizeInternal(maxEval, f, goalType, optData);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0285, code lost:
        if (r26 >= r44.dimension) goto L_0x02c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0299, code lost:
        if ((r44.sigma * r31[r26]) > r44.stopTolUpX) goto L_0x0147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x029b, code lost:
        r26 = r26 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x02c3, code lost:
        r22 = min(r44.fitnessHistory);
        r24 = max(r44.fitnessHistory);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x02db, code lost:
        if (r44.iterations <= 2) goto L_0x02f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x02f5, code lost:
        if ((org.apache.commons.math3.util.FastMath.max(r24, r32) - org.apache.commons.math3.util.FastMath.min(r22, r14)) < r44.stopTolFun) goto L_0x0147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0308, code lost:
        if (r44.iterations <= r44.fitnessHistory.length) goto L_0x0316;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0314, code lost:
        if ((r24 - r22) < r44.stopTolHistFun) goto L_0x0147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x032f, code lost:
        if ((max(r44.diagD) / min(r44.diagD)) > 1.0E7d) goto L_0x0147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0335, code lost:
        if (getConvergenceChecker() == null) goto L_0x0365;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0337, code lost:
        r4 = r6.getColumn(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0344, code lost:
        if (r44.isMinimize == false) goto L_0x045f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0346, code lost:
        r36 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0348, code lost:
        r13 = new org.apache.commons.math3.optimization.PointValuePair(r4, r36);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x034d, code lost:
        if (r28 == null) goto L_0x0363;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0361, code lost:
        if (getConvergenceChecker().converged(r44.iterations, r13, r28) != false) goto L_0x0147;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0363, code lost:
        r28 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0380, code lost:
        if (r16 != r0[r8[(int) (0.1d + (((double) r44.lambda) / 4.0d))]]) goto L_0x03a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0382, code lost:
        r44.sigma *= org.apache.commons.math3.util.FastMath.exp(0.2d + (r44.f332cs / r44.damps));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x03b1, code lost:
        if (r44.iterations <= 2) goto L_0x03ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x03c5, code lost:
        if ((org.apache.commons.math3.util.FastMath.max(r24, r14) - org.apache.commons.math3.util.FastMath.min(r22, r14)) != 0.0d) goto L_0x03ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x03c7, code lost:
        r44.sigma *= org.apache.commons.math3.util.FastMath.exp(0.2d + (r44.f332cs / r44.damps));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x03ee, code lost:
        push(r44.fitnessHistory, r14);
        r19.setValueRange(r32 - r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0402, code lost:
        if (r44.generateStatistics == false) goto L_0x0453;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0404, code lost:
        r44.statisticsSigmaHistory.add(java.lang.Double.valueOf(r44.sigma));
        r44.statisticsFitnessHistory.add(java.lang.Double.valueOf(r14));
        r44.statisticsMeanHistory.add(r44.xmean.transpose());
        r44.statisticsDHistory.add(r44.diagD.transpose().scalarMultiply(100000.0d));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0453, code lost:
        r44.iterations++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x045f, code lost:
        r36 = -r14;
     */
    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.optimization.PointValuePair doOptimize() {
        /*
        // Method dump skipped, instructions count: 1124
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.optimization.direct.CMAESOptimizer.doOptimize():org.apache.commons.math3.optimization.PointValuePair");
    }

    private void parseOptimizationData(OptimizationData... optData) {
        for (OptimizationData data : optData) {
            if (data instanceof Sigma) {
                this.inputSigma = ((Sigma) data).getSigma();
            } else if (data instanceof PopulationSize) {
                this.lambda = ((PopulationSize) data).getPopulationSize();
            }
        }
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
            if (this.inputSigma[i] < 0.0d) {
                throw new NotPositiveException(Double.valueOf(this.inputSigma[i]));
            } else if (this.inputSigma[i] > uB[i] - lB[i]) {
                throw new OutOfRangeException(Double.valueOf(this.inputSigma[i]), 0, Double.valueOf(uB[i] - lB[i]));
            }
        }
    }

    private void initializeCMA(double[] guess) {
        if (this.lambda <= 0) {
            this.lambda = ((int) (3.0d * FastMath.log((double) this.dimension))) + 4;
        }
        double[][] sigmaArray = (double[][]) Array.newInstance(Double.TYPE, guess.length, 1);
        for (int i = 0; i < guess.length; i++) {
            sigmaArray[i][0] = this.inputSigma == null ? 0.3d : this.inputSigma[i];
        }
        RealMatrix insigma = new Array2DRowRealMatrix(sigmaArray, false);
        this.sigma = max(insigma);
        this.stopTolUpX = 1000.0d * max(insigma);
        this.stopTolX = 1.0E-11d * max(insigma);
        this.stopTolFun = 1.0E-12d;
        this.stopTolHistFun = 1.0E-13d;
        this.f333mu = this.lambda / 2;
        this.logMu2 = FastMath.log(((double) this.f333mu) + 0.5d);
        this.weights = log(sequence(1.0d, (double) this.f333mu, 1.0d)).scalarMultiply(-1.0d).scalarAdd(this.logMu2);
        double sumw = 0.0d;
        double sumwq = 0.0d;
        for (int i2 = 0; i2 < this.f333mu; i2++) {
            double w = this.weights.getEntry(i2, 0);
            sumw += w;
            sumwq += w * w;
        }
        this.weights = this.weights.scalarMultiply(1.0d / sumw);
        this.mueff = (sumw * sumw) / sumwq;
        this.f331cc = (4.0d + (this.mueff / ((double) this.dimension))) / (((double) (this.dimension + 4)) + ((2.0d * this.mueff) / ((double) this.dimension)));
        this.f332cs = (this.mueff + 2.0d) / ((((double) this.dimension) + this.mueff) + 3.0d);
        this.damps = ((1.0d + (2.0d * FastMath.max(0.0d, FastMath.sqrt((this.mueff - 1.0d) / ((double) (this.dimension + 1))) - 1.0d))) * FastMath.max(0.3d, 1.0d - (((double) this.dimension) / (1.0E-6d + ((double) this.maxIterations))))) + this.f332cs;
        this.ccov1 = 2.0d / (((((double) this.dimension) + 1.3d) * (((double) this.dimension) + 1.3d)) + this.mueff);
        this.ccovmu = FastMath.min(1.0d - this.ccov1, (2.0d * ((this.mueff - 2.0d) + (1.0d / this.mueff))) / (((double) ((this.dimension + 2) * (this.dimension + 2))) + this.mueff));
        this.ccov1Sep = FastMath.min(1.0d, (this.ccov1 * (((double) this.dimension) + 1.5d)) / 3.0d);
        this.ccovmuSep = FastMath.min(1.0d - this.ccov1, (this.ccovmu * (((double) this.dimension) + 1.5d)) / 3.0d);
        this.chiN = FastMath.sqrt((double) this.dimension) * ((1.0d - (1.0d / (4.0d * ((double) this.dimension)))) + (1.0d / ((21.0d * ((double) this.dimension)) * ((double) this.dimension))));
        this.xmean = MatrixUtils.createColumnRealMatrix(guess);
        this.diagD = insigma.scalarMultiply(1.0d / this.sigma);
        this.diagC = square(this.diagD);
        this.f334pc = zeros(this.dimension, 1);
        this.f335ps = zeros(this.dimension, 1);
        this.normps = this.f335ps.getFrobeniusNorm();
        this.f327B = eye(this.dimension, this.dimension);
        this.f330D = ones(this.dimension, 1);
        this.f328BD = times(this.f327B, repmat(this.diagD.transpose(), this.dimension, 1));
        this.f329C = this.f327B.multiply(diag(square(this.f330D)).multiply(this.f327B.transpose()));
        this.historySize = ((int) (((double) (this.dimension * 30)) / ((double) this.lambda))) + 10;
        this.fitnessHistory = new double[this.historySize];
        for (int i3 = 0; i3 < this.historySize; i3++) {
            this.fitnessHistory[i3] = Double.MAX_VALUE;
        }
    }

    private boolean updateEvolutionPaths(RealMatrix zmean, RealMatrix xold) {
        this.f335ps = this.f335ps.scalarMultiply(1.0d - this.f332cs).add(this.f327B.multiply(zmean).scalarMultiply(FastMath.sqrt(this.f332cs * (2.0d - this.f332cs) * this.mueff)));
        this.normps = this.f335ps.getFrobeniusNorm();
        boolean hsig = (this.normps / FastMath.sqrt(1.0d - FastMath.pow(1.0d - this.f332cs, this.iterations * 2))) / this.chiN < 1.4d + (2.0d / (((double) this.dimension) + 1.0d));
        this.f334pc = this.f334pc.scalarMultiply(1.0d - this.f331cc);
        if (hsig) {
            this.f334pc = this.f334pc.add(this.xmean.subtract(xold).scalarMultiply(FastMath.sqrt((this.f331cc * (2.0d - this.f331cc)) * this.mueff) / this.sigma));
        }
        return hsig;
    }

    private void updateCovarianceDiagonalOnly(boolean hsig, RealMatrix bestArz) {
        this.diagC = this.diagC.scalarMultiply((hsig ? 0.0d : this.ccov1Sep * this.f331cc * (2.0d - this.f331cc)) + ((1.0d - this.ccov1Sep) - this.ccovmuSep)).add(square(this.f334pc).scalarMultiply(this.ccov1Sep)).add(times(this.diagC, square(bestArz).multiply(this.weights)).scalarMultiply(this.ccovmuSep));
        this.diagD = sqrt(this.diagC);
        if (this.diagonalOnly > 1 && this.iterations > this.diagonalOnly) {
            this.diagonalOnly = 0;
            this.f327B = eye(this.dimension, this.dimension);
            this.f328BD = diag(this.diagD);
            this.f329C = diag(this.diagC);
        }
    }

    private void updateCovariance(boolean hsig, RealMatrix bestArx, RealMatrix arz, int[] arindex, RealMatrix xold) {
        double oldFac;
        double negccov = 0.0d;
        if (this.ccov1 + this.ccovmu > 0.0d) {
            RealMatrix arpos = bestArx.subtract(repmat(xold, 1, this.f333mu)).scalarMultiply(1.0d / this.sigma);
            RealMatrix roneu = this.f334pc.multiply(this.f334pc.transpose()).scalarMultiply(this.ccov1);
            if (hsig) {
                oldFac = 0.0d;
            } else {
                oldFac = this.ccov1 * this.f331cc * (2.0d - this.f331cc);
            }
            double oldFac2 = oldFac + ((1.0d - this.ccov1) - this.ccovmu);
            if (this.isActiveCMA) {
                negccov = (((1.0d - this.ccovmu) * 0.25d) * this.mueff) / (FastMath.pow((double) (this.dimension + 2), 1.5d) + (2.0d * this.mueff));
                RealMatrix arzneg = selectColumns(arz, MathArrays.copyOf(reverse(arindex), this.f333mu));
                RealMatrix arnorms = sqrt(sumRows(square(arzneg)));
                int[] idxnorms = sortedIndices(arnorms.getRow(0));
                RealMatrix arnormsInv = selectColumns(divide(selectColumns(arnorms, reverse(idxnorms)), selectColumns(arnorms, idxnorms)), inverse(idxnorms));
                double negcovMax = 0.33999999999999997d / square(arnormsInv).multiply(this.weights).getEntry(0, 0);
                if (negccov > negcovMax) {
                    negccov = negcovMax;
                }
                RealMatrix artmp = this.f328BD.multiply(times(arzneg, repmat(arnormsInv, this.dimension, 1)));
                RealMatrix Cneg = artmp.multiply(diag(this.weights)).multiply(artmp.transpose());
                this.f329C = this.f329C.scalarMultiply(oldFac2 + (0.5d * negccov)).add(roneu).add(arpos.scalarMultiply(this.ccovmu + (0.5d * negccov)).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose()))).subtract(Cneg.scalarMultiply(negccov));
            } else {
                this.f329C = this.f329C.scalarMultiply(oldFac2).add(roneu).add(arpos.scalarMultiply(this.ccovmu).multiply(times(repmat(this.weights, 1, this.dimension), arpos.transpose())));
            }
        }
        updateBD(negccov);
    }

    private void updateBD(double negccov) {
        if (this.ccov1 + this.ccovmu + negccov > 0.0d && (((((double) this.iterations) % 1.0d) / ((this.ccov1 + this.ccovmu) + negccov)) / ((double) this.dimension)) / 10.0d < 1.0d) {
            this.f329C = triu(this.f329C, 0).add(triu(this.f329C, 1).transpose());
            EigenDecomposition eig = new EigenDecomposition(this.f329C);
            this.f327B = eig.getV();
            this.f330D = eig.getD();
            this.diagD = diag(this.f330D);
            if (min(this.diagD) <= 0.0d) {
                for (int i = 0; i < this.dimension; i++) {
                    if (this.diagD.getEntry(i, 0) < 0.0d) {
                        this.diagD.setEntry(i, 0, 0.0d);
                    }
                }
                double tfac = max(this.diagD) / 1.0E14d;
                this.f329C = this.f329C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac));
                this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac));
            }
            if (max(this.diagD) > 1.0E14d * min(this.diagD)) {
                double tfac2 = (max(this.diagD) / 1.0E14d) - min(this.diagD);
                this.f329C = this.f329C.add(eye(this.dimension, this.dimension).scalarMultiply(tfac2));
                this.diagD = this.diagD.add(ones(this.dimension, 1).scalarMultiply(tfac2));
            }
            this.diagC = diag(this.f329C);
            this.diagD = sqrt(this.diagD);
            this.f328BD = times(this.f327B, repmat(this.diagD.transpose(), this.dimension, 1));
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

    private class FitnessFunction {
        private final boolean isRepairMode = true;
        private double valueRange = 1.0d;

        FitnessFunction() {
        }

        public double value(double[] point) {
            double value;
            if (this.isRepairMode) {
                double[] repaired = repair(point);
                value = CMAESOptimizer.this.computeObjectiveValue(repaired) + penalty(point, repaired);
            } else {
                value = CMAESOptimizer.this.computeObjectiveValue(point);
            }
            return CMAESOptimizer.this.isMinimize ? value : -value;
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

        public void setValueRange(double valueRange2) {
            this.valueRange = valueRange2;
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
                penalty += this.valueRange * FastMath.abs(x[i] - repaired[i]);
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
