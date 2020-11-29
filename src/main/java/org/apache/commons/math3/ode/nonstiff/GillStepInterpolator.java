package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;

class GillStepInterpolator extends RungeKuttaStepInterpolator {
    private static final double ONE_MINUS_INV_SQRT_2 = (1.0d - FastMath.sqrt(0.5d));
    private static final double ONE_PLUS_INV_SQRT_2 = (FastMath.sqrt(0.5d) + 1.0d);
    private static final long serialVersionUID = 20111120;

    public GillStepInterpolator() {
    }

    GillStepInterpolator(GillStepInterpolator interpolator) {
        super(interpolator);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public StepInterpolator doCopy() {
        return new GillStepInterpolator(this);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH) {
        double twoTheta = 2.0d * theta;
        double fourTheta2 = twoTheta * twoTheta;
        double coeffDot1 = ((twoTheta - 3.0d) * theta) + 1.0d;
        double cDot23 = twoTheta * (1.0d - theta);
        double coeffDot2 = cDot23 * ONE_MINUS_INV_SQRT_2;
        double coeffDot3 = cDot23 * ONE_PLUS_INV_SQRT_2;
        double coeffDot4 = theta * (twoTheta - 1.0d);
        if (this.previousState == null || theta > 0.5d) {
            double s = oneMinusThetaH / 6.0d;
            double c23 = s * ((2.0d + twoTheta) - fourTheta2);
            double coeff1 = s * ((1.0d - (5.0d * theta)) + fourTheta2);
            double coeff2 = c23 * ONE_MINUS_INV_SQRT_2;
            double coeff3 = c23 * ONE_PLUS_INV_SQRT_2;
            double coeff4 = s * (1.0d + theta + fourTheta2);
            for (int i = 0; i < this.interpolatedState.length; i++) {
                double yDot1 = this.yDotK[0][i];
                double yDot2 = this.yDotK[1][i];
                double yDot3 = this.yDotK[2][i];
                double yDot4 = this.yDotK[3][i];
                this.interpolatedState[i] = (((this.currentState[i] - (coeff1 * yDot1)) - (coeff2 * yDot2)) - (coeff3 * yDot3)) - (coeff4 * yDot4);
                this.interpolatedDerivatives[i] = (coeffDot1 * yDot1) + (coeffDot2 * yDot2) + (coeffDot3 * yDot3) + (coeffDot4 * yDot4);
            }
            return;
        }
        double s2 = (this.f310h * theta) / 6.0d;
        double c232 = s2 * ((6.0d * theta) - fourTheta2);
        double coeff12 = s2 * ((6.0d - (9.0d * theta)) + fourTheta2);
        double coeff22 = c232 * ONE_MINUS_INV_SQRT_2;
        double coeff32 = c232 * ONE_PLUS_INV_SQRT_2;
        double coeff42 = s2 * ((-3.0d * theta) + fourTheta2);
        for (int i2 = 0; i2 < this.interpolatedState.length; i2++) {
            double yDot12 = this.yDotK[0][i2];
            double yDot22 = this.yDotK[1][i2];
            double yDot32 = this.yDotK[2][i2];
            double yDot42 = this.yDotK[3][i2];
            this.interpolatedState[i2] = this.previousState[i2] + (coeff12 * yDot12) + (coeff22 * yDot22) + (coeff32 * yDot32) + (coeff42 * yDot42);
            this.interpolatedDerivatives[i2] = (coeffDot1 * yDot12) + (coeffDot2 * yDot22) + (coeffDot3 * yDot32) + (coeffDot4 * yDot42);
        }
    }
}
