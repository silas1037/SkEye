package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class ThreeEighthesStepInterpolator extends RungeKuttaStepInterpolator {
    private static final long serialVersionUID = 20111120;

    public ThreeEighthesStepInterpolator() {
    }

    ThreeEighthesStepInterpolator(ThreeEighthesStepInterpolator interpolator) {
        super(interpolator);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public StepInterpolator doCopy() {
        return new ThreeEighthesStepInterpolator(this);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH) {
        double coeffDot3 = 0.75d * theta;
        double coeffDot1 = (((4.0d * theta) - 5.0d) * coeffDot3) + 1.0d;
        double coeffDot2 = coeffDot3 * (5.0d - (6.0d * theta));
        double coeffDot4 = coeffDot3 * ((2.0d * theta) - 1.0d);
        if (this.previousState == null || theta > 0.5d) {
            double s = oneMinusThetaH / 8.0d;
            double fourTheta2 = 4.0d * theta * theta;
            double coeff1 = s * ((1.0d - (7.0d * theta)) + (2.0d * fourTheta2));
            double coeff2 = 3.0d * s * ((1.0d + theta) - fourTheta2);
            double coeff3 = 3.0d * s * (1.0d + theta);
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
        double s2 = (this.f310h * theta) / 8.0d;
        double fourTheta22 = 4.0d * theta * theta;
        double coeff12 = s2 * ((8.0d - (15.0d * theta)) + (2.0d * fourTheta22));
        double coeff22 = 3.0d * s2 * ((5.0d * theta) - fourTheta22);
        double coeff32 = 3.0d * s2 * theta;
        double coeff42 = s2 * ((-3.0d * theta) + fourTheta22);
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
