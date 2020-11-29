package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.util.IterationManager;

public class ConjugateGradient extends PreconditionedIterativeLinearSolver {
    public static final String OPERATOR = "operator";
    public static final String VECTOR = "vector";
    private boolean check;
    private final double delta;

    public ConjugateGradient(int maxIterations, double delta2, boolean check2) {
        super(maxIterations);
        this.delta = delta2;
        this.check = check2;
    }

    public ConjugateGradient(IterationManager manager, double delta2, boolean check2) throws NullArgumentException {
        super(manager);
        this.delta = delta2;
        this.check = check2;
    }

    public final boolean getCheck() {
        return this.check;
    }

    @Override // org.apache.commons.math3.linear.PreconditionedIterativeLinearSolver
    public RealVector solveInPlace(RealLinearOperator a, RealLinearOperator m, RealVector b, RealVector x0) throws NullArgumentException, NonPositiveDefiniteOperatorException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        RealVector z;
        IterativeLinearSolverEvent evt;
        checkParameters(a, m, b, x0);
        IterationManager manager = getIterationManager();
        manager.resetIterationCount();
        double rmax = this.delta * b.getNorm();
        RealVector bro = RealVector.unmodifiableRealVector(b);
        manager.incrementIterationCount();
        RealVector xro = RealVector.unmodifiableRealVector(x0);
        RealVector p = x0.copy();
        RealVector r = b.combine(1.0d, -1.0d, a.operate(p));
        RealVector rro = RealVector.unmodifiableRealVector(r);
        double rnorm = r.getNorm();
        if (m == null) {
            z = r;
        } else {
            z = null;
        }
        IterativeLinearSolverEvent evt2 = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), xro, bro, rro, rnorm);
        manager.fireInitializationEvent(evt2);
        if (rnorm <= rmax) {
            manager.fireTerminationEvent(evt2);
        } else {
            double rhoPrev = 0.0d;
            do {
                manager.incrementIterationCount();
                manager.fireIterationStartedEvent(new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), xro, bro, rro, rnorm));
                if (m != null) {
                    z = m.operate(r);
                }
                double rhoNext = r.dotProduct(z);
                if (!this.check || rhoNext > 0.0d) {
                    if (manager.getIterations() == 2) {
                        p.setSubVector(0, z);
                    } else {
                        p.combineToSelf(rhoNext / rhoPrev, 1.0d, z);
                    }
                    RealVector q = a.operate(p);
                    double pq = p.dotProduct(q);
                    if (!this.check || pq > 0.0d) {
                        double alpha = rhoNext / pq;
                        x0.combineToSelf(1.0d, alpha, p);
                        r.combineToSelf(1.0d, -alpha, q);
                        rhoPrev = rhoNext;
                        rnorm = r.getNorm();
                        evt = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), xro, bro, rro, rnorm);
                        manager.fireIterationPerformedEvent(evt);
                    } else {
                        NonPositiveDefiniteOperatorException e = new NonPositiveDefiniteOperatorException();
                        ExceptionContext context = e.getContext();
                        context.setValue(OPERATOR, a);
                        context.setValue(VECTOR, p);
                        throw e;
                    }
                } else {
                    NonPositiveDefiniteOperatorException e2 = new NonPositiveDefiniteOperatorException();
                    ExceptionContext context2 = e2.getContext();
                    context2.setValue(OPERATOR, m);
                    context2.setValue(VECTOR, r);
                    throw e2;
                }
            } while (rnorm > rmax);
            manager.fireTerminationEvent(evt);
        }
        return x0;
    }
}
