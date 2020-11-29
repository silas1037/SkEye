package com.lavadip.skeye.astro.ephemeris;

import java.util.Date;

public interface Ephemeris {

    public enum Planet {
        SUN,
        MOON,
        MERCURY,
        VENUS,
        MARS,
        JUPITER,
        SATURN,
        URANUS,
        NEPTUNE
    }

    PlanetData getPlanetPosition(Date date, Planet planet);

    PlanetData getPlanetPosition(Date date, Planet planet, double d, double d2);

    PlanetData[] getPlanetPositions(Date date);

    PlanetData[] getPlanetPositions(Date date, double d, double d2);

    PlanetData[] getSunMoonPositions(Date date, double d, double d2);

    void setEquinoxOfDay();

    void setJequinox(double d);

    public static class PlanetData {
        public final double Dec;

        /* renamed from: RA */
        public final double f37RA;

        /* renamed from: r */
        public final double f38r;

        public PlanetData(double RA, double Dec2, double r) {
            while (RA < 0.0d) {
                RA += 360.0d;
            }
            this.f37RA = RA;
            this.Dec = Dec2;
            this.f38r = r;
        }

        public Rect3 toRect3() {
            return new Rect3(this.f38r * Utils.cosd(this.f37RA) * Utils.cosd(this.Dec), this.f38r * Utils.sind(this.f37RA) * Utils.cosd(this.Dec), this.f38r * Utils.sind(this.Dec));
        }
    }
}
