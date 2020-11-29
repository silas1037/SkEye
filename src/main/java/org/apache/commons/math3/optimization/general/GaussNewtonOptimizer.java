package org.apache.commons.math3.optimization.general;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.PointVectorValuePair;
import org.apache.commons.math3.optimization.SimpleVectorValueChecker;

@Deprecated
public class GaussNewtonOptimizer extends AbstractLeastSquaresOptimizer {
    private final boolean useLU;

    @Deprecated
    public GaussNewtonOptimizer() {
        this(true);
    }

    public GaussNewtonOptimizer(ConvergenceChecker<PointVectorValuePair> checker) {
        this(true, checker);
    }

    @Deprecated
    public GaussNewtonOptimizer(boolean useLU2) {
        this(useLU2, new SimpleVectorValueChecker());
    }

    public GaussNewtonOptimizer(boolean useLU2, ConvergenceChecker<PointVectorValuePair> checker) {
        super(checker);
        this.useLU = useLU2;
    }

    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateVectorOptimizer
    public PointVectorValuePair doOptimize() {
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
        int iter = 0;
        boolean converged = false;
        while (!converged) {
            iter++;
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
            try {
                RealMatrix mA = new BlockRealMatrix(a);
                double[] dX = (this.useLU ? new LUDecomposition(mA).getSolver() : new QRDecomposition(mA).getSolver()).solve(new ArrayRealVector(b, false)).toArray();
                for (int i3 = 0; i3 < nC; i3++) {
                    currentPoint[i3] = currentPoint[i3] + dX[i3];
                }
                if (current != null && (converged = checker.converged(iter, current, current))) {
                    this.cost = computeCost(currentResiduals);
                    this.point = current.getPoint();
                    return current;
                }
            } catch (SingularMatrixException e) {
                throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[0]);
            }
        }
        throw new MathInternalError();
    }
}
