package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public interface FieldMatrixPreservingVisitor<T extends FieldElement<?>> {
    T end();

    void start(int i, int i2, int i3, int i4, int i5, int i6);

    void visit(int i, int i2, T t);
}
