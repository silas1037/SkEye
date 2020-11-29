package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.FastMath;

public abstract class AbstractEvaluation implements LeastSquaresProblem.Evaluation {
    private final int observationSize;

    AbstractEvaluation(int observationSize2) {
        this.observationSize = observationSize2;
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation
    public RealMatrix getCovariances(double threshold) {
        RealMatrix j = getJacobian();
        return new QRDecomposition(j.transpose().multiply(j), threshold).getSolver().getInverse();
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation
    public RealVector getSigma(double covarianceSingularityThreshold) {
        RealMatrix cov = getCovariances(covarianceSingularityThreshold);
        int nC = cov.getColumnDimension();
        RealVector sig = new ArrayRealVector(nC);
        for (int i = 0; i < nC; i++) {
            sig.setEntry(i, FastMath.sqrt(cov.getEntry(i, i)));
        }
        return sig;
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation
    public double getRMS() {
        double cost = getCost();
        return FastMath.sqrt((cost * cost) / ((double) this.observationSize));
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem.Evaluation
    public double getCost() {
        ArrayRealVector r = new ArrayRealVector(getResiduals());
        return FastMath.sqrt(r.dotProduct(r));
    }
}
