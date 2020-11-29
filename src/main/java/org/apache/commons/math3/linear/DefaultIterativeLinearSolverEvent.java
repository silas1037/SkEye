package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;

public class DefaultIterativeLinearSolverEvent extends IterativeLinearSolverEvent {
    private static final long serialVersionUID = 20120129;

    /* renamed from: b */
    private final RealVector f216b;

    /* renamed from: r */
    private final RealVector f217r;
    private final double rnorm;

    /* renamed from: x */
    private final RealVector f218x;

    public DefaultIterativeLinearSolverEvent(Object source, int iterations, RealVector x, RealVector b, RealVector r, double rnorm2) {
        super(source, iterations);
        this.f218x = x;
        this.f216b = b;
        this.f217r = r;
        this.rnorm = rnorm2;
    }

    public DefaultIterativeLinearSolverEvent(Object source, int iterations, RealVector x, RealVector b, double rnorm2) {
        super(source, iterations);
        this.f218x = x;
        this.f216b = b;
        this.f217r = null;
        this.rnorm = rnorm2;
    }

    @Override // org.apache.commons.math3.linear.IterativeLinearSolverEvent
    public double getNormOfResidual() {
        return this.rnorm;
    }

    @Override // org.apache.commons.math3.linear.IterativeLinearSolverEvent
    public RealVector getResidual() {
        if (this.f217r != null) {
            return this.f217r;
        }
        throw new MathUnsupportedOperationException();
    }

    @Override // org.apache.commons.math3.linear.IterativeLinearSolverEvent
    public RealVector getRightHandSideVector() {
        return this.f216b;
    }

    @Override // org.apache.commons.math3.linear.IterativeLinearSolverEvent
    public RealVector getSolution() {
        return this.f218x;
    }

    @Override // org.apache.commons.math3.linear.IterativeLinearSolverEvent
    public boolean providesResidual() {
        return this.f217r != null;
    }
}
