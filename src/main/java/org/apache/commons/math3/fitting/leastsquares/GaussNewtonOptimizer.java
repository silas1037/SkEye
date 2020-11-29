package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.Pair;

public class GaussNewtonOptimizer implements LeastSquaresOptimizer {
    private static final double SINGULARITY_THRESHOLD = 1.0E-11d;
    private final Decomposition decomposition;

    public enum Decomposition {
        LU {
            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer.Decomposition
            public RealVector solve(RealMatrix jacobian, RealVector residuals) {
                try {
                    Pair<RealMatrix, RealVector> normalEquation = GaussNewtonOptimizer.computeNormalMatrix(jacobian, residuals);
                    return new LUDecomposition(normalEquation.getFirst(), GaussNewtonOptimizer.SINGULARITY_THRESHOLD).getSolver().solve(normalEquation.getSecond());
                } catch (SingularMatrixException e) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, e);
                }
            }
        },
        QR {
            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer.Decomposition
            public RealVector solve(RealMatrix jacobian, RealVector residuals) {
                try {
                    return new QRDecomposition(jacobian, GaussNewtonOptimizer.SINGULARITY_THRESHOLD).getSolver().solve(residuals);
                } catch (SingularMatrixException e) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, e);
                }
            }
        },
        CHOLESKY {
            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer.Decomposition
            public RealVector solve(RealMatrix jacobian, RealVector residuals) {
                try {
                    Pair<RealMatrix, RealVector> normalEquation = GaussNewtonOptimizer.computeNormalMatrix(jacobian, residuals);
                    return new CholeskyDecomposition(normalEquation.getFirst(), GaussNewtonOptimizer.SINGULARITY_THRESHOLD, GaussNewtonOptimizer.SINGULARITY_THRESHOLD).getSolver().solve(normalEquation.getSecond());
                } catch (NonPositiveDefiniteMatrixException e) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, e);
                }
            }
        },
        SVD {
            /* access modifiers changed from: protected */
            @Override // org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizer.Decomposition
            public RealVector solve(RealMatrix jacobian, RealVector residuals) {
                return new SingularValueDecomposition(jacobian).getSolver().solve(residuals);
            }
        };

        /* access modifiers changed from: protected */
        public abstract RealVector solve(RealMatrix realMatrix, RealVector realVector);
    }

    public GaussNewtonOptimizer() {
        this(Decomposition.QR);
    }

    public GaussNewtonOptimizer(Decomposition decomposition2) {
        this.decomposition = decomposition2;
    }

    public Decomposition getDecomposition() {
        return this.decomposition;
    }

    public GaussNewtonOptimizer withDecomposition(Decomposition newDecomposition) {
        return new GaussNewtonOptimizer(newDecomposition);
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer
    public LeastSquaresOptimizer.Optimum optimize(LeastSquaresProblem lsp) {
        Incrementor evaluationCounter = lsp.getEvaluationCounter();
        Incrementor iterationCounter = lsp.getIterationCounter();
        ConvergenceChecker<LeastSquaresProblem.Evaluation> checker = lsp.getConvergenceChecker();
        if (checker == null) {
            throw new NullArgumentException();
        }
        RealVector currentPoint = lsp.getStart();
        LeastSquaresProblem.Evaluation current = null;
        while (true) {
            iterationCounter.incrementCount();
            evaluationCounter.incrementCount();
            current = lsp.evaluate(currentPoint);
            RealVector currentResiduals = current.getResiduals();
            RealMatrix weightedJacobian = current.getJacobian();
            RealVector currentPoint2 = current.getPoint();
            if (current != null && checker.converged(iterationCounter.getCount(), current, current)) {
                return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
            }
            currentPoint = currentPoint2.add(this.decomposition.solve(weightedJacobian, currentResiduals));
        }
    }

    public String toString() {
        return "GaussNewtonOptimizer{decomposition=" + this.decomposition + '}';
    }

    /* access modifiers changed from: private */
    public static Pair<RealMatrix, RealVector> computeNormalMatrix(RealMatrix jacobian, RealVector residuals) {
        int nR = jacobian.getRowDimension();
        int nC = jacobian.getColumnDimension();
        RealMatrix normal = MatrixUtils.createRealMatrix(nC, nC);
        RealVector jTr = new ArrayRealVector(nC);
        for (int i = 0; i < nR; i++) {
            for (int j = 0; j < nC; j++) {
                jTr.setEntry(j, jTr.getEntry(j) + (residuals.getEntry(i) * jacobian.getEntry(i, j)));
            }
            for (int k = 0; k < nC; k++) {
                for (int l = k; l < nC; l++) {
                    normal.setEntry(k, l, normal.getEntry(k, l) + (jacobian.getEntry(i, k) * jacobian.getEntry(i, l)));
                }
            }
        }
        for (int i2 = 0; i2 < nC; i2++) {
            for (int j2 = 0; j2 < i2; j2++) {
                normal.setEntry(i2, j2, normal.getEntry(j2, i2));
            }
        }
        return new Pair<>(normal, jTr);
    }
}
