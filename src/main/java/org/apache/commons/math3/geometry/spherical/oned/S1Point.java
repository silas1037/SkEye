package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class S1Point implements Point<Sphere1D> {
    public static final S1Point NaN = new S1Point(Double.NaN, Vector2D.NaN);
    private static final long serialVersionUID = 20131218;
    private final double alpha;
    private final Vector2D vector;

    public S1Point(double alpha2) {
        this(MathUtils.normalizeAngle(alpha2, 3.141592653589793d), new Vector2D(FastMath.cos(alpha2), FastMath.sin(alpha2)));
    }

    private S1Point(double alpha2, Vector2D vector2) {
        this.alpha = alpha2;
        this.vector = vector2;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public Vector2D getVector() {
        return this.vector;
    }

    @Override // org.apache.commons.math3.geometry.Point
    public Space getSpace() {
        return Sphere1D.getInstance();
    }

    @Override // org.apache.commons.math3.geometry.Point
    public boolean isNaN() {
        return Double.isNaN(this.alpha);
    }

    @Override // org.apache.commons.math3.geometry.Point
    public double distance(Point<Sphere1D> point) {
        return distance(this, (S1Point) point);
    }

    public static double distance(S1Point p1, S1Point p2) {
        return Vector2D.angle(p1.vector, p2.vector);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof S1Point)) {
            return false;
        }
        S1Point rhs = (S1Point) other;
        if (rhs.isNaN()) {
            return isNaN();
        }
        return this.alpha == rhs.alpha;
    }

    public int hashCode() {
        if (isNaN()) {
            return 542;
        }
        return MathUtils.hash(this.alpha) * 1759;
    }
}
