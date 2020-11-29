package com.lavadip.skeye.astro;

import com.lavadip.skeye.Vector3d;
import java.util.Date;

/* access modifiers changed from: package-private */
public final class Precession {

    /* renamed from: A */
    final double f31A = (0.5d + (0.125d * (this.f33X2 + this.f36Y2)));

    /* renamed from: X */
    final double f32X;

    /* renamed from: X2 */
    final double f33X2 = (this.f32X * this.f32X);

    /* renamed from: XY */
    final double f34XY = (this.f32X * this.f35Y);

    /* renamed from: Y */
    final double f35Y;

    /* renamed from: Y2 */
    final double f36Y2 = (this.f35Y * this.f35Y);

    /* access modifiers changed from: package-private */
    public static final class UTCDate {
        static final Date J2000Java = makeJavaUTCDate(100, 0, 1, 12, 0, 0);
        final int day;
        final double daysSince2000 = getDaysSince2000();
        final int hours;
        final int mins;
        final int month;
        final int secs;
        final int year;

        UTCDate(int year2, int month2, int day2, int hours2, int mins2, int secs2) {
            this.year = year2;
            this.month = month2;
            this.day = day2;
            this.hours = hours2;
            this.mins = mins2;
            this.secs = secs2;
        }

        private double getDaysSince2000() {
            return ((double) (makeJavaUTCDate(this.year, this.month, this.day, this.hours, this.mins, this.secs).getTime() - J2000Java.getTime())) / 8.64E7d;
        }

        UTCDate(Date localDate) {
            Date greenWhichDate = new Date(localDate.getTime() + (((long) localDate.getTimezoneOffset()) * 60000));
            this.year = greenWhichDate.getYear();
            this.month = greenWhichDate.getMonth();
            this.day = greenWhichDate.getDate();
            this.hours = greenWhichDate.getHours();
            this.mins = greenWhichDate.getMinutes();
            this.secs = greenWhichDate.getSeconds();
        }

        static Date makeJavaUTCDate(int year2, int month2, int day2, int hours2, int mins2, int secs2) {
            return new Date(Date.UTC(year2, month2, day2, hours2, mins2, secs2));
        }

        public String toString() {
            return String.format("Y:%d M:%d D:%d h:%d m:%d s:%d", Integer.valueOf(this.year), Integer.valueOf(this.month), Integer.valueOf(this.day), Integer.valueOf(this.hours), Integer.valueOf(this.mins), Integer.valueOf(this.secs));
        }
    }

    Precession(UTCDate dateNow) {
        double days = dateNow.daysSince2000;
        double omegaNow = 2.182439196616d - (9.242E-4d * days);
        this.f32X = (2.6603E-7d * days) - (3.32E-5d * Math.sin(omegaNow));
        this.f35Y = (-8.14E-14d * days * days) + (4.46E-5d * Math.cos(omegaNow));
    }

    /* access modifiers changed from: package-private */
    public Vector3d applyRotationCIRS(Vector3d vec) {
        return new Vector3d((double) ((float) (vec.f16x - (vec.f18z * this.f32X))), (double) ((float) (vec.f17y - (vec.f18z * this.f35Y))), (double) ((float) ((vec.f16x * this.f32X) + (vec.f17y * this.f35Y) + vec.f18z)), true);
    }

    /* access modifiers changed from: package-private */
    public float[] getTransformMatrix() {
        return new float[]{(float) (1.0d - (this.f36Y2 * this.f31A)), (float) this.f35Y, (float) (-(this.f34XY * this.f31A)), 0.0f, (float) (-this.f35Y), (float) (1.0d - ((this.f33X2 + this.f36Y2) * this.f31A)), (float) (-this.f32X), 0.0f, (float) (-(this.f34XY * this.f31A)), (float) this.f32X, (float) (1.0d - (this.f33X2 * this.f31A)), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }
}
