package com.lavadip.skeye.astro.ephemeris;

import com.lavadip.skeye.astro.ephemeris.Ephemeris;

/* access modifiers changed from: package-private */
public final class Rect3 {

    /* renamed from: x */
    final double f44x;

    /* renamed from: y */
    final double f45y;

    /* renamed from: z */
    final double f46z;

    public Rect3() {
        this.f46z = 0.0d;
        this.f45y = 0.0d;
        this.f44x = 0.0d;
    }

    public Rect3(double x, double y, double z) {
        this.f44x = x;
        this.f45y = y;
        this.f46z = z;
    }

    public Ephemeris.PlanetData toPlanetData() {
        return new Ephemeris.PlanetData(Utils.atan2d(this.f45y, this.f44x), Utils.atan2d(this.f46z, Math.sqrt((this.f44x * this.f44x) + (this.f45y * this.f45y))), Math.sqrt((this.f44x * this.f44x) + (this.f45y * this.f45y) + (this.f46z * this.f46z)));
    }

    public String toString() {
        return String.format("[%f, %f, %f] Length = %f", Double.valueOf(this.f44x), Double.valueOf(this.f45y), Double.valueOf(this.f46z), Double.valueOf(Utils.sqsum(this.f44x, this.f45y, this.f46z)));
    }
}
