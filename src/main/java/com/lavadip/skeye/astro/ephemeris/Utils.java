package com.lavadip.skeye.astro.ephemeris;

import com.lavadip.skeye.astro.ephemeris.Ephemeris;
import java.util.Date;

public final class Utils {
    public static final double AU_IN_KM = 1.495978707E8d;
    private static final double EPSILON = 3.0E-8d;
    public static final double EarthR = 4.263496512454038E-5d;
    private static final double INFINITY = 1.0E10d;
    private static final double JD_1970_JAN_1 = 2440587.5d;
    public static final double JupiterEquatorialR = 4.7789450254521576E-4d;
    public static final double JupiterMeanR = 4.6732617030490934E-4d;
    public static final double JupiterPolarR = 4.468913874721347E-4d;
    public static final double MoonMeanR = 1.1614E-5d;
    private static final double RADEG = (45.0d / Math.atan(1.0d));
    public static final double SunMeanR = 0.0046491d;
    private static final int YEND = 2200;
    private static final int YSTART = 1620;

    /* renamed from: dt */
    private static final short[] f47dt = {1240, 1020, 850, 720, 620, 540, 480, 430, 370, 320, 260, 210, 160, 120, 100, 90, 90, 90, 100, 100, 110, 110, 110, 120, 120, 130, 130, 140, 150, 160, 160, 170, 170, 170, 170, 160, 137, 126, 125, 125, 120, 102, 75, 58, 57, 63, 71, 76, 79, 60, 16, -32, -54, -58, -59, -65, -27, 39, 105, 172, 212, 236, 240, 239, 243, 268, 292, 311, 332, 357, 402, 455, 505, 543, 569, 608, 660, 710, 750, 790, 830, 870, 910, 950, 990, 1030, 1070, 1110, 1150, 1190, 1230, 1270, 1310, 1350, 1390, 1430, 1470, 1510, 1550, 1590, 1630, 1670, 1710, 1750, 1790, 1830, 1870, 1910, 1950, 1990, 2030, 2070, 2110, 2150, 2190, 2230, 2270};
    static final double yday = 2.7379092633269358E-5d;

    /* renamed from: JD */
    static double m1JD(Date d) {
        return JD_1970_JAN_1 + (((double) d.getTime()) / 8.64E7d);
    }

    public static double JED(Date d) {
        double jd = m1JD(d);
        return jd + (DTT(jd) / 360.0d);
    }

    static double DTT(double JD) {
        double year = ((JD - 2451545.0d) / 365.25d) + 2000.0d;
        if (year < 1620.0d || year > 2200.0d) {
            double cent = (year - 1900.0d) / 100.0d;
            return 0.1025d + (0.301325d * cent) + (0.1248d * cent * cent);
        }
        int i = (int) Math.floor((year - 1620.0d) / 5.0d);
        double frc = ((year - 1620.0d) - ((double) (i * 5))) * 0.2d;
        return ((((double) f47dt[i]) * (1.0d - frc)) + (((double) f47dt[i + 1]) * frc)) / 2400.0d;
    }

    static double rev180(double x) {
        return x - (Math.floor((x / 360.0d) + 0.5d) * 360.0d);
    }

    static double rev360(double x) {
        return x - (Math.floor(x / 360.0d) * 360.0d);
    }

    static double frac(double x) {
        return x - Math.floor(x);
    }

    static double sind(double x) {
        return Math.sin(x / RADEG);
    }

    static double cosd(double x) {
        return Math.cos(x / RADEG);
    }

    static double atan2d(double y, double x) {
        return Math.atan2(y, x) * RADEG;
    }

    static Rect3 helpos(double v, double r, double w, double i, double n) {
        double vw = v + w;
        double svw = sind(vw);
        double cvw = cosd(vw);
        double si = sind(i);
        double ci = cosd(i);
        double sn = sind(n);
        double cn = cosd(n);
        return new Rect3(r * ((cn * cvw) - ((sn * svw) * ci)), r * ((sn * cvw) + (cn * svw * ci)), r * svw * si);
    }

    static double kepler(double m, double ex) {
        boolean converged;
        double m1 = rev180(m);
        double exan = atan2d(sind(m1), cosd(m1) - ex);
        if (ex > 0.008d) {
            double exd = ex * RADEG;
            double lim1 = 0.001d / ex;
            double adko = INFINITY;
            double denom = 1.0d - (cosd(exan) * ex);
            do {
                double dexan = (((sind(exan) * exd) + m1) - exan) / denom;
                exan += dexan;
                double adk = Math.abs(dexan);
                converged = adk < EPSILON || adk >= adko;
                adko = adk;
                if (!converged && adk > lim1) {
                    denom = 1.0d - (cosd(exan) * ex);
                    continue;
                }
            } while (!converged);
        }
        return exan;
    }

    /* renamed from: vr */
    static Pol2 m2vr(double m, double e, double a) {
        double ean = kepler(m, e);
        double x = a * (cosd(ean) - e);
        double y = Math.sqrt(1.0d - (e * e)) * a * sind(ean);
        return new Pol2(atan2d(y, x), Math.sqrt((x * x) + (y * y)));
    }

    static Rect3 P2R(MajorPlanet p) {
        return new Rect3(cosd(p.lon) * cosd(p.lat) * p.f41r, sind(p.lon) * cosd(p.lat) * p.f41r, sind(p.lat) * p.f41r);
    }

    static Rect3 R3sum(Rect3 a, Rect3 b) {
        return new Rect3(a.f44x + b.f44x, a.f45y + b.f45y, a.f46z + b.f46z);
    }

    static Rect3 R3diff(Rect3 a, Rect3 b) {
        return new Rect3(a.f44x - b.f44x, a.f45y - b.f45y, a.f46z - b.f46z);
    }

    static double sqsum(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }

    static double sqsum(double x, double y, double z) {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    static Ephemeris.PlanetData eclequ(MajorPlanet pl, double eklut) {
        double sl = sind(pl.lat);
        double cl = cosd(pl.lat);
        double x = cl * cosd(pl.lon);
        double ydash = cl * sind(pl.lon);
        double ce = cosd(eklut);
        double se = sind(eklut);
        double y = (ydash * ce) - (sl * se);
        return new Ephemeris.PlanetData(atan2d(y, x), atan2d((ydash * se) + (sl * ce), Math.sqrt((x * x) + (y * y))), pl.f41r);
    }

    static double oblecl(double JD) {
        return 23.4393d - (3.563E-7d * (JD - 2451543.5d));
    }

    static Ephemeris.PlanetData topocentric(Ephemeris.PlanetData pl, double JD, double lon, double lat) {
        double LST = (360.0d * frac(0.279072d + (0.00273790931d * (JD - 2451545.0d)))) + (frac(0.5d + JD) * 360.0d) + lon;
        double erCosLat = 4.263496512454038E-5d * cosd(lat);
        return R3diff(pl.toRect3(), new Rect3(erCosLat * cosd(LST), erCosLat * sind(LST), 4.263496512454038E-5d * sind(lat))).toPlanetData();
    }

    static LoLa prec(double JD0, LoLa rade0, double JD) {
        double tau0 = (JD0 - 2415020.313d) * yday;
        double tau = (JD - JD0) * yday;
        double zeta = tau * ((((5.0E-6d * tau) + 8.39E-5d) * tau) + 0.6400694d + (3.878E-4d * tau0));
        double z = zeta + (tau * tau * (2.197E-4d + (3.0E-7d * tau)));
        double theta = tau * ((((-1.17E-5d * tau) - 1.183E-4d) * tau) + (0.5568561d - (2.369E-4d * tau0)));
        double sina0z = sind(rade0.f40lo + zeta);
        double cosa0z = cosd(rade0.f40lo + zeta);
        double sindek = sind(rade0.f39la);
        double cosdek = cosd(rade0.f39la);
        double sinthe = sind(theta);
        double costhe = cosd(theta);
        double a = cosdek * sina0z;
        double b = ((costhe * cosdek) * cosa0z) - (sinthe * sindek);
        return new LoLa(atan2d(a, b) + z, atan2d((sinthe * cosdek * cosa0z) + (costhe * sindek), Math.sqrt((a * a) + (b * b))));
    }
}
