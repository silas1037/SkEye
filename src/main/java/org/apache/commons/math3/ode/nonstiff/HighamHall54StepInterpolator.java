package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class HighamHall54StepInterpolator extends RungeKuttaStepInterpolator {
    private static final long serialVersionUID = 20111120;

    public HighamHall54StepInterpolator() {
    }

    HighamHall54StepInterpolator(HighamHall54StepInterpolator interpolator) {
        super(interpolator);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public StepInterpolator doCopy() {
        return new HighamHall54StepInterpolator(this);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH) {
        double bDot0 = 1.0d + ((-7.5d + ((16.0d - (10.0d * theta)) * theta)) * theta);
        double bDot2 = theta * (28.6875d + ((-91.125d + (67.5d * theta)) * theta));
        double bDot3 = theta * (-44.0d + ((152.0d - (120.0d * theta)) * theta));
        double bDot4 = theta * (23.4375d + ((-78.125d + (62.5d * theta)) * theta));
        double bDot5 = ((5.0d * theta) / 8.0d) * ((2.0d * theta) - 1.0d);
        if (this.previousState == null || theta > 0.5d) {
            double theta2 = theta * theta;
            double b0 = this.f310h * (-0.08333333333333333d + ((1.0d + ((-3.75d + ((5.333333333333333d + ((-5.0d * theta) / 2.0d)) * theta)) * theta)) * theta));
            double b2 = this.f310h * (-0.84375d + ((14.34375d + ((-30.375d + ((135.0d * theta) / 8.0d)) * theta)) * theta2));
            double b3 = this.f310h * (1.3333333333333333d + ((-22.0d + ((50.666666666666664d + (-30.0d * theta)) * theta)) * theta2));
            double b4 = this.f310h * (-1.3020833333333333d + ((11.71875d + ((-26.041666666666668d + ((125.0d * theta) / 8.0d)) * theta)) * theta2));
            double b5 = this.f310h * (-0.10416666666666667d + ((-0.3125d + ((5.0d * theta) / 12.0d)) * theta2));
            for (int i = 0; i < this.interpolatedState.length; i++) {
                double yDot0 = this.yDotK[0][i];
                double yDot2 = this.yDotK[2][i];
                double yDot3 = this.yDotK[3][i];
                double yDot4 = this.yDotK[4][i];
                double yDot5 = this.yDotK[5][i];
                this.interpolatedState[i] = this.currentState[i] + (b0 * yDot0) + (b2 * yDot2) + (b3 * yDot3) + (b4 * yDot4) + (b5 * yDot5);
                this.interpolatedDerivatives[i] = (bDot0 * yDot0) + (bDot2 * yDot2) + (bDot3 * yDot3) + (bDot4 * yDot4) + (bDot5 * yDot5);
            }
            return;
        }
        double hTheta = this.f310h * theta;
        double b02 = hTheta * (1.0d + ((-3.75d + ((5.333333333333333d - (2.5d * theta)) * theta)) * theta));
        double b22 = hTheta * (14.34375d + ((-30.375d + ((135.0d * theta) / 8.0d)) * theta)) * theta;
        double b32 = hTheta * (-22.0d + ((50.666666666666664d + (-30.0d * theta)) * theta)) * theta;
        double b42 = hTheta * (11.71875d + ((-26.041666666666668d + ((125.0d * theta) / 8.0d)) * theta)) * theta;
        double b52 = hTheta * (-0.3125d + ((5.0d * theta) / 12.0d)) * theta;
        for (int i2 = 0; i2 < this.interpolatedState.length; i2++) {
            double yDot02 = this.yDotK[0][i2];
            double yDot22 = this.yDotK[2][i2];
            double yDot32 = this.yDotK[3][i2];
            double yDot42 = this.yDotK[4][i2];
            double yDot52 = this.yDotK[5][i2];
            this.interpolatedState[i2] = this.previousState[i2] + (b02 * yDot02) + (b22 * yDot22) + (b32 * yDot32) + (b42 * yDot42) + (b52 * yDot52);
            this.interpolatedDerivatives[i2] = (bDot0 * yDot02) + (bDot2 * yDot22) + (bDot3 * yDot32) + (bDot4 * yDot42) + (bDot5 * yDot52);
        }
    }
}
