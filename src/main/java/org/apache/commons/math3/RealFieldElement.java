package org.apache.commons.math3;

import org.apache.commons.math3.exception.DimensionMismatchException;

public interface RealFieldElement<T> extends FieldElement<T> {
    T abs();

    T acos();

    T acosh();

    T add(double d);

    T asin();

    T asinh();

    T atan();

    T atan2(T t) throws DimensionMismatchException;

    T atanh();

    T cbrt();

    T ceil();

    T copySign(double d);

    T copySign(T t);

    T cos();

    T cosh();

    T divide(double d);

    T exp();

    T expm1();

    T floor();

    double getReal();

    T hypot(T t) throws DimensionMismatchException;

    T linearCombination(double d, T t, double d2, T t2);

    T linearCombination(double d, T t, double d2, T t2, double d3, T t3);

    T linearCombination(double d, T t, double d2, T t2, double d3, T t3, double d4, T t4);

    T linearCombination(T t, T t2, T t3, T t4);

    T linearCombination(T t, T t2, T t3, T t4, T t5, T t6);

    T linearCombination(T t, T t2, T t3, T t4, T t5, T t6, T t7, T t8);

    T linearCombination(double[] dArr, T[] tArr) throws DimensionMismatchException;

    T linearCombination(T[] tArr, T[] tArr2) throws DimensionMismatchException;

    T log();

    T log1p();

    T multiply(double d);

    T pow(double d);

    T pow(int i);

    T pow(T t) throws DimensionMismatchException;

    @Override // org.apache.commons.math3.FieldElement
    T reciprocal();

    T remainder(double d);

    T remainder(T t) throws DimensionMismatchException;

    T rint();

    T rootN(int i);

    long round();

    T scalb(int i);

    T signum();

    T sin();

    T sinh();

    T sqrt();

    T subtract(double d);

    T tan();

    T tanh();
}
