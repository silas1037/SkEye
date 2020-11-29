package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

public class SynchronizedSummaryStatistics extends SummaryStatistics {
    private static final long serialVersionUID = 1909861009042253704L;

    public SynchronizedSummaryStatistics() {
    }

    public SynchronizedSummaryStatistics(SynchronizedSummaryStatistics original) throws NullArgumentException {
        copy(original, this);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StatisticalSummary getSummary() {
        return super.getSummary();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void addValue(double value) {
        super.addValue(value);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized long getN() {
        return super.getN();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized double getSum() {
        return super.getSum();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized double getSumsq() {
        return super.getSumsq();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized double getMean() {
        return super.getMean();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized double getStandardDeviation() {
        return super.getStandardDeviation();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized double getQuadraticMean() {
        return super.getQuadraticMean();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized double getVariance() {
        return super.getVariance();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized double getPopulationVariance() {
        return super.getPopulationVariance();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized double getMax() {
        return super.getMax();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized double getMin() {
        return super.getMin();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized double getGeometricMean() {
        return super.getGeometricMean();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized String toString() {
        return super.toString();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void clear() {
        super.clear();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized boolean equals(Object object) {
        return super.equals(object);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized int hashCode() {
        return super.hashCode();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getSumImpl() {
        return super.getSumImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setSumImpl(StorelessUnivariateStatistic sumImpl) throws MathIllegalStateException {
        super.setSumImpl(sumImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getSumsqImpl() {
        return super.getSumsqImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl) throws MathIllegalStateException {
        super.setSumsqImpl(sumsqImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getMinImpl() {
        return super.getMinImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setMinImpl(StorelessUnivariateStatistic minImpl) throws MathIllegalStateException {
        super.setMinImpl(minImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getMaxImpl() {
        return super.getMaxImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setMaxImpl(StorelessUnivariateStatistic maxImpl) throws MathIllegalStateException {
        super.setMaxImpl(maxImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getSumLogImpl() {
        return super.getSumLogImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl) throws MathIllegalStateException {
        super.setSumLogImpl(sumLogImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getGeoMeanImpl() {
        return super.getGeoMeanImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl) throws MathIllegalStateException {
        super.setGeoMeanImpl(geoMeanImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getMeanImpl() {
        return super.getMeanImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setMeanImpl(StorelessUnivariateStatistic meanImpl) throws MathIllegalStateException {
        super.setMeanImpl(meanImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized StorelessUnivariateStatistic getVarianceImpl() {
        return super.getVarianceImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized void setVarianceImpl(StorelessUnivariateStatistic varianceImpl) throws MathIllegalStateException {
        super.setVarianceImpl(varianceImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
    public synchronized SynchronizedSummaryStatistics copy() {
        SynchronizedSummaryStatistics result;
        result = new SynchronizedSummaryStatistics();
        copy(this, result);
        return result;
    }

    public static void copy(SynchronizedSummaryStatistics source, SynchronizedSummaryStatistics dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        synchronized (source) {
            synchronized (dest) {
                SummaryStatistics.copy(source, dest);
            }
        }
    }
}
