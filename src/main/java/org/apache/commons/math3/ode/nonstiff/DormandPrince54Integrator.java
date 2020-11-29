package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.FastMath;

public class DormandPrince54Integrator extends EmbeddedRungeKuttaIntegrator {

    /* renamed from: E1 */
    private static final double f275E1 = 0.0012326388888888888d;

    /* renamed from: E3 */
    private static final double f276E3 = -0.0042527702905061394d;

    /* renamed from: E4 */
    private static final double f277E4 = 0.03697916666666667d;

    /* renamed from: E5 */
    private static final double f278E5 = -0.05086379716981132d;

    /* renamed from: E6 */
    private static final double f279E6 = 0.0419047619047619d;

    /* renamed from: E7 */
    private static final double f280E7 = -0.025d;
    private static final String METHOD_NAME = "Dormand-Prince 5(4)";
    private static final double[][] STATIC_A = {new double[]{0.2d}, new double[]{0.075d, 0.225d}, new double[]{0.9777777777777777d, -3.7333333333333334d, 3.5555555555555554d}, new double[]{2.9525986892242035d, -11.595793324188385d, 9.822892851699436d, -0.2908093278463649d}, new double[]{2.8462752525252526d, -10.757575757575758d, 8.906422717743473d, 0.2784090909090909d, -0.2735313036020583d}, new double[]{0.09114583333333333d, 0.0d, 0.44923629829290207d, 0.6510416666666666d, -0.322376179245283d, 0.13095238095238096d}};
    private static final double[] STATIC_B = {0.09114583333333333d, 0.0d, 0.44923629829290207d, 0.6510416666666666d, -0.322376179245283d, 0.13095238095238096d, 0.0d};
    private static final double[] STATIC_C = {0.2d, 0.3d, 0.8d, 0.8888888888888888d, 1.0d, 1.0d};

    public DormandPrince54Integrator(double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
        super(METHOD_NAME, true, STATIC_C, STATIC_A, STATIC_B, (RungeKuttaStepInterpolator) new DormandPrince54StepInterpolator(), minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    public DormandPrince54Integrator(double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
        super(METHOD_NAME, true, STATIC_C, STATIC_A, STATIC_B, (RungeKuttaStepInterpolator) new DormandPrince54StepInterpolator(), minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaIntegrator
    public int getOrder() {
        return 5;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaIntegrator
    public double estimateError(double[][] yDotK, double[] y0, double[] y1, double h) {
        double d;
        double d2;
        double error = 0.0d;
        for (int j = 0; j < this.mainSetDimension; j++) {
            double errSum = (f275E1 * yDotK[0][j]) + (f276E3 * yDotK[2][j]) + (f277E4 * yDotK[3][j]) + (f278E5 * yDotK[4][j]) + (f279E6 * yDotK[5][j]) + (f280E7 * yDotK[6][j]);
            double yScale = FastMath.max(FastMath.abs(y0[j]), FastMath.abs(y1[j]));
            if (this.vecAbsoluteTolerance == null) {
                d = this.scalAbsoluteTolerance;
                d2 = this.scalRelativeTolerance;
            } else {
                d = this.vecAbsoluteTolerance[j];
                d2 = this.vecRelativeTolerance[j];
            }
            double ratio = (h * errSum) / (d + (d2 * yScale));
            error += ratio * ratio;
        }
        return FastMath.sqrt(error / ((double) this.mainSetDimension));
    }
}
