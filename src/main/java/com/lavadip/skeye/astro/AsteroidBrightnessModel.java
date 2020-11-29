package com.lavadip.skeye.astro;

import com.lavadip.skeye.astro.keplerian.Orbit;

public final class AsteroidBrightnessModel {
    static double getMagnitude(double H, double G, Orbit.OrbitInstantData orbData) {
        double alpha = orbData.phaseAngle;
        double t = Math.tan(alpha / 2.0d);
        double sa = Math.sin(alpha);
        double W = Math.exp(-90.56d * t * t);
        double denom = (0.119d + (1.341d * sa)) - ((0.754d * sa) * sa);
        return (5.0d * Math.log10(orbData.heliocentricDistance * orbData.geocentricDistance)) + (H - (2.5d * Math.log10(((1.0d - G) * ((W * (1.0d - ((0.986d * sa) / denom))) + ((1.0d - W) * Math.exp(-3.332d * Math.pow(t, 0.631d))))) + (G * ((W * (1.0d - ((0.238d * sa) / denom))) + ((1.0d - W) * Math.exp(-1.862d * Math.pow(t, 1.218d))))))));
    }
}
