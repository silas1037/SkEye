package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.FastMath;

public class LutherIntegrator extends RungeKuttaIntegrator {

    /* renamed from: Q */
    private static final double f302Q = FastMath.sqrt(21.0d);
    private static final double[][] STATIC_A = {new double[]{1.0d}, new double[]{0.375d, 0.125d}, new double[]{0.2962962962962963d, 0.07407407407407407d, 0.2962962962962963d}, new double[]{(-21.0d + (9.0d * f302Q)) / 392.0d, (-56.0d + (8.0d * f302Q)) / 392.0d, (336.0d - (48.0d * f302Q)) / 392.0d, (-63.0d + (3.0d * f302Q)) / 392.0d}, new double[]{(-1155.0d - (255.0d * f302Q)) / 1960.0d, (-280.0d - (40.0d * f302Q)) / 1960.0d, (0.0d - (320.0d * f302Q)) / 1960.0d, (63.0d + (363.0d * f302Q)) / 1960.0d, (2352.0d + (392.0d * f302Q)) / 1960.0d}, new double[]{(330.0d + (105.0d * f302Q)) / 180.0d, (120.0d + (0.0d * f302Q)) / 180.0d, (-200.0d + (280.0d * f302Q)) / 180.0d, (126.0d - (189.0d * f302Q)) / 180.0d, (-686.0d - (126.0d * f302Q)) / 180.0d, (490.0d - (70.0d * f302Q)) / 180.0d}};
    private static final double[] STATIC_B = {0.05d, 0.0d, 0.35555555555555557d, 0.0d, 0.2722222222222222d, 0.2722222222222222d, 0.05d};
    private static final double[] STATIC_C = {1.0d, 0.5d, 0.6666666666666666d, (7.0d - f302Q) / 14.0d, (7.0d + f302Q) / 14.0d, 1.0d};

    public LutherIntegrator(double step) {
        super("Luther", STATIC_C, STATIC_A, STATIC_B, new LutherStepInterpolator(), step);
    }
}
