package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;

class LutherStepInterpolator extends RungeKuttaStepInterpolator {

    /* renamed from: Q */
    private static final double f303Q = FastMath.sqrt(21.0d);
    private static final long serialVersionUID = 20140416;

    public LutherStepInterpolator() {
    }

    LutherStepInterpolator(LutherStepInterpolator interpolator) {
        super(interpolator);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public StepInterpolator doCopy() {
        return new LutherStepInterpolator(this);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH) {
        double coeffDot1 = 1.0d + ((-10.8d + ((36.0d + ((-47.0d + (21.0d * theta)) * theta)) * theta)) * theta);
        double coeffDot3 = theta * (-13.866666666666667d + ((106.66666666666667d + ((-202.66666666666666d + (112.0d * theta)) * theta)) * theta));
        double coeffDot4 = theta * (12.96d + ((-97.2d + ((194.4d + ((-567.0d * theta) / 5.0d)) * theta)) * theta));
        double coeffDot5 = theta * (((833.0d + (343.0d * f303Q)) / 150.0d) + ((((-637.0d - (357.0d * f303Q)) / 30.0d) + ((((392.0d + (287.0d * f303Q)) / 15.0d) + (((-49.0d - (49.0d * f303Q)) * theta) / 5.0d)) * theta)) * theta));
        double coeffDot6 = theta * (((833.0d - (343.0d * f303Q)) / 150.0d) + ((((-637.0d + (357.0d * f303Q)) / 30.0d) + ((((392.0d - (287.0d * f303Q)) / 15.0d) + (((-49.0d + (49.0d * f303Q)) * theta) / 5.0d)) * theta)) * theta));
        double coeffDot7 = theta * (0.6d + ((-3.0d + (3.0d * theta)) * theta));
        if (this.previousState == null || theta > 0.5d) {
            double coeff1 = -0.05d + ((0.95d + ((-4.45d + ((7.55d + ((-21.0d * theta) / 5.0d)) * theta)) * theta)) * theta);
            double coeff3 = -0.35555555555555557d + ((-0.35555555555555557d + ((-7.288888888888889d + ((28.266666666666666d + ((-112.0d * theta) / 5.0d)) * theta)) * theta)) * theta);
            double coeff4 = theta * (6.48d + ((-25.92d + ((567.0d * theta) / 25.0d)) * theta)) * theta;
            double coeff5 = -0.2722222222222222d + ((-0.2722222222222222d + ((((2254.0d + (1029.0d * f303Q)) / 900.0d) + ((((-1372.0d - (847.0d * f303Q)) / 300.0d) + (((49.0d + (49.0d * f303Q)) * theta) / 25.0d)) * theta)) * theta)) * theta);
            double coeff6 = -0.2722222222222222d + ((-0.2722222222222222d + ((((2254.0d - (1029.0d * f303Q)) / 900.0d) + ((((-1372.0d + (847.0d * f303Q)) / 300.0d) + (((49.0d - (49.0d * f303Q)) * theta) / 25.0d)) * theta)) * theta)) * theta);
            double coeff7 = -0.05d + ((-0.05d + ((0.25d + (-0.75d * theta)) * theta)) * theta);
            for (int i = 0; i < this.interpolatedState.length; i++) {
                double yDot1 = this.yDotK[0][i];
                double yDot2 = this.yDotK[1][i];
                double yDot3 = this.yDotK[2][i];
                double yDot4 = this.yDotK[3][i];
                double yDot5 = this.yDotK[4][i];
                double yDot6 = this.yDotK[5][i];
                double yDot7 = this.yDotK[6][i];
                this.interpolatedState[i] = this.currentState[i] + (((coeff1 * yDot1) + (0.0d * yDot2) + (coeff3 * yDot3) + (coeff4 * yDot4) + (coeff5 * yDot5) + (coeff6 * yDot6) + (coeff7 * yDot7)) * oneMinusThetaH);
                this.interpolatedDerivatives[i] = (coeffDot1 * yDot1) + (0.0d * yDot2) + (coeffDot3 * yDot3) + (coeffDot4 * yDot4) + (coeffDot5 * yDot5) + (coeffDot6 * yDot6) + (coeffDot7 * yDot7);
            }
            return;
        }
        double coeff12 = 1.0d + ((-5.4d + ((12.0d + ((-11.75d + ((21.0d * theta) / 5.0d)) * theta)) * theta)) * theta);
        double coeff32 = theta * (-6.933333333333334d + ((35.55555555555556d + ((-50.666666666666664d + ((112.0d * theta) / 5.0d)) * theta)) * theta));
        double coeff42 = theta * (6.48d + ((-32.4d + ((48.6d + ((-567.0d * theta) / 25.0d)) * theta)) * theta));
        double coeff52 = theta * (((833.0d + (343.0d * f303Q)) / 300.0d) + ((((-637.0d - (357.0d * f303Q)) / 90.0d) + ((((392.0d + (287.0d * f303Q)) / 60.0d) + (((-49.0d - (49.0d * f303Q)) * theta) / 25.0d)) * theta)) * theta));
        double coeff62 = theta * (((833.0d - (343.0d * f303Q)) / 300.0d) + ((((-637.0d + (357.0d * f303Q)) / 90.0d) + ((((392.0d - (287.0d * f303Q)) / 60.0d) + (((-49.0d + (49.0d * f303Q)) * theta) / 25.0d)) * theta)) * theta));
        double coeff72 = theta * (0.3d + ((-1.0d + (0.75d * theta)) * theta));
        for (int i2 = 0; i2 < this.interpolatedState.length; i2++) {
            double yDot12 = this.yDotK[0][i2];
            double yDot22 = this.yDotK[1][i2];
            double yDot32 = this.yDotK[2][i2];
            double yDot42 = this.yDotK[3][i2];
            double yDot52 = this.yDotK[4][i2];
            double yDot62 = this.yDotK[5][i2];
            double yDot72 = this.yDotK[6][i2];
            this.interpolatedState[i2] = this.previousState[i2] + (this.f310h * theta * ((coeff12 * yDot12) + (0.0d * yDot22) + (coeff32 * yDot32) + (coeff42 * yDot42) + (coeff52 * yDot52) + (coeff62 * yDot62) + (coeff72 * yDot72)));
            this.interpolatedDerivatives[i2] = (coeffDot1 * yDot12) + (0.0d * yDot22) + (coeffDot3 * yDot32) + (coeffDot4 * yDot42) + (coeffDot5 * yDot52) + (coeffDot6 * yDot62) + (coeffDot7 * yDot72);
        }
    }
}
