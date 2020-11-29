package org.apache.commons.math3.optimization.general;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.optimization.Target;
import org.apache.commons.math3.optimization.Weight;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateVectorOptimizer;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public abstract class AbstractLeastSquaresOptimizer extends BaseAbstractMultivariateVectorOptimizer<DifferentiableMultivariateVectorFunction> implements DifferentiableMultivariateVectorOptimizer {
    @Deprecated
    private static final double DEFAULT_SINGULARITY_THRESHOLD = 1.0E-14d;
    @Deprecated
    protected int cols;
    @Deprecated
    protected double cost;

    /* renamed from: jF */
    private MultivariateDifferentiableVectorFunction f341jF;
    private int jacobianEvaluations;
    @Deprecated
    protected double[] objective;
    @Deprecated
    protected double[] point;
    @Deprecated
    protected int rows;
    private RealMatrix weightMatrixSqrt;
    @Deprecated
    protected double[][] weightedResidualJacobian;
    @Deprecated
    protected double[] weightedResiduals;

    @Deprecated
    protected AbstractLeastSquaresOptimizer() {
    }

    protected AbstractLeastSquaresOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
    }

    public int getJacobianEvaluations() {
        return this.jacobianEvaluations;
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void updateJacobian() {
        this.weightedResidualJacobian = computeWeightedJacobian(this.point).scalarMultiply(-1.0d).getData();
    }

    /* access modifiers changed from: protected */
    public RealMatrix computeWeightedJacobian(double[] params) {
        this.jacobianEvaluations++;
        DerivativeStructure[] dsPoint = new DerivativeStructure[params.length];
        int nC = params.length;
        for (int i = 0; i < nC; i++) {
            dsPoint[i] = new DerivativeStructure(nC, 1, i, params[i]);
        }
        DerivativeStructure[] dsValue = this.f341jF.value(dsPoint);
        int nR = getTarget().length;
        if (dsValue.length != nR) {
            throw new DimensionMismatchException(dsValue.length, nR);
        }
        double[][] jacobianData = (double[][]) Array.newInstance(Double.TYPE, nR, nC);
        for (int i2 = 0; i2 < nR; i2++) {
            int[] orders = new int[nC];
            for (int j = 0; j < nC; j++) {
                orders[j] = 1;
                jacobianData[i2][j] = dsValue[i2].getPartialDerivative(orders);
                orders[j] = 0;
            }
        }
        return this.weightMatrixSqrt.multiply(MatrixUtils.createRealMatrix(jacobianData));
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void updateResidualsAndCost() {
        this.objective = computeObjectiveValue(this.point);
        double[] res = computeResiduals(this.objective);
        this.cost = computeCost(res);
        this.weightedResiduals = this.weightMatrixSqrt.operate(new ArrayRealVector(res)).toArray();
    }

    /* access modifiers changed from: protected */
    public double computeCost(double[] residuals) {
        ArrayRealVector r = new ArrayRealVector(residuals);
        return FastMath.sqrt(r.dotProduct(getWeight().operate(r)));
    }

    public double getRMS() {
        return FastMath.sqrt(getChiSquare() / ((double) this.rows));
    }

    public double getChiSquare() {
        return this.cost * this.cost;
    }

    public RealMatrix getWeightSquareRoot() {
        return this.weightMatrixSqrt.copy();
    }

    /* access modifiers changed from: protected */
    public void setCost(double cost2) {
        this.cost = cost2;
    }

    @Deprecated
    public double[][] getCovariances() {
        return getCovariances(DEFAULT_SINGULARITY_THRESHOLD);
    }

    @Deprecated
    public double[][] getCovariances(double threshold) {
        return computeCovariances(this.point, threshold);
    }

    public double[][] computeCovariances(double[] params, double threshold) {
        RealMatrix j = computeWeightedJacobian(params);
        return new QRDecomposition(j.transpose().multiply(j), threshold).getSolver().getInverse().getData();
    }

    @Deprecated
    public double[] guessParametersErrors() {
        if (this.rows <= this.cols) {
            throw new NumberIsTooSmallException(LocalizedFormats.NO_DEGREES_OF_FREEDOM, Integer.valueOf(this.rows), Integer.valueOf(this.cols), false);
        }
        double[] errors = new double[this.cols];
        double c = FastMath.sqrt(getChiSquare() / ((double) (this.rows - this.cols)));
        double[][] covar = computeCovariances(this.point, DEFAULT_SINGULARITY_THRESHOLD);
        for (int i = 0; i < errors.length; i++) {
            errors[i] = FastMath.sqrt(covar[i][i]) * c;
        }
        return errors;
    }

    public double[] computeSigma(double[] params, double covarianceSingularityThreshold) {
        int nC = params.length;
        double[] sig = new double[nC];
        double[][] cov = computeCovariances(params, covarianceSingularityThreshold);
        for (int i = 0; i < nC; i++) {
            sig[i] = FastMath.sqrt(cov[i][i]);
        }
        return sig;
    }

    @Deprecated
    public PointVectorValuePair optimize(int maxEval, DifferentiableMultivariateVectorFunction f, double[] target, double[] weights, double[] startPoint) {
        return optimizeInternal(maxEval, FunctionUtils.toMultivariateDifferentiableVectorFunction(f), new Target(target), new Weight(weights), new InitialGuess(startPoint));
    }

    @Deprecated
    public PointVectorValuePair optimize(int maxEval, MultivariateDifferentiableVectorFunction f, double[] target, double[] weights, double[] startPoint) {
        return optimizeInternal(maxEval, f, new Target(target), new Weight(weights), new InitialGuess(startPoint));
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public PointVectorValuePair optimizeInternal(int maxEval, MultivariateDifferentiableVectorFunction f, OptimizationData... optData) {
        return super.optimizeInternal(maxEval, FunctionUtils.toDifferentiableMultivariateVectorFunction(f), optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateVectorOptimizer
    public void setUp() {
        super.setUp();
        this.jacobianEvaluations = 0;
        this.weightMatrixSqrt = squareRoot(getWeight());
        this.f341jF = FunctionUtils.toMultivariateDifferentiableVectorFunction((DifferentiableMultivariateVectorFunction) getObjectiveFunction());
        this.point = getStartPoint();
        this.rows = getTarget().length;
        this.cols = this.point.length;
    }

    /* access modifiers changed from: protected */
    public double[] computeResiduals(double[] objectiveValue) {
        double[] target = getTarget();
        if (objectiveValue.length != target.length) {
            throw new DimensionMismatchException(target.length, objectiveValue.length);
        }
        double[] residuals = new double[target.length];
        for (int i = 0; i < target.length; i++) {
            residuals[i] = target[i] - objectiveValue[i];
        }
        return residuals;
    }

    private RealMatrix squareRoot(RealMatrix m) {
        if (!(m instanceof DiagonalMatrix)) {
            return new EigenDecomposition(m).getSquareRoot();
        }
        int dim = m.getRowDimension();
        RealMatrix sqrtM = new DiagonalMatrix(dim);
        for (int i = 0; i < dim; i++) {
            sqrtM.setEntry(i, i, FastMath.sqrt(m.getEntry(i, i)));
        }
        return sqrtM;
    }
}
