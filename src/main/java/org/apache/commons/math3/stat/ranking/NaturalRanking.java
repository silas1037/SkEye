package org.apache.commons.math3.stat.ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

public class NaturalRanking implements RankingAlgorithm {
    public static final NaNStrategy DEFAULT_NAN_STRATEGY = NaNStrategy.FAILED;
    public static final TiesStrategy DEFAULT_TIES_STRATEGY = TiesStrategy.AVERAGE;
    private final NaNStrategy nanStrategy;
    private final RandomDataGenerator randomData;
    private final TiesStrategy tiesStrategy;

    public NaturalRanking() {
        this.tiesStrategy = DEFAULT_TIES_STRATEGY;
        this.nanStrategy = DEFAULT_NAN_STRATEGY;
        this.randomData = null;
    }

    public NaturalRanking(TiesStrategy tiesStrategy2) {
        this.tiesStrategy = tiesStrategy2;
        this.nanStrategy = DEFAULT_NAN_STRATEGY;
        this.randomData = new RandomDataGenerator();
    }

    public NaturalRanking(NaNStrategy nanStrategy2) {
        this.nanStrategy = nanStrategy2;
        this.tiesStrategy = DEFAULT_TIES_STRATEGY;
        this.randomData = null;
    }

    public NaturalRanking(NaNStrategy nanStrategy2, TiesStrategy tiesStrategy2) {
        this.nanStrategy = nanStrategy2;
        this.tiesStrategy = tiesStrategy2;
        this.randomData = new RandomDataGenerator();
    }

    public NaturalRanking(RandomGenerator randomGenerator) {
        this.tiesStrategy = TiesStrategy.RANDOM;
        this.nanStrategy = DEFAULT_NAN_STRATEGY;
        this.randomData = new RandomDataGenerator(randomGenerator);
    }

    public NaturalRanking(NaNStrategy nanStrategy2, RandomGenerator randomGenerator) {
        this.nanStrategy = nanStrategy2;
        this.tiesStrategy = TiesStrategy.RANDOM;
        this.randomData = new RandomDataGenerator(randomGenerator);
    }

    public NaNStrategy getNanStrategy() {
        return this.nanStrategy;
    }

    public TiesStrategy getTiesStrategy() {
        return this.tiesStrategy;
    }

    @Override // org.apache.commons.math3.stat.ranking.RankingAlgorithm
    public double[] rank(double[] data) {
        IntDoublePair[] ranks = new IntDoublePair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntDoublePair(data[i], i);
        }
        List<Integer> nanPositions = null;
        switch (this.nanStrategy) {
            case MAXIMAL:
                recodeNaNs(ranks, Double.POSITIVE_INFINITY);
                break;
            case MINIMAL:
                recodeNaNs(ranks, Double.NEGATIVE_INFINITY);
                break;
            case REMOVED:
                ranks = removeNaNs(ranks);
                break;
            case FIXED:
                nanPositions = getNanPositions(ranks);
                break;
            case FAILED:
                nanPositions = getNanPositions(ranks);
                if (nanPositions.size() > 0) {
                    throw new NotANumberException();
                }
                break;
            default:
                throw new MathInternalError();
        }
        Arrays.sort(ranks);
        double[] out = new double[ranks.length];
        int pos = 1;
        out[ranks[0].getPosition()] = (double) 1;
        List<Integer> tiesTrace = new ArrayList<>();
        tiesTrace.add(Integer.valueOf(ranks[0].getPosition()));
        for (int i2 = 1; i2 < ranks.length; i2++) {
            if (Double.compare(ranks[i2].getValue(), ranks[i2 - 1].getValue()) > 0) {
                pos = i2 + 1;
                if (tiesTrace.size() > 1) {
                    resolveTie(out, tiesTrace);
                }
                tiesTrace = new ArrayList<>();
                tiesTrace.add(Integer.valueOf(ranks[i2].getPosition()));
            } else {
                tiesTrace.add(Integer.valueOf(ranks[i2].getPosition()));
            }
            out[ranks[i2].getPosition()] = (double) pos;
        }
        if (tiesTrace.size() > 1) {
            resolveTie(out, tiesTrace);
        }
        if (this.nanStrategy == NaNStrategy.FIXED) {
            restoreNaNs(out, nanPositions);
        }
        return out;
    }

    private IntDoublePair[] removeNaNs(IntDoublePair[] ranks) {
        if (!containsNaNs(ranks)) {
            return ranks;
        }
        IntDoublePair[] outRanks = new IntDoublePair[ranks.length];
        int j = 0;
        for (int i = 0; i < ranks.length; i++) {
            if (Double.isNaN(ranks[i].getValue())) {
                for (int k = i + 1; k < ranks.length; k++) {
                    ranks[k] = new IntDoublePair(ranks[k].getValue(), ranks[k].getPosition() - 1);
                }
            } else {
                outRanks[j] = new IntDoublePair(ranks[i].getValue(), ranks[i].getPosition());
                j++;
            }
        }
        IntDoublePair[] returnRanks = new IntDoublePair[j];
        System.arraycopy(outRanks, 0, returnRanks, 0, j);
        return returnRanks;
    }

    private void recodeNaNs(IntDoublePair[] ranks, double value) {
        for (int i = 0; i < ranks.length; i++) {
            if (Double.isNaN(ranks[i].getValue())) {
                ranks[i] = new IntDoublePair(value, ranks[i].getPosition());
            }
        }
    }

    private boolean containsNaNs(IntDoublePair[] ranks) {
        for (IntDoublePair intDoublePair : ranks) {
            if (Double.isNaN(intDoublePair.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void resolveTie(double[] ranks, List<Integer> tiesTrace) {
        double c = ranks[tiesTrace.get(0).intValue()];
        int length = tiesTrace.size();
        switch (this.tiesStrategy) {
            case AVERAGE:
                fill(ranks, tiesTrace, (((2.0d * c) + ((double) length)) - 1.0d) / 2.0d);
                return;
            case MAXIMUM:
                fill(ranks, tiesTrace, (((double) length) + c) - 1.0d);
                return;
            case MINIMUM:
                fill(ranks, tiesTrace, c);
                return;
            case RANDOM:
                long f = FastMath.round(c);
                for (Integer num : tiesTrace) {
                    ranks[num.intValue()] = (double) this.randomData.nextLong(f, (((long) length) + f) - 1);
                }
                return;
            case SEQUENTIAL:
                long f2 = FastMath.round(c);
                int i = 0;
                for (Integer num2 : tiesTrace) {
                    ranks[num2.intValue()] = (double) (((long) i) + f2);
                    i++;
                }
                return;
            default:
                throw new MathInternalError();
        }
    }

    private void fill(double[] data, List<Integer> tiesTrace, double value) {
        for (Integer num : tiesTrace) {
            data[num.intValue()] = value;
        }
    }

    private void restoreNaNs(double[] ranks, List<Integer> nanPositions) {
        if (nanPositions.size() != 0) {
            for (Integer num : nanPositions) {
                ranks[num.intValue()] = Double.NaN;
            }
        }
    }

    private List<Integer> getNanPositions(IntDoublePair[] ranks) {
        ArrayList<Integer> out = new ArrayList<>();
        for (int i = 0; i < ranks.length; i++) {
            if (Double.isNaN(ranks[i].getValue())) {
                out.add(Integer.valueOf(i));
            }
        }
        return out;
    }

    /* access modifiers changed from: private */
    public static class IntDoublePair implements Comparable<IntDoublePair> {
        private final int position;
        private final double value;

        IntDoublePair(double value2, int position2) {
            this.value = value2;
            this.position = position2;
        }

        public int compareTo(IntDoublePair other) {
            return Double.compare(this.value, other.value);
        }

        public double getValue() {
            return this.value;
        }

        public int getPosition() {
            return this.position;
        }
    }
}
