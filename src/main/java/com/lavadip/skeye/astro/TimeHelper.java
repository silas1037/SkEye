package com.lavadip.skeye.astro;

import java.util.Date;

public final class TimeHelper {
    public static double lmst(Date localDate, double observerLongitude) {
        int offset = localDate.getTimezoneOffset();
        long imjd = mjd(localDate);
        double ut1 = ((double) ((localDate.getSeconds() + ((localDate.getMinutes() + offset) * 60)) + (localDate.getHours() * 3600))) / 86400.0d;
        if (ut1 > 1.0d) {
            ut1 -= 1.0d;
            imjd++;
        }
        double tu = ((((double) imjd) + ut1) - 51544.5d) / 36525.0d;
        double LST = (6.283185307179586d * ut1) + (((24110.54841d + ((8640184.812866d + ((0.093104d - (6.2E-6d * tu)) * tu)) * tu)) * 3.141592653589793d) / 43200.0d) + observerLongitude;
        while (LST < 0.0d) {
            LST += 6.283185307179586d;
        }
        while (LST > 6.283185307179586d) {
            LST -= 6.283185307179586d;
        }
        return LST;
    }

    public static long mjd(Date date) {
        long jm;
        long month = (long) (date.getMonth() + 1);
        long day = (long) date.getDate();
        long year = (long) (date.getYear() + 1900);
        long jy = year;
        if (jy == 0) {
            return 0;
        }
        if (jy < 0) {
            jy++;
        }
        if (month > 2) {
            jm = month + 1;
        } else {
            jy--;
            jm = month + 13;
        }
        long jday = (long) (Math.floor(365.25d * ((double) jy)) + Math.floor(30.6001d * ((double) jm)) + ((double) day) + 1720995.0d);
        if ((31 * ((12 * year) + month)) + day >= 588829) {
            long ja = (long) (0.01d * ((double) jy));
            jday += (2 - ja) + ((long) (0.25d * ((double) ja)));
        }
        return jday - 2400001;
    }
}
