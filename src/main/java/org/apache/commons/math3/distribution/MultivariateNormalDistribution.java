package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class MultivariateNormalDistribution extends AbstractMultivariateRealDistribution {
    private final RealMatrix covarianceMatrix;
    private final double covarianceMatrixDeterminant;
    private final RealMatrix covarianceMatrixInverse;
    private final double[] means;
    private final RealMatrix samplingMatrix;

    public MultivariateNormalDistribution(double[] means2, double[][] covariances) throws SingularMatrixException, DimensionMismatchException, NonPositiveDefiniteMatrixException {
        this(new Well19937c(), means2, covariances);
    }

    public MultivariateNormalDistribution(RandomGenerator rng, double[] means2, double[][] covariances) throws SingularMatrixException, DimensionMismatchException, NonPositiveDefiniteMatrixException {
        super(rng, means2.length);
        int dim = means2.length;
        if (covariances.length != dim) {
            throw new DimensionMismatchException(covariances.length, dim);
        }
        for (int i = 0; i < dim; i++) {
            if (dim != covariances[i].length) {
                throw new DimensionMismatchException(covariances[i].length, dim);
            }
        }
        this.means = MathArrays.copyOf(means2);
        this.covarianceMatrix = new Array2DRowRealMatrix(covariances);
        EigenDecomposition covMatDec = new EigenDecomposition(this.covarianceMatrix);
        this.covarianceMatrixInverse = covMatDec.getSolver().getInverse();
        this.covarianceMatrixDeterminant = covMatDec.getDeterminant();
        double[] covMatEigenvalues = covMatDec.getRealEigenvalues();
        for (int i2 = 0; i2 < covMatEigenvalues.length; i2++) {
            if (covMatEigenvalues[i2] < 0.0d) {
                throw new NonPositiveDefiniteMatrixException(covMatEigenvalues[i2], i2, 0.0d);
            }
        }
        Array2DRowRealMatrix covMatEigenvectors = new Array2DRowRealMatrix(dim, dim);
        for (int v = 0; v < dim; v++) {
            covMatEigenvectors.setColumn(v, covMatDec.getEigenvector(v).toArray());
        }
        RealMatrix tmpMatrix = covMatEigenvectors.transpose();
        for (int row = 0; row < dim; row++) {
            double factor = FastMath.sqrt(covMatEigenvalues[row]);
            for (int col = 0; col < dim; col++) {
                tmpMatrix.multiplyEntry(row, col, factor);
            }
        }
        this.samplingMatrix = covMatEigenvectors.multiply(tmpMatrix);
    }

    public double[] getMeans() {
        return MathArrays.copyOf(this.means);
    }

    public RealMatrix getCovariances() {
        return this.covarianceMatrix.copy();
    }

    @Override // org.apache.commons.math3.distribution.MultivariateRealDistribution
    public double density(double[] vals) throws DimensionMismatchException {
        int dim = getDimension();
        if (vals.length == dim) {
            return FastMath.pow(6.283185307179586d, ((double) dim) * -0.5d) * FastMath.pow(this.covarianceMatrixDeterminant, -0.5d) * getExponentTerm(vals);
        }
        throw new DimensionMismatchException(vals.length, dim);
    }

    public double[] getStandardDeviations() {
        int dim = getDimension();
        double[] std = new double[dim];
        double[][] s = this.covarianceMatrix.getData();
        for (int i = 0; i < dim; i++) {
            std[i] = FastMath.sqrt(s[i][i]);
        }
        return std;
    }

    @Override // org.apache.commons.math3.distribution.AbstractMultivariateRealDistribution, org.apache.commons.math3.distribution.MultivariateRealDistribution
    public double[] sample() {
        int dim = getDimension();
        double[] normalVals = new double[dim];
        for (int i = 0; i < dim; i++) {
            normalVals[i] = this.random.nextGaussian();
        }
        double[] vals = this.samplingMatrix.operate(normalVals);
        for (int i2 = 0; i2 < dim; i2++) {
            vals[i2] = vals[i2] + this.means[i2];
        }
        return vals;
    }

    private double getExponentTerm(double[] values) {
        double[] centered = new double[values.length];
        for (int i = 0; i < centered.length; i++) {
            centered[i] = values[i] - getMeans()[i];
        }
        double[] preMultiplied = this.covarianceMatrixInverse.preMultiply(centered);
        double sum = 0.0d;
        for (int i2 = 0; i2 < preMultiplied.length; i2++) {
            sum += preMultiplied[i2] * centered[i2];
        }
        return FastMath.exp(-0.5d * sum);
    }
}
