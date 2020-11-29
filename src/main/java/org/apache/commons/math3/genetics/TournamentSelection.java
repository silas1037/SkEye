package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class TournamentSelection implements SelectionPolicy {
    private int arity;

    public TournamentSelection(int arity2) {
        this.arity = arity2;
    }

    @Override // org.apache.commons.math3.genetics.SelectionPolicy
    public ChromosomePair select(Population population) throws MathIllegalArgumentException {
        return new ChromosomePair(tournament((ListPopulation) population), tournament((ListPopulation) population));
    }

    private Chromosome tournament(ListPopulation population) throws MathIllegalArgumentException {
        if (population.getPopulationSize() < this.arity) {
            throw new MathIllegalArgumentException(LocalizedFormats.TOO_LARGE_TOURNAMENT_ARITY, Integer.valueOf(this.arity), Integer.valueOf(population.getPopulationSize()));
        }
        ListPopulation tournamentPopulation = new ListPopulation(this.arity) {
            /* class org.apache.commons.math3.genetics.TournamentSelection.C02391 */

            @Override // org.apache.commons.math3.genetics.Population
            public Population nextGeneration() {
                return null;
            }
        };
        List<Chromosome> chromosomes = new ArrayList<>(population.getChromosomes());
        for (int i = 0; i < this.arity; i++) {
            int rind = GeneticAlgorithm.getRandomGenerator().nextInt(chromosomes.size());
            tournamentPopulation.addChromosome(chromosomes.get(rind));
            chromosomes.remove(rind);
        }
        return tournamentPopulation.getFittestChromosome();
    }

    public int getArity() {
        return this.arity;
    }

    public void setArity(int arity2) {
        this.arity = arity2;
    }
}
