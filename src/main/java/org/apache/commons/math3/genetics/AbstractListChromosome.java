package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractListChromosome<T> extends Chromosome {
    private final List<T> representation;

    /* access modifiers changed from: protected */
    public abstract void checkValidity(List<T> list) throws InvalidRepresentationException;

    public abstract AbstractListChromosome<T> newFixedLengthChromosome(List<T> list);

    public AbstractListChromosome(List<T> representation2) throws InvalidRepresentationException {
        this(representation2, true);
    }

    public AbstractListChromosome(T[] representation2) throws InvalidRepresentationException {
        this(Arrays.asList(representation2));
    }

    public AbstractListChromosome(List<T> representation2, boolean copyList) {
        checkValidity(representation2);
        this.representation = Collections.unmodifiableList(copyList ? new ArrayList(representation2) : representation2);
    }

    /* access modifiers changed from: protected */
    public List<T> getRepresentation() {
        return this.representation;
    }

    public int getLength() {
        return getRepresentation().size();
    }

    public String toString() {
        return String.format("(f=%s %s)", Double.valueOf(getFitness()), getRepresentation());
    }
}
