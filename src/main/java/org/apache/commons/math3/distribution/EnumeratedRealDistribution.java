package org.apache.commons.math3.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.Pair;

public class EnumeratedRealDistribution extends AbstractRealDistribution {
    private static final long serialVersionUID = 20130308;
    protected final EnumeratedDistribution<Double> innerDistribution;

    public EnumeratedRealDistribution(double[] singletons, double[] probabilities) throws DimensionMismatchException, NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        this(new Well19937c(), singletons, probabilities);
    }

    public EnumeratedRealDistribution(RandomGenerator rng, double[] singletons, double[] probabilities) throws DimensionMismatchException, NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        super(rng);
        this.innerDistribution = new EnumeratedDistribution<>(rng, createDistribution(singletons, probabilities));
    }

    /* JADX INFO: Multiple debug info for r10v2 java.util.Iterator<java.util.Map$Entry<java.lang.Double, java.lang.Integer>>: [D('i$' java.util.Iterator), D('i$' int)] */
    public EnumeratedRealDistribution(RandomGenerator rng, double[] data) {
        super(rng);
        Map<Double, Integer> dataMap = new HashMap<>();
        for (double value : data) {
            Integer count = dataMap.get(Double.valueOf(value));
            if (count == null) {
                count = 0;
            }
            dataMap.put(Double.valueOf(value), Integer.valueOf(count.intValue() + 1));
        }
        int massPoints = dataMap.size();
        double denom = (double) data.length;
        double[] values = new double[massPoints];
        double[] probabilities = new double[massPoints];
        int index = 0;
        for (Map.Entry<Double, Integer> entry : dataMap.entrySet()) {
            values[index] = entry.getKey().doubleValue();
            probabilities[index] = ((double) entry.getValue().intValue()) / denom;
            index++;
        }
        this.innerDistribution = new EnumeratedDistribution<>(rng, createDistribution(values, probabilities));
    }

    public EnumeratedRealDistribution(double[] data) {
        this(new Well19937c(), data);
    }

    private static List<Pair<Double, Double>> createDistribution(double[] singletons, double[] probabilities) {
        if (singletons.length != probabilities.length) {
            throw new DimensionMismatchException(probabilities.length, singletons.length);
        }
        List<Pair<Double, Double>> samples = new ArrayList<>(singletons.length);
        for (int i = 0; i < singletons.length; i++) {
            samples.add(new Pair<>(Double.valueOf(singletons[i]), Double.valueOf(probabilities[i])));
        }
        return samples;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double probability(double x) {
        return this.innerDistribution.probability(Double.valueOf(x));
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        return probability(x);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        double probability = 0.0d;
        for (Pair<Double, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey().doubleValue() <= x) {
                probability += sample.getValue().doubleValue();
            }
        }
        return probability;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        }
        double probability = 0.0d;
        double x = getSupportLowerBound();
        for (Pair<Double, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getValue().doubleValue() != 0.0d) {
                probability += sample.getValue().doubleValue();
                x = sample.getKey().doubleValue();
                if (probability >= p) {
                    break;
                }
            }
        }
        return x;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        double mean = 0.0d;
        for (Pair<Double, Double> sample : this.innerDistribution.getPmf()) {
            mean += sample.getKey().doubleValue() * sample.getValue().doubleValue();
        }
        return mean;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        double mean = 0.0d;
        double meanOfSquares = 0.0d;
        for (Pair<Double, Double> sample : this.innerDistribution.getPmf()) {
            mean += sample.getKey().doubleValue() * sample.getValue().doubleValue();
            meanOfSquares += sample.getKey().doubleValue() * sample.getValue().doubleValue() * sample.getKey().doubleValue();
        }
        return meanOfSquares - (mean * mean);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        double min = Double.POSITIVE_INFINITY;
        for (Pair<Double, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey().doubleValue() < min && sample.getValue().doubleValue() > 0.0d) {
                min = sample.getKey().doubleValue();
            }
        }
        return min;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportUpperBound() {
        double max = Double.NEGATIVE_INFINITY;
        for (Pair<Double, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey().doubleValue() > max && sample.getValue().doubleValue() > 0.0d) {
                max = sample.getKey().doubleValue();
            }
        }
        return max;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportConnected() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double sample() {
        return this.innerDistribution.sample().doubleValue();
    }
}
