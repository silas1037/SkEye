package com.lavadip.skeye.astro;

import com.lavadip.skeye.AstroUtil;

final class PlanetInfo {
    double MeanAnomaly;
    private final double ecl;
    private double mLatEcl;
    private double mLongEcl;

    /* renamed from: mR */
    private final double f29mR;

    public PlanetInfo(double N, double i, double w, double a, double e, double M, double d, double ecl2) {
        this.ecl = ecl2;
        this.MeanAnomaly = M;
        double E1 = M + (Math.sin(M) * e * (1.0d + (Math.cos(M) * e)));
        int iterations = 0;
        do {
            E1 -= ((E1 - (Math.sin(E1) * e)) - M) / (1.0d - (Math.cos(E1) * e));
            iterations++;
            if (Math.abs(E1 - E1) <= 1.0E-7d) {
                break;
            }
        } while (iterations < 1000);
        double xv = a * (Math.cos(E1) - e);
        double yv = a * Math.sin(E1) * Math.sqrt(1.0d - (e * e));
        double v = Math.atan2(yv, xv);
        double r = Math.sqrt((xv * xv) + (yv * yv));
        this.f29mR = r;
        double T1 = Math.sin(v + w) * Math.cos(i);
        double T2 = Math.cos(v + w);
        double xh = r * ((Math.cos(N) * T2) - (Math.sin(N) * T1));
        double yh = r * ((Math.sin(N) * T2) + (Math.cos(N) * T1));
        double lonEcl = Math.atan2(yh, xh);
        double latEcl = Math.atan2(r * Math.sin(v + w) * Math.sin(i), Math.sqrt((xh * xh) + (yh * yh)));
        this.mLongEcl = lonEcl + Math.toRadians(3.82394E-5d * (-d));
        this.mLatEcl = latEcl;
    }

    /* access modifiers changed from: package-private */
    public double[] getPlanetEquatorialPos(PlanetInfo sun) {
        double xh = this.f29mR * Math.cos(this.mLongEcl) * Math.cos(this.mLatEcl);
        double yh = this.f29mR * Math.sin(this.mLongEcl) * Math.cos(this.mLatEcl);
        double zh = this.f29mR * Math.sin(this.mLatEcl);
        double xs = sun.f29mR * Math.cos(sun.mLongEcl);
        double yg = yh + (sun.f29mR * Math.sin(sun.mLongEcl));
        double cosEcl = Math.cos(this.ecl);
        double sinEcl = Math.sin(this.ecl);
        return getRADec(xh + xs, (yg * cosEcl) - (zh * sinEcl), (yg * sinEcl) + (zh * cosEcl));
    }

    /* access modifiers changed from: package-private */
    public double[] getSunPosition() {
        double xs = Math.cos(this.mLongEcl);
        double ys = Math.sin(this.mLongEcl);
        return getRADec(xs, ys * Math.cos(this.ecl), ys * Math.sin(this.ecl));
    }

    private static double[] getRADec(double xe, double ye, double ze) {
        double RA = Math.atan2(ye, xe);
        return new double[]{AstroUtil.makeAnglePositive(RA), Math.atan2(ze, Math.sqrt((xe * xe) + (ye * ye)))};
    }

    public void addPerturbationToJupiter(PlanetInfo saturn) {
        double Mj = this.MeanAnomaly;
        double Ms = saturn.MeanAnomaly;
        this.mLongEcl += Math.toRadians(((((((-0.332d * Math.sin(((2.0d * Mj) - (5.0d * Ms)) - Math.toRadians(67.6d))) - (0.056d * Math.sin(((2.0d * Mj) - (2.0d * Ms)) + Math.toRadians(21.0d)))) + (0.042d * Math.sin(((3.0d * Mj) - (5.0d * Ms)) + Math.toRadians(21.0d)))) - (0.036d * Math.sin(Mj - (2.0d * Ms)))) + (0.022d * Math.cos(Mj - Ms))) + (0.023d * Math.sin(((2.0d * Mj) - (3.0d * Ms)) + Math.toRadians(52.0d)))) - (0.016d * Math.sin((Mj - (5.0d * Ms)) - Math.toRadians(69.0d))));
    }

    public void addPerturbationToSaturn(PlanetInfo jupiter) {
        double Mj = jupiter.MeanAnomaly;
        double Ms = this.MeanAnomaly;
        this.mLongEcl += Math.toRadians(((0.812d * Math.sin(((2.0d * Mj) - (5.0d * Ms)) - Math.toRadians(67.6d))) - (0.229d * Math.cos(((2.0d * Mj) - (4.0d * Ms)) - Math.toRadians(2.0d)))) + (0.119d * Math.sin((Mj - (2.0d * Ms)) - Math.toRadians(3.0d))) + (0.046d * Math.sin(((2.0d * Mj) - (6.0d * Ms)) - Math.toRadians(69.0d))) + (0.014d * Math.sin((Mj - (3.0d * Ms)) + Math.toRadians(32.0d))));
        this.mLatEcl += Math.toRadians((-0.02d * Math.cos(((2.0d * Mj) - (4.0d * Ms)) - Math.toRadians(2.0d))) + (0.018d * Math.sin(((2.0d * Mj) - (6.0d * Ms)) - Math.toRadians(49.0d))));
    }

    public void addPerturbationToUranus(PlanetInfo saturn, PlanetInfo jupiter) {
        double Mu = this.MeanAnomaly;
        double Mj = jupiter.MeanAnomaly;
        double Ms = saturn.MeanAnomaly;
        this.mLongEcl += Math.toRadians(((0.04d * Math.sin((Ms - (2.0d * Mu)) + Math.toRadians(6.0d))) + (0.035d * Math.sin((Ms - (3.0d * Mu)) + Math.toRadians(33.0d)))) - (0.015d * Math.sin((Mj - Mu) + Math.toRadians(20.0d))));
    }
}
