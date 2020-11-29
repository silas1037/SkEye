package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class Arc {
    private final double lower;
    private final double middle;
    private final double tolerance;
    private final double upper;

    public Arc(double lower2, double upper2, double tolerance2) throws NumberIsTooLargeException {
        this.tolerance = tolerance2;
        if (Precision.equals(lower2, upper2, 0) || upper2 - lower2 >= 6.283185307179586d) {
            this.lower = 0.0d;
            this.upper = 6.283185307179586d;
            this.middle = 3.141592653589793d;
        } else if (lower2 <= upper2) {
            this.lower = MathUtils.normalizeAngle(lower2, 3.141592653589793d);
            this.upper = this.lower + (upper2 - lower2);
            this.middle = 0.5d * (this.lower + this.upper);
        } else {
            throw new NumberIsTooLargeException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, Double.valueOf(lower2), Double.valueOf(upper2), true);
        }
    }

    public double getInf() {
        return this.lower;
    }

    public double getSup() {
        return this.upper;
    }

    public double getSize() {
        return this.upper - this.lower;
    }

    public double getBarycenter() {
        return this.middle;
    }

    public double getTolerance() {
        return this.tolerance;
    }

    public Region.Location checkPoint(double point) {
        double normalizedPoint = MathUtils.normalizeAngle(point, this.middle);
        if (normalizedPoint < this.lower - this.tolerance || normalizedPoint > this.upper + this.tolerance) {
            return Region.Location.OUTSIDE;
        }
        if (normalizedPoint <= this.lower + this.tolerance || normalizedPoint >= this.upper - this.tolerance) {
            return getSize() >= 6.283185307179586d - this.tolerance ? Region.Location.INSIDE : Region.Location.BOUNDARY;
        }
        return Region.Location.INSIDE;
    }
}
