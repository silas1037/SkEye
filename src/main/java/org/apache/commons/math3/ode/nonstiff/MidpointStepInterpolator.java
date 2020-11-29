package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class MidpointStepInterpolator extends RungeKuttaStepInterpolator {
    private static final long serialVersionUID = 20111120;

    public MidpointStepInterpolator() {
    }

    MidpointStepInterpolator(MidpointStepInterpolator interpolator) {
        super(interpolator);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public StepInterpolator doCopy() {
        return new MidpointStepInterpolator(this);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH) {
        double coeffDot2 = 2.0d * theta;
        double coeffDot1 = 1.0d - coeffDot2;
        if (this.previousState == null || theta > 0.5d) {
            double coeff1 = oneMinusThetaH * theta;
            double coeff2 = oneMinusThetaH * (1.0d + theta);
            for (int i = 0; i < this.interpolatedState.length; i++) {
                double yDot1 = this.yDotK[0][i];
                double yDot2 = this.yDotK[1][i];
                this.interpolatedState[i] = (this.currentState[i] + (coeff1 * yDot1)) - (coeff2 * yDot2);
                this.interpolatedDerivatives[i] = (coeffDot1 * yDot1) + (coeffDot2 * yDot2);
            }
            return;
        }
        double coeff12 = theta * oneMinusThetaH;
        double coeff22 = theta * theta * this.f310h;
        for (int i2 = 0; i2 < this.interpolatedState.length; i2++) {
            double yDot12 = this.yDotK[0][i2];
            double yDot22 = this.yDotK[1][i2];
            this.interpolatedState[i2] = this.previousState[i2] + (coeff12 * yDot12) + (coeff22 * yDot22);
            this.interpolatedDerivatives[i2] = (coeffDot1 * yDot12) + (coeffDot2 * yDot22);
        }
    }
}
