package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class RandomKeyMutation implements MutationPolicy {
    @Override // org.apache.commons.math3.genetics.MutationPolicy
    public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
        if (!(original instanceof RandomKey)) {
            throw new MathIllegalArgumentException(LocalizedFormats.RANDOMKEY_MUTATION_WRONG_CLASS, original.getClass().getSimpleName());
        }
        RandomKey<?> originalRk = (RandomKey) original;
        List<?> representation = originalRk.getRepresentation();
        int rInd = GeneticAlgorithm.getRandomGenerator().nextInt(representation.size());
        List<Double> newRepr = new ArrayList<>(representation);
        newRepr.set(rInd, Double.valueOf(GeneticAlgorithm.getRandomGenerator().nextDouble()));
        return originalRk.newFixedLengthChromosome(newRepr);
    }
}
