package org.apache.commons.math3.stat.correlation;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;

public class KendallsCorrelation {
    private final RealMatrix correlationMatrix;

    public KendallsCorrelation() {
        this.correlationMatrix = null;
    }

    public KendallsCorrelation(double[][] data) {
        this(MatrixUtils.createRealMatrix(data));
    }

    public KendallsCorrelation(RealMatrix matrix) {
        this.correlationMatrix = computeCorrelationMatrix(matrix);
    }

    public RealMatrix getCorrelationMatrix() {
        return this.correlationMatrix;
    }

    public RealMatrix computeCorrelationMatrix(RealMatrix matrix) {
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

    public RealMatrix computeCorrelationMatrix(double[][] matrix) {
        return computeCorrelationMatrix(new BlockRealMatrix(matrix));
    }

    public double correlation(double[] xArray, double[] yArray) throws DimensionMismatchException {
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        }
        int n = xArray.length;
        long numPairs = sum((long) (n - 1));
        Pair<Double, Double>[] pairs = new Pair[n];
        for (int i = 0; i < n; i++) {
            pairs[i] = new Pair<>(Double.valueOf(xArray[i]), Double.valueOf(yArray[i]));
        }
        Arrays.sort(pairs, new Comparator<Pair<Double, Double>>() {
            /* class org.apache.commons.math3.stat.correlation.KendallsCorrelation.C03311 */

            public int compare(Pair<Double, Double> pair1, Pair<Double, Double> pair2) {
                int compareFirst = pair1.getFirst().compareTo(pair2.getFirst());
                return compareFirst != 0 ? compareFirst : pair1.getSecond().compareTo(pair2.getSecond());
            }
        });
        long tiedXPairs = 0;
        long tiedXYPairs = 0;
        long consecutiveXTies = 1;
        long consecutiveXYTies = 1;
        Pair<Double, Double> prev = pairs[0];
        for (int i2 = 1; i2 < n; i2++) {
            Pair<Double, Double> curr = pairs[i2];
            if (curr.getFirst().equals(prev.getFirst())) {
                consecutiveXTies++;
                if (curr.getSecond().equals(prev.getSecond())) {
                    consecutiveXYTies++;
                } else {
                    tiedXYPairs += sum(consecutiveXYTies - 1);
                    consecutiveXYTies = 1;
                }
            } else {
                tiedXPairs += sum(consecutiveXTies - 1);
                consecutiveXTies = 1;
                tiedXYPairs += sum(consecutiveXYTies - 1);
                consecutiveXYTies = 1;
            }
            prev = curr;
        }
        long tiedXPairs2 = tiedXPairs + sum(consecutiveXTies - 1);
        long tiedXYPairs2 = tiedXYPairs + sum(consecutiveXYTies - 1);
        long swaps = 0;
        Pair<Double, Double>[] pairsDestination = new Pair[n];
        for (int segmentSize = 1; segmentSize < n; segmentSize <<= 1) {
            for (int offset = 0; offset < n; offset += segmentSize * 2) {
                int i3 = offset;
                int iEnd = FastMath.min(i3 + segmentSize, n);
                int j = iEnd;
                int jEnd = FastMath.min(j + segmentSize, n);
                int copyLocation = offset;
                while (true) {
                    if (i3 >= iEnd && j >= jEnd) {
                        break;
                    }
                    if (i3 >= iEnd) {
                        pairsDestination[copyLocation] = pairs[j];
                        j++;
                    } else if (j >= jEnd) {
                        pairsDestination[copyLocation] = pairs[i3];
                        i3++;
                    } else if (pairs[i3].getSecond().compareTo(pairs[j].getSecond()) <= 0) {
                        pairsDestination[copyLocation] = pairs[i3];
                        i3++;
                    } else {
                        pairsDestination[copyLocation] = pairs[j];
                        j++;
                        swaps += (long) (iEnd - i3);
                    }
                    copyLocation++;
                }
            }
            pairs = pairsDestination;
            pairsDestination = pairs;
        }
        long tiedYPairs = 0;
        long consecutiveYTies = 1;
        Pair<Double, Double> prev2 = pairs[0];
        for (int i4 = 1; i4 < n; i4++) {
            Pair<Double, Double> curr2 = pairs[i4];
            if (curr2.getSecond().equals(prev2.getSecond())) {
                consecutiveYTies++;
            } else {
                tiedYPairs += sum(consecutiveYTies - 1);
                consecutiveYTies = 1;
            }
            prev2 = curr2;
        }
        long tiedYPairs2 = tiedYPairs + sum(consecutiveYTies - 1);
        return ((double) ((((numPairs - tiedXPairs2) - tiedYPairs2) + tiedXYPairs2) - (2 * swaps))) / FastMath.sqrt(((double) (numPairs - tiedXPairs2)) * ((double) (numPairs - tiedYPairs2)));
    }

    private static long sum(long n) {
        return ((1 + n) * n) / 2;
    }
}
