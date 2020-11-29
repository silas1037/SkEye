package com.lavadip.skeye.astro;

import com.lavadip.skeye.astro.keplerian.Orbit;

public final class Comet {
    public final double absoluteMagnitude;
    public final String name;
    public final Orbit orbit;
    public final double slope;

    public static class CometData {
        public final double apparentMagnitude;
        public final Orbit.OrbitInstantData posData;

        /* renamed from: t */
        public final Instant f22t;

        public CometData(Instant t, double apparentMagnitude2, Orbit.OrbitInstantData posData2) {
            this.f22t = t;
            this.apparentMagnitude = apparentMagnitude2;
            this.posData = posData2;
        }
    }

    public Comet(String name2, double absoluteMagnitude2, double slope2, Orbit orbit2) {
        this.name = name2;
        this.absoluteMagnitude = absoluteMagnitude2;
        this.slope = slope2;
        this.orbit = orbit2;
    }

    public CometData getDataForInstant(Instant t) {
        Orbit.OrbitInstantData pos = this.orbit.getGeoentricPosition(t);
        return new CometData(t, getApparentMag(pos, t), pos);
    }

    private double getApparentMag(Orbit.OrbitInstantData pos, Instant t) {
        if (this.name.equals("2P/Encke")) {
            return getMag2PEncke(pos, t);
        }
        return getStandardCometApparentMag(this.absoluteMagnitude, this.slope, pos);
    }

    private double getMag2PEncke(Orbit.OrbitInstantData pos, Instant t) {
        double days = t.getDaysJ2000() - this.orbit.elems.epochPeriapsis.getDaysJ2000();
        if (days < -110.0d) {
            return AsteroidBrightnessModel.getMagnitude(14.2d, 0.15d, pos);
        }
        if (days < -45.0d) {
            return getStandardCometApparentMag(9.8d, 10.0d, pos);
        }
        if (days < 20.0d) {
            return getStandardCometApparentMag(10.3d, 2.8d, pos);
        }
        if (days < 110.0d) {
            return getStandardCometApparentMag(12.3d, 6.279999999999999d, pos);
        }
        return AsteroidBrightnessModel.getMagnitude(14.2d, 0.15d, pos);
    }

    private static double getStandardCometApparentMag(double absMag, double n, Orbit.OrbitInstantData pos) {
        return (5.0d * Math.log10(pos.geocentricDistance)) + absMag + (2.5d * n * Math.log10(pos.heliocentricDistance));
    }

    public int hashCode() {
        int i = 0;
        long temp = Double.doubleToLongBits(this.absoluteMagnitude);
        int hashCode = (((((int) ((temp >>> 32) ^ temp)) + 31) * 31) + (this.name == null ? 0 : this.name.hashCode())) * 31;
        if (this.orbit != null) {
            i = this.orbit.hashCode();
        }
        int result = hashCode + i;
        long temp2 = Double.doubleToLongBits(this.slope);
        return (result * 31) + ((int) ((temp2 >>> 32) ^ temp2));
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
        Comet other = (Comet) obj;
        if (Double.doubleToLongBits(this.absoluteMagnitude) != Double.doubleToLongBits(other.absoluteMagnitude)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.orbit == null) {
            if (other.orbit != null) {
                return false;
            }
        } else if (!this.orbit.equals(other.orbit)) {
            return false;
        }
        return Double.doubleToLongBits(this.slope) == Double.doubleToLongBits(other.slope);
    }
}
