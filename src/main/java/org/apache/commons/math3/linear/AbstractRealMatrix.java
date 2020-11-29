package org.apache.commons.math3.linear;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public abstract class AbstractRealMatrix extends RealLinearOperator implements RealMatrix {
    private static final RealMatrixFormat DEFAULT_FORMAT = RealMatrixFormat.getInstance(Locale.US);

    @Override // org.apache.commons.math3.linear.RealMatrix
    public abstract RealMatrix copy();

    @Override // org.apache.commons.math3.linear.RealMatrix
    public abstract RealMatrix createMatrix(int i, int i2) throws NotStrictlyPositiveException;

    @Override // org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.RealLinearOperator
    public abstract int getColumnDimension();

    @Override // org.apache.commons.math3.linear.RealMatrix
    public abstract double getEntry(int i, int i2) throws OutOfRangeException;

    @Override // org.apache.commons.math3.linear.AnyMatrix, org.apache.commons.math3.linear.RealLinearOperator
    public abstract int getRowDimension();

    @Override // org.apache.commons.math3.linear.RealMatrix
    public abstract void setEntry(int i, int i2, double d) throws OutOfRangeException;

    static {
        DEFAULT_FORMAT.getFormat().setMinimumFractionDigits(1);
    }

    protected AbstractRealMatrix() {
    }

    protected AbstractRealMatrix(int rowDimension, int columnDimension) throws NotStrictlyPositiveException {
        if (rowDimension < 1) {
            throw new NotStrictlyPositiveException(Integer.valueOf(rowDimension));
        } else if (columnDimension < 1) {
            throw new NotStrictlyPositiveException(Integer.valueOf(columnDimension));
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix add(RealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkAdditionCompatible(this, m);
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, getEntry(row, col) + m.getEntry(row, col));
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix subtract(RealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkSubtractionCompatible(this, m);
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, getEntry(row, col) - m.getEntry(row, col));
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix scalarAdd(double d) {
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, getEntry(row, col) + d);
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix scalarMultiply(double d) {
        int rowCount = getRowDimension();
        int columnCount = getColumnDimension();
        RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                out.setEntry(row, col, getEntry(row, col) * d);
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix multiply(RealMatrix m) throws DimensionMismatchException {
        MatrixUtils.checkMultiplicationCompatible(this, m);
        int nRows = getRowDimension();
        int nCols = m.getColumnDimension();
        int nSum = getColumnDimension();
        RealMatrix out = createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                double sum = 0.0d;
                for (int i = 0; i < nSum; i++) {
                    sum += getEntry(row, i) * m.getEntry(i, col);
                }
                out.setEntry(row, col, sum);
            }
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix preMultiply(RealMatrix m) throws DimensionMismatchException {
        return m.multiply(this);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix power(int p) throws NotPositiveException, NonSquareMatrixException {
        if (p < 0) {
            throw new NotPositiveException(LocalizedFormats.NOT_POSITIVE_EXPONENT, Integer.valueOf(p));
        } else if (!isSquare()) {
            throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
        } else if (p == 0) {
            return MatrixUtils.createRealIdentityMatrix(getRowDimension());
        } else {
            if (p == 1) {
                return copy();
            }
            char[] binaryRepresentation = Integer.toBinaryString(p - 1).toCharArray();
            ArrayList<Integer> nonZeroPositions = new ArrayList<>();
            int maxI = -1;
            for (int i = 0; i < binaryRepresentation.length; i++) {
                if (binaryRepresentation[i] == '1') {
                    int pos = (binaryRepresentation.length - i) - 1;
                    nonZeroPositions.add(Integer.valueOf(pos));
                    if (maxI == -1) {
                        maxI = pos;
                    }
                }
            }
            RealMatrix[] results = new RealMatrix[(maxI + 1)];
            results[0] = copy();
            for (int i2 = 1; i2 <= maxI; i2++) {
                results[i2] = results[i2 - 1].multiply(results[i2 - 1]);
            }
            RealMatrix result = copy();
            Iterator i$ = nonZeroPositions.iterator();
            while (i$.hasNext()) {
                result = result.multiply(results[i$.next().intValue()]);
            }
            return result;
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double[][] getData() {
        double[][] data = (double[][]) Array.newInstance(Double.TYPE, getRowDimension(), getColumnDimension());
        for (int i = 0; i < data.length; i++) {
            double[] dataI = data[i];
            for (int j = 0; j < dataI.length; j++) {
                dataI[j] = getEntry(i, j);
            }
        }
        return data;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double getNorm() {
        return walkInColumnOrder(new RealMatrixPreservingVisitor() {
            /* class org.apache.commons.math3.linear.AbstractRealMatrix.C02621 */
            private double columnSum;
            private double endRow;
            private double maxColSum;

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
            public void start(int rows, int columns, int startRow, int endRow2, int startColumn, int endColumn) {
                this.endRow = (double) endRow2;
                this.columnSum = 0.0d;
                this.maxColSum = 0.0d;
            }

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
            public void visit(int row, int column, double value) {
                this.columnSum += FastMath.abs(value);
                if (((double) row) == this.endRow) {
                    this.maxColSum = FastMath.max(this.maxColSum, this.columnSum);
                    this.columnSum = 0.0d;
                }
            }

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
            public double end() {
                return this.maxColSum;
            }
        });
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double getFrobeniusNorm() {
        return walkInOptimizedOrder(new RealMatrixPreservingVisitor() {
            /* class org.apache.commons.math3.linear.AbstractRealMatrix.C02632 */
            private double sum;

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
            public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
                this.sum = 0.0d;
            }

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
            public void visit(int row, int column, double value) {
                this.sum += value * value;
            }

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
            public double end() {
                return FastMath.sqrt(this.sum);
            }
        });
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        RealMatrix subMatrix = createMatrix((endRow - startRow) + 1, (endColumn - startColumn) + 1);
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
            }
        }
        return subMatrix;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix getSubMatrix(final int[] selectedRows, final int[] selectedColumns) throws NullArgumentException, NoDataException, OutOfRangeException {
        MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);
        RealMatrix subMatrix = createMatrix(selectedRows.length, selectedColumns.length);
        subMatrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            /* class org.apache.commons.math3.linear.AbstractRealMatrix.C02643 */

            @Override // org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor, org.apache.commons.math3.linear.RealMatrixChangingVisitor
            public double visit(int row, int column, double value) {
                return AbstractRealMatrix.this.getEntry(selectedRows[row], selectedColumns[column]);
            }
        });
        return subMatrix;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn, final double[][] destination) throws OutOfRangeException, NumberIsTooSmallException, MatrixDimensionMismatchException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        int rowsCount = (endRow + 1) - startRow;
        int columnsCount = (endColumn + 1) - startColumn;
        if (destination.length < rowsCount || destination[0].length < columnsCount) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, rowsCount, columnsCount);
        }
        for (int i = 1; i < rowsCount; i++) {
            if (destination[i].length < columnsCount) {
                throw new MatrixDimensionMismatchException(destination.length, destination[i].length, rowsCount, columnsCount);
            }
        }
        walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            /* class org.apache.commons.math3.linear.AbstractRealMatrix.C02654 */
            private int startColumn;
            private int startRow;

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor, org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor
            public void start(int rows, int columns, int startRow2, int endRow, int startColumn2, int endColumn) {
                this.startRow = startRow2;
                this.startColumn = startColumn2;
            }

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor, org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor
            public void visit(int row, int column, double value) {
                destination[row - this.startRow][column - this.startColumn] = value;
            }
        }, startRow, endRow, startColumn, endColumn);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void copySubMatrix(int[] selectedRows, int[] selectedColumns, double[][] destination) throws OutOfRangeException, NullArgumentException, NoDataException, MatrixDimensionMismatchException {
        MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);
        int nCols = selectedColumns.length;
        if (destination.length < selectedRows.length || destination[0].length < nCols) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, selectedRows.length, selectedColumns.length);
        }
        for (int i = 0; i < selectedRows.length; i++) {
            double[] destinationI = destination[i];
            if (destinationI.length < nCols) {
                throw new MatrixDimensionMismatchException(destination.length, destinationI.length, selectedRows.length, selectedColumns.length);
            }
            for (int j = 0; j < selectedColumns.length; j++) {
                destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
            }
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void setSubMatrix(double[][] subMatrix, int row, int column) throws NoDataException, OutOfRangeException, DimensionMismatchException, NullArgumentException {
        MathUtils.checkNotNull(subMatrix);
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
        MatrixUtils.checkRowIndex(this, row);
        MatrixUtils.checkColumnIndex(this, column);
        MatrixUtils.checkRowIndex(this, (nRows + row) - 1);
        MatrixUtils.checkColumnIndex(this, (nCols + column) - 1);
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                setEntry(row + i, column + j, subMatrix[i][j]);
            }
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix getRowMatrix(int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        int nCols = getColumnDimension();
        RealMatrix out = createMatrix(1, nCols);
        for (int i = 0; i < nCols; i++) {
            out.setEntry(0, i, getEntry(row, i));
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void setRowMatrix(int row, RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        int nCols = getColumnDimension();
        if (matrix.getRowDimension() == 1 && matrix.getColumnDimension() == nCols) {
            for (int i = 0; i < nCols; i++) {
                setEntry(row, i, matrix.getEntry(0, i));
            }
            return;
        }
        throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix getColumnMatrix(int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        int nRows = getRowDimension();
        RealMatrix out = createMatrix(nRows, 1);
        for (int i = 0; i < nRows; i++) {
            out.setEntry(i, 0, getEntry(i, column));
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void setColumnMatrix(int column, RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        int nRows = getRowDimension();
        if (matrix.getRowDimension() == nRows && matrix.getColumnDimension() == 1) {
            for (int i = 0; i < nRows; i++) {
                setEntry(i, column, matrix.getEntry(i, 0));
            }
            return;
        }
        throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealVector getRowVector(int row) throws OutOfRangeException {
        return new ArrayRealVector(getRow(row), false);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void setRowVector(int row, RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        int nCols = getColumnDimension();
        if (vector.getDimension() != nCols) {
            throw new MatrixDimensionMismatchException(1, vector.getDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; i++) {
            setEntry(row, i, vector.getEntry(i));
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealVector getColumnVector(int column) throws OutOfRangeException {
        return new ArrayRealVector(getColumn(column), false);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void setColumnVector(int column, RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        int nRows = getRowDimension();
        if (vector.getDimension() != nRows) {
            throw new MatrixDimensionMismatchException(vector.getDimension(), 1, nRows, 1);
        }
        for (int i = 0; i < nRows; i++) {
            setEntry(i, column, vector.getEntry(i));
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double[] getRow(int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        int nCols = getColumnDimension();
        double[] out = new double[nCols];
        for (int i = 0; i < nCols; i++) {
            out[i] = getEntry(row, i);
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void setRow(int row, double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        int nCols = getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        for (int i = 0; i < nCols; i++) {
            setEntry(row, i, array[i]);
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double[] getColumn(int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        int nRows = getRowDimension();
        double[] out = new double[nRows];
        for (int i = 0; i < nRows; i++) {
            out[i] = getEntry(i, column);
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void setColumn(int column, double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        int nRows = getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        for (int i = 0; i < nRows; i++) {
            setEntry(i, column, array[i]);
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void addToEntry(int row, int column, double increment) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        setEntry(row, column, getEntry(row, column) + increment);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public void multiplyEntry(int row, int column, double factor) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        setEntry(row, column, getEntry(row, column) * factor);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealMatrix transpose() {
        final RealMatrix out = createMatrix(getColumnDimension(), getRowDimension());
        walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            /* class org.apache.commons.math3.linear.AbstractRealMatrix.C02665 */

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor, org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor
            public void visit(int row, int column, double value) {
                out.setEntry(column, row, value);
            }
        });
        return out;
    }

    @Override // org.apache.commons.math3.linear.AnyMatrix
    public boolean isSquare() {
        return getColumnDimension() == getRowDimension();
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double getTrace() throws NonSquareMatrixException {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(nRows, nCols);
        }
        double trace = 0.0d;
        for (int i = 0; i < nRows; i++) {
            trace += getEntry(i, i);
        }
        return trace;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double[] operate(double[] v) throws DimensionMismatchException {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (v.length != nCols) {
            throw new DimensionMismatchException(v.length, nCols);
        }
        double[] out = new double[nRows];
        for (int row = 0; row < nRows; row++) {
            double sum = 0.0d;
            for (int i = 0; i < nCols; i++) {
                sum += getEntry(row, i) * v[i];
            }
            out[row] = sum;
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix, org.apache.commons.math3.linear.RealLinearOperator
    public RealVector operate(RealVector v) throws DimensionMismatchException {
        try {
            return new ArrayRealVector(operate(((ArrayRealVector) v).getDataRef()), false);
        } catch (ClassCastException e) {
            int nRows = getRowDimension();
            int nCols = getColumnDimension();
            if (v.getDimension() != nCols) {
                throw new DimensionMismatchException(v.getDimension(), nCols);
            }
            double[] out = new double[nRows];
            for (int row = 0; row < nRows; row++) {
                double sum = 0.0d;
                for (int i = 0; i < nCols; i++) {
                    sum += getEntry(row, i) * v.getEntry(i);
                }
                out[row] = sum;
            }
            return new ArrayRealVector(out, false);
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double[] preMultiply(double[] v) throws DimensionMismatchException {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (v.length != nRows) {
            throw new DimensionMismatchException(v.length, nRows);
        }
        double[] out = new double[nCols];
        for (int col = 0; col < nCols; col++) {
            double sum = 0.0d;
            for (int i = 0; i < nRows; i++) {
                sum += getEntry(i, col) * v[i];
            }
            out[col] = sum;
        }
        return out;
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public RealVector preMultiply(RealVector v) throws DimensionMismatchException {
        try {
            return new ArrayRealVector(preMultiply(((ArrayRealVector) v).getDataRef()), false);
        } catch (ClassCastException e) {
            int nRows = getRowDimension();
            int nCols = getColumnDimension();
            if (v.getDimension() != nRows) {
                throw new DimensionMismatchException(v.getDimension(), nRows);
            }
            double[] out = new double[nCols];
            for (int col = 0; col < nCols; col++) {
                double sum = 0.0d;
                for (int i = 0; i < nRows; i++) {
                    sum += getEntry(i, col) * v.getEntry(i);
                }
                out[col] = sum;
            }
            return new ArrayRealVector(out, false);
        }
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixChangingVisitor visitor) {
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

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixPreservingVisitor visitor) {
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

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; row++) {
            for (int column = startColumn; column <= endColumn; column++) {
                setEntry(row, column, visitor.visit(row, column, getEntry(row, column)));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInRowOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; row++) {
            for (int column = startColumn; column <= endColumn; column++) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInColumnOrder(RealMatrixChangingVisitor visitor) {
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

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInColumnOrder(RealMatrixPreservingVisitor visitor) {
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

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInColumnOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; column++) {
            for (int row = startRow; row <= endRow; row++) {
                setEntry(row, column, visitor.visit(row, column, getEntry(row, column)));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInColumnOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; column++) {
            for (int row = startRow; row <= endRow; row++) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor) {
        return walkInRowOrder(visitor);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor) {
        return walkInRowOrder(visitor);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    @Override // org.apache.commons.math3.linear.RealMatrix
    public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        String fullClassName = getClass().getName();
        res.append(fullClassName.substring(fullClassName.lastIndexOf(46) + 1));
        res.append(DEFAULT_FORMAT.format(this));
        return res.toString();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof RealMatrix)) {
            return false;
        }
        RealMatrix m = (RealMatrix) object;
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        if (!(m.getColumnDimension() == nCols && m.getRowDimension() == nRows)) {
            return false;
        }
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                if (getEntry(row, col) != m.getEntry(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        int nRows = getRowDimension();
        int nCols = getColumnDimension();
        int ret = ((nRows + 217) * 31) + nCols;
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                ret = (ret * 31) + ((((row + 1) * 11) + ((col + 1) * 17)) * MathUtils.hash(getEntry(row, col)));
            }
        }
        return ret;
    }
}
