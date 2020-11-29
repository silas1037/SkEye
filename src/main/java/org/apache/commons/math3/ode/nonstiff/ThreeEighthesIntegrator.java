package org.apache.commons.math3.ode.nonstiff;

public class ThreeEighthesIntegrator extends RungeKuttaIntegrator {
    private static final double[][] STATIC_A = {new double[]{0.3333333333333333d}, new double[]{-0.3333333333333333d, 1.0d}, new double[]{1.0d, -1.0d, 1.0d}};
    private static final double[] STATIC_B = {0.125d, 0.375d, 0.375d, 0.125d};
    private static final double[] STATIC_C = {0.3333333333333333d, 0.6666666666666666d, 1.0d};

    public ThreeEighthesIntegrator(double step) {
        super("3/8", STATIC_C, STATIC_A, STATIC_B, new ThreeEighthesStepInterpolator(), step);
    }
}
