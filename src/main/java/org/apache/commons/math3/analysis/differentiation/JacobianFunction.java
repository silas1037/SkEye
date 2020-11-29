package org.apache.commons.math3.analysis.differentiation;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;

public class JacobianFunction implements MultivariateMatrixFunction {

    /* renamed from: f */
    private final MultivariateDifferentiableVectorFunction f111f;

    public JacobianFunction(MultivariateDifferentiableVectorFunction f) {
        this.f111f = f;
    }

    @Override // org.apache.commons.math3.analysis.MultivariateMatrixFunction
    public double[][] value(double[] point) {
        DerivativeStructure[] dsX = new DerivativeStructure[point.length];
        for (int i = 0; i < point.length; i++) {
            dsX[i] = new DerivativeStructure(point.length, 1, i, point[i]);
        }
        DerivativeStructure[] dsY = this.f111f.value(dsX);
        double[][] y = (double[][]) Array.newInstance(Double.TYPE, dsY.length, point.length);
        int[] orders = new int[point.length];
        for (int i2 = 0; i2 < dsY.length; i2++) {
            for (int j = 0; j < point.length; j++) {
                orders[j] = 1;
                y[i2][j] = dsY[i2].getPartialDerivative(orders);
                orders[j] = 0;
            }
        }
        return y;
    }
}
