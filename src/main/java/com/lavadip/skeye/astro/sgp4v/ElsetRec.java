package com.lavadip.skeye.astro.sgp4v;

public final class ElsetRec {

    /* renamed from: a */
    protected double f49a;
    protected double alta;
    protected double altp;
    protected double argpo;
    protected double argpp;
    protected double bstar;
    protected DeepSpaceType dsvalues = new DeepSpaceType();
    protected double ecco;

    /* renamed from: ep */
    protected double f50ep;
    protected double epochdays;
    protected int epochtynumrev;
    protected int epochyr;
    protected double eptime;
    protected int error;
    protected double inclo;
    protected int init;
    protected String intDesgination;

    /* renamed from: mo */
    protected double f51mo;

    /* renamed from: mp */
    protected double f52mp;
    protected NearEarthType nevalues = new NearEarthType();

    /* renamed from: no */
    protected double f53no;
    protected double omegao;
    protected double omegap;
    protected int satnum;
    protected double xincp;

    static final class DeepSpaceType {
        protected double atime = 0.0d;
        protected double d2201 = 0.0d;
        protected double d2211 = 0.0d;
        protected double d3210 = 0.0d;
        protected double d3222 = 0.0d;
        protected double d4410 = 0.0d;
        protected double d4422 = 0.0d;
        protected double d5220 = 0.0d;
        protected double d5232 = 0.0d;
        protected double d5421 = 0.0d;
        protected double d5433 = 0.0d;
        protected double dedt = 0.0d;
        protected double del1 = 0.0d;
        protected double del2 = 0.0d;
        protected double del3 = 0.0d;
        protected double didt = 0.0d;
        protected double dmdt = 0.0d;
        protected double dnodt = 0.0d;
        protected double domdt = 0.0d;

        /* renamed from: e3 */
        protected double f54e3 = 0.0d;
        protected double ee2 = 0.0d;
        protected double gsto = 0.0d;
        protected int irez = 0;
        protected double se2 = 0.0d;
        protected double se3 = 0.0d;
        protected double sgh2 = 0.0d;
        protected double sgh3 = 0.0d;
        protected double sgh4 = 0.0d;
        protected double sh2 = 0.0d;
        protected double sh3 = 0.0d;
        protected double si2 = 0.0d;
        protected double si3 = 0.0d;
        protected double sl2 = 0.0d;
        protected double sl3 = 0.0d;
        protected double sl4 = 0.0d;
        protected double xfact = 0.0d;
        protected double xgh2 = 0.0d;
        protected double xgh3 = 0.0d;
        protected double xgh4 = 0.0d;
        protected double xh2 = 0.0d;
        protected double xh3 = 0.0d;
        protected double xi2 = 0.0d;
        protected double xi3 = 0.0d;
        protected double xl2 = 0.0d;
        protected double xl3 = 0.0d;
        protected double xl4 = 0.0d;
        protected double xlamo = 0.0d;
        protected double xli = 0.0d;
        protected double xni = 0.0d;
        protected double zmol = 0.0d;
        protected double zmos = 0.0d;
    }

    /* access modifiers changed from: package-private */
    public static final class NearEarthType {
        protected double argpdot = 0.0d;
        protected double aycof = 0.0d;
        protected double cc1 = 0.0d;
        protected double cc4 = 0.0d;
        protected double cc5 = 0.0d;
        protected double con41 = 0.0d;

        /* renamed from: d2 */
        protected double f55d2 = 0.0d;

        /* renamed from: d3 */
        protected double f56d3 = 0.0d;

        /* renamed from: d4 */
        protected double f57d4 = 0.0d;
        protected double delmo = 0.0d;
        protected double eta = 0.0d;
        protected int isimp = 0;
        protected double mdot = 0.0d;
        protected int method = 0;
        protected double omegacf = 0.0d;
        protected double omegadot = 0.0d;
        protected double omgcof = 0.0d;
        protected double sinmao = 0.0d;

        /* renamed from: t */
        protected double f58t = 0.0d;
        protected double t2cof = 0.0d;
        protected double t3cof = 0.0d;
        protected double t4cof = 0.0d;
        protected double t5cof = 0.0d;
        protected double x1mth2 = 0.0d;
        protected double x7thm1 = 0.0d;
        protected double xlcof = 0.0d;
        protected double xmcof = 0.0d;
    }
}
