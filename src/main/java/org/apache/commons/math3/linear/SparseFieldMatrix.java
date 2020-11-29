package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.util.OpenIntToFieldHashMap;

public class SparseFieldMatrix<T extends FieldElement<T>> extends AbstractFieldMatrix<T> {
    private final int columns;
    private final OpenIntToFieldHashMap<T> entries;
    private final int rows;

    public SparseFieldMatrix(Field<T> field) {
        super(field);
        this.rows = 0;
        this.columns = 0;
        this.entries = new OpenIntToFieldHashMap<>(field);
    }

    public SparseFieldMatrix(Field<T> field, int rowDimension, int columnDimension) {
        super(field, rowDimension, columnDimension);
        this.rows = rowDimension;
        this.columns = columnDimension;
        this.entries = new OpenIntToFieldHashMap<>(field);
    }

    public SparseFieldMatrix(SparseFieldMatrix<T> other) {
        super(other.getField(), other.getRowDimension(), other.getColumnDimension());
        this.rows = other.getRowDimension();
        this.columns = other.getColumnDimension();
        this.entries = new OpenIntToFieldHashMap<>(other.entries);
    }

    public SparseFieldMatrix(FieldMatrix<T> other) {
        super(other.getField(), other.getRowDimension(), other.getColumnDimension());
        this.rows = other.getRowDimension();
        this.columns = other.getColumnDimension();
        this.entries = new OpenIntToFieldHashMap<>(getField());
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                setEntry(i, j, other.getEntry(i, j));
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v6, resolved type: org.apache.commons.math3.util.OpenIntToFieldHashMap<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void addToEntry(int row, int column, T increment) {
        checkRowIndex(row);
        checkColumnIndex(column);
        int key = computeKey(row, column);
        FieldElement fieldElement = (FieldElement) this.entries.get(key).add(increment);
        if (getField().getZero().equals(fieldElement)) {
            this.entries.remove(key);
        } else {
            this.entries.put(key, fieldElement);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> copy() {
        return new SparseFieldMatrix((SparseFieldMatrix) this);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public FieldMatrix<T> createMatrix(int rowDimension, int columnDimension) {
        return new SparseFieldMatrix(getField(), rowDimension, columnDimension);
    }

    @Override // org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public int getColumnDimension() {
        return this.columns;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public T getEntry(int row, int column) {
        checkRowIndex(row);
        checkColumnIndex(column);
        return this.entries.get(computeKey(row, column));
    }

    @Override // org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public int getRowDimension() {
        return this.rows;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v6, resolved type: org.apache.commons.math3.util.OpenIntToFieldHashMap<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void multiplyEntry(int row, int column, T factor) {
        checkRowIndex(row);
        checkColumnIndex(column);
        int key = computeKey(row, column);
        FieldElement fieldElement = (FieldElement) this.entries.get(key).multiply(factor);
        if (getField().getZero().equals(fieldElement)) {
            this.entries.remove(key);
        } else {
            this.entries.put(key, fieldElement);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix, org.apache.commons.math3.linear.AbstractFieldMatrix
    public void setEntry(int row, int column, T value) {
        checkRowIndex(row);
        checkColumnIndex(column);
        if (getField().getZero().equals(value)) {
            this.entries.remove(computeKey(row, column));
        } else {
            this.entries.put(computeKey(row, column), value);
        }
    }

    private int computeKey(int row, int column) {
        return (this.columns * row) + column;
    }
}
