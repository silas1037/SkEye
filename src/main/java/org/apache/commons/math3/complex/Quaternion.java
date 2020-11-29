package org.apache.commons.math3.complex;

import java.io.Serializable;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public final class Quaternion implements Serializable {

    /* renamed from: I */
    public static final Quaternion f147I = new Quaternion(0.0d, 1.0d, 0.0d, 0.0d);
    public static final Quaternion IDENTITY = new Quaternion(1.0d, 0.0d, 0.0d, 0.0d);

    /* renamed from: J */
    public static final Quaternion f148J = new Quaternion(0.0d, 0.0d, 1.0d, 0.0d);

    /* renamed from: K */
    public static final Quaternion f149K = new Quaternion(0.0d, 0.0d, 0.0d, 1.0d);
    public static final Quaternion ZERO = new Quaternion(0.0d, 0.0d, 0.0d, 0.0d);
    private static final long serialVersionUID = 20092012;

    /* renamed from: q0 */
    private final double f150q0;

    /* renamed from: q1 */
    private final double f151q1;

    /* renamed from: q2 */
    private final double f152q2;

    /* renamed from: q3 */
    private final double f153q3;

    public Quaternion(double a, double b, double c, double d) {
        this.f150q0 = a;
        this.f151q1 = b;
        this.f152q2 = c;
        this.f153q3 = d;
    }

    public Quaternion(double scalar, double[] v) throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.f150q0 = scalar;
        this.f151q1 = v[0];
        this.f152q2 = v[1];
        this.f153q3 = v[2];
    }

    public Quaternion(double[] v) {
        this(0.0d, v);
    }

    public Quaternion getConjugate() {
        return new Quaternion(this.f150q0, -this.f151q1, -this.f152q2, -this.f153q3);
    }

    public static Quaternion multiply(Quaternion q1, Quaternion q2) {
        double q1a = q1.getQ0();
        double q1b = q1.getQ1();
        double q1c = q1.getQ2();
        double q1d = q1.getQ3();
        double q2a = q2.getQ0();
        double q2b = q2.getQ1();
        double q2c = q2.getQ2();
        double q2d = q2.getQ3();
        return new Quaternion((((q1a * q2a) - (q1b * q2b)) - (q1c * q2c)) - (q1d * q2d), (((q1a * q2b) + (q1b * q2a)) + (q1c * q2d)) - (q1d * q2c), ((q1a * q2c) - (q1b * q2d)) + (q1c * q2a) + (q1d * q2b), (((q1a * q2d) + (q1b * q2c)) - (q1c * q2b)) + (q1d * q2a));
    }

    public Quaternion multiply(Quaternion q) {
        return multiply(this, q);
    }

    public static Quaternion add(Quaternion q1, Quaternion q2) {
        return new Quaternion(q1.getQ0() + q2.getQ0(), q1.getQ1() + q2.getQ1(), q1.getQ2() + q2.getQ2(), q1.getQ3() + q2.getQ3());
    }

    public Quaternion add(Quaternion q) {
        return add(this, q);
    }

    public static Quaternion subtract(Quaternion q1, Quaternion q2) {
        return new Quaternion(q1.getQ0() - q2.getQ0(), q1.getQ1() - q2.getQ1(), q1.getQ2() - q2.getQ2(), q1.getQ3() - q2.getQ3());
    }

    public Quaternion subtract(Quaternion q) {
        return subtract(this, q);
    }

    public static double dotProduct(Quaternion q1, Quaternion q2) {
        return (q1.getQ0() * q2.getQ0()) + (q1.getQ1() * q2.getQ1()) + (q1.getQ2() * q2.getQ2()) + (q1.getQ3() * q2.getQ3());
    }

    public double dotProduct(Quaternion q) {
        return dotProduct(this, q);
    }

    public double getNorm() {
        return FastMath.sqrt((this.f150q0 * this.f150q0) + (this.f151q1 * this.f151q1) + (this.f152q2 * this.f152q2) + (this.f153q3 * this.f153q3));
    }

    public Quaternion normalize() {
        double norm = getNorm();
        if (norm >= Precision.SAFE_MIN) {
            return new Quaternion(this.f150q0 / norm, this.f151q1 / norm, this.f152q2 / norm, this.f153q3 / norm);
        }
        throw new ZeroException(LocalizedFormats.NORM, Double.valueOf(norm));
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Quaternion)) {
            return false;
        }
        Quaternion q = (Quaternion) other;
        return this.f150q0 == q.getQ0() && this.f151q1 == q.getQ1() && this.f152q2 == q.getQ2() && this.f153q3 == q.getQ3();
    }

    public int hashCode() {
        int result = 17;
        for (double comp : new double[]{this.f150q0, this.f151q1, this.f152q2, this.f153q3}) {
            result = (result * 31) + MathUtils.hash(comp);
        }
        return result;
    }

    public boolean equals(Quaternion q, double eps) {
        return Precision.equals(this.f150q0, q.getQ0(), eps) && Precision.equals(this.f151q1, q.getQ1(), eps) && Precision.equals(this.f152q2, q.getQ2(), eps) && Precision.equals(this.f153q3, q.getQ3(), eps);
    }

    public boolean isUnitQuaternion(double eps) {
        return Precision.equals(getNorm(), 1.0d, eps);
    }

    public boolean isPureQuaternion(double eps) {
        return FastMath.abs(getQ0()) <= eps;
    }

    public Quaternion getPositivePolarForm() {
        if (getQ0() >= 0.0d) {
            return normalize();
        }
        Quaternion unitQ = normalize();
        return new Quaternion(-unitQ.getQ0(), -unitQ.getQ1(), -unitQ.getQ2(), -unitQ.getQ3());
    }

    public Quaternion getInverse() {
        double squareNorm = (this.f150q0 * this.f150q0) + (this.f151q1 * this.f151q1) + (this.f152q2 * this.f152q2) + (this.f153q3 * this.f153q3);
        if (squareNorm >= Precision.SAFE_MIN) {
            return new Quaternion(this.f150q0 / squareNorm, (-this.f151q1) / squareNorm, (-this.f152q2) / squareNorm, (-this.f153q3) / squareNorm);
        }
        throw new ZeroException(LocalizedFormats.NORM, Double.valueOf(squareNorm));
    }

    public double getQ0() {
        return this.f150q0;
    }

    public double getQ1() {
        return this.f151q1;
    }

    public double getQ2() {
        return this.f152q2;
    }

    public double getQ3() {
        return this.f153q3;
    }

    public double getScalarPart() {
        return getQ0();
    }

    public double[] getVectorPart() {
        return new double[]{getQ1(), getQ2(), getQ3()};
    }

    public Quaternion multiply(double alpha) {
        return new Quaternion(this.f150q0 * alpha, this.f151q1 * alpha, this.f152q2 * alpha, this.f153q3 * alpha);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[").append(this.f150q0).append(" ").append(this.f151q1).append(" ").append(this.f152q2).append(" ").append(this.f153q3).append("]");
        return s.toString();
    }
}
