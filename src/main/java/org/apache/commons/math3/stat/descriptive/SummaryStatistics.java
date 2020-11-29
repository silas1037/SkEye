package org.apache.commons.math3.stat.descriptive;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class SummaryStatistics implements StatisticalSummary, Serializable {
    private static final long serialVersionUID = -2021321786743555871L;
    private GeometricMean geoMean = new GeometricMean(this.sumLog);
    private StorelessUnivariateStatistic geoMeanImpl = this.geoMean;
    private Max max = new Max();
    private StorelessUnivariateStatistic maxImpl = this.max;
    private Mean mean = new Mean(this.secondMoment);
    private StorelessUnivariateStatistic meanImpl = this.mean;
    private Min min = new Min();
    private StorelessUnivariateStatistic minImpl = this.min;

    /* renamed from: n */
    private long f386n = 0;
    private SecondMoment secondMoment = new SecondMoment();
    private Sum sum = new Sum();
    private StorelessUnivariateStatistic sumImpl = this.sum;
    private SumOfLogs sumLog = new SumOfLogs();
    private StorelessUnivariateStatistic sumLogImpl = this.sumLog;
    private SumOfSquares sumsq = new SumOfSquares();
    private StorelessUnivariateStatistic sumsqImpl = this.sumsq;
    private Variance variance = new Variance(this.secondMoment);
    private StorelessUnivariateStatistic varianceImpl = this.variance;

    public SummaryStatistics() {
    }

    public SummaryStatistics(SummaryStatistics original) throws NullArgumentException {
        copy(original, this);
    }

    public StatisticalSummary getSummary() {
        return new StatisticalSummaryValues(getMean(), getVariance(), getN(), getMax(), getMin(), getSum());
    }

    public void addValue(double value) {
        this.sumImpl.increment(value);
        this.sumsqImpl.increment(value);
        this.minImpl.increment(value);
        this.maxImpl.increment(value);
        this.sumLogImpl.increment(value);
        this.secondMoment.increment(value);
        if (this.meanImpl != this.mean) {
            this.meanImpl.increment(value);
        }
        if (this.varianceImpl != this.variance) {
            this.varianceImpl.increment(value);
        }
        if (this.geoMeanImpl != this.geoMean) {
            this.geoMeanImpl.increment(value);
        }
        this.f386n++;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public long getN() {
        return this.f386n;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getSum() {
        return this.sumImpl.getResult();
    }

    public double getSumsq() {
        return this.sumsqImpl.getResult();
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMean() {
        return this.meanImpl.getResult();
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
        long size = getN();
        if (size > 0) {
            return FastMath.sqrt(getSumsq() / ((double) size));
        }
        return Double.NaN;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getVariance() {
        return this.varianceImpl.getResult();
    }

    public double getPopulationVariance() {
        Variance populationVariance = new Variance(this.secondMoment);
        populationVariance.setBiasCorrected(false);
        return populationVariance.getResult();
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMax() {
        return this.maxImpl.getResult();
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMin() {
        return this.minImpl.getResult();
    }

    public double getGeometricMean() {
        return this.geoMeanImpl.getResult();
    }

    public double getSumOfLogs() {
        return this.sumLogImpl.getResult();
    }

    public double getSecondMoment() {
        return this.secondMoment.getResult();
    }

    public String toString() {
        StringBuilder outBuffer = new StringBuilder();
        outBuffer.append("SummaryStatistics:").append("\n");
        outBuffer.append("n: ").append(getN()).append("\n");
        outBuffer.append("min: ").append(getMin()).append("\n");
        outBuffer.append("max: ").append(getMax()).append("\n");
        outBuffer.append("sum: ").append(getSum()).append("\n");
        outBuffer.append("mean: ").append(getMean()).append("\n");
        outBuffer.append("geometric mean: ").append(getGeometricMean()).append("\n");
        outBuffer.append("variance: ").append(getVariance()).append("\n");
        outBuffer.append("population variance: ").append(getPopulationVariance()).append("\n");
        outBuffer.append("second moment: ").append(getSecondMoment()).append("\n");
        outBuffer.append("sum of squares: ").append(getSumsq()).append("\n");
        outBuffer.append("standard deviation: ").append(getStandardDeviation()).append("\n");
        outBuffer.append("sum of logs: ").append(getSumOfLogs()).append("\n");
        return outBuffer.toString();
    }

    public void clear() {
        this.f386n = 0;
        this.minImpl.clear();
        this.maxImpl.clear();
        this.sumImpl.clear();
        this.sumLogImpl.clear();
        this.sumsqImpl.clear();
        this.geoMeanImpl.clear();
        this.secondMoment.clear();
        if (this.meanImpl != this.mean) {
            this.meanImpl.clear();
        }
        if (this.varianceImpl != this.variance) {
            this.varianceImpl.clear();
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof SummaryStatistics)) {
            return false;
        }
        SummaryStatistics stat = (SummaryStatistics) object;
        return Precision.equalsIncludingNaN(stat.getGeometricMean(), getGeometricMean()) && Precision.equalsIncludingNaN(stat.getMax(), getMax()) && Precision.equalsIncludingNaN(stat.getMean(), getMean()) && Precision.equalsIncludingNaN(stat.getMin(), getMin()) && Precision.equalsIncludingNaN((float) stat.getN(), (float) getN()) && Precision.equalsIncludingNaN(stat.getSum(), getSum()) && Precision.equalsIncludingNaN(stat.getSumsq(), getSumsq()) && Precision.equalsIncludingNaN(stat.getVariance(), getVariance());
    }

    public int hashCode() {
        return ((((((((((((((((MathUtils.hash(getGeometricMean()) + 31) * 31) + MathUtils.hash(getGeometricMean())) * 31) + MathUtils.hash(getMax())) * 31) + MathUtils.hash(getMean())) * 31) + MathUtils.hash(getMin())) * 31) + MathUtils.hash((double) getN())) * 31) + MathUtils.hash(getSum())) * 31) + MathUtils.hash(getSumsq())) * 31) + MathUtils.hash(getVariance());
    }

    public StorelessUnivariateStatistic getSumImpl() {
        return this.sumImpl;
    }

    public void setSumImpl(StorelessUnivariateStatistic sumImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.sumImpl = sumImpl2;
    }

    public StorelessUnivariateStatistic getSumsqImpl() {
        return this.sumsqImpl;
    }

    public void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.sumsqImpl = sumsqImpl2;
    }

    public StorelessUnivariateStatistic getMinImpl() {
        return this.minImpl;
    }

    public void setMinImpl(StorelessUnivariateStatistic minImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.minImpl = minImpl2;
    }

    public StorelessUnivariateStatistic getMaxImpl() {
        return this.maxImpl;
    }

    public void setMaxImpl(StorelessUnivariateStatistic maxImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.maxImpl = maxImpl2;
    }

    public StorelessUnivariateStatistic getSumLogImpl() {
        return this.sumLogImpl;
    }

    public void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.sumLogImpl = sumLogImpl2;
        this.geoMean.setSumLogImpl(sumLogImpl2);
    }

    public StorelessUnivariateStatistic getGeoMeanImpl() {
        return this.geoMeanImpl;
    }

    public void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.geoMeanImpl = geoMeanImpl2;
    }

    public StorelessUnivariateStatistic getMeanImpl() {
        return this.meanImpl;
    }

    public void setMeanImpl(StorelessUnivariateStatistic meanImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.meanImpl = meanImpl2;
    }

    public StorelessUnivariateStatistic getVarianceImpl() {
        return this.varianceImpl;
    }

    public void setVarianceImpl(StorelessUnivariateStatistic varianceImpl2) throws MathIllegalStateException {
        checkEmpty();
        this.varianceImpl = varianceImpl2;
    }

    private void checkEmpty() throws MathIllegalStateException {
        if (this.f386n > 0) {
            throw new MathIllegalStateException(LocalizedFormats.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, Long.valueOf(this.f386n));
        }
    }

    public SummaryStatistics copy() {
        SummaryStatistics result = new SummaryStatistics();
        copy(this, result);
        return result;
    }

    public static void copy(SummaryStatistics source, SummaryStatistics dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.maxImpl = source.maxImpl.copy();
        dest.minImpl = source.minImpl.copy();
        dest.sumImpl = source.sumImpl.copy();
        dest.sumLogImpl = source.sumLogImpl.copy();
        dest.sumsqImpl = source.sumsqImpl.copy();
        dest.secondMoment = source.secondMoment.copy();
        dest.f386n = source.f386n;
        if (source.getVarianceImpl() instanceof Variance) {
            dest.varianceImpl = new Variance(dest.secondMoment);
        } else {
            dest.varianceImpl = source.varianceImpl.copy();
        }
        if (source.meanImpl instanceof Mean) {
            dest.meanImpl = new Mean(dest.secondMoment);
        } else {
            dest.meanImpl = source.meanImpl.copy();
        }
        if (source.getGeoMeanImpl() instanceof GeometricMean) {
            dest.geoMeanImpl = new GeometricMean((SumOfLogs) dest.sumLogImpl);
        } else {
            dest.geoMeanImpl = source.geoMeanImpl.copy();
        }
        if (source.geoMean == source.geoMeanImpl) {
            dest.geoMean = (GeometricMean) dest.geoMeanImpl;
        } else {
            GeometricMean.copy(source.geoMean, dest.geoMean);
        }
        if (source.max == source.maxImpl) {
            dest.max = (Max) dest.maxImpl;
        } else {
            Max.copy(source.max, dest.max);
        }
        if (source.mean == source.meanImpl) {
            dest.mean = (Mean) dest.meanImpl;
        } else {
            Mean.copy(source.mean, dest.mean);
        }
        if (source.min == source.minImpl) {
            dest.min = (Min) dest.minImpl;
        } else {
            Min.copy(source.min, dest.min);
        }
        if (source.sum == source.sumImpl) {
            dest.sum = (Sum) dest.sumImpl;
        } else {
            Sum.copy(source.sum, dest.sum);
        }
        if (source.variance == source.varianceImpl) {
            dest.variance = (Variance) dest.varianceImpl;
        } else {
            Variance.copy(source.variance, dest.variance);
        }
        if (source.sumLog == source.sumLogImpl) {
            dest.sumLog = (SumOfLogs) dest.sumLogImpl;
        } else {
            SumOfLogs.copy(source.sumLog, dest.sumLog);
        }
        if (source.sumsq == source.sumsqImpl) {
            dest.sumsq = (SumOfSquares) dest.sumsqImpl;
        } else {
            SumOfSquares.copy(source.sumsq, dest.sumsq);
        }
    }
}
