package com.lavadip.skeye.astro.keplerian;

import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.Vector2d;
import com.lavadip.skeye.astro.Instant;

public final class HyperbolicOrbit extends Orbit {
    static final /* synthetic */ boolean $assertionsDisabled = (!HyperbolicOrbit.class.desiredAssertionStatus());

    public HyperbolicOrbit(Elements elems) {
        super(elems);
    }

    @Override // com.lavadip.skeye.astro.keplerian.Orbit
    public Vector2d getPolarPosition(Instant d) {
        double trueAnomaly;
        final double e = this.elems.eccentricity;
        if ($assertionsDisabled || e > 1.0d) {
            double a = this.elems.periapsisDistance / (e - 1.0d);
            double a3by2 = Math.sqrt(Math.pow(a, 3.0d));
            double l = a * ((e * e) - 1.0d);
            double T = this.elems.epochPeriapsis.getSiderealYears();
            double t = d.getSiderealYears();
            final double Q = (6.283185307179586d * (t - T)) / a3by2;
            double coshE = Math.cosh(AstroUtil.newtonRaphson(3.141592653589793d, new AstroUtil.DeltaFunction() {
                /* class com.lavadip.skeye.astro.keplerian.HyperbolicOrbit.C00611 */

                @Override // com.lavadip.skeye.AstroUtil.DeltaFunction
                public double apply(double E) {
                    double cE = Math.cosh(E);
                    return (Q + (e * ((E * cE) - Math.sinh(E)))) / ((e * cE) - 1.0d);
                }
            }, 50, 1.0E-12d));
            double trueAnomalyAbs = Math.acos((e - coshE) / ((e * coshE) - 1.0d));
            if (t > T) {
                trueAnomaly = trueAnomalyAbs;
            } else {
                trueAnomaly = -trueAnomalyAbs;
            }
            return new Vector2d(trueAnomaly, l / (1.0d + (Math.cos(trueAnomaly) * e)));
        }
        throw new AssertionError();
    }
}
