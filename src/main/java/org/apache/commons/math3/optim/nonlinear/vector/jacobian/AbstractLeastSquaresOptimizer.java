package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.nonlinear.vector.JacobianMultivariateVectorOptimizer;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public abstract class AbstractLeastSquaresOptimizer extends JacobianMultivariateVectorOptimizer {
    private double cost;
    private RealMatrix weightMatrixSqrt;

    protected AbstractLeastSquaresOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
    }

    /* access modifiers changed from: protected */
    public RealMatrix computeWeightedJacobian(double[] params) {
        return this.weightMatrixSqrt.multiply(MatrixUtils.createRealMatrix(computeJacobian(params)));
    }

    /* access modifiers changed from: protected */
    public double computeCost(double[] residuals) {
        ArrayRealVector r = new ArrayRealVector(residuals);
        return FastMath.sqrt(r.dotProduct(getWeight().operate(r)));
    }

    public double getRMS() {
        return FastMath.sqrt(getChiSquare() / ((double) getTargetSize()));
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

    public double[][] computeCovariances(double[] params, double threshold) {
        RealMatrix j = computeWeightedJacobian(params);
        return new QRDecomposition(j.transpose().multiply(j), threshold).getSolver().getInverse().getData();
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

    @Override // org.apache.commons.math3.optim.nonlinear.vector.JacobianMultivariateVectorOptimizer, org.apache.commons.math3.optim.nonlinear.vector.JacobianMultivariateVectorOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer, org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public PointVectorValuePair optimize(OptimizationData... optData) throws TooManyEvaluationsException {
        return super.optimize(optData);
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

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.nonlinear.vector.JacobianMultivariateVectorOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (OptimizationData data : optData) {
            if (data instanceof Weight) {
                this.weightMatrixSqrt = squareRoot(((Weight) data).getWeight());
                return;
            }
        }
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
