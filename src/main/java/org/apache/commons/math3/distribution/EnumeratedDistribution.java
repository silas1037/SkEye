package org.apache.commons.math3.distribution;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Pair;

public class EnumeratedDistribution<T> implements Serializable {
    private static final long serialVersionUID = 20123308;
    private final double[] cumulativeProbabilities;
    private final double[] probabilities;
    protected final RandomGenerator random;
    private final List<T> singletons;

    public EnumeratedDistribution(List<Pair<T, Double>> pmf) throws NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        this(new Well19937c(), pmf);
    }

    public EnumeratedDistribution(RandomGenerator rng, List<Pair<T, Double>> pmf) throws NotPositiveException, MathArithmeticException, NotFiniteNumberException, NotANumberException {
        this.random = rng;
        this.singletons = new ArrayList(pmf.size());
        double[] probs = new double[pmf.size()];
        for (int i = 0; i < pmf.size(); i++) {
            Pair<T, Double> sample = pmf.get(i);
            this.singletons.add(sample.getKey());
            double p = sample.getValue().doubleValue();
            if (p < 0.0d) {
                throw new NotPositiveException(sample.getValue());
            } else if (Double.isInfinite(p)) {
                throw new NotFiniteNumberException(Double.valueOf(p), new Object[0]);
            } else if (Double.isNaN(p)) {
                throw new NotANumberException();
            } else {
                probs[i] = p;
            }
        }
        this.probabilities = MathArrays.normalizeArray(probs, 1.0d);
        this.cumulativeProbabilities = new double[this.probabilities.length];
        double sum = 0.0d;
        for (int i2 = 0; i2 < this.probabilities.length; i2++) {
            sum += this.probabilities[i2];
            this.cumulativeProbabilities[i2] = sum;
        }
    }

    public void reseedRandomGenerator(long seed) {
        this.random.setSeed(seed);
    }

    /* access modifiers changed from: package-private */
    public double probability(T x) {
        double probability = 0.0d;
        for (int i = 0; i < this.probabilities.length; i++) {
            if ((x == null && this.singletons.get(i) == null) || (x != null && x.equals(this.singletons.get(i)))) {
                probability += this.probabilities[i];
            }
        }
        return probability;
    }

    public List<Pair<T, Double>> getPmf() {
        List<Pair<T, Double>> samples = new ArrayList<>(this.probabilities.length);
        for (int i = 0; i < this.probabilities.length; i++) {
            samples.add(new Pair<>(this.singletons.get(i), Double.valueOf(this.probabilities[i])));
        }
        return samples;
    }

    public T sample() {
        double randomValue = this.random.nextDouble();
        int index = Arrays.binarySearch(this.cumulativeProbabilities, randomValue);
        if (index < 0) {
            index = (-index) - 1;
        }
        if (index < 0 || index >= this.probabilities.length || randomValue >= this.cumulativeProbabilities[index]) {
            return this.singletons.get(this.singletons.size() - 1);
        }
        return this.singletons.get(index);
    }

    public Object[] sample(int sampleSize) throws NotStrictlyPositiveException {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, Integer.valueOf(sampleSize));
        }
        Object[] out = new Object[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v1, types: [java.lang.Object[]] */
    public T[] sample(int sampleSize, T[] array) throws NotStrictlyPositiveException {
        T[] out;
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, Integer.valueOf(sampleSize));
        } else if (array == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        } else {
            if (array.length < sampleSize) {
                out = (Object[]) Array.newInstance(array.getClass().getComponentType(), sampleSize);
            } else {
                out = array;
            }
            for (int i = 0; i < sampleSize; i++) {
                out[i] = sample();
            }
            return out;
        }
    }
}
