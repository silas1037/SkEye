package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public interface FieldVectorChangingVisitor<T extends FieldElement<?>> {
    T end();

    void start(int i, int i2, int i3);

    T visit(int i, T t);
}
