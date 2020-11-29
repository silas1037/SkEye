package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface FieldMatrix<T extends FieldElement<T>> extends AnyMatrix {
    FieldMatrix<T> add(FieldMatrix<T> fieldMatrix) throws MatrixDimensionMismatchException;

    void addToEntry(int i, int i2, T t) throws OutOfRangeException;

    FieldMatrix<T> copy();

    void copySubMatrix(int i, int i2, int i3, int i4, T[][] tArr) throws MatrixDimensionMismatchException, NumberIsTooSmallException, OutOfRangeException;

    void copySubMatrix(int[] iArr, int[] iArr2, T[][] tArr) throws MatrixDimensionMismatchException, NoDataException, NullArgumentException, OutOfRangeException;

    FieldMatrix<T> createMatrix(int i, int i2) throws NotStrictlyPositiveException;

    T[] getColumn(int i) throws OutOfRangeException;

    FieldMatrix<T> getColumnMatrix(int i) throws OutOfRangeException;

    FieldVector<T> getColumnVector(int i) throws OutOfRangeException;

    T[][] getData();

    T getEntry(int i, int i2) throws OutOfRangeException;

    Field<T> getField();

    T[] getRow(int i) throws OutOfRangeException;

    FieldMatrix<T> getRowMatrix(int i) throws OutOfRangeException;

    FieldVector<T> getRowVector(int i) throws OutOfRangeException;

    FieldMatrix<T> getSubMatrix(int i, int i2, int i3, int i4) throws NumberIsTooSmallException, OutOfRangeException;

    FieldMatrix<T> getSubMatrix(int[] iArr, int[] iArr2) throws NoDataException, NullArgumentException, OutOfRangeException;

    T getTrace() throws NonSquareMatrixException;

    FieldMatrix<T> multiply(FieldMatrix<T> fieldMatrix) throws DimensionMismatchException;

    void multiplyEntry(int i, int i2, T t) throws OutOfRangeException;

    FieldVector<T> operate(FieldVector<T> fieldVector) throws DimensionMismatchException;

    T[] operate(T[] tArr) throws DimensionMismatchException;

    FieldMatrix<T> power(int i) throws NonSquareMatrixException, NotPositiveException;

    FieldMatrix<T> preMultiply(FieldMatrix<T> fieldMatrix) throws DimensionMismatchException;

    FieldVector<T> preMultiply(FieldVector<T> fieldVector) throws DimensionMismatchException;

    T[] preMultiply(T[] tArr) throws DimensionMismatchException;

    FieldMatrix<T> scalarAdd(T t);

    FieldMatrix<T> scalarMultiply(T t);

    void setColumn(int i, T[] tArr) throws MatrixDimensionMismatchException, OutOfRangeException;

    void setColumnMatrix(int i, FieldMatrix<T> fieldMatrix) throws MatrixDimensionMismatchException, OutOfRangeException;

    void setColumnVector(int i, FieldVector<T> fieldVector) throws MatrixDimensionMismatchException, OutOfRangeException;

    void setEntry(int i, int i2, T t) throws OutOfRangeException;

    void setRow(int i, T[] tArr) throws MatrixDimensionMismatchException, OutOfRangeException;

    void setRowMatrix(int i, FieldMatrix<T> fieldMatrix) throws MatrixDimensionMismatchException, OutOfRangeException;

    void setRowVector(int i, FieldVector<T> fieldVector) throws MatrixDimensionMismatchException, OutOfRangeException;

    void setSubMatrix(T[][] tArr, int i, int i2) throws DimensionMismatchException, OutOfRangeException, NoDataException, NullArgumentException;

    FieldMatrix<T> subtract(FieldMatrix<T> fieldMatrix) throws MatrixDimensionMismatchException;

    FieldMatrix<T> transpose();

    T walkInColumnOrder(FieldMatrixChangingVisitor<T> fieldMatrixChangingVisitor);

    T walkInColumnOrder(FieldMatrixChangingVisitor<T> fieldMatrixChangingVisitor, int i, int i2, int i3, int i4) throws NumberIsTooSmallException, OutOfRangeException;

    T walkInColumnOrder(FieldMatrixPreservingVisitor<T> fieldMatrixPreservingVisitor);

    T walkInColumnOrder(FieldMatrixPreservingVisitor<T> fieldMatrixPreservingVisitor, int i, int i2, int i3, int i4) throws NumberIsTooSmallException, OutOfRangeException;

    T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> fieldMatrixChangingVisitor);

    T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> fieldMatrixChangingVisitor, int i, int i2, int i3, int i4) throws NumberIsTooSmallException, OutOfRangeException;

    T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> fieldMatrixPreservingVisitor);

    T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> fieldMatrixPreservingVisitor, int i, int i2, int i3, int i4) throws NumberIsTooSmallException, OutOfRangeException;

    T walkInRowOrder(FieldMatrixChangingVisitor<T> fieldMatrixChangingVisitor);

    T walkInRowOrder(FieldMatrixChangingVisitor<T> fieldMatrixChangingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;

    T walkInRowOrder(FieldMatrixPreservingVisitor<T> fieldMatrixPreservingVisitor);

    T walkInRowOrder(FieldMatrixPreservingVisitor<T> fieldMatrixPreservingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;
}
