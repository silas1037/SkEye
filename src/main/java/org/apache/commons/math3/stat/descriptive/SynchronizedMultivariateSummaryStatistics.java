package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.linear.RealMatrix;

public class SynchronizedMultivariateSummaryStatistics extends MultivariateSummaryStatistics {
    private static final long serialVersionUID = 7099834153347155363L;

    public SynchronizedMultivariateSummaryStatistics(int k, boolean isCovarianceBiasCorrected) {
        super(k, isCovarianceBiasCorrected);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void addValue(double[] value) throws DimensionMismatchException {
        super.addValue(value);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized int getDimension() {
        return super.getDimension();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized long getN() {
        return super.getN();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getSum() {
        return super.getSum();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getSumSq() {
        return super.getSumSq();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getSumLog() {
        return super.getSumLog();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getMean() {
        return super.getMean();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getStandardDeviation() {
        return super.getStandardDeviation();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized RealMatrix getCovariance() {
        return super.getCovariance();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getMax() {
        return super.getMax();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getMin() {
        return super.getMin();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics, org.apache.commons.math3.stat.descriptive.StatisticalMultivariateSummary
    public synchronized double[] getGeometricMean() {
        return super.getGeometricMean();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized String toString() {
        return super.toString();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void clear() {
        super.clear();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized boolean equals(Object object) {
        return super.equals(object);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized int hashCode() {
        return super.hashCode();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized StorelessUnivariateStatistic[] getSumImpl() {
        return super.getSumImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void setSumImpl(StorelessUnivariateStatistic[] sumImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setSumImpl(sumImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized StorelessUnivariateStatistic[] getSumsqImpl() {
        return super.getSumsqImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void setSumsqImpl(StorelessUnivariateStatistic[] sumsqImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setSumsqImpl(sumsqImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized StorelessUnivariateStatistic[] getMinImpl() {
        return super.getMinImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void setMinImpl(StorelessUnivariateStatistic[] minImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setMinImpl(minImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized StorelessUnivariateStatistic[] getMaxImpl() {
        return super.getMaxImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void setMaxImpl(StorelessUnivariateStatistic[] maxImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setMaxImpl(maxImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized StorelessUnivariateStatistic[] getSumLogImpl() {
        return super.getSumLogImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void setSumLogImpl(StorelessUnivariateStatistic[] sumLogImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setSumLogImpl(sumLogImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized StorelessUnivariateStatistic[] getGeoMeanImpl() {
        return super.getGeoMeanImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void setGeoMeanImpl(StorelessUnivariateStatistic[] geoMeanImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setGeoMeanImpl(geoMeanImpl);
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized StorelessUnivariateStatistic[] getMeanImpl() {
        return super.getMeanImpl();
    }

    @Override // org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics
    public synchronized void setMeanImpl(StorelessUnivariateStatistic[] meanImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setMeanImpl(meanImpl);
    }
}
