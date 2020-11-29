package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface FieldVector<T extends FieldElement<T>> {
    FieldVector<T> add(FieldVector<T> fieldVector) throws DimensionMismatchException;

    FieldVector<T> append(T t);

    FieldVector<T> append(FieldVector<T> fieldVector);

    FieldVector<T> copy();

    T dotProduct(FieldVector<T> fieldVector) throws DimensionMismatchException;

    FieldVector<T> ebeDivide(FieldVector<T> fieldVector) throws DimensionMismatchException, MathArithmeticException;

    FieldVector<T> ebeMultiply(FieldVector<T> fieldVector) throws DimensionMismatchException;

    @Deprecated
    T[] getData();

    int getDimension();

    T getEntry(int i) throws OutOfRangeException;

    Field<T> getField();

    FieldVector<T> getSubVector(int i, int i2) throws OutOfRangeException, NotPositiveException;

    FieldVector<T> mapAdd(T t) throws NullArgumentException;

    FieldVector<T> mapAddToSelf(T t) throws NullArgumentException;

    FieldVector<T> mapDivide(T t) throws NullArgumentException, MathArithmeticException;

    FieldVector<T> mapDivideToSelf(T t) throws NullArgumentException, MathArithmeticException;

    FieldVector<T> mapInv() throws MathArithmeticException;

    FieldVector<T> mapInvToSelf() throws MathArithmeticException;

    FieldVector<T> mapMultiply(T t) throws NullArgumentException;

    FieldVector<T> mapMultiplyToSelf(T t) throws NullArgumentException;

    FieldVector<T> mapSubtract(T t) throws NullArgumentException;

    FieldVector<T> mapSubtractToSelf(T t) throws NullArgumentException;

    FieldMatrix<T> outerProduct(FieldVector<T> fieldVector);

    FieldVector<T> projection(FieldVector<T> fieldVector) throws DimensionMismatchException, MathArithmeticException;

    void set(T t);

    void setEntry(int i, T t) throws OutOfRangeException;

    void setSubVector(int i, FieldVector<T> fieldVector) throws OutOfRangeException;

    FieldVector<T> subtract(FieldVector<T> fieldVector) throws DimensionMismatchException;

    T[] toArray();
}
