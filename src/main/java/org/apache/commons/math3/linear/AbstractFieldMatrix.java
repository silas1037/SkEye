package org.apache.commons.math3.linear;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.VectorFormat;
import org.apache.commons.math3.util.MathArrays;

public abstract class AbstractFieldMatrix<T extends FieldElement<T>> implements FieldMatrix<T> {
    private final Field<T> field;

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public abstract void addToEntry(int i, int i2, T t) throws OutOfRangeException;

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public abstract FieldMatrix<T> copy();

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public abstract FieldMatrix<T> createMatrix(int i, int i2) throws NotStrictlyPositiveException;

    @Override // org.apache.commons.math3.linear.AnyMatrix
    public abstract int getColumnDimension();

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public abstract T getEntry(int i, int i2) throws OutOfRangeException;

    @Override // org.apache.commons.math3.linear.AnyMatrix
    public abstract int getRowDimension();

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public abstract void multiplyEntry(int i, int i2, T t) throws OutOfRangeException;

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public abstract void setEntry(int i, int i2, T t) throws OutOfRangeException;

    protected AbstractFieldMatrix() {
        this.field = null;
    }

    protected AbstractFieldMatrix(Field<T> field2) {
        this.field = field2;
    }

    protected AbstractFieldMatrix(Field<T> field2, int rowDimension, int columnDimension) throws NotStrictlyPositiveException {
        if (rowDimension <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION, Integer.valueOf(rowDimension));
        } else if (columnDimension <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION, Integer.valueOf(columnDimension));
        } else {
            this.field = field2;
        }
    }

    protected static <T extends FieldElement<T>> Field<T> extractField(T[][] d) throws NoDataException, NullArgumentException {
        if (d == null) {
            throw new NullArgumentException();
        } else if (d.length == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
        } else if (d[0].length != 0) {
            return d[0][0].getField();
        } else {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
    }

    protected static <T extends FieldElement<T>> Field<T> extractField(T[] d) throws NoDataException {
        if (d.length != 0) {
            return d[0].getField();
        }
        throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
    }

    @Deprecated
    protected static <T extends FieldElement<T>> T[][] buildArray(Field<T> field2, int rows, int columns) {
        return (T[][]) ((FieldElement[][]) MathArrays.buildArray(field2, rows, columns));
    }

    @Deprecated
    protected static <T extends FieldElement<T>> T[] buildArray(Field<T> field2, int length) {
        return (T[]) ((FieldElement[]) MathArrays.buildArray(field2, length));
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public Field<T> getField() {
        return this.field;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> add(FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        checkAdditionCompatible(m);
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, (T) ((FieldElement) getEntry(row, col).add(m.getEntry(row, col))));
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> subtract(FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        checkSubtractionCompatible(m);
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, (T) ((FieldElement) getEntry(row, col).subtract(m.getEntry(row, col))));
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> scalarAdd(T d) {
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, (T) ((FieldElement) getEntry(row, col).add(d)));
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> scalarMultiply(T d) {
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, (T) ((FieldElement) getEntry(row, col).multiply(d)));
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> multiply(FieldMatrix<T> m) throws DimensionMismatchException {
        checkMultiplicationCompatible(m);
        int nRows = getRowDimension();
        int nCols = m.getColumnDimension();
        int nSum = getColumnDimension();
        FieldMatrix<T> out = createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                T sum = this.field.getZero();
                for (int i = 0; i < nSum; i++) {
                    sum = (T) ((FieldElement) sum.add(getEntry(row, i).multiply(m.getEntry(i, col))));
                }
                out.setEntry(row, col, sum);
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> preMultiply(FieldMatrix<T> m) throws DimensionMismatchException {
        return m.multiply(this);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> power(int p) throws NonSquareMatrixException, NotPositiveException {
        if (p < 0) {
            throw new NotPositiveException(Integer.valueOf(p));
        } else if (!isSquare()) {
            throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
        } else if (p == 0) {
            return MatrixUtils.createFieldIdentityMatrix(getField(), getRowDimension());
        } else {
            if (p == 1) {
                return copy();
            }
            char[] binaryRepresentation = Integer.toBinaryString(p - 1).toCharArray();
            ArrayList<Integer> nonZeroPositions = new ArrayList<>();
            for (int i = 0; i < binaryRepresentation.length; i++) {
                if (binaryRepresentation[i] == '1') {
                    nonZeroPositions.add(Integer.valueOf((binaryRepresentation.length - i) - 1));
                }
            }
            ArrayList<FieldMatrix<T>> results = new ArrayList<>(binaryRepresentation.length);
            results.add(0, copy());
            for (int i2 = 1; i2 < binaryRepresentation.length; i2++) {
                FieldMatrix<T> s = results.get(i2 - 1);
                results.add(i2, s.multiply(s));
            }
            FieldMatrix<T> result = copy();
            Iterator i$ = nonZeroPositions.iterator();
            while (i$.hasNext()) {
                result = result.multiply(results.get(i$.next().intValue()));
            }
            return result;
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T[][] getData() {
        T[][] data = (T[][]) ((FieldElement[][]) MathArrays.buildArray(this.field, getRowDimension(), getColumnDimension()));
        for (int i = 0; i < data.length; i++) {
            T[] dataI = data[i];
            for (int j = 0; j < dataI.length; j++) {
                dataI[j] = getEntry(i, j);
            }
        }
        return data;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        FieldMatrix<T> subMatrix = createMatrix((endRow - startRow) + 1, (endColumn - startColumn) + 1);
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
            }
        }
        return subMatrix;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> getSubMatrix(final int[] selectedRows, final int[] selectedColumns) throws NoDataException, NullArgumentException, OutOfRangeException {
        checkSubMatrixIndex(selectedRows, selectedColumns);
        FieldMatrix<T> subMatrix = createMatrix(selectedRows.length, selectedColumns.length);
        subMatrix.walkInOptimizedOrder(new DefaultFieldMatrixChangingVisitor<T>(this.field.getZero()) {
            /* class org.apache.commons.math3.linear.AbstractFieldMatrix.C02591 */

            @Override // org.apache.commons.math3.linear.DefaultFieldMatrixChangingVisitor, org.apache.commons.math3.linear.FieldMatrixChangingVisitor
            public T visit(int row, int column, T t) {
                return (T) AbstractFieldMatrix.this.getEntry(selectedRows[row], selectedColumns[column]);
            }
        });
        return subMatrix;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn, final T[][] destination) throws MatrixDimensionMismatchException, NumberIsTooSmallException, OutOfRangeException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        int rowsCount = (endRow + 1) - startRow;
        int columnsCount = (endColumn + 1) - startColumn;
        if (destination.length < rowsCount || destination[0].length < columnsCount) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, rowsCount, columnsCount);
        }
        walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>(this.field.getZero()) {
            /* class org.apache.commons.math3.linear.AbstractFieldMatrix.C02602 */
            private int startColumn;
            private int startRow;

            @Override // org.apache.commons.math3.linear.FieldMatrixPreservingVisitor, org.apache.commons.math3.linear.DefaultFieldMatrixPreservingVisitor
            public void start(int rows, int columns, int startRow2, int endRow, int startColumn2, int endColumn) {
                this.startRow = startRow2;
                this.startColumn = startColumn2;
            }

            @Override // org.apache.commons.math3.linear.FieldMatrixPreservingVisitor, org.apache.commons.math3.linear.DefaultFieldMatrixPreservingVisitor
            public void visit(int row, int column, T value) {
                destination[row - this.startRow][column - this.startColumn] = value;
            }
        }, startRow, endRow, startColumn, endColumn);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void copySubMatrix(int[] selectedRows, int[] selectedColumns, T[][] destination) throws MatrixDimensionMismatchException, NoDataException, NullArgumentException, OutOfRangeException {
        checkSubMatrixIndex(selectedRows, selectedColumns);
        if (destination.length < selectedRows.length || destination[0].length < selectedColumns.length) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, selectedRows.length, selectedColumns.length);
        }
        for (int i = 0; i < selectedRows.length; i++) {
            T[] destinationI = destination[i];
            for (int j = 0; j < selectedColumns.length; j++) {
                destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
            }
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void setSubMatrix(T[][] subMatrix, int row, int column) throws DimensionMismatchException, OutOfRangeException, NoDataException, NullArgumentException {
        if (subMatrix == null) {
            throw new NullArgumentException();
        }
        int nRows = subMatrix.length;
        if (nRows == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }
        int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        for (int r = 1; r < nRows; r++) {
            if (subMatrix[r].length != nCols) {
                throw new DimensionMismatchException(nCols, subMatrix[r].length);
            }
        }
        checkRowIndex(row);
        checkColumnIndex(column);
        checkRowIndex((nRows + row) - 1);
        checkColumnIndex((nCols + column) - 1);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                setEntry(row + i, column + j, subMatrix[i][j]);
            }
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> getRowMatrix(int row) throws OutOfRangeException {
        checkRowIndex(row);
        int nCols = getColumnDimension();
        FieldMatrix<T> out = createMatrix(1, nCols);
        for (int i = 0; i < nCols; i++) {
            out.setEntry(0, i, getEntry(row, i));
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void setRowMatrix(int row, FieldMatrix<T> matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        checkRowIndex(row);
        int nCols = getColumnDimension();
        if (matrix.getRowDimension() == 1 && matrix.getColumnDimension() == nCols) {
            for (int i = 0; i < nCols; i++) {
                setEntry(row, i, matrix.getEntry(0, i));
            }
            return;
        }
        throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> getColumnMatrix(int column) throws OutOfRangeException {
        checkColumnIndex(column);
        int nRows = getRowDimension();
        FieldMatrix<T> out = createMatrix(nRows, 1);
        for (int i = 0; i < nRows; i++) {
            out.setEntry(i, 0, getEntry(i, column));
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void setColumnMatrix(int column, FieldMatrix<T> matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        checkColumnIndex(column);
        int nRows = getRowDimension();
        if (matrix.getRowDimension() == nRows && matrix.getColumnDimension() == 1) {
            for (int i = 0; i < nRows; i++) {
                setEntry(i, column, matrix.getEntry(i, 0));
            }
            return;
        }
        throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldVector<T> getRowVector(int row) throws OutOfRangeException {
        return new ArrayFieldVector((Field) this.field, (FieldElement[]) getRow(row), false);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void setRowVector(int row, FieldVector<T> vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        checkRowIndex(row);
        int nCols = getColumnDimension();
        if (vector.getDimension() != nCols) {
            throw new MatrixDimensionMismatchException(1, vector.getDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; i++) {
            setEntry(row, i, vector.getEntry(i));
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldVector<T> getColumnVector(int column) throws OutOfRangeException {
        return new ArrayFieldVector((Field) this.field, (FieldElement[]) getColumn(column), false);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void setColumnVector(int column, FieldVector<T> vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        checkColumnIndex(column);
        int nRows = getRowDimension();
        if (vector.getDimension() != nRows) {
            throw new MatrixDimensionMismatchException(vector.getDimension(), 1, nRows, 1);
        }
        for (int i = 0; i < nRows; i++) {
            setEntry(i, column, vector.getEntry(i));
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T[] getRow(int row) throws OutOfRangeException {
        checkRowIndex(row);
        int nCols = getColumnDimension();
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, nCols));
        for (int i = 0; i < nCols; i++) {
            out[i] = getEntry(row, i);
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void setRow(int row, T[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        checkRowIndex(row);
        int nCols = getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        for (int i = 0; i < nCols; i++) {
            setEntry(row, i, array[i]);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T[] getColumn(int column) throws OutOfRangeException {
        checkColumnIndex(column);
        int nRows = getRowDimension();
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, nRows));
        for (int i = 0; i < nRows; i++) {
            out[i] = getEntry(i, column);
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public void setColumn(int column, T[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        checkColumnIndex(column);
        int nRows = getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        for (int i = 0; i < nRows; i++) {
            setEntry(i, column, array[i]);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldMatrix<T> transpose() {
        final FieldMatrix<T> out = createMatrix(getColumnDimension(), getRowDimension());
        walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>(this.field.getZero()) {
            /* class org.apache.commons.math3.linear.AbstractFieldMatrix.C02613 */

            @Override // org.apache.commons.math3.linear.FieldMatrixPreservingVisitor, org.apache.commons.math3.linear.DefaultFieldMatrixPreservingVisitor
            public void visit(int row, int column, T value) {
                out.setEntry(column, row, value);
            }
        });
        return out;
    }

    @Override // org.apache.commons.math3.linear.AnyMatrix
    public boolean isSquare() {
        return getColumnDimension() == getRowDimension();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T getTrace() throws NonSquareMatrixException {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(nRows, nCols);
        }
        T trace = this.field.getZero();
        for (int i = 0; i < nRows; i++) {
            trace = (T) ((FieldElement) trace.add(getEntry(i, i)));
        }
        return trace;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T[] operate(T[] v) throws DimensionMismatchException {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (v.length != nCols) {
            throw new DimensionMismatchException(v.length, nCols);
        }
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, nRows));
        for (int row = 0; row < nRows; row++) {
            T zero = this.field.getZero();
            for (int i = 0; i < nCols; i++) {
                zero = (FieldElement) zero.add(getEntry(row, i).multiply(v[i]));
            }
            out[row] = zero;
        }
        return out;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r13v0, resolved type: org.apache.commons.math3.linear.AbstractFieldMatrix<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldVector<T> operate(FieldVector<T> v) throws DimensionMismatchException {
        try {
            return new ArrayFieldVector((Field) this.field, operate(((ArrayFieldVector) v).getDataRef()), false);
        } catch (ClassCastException e) {
            int nRows = getRowDimension();
            int nCols = getColumnDimension();
            if (v.getDimension() != nCols) {
                throw new DimensionMismatchException(v.getDimension(), nCols);
            }
            FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, nRows);
            for (int row = 0; row < nRows; row++) {
                T zero = this.field.getZero();
                for (int i = 0; i < nCols; i++) {
                    zero = (FieldElement) zero.add(getEntry(row, i).multiply(v.getEntry(i)));
                }
                fieldElementArr[row] = zero;
            }
            return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T[] preMultiply(T[] v) throws DimensionMismatchException {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (v.length != nRows) {
            throw new DimensionMismatchException(v.length, nRows);
        }
        T[] out = (T[]) ((FieldElement[]) MathArrays.buildArray(this.field, nCols));
        for (int col = 0; col < nCols; col++) {
            T zero = this.field.getZero();
            for (int i = 0; i < nRows; i++) {
                zero = (FieldElement) zero.add(getEntry(i, col).multiply(v[i]));
            }
            out[col] = zero;
        }
        return out;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r13v0, resolved type: org.apache.commons.math3.linear.AbstractFieldMatrix<T extends org.apache.commons.math3.FieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.linear.FieldMatrix
    public FieldVector<T> preMultiply(FieldVector<T> v) throws DimensionMismatchException {
        try {
            return new ArrayFieldVector((Field) this.field, preMultiply(((ArrayFieldVector) v).getDataRef()), false);
        } catch (ClassCastException e) {
            int nRows = getRowDimension();
            int nCols = getColumnDimension();
            if (v.getDimension() != nRows) {
                throw new DimensionMismatchException(v.getDimension(), nRows);
            }
            FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, nCols);
            for (int col = 0; col < nCols; col++) {
                T zero = this.field.getZero();
                for (int i = 0; i < nRows; i++) {
                    zero = (FieldElement) zero.add(getEntry(i, col).multiply(v.getEntry(i)));
                }
                fieldElementArr[col] = zero;
            }
            return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
        }
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor) {
        int rows = getRowDimension();
        int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                setEntry(row, column, visitor.visit(row, column, getEntry(row, column)));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor) {
        int rows = getRowDimension();
        int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; row++) {
            for (int column = startColumn; column <= endColumn; column++) {
                setEntry(row, column, visitor.visit(row, column, getEntry(row, column)));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; row++) {
            for (int column = startColumn; column <= endColumn; column++) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInColumnOrder(FieldMatrixChangingVisitor<T> visitor) {
        int rows = getRowDimension();
        int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                setEntry(row, column, visitor.visit(row, column, getEntry(row, column)));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInColumnOrder(FieldMatrixPreservingVisitor<T> visitor) {
        int rows = getRowDimension();
        int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInColumnOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; column++) {
            for (int row = startRow; row <= endRow; row++) {
                setEntry(row, column, visitor.visit(row, column, getEntry(row, column)));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInColumnOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; column++) {
            for (int row = startRow; row <= endRow; row++) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor) {
        return walkInRowOrder(visitor);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor) {
        return walkInRowOrder(visitor);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    @Override // org.apache.commons.math3.linear.FieldMatrix
    public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    public String toString() {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        StringBuffer res = new StringBuffer();
        String fullClassName = getClass().getName();
        res.append(fullClassName.substring(fullClassName.lastIndexOf(46) + 1)).append(VectorFormat.DEFAULT_PREFIX);
        for (int i = 0; i < nRows; i++) {
            if (i > 0) {
                res.append(",");
            }
            res.append(VectorFormat.DEFAULT_PREFIX);
            for (int j = 0; j < nCols; j++) {
                if (j > 0) {
                    res.append(",");
                }
                res.append(getEntry(i, j));
            }
            res.append(VectorFormat.DEFAULT_SUFFIX);
        }
        res.append(VectorFormat.DEFAULT_SUFFIX);
        return res.toString();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof FieldMatrix)) {
            return false;
        }
        FieldMatrix<?> m = (FieldMatrix) object;
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (!(m.getColumnDimension() == nCols && m.getRowDimension() == nRows)) {
            return false;
        }
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                if (!getEntry(row, col).equals(m.getEntry(row, col))) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        int ret = ((9999422 + nRows) * 31) + nCols;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                ret = (ret * 31) + ((((row + 1) * 11) + ((col + 1) * 17)) * getEntry(row, col).hashCode());
            }
        }
        return ret;
    }

    /* access modifiers changed from: protected */
    public void checkRowIndex(int row) throws OutOfRangeException {
        if (row < 0 || row >= getRowDimension()) {
            throw new OutOfRangeException(LocalizedFormats.ROW_INDEX, Integer.valueOf(row), 0, Integer.valueOf(getRowDimension() - 1));
        }
    }

    /* access modifiers changed from: protected */
    public void checkColumnIndex(int column) throws OutOfRangeException {
        if (column < 0 || column >= getColumnDimension()) {
            throw new OutOfRangeException(LocalizedFormats.COLUMN_INDEX, Integer.valueOf(column), 0, Integer.valueOf(getColumnDimension() - 1));
        }
    }

    /* access modifiers changed from: protected */
    public void checkSubMatrixIndex(int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        checkRowIndex(startRow);
        checkRowIndex(endRow);
        if (endRow < startRow) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, Integer.valueOf(endRow), Integer.valueOf(startRow), true);
        }
        checkColumnIndex(startColumn);
        checkColumnIndex(endColumn);
        if (endColumn < startColumn) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, Integer.valueOf(endColumn), Integer.valueOf(startColumn), true);
        }
    }

    /* access modifiers changed from: protected */
    public void checkSubMatrixIndex(int[] selectedRows, int[] selectedColumns) throws NoDataException, NullArgumentException, OutOfRangeException {
        if (selectedRows == null || selectedColumns == null) {
            throw new NullArgumentException();
        } else if (selectedRows.length == 0 || selectedColumns.length == 0) {
            throw new NoDataException();
        } else {
            for (int row : selectedRows) {
                checkRowIndex(row);
            }
            for (int column : selectedColumns) {
                checkColumnIndex(column);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void checkAdditionCompatible(FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        if (getRowDimension() != m.getRowDimension() || getColumnDimension() != m.getColumnDimension()) {
            throw new MatrixDimensionMismatchException(m.getRowDimension(), m.getColumnDimension(), getRowDimension(), getColumnDimension());
        }
    }

    /* access modifiers changed from: protected */
    public void checkSubtractionCompatible(FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        if (getRowDimension() != m.getRowDimension() || getColumnDimension() != m.getColumnDimension()) {
            throw new MatrixDimensionMismatchException(m.getRowDimension(), m.getColumnDimension(), getRowDimension(), getColumnDimension());
        }
    }

    /* access modifiers changed from: protected */
    public void checkMultiplicationCompatible(FieldMatrix<T> m) throws DimensionMismatchException {
        if (getColumnDimension() != m.getRowDimension()) {
            throw new DimensionMismatchException(m.getRowDimension(), getColumnDimension());
        }
    }
}
