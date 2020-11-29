package org.apache.commons.math3;

public interface Field<T> {
    T getOne();

    Class<? extends FieldElement<T>> getRuntimeClass();

    T getZero();
}
