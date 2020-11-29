package org.apache.commons.math3.stat.correlation;

import java.lang.reflect.Array;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

public class PearsonsCorrelation {
    private final RealMatrix correlationMatrix;
    private final int nObs;

    public PearsonsCorrelation() {
        this.correlationMatrix = null;
        this.nObs = 0;
    }

    public PearsonsCorrelation(double[][] data) {
        this(new BlockRealMatrix(data));
    }

    public PearsonsCorrelation(RealMatrix matrix) {
        this.nObs = matrix.getRowDimension();
        this.correlationMatrix = computeCorrelationMatrix(matrix);
    }

    public PearsonsCorrelation(Covariance covariance) {
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        if (covarianceMatrix == null) {
            throw new NullArgumentException(LocalizedFormats.COVARIANCE_MATRIX, new Object[0]);
        }
        this.nObs = covariance.getN();
        this.correlationMatrix = covarianceToCorrelation(covarianceMatrix);
    }

    public PearsonsCorrelation(RealMatrix covarianceMatrix, int numberOfObservations) {
        this.nObs = numberOfObservations;
        this.correlationMatrix = covarianceToCorrelation(covarianceMatrix);
    }

    public RealMatrix getCorrelationMatrix() {
        return this.correlationMatrix;
    }

    public RealMatrix getCorrelationStandardErrors() {
        int nVars = this.correlationMatrix.getColumnDimension();
        double[][] out = (double[][]) Array.newInstance(Double.TYPE, nVars, nVars);
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < nVars; j++) {
                double r = this.correlationMatrix.getEntry(i, j);
                out[i][j] = FastMath.sqrt((1.0d - (r * r)) / ((double) (this.nObs - 2)));
            }
        }
        return new BlockRealMatrix(out);
    }

    public RealMatrix getCorrelationPValues() {
        TDistribution tDistribution = new TDistribution((double) (this.nObs - 2));
        int nVars = this.correlationMatrix.getColumnDimension();
        double[][] out = (double[][]) Array.newInstance(Double.TYPE, nVars, nVars);
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < nVars; j++) {
                if (i == j) {
                    out[i][j] = 0.0d;
                } else {
                    double r = this.correlationMatrix.getEntry(i, j);
                    out[i][j] = 2.0d * tDistribution.cumulativeProbability(-FastMath.abs(FastMath.sqrt(((double) (this.nObs - 2)) / (1.0d - (r * r))) * r));
                }
            }
        }
        return new BlockRealMatrix(out);
    }

    public RealMatrix computeCorrelationMatrix(RealMatrix matrix) {
        checkSufficientData(matrix);
        int nVars = matrix.getColumnDimension();
        RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);
        for (int i = 0; i < nVars; i++) {
            for (int j = 0; j < i; j++) {
                double corr = correlation(matrix.getColumn(i), matrix.getColumn(j));
                outMatrix.setEntry(i, j, corr);
                outMatrix.setEntry(j, i, corr);
            }
            outMatrix.setEntry(i, i, 1.0d);
        }
        return outMatrix;
    }

    public RealMatrix computeCorrelationMatrix(double[][] data) {
        return computeCorrelationMatrix(new BlockRealMatrix(data));
    }

    public double correlation(double[] xArray, double[] yArray) {
        SimpleRegression regression = new SimpleRegression();
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        } else if (xArray.length < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, Integer.valueOf(xArray.length), 2);
        } else {
            for (int i = 0; i < xArray.length; i++) {
                regression.addData(xArray[i], yArray[i]);
            }
            return regression.getR();
        }
    }

    public RealMatrix covarianceToCorrelation(RealMatrix covarianceMatrix) {
        int nVars = covarianceMatrix.getColumnDimension();
        RealMatrix outMatrix = new BlockRealMatrix(nVars, nVars);
        for (int i = 0; i < nVars; i++) {
            double sigma = FastMath.sqrt(covarianceMatrix.getEntry(i, i));
            outMatrix.setEntry(i, i, 1.0d);
            for (int j = 0; j < i; j++) {
                double entry = covarianceMatrix.getEntry(i, j) / (FastMath.sqrt(covarianceMatrix.getEntry(j, j)) * sigma);
                outMatrix.setEntry(i, j, entry);
                outMatrix.setEntry(j, i, entry);
            }
        }
        return outMatrix;
    }

    private void checkSufficientData(RealMatrix matrix) {
        int nRows = matrix.getRowDimension();
        int nCols = matrix.getColumnDimension();
        if (nRows < 2 || nCols < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_ROWS_AND_COLUMNS, Integer.valueOf(nRows), Integer.valueOf(nCols));
        }
    }
}
