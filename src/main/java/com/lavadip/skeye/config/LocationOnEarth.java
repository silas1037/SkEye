package com.lavadip.skeye.config;

import android.hardware.GeomagneticField;

public class LocationOnEarth {
    public final float altitude;
    public final float latitude;
    public final float longitude;

    public LocationOnEarth(float latitude2, float longitude2, float altitude2) {
        this.latitude = latitude2;
        this.longitude = longitude2;
        this.altitude = altitude2;
    }

    public GeomagneticField getGeoMagneticField() {
        return new GeomagneticField((float) Math.toDegrees((double) this.latitude), (float) Math.toDegrees((double) this.longitude), this.altitude, System.currentTimeMillis());
    }
}
