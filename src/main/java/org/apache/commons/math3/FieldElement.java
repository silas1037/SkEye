package org.apache.commons.math3;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;

public interface FieldElement<T> {
    T add(T t) throws NullArgumentException;

    T divide(T t) throws NullArgumentException, MathArithmeticException;

    Field<T> getField();

    T multiply(int i);

    T multiply(T t) throws NullArgumentException;

    T negate();

    T reciprocal() throws MathArithmeticException;

    T subtract(T t) throws NullArgumentException;
}
