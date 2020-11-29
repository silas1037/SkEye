package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public interface FieldDecompositionSolver<T extends FieldElement<T>> {
    FieldMatrix<T> getInverse();

    boolean isNonSingular();

    FieldMatrix<T> solve(FieldMatrix<T> fieldMatrix);

    FieldVector<T> solve(FieldVector<T> fieldVector);
}
