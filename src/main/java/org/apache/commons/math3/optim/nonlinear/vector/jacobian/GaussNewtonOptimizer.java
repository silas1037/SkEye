package org.apache.commons.math3.optim.nonlinear.vector.jacobian;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;

@Deprecated
public class GaussNewtonOptimizer extends AbstractLeastSquaresOptimizer {
    private final boolean useLU;

    public GaussNewtonOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
        this(true, checker);
    }

    public GaussNewtonOptimizer(boolean useLU2, ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
        this.useLU = useLU2;
    }

    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public PointVectorValuePair doOptimize() {
        checkParameters();
        ConvergenceChecker<PointVectorValuePair> checker = getConvergenceChecker();
        if (checker == null) {
            throw new NullArgumentException();
        }
        int nR = getTarget().length;
        RealMatrix weightMatrix = getWeight();
        double[] residualsWeights = new double[nR];
        for (int i = 0; i < nR; i++) {
            residualsWeights[i] = weightMatrix.getEntry(i, i);
        }
        double[] currentPoint = getStartPoint();
        int nC = currentPoint.length;
        PointVectorValuePair current = null;
        boolean converged = false;
        while (!converged) {
            incrementIterationCount();
            double[] currentObjective = computeObjectiveValue(currentPoint);
            double[] currentResiduals = computeResiduals(currentObjective);
            RealMatrix weightedJacobian = computeWeightedJacobian(currentPoint);
            current = new PointVectorValuePair(currentPoint, currentObjective);
            double[] b = new double[nC];
            double[][] a = (double[][]) Array.newInstance(Double.TYPE, nC, nC);
            for (int i2 = 0; i2 < nR; i2++) {
                double[] grad = weightedJacobian.getRow(i2);
                double weight = residualsWeights[i2];
                double wr = weight * currentResiduals[i2];
                for (int j = 0; j < nC; j++) {
                    b[j] = b[j] + (grad[j] * wr);
                }
                for (int k = 0; k < nC; k++) {
                    double[] ak = a[k];
                    double wgk = weight * grad[k];
                    for (int l = 0; l < nC; l++) {
                        ak[l] = ak[l] + (grad[l] * wgk);
                    }
                }
            }
            if (current == null || !(converged = checker.converged(getIterations(), current, current))) {
                try {
                    RealMatrix mA = new BlockRealMatrix(a);
                    double[] dX = (this.useLU ? new LUDecomposition(mA).getSolver() : new QRDecomposition(mA).getSolver()).solve(new ArrayRealVector(b, false)).toArray();
                    for (int i3 = 0; i3 < nC; i3++) {
                        currentPoint[i3] = currentPoint[i3] + dX[i3];
                    }
                } catch (SingularMatrixException e) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[0]);
                }
            } else {
                setCost(computeCost(currentResiduals));
                return current;
            }
        }
        throw new MathInternalError();
    }

    private void checkParameters() {
        if (getLowerBound() != null || getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT, new Object[0]);
        }
    }
}
