package com.lavadip.skeye.astro.keplerian;

import com.lavadip.skeye.Vector2d;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.Instant;
import org.apache.commons.math3.analysis.interpolation.MicrosphereInterpolator;

public abstract class Orbit {
    private static Instant J2000 = new Instant(MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS, 1, 1, 0.5d);
    private static final double earthEccentricity = 0.01671022d;
    public static final Orbit earthOrbit = new EllipticalOrbit(new Elements(new Instant(MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS, 1, 4, 0.0d), 0.9832898881618758d, 0.0d, earthEccentricity, Math.toRadians(5.0E-5d), Math.toRadians(-11.26064d), Math.toRadians(114.20783d)));
    private static final double earthSemiMajorAxix = 1.00000011d;
    public static final Orbit sunOrbit = new Orbit(null) {
        /* class com.lavadip.skeye.astro.keplerian.Orbit.C00621 */

        @Override // com.lavadip.skeye.astro.keplerian.Orbit
        public Vector2d getPolarPosition(Instant d) {
            return null;
        }

        @Override // com.lavadip.skeye.astro.keplerian.Orbit
        public Vector3d getHeliocentricPosition(Instant d) {
            return new Vector3d(0.0f, 0.0f, 0.0f);
        }
    };
    public final Elements elems;

    public abstract Vector2d getPolarPosition(Instant instant);

    public static class OrbitInstantData {
        public final double Dec;

        /* renamed from: RA */
        public final double f48RA;
        public final double geocentricDistance;
        public final double heliocentricDistance;
        public final double phaseAngle;

        public OrbitInstantData(double rA, double dec, double heliocentricDistance2, double geocentricDistance2, double phaseAngle2) {
            this.f48RA = rA;
            this.Dec = dec;
            this.heliocentricDistance = heliocentricDistance2;
            this.geocentricDistance = geocentricDistance2;
            this.phaseAngle = phaseAngle2;
        }
    }

    public Orbit(Elements elems2) {
        this.elems = elems2;
    }

    public Vector3d getHeliocentricPosition(Instant d) {
        Vector2d polarPos = getPolarPosition(d);
        double trueAnomaly = polarPos.f14x;
        double r = polarPos.f15y;
        double u = this.elems.argOfPeriapsis + trueAnomaly;
        double L = this.elems.longitudeOfAscendingNode;
        double i = this.elems.inclination;
        double sinL = Math.sin(L);
        double cosL = Math.cos(L);
        double sinU = Math.sin(u);
        double cosU = Math.cos(u);
        double sinI = Math.sin(i);
        double cosI = Math.cos(i);
        return new Vector3d(r * ((cosU * cosL) - ((sinU * sinL) * cosI)), r * ((cosU * sinL) + (sinU * cosL * cosI)), r * sinU * sinI);
    }

    public OrbitInstantData getGeoentricPosition(Instant d) {
        return getGeoentricPosition(d, J2000);
    }

    public OrbitInstantData getGeoentricPosition(Instant d, Instant epoch) {
        Vector3d heliocentricPos = getHeliocentricPosition(d);
        Vector3d earthPos = earthOrbit.getHeliocentricPosition(d);
        Vector3d geocentricPos = heliocentricPos.sub(earthPos);
        double geocentricDistance = geocentricPos.length();
        double ecObq = obliqnessOfEcliptic(epoch);
        double phaseAngle = heliocentricPos.scalarMultiply(-1.0d).normalised().angleBetweenMag(earthPos.sub(heliocentricPos).normalised());
        Vector3d geocentricPosEq = geocentricPos.rotateAboutXaxis(ecObq);
        return new OrbitInstantData(normaliseRA(Math.atan2(geocentricPosEq.f17y, geocentricPosEq.f16x)), Math.atan2(geocentricPosEq.f18z, Math.sqrt((geocentricPosEq.f16x * geocentricPosEq.f16x) + (geocentricPosEq.f17y * geocentricPosEq.f17y))), heliocentricPos.length(), geocentricDistance, phaseAngle);
    }

    private static double normaliseRA(double ra) {
        if (ra < 0.0d) {
            return ra + 6.283185307179586d;
        }
        return ra;
    }

    private static double obliqnessOfEcliptic(Instant date) {
        return Math.toRadians(23.4393d - (3.563E-7d * date.getDaysJ2000()));
    }

    public int hashCode() {
        return (this.elems == null ? 0 : this.elems.hashCode()) + 31;
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
        Orbit other = (Orbit) obj;
        return this.elems == null ? other.elems == null : this.elems.equals(other.elems);
    }
}
