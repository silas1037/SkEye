package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public abstract class ListPopulation implements Population {
    private List<Chromosome> chromosomes;
    private int populationLimit;

    public ListPopulation(int populationLimit2) throws NotPositiveException {
        this(Collections.emptyList(), populationLimit2);
    }

    public ListPopulation(List<Chromosome> chromosomes2, int populationLimit2) throws NullArgumentException, NotPositiveException, NumberIsTooLargeException {
        if (chromosomes2 == null) {
            throw new NullArgumentException();
        } else if (populationLimit2 <= 0) {
            throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, Integer.valueOf(populationLimit2));
        } else if (chromosomes2.size() > populationLimit2) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, Integer.valueOf(chromosomes2.size()), Integer.valueOf(populationLimit2), false);
        } else {
            this.populationLimit = populationLimit2;
            this.chromosomes = new ArrayList(populationLimit2);
            this.chromosomes.addAll(chromosomes2);
        }
    }

    @Deprecated
    public void setChromosomes(List<Chromosome> chromosomes2) throws NullArgumentException, NumberIsTooLargeException {
        if (chromosomes2 == null) {
            throw new NullArgumentException();
        } else if (chromosomes2.size() > this.populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, Integer.valueOf(chromosomes2.size()), Integer.valueOf(this.populationLimit), false);
        } else {
            this.chromosomes.clear();
            this.chromosomes.addAll(chromosomes2);
        }
    }

    public void addChromosomes(Collection<Chromosome> chromosomeColl) throws NumberIsTooLargeException {
        if (this.chromosomes.size() + chromosomeColl.size() > this.populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, Integer.valueOf(this.chromosomes.size()), Integer.valueOf(this.populationLimit), false);
        }
        this.chromosomes.addAll(chromosomeColl);
    }

    public List<Chromosome> getChromosomes() {
        return Collections.unmodifiableList(this.chromosomes);
    }

    /* access modifiers changed from: protected */
    public List<Chromosome> getChromosomeList() {
        return this.chromosomes;
    }

    @Override // org.apache.commons.math3.genetics.Population
    public void addChromosome(Chromosome chromosome) throws NumberIsTooLargeException {
        if (this.chromosomes.size() >= this.populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, Integer.valueOf(this.chromosomes.size()), Integer.valueOf(this.populationLimit), false);
        }
        this.chromosomes.add(chromosome);
    }

    @Override // org.apache.commons.math3.genetics.Population
    public Chromosome getFittestChromosome() {
        Chromosome bestChromosome = this.chromosomes.get(0);
        for (Chromosome chromosome : this.chromosomes) {
            if (chromosome.compareTo(bestChromosome) > 0) {
                bestChromosome = chromosome;
            }
        }
        return bestChromosome;
    }

    @Override // org.apache.commons.math3.genetics.Population
    public int getPopulationLimit() {
        return this.populationLimit;
    }

    public void setPopulationLimit(int populationLimit2) throws NotPositiveException, NumberIsTooSmallException {
        if (populationLimit2 <= 0) {
            throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, Integer.valueOf(populationLimit2));
        } else if (populationLimit2 < this.chromosomes.size()) {
            throw new NumberIsTooSmallException(Integer.valueOf(populationLimit2), Integer.valueOf(this.chromosomes.size()), true);
        } else {
            this.populationLimit = populationLimit2;
        }
    }

    @Override // org.apache.commons.math3.genetics.Population
    public int getPopulationSize() {
        return this.chromosomes.size();
    }

    public String toString() {
        return this.chromosomes.toString();
    }

    @Override // java.lang.Iterable
    public Iterator<Chromosome> iterator() {
        return getChromosomes().iterator();
    }
}
