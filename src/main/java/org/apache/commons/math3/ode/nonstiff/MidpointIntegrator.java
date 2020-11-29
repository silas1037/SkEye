package org.apache.commons.math3.ode.nonstiff;

public class MidpointIntegrator extends RungeKuttaIntegrator {
    private static final double[][] STATIC_A = {new double[]{0.5d}};
    private static final double[] STATIC_B = {0.0d, 1.0d};
    private static final double[] STATIC_C = {0.5d};

    public MidpointIntegrator(double step) {
        super("midpoint", STATIC_C, STATIC_A, STATIC_B, new MidpointStepInterpolator(), step);
    }
}
