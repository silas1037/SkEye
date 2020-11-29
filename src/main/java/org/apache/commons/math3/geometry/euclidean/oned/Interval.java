package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.Region;

public class Interval {
    private final double lower;
    private final double upper;

    public Interval(double lower2, double upper2) {
        if (upper2 < lower2) {
            throw new NumberIsTooSmallException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, Double.valueOf(upper2), Double.valueOf(lower2), true);
        }
        this.lower = lower2;
        this.upper = upper2;
    }

    public double getInf() {
        return this.lower;
    }

    @Deprecated
    public double getLower() {
        return getInf();
    }

    public double getSup() {
        return this.upper;
    }

    @Deprecated
    public double getUpper() {
        return getSup();
    }

    public double getSize() {
        return this.upper - this.lower;
    }

    @Deprecated
    public double getLength() {
        return getSize();
    }

    public double getBarycenter() {
        return 0.5d * (this.lower + this.upper);
    }

    @Deprecated
    public double getMidPoint() {
        return getBarycenter();
    }

    public Region.Location checkPoint(double point, double tolerance) {
        if (point < this.lower - tolerance || point > this.upper + tolerance) {
            return Region.Location.OUTSIDE;
        }
        if (point <= this.lower + tolerance || point >= this.upper - tolerance) {
            return Region.Location.BOUNDARY;
        }
        return Region.Location.INSIDE;
    }
}
