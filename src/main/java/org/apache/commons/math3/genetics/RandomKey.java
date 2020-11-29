package org.apache.commons.math3.genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public abstract class RandomKey<T> extends AbstractListChromosome<Double> implements PermutationChromosome<T> {
    private final List<Integer> baseSeqPermutation;
    private final List<Double> sortedRepresentation;

    public RandomKey(List<Double> representation) throws InvalidRepresentationException {
        super(representation);
        List<Double> sortedRepr = new ArrayList<>(getRepresentation());
        Collections.sort(sortedRepr);
        this.sortedRepresentation = Collections.unmodifiableList(sortedRepr);
        this.baseSeqPermutation = Collections.unmodifiableList(decodeGeneric(baseSequence(getLength()), getRepresentation(), this.sortedRepresentation));
    }

    public RandomKey(Double[] representation) throws InvalidRepresentationException {
        this(Arrays.asList(representation));
    }

    @Override // org.apache.commons.math3.genetics.PermutationChromosome
    public List<T> decode(List<T> sequence) {
        return decodeGeneric(sequence, getRepresentation(), this.sortedRepresentation);
    }

    private static <S> List<S> decodeGeneric(List<S> sequence, List<Double> representation, List<Double> sortedRepr) throws DimensionMismatchException {
        int l = sequence.size();
        if (representation.size() != l) {
            throw new DimensionMismatchException(representation.size(), l);
        } else if (sortedRepr.size() != l) {
            throw new DimensionMismatchException(sortedRepr.size(), l);
        } else {
            List<Double> reprCopy = new ArrayList<>(representation);
            List<S> res = new ArrayList<>(l);
            for (int i = 0; i < l; i++) {
                int index = reprCopy.indexOf(sortedRepr.get(i));
                res.add(sequence.get(index));
                reprCopy.set(index, null);
            }
            return res;
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.genetics.Chromosome
    public boolean isSame(Chromosome another) {
        if (!(another instanceof RandomKey)) {
            return false;
        }
        RandomKey<?> anotherRk = (RandomKey) another;
        if (getLength() != anotherRk.getLength()) {
            return false;
        }
        List<Integer> thisPerm = this.baseSeqPermutation;
        List<Integer> anotherPerm = anotherRk.baseSeqPermutation;
        for (int i = 0; i < getLength(); i++) {
            if (thisPerm.get(i) != anotherPerm.get(i)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:3:0x000c  */
    @Override // org.apache.commons.math3.genetics.AbstractListChromosome
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkValidity(java.util.List<java.lang.Double> r10) throws org.apache.commons.math3.genetics.InvalidRepresentationException {
        /*
            r9 = this;
            r8 = 1
            r7 = 0
            java.util.Iterator r0 = r10.iterator()
        L_0x0006:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0040
            java.lang.Object r1 = r0.next()
            java.lang.Double r1 = (java.lang.Double) r1
            double r2 = r1.doubleValue()
            r4 = 0
            int r1 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r1 < 0) goto L_0x0022
            r4 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r1 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x0006
        L_0x0022:
            org.apache.commons.math3.genetics.InvalidRepresentationException r1 = new org.apache.commons.math3.genetics.InvalidRepresentationException
            org.apache.commons.math3.exception.util.LocalizedFormats r4 = org.apache.commons.math3.exception.util.LocalizedFormats.OUT_OF_RANGE_SIMPLE
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.Double r6 = java.lang.Double.valueOf(r2)
            r5[r7] = r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)
            r5[r8] = r6
            r6 = 2
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)
            r5[r6] = r7
            r1.<init>(r4, r5)
            throw r1
        L_0x0040:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.genetics.RandomKey.checkValidity(java.util.List):void");
    }

    public static final List<Double> randomPermutation(int l) {
        List<Double> repr = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            repr.add(Double.valueOf(GeneticAlgorithm.getRandomGenerator().nextDouble()));
        }
        return repr;
    }

    public static final List<Double> identityPermutation(int l) {
        List<Double> repr = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            repr.add(Double.valueOf(((double) i) / ((double) l)));
        }
        return repr;
    }

    public static <S> List<Double> comparatorPermutation(List<S> data, Comparator<S> comparator) {
        List<S> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData, comparator);
        return inducedPermutation(data, sortedData);
    }

    public static <S> List<Double> inducedPermutation(List<S> originalData, List<S> permutedData) throws DimensionMismatchException, MathIllegalArgumentException {
        if (originalData.size() != permutedData.size()) {
            throw new DimensionMismatchException(permutedData.size(), originalData.size());
        }
        int l = originalData.size();
        List<S> origDataCopy = new ArrayList<>(originalData);
        Double[] res = new Double[l];
        for (int i = 0; i < l; i++) {
            int index = origDataCopy.indexOf(permutedData.get(i));
            if (index == -1) {
                throw new MathIllegalArgumentException(LocalizedFormats.DIFFERENT_ORIG_AND_PERMUTED_DATA, new Object[0]);
            }
            res[index] = Double.valueOf(((double) i) / ((double) l));
            origDataCopy.set(index, null);
        }
        return Arrays.asList(res);
    }

    @Override // org.apache.commons.math3.genetics.AbstractListChromosome
    public String toString() {
        return String.format("(f=%s pi=(%s))", Double.valueOf(getFitness()), this.baseSeqPermutation);
    }

    private static List<Integer> baseSequence(int l) {
        List<Integer> baseSequence = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            baseSequence.add(Integer.valueOf(i));
        }
        return baseSequence;
    }
}
