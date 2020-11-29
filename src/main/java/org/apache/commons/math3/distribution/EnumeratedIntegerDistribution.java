package org.apache.commons.math3.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.Pair;

public class EnumeratedIntegerDistribution extends AbstractIntegerDistribution {
    private static final long serialVersionUID = 20130308;
    protected final EnumeratedDistribution<Integer> innerDistribution;

    public EnumeratedIntegerDistribution(int[] singletons, double[] probabilities) throws DimensionMismatchException, NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        this(new Well19937c(), singletons, probabilities);
    }

    public EnumeratedIntegerDistribution(RandomGenerator rng, int[] singletons, double[] probabilities) throws DimensionMismatchException, NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        super(rng);
        this.innerDistribution = new EnumeratedDistribution<>(rng, createDistribution(singletons, probabilities));
    }

    /* JADX INFO: Multiple debug info for r8v2 java.util.Iterator<java.util.Map$Entry<java.lang.Integer, java.lang.Integer>>: [D('i$' java.util.Iterator), D('i$' int)] */
    public EnumeratedIntegerDistribution(RandomGenerator rng, int[] data) {
        super(rng);
        Map<Integer, Integer> dataMap = new HashMap<>();
        for (int value : data) {
            Integer count = dataMap.get(Integer.valueOf(value));
            if (count == null) {
                count = 0;
            }
            dataMap.put(Integer.valueOf(value), Integer.valueOf(count.intValue() + 1));
        }
        int massPoints = dataMap.size();
        double denom = (double) data.length;
        int[] values = new int[massPoints];
        double[] probabilities = new double[massPoints];
        int index = 0;
        for (Map.Entry<Integer, Integer> entry : dataMap.entrySet()) {
            values[index] = entry.getKey().intValue();
            probabilities[index] = ((double) entry.getValue().intValue()) / denom;
            index++;
        }
        this.innerDistribution = new EnumeratedDistribution<>(rng, createDistribution(values, probabilities));
    }

    public EnumeratedIntegerDistribution(int[] data) {
        this(new Well19937c(), data);
    }

    private static List<Pair<Integer, Double>> createDistribution(int[] singletons, double[] probabilities) {
        if (singletons.length != probabilities.length) {
            throw new DimensionMismatchException(probabilities.length, singletons.length);
        }
        List<Pair<Integer, Double>> samples = new ArrayList<>(singletons.length);
        for (int i = 0; i < singletons.length; i++) {
            samples.add(new Pair<>(Integer.valueOf(singletons[i]), Double.valueOf(probabilities[i])));
        }
        return samples;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double probability(int x) {
        return this.innerDistribution.probability(Integer.valueOf(x));
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double cumulativeProbability(int x) {
        double probability = 0.0d;
        for (Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey().intValue() <= x) {
                probability += sample.getValue().doubleValue();
            }
        }
        return probability;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalMean() {
        double mean = 0.0d;
        for (Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            mean += ((double) sample.getKey().intValue()) * sample.getValue().doubleValue();
        }
        return mean;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalVariance() {
        double mean = 0.0d;
        double meanOfSquares = 0.0d;
        for (Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            mean += ((double) sample.getKey().intValue()) * sample.getValue().doubleValue();
            meanOfSquares += ((double) sample.getKey().intValue()) * sample.getValue().doubleValue() * ((double) sample.getKey().intValue());
        }
        return meanOfSquares - (mean * mean);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportLowerBound() {
        int min = BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT;
        for (Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey().intValue() < min && sample.getValue().doubleValue() > 0.0d) {
                min = sample.getKey().intValue();
            }
        }
        return min;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportUpperBound() {
        int max = Integer.MIN_VALUE;
        for (Pair<Integer, Double> sample : this.innerDistribution.getPmf()) {
            if (sample.getKey().intValue() > max && sample.getValue().doubleValue() > 0.0d) {
                max = sample.getKey().intValue();
            }
        }
        return max;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public boolean isSupportConnected() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.AbstractIntegerDistribution, org.apache.commons.math3.distribution.IntegerDistribution
    public int sample() {
        return this.innerDistribution.sample().intValue();
    }
}
