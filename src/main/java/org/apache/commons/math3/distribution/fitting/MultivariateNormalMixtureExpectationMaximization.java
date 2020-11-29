package org.apache.commons.math3.distribution.fitting;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Pair;

public class MultivariateNormalMixtureExpectationMaximization {
    private static final int DEFAULT_MAX_ITERATIONS = 1000;
    private static final double DEFAULT_THRESHOLD = 1.0E-5d;
    private final double[][] data;
    private MixtureMultivariateNormalDistribution fittedModel;
    private double logLikelihood = 0.0d;

    public MultivariateNormalMixtureExpectationMaximization(double[][] data2) throws NotStrictlyPositiveException, DimensionMismatchException, NumberIsTooSmallException {
        if (data2.length < 1) {
            throw new NotStrictlyPositiveException(Integer.valueOf(data2.length));
        }
        this.data = (double[][]) Array.newInstance(Double.TYPE, data2.length, data2[0].length);
        for (int i = 0; i < data2.length; i++) {
            if (data2[i].length != data2[0].length) {
                throw new DimensionMismatchException(data2[i].length, data2[0].length);
            } else if (data2[i].length < 2) {
                throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_TOO_SMALL, Integer.valueOf(data2[i].length), 2, true);
            } else {
                this.data[i] = MathArrays.copyOf(data2[i], data2[i].length);
            }
        }
    }

    public void fit(MixtureMultivariateNormalDistribution initialMixture, int maxIterations, double threshold) throws SingularMatrixException, NotStrictlyPositiveException, DimensionMismatchException {
        if (maxIterations < 1) {
            throw new NotStrictlyPositiveException(Integer.valueOf(maxIterations));
        } else if (threshold < Double.MIN_VALUE) {
            throw new NotStrictlyPositiveException(Double.valueOf(threshold));
        } else {
            int n = this.data.length;
            int numCols = this.data[0].length;
            int k = initialMixture.getComponents().size();
            int numMeanColumns = ((MultivariateNormalDistribution) ((Pair) initialMixture.getComponents().get(0)).getSecond()).getMeans().length;
            if (numMeanColumns != numCols) {
                throw new DimensionMismatchException(numMeanColumns, numCols);
            }
            int numIterations = 0;
            double previousLogLikelihood = 0.0d;
            this.logLikelihood = Double.NEGATIVE_INFINITY;
            this.fittedModel = new MixtureMultivariateNormalDistribution(initialMixture.getComponents());
            while (true) {
                numIterations++;
                if (numIterations <= maxIterations && FastMath.abs(previousLogLikelihood - this.logLikelihood) > threshold) {
                    previousLogLikelihood = this.logLikelihood;
                    double sumLogLikelihood = 0.0d;
                    List<Pair<Double, MultivariateNormalDistribution>> components = this.fittedModel.getComponents();
                    double[] weights = new double[k];
                    MultivariateNormalDistribution[] mvns = new MultivariateNormalDistribution[k];
                    for (int j = 0; j < k; j++) {
                        weights[j] = components.get(j).getFirst().doubleValue();
                        mvns[j] = components.get(j).getSecond();
                    }
                    double[][] gamma = (double[][]) Array.newInstance(Double.TYPE, n, k);
                    double[] gammaSums = new double[k];
                    double[][] gammaDataProdSums = (double[][]) Array.newInstance(Double.TYPE, k, numCols);
                    for (int i = 0; i < n; i++) {
                        double rowDensity = this.fittedModel.density(this.data[i]);
                        sumLogLikelihood += FastMath.log(rowDensity);
                        for (int j2 = 0; j2 < k; j2++) {
                            gamma[i][j2] = (weights[j2] * mvns[j2].density(this.data[i])) / rowDensity;
                            gammaSums[j2] = gammaSums[j2] + gamma[i][j2];
                            for (int col = 0; col < numCols; col++) {
                                double[] dArr = gammaDataProdSums[j2];
                                dArr[col] = dArr[col] + (gamma[i][j2] * this.data[i][col]);
                            }
                        }
                    }
                    this.logLikelihood = sumLogLikelihood / ((double) n);
                    double[] newWeights = new double[k];
                    double[][] newMeans = (double[][]) Array.newInstance(Double.TYPE, k, numCols);
                    for (int j3 = 0; j3 < k; j3++) {
                        newWeights[j3] = gammaSums[j3] / ((double) n);
                        for (int col2 = 0; col2 < numCols; col2++) {
                            newMeans[j3][col2] = gammaDataProdSums[j3][col2] / gammaSums[j3];
                        }
                    }
                    RealMatrix[] newCovMats = new RealMatrix[k];
                    for (int j4 = 0; j4 < k; j4++) {
                        newCovMats[j4] = new Array2DRowRealMatrix(numCols, numCols);
                    }
                    for (int i2 = 0; i2 < n; i2++) {
                        for (int j5 = 0; j5 < k; j5++) {
                            RealMatrix vec = new Array2DRowRealMatrix(MathArrays.ebeSubtract(this.data[i2], newMeans[j5]));
                            newCovMats[j5] = newCovMats[j5].add(vec.multiply(vec.transpose()).scalarMultiply(gamma[i2][j5]));
                        }
                    }
                    double[][][] newCovMatArrays = (double[][][]) Array.newInstance(Double.TYPE, k, numCols, numCols);
                    for (int j6 = 0; j6 < k; j6++) {
                        newCovMats[j6] = newCovMats[j6].scalarMultiply(1.0d / gammaSums[j6]);
                        newCovMatArrays[j6] = newCovMats[j6].getData();
                    }
                    this.fittedModel = new MixtureMultivariateNormalDistribution(newWeights, newMeans, newCovMatArrays);
                }
            }
            if (FastMath.abs(previousLogLikelihood - this.logLikelihood) > threshold) {
                throw new ConvergenceException();
            }
        }
    }

    public void fit(MixtureMultivariateNormalDistribution initialMixture) throws SingularMatrixException, NotStrictlyPositiveException {
        fit(initialMixture, 1000, DEFAULT_THRESHOLD);
    }

    public static MixtureMultivariateNormalDistribution estimate(double[][] data2, int numComponents) throws NotStrictlyPositiveException, DimensionMismatchException {
        if (data2.length < 2) {
            throw new NotStrictlyPositiveException(Integer.valueOf(data2.length));
        } else if (numComponents < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(numComponents), 2, true);
        } else if (numComponents > data2.length) {
            throw new NumberIsTooLargeException(Integer.valueOf(numComponents), Integer.valueOf(data2.length), true);
        } else {
            int numRows = data2.length;
            int numCols = data2[0].length;
            DataRow[] sortedData = new DataRow[numRows];
            for (int i = 0; i < numRows; i++) {
                sortedData[i] = new DataRow(data2[i]);
            }
            Arrays.sort(sortedData);
            double weight = 1.0d / ((double) numComponents);
            List<Pair<Double, MultivariateNormalDistribution>> components = new ArrayList<>(numComponents);
            for (int binIndex = 0; binIndex < numComponents; binIndex++) {
                int minIndex = (binIndex * numRows) / numComponents;
                int maxIndex = ((binIndex + 1) * numRows) / numComponents;
                int numBinRows = maxIndex - minIndex;
                double[][] binData = (double[][]) Array.newInstance(Double.TYPE, numBinRows, numCols);
                double[] columnMeans = new double[numCols];
                int i2 = minIndex;
                int iBin = 0;
                while (i2 < maxIndex) {
                    for (int j = 0; j < numCols; j++) {
                        double val = sortedData[i2].getRow()[j];
                        columnMeans[j] = columnMeans[j] + val;
                        binData[iBin][j] = val;
                    }
                    i2++;
                    iBin++;
                }
                MathArrays.scaleInPlace(1.0d / ((double) numBinRows), columnMeans);
                components.add(new Pair<>(Double.valueOf(weight), new MultivariateNormalDistribution(columnMeans, new Covariance(binData).getCovarianceMatrix().getData())));
            }
            return new MixtureMultivariateNormalDistribution(components);
        }
    }

    public double getLogLikelihood() {
        return this.logLikelihood;
    }

    public MixtureMultivariateNormalDistribution getFittedModel() {
        return new MixtureMultivariateNormalDistribution(this.fittedModel.getComponents());
    }

    private static class DataRow implements Comparable<DataRow> {
        private Double mean = Double.valueOf(0.0d);
        private final double[] row;

        DataRow(double[] data) {
            this.row = data;
            for (double d : data) {
                this.mean = Double.valueOf(this.mean.doubleValue() + d);
            }
            this.mean = Double.valueOf(this.mean.doubleValue() / ((double) data.length));
        }

        public int compareTo(DataRow other) {
            return this.mean.compareTo(other.mean);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other instanceof DataRow) {
                return MathArrays.equals(this.row, ((DataRow) other).row);
            }
            return false;
        }

        public int hashCode() {
            return Arrays.hashCode(this.row);
        }

        public double[] getRow() {
            return this.row;
        }
    }
}
