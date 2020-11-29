package com.lavadip.skeye.astro;

public final class Instant {
    private final int day;
    private final double dayFraction;
    private final int month;
    private final int year;

    public Instant(int year2, int month2, int day2, double dayFraction2) {
        this.year = year2;
        this.month = month2;
        this.day = day2;
        this.dayFraction = dayFraction2;
    }

    public Instant(int year2, int month2, double day2) {
        this.year = year2;
        this.month = month2;
        this.day = (int) day2;
        this.dayFraction = day2 - ((double) this.day);
    }

    public double getJulianDay() {
        int Y = this.year;
        int M = this.month;
        return ((double) ((long) ((((this.day - 32075) + ((((Y + 4800) + ((M - 14) / 12)) * 1461) / 4)) + ((((M - 2) - (((M - 14) / 12) * 12)) * 367) / 12)) - (((((Y + 4900) + ((M - 14) / 12)) / 100) * 3) / 4)))) + (this.dayFraction - 0.5d);
    }

    public double getSiderealYears() {
        return 0.002737803091986241d * getJulianDay();
    }

    public double getDaysJ2000() {
        long y = (long) this.year;
        int m = this.month;
        return ((double) (((((367 * y) - ((7 * (((long) ((m + 9) / 12)) + y)) / 4)) + ((long) ((m * 275) / 9))) + ((long) this.day)) - 730530)) + (this.dayFraction - 0.5d);
    }

    public int hashCode() {
        int result = this.day + 31;
        long temp = Double.doubleToLongBits(this.dayFraction);
        return (((((result * 31) + ((int) ((temp >>> 32) ^ temp))) * 31) + this.month) * 31) + this.year;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Instant other = (Instant) obj;
        if (this.day != other.day) {
            return false;
        }
        if (Double.doubleToLongBits(this.dayFraction) != Double.doubleToLongBits(other.dayFraction)) {
            return false;
        }
        if (this.month != other.month) {
            return false;
        }
        return this.year == other.year;
    }

    public String formatYYYYMMDD() {
        return String.format("%4d %2d %2d UT", Integer.valueOf(this.year), Integer.valueOf(this.month), Integer.valueOf(this.day));
    }
}
