package com.lavadip.skeye.astro.keplerian;

import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.Vector2d;
import com.lavadip.skeye.astro.Instant;

public final class EllipticalOrbit extends Orbit {
    static final /* synthetic */ boolean $assertionsDisabled = (!EllipticalOrbit.class.desiredAssertionStatus());

    @Override // com.lavadip.skeye.astro.keplerian.Orbit
    public Vector2d getPolarPosition(Instant d) {
        final double e = this.elems.eccentricity;
        if ($assertionsDisabled || e < 1.0d) {
            double semiMajorAxis = this.elems.periapsisDistance / (1.0d - e);
            double P = Math.sqrt(Math.pow(semiMajorAxis, 3.0d));
            double l = semiMajorAxis * (1.0d - (e * e));
            double T = this.elems.epochPeriapsis.getSiderealYears();
            final double M = this.elems.meanAnomalyAtEpoch + ((6.283185307179586d * (d.getSiderealYears() - T)) / P);
            double E = AstroUtil.newtonRaphson(3.141592653589793d, new AstroUtil.DeltaFunction() {
                /* class com.lavadip.skeye.astro.keplerian.EllipticalOrbit.C00601 */

                @Override // com.lavadip.skeye.AstroUtil.DeltaFunction
                public double apply(double x) {
                    return x - (((x - (e * Math.sin(x))) - M) / (1.0d - (e * Math.cos(x))));
                }
            }, 50, 1.0E-12d);
            double trueAnomaly = 2.0d * Math.atan2(Math.sin(E / 2.0d) * Math.sqrt((1.0d + e) / (1.0d - e)), Math.cos(E / 2.0d));
            return new Vector2d(trueAnomaly, l / (1.0d + (Math.cos(trueAnomaly) * e)));
        }
        throw new AssertionError();
    }

    public EllipticalOrbit(Elements elems) {
        super(elems);
    }
}
