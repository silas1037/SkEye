package org.apache.commons.math3.linear;

public interface RealVectorPreservingVisitor {
    double end();

    void start(int i, int i2, int i3);

    void visit(int i, double d);
}
