package org.apache.commons.math3.util;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;

public class ResizableDoubleArray implements DoubleArray, Serializable {
    @Deprecated
    public static final int ADDITIVE_MODE = 1;
    private static final double DEFAULT_CONTRACTION_DELTA = 0.5d;
    private static final double DEFAULT_EXPANSION_FACTOR = 2.0d;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    @Deprecated
    public static final int MULTIPLICATIVE_MODE = 0;
    private static final long serialVersionUID = -3485529955529426875L;
    private double contractionCriterion;
    private double expansionFactor;
    private ExpansionMode expansionMode;
    private double[] internalArray;
    private int numElements;
    private int startIndex;

    public enum ExpansionMode {
        MULTIPLICATIVE,
        ADDITIVE
    }

    public ResizableDoubleArray() {
        this(16);
    }

    public ResizableDoubleArray(int initialCapacity) throws MathIllegalArgumentException {
        this(initialCapacity, (double) DEFAULT_EXPANSION_FACTOR);
    }

    public ResizableDoubleArray(double[] initialArray) {
        this(16, DEFAULT_EXPANSION_FACTOR, 2.5d, ExpansionMode.MULTIPLICATIVE, initialArray);
    }

    @Deprecated
    public ResizableDoubleArray(int initialCapacity, float expansionFactor2) throws MathIllegalArgumentException {
        this(initialCapacity, (double) expansionFactor2);
    }

    public ResizableDoubleArray(int initialCapacity, double expansionFactor2) throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor2, DEFAULT_CONTRACTION_DELTA + expansionFactor2);
    }

    @Deprecated
    public ResizableDoubleArray(int initialCapacity, float expansionFactor2, float contractionCriteria) throws MathIllegalArgumentException {
        this(initialCapacity, (double) expansionFactor2, (double) contractionCriteria);
    }

    public ResizableDoubleArray(int initialCapacity, double expansionFactor2, double contractionCriterion2) throws MathIllegalArgumentException {
        this(initialCapacity, expansionFactor2, contractionCriterion2, ExpansionMode.MULTIPLICATIVE, null);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    @Deprecated
    public ResizableDoubleArray(int initialCapacity, float expansionFactor2, float contractionCriteria, int expansionMode2) throws MathIllegalArgumentException {
        this(initialCapacity, (double) expansionFactor2, (double) contractionCriteria, expansionMode2 == 1 ? ExpansionMode.ADDITIVE : ExpansionMode.MULTIPLICATIVE, null);
        setExpansionMode(expansionMode2);
    }

    public ResizableDoubleArray(int initialCapacity, double expansionFactor2, double contractionCriterion2, ExpansionMode expansionMode2, double... data) throws MathIllegalArgumentException {
        this.contractionCriterion = 2.5d;
        this.expansionFactor = DEFAULT_EXPANSION_FACTOR;
        this.expansionMode = ExpansionMode.MULTIPLICATIVE;
        this.numElements = 0;
        this.startIndex = 0;
        if (initialCapacity <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.INITIAL_CAPACITY_NOT_POSITIVE, Integer.valueOf(initialCapacity));
        }
        checkContractExpand(contractionCriterion2, expansionFactor2);
        this.expansionFactor = expansionFactor2;
        this.contractionCriterion = contractionCriterion2;
        this.expansionMode = expansionMode2;
        this.internalArray = new double[initialCapacity];
        this.numElements = 0;
        this.startIndex = 0;
        if (data != null && data.length > 0) {
            addElements(data);
        }
    }

    public ResizableDoubleArray(ResizableDoubleArray original) throws NullArgumentException {
        this.contractionCriterion = 2.5d;
        this.expansionFactor = DEFAULT_EXPANSION_FACTOR;
        this.expansionMode = ExpansionMode.MULTIPLICATIVE;
        this.numElements = 0;
        this.startIndex = 0;
        MathUtils.checkNotNull(original);
        copy(original, this);
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized void addElement(double value) {
        if (this.internalArray.length <= this.startIndex + this.numElements) {
            expand();
        }
        double[] dArr = this.internalArray;
        int i = this.startIndex;
        int i2 = this.numElements;
        this.numElements = i2 + 1;
        dArr[i + i2] = value;
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized void addElements(double[] values) {
        double[] tempArray = new double[(this.numElements + values.length + 1)];
        System.arraycopy(this.internalArray, this.startIndex, tempArray, 0, this.numElements);
        System.arraycopy(values, 0, tempArray, this.numElements, values.length);
        this.internalArray = tempArray;
        this.startIndex = 0;
        this.numElements += values.length;
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized double addElementRolling(double value) {
        double discarded;
        discarded = this.internalArray[this.startIndex];
        if (this.startIndex + this.numElements + 1 > this.internalArray.length) {
            expand();
        }
        this.startIndex++;
        this.internalArray[this.startIndex + (this.numElements - 1)] = value;
        if (shouldContract()) {
            contract();
        }
        return discarded;
    }

    public synchronized double substituteMostRecentElement(double value) throws MathIllegalStateException {
        double discarded;
        if (this.numElements < 1) {
            throw new MathIllegalStateException(LocalizedFormats.CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY, new Object[0]);
        }
        int substIndex = this.startIndex + (this.numElements - 1);
        discarded = this.internalArray[substIndex];
        this.internalArray[substIndex] = value;
        return discarded;
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void checkContractExpand(float contraction, float expansion) throws MathIllegalArgumentException {
        checkContractExpand((double) contraction, (double) expansion);
    }

    /* access modifiers changed from: protected */
    public void checkContractExpand(double contraction, double expansion) throws NumberIsTooSmallException {
        if (contraction < expansion) {
            NumberIsTooSmallException e = new NumberIsTooSmallException(Double.valueOf(contraction), 1, true);
            e.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR, Double.valueOf(contraction), Double.valueOf(expansion));
            throw e;
        } else if (contraction <= 1.0d) {
            NumberIsTooSmallException e2 = new NumberIsTooSmallException(Double.valueOf(contraction), 1, false);
            e2.getContext().addMessage(LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_ONE, Double.valueOf(contraction));
            throw e2;
        } else if (expansion <= 1.0d) {
            NumberIsTooSmallException e3 = new NumberIsTooSmallException(Double.valueOf(contraction), 1, false);
            e3.getContext().addMessage(LocalizedFormats.EXPANSION_FACTOR_SMALLER_THAN_ONE, Double.valueOf(expansion));
            throw e3;
        }
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized void clear() {
        this.numElements = 0;
        this.startIndex = 0;
    }

    public synchronized void contract() {
        double[] tempArray = new double[(this.numElements + 1)];
        System.arraycopy(this.internalArray, this.startIndex, tempArray, 0, this.numElements);
        this.internalArray = tempArray;
        this.startIndex = 0;
    }

    public synchronized void discardFrontElements(int i) throws MathIllegalArgumentException {
        discardExtremeElements(i, true);
    }

    public synchronized void discardMostRecentElements(int i) throws MathIllegalArgumentException {
        discardExtremeElements(i, false);
    }

    private synchronized void discardExtremeElements(int i, boolean front) throws MathIllegalArgumentException {
        if (i > this.numElements) {
            throw new MathIllegalArgumentException(LocalizedFormats.TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY, Integer.valueOf(i), Integer.valueOf(this.numElements));
        } else if (i < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS, Integer.valueOf(i));
        } else {
            this.numElements -= i;
            if (front) {
                this.startIndex += i;
            }
            if (shouldContract()) {
                contract();
            }
        }
    }

    /* access modifiers changed from: protected */
    public synchronized void expand() {
        int newSize;
        if (this.expansionMode == ExpansionMode.MULTIPLICATIVE) {
            newSize = (int) FastMath.ceil(((double) this.internalArray.length) * this.expansionFactor);
        } else {
            newSize = (int) (((long) this.internalArray.length) + FastMath.round(this.expansionFactor));
        }
        double[] tempArray = new double[newSize];
        System.arraycopy(this.internalArray, 0, tempArray, 0, this.internalArray.length);
        this.internalArray = tempArray;
    }

    private synchronized void expandTo(int size) {
        double[] tempArray = new double[size];
        System.arraycopy(this.internalArray, 0, tempArray, 0, this.internalArray.length);
        this.internalArray = tempArray;
    }

    @Deprecated
    public float getContractionCriteria() {
        return (float) getContractionCriterion();
    }

    public double getContractionCriterion() {
        return this.contractionCriterion;
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized double getElement(int index) {
        if (index >= this.numElements) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else if (index >= 0) {
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return this.internalArray[this.startIndex + index];
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized double[] getElements() {
        double[] elementArray;
        elementArray = new double[this.numElements];
        System.arraycopy(this.internalArray, this.startIndex, elementArray, 0, this.numElements);
        return elementArray;
    }

    @Deprecated
    public float getExpansionFactor() {
        return (float) this.expansionFactor;
    }

    @Deprecated
    public int getExpansionMode() {
        int i;
        synchronized (this) {
            switch (this.expansionMode) {
                case MULTIPLICATIVE:
                    i = 0;
                    break;
                case ADDITIVE:
                    i = 1;
                    break;
                default:
                    throw new MathInternalError();
            }
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    @Deprecated
    public synchronized int getInternalLength() {
        return this.internalArray.length;
    }

    public int getCapacity() {
        return this.internalArray.length;
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized int getNumElements() {
        return this.numElements;
    }

    @Deprecated
    public synchronized double[] getInternalValues() {
        return this.internalArray;
    }

    /* access modifiers changed from: protected */
    public double[] getArrayRef() {
        return this.internalArray;
    }

    /* access modifiers changed from: protected */
    public int getStartIndex() {
        return this.startIndex;
    }

    @Deprecated
    public void setContractionCriteria(float contractionCriteria) throws MathIllegalArgumentException {
        checkContractExpand(contractionCriteria, getExpansionFactor());
        synchronized (this) {
            this.contractionCriterion = (double) contractionCriteria;
        }
    }

    public double compute(MathArrays.Function f) {
        double[] array;
        int start;
        int num;
        synchronized (this) {
            array = this.internalArray;
            start = this.startIndex;
            num = this.numElements;
        }
        return f.evaluate(array, start, num);
    }

    @Override // org.apache.commons.math3.util.DoubleArray
    public synchronized void setElement(int index, double value) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index + 1 > this.numElements) {
            this.numElements = index + 1;
        }
        if (this.startIndex + index >= this.internalArray.length) {
            expandTo(this.startIndex + index + 1);
        }
        this.internalArray[this.startIndex + index] = value;
    }

    @Deprecated
    public void setExpansionFactor(float expansionFactor2) throws MathIllegalArgumentException {
        checkContractExpand(getContractionCriterion(), (double) expansionFactor2);
        synchronized (this) {
            this.expansionFactor = (double) expansionFactor2;
        }
    }

    @Deprecated
    public void setExpansionMode(int expansionMode2) throws MathIllegalArgumentException {
        if (expansionMode2 == 0 || expansionMode2 == 1) {
            synchronized (this) {
                if (expansionMode2 == 0) {
                    setExpansionMode(ExpansionMode.MULTIPLICATIVE);
                } else if (expansionMode2 == 1) {
                    setExpansionMode(ExpansionMode.ADDITIVE);
                }
            }
            return;
        }
        throw new MathIllegalArgumentException(LocalizedFormats.UNSUPPORTED_EXPANSION_MODE, Integer.valueOf(expansionMode2), 0, "MULTIPLICATIVE_MODE", 1, "ADDITIVE_MODE");
    }

    @Deprecated
    public void setExpansionMode(ExpansionMode expansionMode2) {
        synchronized (this) {
            this.expansionMode = expansionMode2;
        }
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public void setInitialCapacity(int initialCapacity) throws MathIllegalArgumentException {
    }

    public synchronized void setNumElements(int i) throws MathIllegalArgumentException {
        if (i < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.INDEX_NOT_POSITIVE, Integer.valueOf(i));
        }
        int newSize = this.startIndex + i;
        if (newSize > this.internalArray.length) {
            expandTo(newSize);
        }
        this.numElements = i;
    }

    private synchronized boolean shouldContract() {
        boolean z = true;
        synchronized (this) {
            if (this.expansionMode == ExpansionMode.MULTIPLICATIVE) {
                if (((double) (((float) this.internalArray.length) / ((float) this.numElements))) <= this.contractionCriterion) {
                    z = false;
                }
            } else if (((double) (this.internalArray.length - this.numElements)) <= this.contractionCriterion) {
                z = false;
            }
        }
        return z;
    }

    @Deprecated
    public synchronized int start() {
        return this.startIndex;
    }

    public static void copy(ResizableDoubleArray source, ResizableDoubleArray dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        synchronized (source) {
            synchronized (dest) {
                dest.contractionCriterion = source.contractionCriterion;
                dest.expansionFactor = source.expansionFactor;
                dest.expansionMode = source.expansionMode;
                dest.internalArray = new double[source.internalArray.length];
                System.arraycopy(source.internalArray, 0, dest.internalArray, 0, dest.internalArray.length);
                dest.numElements = source.numElements;
                dest.startIndex = source.startIndex;
            }
        }
    }

    public synchronized ResizableDoubleArray copy() {
        ResizableDoubleArray result;
        result = new ResizableDoubleArray();
        copy(this, result);
        return result;
    }

    public boolean equals(Object object) {
        boolean result;
        boolean result2;
        boolean result3;
        boolean result4;
        if (object == this) {
            return true;
        }
        if (!(object instanceof ResizableDoubleArray)) {
            return false;
        }
        synchronized (this) {
            synchronized (object) {
                ResizableDoubleArray other = (ResizableDoubleArray) object;
                if (!(1 != 0 && other.contractionCriterion == this.contractionCriterion) || other.expansionFactor != this.expansionFactor) {
                    result = false;
                } else {
                    result = true;
                }
                if (!result || other.expansionMode != this.expansionMode) {
                    result2 = false;
                } else {
                    result2 = true;
                }
                if (!result2 || other.numElements != this.numElements) {
                    result3 = false;
                } else {
                    result3 = true;
                }
                if (!result3 || other.startIndex != this.startIndex) {
                    result4 = false;
                } else {
                    result4 = true;
                }
                if (!result4) {
                    return false;
                }
                return Arrays.equals(this.internalArray, other.internalArray);
            }
        }
    }

    public synchronized int hashCode() {
        return Arrays.hashCode(new int[]{Double.valueOf(this.expansionFactor).hashCode(), Double.valueOf(this.contractionCriterion).hashCode(), this.expansionMode.hashCode(), Arrays.hashCode(this.internalArray), this.numElements, this.startIndex});
    }
}
