package org.apache.commons.math3.linear;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.OpenIntToFieldHashMap;

public class SparseFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable {
    private static final long serialVersionUID = 7841233292190413362L;
    private final OpenIntToFieldHashMap<T> entries;
    private final Field<T> field;
    private final int virtualSize;

    public SparseFieldVector(Field<T> field2) {
        this(field2, 0);
    }

    public SparseFieldVector(Field<T> field2, int dimension) {
        this.field = field2;
        this.virtualSize = dimension;
        this.entries = new OpenIntToFieldHashMap<>(field2);
    }

    protected SparseFieldVector(SparseFieldVector<T> v, int resize) {
        this.field = v.field;
        this.virtualSize = v.getDimension() + resize;
        this.entries = new OpenIntToFieldHashMap<>(v.entries);
    }

    public SparseFieldVector(Field<T> field2, int dimension, int expectedSize) {
        this.field = field2;
        this.virtualSize = dimension;
        this.entries = new OpenIntToFieldHashMap<>(field2, expectedSize);
    }

    public SparseFieldVector(Field<T> field2, T[] values) throws NullArgumentException {
        MathUtils.checkNotNull(values);
        this.field = field2;
        this.virtualSize = values.length;
        this.entries = new OpenIntToFieldHashMap<>(field2);
        for (int key = 0; key < values.length; key++) {
            this.entries.put(key, values[key]);
        }
    }

    public SparseFieldVector(SparseFieldVector<T> v) {
        this.field = v.field;
        this.virtualSize = v.getDimension();
        this.entries = new OpenIntToFieldHashMap<>(v.getEntries());
    }

    private OpenIntToFieldHashMap<T> getEntries() {
        return this.entries;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v1, resolved type: org.apache.commons.math3.linear.SparseFieldVector */
    /* JADX WARN: Multi-variable type inference failed */
    public FieldVector<T> add(SparseFieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        SparseFieldVector sparseFieldVector = (SparseFieldVector) copy();
        OpenIntToFieldHashMap<T>.Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            FieldElement value = iter.value();
            if (this.entries.containsKey(key)) {
                sparseFieldVector.setEntry(key, (FieldElement) this.entries.get(key).add(value));
            } else {
                sparseFieldVector.setEntry(key, value);
            }
        }
        return sparseFieldVector;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector */
    /* JADX WARN: Multi-variable type inference failed */
    public FieldVector<T> append(SparseFieldVector<T> v) {
        SparseFieldVector sparseFieldVector = new SparseFieldVector(this, v.getDimension());
        OpenIntToFieldHashMap<T>.Iterator iter = v.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            sparseFieldVector.setEntry(iter.key() + this.virtualSize, iter.value());
        }
        return sparseFieldVector;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> append(FieldVector<T> v) {
        if (v instanceof SparseFieldVector) {
            return append((SparseFieldVector) ((SparseFieldVector) v));
        }
        int n = v.getDimension();
        FieldVector<T> res = new SparseFieldVector<>(this, n);
        for (int i = 0; i < n; i++) {
            res.setEntry(this.virtualSize + i, v.getEntry(i));
        }
        return res;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> append(T d) throws NullArgumentException {
        MathUtils.checkNotNull(d);
        FieldVector<T> res = new SparseFieldVector<>(this, 1);
        res.setEntry(this.virtualSize, d);
        return res;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> copy() {
        return new SparseFieldVector(this);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public T dotProduct(FieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        T res = this.field.getZero();
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res = (T) ((FieldElement) res.add(v.getEntry(iter.key()).multiply(iter.value())));
        }
        return res;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> ebeDivide(FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        checkVectorDimensions(v.getDimension());
        SparseFieldVector sparseFieldVector = new SparseFieldVector(this);
        OpenIntToFieldHashMap<T>.Iterator iter = sparseFieldVector.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            sparseFieldVector.setEntry(iter.key(), (FieldElement) iter.value().divide(v.getEntry(iter.key())));
        }
        return sparseFieldVector;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> ebeMultiply(FieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        SparseFieldVector sparseFieldVector = new SparseFieldVector(this);
        OpenIntToFieldHashMap<T>.Iterator iter = sparseFieldVector.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            sparseFieldVector.setEntry(iter.key(), (FieldElement) iter.value().multiply(v.getEntry(iter.key())));
        }
        return sparseFieldVector;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    @Deprecated
    public T[] getData() {
        return toArray();
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public int getDimension() {
        return this.virtualSize;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public T getEntry(int index) throws OutOfRangeException {
        checkIndex(index);
        return this.entries.get(index);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public Field<T> getField() {
        return this.field;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> getSubVector(int index, int n) throws OutOfRangeException, NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, Integer.valueOf(n));
        }
        checkIndex(index);
        checkIndex((index + n) - 1);
        SparseFieldVector sparseFieldVector = new SparseFieldVector(this.field, n);
        int end = index + n;
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (key >= index && key < end) {
                sparseFieldVector.setEntry(key - index, iter.value());
            }
        }
        return sparseFieldVector;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapAdd(T d) throws NullArgumentException {
        return copy().mapAddToSelf(d);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapAddToSelf(T d) throws NullArgumentException {
        for (int i = 0; i < this.virtualSize; i++) {
            setEntry(i, (FieldElement) getEntry(i).add(d));
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapDivide(T d) throws NullArgumentException, MathArithmeticException {
        return copy().mapDivideToSelf(d);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: org.apache.commons.math3.util.OpenIntToFieldHashMap<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapDivideToSelf(T d) throws NullArgumentException, MathArithmeticException {
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.entries.put(iter.key(), (FieldElement) iter.value().divide(d));
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapInv() throws MathArithmeticException {
        return copy().mapInvToSelf();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapInvToSelf() throws MathArithmeticException {
        for (int i = 0; i < this.virtualSize; i++) {
            setEntry(i, (FieldElement) this.field.getOne().divide(getEntry(i)));
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapMultiply(T d) throws NullArgumentException {
        return copy().mapMultiplyToSelf(d);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: org.apache.commons.math3.util.OpenIntToFieldHashMap<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapMultiplyToSelf(T d) throws NullArgumentException {
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.entries.put(iter.key(), (FieldElement) iter.value().multiply(d));
        }
        return this;
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapSubtract(T d) throws NullArgumentException {
        return copy().mapSubtractToSelf(d);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> mapSubtractToSelf(T d) throws NullArgumentException {
        return mapAddToSelf((FieldElement) this.field.getZero().subtract(d));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.SparseFieldMatrix */
    /* JADX WARN: Multi-variable type inference failed */
    public FieldMatrix<T> outerProduct(SparseFieldVector<T> v) {
        SparseFieldMatrix sparseFieldMatrix = new SparseFieldMatrix(this.field, this.virtualSize, v.getDimension());
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            OpenIntToFieldHashMap<T>.Iterator iter2 = v.entries.iterator();
            while (iter2.hasNext()) {
                iter2.advance();
                sparseFieldMatrix.setEntry(iter.key(), iter2.key(), (FieldElement) iter.value().multiply(iter2.value()));
            }
        }
        return sparseFieldMatrix;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.SparseFieldMatrix */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldMatrix<T> outerProduct(FieldVector<T> v) {
        if (v instanceof SparseFieldVector) {
            return outerProduct((SparseFieldVector) ((SparseFieldVector) v));
        }
        int n = v.getDimension();
        SparseFieldMatrix sparseFieldMatrix = new SparseFieldMatrix(this.field, this.virtualSize, n);
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int row = iter.key();
            FieldElement<T> value = iter.value();
            for (int col = 0; col < n; col++) {
                sparseFieldMatrix.setEntry(row, col, value.multiply(v.getEntry(col)));
            }
        }
        return sparseFieldMatrix;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.linear.FieldVector<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> projection(FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        checkVectorDimensions(v.getDimension());
        return v.mapMultiply((FieldElement) dotProduct(v).divide(v.dotProduct(v)));
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public void set(T value) {
        MathUtils.checkNotNull(value);
        for (int i = 0; i < this.virtualSize; i++) {
            setEntry(i, value);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public void setEntry(int index, T value) throws NullArgumentException, OutOfRangeException {
        MathUtils.checkNotNull(value);
        checkIndex(index);
        this.entries.put(index, value);
    }

    @Override // org.apache.commons.math3.linear.FieldVector
    public void setSubVector(int index, FieldVector<T> v) throws OutOfRangeException {
        checkIndex(index);
        checkIndex((v.getDimension() + index) - 1);
        int n = v.getDimension();
        for (int i = 0; i < n; i++) {
            setEntry(i + index, v.getEntry(i));
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v1, resolved type: org.apache.commons.math3.linear.SparseFieldVector<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public SparseFieldVector<T> subtract(SparseFieldVector<T> v) throws DimensionMismatchException {
        checkVectorDimensions(v.getDimension());
        SparseFieldVector<T> res = (SparseFieldVector<T>) ((SparseFieldVector) copy());
        OpenIntToFieldHashMap<T>.Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (this.entries.containsKey(key)) {
                res.setEntry(key, (FieldElement) this.entries.get(key).subtract(iter.value()));
            } else {
                res.setEntry(key, (FieldElement) this.field.getZero().subtract(iter.value()));
            }
        }
        return res;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> subtract(FieldVector<T> v) throws DimensionMismatchException {
        if (v instanceof SparseFieldVector) {
            return subtract((SparseFieldVector) ((SparseFieldVector) v));
        }
        int n = v.getDimension();
        checkVectorDimensions(n);
        SparseFieldVector sparseFieldVector = new SparseFieldVector(this);
        for (int i = 0; i < n; i++) {
            if (this.entries.containsKey(i)) {
                sparseFieldVector.setEntry(i, (FieldElement) this.entries.get(i).subtract(v.getEntry(i)));
            } else {
                sparseFieldVector.setEntry(i, (FieldElement) this.field.getZero().subtract(v.getEntry(i)));
            }
        }
        return sparseFieldVector;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public T[] toArray() {
        T[] res = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, this.virtualSize));
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res[iter.key()] = iter.value();
        }
        return res;
    }

    private void checkIndex(int index) throws OutOfRangeException {
        if (index < 0 || index >= getDimension()) {
            throw new OutOfRangeException(Integer.valueOf(index), 0, Integer.valueOf(getDimension() - 1));
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

    /* access modifiers changed from: protected */
    public void checkVectorDimensions(int n) throws DimensionMismatchException {
        if (getDimension() != n) {
            throw new DimensionMismatchException(getDimension(), n);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: org.apache.commons.math3.linear.SparseFieldVector */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldVector
    public FieldVector<T> add(FieldVector<T> v) throws DimensionMismatchException {
        if (v instanceof SparseFieldVector) {
            return add((SparseFieldVector) ((SparseFieldVector) v));
        }
        int n = v.getDimension();
        checkVectorDimensions(n);
        SparseFieldVector sparseFieldVector = new SparseFieldVector(this.field, getDimension());
        for (int i = 0; i < n; i++) {
            sparseFieldVector.setEntry(i, (FieldElement) v.getEntry(i).add(getEntry(i)));
        }
        return sparseFieldVector;
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

    public int hashCode() {
        int result = (((this.field == null ? 0 : this.field.hashCode()) + 31) * 31) + this.virtualSize;
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            result = (result * 31) + iter.value().hashCode();
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SparseFieldVector)) {
            return false;
        }
        SparseFieldVector<T> other = (SparseFieldVector) obj;
        if (this.field == null) {
            if (other.field != null) {
                return false;
            }
        } else if (!this.field.equals(other.field)) {
            return false;
        }
        if (this.virtualSize != other.virtualSize) {
            return false;
        }
        OpenIntToFieldHashMap<T>.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (!other.getEntry(iter.key()).equals(iter.value())) {
                return false;
            }
        }
        OpenIntToFieldHashMap<T>.Iterator iter2 = other.getEntries().iterator();
        while (iter2.hasNext()) {
            iter2.advance();
            if (!iter2.value().equals(getEntry(iter2.key()))) {
                return false;
            }
        }
        return true;
    }
}
