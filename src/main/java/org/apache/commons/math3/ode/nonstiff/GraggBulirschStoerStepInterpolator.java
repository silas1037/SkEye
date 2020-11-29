package org.apache.commons.math3.ode.nonstiff;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;

class GraggBulirschStoerStepInterpolator extends AbstractStepInterpolator {
    private static final long serialVersionUID = 20110928;
    private int currentDegree;
    private double[] errfac;
    private double[][] polynomials;
    private double[] y0Dot;

    /* renamed from: y1 */
    private double[] f300y1;
    private double[] y1Dot;
    private double[][] yMidDots;

    public GraggBulirschStoerStepInterpolator() {
        this.y0Dot = null;
        this.f300y1 = null;
        this.y1Dot = null;
        this.yMidDots = null;
        resetTables(-1);
    }

    GraggBulirschStoerStepInterpolator(double[] y, double[] y0Dot2, double[] y1, double[] y1Dot2, double[][] yMidDots2, boolean forward, EquationsMapper primaryMapper, EquationsMapper[] secondaryMappers) {
        super(y, forward, primaryMapper, secondaryMappers);
        this.y0Dot = y0Dot2;
        this.f300y1 = y1;
        this.y1Dot = y1Dot2;
        this.yMidDots = yMidDots2;
        resetTables(yMidDots2.length + 4);
    }

    GraggBulirschStoerStepInterpolator(GraggBulirschStoerStepInterpolator interpolator) {
        super(interpolator);
        int dimension = this.currentState.length;
        this.y0Dot = null;
        this.f300y1 = null;
        this.y1Dot = null;
        this.yMidDots = null;
        if (interpolator.polynomials == null) {
            this.polynomials = null;
            this.currentDegree = -1;
            return;
        }
        resetTables(interpolator.currentDegree);
        for (int i = 0; i < this.polynomials.length; i++) {
            this.polynomials[i] = new double[dimension];
            System.arraycopy(interpolator.polynomials[i], 0, this.polynomials[i], 0, dimension);
        }
        this.currentDegree = interpolator.currentDegree;
    }

    private void resetTables(int maxDegree) {
        if (maxDegree < 0) {
            this.polynomials = null;
            this.errfac = null;
            this.currentDegree = -1;
            return;
        }
        double[][] newPols = new double[(maxDegree + 1)][];
        if (this.polynomials != null) {
            System.arraycopy(this.polynomials, 0, newPols, 0, this.polynomials.length);
            for (int i = this.polynomials.length; i < newPols.length; i++) {
                newPols[i] = new double[this.currentState.length];
            }
        } else {
            for (int i2 = 0; i2 < newPols.length; i2++) {
                newPols[i2] = new double[this.currentState.length];
            }
        }
        this.polynomials = newPols;
        if (maxDegree <= 4) {
            this.errfac = null;
        } else {
            this.errfac = new double[(maxDegree - 4)];
            for (int i3 = 0; i3 < this.errfac.length; i3++) {
                int ip5 = i3 + 5;
                this.errfac[i3] = 1.0d / ((double) (ip5 * ip5));
                double e = 0.5d * FastMath.sqrt(((double) (i3 + 1)) / ((double) ip5));
                for (int j = 0; j <= i3; j++) {
                    double[] dArr = this.errfac;
                    dArr[i3] = dArr[i3] * (e / ((double) (j + 1)));
                }
            }
        }
        this.currentDegree = 0;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public StepInterpolator doCopy() {
        return new GraggBulirschStoerStepInterpolator(this);
    }

    public void computeCoefficients(int mu, double h) {
        if (this.polynomials == null || this.polynomials.length <= mu + 4) {
            resetTables(mu + 4);
        }
        this.currentDegree = mu + 4;
        for (int i = 0; i < this.currentState.length; i++) {
            double yp0 = h * this.y0Dot[i];
            double yp1 = h * this.y1Dot[i];
            double ydiff = this.f300y1[i] - this.currentState[i];
            double aspl = ydiff - yp1;
            double bspl = yp0 - ydiff;
            this.polynomials[0][i] = this.currentState[i];
            this.polynomials[1][i] = ydiff;
            this.polynomials[2][i] = aspl;
            this.polynomials[3][i] = bspl;
            if (mu >= 0) {
                this.polynomials[4][i] = 16.0d * (this.yMidDots[0][i] - ((0.5d * (this.currentState[i] + this.f300y1[i])) + (0.125d * (aspl + bspl))));
                if (mu > 0) {
                    this.polynomials[5][i] = 16.0d * (this.yMidDots[1][i] - (ydiff + (0.25d * (aspl - bspl))));
                    if (mu > 1) {
                        this.polynomials[6][i] = 16.0d * ((this.yMidDots[2][i] - (yp1 - yp0)) + this.polynomials[4][i]);
                        if (mu > 2) {
                            this.polynomials[7][i] = 16.0d * ((this.yMidDots[3][i] - (6.0d * (bspl - aspl))) + (3.0d * this.polynomials[5][i]));
                            for (int j = 4; j <= mu; j++) {
                                double fac1 = 0.5d * ((double) j) * ((double) (j - 1));
                                this.polynomials[j + 4][i] = 16.0d * ((this.yMidDots[j][i] + (this.polynomials[j + 2][i] * fac1)) - (this.polynomials[j][i] * (((2.0d * fac1) * ((double) (j - 2))) * ((double) (j - 3)))));
                            }
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    public double estimateError(double[] scale) {
        double error = 0.0d;
        if (this.currentDegree < 5) {
            return 0.0d;
        }
        for (int i = 0; i < scale.length; i++) {
            double e = this.polynomials[this.currentDegree][i] / scale[i];
            error += e * e;
        }
        return FastMath.sqrt(error / ((double) scale.length)) * this.errfac[this.currentDegree - 5];
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator
    public void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH) {
        int dimension = this.currentState.length;
        double oneMinusTheta = 1.0d - theta;
        double theta05 = theta - 0.5d;
        double tOmT = theta * oneMinusTheta;
        double t4 = tOmT * tOmT;
        double t4Dot = 2.0d * tOmT * (1.0d - (2.0d * theta));
        double dot1 = 1.0d / this.f310h;
        double dot2 = ((2.0d - (3.0d * theta)) * theta) / this.f310h;
        double dot3 = ((((3.0d * theta) - 4.0d) * theta) + 1.0d) / this.f310h;
        for (int i = 0; i < dimension; i++) {
            double p0 = this.polynomials[0][i];
            double p1 = this.polynomials[1][i];
            double p2 = this.polynomials[2][i];
            double p3 = this.polynomials[3][i];
            this.interpolatedState[i] = (((((p2 * theta) + (p3 * oneMinusTheta)) * oneMinusTheta) + p1) * theta) + p0;
            this.interpolatedDerivatives[i] = (dot1 * p1) + (dot2 * p2) + (dot3 * p3);
            if (this.currentDegree > 3) {
                double cDot = 0.0d;
                double c = this.polynomials[this.currentDegree][i];
                for (int j = this.currentDegree - 1; j > 3; j--) {
                    double d = 1.0d / ((double) (j - 3));
                    cDot = d * ((theta05 * cDot) + c);
                    c = this.polynomials[j][i] + (c * d * theta05);
                }
                double[] dArr = this.interpolatedState;
                dArr[i] = dArr[i] + (t4 * c);
                double[] dArr2 = this.interpolatedDerivatives;
                dArr2[i] = dArr2[i] + (((t4 * cDot) + (t4Dot * c)) / this.f310h);
            }
        }
        if (this.f310h == 0.0d) {
            System.arraycopy(this.yMidDots[1], 0, this.interpolatedDerivatives, 0, dimension);
        }
    }

    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator, java.io.Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        int dimension = this.currentState == null ? -1 : this.currentState.length;
        writeBaseExternal(out);
        out.writeInt(this.currentDegree);
        for (int k = 0; k <= this.currentDegree; k++) {
            for (int l = 0; l < dimension; l++) {
                out.writeDouble(this.polynomials[k][l]);
            }
        }
    }

    @Override // org.apache.commons.math3.ode.sampling.AbstractStepInterpolator, java.io.Externalizable
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        double t = readBaseExternal(in);
        int dimension = this.currentState == null ? -1 : this.currentState.length;
        int degree = in.readInt();
        resetTables(degree);
        this.currentDegree = degree;
        for (int k = 0; k <= this.currentDegree; k++) {
            for (int l = 0; l < dimension; l++) {
                this.polynomials[k][l] = in.readDouble();
            }
        }
        setInterpolatedTime(t);
    }
}
