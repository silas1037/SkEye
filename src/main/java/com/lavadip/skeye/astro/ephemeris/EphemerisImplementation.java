package com.lavadip.skeye.astro.ephemeris;

import com.lavadip.skeye.astro.ephemeris.Ephemeris;
import com.lavadip.skeye.catalog.CatalogManager;
import java.util.Date;

public final class EphemerisImplementation implements Ephemeris {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$lavadip$skeye$astro$ephemeris$Ephemeris$Planet = null;
    private static final double EQUINOX_OF_DAY = -1111.0d;
    private static final double JD_J2000 = 2451545.0d;
    private static double JDequinox = JD_J2000;

    static /* synthetic */ int[] $SWITCH_TABLE$com$lavadip$skeye$astro$ephemeris$Ephemeris$Planet() {
        int[] iArr = $SWITCH_TABLE$com$lavadip$skeye$astro$ephemeris$Ephemeris$Planet;
        if (iArr == null) {
            iArr = new int[Ephemeris.Planet.values().length];
            try {
                iArr[Ephemeris.Planet.JUPITER.ordinal()] = 6;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Ephemeris.Planet.MARS.ordinal()] = 5;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Ephemeris.Planet.MERCURY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Ephemeris.Planet.MOON.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Ephemeris.Planet.NEPTUNE.ordinal()] = 9;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Ephemeris.Planet.SATURN.ordinal()] = 7;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[Ephemeris.Planet.SUN.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[Ephemeris.Planet.URANUS.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[Ephemeris.Planet.VENUS.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            $SWITCH_TABLE$com$lavadip$skeye$astro$ephemeris$Ephemeris$Planet = iArr;
        }
        return iArr;
    }

    @Override // com.lavadip.skeye.astro.ephemeris.Ephemeris
    public void setJequinox(double equinox) {
        JDequinox = JD_J2000 + ((equinox - 2000.0d) * 365.25d);
    }

    @Override // com.lavadip.skeye.astro.ephemeris.Ephemeris
    public void setEquinoxOfDay() {
        JDequinox = EQUINOX_OF_DAY;
    }

    private static Ephemeris.PlanetData precessData(Ephemeris.PlanetData pl, double JD) {
        if (JDequinox == EQUINOX_OF_DAY) {
            return pl;
        }
        LoLa RADecPrecessed = Utils.prec(JD, new LoLa(pl.f37RA, pl.Dec), JDequinox);
        return new Ephemeris.PlanetData(RADecPrecessed.f40lo, RADecPrecessed.f39la, pl.f38r);
    }

    private static Ephemeris.PlanetData getPlanetPositionEquinoxOfDay(double JED, Ephemeris.Planet planet) {
        MajorPlanet pl;
        switch ($SWITCH_TABLE$com$lavadip$skeye$astro$ephemeris$Ephemeris$Planet()[planet.ordinal()]) {
            case 1:
                pl = MajorPlanet.Sun(JED);
                break;
            case 2:
                pl = MajorPlanet.Moon(JED);
                break;
            case 3:
                pl = MajorPlanet.geocentric(MajorPlanet.Mercury(JED), JED);
                break;
            case 4:
                pl = MajorPlanet.geocentric(MajorPlanet.Venus(JED), JED);
                break;
            case 5:
                pl = MajorPlanet.geocentric(MajorPlanet.Mars(JED), JED);
                break;
            case 6:
                pl = MajorPlanet.geocentric(MajorPlanet.Jupiter(JED), JED);
                break;
            case CatalogManager.CATALOG_ID_SATELLITE:
                pl = MajorPlanet.geocentric(MajorPlanet.Saturn(JED), JED);
                break;
            case 8:
                pl = MajorPlanet.geocentric(MajorPlanet.Uranus(JED), JED);
                break;
            case 9:
                pl = MajorPlanet.geocentric(MajorPlanet.Neptune(JED), JED);
                break;
            default:
                return null;
        }
        return Utils.eclequ(pl, Utils.oblecl(JED));
    }

    @Override // com.lavadip.skeye.astro.ephemeris.Ephemeris
    public Ephemeris.PlanetData getPlanetPosition(Date targetDate, Ephemeris.Planet planet) {
        double JED = Utils.JED(targetDate);
        return precessData(getPlanetPositionEquinoxOfDay(JED, planet), JED);
    }

    @Override // com.lavadip.skeye.astro.ephemeris.Ephemeris
    public Ephemeris.PlanetData getPlanetPosition(Date targetDate, Ephemeris.Planet planet, double longitude, double latitude) {
        double JED = Utils.JED(targetDate);
        return precessData(Utils.topocentric(getPlanetPositionEquinoxOfDay(JED, planet), Utils.m1JD(targetDate), longitude, latitude), JED);
    }

    private static Ephemeris.PlanetData[] getPlanetPositionsEquinoxOfDay(double JED) {
        Ephemeris.PlanetData[] pdat = new Ephemeris.PlanetData[9];
        MajorPlanet sun = MajorPlanet.Sun(JED);
        MajorPlanet[] pl = {sun, MajorPlanet.Moon(JED), MajorPlanet.geocentric(MajorPlanet.Mercury(JED), sun), MajorPlanet.geocentric(MajorPlanet.Venus(JED), sun), MajorPlanet.geocentric(MajorPlanet.Mars(JED), sun), MajorPlanet.geocentric(MajorPlanet.Jupiter(JED), sun), MajorPlanet.geocentric(MajorPlanet.Saturn(JED), sun), MajorPlanet.geocentric(MajorPlanet.Uranus(JED), sun), MajorPlanet.geocentric(MajorPlanet.Neptune(JED), sun)};
        double oe = Utils.oblecl(JED);
        for (int i = 0; i < 9; i++) {
            pdat[i] = Utils.eclequ(pl[i], oe);
        }
        return pdat;
    }

    @Override // com.lavadip.skeye.astro.ephemeris.Ephemeris
    public Ephemeris.PlanetData[] getPlanetPositions(Date targetDate) {
        double JED = Utils.JED(targetDate);
        Ephemeris.PlanetData[] plposs = getPlanetPositionsEquinoxOfDay(JED);
        for (int i = 0; i < 9; i++) {
            plposs[i] = precessData(plposs[i], JED);
        }
        return plposs;
    }

    @Override // com.lavadip.skeye.astro.ephemeris.Ephemeris
    public Ephemeris.PlanetData[] getPlanetPositions(Date targetDate, double longitude, double latitude) {
        double JED = Utils.JED(targetDate);
        Ephemeris.PlanetData[] plposs = getPlanetPositionsEquinoxOfDay(JED);
        double JD = Utils.m1JD(targetDate);
        for (int i = 0; i < 5; i++) {
            plposs[i] = Utils.topocentric(plposs[i], JD, longitude, latitude);
        }
        for (int i2 = 0; i2 < 9; i2++) {
            plposs[i2] = precessData(plposs[i2], JED);
        }
        return plposs;
    }

    @Override // com.lavadip.skeye.astro.ephemeris.Ephemeris
    public Ephemeris.PlanetData[] getSunMoonPositions(Date targetDate, double longitude, double latitude) {
        double JED = Utils.JED(targetDate);
        Ephemeris.PlanetData[] plposs = getSunMoonPositionsEquinoxOfDay(JED);
        double JD = Utils.m1JD(targetDate);
        for (int i = 0; i < 2; i++) {
            plposs[i] = Utils.topocentric(plposs[i], JD, longitude, latitude);
        }
        for (int i2 = 0; i2 < 2; i2++) {
            plposs[i2] = precessData(plposs[i2], JED);
        }
        return plposs;
    }

    private static Ephemeris.PlanetData[] getSunMoonPositionsEquinoxOfDay(double JED) {
        Ephemeris.PlanetData[] pdat = new Ephemeris.PlanetData[2];
        MajorPlanet[] pl = {MajorPlanet.Sun(JED), MajorPlanet.Moon(JED)};
        double oe = Utils.oblecl(JED);
        for (int i = 0; i < 2; i++) {
            pdat[i] = Utils.eclequ(pl[i], oe);
        }
        return pdat;
    }
}
