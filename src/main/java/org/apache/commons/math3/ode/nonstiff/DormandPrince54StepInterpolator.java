package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

class DormandPrince54StepInterpolator extends RungeKuttaStepInterpolator {
    private static final double A70 = 0.09114583333333333d;
    private static final double A72 = 0.44923629829290207d;
    private static final double A73 = 0.6510416666666666d;
    private static final double A74 = -0.322376179245283d;
    private static final double A75 = 0.13095238095238096d;

    /* renamed from: D0 */
    private static final double f281D0 = -1.1270175653862835d;

    /* renamed from: D2 */
    private static final double f282D2 = 2.675424484351598d;

    /* renamed from: D3 */
    private static final double f283D3 = -5.685526961588504d;

    /* renamed from: D4 */
    private static final double f284D4 = 3.5219323679207912d;

    /* renamed from: D5 */
    private static final double f285D5 = -1.7672812570757455d;

    /* renamed from: D6 */
    private static final double f286D6 = 2.382468931778144d;
    private static final long serialVersionUID = 20111120;

    /* renamed from: v1 */
    private double[] f287v1;

    /* renamed from: v2 */
    private double[] f288v2;

    /* renamed from: v3 */
    private double[] f289v3;

    /* renamed from: v4 */
    private double[] f290v4;
    private boolean vectorsInitialized;

    public DormandPrince54StepInterpolator() {
        this.f287v1 = null;
        this.f288v2 = null;
        this.f289v3 = null;
        this.f290v4 = null;
        this.vectorsInitialized = false;
    }

    DormandPrince54StepInterpolator(DormandPrince54StepInterpolator interpolator) {
        super(interpolator);
        if (interpolator.f287v1 == null) {
            this.f287v1 = null;
            this.f288v2 = null;
            this.f289v3 = null;
            this.f290v4 = null;
            this.vectorsInitialized = false;
            return;
        }
        this.f287v1 = (double[]) interpolator.f287v1.clone();
        this.f288v2 = (double[]) interpolator.f288v2.clone();
        this.f289v3 = (double[]) interpolator.f289v3.clone();
        this.f290v4 = (double[]) interpolator.f290v4.clone();
        this.vectorsInitialized = interpolator.vectorsInitialized;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public StepInterpolator doCopy() {
        return new DormandPrince54StepInterpolator(this);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaStepInterpolator
    public void reinitialize(AbstractIntegrator integrator, double[] y, double[][] yDotK, boolean forward, EquationsMapper primaryMapper, EquationsMapper[] secondaryMappers) {
        super.reinitialize(integrator, y, yDotK, forward, primaryMapper, secondaryMappers);
        this.f287v1 = null;
        this.f288v2 = null;
        this.f289v3 = null;
        this.f290v4 = null;
        this.vectorsInitialized = false;
    }

    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void storeTime(double t) {
        super.storeTime(t);
        this.vectorsInitialized = false;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH) {
        if (!this.vectorsInitialized) {
            if (this.f287v1 == null) {
                this.f287v1 = new double[this.interpolatedState.length];
                this.f288v2 = new double[this.interpolatedState.length];
                this.f289v3 = new double[this.interpolatedState.length];
                this.f290v4 = new double[this.interpolatedState.length];
            }
            for (int i = 0; i < this.interpolatedState.length; i++) {
                double yDot0 = this.yDotK[0][i];
                double yDot2 = this.yDotK[2][i];
                double yDot3 = this.yDotK[3][i];
                double yDot4 = this.yDotK[4][i];
                double yDot5 = this.yDotK[5][i];
                double yDot6 = this.yDotK[6][i];
                this.f287v1[i] = (A70 * yDot0) + (A72 * yDot2) + (A73 * yDot3) + (A74 * yDot4) + (A75 * yDot5);
                this.f288v2[i] = yDot0 - this.f287v1[i];
                this.f289v3[i] = (this.f287v1[i] - this.f288v2[i]) - yDot6;
                this.f290v4[i] = (f281D0 * yDot0) + (f282D2 * yDot2) + (f283D3 * yDot3) + (f284D4 * yDot4) + (f285D5 * yDot5) + (f286D6 * yDot6);
            }
            this.vectorsInitialized = true;
        }
        double eta = 1.0d - theta;
        double twoTheta = 2.0d * theta;
        double dot2 = 1.0d - twoTheta;
        double dot3 = theta * (2.0d - (3.0d * theta));
        double dot4 = twoTheta * (1.0d + ((twoTheta - 3.0d) * theta));
        if (this.previousState == null || theta > 0.5d) {
            for (int i2 = 0; i2 < this.interpolatedState.length; i2++) {
                this.interpolatedState[i2] = this.currentState[i2] - ((this.f287v1[i2] - ((this.f288v2[i2] + ((this.f289v3[i2] + (this.f290v4[i2] * eta)) * theta)) * theta)) * oneMinusThetaH);
                this.interpolatedDerivatives[i2] = this.f287v1[i2] + (this.f288v2[i2] * dot2) + (this.f289v3[i2] * dot3) + (this.f290v4[i2] * dot4);
            }
            return;
        }
        for (int i3 = 0; i3 < this.interpolatedState.length; i3++) {
            this.interpolatedState[i3] = this.previousState[i3] + (this.f310h * theta * (this.f287v1[i3] + ((this.f288v2[i3] + ((this.f289v3[i3] + (this.f290v4[i3] * eta)) * theta)) * eta)));
            this.interpolatedDerivatives[i3] = this.f287v1[i3] + (this.f288v2[i3] * dot2) + (this.f289v3[i3] * dot3) + (this.f290v4[i3] * dot4);
        }
    }
}
