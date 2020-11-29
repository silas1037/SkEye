package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

public class GeneticAlgorithm {
    private static RandomGenerator randomGenerator = new JDKRandomGenerator();
    private final CrossoverPolicy crossoverPolicy;
    private final double crossoverRate;
    private int generationsEvolved = 0;
    private final MutationPolicy mutationPolicy;
    private final double mutationRate;
    private final SelectionPolicy selectionPolicy;

    public GeneticAlgorithm(CrossoverPolicy crossoverPolicy2, double crossoverRate2, MutationPolicy mutationPolicy2, double mutationRate2, SelectionPolicy selectionPolicy2) throws OutOfRangeException {
        if (crossoverRate2 < 0.0d || crossoverRate2 > 1.0d) {
            throw new OutOfRangeException(LocalizedFormats.CROSSOVER_RATE, Double.valueOf(crossoverRate2), 0, 1);
        } else if (mutationRate2 < 0.0d || mutationRate2 > 1.0d) {
            throw new OutOfRangeException(LocalizedFormats.MUTATION_RATE, Double.valueOf(mutationRate2), 0, 1);
        } else {
            this.crossoverPolicy = crossoverPolicy2;
            this.crossoverRate = crossoverRate2;
            this.mutationPolicy = mutationPolicy2;
            this.mutationRate = mutationRate2;
            this.selectionPolicy = selectionPolicy2;
        }
    }

    public static synchronized void setRandomGenerator(RandomGenerator random) {
        synchronized (GeneticAlgorithm.class) {
            randomGenerator = random;
        }
    }

    public static synchronized RandomGenerator getRandomGenerator() {
        RandomGenerator randomGenerator2;
        synchronized (GeneticAlgorithm.class) {
            randomGenerator2 = randomGenerator;
        }
        return randomGenerator2;
    }

    public Population evolve(Population initial, StoppingCondition condition) {
        Population current = initial;
        this.generationsEvolved = 0;
        while (!condition.isSatisfied(current)) {
            current = nextGeneration(current);
            this.generationsEvolved++;
        }
        return current;
    }

    public Population nextGeneration(Population current) {
        Population nextGeneration = current.nextGeneration();
        RandomGenerator randGen = getRandomGenerator();
        while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
            ChromosomePair pair = getSelectionPolicy().select(current);
            if (randGen.nextDouble() < getCrossoverRate()) {
                pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond());
            }
            if (randGen.nextDouble() < getMutationRate()) {
                pair = new ChromosomePair(getMutationPolicy().mutate(pair.getFirst()), getMutationPolicy().mutate(pair.getSecond()));
            }
            nextGeneration.addChromosome(pair.getFirst());
            if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
                nextGeneration.addChromosome(pair.getSecond());
            }
        }
        return nextGeneration;
    }

    public CrossoverPolicy getCrossoverPolicy() {
        return this.crossoverPolicy;
    }

    public double getCrossoverRate() {
        return this.crossoverRate;
    }

    public MutationPolicy getMutationPolicy() {
        return this.mutationPolicy;
    }

    public double getMutationRate() {
        return this.mutationRate;
    }

    public SelectionPolicy getSelectionPolicy() {
        return this.selectionPolicy;
    }

    public int getGenerationsEvolved() {
        return this.generationsEvolved;
    }
}
