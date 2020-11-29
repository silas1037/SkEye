package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.SecondMoment;

public class OLSMultipleLinearRegression extends AbstractMultipleLinearRegression {

    /* renamed from: qr */
    private QRDecomposition f404qr;
    private final double threshold;

    public OLSMultipleLinearRegression() {
        this(0.0d);
    }

    public OLSMultipleLinearRegression(double threshold2) {
        this.f404qr = null;
        this.threshold = threshold2;
    }

    public void newSampleData(double[] y, double[][] x) throws MathIllegalArgumentException {
        validateSampleData(x, y);
        newYSampleData(y);
        newXSampleData(x);
    }

    @Override // org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression
    public void newSampleData(double[] data, int nobs, int nvars) {
        super.newSampleData(data, nobs, nvars);
        this.f404qr = new QRDecomposition(getX(), this.threshold);
    }

    public RealMatrix calculateHat() {
        RealMatrix Q = this.f404qr.getQ();
        int p = this.f404qr.getR().getColumnDimension();
        int n = Q.getColumnDimension();
        Array2DRowRealMatrix augI = new Array2DRowRealMatrix(n, n);
        double[][] augIData = augI.getDataRef();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j || i >= p) {
                    augIData[i][j] = 0.0d;
                } else {
                    augIData[i][j] = 1.0d;
                }
            }
        }
        return Q.multiply(augI).multiply(Q.transpose());
    }

    public double calculateTotalSumOfSquares() {
        if (isNoIntercept()) {
            return StatUtils.sumSq(getY().toArray());
        }
        return new SecondMoment().evaluate(getY().toArray());
    }

    public double calculateResidualSumOfSquares() {
        RealVector residuals = calculateResiduals();
        return residuals.dotProduct(residuals);
    }

    public double calculateRSquared() {
        return 1.0d - (calculateResidualSumOfSquares() / calculateTotalSumOfSquares());
    }

    public double calculateAdjustedRSquared() {
        double n = (double) getX().getRowDimension();
        if (isNoIntercept()) {
            return 1.0d - ((1.0d - calculateRSquared()) * (n / (n - ((double) getX().getColumnDimension()))));
        }
        return 1.0d - ((calculateResidualSumOfSquares() * (n - 1.0d)) / (calculateTotalSumOfSquares() * (n - ((double) getX().getColumnDimension()))));
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression
    public void newXSampleData(double[][] x) {
        super.newXSampleData(x);
        this.f404qr = new QRDecomposition(getX(), this.threshold);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression
    public RealVector calculateBeta() {
        return this.f404qr.getSolver().solve(getY());
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.stat.regression.AbstractMultipleLinearRegression
    public RealMatrix calculateBetaVariance() {
        int p = getX().getColumnDimension();
        RealMatrix Rinv = new LUDecomposition(this.f404qr.getR().getSubMatrix(0, p - 1, 0, p - 1)).getSolver().getInverse();
        return Rinv.multiply(Rinv.transpose());
    }
}
