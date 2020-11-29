package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class BinaryMutation implements MutationPolicy {
    @Override // org.apache.commons.math3.genetics.MutationPolicy
    public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
        int i;
        if (!(original instanceof BinaryChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_BINARY_CHROMOSOME, new Object[0]);
        }
        BinaryChromosome origChrom = (BinaryChromosome) original;
        ArrayList arrayList = new ArrayList(origChrom.getRepresentation());
        int geneIndex = GeneticAlgorithm.getRandomGenerator().nextInt(origChrom.getLength());
        if (((Integer) origChrom.getRepresentation().get(geneIndex)).intValue() == 0) {
            i = 1;
        } else {
            i = 0;
        }
        arrayList.set(geneIndex, Integer.valueOf(i));
        return origChrom.newFixedLengthChromosome(arrayList);
    }
}
