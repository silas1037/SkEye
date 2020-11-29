package com.lavadip.skeye.astro.keplerian;

import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.Vector2d;
import com.lavadip.skeye.astro.Instant;

public final class ParabolicOrbit extends Orbit {
    private static final double SQUARE_ROOT_TWO = Math.sqrt(2.0d);
    private static final double THREE_PI = 9.42477796076938d;
    private static final double THREE_PI_SQUARE_ROOT_TWO = (THREE_PI * SQUARE_ROOT_TWO);

    public ParabolicOrbit(Elements elems) {
        super(elems);
    }

    @Override // com.lavadip.skeye.astro.keplerian.Orbit
    public Vector2d getPolarPosition(Instant d) {
        double trueAnomaly;
        double q = this.elems.periapsisDistance;
        double q3by2 = Math.sqrt(Math.pow(q, 3.0d));
        double twoQ = 2.0d * q;
        double T = this.elems.epochPeriapsis.getSiderealYears();
        final double M = (THREE_PI_SQUARE_ROOT_TWO * (d.getSiderealYears() - T)) / q3by2;
        double u = AstroUtil.newtonRaphson(3.141592653589793d, new AstroUtil.DeltaFunction() {
            /* class com.lavadip.skeye.astro.keplerian.ParabolicOrbit.C00631 */

            @Override // com.lavadip.skeye.AstroUtil.DeltaFunction
            public double apply(double u) {
                double u2 = u * u;
                return (((2.0d * u2) * u) + M) / (3.0d * (1.0d + u2));
            }
        }, 50, 1.0E-12d);
        if (M < 0.0d) {
            trueAnomaly = -2.0d * Math.atan(u);
        } else {
            trueAnomaly = 2.0d * Math.atan(u);
        }
        return new Vector2d(trueAnomaly, twoQ / (1.0d + Math.cos(trueAnomaly)));
    }
}
