package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

public class SynchronizedDescriptiveStatistics extends DescriptiveStatistics {
    private static final long serialVersionUID = 1;

    public SynchronizedDescriptiveStatistics() {
        this(-1);
    }

    public SynchronizedDescriptiveStatistics(int window) throws MathIllegalArgumentException {
        super(window);
    }

    public SynchronizedDescriptiveStatistics(SynchronizedDescriptiveStatistics original) throws NullArgumentException {
        copy(original, this);
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized void addValue(double v) {
        super.addValue(v);
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized double apply(UnivariateStatistic stat) {
        return super.apply(stat);
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized void clear() {
        super.clear();
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized double getElement(int index) {
        return super.getElement(index);
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized long getN() {
        return super.getN();
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics, org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public synchronized double getStandardDeviation() {
        return super.getStandardDeviation();
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized double getQuadraticMean() {
        return super.getQuadraticMean();
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized double[] getValues() {
        return super.getValues();
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized int getWindowSize() {
        return super.getWindowSize();
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized void setWindowSize(int windowSize) throws MathIllegalArgumentException {
        super.setWindowSize(windowSize);
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized String toString() {
        return super.toString();
    }

    @Override // org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
    public synchronized SynchronizedDescriptiveStatistics copy() {
        SynchronizedDescriptiveStatistics result;
        result = new SynchronizedDescriptiveStatistics();
        copy(this, result);
        return result;
    }

    public static void copy(SynchronizedDescriptiveStatistics source, SynchronizedDescriptiveStatistics dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        synchronized (source) {
            synchronized (dest) {
                DescriptiveStatistics.copy(source, dest);
            }
        }
    }
}
