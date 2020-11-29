package org.apache.commons.math3.stat.correlation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;

public class SpearmansCorrelation {
    private final RealMatrix data;
    private final PearsonsCorrelation rankCorrelation;
    private final RankingAlgorithm rankingAlgorithm;

    public SpearmansCorrelation() {
        this(new NaturalRanking());
    }

    public SpearmansCorrelation(RankingAlgorithm rankingAlgorithm2) {
        this.data = null;
        this.rankingAlgorithm = rankingAlgorithm2;
        this.rankCorrelation = null;
    }

    public SpearmansCorrelation(RealMatrix dataMatrix) {
        this(dataMatrix, new NaturalRanking());
    }

    public SpearmansCorrelation(RealMatrix dataMatrix, RankingAlgorithm rankingAlgorithm2) {
        this.rankingAlgorithm = rankingAlgorithm2;
        this.data = rankTransform(dataMatrix);
        this.rankCorrelation = new PearsonsCorrelation(this.data);
    }

    public RealMatrix getCorrelationMatrix() {
        return this.rankCorrelation.getCorrelationMatrix();
    }

    public PearsonsCorrelation getRankCorrelation() {
        return this.rankCorrelation;
    }

    public RealMatrix computeCorrelationMatrix(RealMatrix matrix) {
        return new PearsonsCorrelation().computeCorrelationMatrix(rankTransform(matrix));
    }

    public RealMatrix computeCorrelationMatrix(double[][] matrix) {
        return computeCorrelationMatrix(new BlockRealMatrix(matrix));
    }

    public double correlation(double[] xArray, double[] yArray) {
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        } else if (xArray.length < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, Integer.valueOf(xArray.length), 2);
        } else {
            double[] x = xArray;
            double[] y = yArray;
            if ((this.rankingAlgorithm instanceof NaturalRanking) && NaNStrategy.REMOVED == ((NaturalRanking) this.rankingAlgorithm).getNanStrategy()) {
                Set<Integer> nanPositions = new HashSet<>();
                nanPositions.addAll(getNaNPositions(xArray));
                nanPositions.addAll(getNaNPositions(yArray));
                x = removeValues(xArray, nanPositions);
                y = removeValues(yArray, nanPositions);
            }
            return new PearsonsCorrelation().correlation(this.rankingAlgorithm.rank(x), this.rankingAlgorithm.rank(y));
        }
    }

    private RealMatrix rankTransform(RealMatrix matrix) {
        RealMatrix transformed = null;
        if ((this.rankingAlgorithm instanceof NaturalRanking) && ((NaturalRanking) this.rankingAlgorithm).getNanStrategy() == NaNStrategy.REMOVED) {
            Set<Integer> nanPositions = new HashSet<>();
            for (int i = 0; i < matrix.getColumnDimension(); i++) {
                nanPositions.addAll(getNaNPositions(matrix.getColumn(i)));
            }
            if (!nanPositions.isEmpty()) {
                transformed = new BlockRealMatrix(matrix.getRowDimension() - nanPositions.size(), matrix.getColumnDimension());
                for (int i2 = 0; i2 < transformed.getColumnDimension(); i2++) {
                    transformed.setColumn(i2, removeValues(matrix.getColumn(i2), nanPositions));
                }
            }
        }
        if (transformed == null) {
            transformed = matrix.copy();
        }
        for (int i3 = 0; i3 < transformed.getColumnDimension(); i3++) {
            transformed.setColumn(i3, this.rankingAlgorithm.rank(transformed.getColumn(i3)));
        }
        return transformed;
    }

    private List<Integer> getNaNPositions(double[] input) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < input.length; i++) {
            if (Double.isNaN(input[i])) {
                positions.add(Integer.valueOf(i));
            }
        }
        return positions;
    }

    private double[] removeValues(double[] input, Set<Integer> indices) {
        if (indices.isEmpty()) {
            return input;
        }
        double[] result = new double[(input.length - indices.size())];
        int j = 0;
        for (int i = 0; i < input.length; i++) {
            if (!indices.contains(Integer.valueOf(i))) {
                result[j] = input[i];
                j++;
            }
        }
        return result;
    }
}
