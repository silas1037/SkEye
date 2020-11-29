package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

public class OrderedCrossover<T> implements CrossoverPolicy {
    @Override // org.apache.commons.math3.genetics.CrossoverPolicy
    public ChromosomePair crossover(Chromosome first, Chromosome second) throws DimensionMismatchException, MathIllegalArgumentException {
        if ((first instanceof AbstractListChromosome) && (second instanceof AbstractListChromosome)) {
            return mate((AbstractListChromosome) first, (AbstractListChromosome) second);
        }
        throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME, new Object[0]);
    }

    /* access modifiers changed from: protected */
    public ChromosomePair mate(AbstractListChromosome<T> first, AbstractListChromosome<T> second) throws DimensionMismatchException {
        int b;
        int length = first.getLength();
        if (length != second.getLength()) {
            throw new DimensionMismatchException(second.getLength(), length);
        }
        List<T> parent1Rep = first.getRepresentation();
        List<T> parent2Rep = second.getRepresentation();
        ArrayList arrayList = new ArrayList(length);
        ArrayList arrayList2 = new ArrayList(length);
        Set<T> child1Set = new HashSet<>(length);
        Set<T> child2Set = new HashSet<>(length);
        RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
        int a = random.nextInt(length);
        do {
            b = random.nextInt(length);
        } while (a == b);
        int lb = FastMath.min(a, b);
        int ub = FastMath.max(a, b);
        arrayList.addAll(parent1Rep.subList(lb, ub + 1));
        child1Set.addAll(arrayList);
        arrayList2.addAll(parent2Rep.subList(lb, ub + 1));
        child2Set.addAll(arrayList2);
        for (int i = 1; i <= length; i++) {
            int idx = (ub + i) % length;
            T item1 = parent1Rep.get(idx);
            T item2 = parent2Rep.get(idx);
            if (!child1Set.contains(item2)) {
                arrayList.add(item2);
                child1Set.add(item2);
            }
            if (!child2Set.contains(item1)) {
                arrayList2.add(item1);
                child2Set.add(item1);
            }
        }
        Collections.rotate(arrayList, lb);
        Collections.rotate(arrayList2, lb);
        return new ChromosomePair(first.newFixedLengthChromosome(arrayList), second.newFixedLengthChromosome(arrayList2));
    }
}
