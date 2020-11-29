package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class GLSMultipleLinearRegression extends AbstractMultipleLinearRegression {
    private RealMatrix Omega;
    private RealMatrix OmegaInverse;

    public void newSampleData(double[] y, double[][] x, double[][] covariance) {
        validateSampleData(x, y);
        newYSampleData(y);
        newXSampleData(x);
        validateCovarianceData(x, covariance);
        newCovarianceData(covariance);
    }

    /* access modifiers changed from: protected */
    public void newCovarianceData(double[][] omega) {
        this.Omega = new Array2DRowRealMatrix(omega);
        this.OmegaInverse = null;
    }

    /* access modifiers changed from: protected */
    public RealMatrix getOmegaInverse() {
        if (this.OmegaInverse == null) {
            this.OmegaInverse = new LUDecomposition(this.Omega).getSolver().getInverse();
        }
        return this.OmegaInverse;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression
    public RealVector calculateBeta() {
        RealMatrix OI = getOmegaInverse();
        RealMatrix XT = getX().transpose();
        return new LUDecomposition(XT.multiply(OI).multiply(getX())).getSolver().getInverse().multiply(XT).multiply(OI).operate(getY());
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression
    public RealMatrix calculateBetaVariance() {
        return new LUDecomposition(getX().transpose().multiply(getOmegaInverse()).multiply(getX())).getSolver().getInverse();
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression
    public double calculateErrorVariance() {
        RealVector residuals = calculateResiduals();
        return residuals.dotProduct(getOmegaInverse().operate(residuals)) / ((double) (getX().getRowDimension() - getX().getColumnDimension()));
    }
}
