package org.apache.commons.math3.genetics;

import java.util.Iterator;

public abstract class Chromosome implements Comparable<Chromosome>, Fitness {
    private static final double NO_FITNESS = Double.NEGATIVE_INFINITY;
    private double fitness = NO_FITNESS;

    public double getFitness() {
        if (this.fitness == NO_FITNESS) {
            this.fitness = fitness();
        }
        return this.fitness;
    }

    public int compareTo(Chromosome another) {
        return Double.compare(getFitness(), another.getFitness());
    }

    /* access modifiers changed from: protected */
    public boolean isSame(Chromosome another) {
        return false;
    }

    /* access modifiers changed from: protected */
    public Chromosome findSameChromosome(Population population) {
        Iterator i$ = population.iterator();
        while (i$.hasNext()) {
            Chromosome anotherChr = (Chromosome) i$.next();
            if (isSame(anotherChr)) {
                return anotherChr;
            }
        }
        return null;
    }

    public void searchForFitnessUpdate(Population population) {
        Chromosome sameChromosome = findSameChromosome(population);
        if (sameChromosome != null) {
            this.fitness = sameChromosome.getFitness();
        }
    }
}
