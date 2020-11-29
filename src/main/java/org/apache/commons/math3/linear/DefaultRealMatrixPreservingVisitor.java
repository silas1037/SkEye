package org.apache.commons.math3.linear;

public class DefaultRealMatrixPreservingVisitor implements RealMatrixPreservingVisitor {
    @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
    public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
    }

    @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
    public void visit(int row, int column, double value) {
    }

    @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
    public double end() {
        return 0.0d;
    }
}
