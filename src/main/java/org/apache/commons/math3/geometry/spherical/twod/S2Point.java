package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class S2Point implements Point<Sphere2D> {
    public static final S2Point MINUS_I = new S2Point(3.141592653589793d, 1.5707963267948966d, Vector3D.MINUS_I);
    public static final S2Point MINUS_J = new S2Point(4.71238898038469d, 1.5707963267948966d, Vector3D.MINUS_J);
    public static final S2Point MINUS_K = new S2Point(0.0d, 3.141592653589793d, Vector3D.MINUS_K);
    public static final S2Point NaN = new S2Point(Double.NaN, Double.NaN, Vector3D.NaN);
    public static final S2Point PLUS_I = new S2Point(0.0d, 1.5707963267948966d, Vector3D.PLUS_I);
    public static final S2Point PLUS_J = new S2Point(1.5707963267948966d, 1.5707963267948966d, Vector3D.PLUS_J);
    public static final S2Point PLUS_K = new S2Point(0.0d, 0.0d, Vector3D.PLUS_K);
    private static final long serialVersionUID = 20131218;
    private final double phi;
    private final double theta;
    private final Vector3D vector;

    public S2Point(double theta2, double phi2) throws OutOfRangeException {
        this(theta2, phi2, vector(theta2, phi2));
    }

    /* JADX WARN: Type inference failed for: r6v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public S2Point(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r8) throws org.apache.commons.math3.exception.MathArithmeticException {
        /*
            r7 = this;
            double r0 = r8.getY()
            double r2 = r8.getX()
            double r2 = org.apache.commons.math3.util.FastMath.atan2(r0, r2)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.PLUS_K
            double r4 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.angle(r0, r8)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r8.normalize()
            r1 = r7
            r1.<init>(r2, r4, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.spherical.twod.S2Point.<init>(org.apache.commons.math3.geometry.euclidean.threed.Vector3D):void");
    }

    private S2Point(double theta2, double phi2, Vector3D vector2) {
        this.theta = theta2;
        this.phi = phi2;
        this.vector = vector2;
    }

    private static Vector3D vector(double theta2, double phi2) throws OutOfRangeException {
        if (phi2 < 0.0d || phi2 > 3.141592653589793d) {
            throw new OutOfRangeException(Double.valueOf(phi2), 0, Double.valueOf(3.141592653589793d));
        }
        double cosTheta = FastMath.cos(theta2);
        double sinTheta = FastMath.sin(theta2);
        double cosPhi = FastMath.cos(phi2);
        double sinPhi = FastMath.sin(phi2);
        return new Vector3D(cosTheta * sinPhi, sinTheta * sinPhi, cosPhi);
    }

    public double getTheta() {
        return this.theta;
    }

    public double getPhi() {
        return this.phi;
    }

    public Vector3D getVector() {
        return this.vector;
    }

    @Override // org.apache.commons.math3.geometry.Point
    public Space getSpace() {
        return Sphere2D.getInstance();
    }

    @Override // org.apache.commons.math3.geometry.Point
    public boolean isNaN() {
        return Double.isNaN(this.theta) || Double.isNaN(this.phi);
    }

    /* JADX WARN: Type inference failed for: r6v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.spherical.twod.S2Point negate() {
        /*
            r8 = this;
            org.apache.commons.math3.geometry.spherical.twod.S2Point r1 = new org.apache.commons.math3.geometry.spherical.twod.S2Point
            double r2 = r8.theta
            double r2 = -r2
            r4 = 4614256656552045848(0x400921fb54442d18, double:3.141592653589793)
            double r6 = r8.phi
            double r4 = r4 - r6
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r8.vector
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r0.negate()
            r1.<init>(r2, r4, r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.spherical.twod.S2Point.negate():org.apache.commons.math3.geometry.spherical.twod.S2Point");
    }

    @Override // org.apache.commons.math3.geometry.Point
    public double distance(Point<Sphere2D> point) {
        return distance(this, (S2Point) point);
    }

    public static double distance(S2Point p1, S2Point p2) {
        return Vector3D.angle(p1.vector, p2.vector);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof S2Point)) {
            return false;
        }
        S2Point rhs = (S2Point) other;
        if (rhs.isNaN()) {
            return isNaN();
        }
        return this.theta == rhs.theta && this.phi == rhs.phi;
    }

    public int hashCode() {
        if (isNaN()) {
            return 542;
        }
        return ((MathUtils.hash(this.theta) * 37) + MathUtils.hash(this.phi)) * 134;
    }
}
