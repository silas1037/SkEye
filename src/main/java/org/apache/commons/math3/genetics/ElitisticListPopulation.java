package org.apache.commons.math3.genetics;

import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

public class ElitisticListPopulation extends ListPopulation {
    private double elitismRate = 0.9d;

    public ElitisticListPopulation(List<Chromosome> chromosomes, int populationLimit, double elitismRate2) throws NullArgumentException, NotPositiveException, NumberIsTooLargeException, OutOfRangeException {
        super(chromosomes, populationLimit);
        setElitismRate(elitismRate2);
    }

    public ElitisticListPopulation(int populationLimit, double elitismRate2) throws NotPositiveException, OutOfRangeException {
        super(populationLimit);
        setElitismRate(elitismRate2);
    }

    @Override // org.apache.commons.math3.genetics.Population
    public Population nextGeneration() {
        ElitisticListPopulation nextGeneration = new ElitisticListPopulation(getPopulationLimit(), getElitismRate());
        List<Chromosome> oldChromosomes = getChromosomeList();
        Collections.sort(oldChromosomes);
        for (int i = (int) FastMath.ceil((1.0d - getElitismRate()) * ((double) oldChromosomes.size())); i < oldChromosomes.size(); i++) {
            nextGeneration.addChromosome(oldChromosomes.get(i));
        }
        return nextGeneration;
    }

    public void setElitismRate(double elitismRate2) throws OutOfRangeException {
        if (elitismRate2 < 0.0d || elitismRate2 > 1.0d) {
            throw new OutOfRangeException(LocalizedFormats.ELITISM_RATE, Double.valueOf(elitismRate2), 0, 1);
        }
        this.elitismRate = elitismRate2;
    }

    public double getElitismRate() {
        return this.elitismRate;
    }
}
