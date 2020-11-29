package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.IterationManager;
import org.apache.commons.math3.util.MathUtils;

public class SymmLQ extends PreconditionedIterativeLinearSolver {
    private static final String OPERATOR = "operator";
    private static final String THRESHOLD = "threshold";
    private static final String VECTOR = "vector";
    private static final String VECTOR1 = "vector1";
    private static final String VECTOR2 = "vector2";
    private final boolean check;
    private final double delta;

    /* access modifiers changed from: private */
    public static class State {
        static final double CBRT_MACH_PREC = FastMath.cbrt(MACH_PREC);
        static final double MACH_PREC = FastMath.ulp(1.0d);

        /* renamed from: a */
        private final RealLinearOperator f234a;

        /* renamed from: b */
        private final RealVector f235b;
        private boolean bIsNull;
        private double beta;
        private double beta1;
        private double bstep;
        private double cgnorm;
        private final boolean check;
        private double dbar;
        private final double delta;
        private double gammaZeta;
        private double gbar;
        private double gmax;
        private double gmin;
        private final boolean goodb;
        private boolean hasConverged;
        private double lqnorm;

        /* renamed from: m */
        private final RealLinearOperator f236m;

        /* renamed from: mb */
        private final RealVector f237mb;
        private double minusEpsZeta;
        private double oldb;

        /* renamed from: r1 */
        private RealVector f238r1;

        /* renamed from: r2 */
        private RealVector f239r2;
        private double rnorm;
        private final double shift;
        private double snprod;
        private double tnorm;
        private RealVector wbar;

        /* renamed from: xL */
        private final RealVector f240xL;

        /* renamed from: y */
        private RealVector f241y;
        private double ynorm2;

        State(RealLinearOperator a, RealLinearOperator m, RealVector b, boolean goodb2, double shift2, double delta2, boolean check2) {
            this.f234a = a;
            this.f236m = m;
            this.f235b = b;
            this.f240xL = new ArrayRealVector(b.getDimension());
            this.goodb = goodb2;
            this.shift = shift2;
            this.f237mb = m != null ? m.operate(b) : b;
            this.hasConverged = false;
            this.check = check2;
            this.delta = delta2;
        }

        private static void checkSymmetry(RealLinearOperator l, RealVector x, RealVector y, RealVector z) throws NonSelfAdjointOperatorException {
            double s = y.dotProduct(y);
            double t = x.dotProduct(z);
            double epsa = (MACH_PREC + s) * CBRT_MACH_PREC;
            if (FastMath.abs(s - t) > epsa) {
                NonSelfAdjointOperatorException e = new NonSelfAdjointOperatorException();
                ExceptionContext context = e.getContext();
                context.setValue("operator", l);
                context.setValue(SymmLQ.VECTOR1, x);
                context.setValue(SymmLQ.VECTOR2, y);
                context.setValue(SymmLQ.THRESHOLD, Double.valueOf(epsa));
                throw e;
            }
        }

        private static void throwNPDLOException(RealLinearOperator l, RealVector v) throws NonPositiveDefiniteOperatorException {
            NonPositiveDefiniteOperatorException e = new NonPositiveDefiniteOperatorException();
            ExceptionContext context = e.getContext();
            context.setValue("operator", l);
            context.setValue("vector", v);
            throw e;
        }

        private static void daxpy(double a, RealVector x, RealVector y) {
            int n = x.getDimension();
            for (int i = 0; i < n; i++) {
                y.setEntry(i, (x.getEntry(i) * a) + y.getEntry(i));
            }
        }

        private static void daxpbypz(double a, RealVector x, double b, RealVector y, RealVector z) {
            int n = z.getDimension();
            for (int i = 0; i < n; i++) {
                z.setEntry(i, (x.getEntry(i) * a) + (y.getEntry(i) * b) + z.getEntry(i));
            }
        }

        /* access modifiers changed from: package-private */
        public void refineSolution(RealVector x) {
            double diag;
            int n = this.f240xL.getDimension();
            if (this.lqnorm >= this.cgnorm) {
                double anorm = FastMath.sqrt(this.tnorm);
                if (this.gbar == 0.0d) {
                    diag = anorm * MACH_PREC;
                } else {
                    diag = this.gbar;
                }
                double zbar = this.gammaZeta / diag;
                double step = (this.bstep + (this.snprod * zbar)) / this.beta1;
                if (!this.goodb) {
                    for (int i = 0; i < n; i++) {
                        x.setEntry(i, (zbar * this.wbar.getEntry(i)) + this.f240xL.getEntry(i));
                    }
                    return;
                }
                for (int i2 = 0; i2 < n; i2++) {
                    x.setEntry(i2, (zbar * this.wbar.getEntry(i2)) + this.f240xL.getEntry(i2) + (step * this.f237mb.getEntry(i2)));
                }
            } else if (!this.goodb) {
                x.setSubVector(0, this.f240xL);
            } else {
                double step2 = this.bstep / this.beta1;
                for (int i3 = 0; i3 < n; i3++) {
                    x.setEntry(i3, (step2 * this.f237mb.getEntry(i3)) + this.f240xL.getEntry(i3));
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void init() {
            RealVector operate;
            this.f240xL.set(0.0d);
            this.f238r1 = this.f235b.copy();
            if (this.f236m == null) {
                operate = this.f235b.copy();
            } else {
                operate = this.f236m.operate(this.f238r1);
            }
            this.f241y = operate;
            if (this.f236m != null && this.check) {
                checkSymmetry(this.f236m, this.f238r1, this.f241y, this.f236m.operate(this.f241y));
            }
            this.beta1 = this.f238r1.dotProduct(this.f241y);
            if (this.beta1 < 0.0d) {
                throwNPDLOException(this.f236m, this.f241y);
            }
            if (this.beta1 == 0.0d) {
                this.bIsNull = true;
                return;
            }
            this.bIsNull = false;
            this.beta1 = FastMath.sqrt(this.beta1);
            RealVector v = this.f241y.mapMultiply(1.0d / this.beta1);
            this.f241y = this.f234a.operate(v);
            if (this.check) {
                checkSymmetry(this.f234a, v, this.f241y, this.f234a.operate(this.f241y));
            }
            daxpy(-this.shift, v, this.f241y);
            double alpha = v.dotProduct(this.f241y);
            daxpy((-alpha) / this.beta1, this.f238r1, this.f241y);
            daxpy((-v.dotProduct(this.f241y)) / v.dotProduct(v), v, this.f241y);
            this.f239r2 = this.f241y.copy();
            if (this.f236m != null) {
                this.f241y = this.f236m.operate(this.f239r2);
            }
            this.oldb = this.beta1;
            this.beta = this.f239r2.dotProduct(this.f241y);
            if (this.beta < 0.0d) {
                throwNPDLOException(this.f236m, this.f241y);
            }
            this.beta = FastMath.sqrt(this.beta);
            this.cgnorm = this.beta1;
            this.gbar = alpha;
            this.dbar = this.beta;
            this.gammaZeta = this.beta1;
            this.minusEpsZeta = 0.0d;
            this.bstep = 0.0d;
            this.snprod = 1.0d;
            this.tnorm = (alpha * alpha) + (this.beta * this.beta);
            this.ynorm2 = 0.0d;
            this.gmax = FastMath.abs(alpha) + MACH_PREC;
            this.gmin = this.gmax;
            if (this.goodb) {
                this.wbar = new ArrayRealVector(this.f234a.getRowDimension());
                this.wbar.set(0.0d);
            } else {
                this.wbar = v;
            }
            updateNorms();
        }

        /* access modifiers changed from: package-private */
        public void update() {
            RealVector v = this.f241y.mapMultiply(1.0d / this.beta);
            this.f241y = this.f234a.operate(v);
            daxpbypz(-this.shift, v, (-this.beta) / this.oldb, this.f238r1, this.f241y);
            double alpha = v.dotProduct(this.f241y);
            daxpy((-alpha) / this.beta, this.f239r2, this.f241y);
            this.f238r1 = this.f239r2;
            this.f239r2 = this.f241y;
            if (this.f236m != null) {
                this.f241y = this.f236m.operate(this.f239r2);
            }
            this.oldb = this.beta;
            this.beta = this.f239r2.dotProduct(this.f241y);
            if (this.beta < 0.0d) {
                throwNPDLOException(this.f236m, this.f241y);
            }
            this.beta = FastMath.sqrt(this.beta);
            this.tnorm += (alpha * alpha) + (this.oldb * this.oldb) + (this.beta * this.beta);
            double gamma = FastMath.sqrt((this.gbar * this.gbar) + (this.oldb * this.oldb));
            double c = this.gbar / gamma;
            double s = this.oldb / gamma;
            double deltak = (this.dbar * c) + (s * alpha);
            this.gbar = (this.dbar * s) - (c * alpha);
            double eps = s * this.beta;
            this.dbar = (-c) * this.beta;
            double zeta = this.gammaZeta / gamma;
            double zetaC = zeta * c;
            double zetaS = zeta * s;
            int n = this.f240xL.getDimension();
            for (int i = 0; i < n; i++) {
                double xi = this.f240xL.getEntry(i);
                double vi = v.getEntry(i);
                double wi = this.wbar.getEntry(i);
                this.f240xL.setEntry(i, (wi * zetaC) + xi + (vi * zetaS));
                this.wbar.setEntry(i, (wi * s) - (vi * c));
            }
            this.bstep += this.snprod * c * zeta;
            this.snprod *= s;
            this.gmax = FastMath.max(this.gmax, gamma);
            this.gmin = FastMath.min(this.gmin, gamma);
            this.ynorm2 += zeta * zeta;
            this.gammaZeta = this.minusEpsZeta - (deltak * zeta);
            this.minusEpsZeta = (-eps) * zeta;
            updateNorms();
        }

        private void updateNorms() {
            double diag;
            double acond;
            double anorm = FastMath.sqrt(this.tnorm);
            double ynorm = FastMath.sqrt(this.ynorm2);
            double epsa = anorm * MACH_PREC;
            double epsx = anorm * ynorm * MACH_PREC;
            double epsr = anorm * ynorm * this.delta;
            if (this.gbar == 0.0d) {
                diag = epsa;
            } else {
                diag = this.gbar;
            }
            this.lqnorm = FastMath.sqrt((this.gammaZeta * this.gammaZeta) + (this.minusEpsZeta * this.minusEpsZeta));
            this.cgnorm = (this.beta * (this.snprod * this.beta1)) / FastMath.abs(diag);
            if (this.lqnorm <= this.cgnorm) {
                acond = this.gmax / this.gmin;
            } else {
                acond = this.gmax / FastMath.min(this.gmin, FastMath.abs(diag));
            }
            if (MACH_PREC * acond >= 0.1d) {
                throw new IllConditionedOperatorException(acond);
            } else if (this.beta1 <= epsx) {
                throw new SingularOperatorException();
            } else {
                this.rnorm = FastMath.min(this.cgnorm, this.lqnorm);
                this.hasConverged = this.cgnorm <= epsx || this.cgnorm <= epsr;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean hasConverged() {
            return this.hasConverged;
        }

        /* access modifiers changed from: package-private */
        public boolean bEqualsNullVector() {
            return this.bIsNull;
        }

        /* access modifiers changed from: package-private */
        public boolean betaEqualsZero() {
            return this.beta < MACH_PREC;
        }

        /* access modifiers changed from: package-private */
        public double getNormOfResidual() {
            return this.rnorm;
        }
    }

    public SymmLQ(int maxIterations, double delta2, boolean check2) {
        super(maxIterations);
        this.delta = delta2;
        this.check = check2;
    }

    public SymmLQ(IterationManager manager, double delta2, boolean check2) {
        super(manager);
        this.delta = delta2;
        this.check = check2;
    }

    public final boolean getCheck() {
        return this.check;
    }

    @Override // org.apache.commons.math3.linear.PreconditionedIterativeLinearSolver
    public RealVector solve(RealLinearOperator a, RealLinearOperator m, RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException {
        MathUtils.checkNotNull(a);
        return solveInPlace(a, m, b, new ArrayRealVector(a.getColumnDimension()), false, 0.0d);
    }

    public RealVector solve(RealLinearOperator a, RealLinearOperator m, RealVector b, boolean goodb, double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException {
        MathUtils.checkNotNull(a);
        return solveInPlace(a, m, b, new ArrayRealVector(a.getColumnDimension()), goodb, shift);
    }

    @Override // org.apache.commons.math3.linear.PreconditionedIterativeLinearSolver
    public RealVector solve(RealLinearOperator a, RealLinearOperator m, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(x);
        return solveInPlace(a, m, b, x.copy(), false, 0.0d);
    }

    @Override // org.apache.commons.math3.linear.PreconditionedIterativeLinearSolver, org.apache.commons.math3.linear.IterativeLinearSolver
    public RealVector solve(RealLinearOperator a, RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(a);
        RealVector x = new ArrayRealVector(a.getColumnDimension());
        x.set(0.0d);
        return solveInPlace(a, null, b, x, false, 0.0d);
    }

    public RealVector solve(RealLinearOperator a, RealVector b, boolean goodb, double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(a);
        return solveInPlace(a, null, b, new ArrayRealVector(a.getColumnDimension()), goodb, shift);
    }

    @Override // org.apache.commons.math3.linear.PreconditionedIterativeLinearSolver, org.apache.commons.math3.linear.IterativeLinearSolver
    public RealVector solve(RealLinearOperator a, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(x);
        return solveInPlace(a, null, b, x.copy(), false, 0.0d);
    }

    @Override // org.apache.commons.math3.linear.PreconditionedIterativeLinearSolver
    public RealVector solveInPlace(RealLinearOperator a, RealLinearOperator m, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        return solveInPlace(a, m, b, x, false, 0.0d);
    }

    public RealVector solveInPlace(RealLinearOperator a, RealLinearOperator m, RealVector b, RealVector x, boolean goodb, double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        checkParameters(a, m, b, x);
        IterationManager manager = getIterationManager();
        manager.resetIterationCount();
        manager.incrementIterationCount();
        State state = new State(a, m, b, goodb, shift, this.delta, this.check);
        state.init();
        state.refineSolution(x);
        IterativeLinearSolverEvent event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
        if (state.bEqualsNullVector()) {
            manager.fireTerminationEvent(event);
        } else {
            boolean earlyStop = state.betaEqualsZero() || state.hasConverged();
            manager.fireInitializationEvent(event);
            if (!earlyStop) {
                do {
                    manager.incrementIterationCount();
                    manager.fireIterationStartedEvent(new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual()));
                    state.update();
                    state.refineSolution(x);
                    manager.fireIterationPerformedEvent(new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual()));
                } while (!state.hasConverged());
            }
            manager.fireTerminationEvent(new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual()));
        }
        return x;
    }

    @Override // org.apache.commons.math3.linear.PreconditionedIterativeLinearSolver, org.apache.commons.math3.linear.IterativeLinearSolver
    public RealVector solveInPlace(RealLinearOperator a, RealVector b, RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        return solveInPlace(a, null, b, x, false, 0.0d);
    }
}
