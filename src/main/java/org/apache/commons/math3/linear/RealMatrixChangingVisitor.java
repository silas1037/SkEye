package org.apache.commons.math3.linear;

public interface RealMatrixChangingVisitor {
    double end();

    void start(int i, int i2, int i3, int i4, int i5, int i6);

    double visit(int i, int i2, double d);
}
