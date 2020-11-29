package org.apache.commons.math3.stat.descriptive;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.ResizableDoubleArray;

public class DescriptiveStatistics implements StatisticalSummary, Serializable {
    public static final int INFINITE_WINDOW = -1;
    private static final String SET_QUANTILE_METHOD_NAME = "setQuantile";
    private static final long serialVersionUID = 4133067267405273064L;
    private ResizableDoubleArray eDA = new ResizableDoubleArray();
    private UnivariateStatistic geometricMeanImpl = new GeometricMean();
    private UnivariateStatistic kurtosisImpl = new Kurtosis();
    private UnivariateStatistic maxImpl = new Max();
    private UnivariateStatistic meanImpl = new Mean();
    private UnivariateStatistic minImpl = new Min();
    private UnivariateStatistic percentileImpl = new Percentile();
    private UnivariateStatistic skewnessImpl = new Skewness();
    private UnivariateStatistic sumImpl = new Sum();
    private UnivariateStatistic sumsqImpl = new SumOfSquares();
    private UnivariateStatistic varianceImpl = new Variance();
    protected int windowSize = -1;

    public DescriptiveStatistics() {
    }

    public DescriptiveStatistics(int window) throws MathIllegalArgumentException {
        setWindowSize(window);
    }

    public DescriptiveStatistics(double[] initialDoubleArray) {
        if (initialDoubleArray != null) {
            this.eDA = new ResizableDoubleArray(initialDoubleArray);
        }
    }

    public DescriptiveStatistics(DescriptiveStatistics original) throws NullArgumentException {
        copy(original, this);
    }

    public void addValue(double v) {
        if (this.windowSize == -1) {
            this.eDA.addElement(v);
        } else if (getN() == ((long) this.windowSize)) {
            this.eDA.addElementRolling(v);
        } else if (getN() < ((long) this.windowSize)) {
            this.eDA.addElement(v);
        }
    }

    public void removeMostRecentValue() throws MathIllegalStateException {
        try {
            this.eDA.discardMostRecentElements(1);
        } catch (MathIllegalArgumentException e) {
            throw new MathIllegalStateException(LocalizedFormats.NO_DATA, new Object[0]);
        }
    }

    public double replaceMostRecentValue(double v) throws MathIllegalStateException {
        return this.eDA.substituteMostRecentElement(v);
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMean() {
        return apply(this.meanImpl);
    }

    public double getGeometricMean() {
        return apply(this.geometricMeanImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getVariance() {
        return apply(this.varianceImpl);
    }

    public double getPopulationVariance() {
        return apply(new Variance(false));
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getStandardDeviation() {
        if (getN() <= 0) {
            return Double.NaN;
        }
        if (getN() > 1) {
            return FastMath.sqrt(getVariance());
        }
        return 0.0d;
    }

    public double getQuadraticMean() {
        long n = getN();
        if (n > 0) {
            return FastMath.sqrt(getSumsq() / ((double) n));
        }
        return Double.NaN;
    }

    public double getSkewness() {
        return apply(this.skewnessImpl);
    }

    public double getKurtosis() {
        return apply(this.kurtosisImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMax() {
        return apply(this.maxImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMin() {
        return apply(this.minImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public long getN() {
        return (long) this.eDA.getNumElements();
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getSum() {
        return apply(this.sumImpl);
    }

    public double getSumsq() {
        return apply(this.sumsqImpl);
    }

    public void clear() {
        this.eDA.clear();
    }

    public int getWindowSize() {
        return this.windowSize;
    }

    public void setWindowSize(int windowSize2) throws MathIllegalArgumentException {
        if (windowSize2 >= 1 || windowSize2 == -1) {
            this.windowSize = windowSize2;
            if (windowSize2 != -1 && windowSize2 < this.eDA.getNumElements()) {
                this.eDA.discardFrontElements(this.eDA.getNumElements() - windowSize2);
                return;
            }
            return;
        }
        throw new MathIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_WINDOW_SIZE, Integer.valueOf(windowSize2));
    }

    public double[] getValues() {
        return this.eDA.getElements();
    }

    public double[] getSortedValues() {
        double[] sort = getValues();
        Arrays.sort(sort);
        return sort;
    }

    public double getElement(int index) {
        return this.eDA.getElement(index);
    }

    public double getPercentile(double p) throws MathIllegalStateException, MathIllegalArgumentException {
        if (this.percentileImpl instanceof Percentile) {
            ((Percentile) this.percentileImpl).setQuantile(p);
        } else {
            try {
                this.percentileImpl.getClass().getMethod(SET_QUANTILE_METHOD_NAME, Double.TYPE).invoke(this.percentileImpl, Double.valueOf(p));
            } catch (NoSuchMethodException e) {
                throw new MathIllegalStateException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, this.percentileImpl.getClass().getName(), SET_QUANTILE_METHOD_NAME);
            } catch (IllegalAccessException e2) {
                throw new MathIllegalStateException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, SET_QUANTILE_METHOD_NAME, this.percentileImpl.getClass().getName());
            } catch (InvocationTargetException e3) {
                throw new IllegalStateException(e3.getCause());
            }
        }
        return apply(this.percentileImpl);
    }

    public String toString() {
        StringBuilder outBuffer = new StringBuilder();
        outBuffer.append("DescriptiveStatistics:").append("\n");
        outBuffer.append("n: ").append(getN()).append("\n");
        outBuffer.append("min: ").append(getMin()).append("\n");
        outBuffer.append("max: ").append(getMax()).append("\n");
        outBuffer.append("mean: ").append(getMean()).append("\n");
        outBuffer.append("std dev: ").append(getStandardDeviation()).append("\n");
        try {
            outBuffer.append("median: ").append(getPercentile(50.0d)).append("\n");
        } catch (MathIllegalStateException e) {
            outBuffer.append("median: unavailable").append("\n");
        }
        outBuffer.append("skewness: ").append(getSkewness()).append("\n");
        outBuffer.append("kurtosis: ").append(getKurtosis()).append("\n");
        return outBuffer.toString();
    }

    public double apply(UnivariateStatistic stat) {
        return this.eDA.compute(stat);
    }

    public synchronized UnivariateStatistic getMeanImpl() {
        return this.meanImpl;
    }

    public synchronized void setMeanImpl(UnivariateStatistic meanImpl2) {
        this.meanImpl = meanImpl2;
    }

    public synchronized UnivariateStatistic getGeometricMeanImpl() {
        return this.geometricMeanImpl;
    }

    public synchronized void setGeometricMeanImpl(UnivariateStatistic geometricMeanImpl2) {
        this.geometricMeanImpl = geometricMeanImpl2;
    }

    public synchronized UnivariateStatistic getKurtosisImpl() {
        return this.kurtosisImpl;
    }

    public synchronized void setKurtosisImpl(UnivariateStatistic kurtosisImpl2) {
        this.kurtosisImpl = kurtosisImpl2;
    }

    public synchronized UnivariateStatistic getMaxImpl() {
        return this.maxImpl;
    }

    public synchronized void setMaxImpl(UnivariateStatistic maxImpl2) {
        this.maxImpl = maxImpl2;
    }

    public synchronized UnivariateStatistic getMinImpl() {
        return this.minImpl;
    }

    public synchronized void setMinImpl(UnivariateStatistic minImpl2) {
        this.minImpl = minImpl2;
    }

    public synchronized UnivariateStatistic getPercentileImpl() {
        return this.percentileImpl;
    }

    public synchronized void setPercentileImpl(UnivariateStatistic percentileImpl2) throws MathIllegalArgumentException {
        try {
            percentileImpl2.getClass().getMethod(SET_QUANTILE_METHOD_NAME, Double.TYPE).invoke(percentileImpl2, Double.valueOf(50.0d));
            this.percentileImpl = percentileImpl2;
        } catch (NoSuchMethodException e) {
            throw new MathIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, percentileImpl2.getClass().getName(), SET_QUANTILE_METHOD_NAME);
        } catch (IllegalAccessException e2) {
            throw new MathIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, SET_QUANTILE_METHOD_NAME, percentileImpl2.getClass().getName());
        } catch (InvocationTargetException e3) {
            throw new IllegalArgumentException(e3.getCause());
        }
    }

    public synchronized UnivariateStatistic getSkewnessImpl() {
        return this.skewnessImpl;
    }

    public synchronized void setSkewnessImpl(UnivariateStatistic skewnessImpl2) {
        this.skewnessImpl = skewnessImpl2;
    }

    public synchronized UnivariateStatistic getVarianceImpl() {
        return this.varianceImpl;
    }

    public synchronized void setVarianceImpl(UnivariateStatistic varianceImpl2) {
        this.varianceImpl = varianceImpl2;
    }

    public synchronized UnivariateStatistic getSumsqImpl() {
        return this.sumsqImpl;
    }

    public synchronized void setSumsqImpl(UnivariateStatistic sumsqImpl2) {
        this.sumsqImpl = sumsqImpl2;
    }

    public synchronized UnivariateStatistic getSumImpl() {
        return this.sumImpl;
    }

    public synchronized void setSumImpl(UnivariateStatistic sumImpl2) {
        this.sumImpl = sumImpl2;
    }

    public DescriptiveStatistics copy() {
        DescriptiveStatistics result = new DescriptiveStatistics();
        copy(this, result);
        return result;
    }

    public static void copy(DescriptiveStatistics source, DescriptiveStatistics dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.eDA = source.eDA.copy();
        dest.windowSize = source.windowSize;
        dest.maxImpl = source.maxImpl.copy();
        dest.meanImpl = source.meanImpl.copy();
        dest.minImpl = source.minImpl.copy();
        dest.sumImpl = source.sumImpl.copy();
        dest.varianceImpl = source.varianceImpl.copy();
        dest.sumsqImpl = source.sumsqImpl.copy();
        dest.geometricMeanImpl = source.geometricMeanImpl.copy();
        dest.kurtosisImpl = source.kurtosisImpl;
        dest.skewnessImpl = source.skewnessImpl;
        dest.percentileImpl = source.percentileImpl;
    }
}
