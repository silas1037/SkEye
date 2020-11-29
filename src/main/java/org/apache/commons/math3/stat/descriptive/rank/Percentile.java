package org.apache.commons.math3.stat.descriptive.rank;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.KthSelector;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.MedianOf3PivotingStrategy;
import org.apache.commons.math3.util.PivotingStrategyInterface;
import org.apache.commons.math3.util.Precision;

public class Percentile extends AbstractUnivariateStatistic implements Serializable {
    private static final int MAX_CACHED_LEVELS = 10;
    private static final int PIVOTS_HEAP_LENGTH = 512;
    private static final long serialVersionUID = -8091216485095130416L;
    private int[] cachedPivots;
    private final EstimationType estimationType;
    private final KthSelector kthSelector;
    private final NaNStrategy nanStrategy;
    private double quantile;

    public Percentile() {
        this(50.0d);
    }

    public Percentile(double quantile2) throws MathIllegalArgumentException {
        this(quantile2, EstimationType.LEGACY, NaNStrategy.REMOVED, new KthSelector(new MedianOf3PivotingStrategy()));
    }

    public Percentile(Percentile original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.estimationType = original.getEstimationType();
        this.nanStrategy = original.getNaNStrategy();
        this.kthSelector = original.getKthSelector();
        setData(original.getDataRef());
        if (original.cachedPivots != null) {
            System.arraycopy(original.cachedPivots, 0, this.cachedPivots, 0, original.cachedPivots.length);
        }
        setQuantile(original.quantile);
    }

    protected Percentile(double quantile2, EstimationType estimationType2, NaNStrategy nanStrategy2, KthSelector kthSelector2) throws MathIllegalArgumentException {
        setQuantile(quantile2);
        this.cachedPivots = null;
        MathUtils.checkNotNull(estimationType2);
        MathUtils.checkNotNull(nanStrategy2);
        MathUtils.checkNotNull(kthSelector2);
        this.estimationType = estimationType2;
        this.nanStrategy = nanStrategy2;
        this.kthSelector = kthSelector2;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic
    public void setData(double[] values) {
        if (values == null) {
            this.cachedPivots = null;
        } else {
            this.cachedPivots = new int[PIVOTS_HEAP_LENGTH];
            Arrays.fill(this.cachedPivots, -1);
        }
        super.setData(values);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic
    public void setData(double[] values, int begin, int length) throws MathIllegalArgumentException {
        if (values == null) {
            this.cachedPivots = null;
        } else {
            this.cachedPivots = new int[PIVOTS_HEAP_LENGTH];
            Arrays.fill(this.cachedPivots, -1);
        }
        super.setData(values, begin, length);
    }

    public double evaluate(double p) throws MathIllegalArgumentException {
        return evaluate(getDataRef(), p);
    }

    public double evaluate(double[] values, double p) throws MathIllegalArgumentException {
        test(values, 0, 0);
        return evaluate(values, 0, values.length, p);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.util.MathArrays.Function
    public double evaluate(double[] values, int start, int length) throws MathIllegalArgumentException {
        return evaluate(values, start, length, this.quantile);
    }

    public double evaluate(double[] values, int begin, int length, double p) throws MathIllegalArgumentException {
        test(values, begin, length);
        if (p > 100.0d || p <= 0.0d) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, Double.valueOf(p), 0, 100);
        } else if (length == 0) {
            return Double.NaN;
        } else {
            if (length == 1) {
                return values[begin];
            }
            double[] work = getWorkArray(values, begin, length);
            int[] pivotsHeap = getPivots(values);
            if (work.length != 0) {
                return this.estimationType.evaluate(work, pivotsHeap, p, this.kthSelector);
            }
            return Double.NaN;
        }
    }

    /* access modifiers changed from: package-private */
    @Deprecated
    public int medianOf3(double[] work, int begin, int end) {
        return new MedianOf3PivotingStrategy().pivotIndex(work, begin, end);
    }

    public double getQuantile() {
        return this.quantile;
    }

    public void setQuantile(double p) throws MathIllegalArgumentException {
        if (p <= 0.0d || p > 100.0d) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, Double.valueOf(p), 0, 100);
        }
        this.quantile = p;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic
    public Percentile copy() {
        return new Percentile(this);
    }

    @Deprecated
    public static void copy(Percentile source, Percentile dest) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }

    /* access modifiers changed from: protected */
    public double[] getWorkArray(double[] values, int begin, int length) {
        if (values == getDataRef()) {
            return getDataRef();
        }
        switch (this.nanStrategy) {
            case MAXIMAL:
                return replaceAndSlice(values, begin, length, Double.NaN, Double.POSITIVE_INFINITY);
            case MINIMAL:
                return replaceAndSlice(values, begin, length, Double.NaN, Double.NEGATIVE_INFINITY);
            case REMOVED:
                return removeAndSlice(values, begin, length, Double.NaN);
            case FAILED:
                double[] work = copyOf(values, begin, length);
                MathArrays.checkNotNaN(work);
                return work;
            default:
                return copyOf(values, begin, length);
        }
    }

    private static double[] copyOf(double[] values, int begin, int length) {
        MathArrays.verifyValues(values, begin, length);
        return MathArrays.copyOfRange(values, begin, begin + length);
    }

    private static double[] replaceAndSlice(double[] values, int begin, int length, double original, double replacement) {
        double[] temp = copyOf(values, begin, length);
        for (int i = 0; i < length; i++) {
            temp[i] = Precision.equalsIncludingNaN(original, temp[i]) ? replacement : temp[i];
        }
        return temp;
    }

    private static double[] removeAndSlice(double[] values, int begin, int length, double removedValue) {
        MathArrays.verifyValues(values, begin, length);
        BitSet bits = new BitSet(length);
        for (int i = begin; i < begin + length; i++) {
            if (Precision.equalsIncludingNaN(removedValue, values[i])) {
                bits.set(i - begin);
            }
        }
        if (bits.isEmpty()) {
            return copyOf(values, begin, length);
        }
        if (bits.cardinality() == length) {
            return new double[0];
        }
        double[] temp = new double[(length - bits.cardinality())];
        int start = begin;
        int dest = 0;
        int bitSetPtr = 0;
        while (true) {
            int nextOne = bits.nextSetBit(bitSetPtr);
            if (nextOne == -1) {
                break;
            }
            int lengthToCopy = nextOne - bitSetPtr;
            System.arraycopy(values, start, temp, dest, lengthToCopy);
            dest += lengthToCopy;
            bitSetPtr = bits.nextClearBit(nextOne);
            start = begin + bitSetPtr;
        }
        if (start >= begin + length) {
            return temp;
        }
        System.arraycopy(values, start, temp, dest, (begin + length) - start);
        return temp;
    }

    private int[] getPivots(double[] values) {
        if (values == getDataRef()) {
            return this.cachedPivots;
        }
        int[] pivotsHeap = new int[PIVOTS_HEAP_LENGTH];
        Arrays.fill(pivotsHeap, -1);
        return pivotsHeap;
    }

    public EstimationType getEstimationType() {
        return this.estimationType;
    }

    public Percentile withEstimationType(EstimationType newEstimationType) {
        return new Percentile(this.quantile, newEstimationType, this.nanStrategy, this.kthSelector);
    }

    public NaNStrategy getNaNStrategy() {
        return this.nanStrategy;
    }

    public Percentile withNaNStrategy(NaNStrategy newNaNStrategy) {
        return new Percentile(this.quantile, this.estimationType, newNaNStrategy, this.kthSelector);
    }

    public KthSelector getKthSelector() {
        return this.kthSelector;
    }

    public PivotingStrategyInterface getPivotingStrategy() {
        return this.kthSelector.getPivotingStrategy();
    }

    public Percentile withKthSelector(KthSelector newKthSelector) {
        return new Percentile(this.quantile, this.estimationType, this.nanStrategy, newKthSelector);
    }

    /* JADX INFO: Failed to restore enum class, 'enum' modifier removed */
    public static abstract class EstimationType extends Enum<EstimationType> {
        private static final /* synthetic */ EstimationType[] $VALUES;
        public static final EstimationType LEGACY = new EstimationType("LEGACY", 0, "Legacy Apache Commons Math") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03341 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                if (Double.compare(p, 0.0d) == 0) {
                    return 0.0d;
                }
                return Double.compare(p, 1.0d) == 0 ? (double) length : ((double) (length + 1)) * p;
            }
        };
        public static final EstimationType R_1 = new EstimationType("R_1", 1, "R-1") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03362 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                if (Double.compare(p, 0.0d) == 0) {
                    return 0.0d;
                }
                return (((double) length) * p) + 0.5d;
            }

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double estimate(double[] values, int[] pivotsHeap, double pos, int length, KthSelector selector) {
                return super.estimate(values, pivotsHeap, FastMath.ceil(pos - 0.5d), length, selector);
            }
        };
        public static final EstimationType R_2 = new EstimationType("R_2", 2, "R-2") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03373 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                if (Double.compare(p, 1.0d) == 0) {
                    return (double) length;
                }
                if (Double.compare(p, 0.0d) != 0) {
                    return (((double) length) * p) + 0.5d;
                }
                return 0.0d;
            }

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double estimate(double[] values, int[] pivotsHeap, double pos, int length, KthSelector selector) {
                return (super.estimate(values, pivotsHeap, FastMath.ceil(pos - 0.5d), length, selector) + super.estimate(values, pivotsHeap, FastMath.floor(0.5d + pos), length, selector)) / 2.0d;
            }
        };
        public static final EstimationType R_3 = new EstimationType("R_3", 3, "R-3") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03384 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                if (Double.compare(p, 0.5d / ((double) length)) <= 0) {
                    return 0.0d;
                }
                return FastMath.rint(((double) length) * p);
            }
        };
        public static final EstimationType R_4 = new EstimationType("R_4", 4, "R-4") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03395 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                if (Double.compare(p, 1.0d / ((double) length)) < 0) {
                    return 0.0d;
                }
                return Double.compare(p, 1.0d) == 0 ? (double) length : ((double) length) * p;
            }
        };
        public static final EstimationType R_5 = new EstimationType("R_5", 5, "R-5") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03406 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                double maxLimit = (((double) length) - 0.5d) / ((double) length);
                if (Double.compare(p, 0.5d / ((double) length)) < 0) {
                    return 0.0d;
                }
                return Double.compare(p, maxLimit) >= 0 ? (double) length : (((double) length) * p) + 0.5d;
            }
        };
        public static final EstimationType R_6 = new EstimationType("R_6", 6, "R-6") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03417 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                double maxLimit = (((double) length) * 1.0d) / ((double) (length + 1));
                if (Double.compare(p, 1.0d / ((double) (length + 1))) < 0) {
                    return 0.0d;
                }
                return Double.compare(p, maxLimit) >= 0 ? (double) length : ((double) (length + 1)) * p;
            }
        };
        public static final EstimationType R_7 = new EstimationType("R_7", 7, "R-7") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03428 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                if (Double.compare(p, 0.0d) == 0) {
                    return 0.0d;
                }
                return Double.compare(p, 1.0d) == 0 ? (double) length : (((double) (length - 1)) * p) + 1.0d;
            }
        };
        public static final EstimationType R_8 = new EstimationType("R_8", 8, "R-8") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C03439 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                double maxLimit = (((double) length) - 0.3333333333333333d) / (((double) length) + 0.3333333333333333d);
                if (Double.compare(p, 0.6666666666666666d / (((double) length) + 0.3333333333333333d)) < 0) {
                    return 0.0d;
                }
                return Double.compare(p, maxLimit) >= 0 ? (double) length : ((((double) length) + 0.3333333333333333d) * p) + 0.3333333333333333d;
            }
        };
        public static final EstimationType R_9 = new EstimationType("R_9", 9, "R-9") {
            /* class org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType.C033510 */

            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile.EstimationType
            public double index(double p, int length) {
                double maxLimit = (((double) length) - 0.375d) / (((double) length) + 0.25d);
                if (Double.compare(p, 0.625d / (((double) length) + 0.25d)) < 0) {
                    return 0.0d;
                }
                return Double.compare(p, maxLimit) >= 0 ? (double) length : ((((double) length) + 0.25d) * p) + 0.375d;
            }
        };
        private final String name;

        /* access modifiers changed from: protected */
        public abstract double index(double d, int i);

        public static EstimationType valueOf(String name2) {
            return (EstimationType) Enum.valueOf(EstimationType.class, name2);
        }

        public static EstimationType[] values() {
            return (EstimationType[]) $VALUES.clone();
        }

        static {
            EstimationType[] estimationTypeArr = new EstimationType[Percentile.MAX_CACHED_LEVELS];
            estimationTypeArr[0] = LEGACY;
            estimationTypeArr[1] = R_1;
            estimationTypeArr[2] = R_2;
            estimationTypeArr[3] = R_3;
            estimationTypeArr[4] = R_4;
            estimationTypeArr[5] = R_5;
            estimationTypeArr[6] = R_6;
            estimationTypeArr[7] = R_7;
            estimationTypeArr[8] = R_8;
            estimationTypeArr[9] = R_9;
            $VALUES = estimationTypeArr;
        }

        private EstimationType(String str, int i, String type) {
            this.name = type;
        }

        /* access modifiers changed from: protected */
        public double estimate(double[] work, int[] pivotsHeap, double pos, int length, KthSelector selector) {
            double fpos = FastMath.floor(pos);
            int intPos = (int) fpos;
            double dif = pos - fpos;
            if (pos < 1.0d) {
                return selector.select(work, pivotsHeap, 0);
            }
            if (pos >= ((double) length)) {
                return selector.select(work, pivotsHeap, length - 1);
            }
            double lower = selector.select(work, pivotsHeap, intPos - 1);
            return ((selector.select(work, pivotsHeap, intPos) - lower) * dif) + lower;
        }

        /* access modifiers changed from: protected */
        public double evaluate(double[] work, int[] pivotsHeap, double p, KthSelector selector) {
            MathUtils.checkNotNull(work);
            if (p <= 100.0d && p > 0.0d) {
                return estimate(work, pivotsHeap, index(p / 100.0d, work.length), work.length, selector);
            }
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, Double.valueOf(p), 0, 100);
        }

        public double evaluate(double[] work, double p, KthSelector selector) {
            return evaluate(work, null, p, selector);
        }

        /* access modifiers changed from: package-private */
        public String getName() {
            return this.name;
        }
    }
}
