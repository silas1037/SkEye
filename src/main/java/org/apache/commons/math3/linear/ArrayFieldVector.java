package org.apache.commons.math3.linear;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class ArrayFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable {
    private static final long serialVersionUID = 7648186910365927050L;
    private T[] data;
    private final Field<T> field;

    public ArrayFieldVector(Field<T> field2) {
        this(field2, 0);
    }

    public ArrayFieldVector(Field<T> field2, int size) {
        this.field = field2;
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(field2, size));
    }

    public ArrayFieldVector(int size, T preset) {
        this(preset.getField(), size);
        Arrays.fill(this.data, preset);
    }

    public ArrayFieldVector(T[] d) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(d);
        try {
            this.field = d[0].getField();
            this.data = (T[]) ((FieldElement[]) d.clone());
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
    }

    public ArrayFieldVector(Field<T> field2, T[] d) throws NullArgumentException {
        MathUtils.checkNotNull(d);
        this.field = field2;
        this.data = (T[]) ((FieldElement[]) d.clone());
    }

    public ArrayFieldVector(T[] d, boolean copyArray) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(d);
        if (d.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        this.field = d[0].getField();
        this.data = copyArray ? (T[]) ((FieldElement[]) d.clone()) : d;
    }

    public ArrayFieldVector(Field<T> field2, T[] d, boolean copyArray) throws NullArgumentException {
        MathUtils.checkNotNull(d);
        this.field = field2;
        this.data = copyArray ? (T[]) ((FieldElement[]) d.clone()) : d;
    }

    public ArrayFieldVector(T[] d, int pos, int size) throws NullArgumentException, NumberIsTooLargeException {
        MathUtils.checkNotNull(d);
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(Integer.valueOf(pos + size), Integer.valueOf(d.length), true);
        }
        this.field = d[0].getField();
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, size));
        System.arraycopy(d, pos, this.data, 0, size);
    }

    public ArrayFieldVector(Field<T> field2, T[] d, int pos, int size) throws NullArgumentException, NumberIsTooLargeException {
        MathUtils.checkNotNull(d);
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(Integer.valueOf(pos + size), Integer.valueOf(d.length), true);
        }
        this.field = field2;
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(field2, size));
        System.arraycopy(d, pos, this.data, 0, size);
    }

    public ArrayFieldVector(FieldVector<T> v) throws NullArgumentException {
        MathUtils.checkNotNull(v);
        this.field = v.getField();
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, v.getDimension()));
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = v.getEntry(i);
        }
    }

    public ArrayFieldVector(ArrayFieldVector<T> v) throws NullArgumentException {
        MathUtils.checkNotNull(v);
        this.field = v.getField();
        this.data = (T[]) ((FieldElement[]) v.data.clone());
    }

    public ArrayFieldVector(ArrayFieldVector<T> v, boolean deep) throws NullArgumentException {
        MathUtils.checkNotNull(v);
        this.field = v.getField();
        this.data = deep ? (T[]) ((FieldElement[]) v.data.clone()) : v.data;
    }

    @Deprecated
    public ArrayFieldVector(ArrayFieldVector<T> v1, ArrayFieldVector<T> v2) throws NullArgumentException {
        this((FieldVector) v1, (FieldVector) v2);
    }

    public ArrayFieldVector(FieldVector<T> v1, FieldVector<T> v2) throws NullArgumentException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        this.field = v1.getField();
        T[] v1Data = v1 instanceof ArrayFieldVector ? ((ArrayFieldVector) v1).data : v1.toArray();
        T[] v2Data = v2 instanceof ArrayFieldVector ? ((ArrayFieldVector) v2).data : v2.toArray();
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, v1Data.length + v2Data.length));
        System.arraycopy(v1Data, 0, this.data, 0, v1Data.length);
        System.arraycopy(v2Data, 0, this.data, v1Data.length, v2Data.length);
    }

    @Deprecated
    public ArrayFieldVector(ArrayFieldVector<T> v1, T[] v2) throws NullArgumentException {
        this((FieldVector) v1, (FieldElement[]) v2);
    }

    public ArrayFieldVector(FieldVector<T> v1, T[] v2) throws NullArgumentException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        this.field = v1.getField();
        T[] v1Data = v1 instanceof ArrayFieldVector ? ((ArrayFieldVector) v1).data : v1.toArray();
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, v1Data.length + v2.length));
        System.arraycopy(v1Data, 0, this.data, 0, v1Data.length);
        System.arraycopy(v2, 0, this.data, v1Data.length, v2.length);
    }

    @Deprecated
    public ArrayFieldVector(T[] v1, ArrayFieldVector<T> v2) throws NullArgumentException {
        this((FieldElement[]) v1, (FieldVector) v2);
    }

    public ArrayFieldVector(T[] v1, FieldVector<T> v2) throws NullArgumentException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        this.field = v2.getField();
        T[] v2Data = v2 instanceof ArrayFieldVector ? ((ArrayFieldVector) v2).data : v2.toArray();
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, v1.length + v2Data.length));
        System.arraycopy(v1, 0, this.data, 0, v1.length);
        System.arraycopy(v2Data, 0, this.data, v1.length, v2Data.length);
    }

    public ArrayFieldVector(T[] v1, T[] v2) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(v1[0].getField(), v1.length + v2.length));
        System.arraycopy(v1, 0, this.data, 0, v1.length);
        System.arraycopy(v2, 0, this.data, v1.length, v2.length);
        this.field = this.data[0].getField();
    }

    public ArrayFieldVector(Field<T> field2, T[] v1, T[] v2) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        this.data = (T[]) ((FieldElement[]) MathArrays.buildArray(field2, v1.length + v2.length));
        System.arraycopy(v1, 0, this.data, 0, v1.length);
        System.arraycopy(v2, 0, this.data, v1.length, v2.length);
        this.field = field2;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public Field<T> getField() {
        return this.field;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> copy() {
        return new ArrayFieldVector((ArrayFieldVector) this, true);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> add(FieldVector<T> v) throws DimensionMismatchException {
        try {
            return add((ArrayFieldVector) ((ArrayFieldVector) v));
        } catch (ClassCastException e) {
            checkVectorDimensions(v);
            FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; i++) {
                fieldElementArr[i] = (FieldElement) this.data[i].add(v.getEntry(i));
            }
            return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
        }
    }

    public ArrayFieldVector<T> add(ArrayFieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.data.length);
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            fieldElementArr[i] = (FieldElement) this.data[i].add(v.data[i]);
        }
        return new ArrayFieldVector<>((Field) this.field, fieldElementArr, false);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> subtract(FieldVector<T> v) throws DimensionMismatchException {
        try {
            return subtract((ArrayFieldVector) ((ArrayFieldVector) v));
        } catch (ClassCastException e) {
            checkVectorDimensions(v);
            FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; i++) {
                fieldElementArr[i] = (FieldElement) this.data[i].subtract(v.getEntry(i));
            }
            return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
        }
    }

    public ArrayFieldVector<T> subtract(ArrayFieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.data.length);
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            fieldElementArr[i] = (FieldElement) this.data[i].subtract(v.data[i]);
        }
        return new ArrayFieldVector<>((Field) this.field, fieldElementArr, false);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapAdd(T d) throws NullArgumentException {
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            fieldElementArr[i] = (FieldElement) this.data[i].add(d);
        }
        return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapAddToSelf(T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (FieldElement) this.data[i].add(d);
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapSubtract(T d) throws NullArgumentException {
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            fieldElementArr[i] = (FieldElement) this.data[i].subtract(d);
        }
        return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapSubtractToSelf(T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (FieldElement) this.data[i].subtract(d);
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapMultiply(T d) throws NullArgumentException {
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            fieldElementArr[i] = (FieldElement) this.data[i].multiply(d);
        }
        return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapMultiplyToSelf(T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (FieldElement) this.data[i].multiply(d);
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapDivide(T d) throws NullArgumentException, MathArithmeticException {
        MathUtils.checkNotNull(d);
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            fieldElementArr[i] = (FieldElement) this.data[i].divide(d);
        }
        return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapDivideToSelf(T d) throws NullArgumentException, MathArithmeticException {
        MathUtils.checkNotNull(d);
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = (FieldElement) this.data[i].divide(d);
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapInv() throws MathArithmeticException {
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        T one = this.field.getOne();
        for (int i = 0; i < this.data.length; i++) {
            try {
                fieldElementArr[i] = (FieldElement) one.divide(this.data[i]);
            } catch (MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, Integer.valueOf(i));
            }
        }
        return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapInvToSelf() throws MathArithmeticException {
        T one = this.field.getOne();
        for (int i = 0; i < this.data.length; i++) {
            try {
                this.data[i] = (FieldElement) one.divide(this.data[i]);
            } catch (MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, Integer.valueOf(i));
            }
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> ebeMultiply(FieldVector<T> v) throws DimensionMismatchException {
        try {
            return ebeMultiply((ArrayFieldVector) ((ArrayFieldVector) v));
        } catch (ClassCastException e) {
            checkVectorDimensions(v);
            FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; i++) {
                fieldElementArr[i] = (FieldElement) this.data[i].multiply(v.getEntry(i));
            }
            return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
        }
    }

    public ArrayFieldVector<T> ebeMultiply(ArrayFieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.data.length);
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            fieldElementArr[i] = (FieldElement) this.data[i].multiply(v.data[i]);
        }
        return new ArrayFieldVector<>((Field) this.field, fieldElementArr, false);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> ebeDivide(FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        try {
            return ebeDivide((ArrayFieldVector) ((ArrayFieldVector) v));
        } catch (ClassCastException e) {
            checkVectorDimensions(v);
            FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; i++) {
                try {
                    fieldElementArr[i] = (FieldElement) this.data[i].divide(v.getEntry(i));
                } catch (MathArithmeticException e2) {
                    throw new MathArithmeticException(LocalizedFormats.INDEX, Integer.valueOf(i));
                }
            }
            return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
        }
    }

    public ArrayFieldVector<T> ebeDivide(ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        checkVectorDimensions(v.data.length);
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            try {
                fieldElementArr[i] = (FieldElement) this.data[i].divide(v.data[i]);
            } catch (MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, Integer.valueOf(i));
            }
        }
        return new ArrayFieldVector<>((Field) this.field, fieldElementArr, false);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public T[] getData() {
        return (T[]) ((FieldElement[]) this.data.clone());
    }

    public T[] getDataRef() {
        return this.data;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public T dotProduct(FieldVector<T> v) throws DimensionMismatchException {
        try {
            return dotProduct((ArrayFieldVector) ((ArrayFieldVector) v));
        } catch (ClassCastException e) {
            checkVectorDimensions(v);
            T dot = this.field.getZero();
            for (int i = 0; i < this.data.length; i++) {
                dot = (T) ((FieldElement) dot.add(this.data[i].multiply(v.getEntry(i))));
            }
            return dot;
        }
    }

    public T dotProduct(ArrayFieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.data.length);
        T dot = this.field.getZero();
        for (int i = 0; i < this.data.length; i++) {
            dot = (T) ((FieldElement) dot.add(this.data[i].multiply(v.data[i])));
        }
        return dot;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.FieldVector<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> projection(FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        return v.mapMultiply((FieldElement) dotProduct(v).divide(v.dotProduct(v)));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.ArrayFieldVector<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public ArrayFieldVector<T> projection(ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        return (ArrayFieldVector) v.mapMultiply((FieldElement) dotProduct((ArrayFieldVector) v).divide(v.dotProduct((ArrayFieldVector) v)));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: org.apache.commons.math3.linear.Array2DRowFieldMatrix */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldMatrix<T> outerProduct(FieldVector<T> v) {
        try {
            return outerProduct((ArrayFieldVector) ((ArrayFieldVector) v));
        } catch (ClassCastException e) {
            int m = this.data.length;
            int n = v.getDimension();
            Array2DRowFieldMatrix array2DRowFieldMatrix = new Array2DRowFieldMatrix(this.field, m, n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    array2DRowFieldMatrix.setEntry(i, j, (FieldElement) this.data[i].multiply(v.getEntry(j)));
                }
            }
            return array2DRowFieldMatrix;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v0, resolved type: org.apache.commons.math3.linear.Array2DRowFieldMatrix */
    /* JADX WARN: Multi-variable type inference failed */
    public FieldMatrix<T> outerProduct(ArrayFieldVector<T> v) {
        int m = this.data.length;
        int n = v.data.length;
        Array2DRowFieldMatrix array2DRowFieldMatrix = new Array2DRowFieldMatrix(this.field, m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                array2DRowFieldMatrix.setEntry(i, j, (FieldElement) this.data[i].multiply(v.data[j]));
            }
        }
        return array2DRowFieldMatrix;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public T getEntry(int index) {
        return this.data[index];
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public int getDimension() {
        return this.data.length;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> append(FieldVector<T> v) {
        try {
            return append((ArrayFieldVector) ((ArrayFieldVector) v));
        } catch (ClassCastException e) {
            return new ArrayFieldVector((ArrayFieldVector) this, new ArrayFieldVector(v));
        }
    }

    public ArrayFieldVector<T> append(ArrayFieldVector<T> v) {
        return new ArrayFieldVector<>((ArrayFieldVector) this, (ArrayFieldVector) v);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> append(T in) {
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, this.data.length + 1);
        System.arraycopy(this.data, 0, fieldElementArr, 0, this.data.length);
        fieldElementArr[this.data.length] = in;
        return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> getSubVector(int index, int n) throws OutOfRangeException, NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, Integer.valueOf(n));
        }
        ArrayFieldVector<T> out = new ArrayFieldVector<>(this.field, n);
        try {
            System.arraycopy(this.data, index, out.data, 0, n);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex((index + n) - 1);
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public void setEntry(int index, T value) {
        try {
            this.data[index] = value;
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public void setSubVector(int index, FieldVector<T> v) throws OutOfRangeException {
        try {
            set(index, (ArrayFieldVector) v);
        } catch (ClassCastException e) {
            for (int i = index; i < v.getDimension() + index; i++) {
                try {
                    this.data[i] = v.getEntry(i - index);
                } catch (IndexOutOfBoundsException e2) {
                    checkIndex(index);
                    checkIndex((v.getDimension() + index) - 1);
                    return;
                }
            }
        }
    }

    public void set(int index, ArrayFieldVector<T> v) throws OutOfRangeException {
        try {
            System.arraycopy(v.data, 0, this.data, index, v.data.length);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex((v.data.length + index) - 1);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public void set(T value) {
        Arrays.fill(this.data, value);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public T[] toArray() {
        return (T[]) ((FieldElement[]) this.data.clone());
    }

    /* access modifiers changed from: protected */
    public void checkVectorDimensions(FieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
    }

    /* access modifiers changed from: protected */
    public void checkVectorDimensions(int n) throws DimensionMismatchException {
        if (this.data.length != n) {
            throw new DimensionMismatchException(this.data.length, n);
        }
    }

    public T walkInDefaultOrder(FieldVectorPreservingVisitor<T> visitor) {
        int dim = getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; i++) {
            visitor.visit(i, getEntry(i));
        }
        return visitor.end();
    }

    public T walkInDefaultOrder(FieldVectorPreservingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        checkIndices(start, end);
        visitor.start(getDimension(), start, end);
        for (int i = start; i <= end; i++) {
            visitor.visit(i, getEntry(i));
        }
        return visitor.end();
    }

    public T walkInOptimizedOrder(FieldVectorPreservingVisitor<T> visitor) {
        return walkInDefaultOrder(visitor);
    }

    public T walkInOptimizedOrder(FieldVectorPreservingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        return walkInDefaultOrder(visitor, start, end);
    }

    public T walkInDefaultOrder(FieldVectorChangingVisitor<T> visitor) {
        int dim = getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; i++) {
            setEntry(i, visitor.visit(i, getEntry(i)));
        }
        return visitor.end();
    }

    public T walkInDefaultOrder(FieldVectorChangingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        checkIndices(start, end);
        visitor.start(getDimension(), start, end);
        for (int i = start; i <= end; i++) {
            setEntry(i, visitor.visit(i, getEntry(i)));
        }
        return visitor.end();
    }

    public T walkInOptimizedOrder(FieldVectorChangingVisitor<T> visitor) {
        return walkInDefaultOrder(visitor);
    }

    public T walkInOptimizedOrder(FieldVectorChangingVisitor<T> visitor, int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        return walkInDefaultOrder(visitor, start, end);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        try {
            FieldVector<T> rhs = (FieldVector) other;
            if (this.data.length != rhs.getDimension()) {
                return false;
            }
            for (int i = 0; i < this.data.length; i++) {
                if (!this.data[i].equals(rhs.getEntry(i))) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        int h = 3542;
        for (T a : this.data) {
            h ^= a.hashCode();
        }
        return h;
    }

    private void checkIndex(int index) throws OutOfRangeException {
        if (index < 0 || index >= getDimension()) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, Integer.valueOf(index), 0, Integer.valueOf(getDimension() - 1));
        }
    }

    private void checkIndices(int start, int end) throws NumberIsTooSmallException, OutOfRangeException {
        int dim = getDimension();
        if (start < 0 || start >= dim) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, Integer.valueOf(start), 0, Integer.valueOf(dim - 1));
        } else if (end < 0 || end >= dim) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, Integer.valueOf(end), 0, Integer.valueOf(dim - 1));
        } else if (end < start) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, Integer.valueOf(end), Integer.valueOf(start), false);
        }
    }
}
