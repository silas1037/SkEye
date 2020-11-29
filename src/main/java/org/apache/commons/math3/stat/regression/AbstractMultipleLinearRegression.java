package org.apache.commons.math3.stat.regression;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.util.FastMath;

public abstract class AbstractMultipleLinearRegression implements MultipleLinearRegression {
    private boolean noIntercept = false;
    private RealMatrix xMatrix;
    private RealVector yVector;

    /* access modifiers changed from: protected */
    public abstract RealVector calculateBeta();

    /* access modifiers changed from: protected */
    public abstract RealMatrix calculateBetaVariance();

    /* access modifiers changed from: protected */
    public RealMatrix getX() {
        return this.xMatrix;
    }

    /* access modifiers changed from: protected */
    public RealVector getY() {
        return this.yVector;
    }

    public boolean isNoIntercept() {
        return this.noIntercept;
    }

    public void setNoIntercept(boolean noIntercept2) {
        this.noIntercept = noIntercept2;
    }

    public void newSampleData(double[] data, int nobs, int nvars) {
        if (data == null) {
            throw new NullArgumentException();
        } else if (data.length != (nvars + 1) * nobs) {
            throw new DimensionMismatchException(data.length, (nvars + 1) * nobs);
        } else if (nobs <= nvars) {
            throw new InsufficientDataException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, Integer.valueOf(nobs), Integer.valueOf(nvars + 1));
        } else {
            double[] y = new double[nobs];
            int cols = this.noIntercept ? nvars : nvars + 1;
            double[][] x = (double[][]) Array.newInstance(Double.TYPE, nobs, cols);
            int pointer = 0;
            for (int i = 0; i < nobs; i++) {
                int pointer2 = pointer + 1;
                y[i] = data[pointer];
                if (!this.noIntercept) {
                    x[i][0] = 1.0d;
                }
                int j = this.noIntercept ? 0 : 1;
                pointer = pointer2;
                while (j < cols) {
                    x[i][j] = data[pointer];
                    j++;
                    pointer++;
                }
            }
            this.xMatrix = new Array2DRowRealMatrix(x);
            this.yVector = new ArrayRealVector(y);
        }
    }

    /* access modifiers changed from: protected */
    public void newYSampleData(double[] y) {
        if (y == null) {
            throw new NullArgumentException();
        } else if (y.length == 0) {
            throw new NoDataException();
        } else {
            this.yVector = new ArrayRealVector(y);
        }
    }

    /* access modifiers changed from: protected */
    public void newXSampleData(double[][] x) {
        if (x == null) {
            throw new NullArgumentException();
        } else if (x.length == 0) {
            throw new NoDataException();
        } else if (this.noIntercept) {
            this.xMatrix = new Array2DRowRealMatrix(x, true);
        } else {
            int nVars = x[0].length;
            double[][] xAug = (double[][]) Array.newInstance(Double.TYPE, x.length, nVars + 1);
            for (int i = 0; i < x.length; i++) {
                if (x[i].length != nVars) {
                    throw new DimensionMismatchException(x[i].length, nVars);
                }
                xAug[i][0] = 1.0d;
                System.arraycopy(x[i], 0, xAug[i], 1, nVars);
            }
            this.xMatrix = new Array2DRowRealMatrix(xAug, false);
        }
    }

    /* access modifiers changed from: protected */
    public void validateSampleData(double[][] x, double[] y) throws MathIllegalArgumentException {
        if (x == null || y == null) {
            throw new NullArgumentException();
        } else if (x.length != y.length) {
            throw new DimensionMismatchException(y.length, x.length);
        } else if (x.length == 0) {
            throw new NoDataException();
        } else if (x[0].length + 1 > x.length) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, Integer.valueOf(x.length), Integer.valueOf(x[0].length));
        }
    }

    /* access modifiers changed from: protected */
    public void validateCovarianceData(double[][] x, double[][] covariance) {
        if (x.length != covariance.length) {
            throw new DimensionMismatchException(x.length, covariance.length);
        } else if (covariance.length > 0 && covariance.length != covariance[0].length) {
            throw new NonSquareMatrixException(covariance.length, covariance[0].length);
        }
    }

    @Override // org.apache.commons.math3.stat.regression.MultipleLinearRegression
    public double[] estimateRegressionParameters() {
        return calculateBeta().toArray();
    }

    @Override // org.apache.commons.math3.stat.regression.MultipleLinearRegression
    public double[] estimateResiduals() {
        return this.yVector.subtract(this.xMatrix.operate(calculateBeta())).toArray();
    }

    @Override // org.apache.commons.math3.stat.regression.MultipleLinearRegression
    public double[][] estimateRegressionParametersVariance() {
        return calculateBetaVariance().getData();
    }

    @Override // org.apache.commons.math3.stat.regression.MultipleLinearRegression
    public double[] estimateRegressionParametersStandardErrors() {
        double[][] betaVariance = estimateRegressionParametersVariance();
        double sigma = calculateErrorVariance();
        int length = betaVariance[0].length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = FastMath.sqrt(betaVariance[i][i] * sigma);
        }
        return result;
    }

    @Override // org.apache.commons.math3.stat.regression.MultipleLinearRegression
    public double estimateRegressandVariance() {
        return calculateYVariance();
    }

    public double estimateErrorVariance() {
        return calculateErrorVariance();
    }

    public double estimateRegressionStandardError() {
        return FastMath.sqrt(estimateErrorVariance());
    }

    /* access modifiers changed from: protected */
    public double calculateYVariance() {
        return new Variance().evaluate(this.yVector.toArray());
    }

    /* access modifiers changed from: protected */
    public double calculateErrorVariance() {
        RealVector residuals = calculateResiduals();
        return residuals.dotProduct(residuals) / ((double) (this.xMatrix.getRowDimension() - this.xMatrix.getColumnDimension()));
    }

    /* access modifiers changed from: protected */
    public RealVector calculateResiduals() {
        return this.yVector.subtract(this.xMatrix.operate(calculateBeta()));
    }
}
