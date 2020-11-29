package com.lavadip.skeye.astro.sgp4v;

import com.lavadip.skeye.Vector3d;

public final class Sgp4Data {
    static final double vkmpersec = 7.905372989414837d;
    private final Vector3d posn;
    private final Vector3d vel;

    public Sgp4Data(Vector3d posn2, Vector3d vel2) {
        this.posn = posn2;
        this.vel = vel2;
    }

    public String toString() {
        return this.posn.toString();
    }

    public Vector3d getPosn() {
        return this.posn;
    }

    public Vector3d getVel() {
        return this.vel;
    }

    public double getVelKmPerSec() {
        return this.vel.length() * vkmpersec;
    }

    public double getAltitudeKm() {
        return (this.posn.length() - 1.0d) * 6378.137d;
    }
}
