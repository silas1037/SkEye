package org.apache.commons.math3.ode;

public class FirstOrderConverter implements FirstOrderDifferentialEquations {
    private final int dimension;
    private final SecondOrderDifferentialEquations equations;

    /* renamed from: z */
    private final double[] f250z = new double[this.dimension];
    private final double[] zDDot = new double[this.dimension];
    private final double[] zDot = new double[this.dimension];

    public FirstOrderConverter(SecondOrderDifferentialEquations equations2) {
        this.equations = equations2;
        this.dimension = equations2.getDimension();
    }

    @Override // org.apache.commons.math3.ode.FirstOrderDifferentialEquations
    public int getDimension() {
        return this.dimension * 2;
    }

    @Override // org.apache.commons.math3.ode.FirstOrderDifferentialEquations
    public void computeDerivatives(double t, double[] y, double[] yDot) {
        System.arraycopy(y, 0, this.f250z, 0, this.dimension);
        System.arraycopy(y, this.dimension, this.zDot, 0, this.dimension);
        this.equations.computeSecondDerivatives(t, this.f250z, this.zDot, this.zDDot);
        System.arraycopy(this.zDot, 0, yDot, 0, this.dimension);
        System.arraycopy(this.zDDot, 0, yDot, this.dimension, this.dimension);
    }
}
