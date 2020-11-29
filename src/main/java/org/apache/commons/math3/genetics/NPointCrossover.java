package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;

public class NPointCrossover<T> implements CrossoverPolicy {
    private final int crossoverPoints;

    public NPointCrossover(int crossoverPoints2) throws NotStrictlyPositiveException {
        if (crossoverPoints2 <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(crossoverPoints2));
        }
        this.crossoverPoints = crossoverPoints2;
    }

    public int getCrossoverPoints() {
        return this.crossoverPoints;
    }

    @Override // org.apache.commons.math3.genetics.CrossoverPolicy
    public ChromosomePair crossover(Chromosome first, Chromosome second) throws DimensionMismatchException, MathIllegalArgumentException {
        if ((first instanceof AbstractListChromosome) && (second instanceof AbstractListChromosome)) {
            return mate((AbstractListChromosome) first, (AbstractListChromosome) second);
        }
        throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME, new Object[0]);
    }

    private ChromosomePair mate(AbstractListChromosome<T> first, AbstractListChromosome<T> second) throws DimensionMismatchException, NumberIsTooLargeException {
        int length = first.getLength();
        if (length != second.getLength()) {
            throw new DimensionMismatchException(second.getLength(), length);
        } else if (this.crossoverPoints >= length) {
            throw new NumberIsTooLargeException(Integer.valueOf(this.crossoverPoints), Integer.valueOf(length), false);
        } else {
            List<T> parent1Rep = first.getRepresentation();
            List<T> parent2Rep = second.getRepresentation();
            List<T> child1Rep = new ArrayList<>(length);
            List<T> child2Rep = new ArrayList<>(length);
            RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
            List<T> c1 = child1Rep;
            List<T> c2 = child2Rep;
            int remainingPoints = this.crossoverPoints;
            int lastIndex = 0;
            int i = 0;
            while (i < this.crossoverPoints) {
                int crossoverIndex = lastIndex + 1 + random.nextInt((length - lastIndex) - remainingPoints);
                for (int j = lastIndex; j < crossoverIndex; j++) {
                    c1.add(parent1Rep.get(j));
                    c2.add(parent2Rep.get(j));
                }
                c1 = c2;
                c2 = c1;
                lastIndex = crossoverIndex;
                i++;
                remainingPoints--;
            }
            for (int j2 = lastIndex; j2 < length; j2++) {
                c1.add(parent1Rep.get(j2));
                c2.add(parent2Rep.get(j2));
            }
            return new ChromosomePair(first.newFixedLengthChromosome(child1Rep), second.newFixedLengthChromosome(child2Rep));
        }
    }
}
