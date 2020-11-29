package com.lavadip.skeye.astro.keplerian;

import com.lavadip.skeye.astro.Instant;

public final class Elements {
    public final double argOfPeriapsis;
    public final double eccentricity;
    public final Instant epochPeriapsis;
    public final double inclination;
    public final double longitudeOfAscendingNode;
    public final double meanAnomalyAtEpoch;
    public final double periapsisDistance;

    public Elements(Instant epochPeriapsis2, double periapsisDistance2, double meanAnomalyAtEpoch2, double eccentricity2, double inclination2, double longitudeOfAscendingNode2, double argOfPeriapsis2) {
        this.epochPeriapsis = epochPeriapsis2;
        this.periapsisDistance = periapsisDistance2;
        this.meanAnomalyAtEpoch = meanAnomalyAtEpoch2;
        this.eccentricity = eccentricity2;
        this.inclination = inclination2;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode2;
        this.argOfPeriapsis = argOfPeriapsis2;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.argOfPeriapsis);
        int result = ((int) ((temp >>> 32) ^ temp)) + 31;
        long temp2 = Double.doubleToLongBits(this.eccentricity);
        int result2 = (((result * 31) + ((int) ((temp2 >>> 32) ^ temp2))) * 31) + (this.epochPeriapsis == null ? 0 : this.epochPeriapsis.hashCode());
        long temp3 = Double.doubleToLongBits(this.inclination);
        int result3 = (result2 * 31) + ((int) ((temp3 >>> 32) ^ temp3));
        long temp4 = Double.doubleToLongBits(this.longitudeOfAscendingNode);
        int result4 = (result3 * 31) + ((int) ((temp4 >>> 32) ^ temp4));
        long temp5 = Double.doubleToLongBits(this.meanAnomalyAtEpoch);
        int result5 = (result4 * 31) + ((int) ((temp5 >>> 32) ^ temp5));
        long temp6 = Double.doubleToLongBits(this.periapsisDistance);
        return (result5 * 31) + ((int) ((temp6 >>> 32) ^ temp6));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Elements other = (Elements) obj;
        if (Double.doubleToLongBits(this.argOfPeriapsis) != Double.doubleToLongBits(other.argOfPeriapsis)) {
            return false;
        }
        if (Double.doubleToLongBits(this.eccentricity) != Double.doubleToLongBits(other.eccentricity)) {
            return false;
        }
        if (this.epochPeriapsis == null) {
            if (other.epochPeriapsis != null) {
                return false;
            }
        } else if (!this.epochPeriapsis.equals(other.epochPeriapsis)) {
            return false;
        }
        if (Double.doubleToLongBits(this.inclination) != Double.doubleToLongBits(other.inclination)) {
            return false;
        }
        if (Double.doubleToLongBits(this.longitudeOfAscendingNode) != Double.doubleToLongBits(other.longitudeOfAscendingNode)) {
            return false;
        }
        if (Double.doubleToLongBits(this.meanAnomalyAtEpoch) != Double.doubleToLongBits(other.meanAnomalyAtEpoch)) {
            return false;
        }
        return Double.doubleToLongBits(this.periapsisDistance) == Double.doubleToLongBits(other.periapsisDistance);
    }
}
