package org.apache.commons.math3.stat.descriptive;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.math3.exception.NullArgumentException;

public class AggregateSummaryStatistics implements StatisticalSummary, Serializable {
    private static final long serialVersionUID = -8207112444016386906L;
    private final SummaryStatistics statistics;
    private final SummaryStatistics statisticsPrototype;

    public AggregateSummaryStatistics() {
        this(new SummaryStatistics());
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public AggregateSummaryStatistics(SummaryStatistics prototypeStatistics) throws NullArgumentException {
        this(prototypeStatistics, prototypeStatistics == null ? null : new SummaryStatistics(prototypeStatistics));
    }

    public AggregateSummaryStatistics(SummaryStatistics prototypeStatistics, SummaryStatistics initialStatistics) {
        this.statisticsPrototype = prototypeStatistics == null ? new SummaryStatistics() : prototypeStatistics;
        this.statistics = initialStatistics == null ? new SummaryStatistics() : initialStatistics;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMax() {
        double max;
        synchronized (this.statistics) {
            max = this.statistics.getMax();
        }
        return max;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMean() {
        double mean;
        synchronized (this.statistics) {
            mean = this.statistics.getMean();
        }
        return mean;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getMin() {
        double min;
        synchronized (this.statistics) {
            min = this.statistics.getMin();
        }
        return min;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public long getN() {
        long n;
        synchronized (this.statistics) {
            n = this.statistics.getN();
        }
        return n;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getStandardDeviation() {
        double standardDeviation;
        synchronized (this.statistics) {
            standardDeviation = this.statistics.getStandardDeviation();
        }
        return standardDeviation;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getSum() {
        double sum;
        synchronized (this.statistics) {
            sum = this.statistics.getSum();
        }
        return sum;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StatisticalSummary
    public double getVariance() {
        double variance;
        synchronized (this.statistics) {
            variance = this.statistics.getVariance();
        }
        return variance;
    }

    public double getSumOfLogs() {
        double sumOfLogs;
        synchronized (this.statistics) {
            sumOfLogs = this.statistics.getSumOfLogs();
        }
        return sumOfLogs;
    }

    public double getGeometricMean() {
        double geometricMean;
        synchronized (this.statistics) {
            geometricMean = this.statistics.getGeometricMean();
        }
        return geometricMean;
    }

    public double getSumsq() {
        double sumsq;
        synchronized (this.statistics) {
            sumsq = this.statistics.getSumsq();
        }
        return sumsq;
    }

    public double getSecondMoment() {
        double secondMoment;
        synchronized (this.statistics) {
            secondMoment = this.statistics.getSecondMoment();
        }
        return secondMoment;
    }

    public StatisticalSummary getSummary() {
        StatisticalSummaryValues statisticalSummaryValues;
        synchronized (this.statistics) {
            statisticalSummaryValues = new StatisticalSummaryValues(getMean(), getVariance(), getN(), getMax(), getMin(), getSum());
        }
        return statisticalSummaryValues;
    }

    public SummaryStatistics createContributingStatistics() {
        SummaryStatistics contributingStatistics = new AggregatingSummaryStatistics(this.statistics);
        SummaryStatistics.copy(this.statisticsPrototype, contributingStatistics);
        return contributingStatistics;
    }

    public static StatisticalSummaryValues aggregate(Collection<? extends StatisticalSummary> statistics2) {
        double variance;
        if (statistics2 == null) {
            return null;
        }
        Iterator<? extends StatisticalSummary> iterator = statistics2.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        StatisticalSummary current = (StatisticalSummary) iterator.next();
        long n = current.getN();
        double min = current.getMin();
        double sum = current.getSum();
        double max = current.getMax();
        double m2 = current.getVariance() * (((double) n) - 1.0d);
        double mean = current.getMean();
        while (iterator.hasNext()) {
            StatisticalSummary current2 = (StatisticalSummary) iterator.next();
            if (current2.getMin() < min || Double.isNaN(min)) {
                min = current2.getMin();
            }
            if (current2.getMax() > max || Double.isNaN(max)) {
                max = current2.getMax();
            }
            sum += current2.getSum();
            double oldN = (double) n;
            double curN = (double) current2.getN();
            n = (long) (((double) n) + curN);
            double meanDiff = current2.getMean() - mean;
            mean = sum / ((double) n);
            m2 = m2 + (current2.getVariance() * (curN - 1.0d)) + ((((meanDiff * meanDiff) * oldN) * curN) / ((double) n));
        }
        if (n == 0) {
            variance = Double.NaN;
        } else if (n == 1) {
            variance = 0.0d;
        } else {
            variance = m2 / ((double) (n - 1));
        }
        return new StatisticalSummaryValues(mean, variance, n, max, min, sum);
    }

    private static class AggregatingSummaryStatistics extends SummaryStatistics {
        private static final long serialVersionUID = 1;
        private final SummaryStatistics aggregateStatistics;

        AggregatingSummaryStatistics(SummaryStatistics aggregateStatistics2) {
            this.aggregateStatistics = aggregateStatistics2;
        }

        @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
        public void addValue(double value) {
            super.addValue(value);
            synchronized (this.aggregateStatistics) {
                this.aggregateStatistics.addValue(value);
            }
        }

        @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof AggregatingSummaryStatistics)) {
                return false;
            }
            AggregatingSummaryStatistics stat = (AggregatingSummaryStatistics) object;
            return super.equals(stat) && this.aggregateStatistics.equals(stat.aggregateStatistics);
        }

        @Override // org.apache.commons.math3.stat.descriptive.SummaryStatistics
        public int hashCode() {
            return super.hashCode() + 123 + this.aggregateStatistics.hashCode();
        }
    }
}
