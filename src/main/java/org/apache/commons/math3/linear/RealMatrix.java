package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface RealMatrix extends AnyMatrix {
    RealMatrix add(RealMatrix realMatrix) throws MatrixDimensionMismatchException;

    void addToEntry(int i, int i2, double d) throws OutOfRangeException;

    RealMatrix copy();

    void copySubMatrix(int i, int i2, int i3, int i4, double[][] dArr) throws OutOfRangeException, NumberIsTooSmallException, MatrixDimensionMismatchException;

    void copySubMatrix(int[] iArr, int[] iArr2, double[][] dArr) throws OutOfRangeException, NullArgumentException, NoDataException, MatrixDimensionMismatchException;

    RealMatrix createMatrix(int i, int i2) throws NotStrictlyPositiveException;

    double[] getColumn(int i) throws OutOfRangeException;

    RealMatrix getColumnMatrix(int i) throws OutOfRangeException;

    RealVector getColumnVector(int i) throws OutOfRangeException;

    double[][] getData();

    double getEntry(int i, int i2) throws OutOfRangeException;

    double getFrobeniusNorm();

    double getNorm();

    double[] getRow(int i) throws OutOfRangeException;

    RealMatrix getRowMatrix(int i) throws OutOfRangeException;

    RealVector getRowVector(int i) throws OutOfRangeException;

    RealMatrix getSubMatrix(int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;

    RealMatrix getSubMatrix(int[] iArr, int[] iArr2) throws NullArgumentException, NoDataException, OutOfRangeException;

    double getTrace() throws NonSquareMatrixException;

    RealMatrix multiply(RealMatrix realMatrix) throws DimensionMismatchException;

    void multiplyEntry(int i, int i2, double d) throws OutOfRangeException;

    RealVector operate(RealVector realVector) throws DimensionMismatchException;

    double[] operate(double[] dArr) throws DimensionMismatchException;

    RealMatrix power(int i) throws NotPositiveException, NonSquareMatrixException;

    RealMatrix preMultiply(RealMatrix realMatrix) throws DimensionMismatchException;

    RealVector preMultiply(RealVector realVector) throws DimensionMismatchException;

    double[] preMultiply(double[] dArr) throws DimensionMismatchException;

    RealMatrix scalarAdd(double d);

    RealMatrix scalarMultiply(double d);

    void setColumn(int i, double[] dArr) throws OutOfRangeException, MatrixDimensionMismatchException;

    void setColumnMatrix(int i, RealMatrix realMatrix) throws OutOfRangeException, MatrixDimensionMismatchException;

    void setColumnVector(int i, RealVector realVector) throws OutOfRangeException, MatrixDimensionMismatchException;

    void setEntry(int i, int i2, double d) throws OutOfRangeException;

    void setRow(int i, double[] dArr) throws OutOfRangeException, MatrixDimensionMismatchException;

    void setRowMatrix(int i, RealMatrix realMatrix) throws OutOfRangeException, MatrixDimensionMismatchException;

    void setRowVector(int i, RealVector realVector) throws OutOfRangeException, MatrixDimensionMismatchException;

    void setSubMatrix(double[][] dArr, int i, int i2) throws NoDataException, OutOfRangeException, DimensionMismatchException, NullArgumentException;

    RealMatrix subtract(RealMatrix realMatrix) throws MatrixDimensionMismatchException;

    RealMatrix transpose();

    double walkInColumnOrder(RealMatrixChangingVisitor realMatrixChangingVisitor);

    double walkInColumnOrder(RealMatrixChangingVisitor realMatrixChangingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;

    double walkInColumnOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor);

    double walkInColumnOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;

    double walkInOptimizedOrder(RealMatrixChangingVisitor realMatrixChangingVisitor);

    double walkInOptimizedOrder(RealMatrixChangingVisitor realMatrixChangingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;

    double walkInOptimizedOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor);

    double walkInOptimizedOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;

    double walkInRowOrder(RealMatrixChangingVisitor realMatrixChangingVisitor);

    double walkInRowOrder(RealMatrixChangingVisitor realMatrixChangingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;

    double walkInRowOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor);

    double walkInRowOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor, int i, int i2, int i3, int i4) throws OutOfRangeException, NumberIsTooSmallException;
}
