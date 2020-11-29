package com.lavadip.skeye.astro.sgp4v;

import com.lavadip.skeye.Vector2d;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.sgp4v.ElsetRec;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;
import org.apache.commons.math3.analysis.interpolation.MicrosphereInterpolator;

public final class Sgp4Unit {
    public static final TimeZone GMT_TZ = TimeZone.getTimeZone("GMT");
    private static final boolean HELP = false;
    private static final double SMALL_VAL = 1.5E-12d;
    private static final double fasx2 = 0.13130908d;
    private static final double fasx4 = 2.8843198d;
    private static final double fasx6 = 0.37448087d;
    private static final double g22 = 5.7686396d;
    private static final double g32 = 0.95240898d;
    private static final double g44 = 1.8014998d;
    private static final double g52 = 1.050833d;
    private static final double g54 = 4.4108898d;

    /* renamed from: pi */
    private static final double f59pi = 3.141592653589793d;
    private static final double rad = 57.2957795130823d;
    private static final double rptim = 0.0043752690880113d;
    private static final double step2 = 259200.0d;
    private static final double stepn = -720.0d;
    private static final double stepp = 720.0d;
    private static final double twopi = 6.283185307179586d;
    static final double x2o3 = 0.6666666666666666d;

    /* renamed from: ao */
    private double f60ao = 0.0d;
    private double argpm = 0.0d;
    private double cnodm = 0.0d;
    private double con42 = 0.0d;
    private double cosim = 0.0d;
    private double cosio = 0.0d;
    private double cosio2 = 0.0d;
    private double cosomm = 0.0d;
    private double day = 0.0d;
    private double dndt = 0.0d;
    private double eccsq = 0.0d;

    /* renamed from: em */
    private double f61em = 0.0d;
    private double emsq = 0.0d;
    private double gam = 0.0d;
    private double inclm = 0.0d;

    /* renamed from: mm */
    private double f62mm = 0.0d;

    /* renamed from: nm */
    private double f63nm = 0.0d;
    private double omegam = 0.0d;
    private double omeosq = 0.0d;
    private double posq = 0.0d;

    /* renamed from: rp */
    private double f64rp = 0.0d;
    private double rtemsq = 0.0d;
    private double rteosq = 0.0d;

    /* renamed from: s1 */
    private double f65s1 = 0.0d;

    /* renamed from: s2 */
    private double f66s2 = 0.0d;

    /* renamed from: s3 */
    private double f67s3 = 0.0d;

    /* renamed from: s4 */
    private double f68s4 = 0.0d;

    /* renamed from: s5 */
    private double f69s5 = 0.0d;

    /* renamed from: s6 */
    private double f70s6 = 0.0d;

    /* renamed from: s7 */
    private double f71s7 = 0.0d;
    private final ElsetRec satrec = new ElsetRec();
    private double sinim = 0.0d;
    private double sinio = 0.0d;
    private double sinomm = 0.0d;
    private double snodm = 0.0d;
    private double ss1 = 0.0d;
    private double ss2 = 0.0d;
    private double ss3 = 0.0d;
    private double ss4 = 0.0d;
    private double ss5 = 0.0d;
    private double ss6 = 0.0d;
    private double ss7 = 0.0d;
    private double sz1 = 0.0d;
    private double sz11 = 0.0d;
    private double sz12 = 0.0d;
    private double sz13 = 0.0d;
    private double sz2 = 0.0d;
    private double sz21 = 0.0d;
    private double sz22 = 0.0d;
    private double sz23 = 0.0d;
    private double sz3 = 0.0d;
    private double sz31 = 0.0d;
    private double sz32 = 0.0d;
    private double sz33 = 0.0d;

    /* renamed from: tz */
    private final TimeZone f72tz = GMT_TZ;

    /* renamed from: z1 */
    private double f73z1 = 0.0d;
    private double z11 = 0.0d;
    private double z12 = 0.0d;
    private double z13 = 0.0d;

    /* renamed from: z2 */
    private double f74z2 = 0.0d;
    private double z21 = 0.0d;
    private double z22 = 0.0d;
    private double z23 = 0.0d;

    /* renamed from: z3 */
    private double f75z3 = 0.0d;
    private double z31 = 0.0d;
    private double z32 = 0.0d;
    private double z33 = 0.0d;

    public static final class GravConstants extends GravConstantsWGS84 {
    }

    public class ObjectDecayed extends SatElsetException {
        private static final long serialVersionUID = 1230517552210764538L;

        public ObjectDecayed() {
        }

        public ObjectDecayed(String msg) {
            super(msg);
        }
    }

    public static void mainX(String[] args) {
        long time1 = System.currentTimeMillis();
        try {
            Iterator<Sgp4Data> it = new Sgp4Unit(new SatElset("1 00005U 58002B   00179.78495062  .00000023  00000-0  28098-4 0  4753", "2 00005  34.2682 348.7242 1859667 331.7664  19.3264 10.82419157413667")).runSgp4(0.0d, 4320.0d, 360.0d).iterator();
            while (it.hasNext()) {
                Sgp4Data data = it.next();
                Vector3d posn = data.getPosn();
                Vector3d vel = data.getVel();
                System.out.println("x " + (posn.f16x * 6378.137d) + " y " + (posn.f17y * 6378.137d) + " z " + (posn.f18z * 6378.137d) + " vx " + (vel.f16x * 7.905372989414837d) + " vy " + (vel.f17y * 7.905372989414837d) + " vz " + (vel.f18z * 7.905372989414837d));
            }
            System.out.println("time to process (millisec): " + (System.currentTimeMillis() - time1));
        } catch (ObjectDecayed ioe) {
            System.out.println("decayed " + ioe);
        } catch (SatElsetException ioe2) {
            System.out.println("elset exception " + ioe2);
        }
    }

    static class GravConstantsWGS72 {

        /* renamed from: j2 */
        public static final double f76j2 = 0.001082616d;

        /* renamed from: j3 */
        public static final double f77j3 = -2.53881E-6d;
        public static final double j3oj2 = -0.002345069720011528d;

        /* renamed from: j4 */
        public static final double f78j4 = -1.65597E-6d;

        /* renamed from: mu */
        public static final double f79mu = 398600.8d;
        public static final double radiusearthkm = 6378.135d;
        public static final double tumin = (1.0d / xke);
        public static final double xke = (60.0d / Math.sqrt(650942.9922085947d));

        GravConstantsWGS72() {
        }
    }

    static class GravConstantsWGS84 {

        /* renamed from: j2 */
        public static final double f80j2 = 0.00108262998905d;

        /* renamed from: j3 */
        public static final double f81j3 = -2.53215306E-6d;
        public static final double j3oj2 = -0.0023388905587420003d;

        /* renamed from: j4 */
        public static final double f82j4 = -1.61098761E-6d;

        /* renamed from: mu */
        public static final double f83mu = 398600.5d;
        public static final double radiusearthkm = 6378.137d;
        public static final double tumin = (1.0d / xke);
        public static final double xke = (60.0d / Math.sqrt(650944.0944816993d));

        GravConstantsWGS84() {
        }
    }

    public Sgp4Unit() {
    }

    public Sgp4Unit(SatElset satElset) throws SatElsetException, ObjectDecayed {
        twoline2rv(satElset);
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: SSATransform
        java.lang.IndexOutOfBoundsException: bitIndex < 0: -120
        	at java.util.BitSet.get(Unknown Source)
        	at jadx.core.dex.visitors.ssa.LiveVarAnalysis.fillBasicBlockInfo(LiveVarAnalysis.java:65)
        	at jadx.core.dex.visitors.ssa.LiveVarAnalysis.runAnalysis(LiveVarAnalysis.java:36)
        	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
        	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:41)
        */
    private void dpper(double r82, double r84, double r86, double r88, double r90, double r92, double r94, double r96, double r98, double r100, double r102, double r104, double r106, double r108, double r110, double r112, double r114, double r116, double r118, double r120, double r122, double r124, double r126, double r128, double r130, double r132, double r134, boolean r136) {
        /*
        // Method dump skipped, instructions count: 756
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lavadip.skeye.astro.sgp4v.Sgp4Unit.dpper(double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, double, boolean):void");
    }

    private void dscom(double epoch, double ep, double argpp, double tc, double inclp, double omegap, double np) {
        this.f63nm = np;
        this.f61em = ep;
        this.snodm = Math.sin(omegap);
        this.cnodm = Math.cos(omegap);
        this.sinomm = Math.sin(argpp);
        this.cosomm = Math.cos(argpp);
        this.sinim = Math.sin(inclp);
        this.cosim = Math.cos(inclp);
        this.emsq = this.f61em * this.f61em;
        double betasq = 1.0d - this.emsq;
        this.rtemsq = Math.sqrt(betasq);
        this.day = 18261.5d + epoch + (tc / 1440.0d);
        double xnodce = (4.523602d - (9.2422029E-4d * this.day)) % 6.283185307179586d;
        double stem = Math.sin(xnodce);
        double ctem = Math.cos(xnodce);
        double zcosil = 0.91375164d - (0.03568096d * ctem);
        double zsinil = Math.sqrt(1.0d - (zcosil * zcosil));
        double zsinhl = (0.089683511d * stem) / zsinil;
        double zcoshl = Math.sqrt(1.0d - (zsinhl * zsinhl));
        this.gam = 5.8351514d + (0.001944368d * this.day);
        double zx = (this.gam + Math.atan2((0.39785416d * stem) / zsinil, (zcoshl * ctem) + ((0.91744867d * zsinhl) * stem))) - xnodce;
        double zcosgl = Math.cos(zx);
        double zsingl = Math.sin(zx);
        double zcosg = 0.1945905d;
        double zsing = -0.98088458d;
        double zcosi = 0.91744867d;
        double zsini = 0.39785416d;
        double zcosh = this.cnodm;
        double zsinh = this.snodm;
        double cc = 2.9864797E-6d;
        double xnoi = 1.0d / this.f63nm;
        for (int lsflg = 1; lsflg <= 2; lsflg++) {
            double a1 = (zcosg * zcosh) + (zsing * zcosi * zsinh);
            double a3 = ((-zsing) * zcosh) + (zcosg * zcosi * zsinh);
            double a7 = ((-zcosg) * zsinh) + (zsing * zcosi * zcosh);
            double a8 = zsing * zsini;
            double a9 = (zsing * zsinh) + (zcosg * zcosi * zcosh);
            double a10 = zcosg * zsini;
            double a2 = (this.cosim * a7) + (this.sinim * a8);
            double a4 = (this.cosim * a9) + (this.sinim * a10);
            double a5 = ((-this.sinim) * a7) + (this.cosim * a8);
            double a6 = ((-this.sinim) * a9) + (this.cosim * a10);
            double x1 = (this.cosomm * a1) + (this.sinomm * a2);
            double x2 = (this.cosomm * a3) + (this.sinomm * a4);
            double x3 = ((-a1) * this.sinomm) + (this.cosomm * a2);
            double x4 = ((-a3) * this.sinomm) + (this.cosomm * a4);
            double x5 = a5 * this.sinomm;
            double x6 = a6 * this.sinomm;
            double x7 = a5 * this.cosomm;
            double x8 = a6 * this.cosomm;
            this.z31 = ((12.0d * x1) * x1) - ((3.0d * x3) * x3);
            this.z32 = ((24.0d * x1) * x2) - ((6.0d * x3) * x4);
            this.z33 = ((12.0d * x2) * x2) - ((3.0d * x4) * x4);
            this.f73z1 = (3.0d * ((a1 * a1) + (a2 * a2))) + (this.z31 * this.emsq);
            this.f74z2 = (6.0d * ((a1 * a3) + (a2 * a4))) + (this.z32 * this.emsq);
            this.f75z3 = (3.0d * ((a3 * a3) + (a4 * a4))) + (this.z33 * this.emsq);
            this.z11 = (-6.0d * a1 * a5) + (this.emsq * (((-24.0d * x1) * x7) - ((6.0d * x3) * x5)));
            this.z12 = (-6.0d * ((a1 * a6) + (a3 * a5))) + (this.emsq * ((-24.0d * ((x2 * x7) + (x1 * x8))) - (6.0d * ((x3 * x6) + (x4 * x5)))));
            this.z13 = (-6.0d * a3 * a6) + (this.emsq * (((-24.0d * x2) * x8) - ((6.0d * x4) * x6)));
            this.z21 = (6.0d * a2 * a5) + (this.emsq * (((24.0d * x1) * x5) - ((6.0d * x3) * x7)));
            this.z22 = (6.0d * ((a4 * a5) + (a2 * a6))) + (this.emsq * ((24.0d * ((x2 * x5) + (x1 * x6))) - (6.0d * ((x4 * x7) + (x3 * x8)))));
            this.z23 = (6.0d * a4 * a6) + (this.emsq * (((24.0d * x2) * x6) - ((6.0d * x4) * x8)));
            this.f73z1 = this.f73z1 + this.f73z1 + (this.z31 * betasq);
            this.f74z2 = this.f74z2 + this.f74z2 + (this.z32 * betasq);
            this.f75z3 = this.f75z3 + this.f75z3 + (this.z33 * betasq);
            this.f67s3 = cc * xnoi;
            this.f66s2 = (-0.5d * this.f67s3) / this.rtemsq;
            this.f68s4 = this.f67s3 * this.rtemsq;
            this.f65s1 = -15.0d * this.f61em * this.f68s4;
            this.f69s5 = (x1 * x3) + (x2 * x4);
            this.f70s6 = (x2 * x3) + (x1 * x4);
            this.f71s7 = (x2 * x4) - (x1 * x3);
            if (lsflg == 1) {
                this.ss1 = this.f65s1;
                this.ss2 = this.f66s2;
                this.ss3 = this.f67s3;
                this.ss4 = this.f68s4;
                this.ss5 = this.f69s5;
                this.ss6 = this.f70s6;
                this.ss7 = this.f71s7;
                this.sz1 = this.f73z1;
                this.sz2 = this.f74z2;
                this.sz3 = this.f75z3;
                this.sz11 = this.z11;
                this.sz12 = this.z12;
                this.sz13 = this.z13;
                this.sz21 = this.z21;
                this.sz22 = this.z22;
                this.sz23 = this.z23;
                this.sz31 = this.z31;
                this.sz32 = this.z32;
                this.sz33 = this.z33;
                zcosg = zcosgl;
                zsing = zsingl;
                zcosi = zcosil;
                zsini = zsinil;
                zcosh = (this.cnodm * zcoshl) + (this.snodm * zsinhl);
                zsinh = (this.snodm * zcoshl) - (this.cnodm * zsinhl);
                cc = 4.7968065E-7d;
            }
        }
        this.satrec.dsvalues.zmol = ((4.7199672d + (0.2299715d * this.day)) - this.gam) % 6.283185307179586d;
        this.satrec.dsvalues.zmos = (6.2565837d + (0.017201977d * this.day)) % 6.283185307179586d;
        this.satrec.dsvalues.se2 = 2.0d * this.ss1 * this.ss6;
        this.satrec.dsvalues.se3 = 2.0d * this.ss1 * this.ss7;
        this.satrec.dsvalues.si2 = 2.0d * this.ss2 * this.sz12;
        this.satrec.dsvalues.si3 = 2.0d * this.ss2 * (this.sz13 - this.sz11);
        this.satrec.dsvalues.sl2 = -2.0d * this.ss3 * this.sz2;
        this.satrec.dsvalues.sl3 = -2.0d * this.ss3 * (this.sz3 - this.sz1);
        this.satrec.dsvalues.sl4 = -2.0d * this.ss3 * (-21.0d - (9.0d * this.emsq)) * 0.01675d;
        this.satrec.dsvalues.sgh2 = 2.0d * this.ss4 * this.sz32;
        this.satrec.dsvalues.sgh3 = 2.0d * this.ss4 * (this.sz33 - this.sz31);
        this.satrec.dsvalues.sgh4 = -18.0d * this.ss4 * 0.01675d;
        this.satrec.dsvalues.sh2 = -2.0d * this.ss2 * this.sz22;
        this.satrec.dsvalues.sh3 = -2.0d * this.ss2 * (this.sz23 - this.sz21);
        this.satrec.dsvalues.ee2 = 2.0d * this.f65s1 * this.f70s6;
        this.satrec.dsvalues.f54e3 = 2.0d * this.f65s1 * this.f71s7;
        this.satrec.dsvalues.xi2 = 2.0d * this.f66s2 * this.z12;
        this.satrec.dsvalues.xi3 = 2.0d * this.f66s2 * (this.z13 - this.z11);
        this.satrec.dsvalues.xl2 = -2.0d * this.f67s3 * this.f74z2;
        this.satrec.dsvalues.xl3 = -2.0d * this.f67s3 * (this.f75z3 - this.f73z1);
        this.satrec.dsvalues.xl4 = -2.0d * this.f67s3 * (-21.0d - (9.0d * this.emsq)) * 0.0549d;
        this.satrec.dsvalues.xgh2 = 2.0d * this.f68s4 * this.z32;
        this.satrec.dsvalues.xgh3 = 2.0d * this.f68s4 * (this.z33 - this.z31);
        this.satrec.dsvalues.xgh4 = -18.0d * this.f68s4 * 0.0549d;
        this.satrec.dsvalues.xh2 = -2.0d * this.f66s2 * this.z22;
        this.satrec.dsvalues.xh3 = -2.0d * this.f66s2 * (this.z23 - this.z21);
    }

    private void dsinit(double cosim2, double emsq2, double argpo, double s1, double s2, double s3, double s4, double s5, double sinim2, double t, double tc, double gsto, double mo, double mdot, double no, double omegao, double omegadot, double xpidot, double z1, double z3, double z112, double z132, double z212, double z232, double z312, double z332, double ecco, double eccsq2) {
        double g211;
        double g310;
        double g322;
        double g410;
        double g422;
        double g520;
        double g533;
        double g521;
        double g532;
        this.satrec.dsvalues.irez = 0;
        if (this.f63nm < 0.0052359877d && this.f63nm > 0.0034906585d) {
            this.satrec.dsvalues.irez = 1;
        }
        if (this.f63nm >= 0.00826d && this.f63nm <= 0.00924d && this.f61em >= 0.5d) {
            this.satrec.dsvalues.irez = 2;
        }
        double ses = this.ss1 * 1.19459E-5d * this.ss5;
        double sis = this.ss2 * 1.19459E-5d * (this.sz11 + this.sz13);
        double sls = -1.19459E-5d * this.ss3 * (((this.sz1 + this.sz3) - 14.0d) - (6.0d * emsq2));
        double sghs = this.ss4 * 1.19459E-5d * ((this.sz31 + this.sz33) - 6.0d);
        double shs = -1.19459E-5d * this.ss2 * (this.sz21 + this.sz23);
        if (this.inclm < 0.052359877d || this.inclm > 3.0892327765897933d) {
            shs = 0.0d;
        }
        if (sinim2 != 0.0d) {
            shs /= sinim2;
        }
        double sgs = sghs - (cosim2 * shs);
        this.satrec.dsvalues.dedt = (1.5835218E-4d * s1 * s5) + ses;
        this.satrec.dsvalues.didt = (1.5835218E-4d * s2 * (z112 + z132)) + sis;
        this.satrec.dsvalues.dmdt = sls - ((1.5835218E-4d * s3) * (((z1 + z3) - 14.0d) - (6.0d * emsq2)));
        double sghl = 1.5835218E-4d * s4 * ((z312 + z332) - 6.0d);
        double shll = -1.5835218E-4d * s2 * (z212 + z232);
        if (this.inclm < 0.052359877d || this.inclm > 3.0892327765897933d) {
            shll = 0.0d;
        }
        this.satrec.dsvalues.domdt = sgs + sghl;
        this.satrec.dsvalues.dnodt = shs;
        if (sinim2 != 0.0d) {
            this.satrec.dsvalues.domdt -= (cosim2 / sinim2) * shll;
            this.satrec.dsvalues.dnodt += shll / sinim2;
        }
        this.dndt = 0.0d;
        double theta = ((rptim * tc) + gsto) % 6.283185307179586d;
        this.f61em += this.satrec.dsvalues.dedt * t;
        this.inclm += this.satrec.dsvalues.didt * t;
        this.argpm += this.satrec.dsvalues.domdt * t;
        this.omegam += this.satrec.dsvalues.dnodt * t;
        this.f62mm += this.satrec.dsvalues.dmdt * t;
        if (this.satrec.dsvalues.irez != 0) {
            double aonv = Math.pow(this.f63nm / GravConstants.xke, x2o3);
            if (this.satrec.dsvalues.irez == 2) {
                double cosisq = cosim2 * cosim2;
                double emo = this.f61em;
                this.f61em = ecco;
                double eoc = this.f61em * eccsq2;
                double g201 = -0.306d - ((this.f61em - 0.64d) * 0.44d);
                if (this.f61em <= 0.65d) {
                    g211 = (3.616d - (13.247d * this.f61em)) + (16.29d * eccsq2);
                    g310 = ((-19.302d + (117.39d * this.f61em)) - (228.419d * eccsq2)) + (156.591d * eoc);
                    g322 = ((-18.9068d + (109.7927d * this.f61em)) - (214.6334d * eccsq2)) + (146.5816d * eoc);
                    g410 = ((-41.122d + (242.694d * this.f61em)) - (471.094d * eccsq2)) + (313.953d * eoc);
                    g422 = ((-146.407d + (841.88d * this.f61em)) - (1629.014d * eccsq2)) + (1083.435d * eoc);
                    g520 = ((-532.114d + (3017.977d * this.f61em)) - (5740.032d * eccsq2)) + (3708.276d * eoc);
                } else {
                    g211 = ((-72.099d + (331.819d * this.f61em)) - (508.738d * eccsq2)) + (266.724d * eoc);
                    g310 = ((-346.844d + (1582.851d * this.f61em)) - (2415.925d * eccsq2)) + (1246.113d * eoc);
                    g322 = ((-342.585d + (1554.908d * this.f61em)) - (2366.899d * eccsq2)) + (1215.972d * eoc);
                    g410 = ((-1052.797d + (4758.686d * this.f61em)) - (7193.992d * eccsq2)) + (3651.957d * eoc);
                    g422 = ((-3581.69d + (16178.11d * this.f61em)) - (24462.77d * eccsq2)) + (12422.52d * eoc);
                    if (this.f61em > 0.715d) {
                        g520 = ((-5149.66d + (29936.92d * this.f61em)) - (54087.36d * eccsq2)) + (31324.56d * eoc);
                    } else {
                        g520 = (1464.74d - (4664.75d * this.f61em)) + (3763.64d * eccsq2);
                    }
                }
                if (this.f61em < 0.7d) {
                    g533 = ((-919.2277d + (4988.61d * this.f61em)) - (9064.77d * eccsq2)) + (5542.21d * eoc);
                    g521 = ((-822.71072d + (4568.6173d * this.f61em)) - (8491.4146d * eccsq2)) + (5337.524d * eoc);
                    g532 = ((-853.666d + (4690.25d * this.f61em)) - (8624.77d * eccsq2)) + (5341.4d * eoc);
                } else {
                    g533 = ((-37995.78d + (161616.52d * this.f61em)) - (229838.2d * eccsq2)) + (109377.94d * eoc);
                    g521 = ((-51752.104d + (218913.95d * this.f61em)) - (309468.16d * eccsq2)) + (146349.42d * eoc);
                    g532 = ((-40023.88d + (170470.89d * this.f61em)) - (242699.48d * eccsq2)) + (115605.82d * eoc);
                }
                double sini2 = sinim2 * sinim2;
                double f220 = 0.75d * (1.0d + (2.0d * cosim2) + cosisq);
                double temp1 = 3.0d * this.f63nm * this.f63nm * aonv * aonv;
                double temp = temp1 * 1.7891679E-6d;
                this.satrec.dsvalues.d2201 = temp * f220 * g201;
                this.satrec.dsvalues.d2211 = temp * 1.5d * sini2 * g211;
                double temp12 = temp1 * aonv;
                double temp2 = temp12 * 3.7393792E-7d;
                this.satrec.dsvalues.d3210 = temp2 * 1.875d * sinim2 * ((1.0d - (2.0d * cosim2)) - (3.0d * cosisq)) * g310;
                this.satrec.dsvalues.d3222 = temp2 * -1.875d * sinim2 * ((1.0d + (2.0d * cosim2)) - (3.0d * cosisq)) * g322;
                double temp13 = temp12 * aonv;
                double temp3 = 2.0d * temp13 * 7.3636953E-9d;
                this.satrec.dsvalues.d4410 = temp3 * 35.0d * sini2 * f220 * g410;
                this.satrec.dsvalues.d4422 = temp3 * 39.375d * sini2 * sini2 * g422;
                double temp14 = temp13 * aonv;
                double temp4 = temp14 * 1.1428639E-7d;
                this.satrec.dsvalues.d5220 = temp4 * 9.84375d * sinim2 * ((((1.0d - (2.0d * cosim2)) - (5.0d * cosisq)) * sini2) + (0.33333333d * (-2.0d + (4.0d * cosim2) + (6.0d * cosisq)))) * g520;
                this.satrec.dsvalues.d5232 = temp4 * sinim2 * ((4.92187512d * sini2 * ((-2.0d - (4.0d * cosim2)) + (10.0d * cosisq))) + (6.56250012d * ((1.0d + (2.0d * cosim2)) - (3.0d * cosisq)))) * g532;
                double temp5 = 2.0d * temp14 * 2.1765803E-9d;
                this.satrec.dsvalues.d5421 = temp5 * 29.53125d * sinim2 * ((2.0d - (8.0d * cosim2)) + ((-12.0d + (8.0d * cosim2) + (10.0d * cosisq)) * cosisq)) * g521;
                this.satrec.dsvalues.d5433 = temp5 * 29.53125d * sinim2 * ((-2.0d - (8.0d * cosim2)) + (((12.0d + (8.0d * cosim2)) - (10.0d * cosisq)) * cosisq)) * g533;
                this.satrec.dsvalues.xlamo = ((((mo + omegao) + omegao) - theta) - theta) % 6.283185307179586d;
                this.satrec.dsvalues.xfact = ((this.satrec.dsvalues.dmdt + mdot) + (2.0d * ((this.satrec.dsvalues.dnodt + omegadot) - rptim))) - no;
                this.f61em = emo;
                emsq2 = emsq2;
            }
            if (this.satrec.dsvalues.irez == 1) {
                double f330 = 1.0d + cosim2;
                this.satrec.dsvalues.del1 = 3.0d * this.f63nm * this.f63nm * aonv * aonv;
                this.satrec.dsvalues.del2 = 2.0d * this.satrec.dsvalues.del1 * 0.75d * (1.0d + cosim2) * (1.0d + cosim2) * (1.0d + ((-2.5d + (0.8125d * emsq2)) * emsq2)) * 1.7891679E-6d;
                this.satrec.dsvalues.del3 = 3.0d * this.satrec.dsvalues.del1 * f330 * 1.875d * f330 * f330 * (1.0d + ((-6.0d + (6.60937d * emsq2)) * emsq2)) * 2.2123015E-7d * aonv;
                this.satrec.dsvalues.del1 = this.satrec.dsvalues.del1 * ((((0.9375d * sinim2) * sinim2) * (1.0d + (3.0d * cosim2))) - (0.75d * (1.0d + cosim2))) * (1.0d + (2.0d * emsq2)) * 2.1460748E-6d * aonv;
                this.satrec.dsvalues.xlamo = (((mo + omegao) + argpo) - theta) % 6.283185307179586d;
                this.satrec.dsvalues.xfact = (((((mdot + xpidot) - rptim) + this.satrec.dsvalues.dmdt) + this.satrec.dsvalues.domdt) + this.satrec.dsvalues.dnodt) - no;
            }
            this.satrec.dsvalues.xli = this.satrec.dsvalues.xlamo;
            this.satrec.dsvalues.xni = no;
            this.satrec.dsvalues.atime = 0.0d;
            this.f63nm = this.dndt + no;
        }
    }

    private void dspace(int irez, double d2201, double d2211, double d3210, double d3222, double d4410, double d4422, double d5220, double d5232, double d5421, double d5433, double dedt, double del1, double del2, double del3, double didt, double dmdt, double dnodt, double domdt, double argpo, double argpdot, double t, double tc, double gsto, double xfact, double xlamo, double no) {
        double delt;
        double xldot = 0.0d;
        double xnddt = 0.0d;
        double xndt = 0.0d;
        this.dndt = 0.0d;
        double theta = ((rptim * tc) + gsto) % 6.283185307179586d;
        this.f61em += dedt * t;
        this.inclm += didt * t;
        this.argpm += domdt * t;
        this.omegam += dnodt * t;
        this.f62mm += dmdt * t;
        double ft = 0.0d;
        this.satrec.dsvalues.atime = 0.0d;
        if (irez != 0) {
            if (this.satrec.dsvalues.atime == 0.0d || ((t >= 0.0d && this.satrec.dsvalues.atime < 0.0d) || (t < 0.0d && this.satrec.dsvalues.atime >= 0.0d))) {
                if (t >= 0.0d) {
                }
                this.satrec.dsvalues.atime = 0.0d;
                this.satrec.dsvalues.xni = no;
                this.satrec.dsvalues.xli = xlamo;
            }
            int iretn = 381;
            int iret = 0;
            while (iretn == 381) {
                if (Math.abs(t) < Math.abs(this.satrec.dsvalues.atime) || iret == 351) {
                    if (t >= 0.0d) {
                        delt = stepn;
                    } else {
                        delt = stepp;
                    }
                    iret = 351;
                    iretn = 381;
                } else {
                    if (t > 0.0d) {
                        delt = stepp;
                    } else {
                        delt = stepn;
                    }
                    if (Math.abs(t - this.satrec.dsvalues.atime) >= stepp) {
                        iret = 0;
                        iretn = 381;
                    } else {
                        ft = t - this.satrec.dsvalues.atime;
                        iretn = 0;
                    }
                }
                if (irez != 2) {
                    xndt = (Math.sin(this.satrec.dsvalues.xli - fasx2) * del1) + (Math.sin(2.0d * (this.satrec.dsvalues.xli - fasx4)) * del2) + (Math.sin(3.0d * (this.satrec.dsvalues.xli - fasx6)) * del3);
                    xldot = this.satrec.dsvalues.xni + xfact;
                    xnddt = ((Math.cos(this.satrec.dsvalues.xli - fasx2) * del1) + (2.0d * del2 * Math.cos(2.0d * (this.satrec.dsvalues.xli - fasx4))) + (3.0d * del3 * Math.cos(3.0d * (this.satrec.dsvalues.xli - fasx6)))) * xldot;
                } else {
                    double xomi = argpo + (this.satrec.dsvalues.atime * argpdot);
                    double x2omi = xomi + xomi;
                    double x2li = this.satrec.dsvalues.xli + this.satrec.dsvalues.xli;
                    xndt = (Math.sin((this.satrec.dsvalues.xli + x2omi) - g22) * d2201) + (Math.sin(this.satrec.dsvalues.xli - g22) * d2211) + (Math.sin((this.satrec.dsvalues.xli + xomi) - g32) * d3210) + (Math.sin(((-xomi) + this.satrec.dsvalues.xli) - g32) * d3222) + (Math.sin((x2omi + x2li) - g44) * d4410) + (Math.sin(x2li - g44) * d4422) + (Math.sin((this.satrec.dsvalues.xli + xomi) - g52) * d5220) + (Math.sin(((-xomi) + this.satrec.dsvalues.xli) - g52) * d5232) + (Math.sin((xomi + x2li) - g54) * d5421) + (Math.sin(((-xomi) + x2li) - g54) * d5433);
                    xldot = this.satrec.dsvalues.xni + xfact;
                    xnddt = ((Math.cos((this.satrec.dsvalues.xli + x2omi) - g22) * d2201) + (Math.cos(this.satrec.dsvalues.xli - g22) * d2211) + (Math.cos((this.satrec.dsvalues.xli + xomi) - g32) * d3210) + (Math.cos(((-xomi) + this.satrec.dsvalues.xli) - g32) * d3222) + (Math.cos((this.satrec.dsvalues.xli + xomi) - g52) * d5220) + (Math.cos(((-xomi) + this.satrec.dsvalues.xli) - g52) * d5232) + (2.0d * ((Math.cos((x2omi + x2li) - g44) * d4410) + (Math.cos(x2li - g44) * d4422) + (Math.cos((xomi + x2li) - g54) * d5421) + (Math.cos(((-xomi) + x2li) - g54) * d5433)))) * xldot;
                }
                if (iretn == 381) {
                    this.satrec.dsvalues.xli = this.satrec.dsvalues.xli + (xldot * delt) + (step2 * xndt);
                    this.satrec.dsvalues.xni = this.satrec.dsvalues.xni + (xndt * delt) + (step2 * xnddt);
                    this.satrec.dsvalues.atime += delt;
                }
            }
            this.f63nm = this.satrec.dsvalues.xni + (xndt * ft) + (xnddt * ft * ft * 0.5d);
            double xl = this.satrec.dsvalues.xli + (xldot * ft) + (xndt * ft * ft * 0.5d);
            if (irez != 1) {
                this.f62mm = (xl - (2.0d * this.omegam)) + (2.0d * theta);
            } else {
                this.f62mm = ((xl - this.omegam) - this.argpm) + theta;
            }
            this.dndt = this.f63nm - no;
            this.f63nm = this.dndt + no;
        }
    }

    private static double gstime(double jdut1) {
        double tut1 = (jdut1 - 2451545.0d) / 36525.0d;
        double temp = ((0.017453292519943295d * ((((((-6.2E-6d * tut1) * tut1) * tut1) + ((0.093104d * tut1) * tut1)) + (3.164400184812866E9d * tut1)) + 67310.54841d)) / 240.0d) % 6.283185307179586d;
        if (temp < 0.0d) {
            return temp + 6.283185307179586d;
        }
        return temp;
    }

    private void initl(int satn, double ecco, double epoch, double inclo) throws ObjectDecayed {
        this.eccsq = ecco * ecco;
        this.omeosq = 1.0d - this.eccsq;
        this.rteosq = Math.sqrt(this.omeosq);
        this.cosio = Math.cos(inclo);
        this.cosio2 = this.cosio * this.cosio;
        double ak = Math.pow(GravConstants.xke / this.satrec.f53no, x2o3);
        double d1 = (8.119724917875E-4d * ((3.0d * this.cosio2) - 1.0d)) / (this.rteosq * this.omeosq);
        double del = d1 / (ak * ak);
        double adel = ak * ((1.0d - (del * del)) - ((0.3333333333333333d + (((134.0d * del) * del) / 81.0d)) * del));
        this.satrec.f53no /= 1.0d + (d1 / (adel * adel));
        this.f60ao = Math.pow(GravConstants.xke / this.satrec.f53no, x2o3);
        this.sinio = Math.sin(inclo);
        double po = this.f60ao * this.omeosq;
        this.posq = po * po;
        this.con42 = 1.0d - (5.0d * this.cosio2);
        this.satrec.nevalues.con41 = ((-this.con42) - this.cosio2) - this.cosio2;
        this.f64rp = this.f60ao * (1.0d - ecco);
        this.satrec.nevalues.method = 0;
        if (this.f64rp < 1.0d) {
            throw new ObjectDecayed("Sgp4Unit.initl Fatal SGP4 error [satn: " + satn + " epoch elts sub-orbital]");
        }
        this.satrec.dsvalues.gsto = gstime(2433281.5d + epoch);
    }

    private static double julianday(int year, int mon, int inday, int hr, int min, double sec) {
        return ((367.0d * ((double) year)) - ((double) ((int) (((double) ((((mon + 9) / 12) + year) * 7)) * 0.25d)))) + ((double) ((mon * 275) / 9)) + ((double) inday) + 1721013.5d + (((((sec / 60.0d) + ((double) min)) / 60.0d) + ((double) hr)) / 24.0d);
    }

    public Sgp4Data runSgp4(int startYear, double startDay) throws ObjectDecayed {
        if (startYear < 1900) {
            if (startYear < 50) {
                startYear += MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS;
            } else {
                startYear += 1900;
            }
        }
        ElsetRec.NearEarthType nearEarthType = this.satrec.nevalues;
        nearEarthType.f58t = ((((double) (((startYear - 1950) * 365) + ((startYear - 1949) / 4))) + startDay) - this.satrec.eptime) * 1440.0d;
        return sgp4();
    }

    public Vector<Sgp4Data> runSgp4(double startMFE, double stopMFE, double deltamin) throws SatElsetException, ObjectDecayed {
        Vector<Sgp4Data> sgp4Results = new Vector<>();
        this.satrec.nevalues.f58t = startMFE;
        double stopTime = stopMFE + deltamin;
        boolean endFlg = HELP;
        while (this.satrec.nevalues.f58t < stopTime && this.satrec.error == 0 && !endFlg) {
            if (this.satrec.nevalues.f58t > stopTime) {
                this.satrec.nevalues.f58t = stopTime;
                endFlg = true;
            }
            sgp4Results.addElement(sgp4());
            this.satrec.nevalues.f58t += deltamin;
        }
        return sgp4Results;
    }

    public Vector<Sgp4Data> runSgp4(int startYear, double startDay, int numPos, double deltamin) throws SatElsetException, ObjectDecayed {
        if (startYear < 1900) {
            if (startYear < 50) {
                startYear += MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS;
            } else {
                startYear += 1900;
            }
        }
        double srtime = ((((double) (((startYear - 1950) * 365) + ((startYear - 1949) / 4))) + startDay) - this.satrec.eptime) * 1440.0d;
        Vector<Sgp4Data> sgp4Results = new Vector<>(numPos);
        int numPosBy2 = (numPos - 1) / 2;
        for (int i = numPosBy2; i >= 0; i--) {
            this.satrec.nevalues.f58t = srtime - (((double) i) * deltamin);
            sgp4Results.addElement(sgp4());
        }
        for (int i2 = 1; i2 <= numPosBy2; i2++) {
            this.satrec.nevalues.f58t = (((double) i2) * deltamin) + srtime;
            sgp4Results.addElement(sgp4());
        }
        return sgp4Results;
    }

    public Vector<Sgp4Data> runSgp4(int startYear, double startDay, int stopYear, double stopDay, double deltamin) throws SatElsetException, ObjectDecayed {
        if (startYear < 1900) {
            if (startYear < 50) {
                startYear += MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS;
            } else {
                startYear += 1900;
            }
        }
        if (stopYear < 1900) {
            if (stopYear < 50) {
                stopYear += MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS;
            } else {
                stopYear += 1900;
            }
        }
        Vector<Sgp4Data> sgp4Results = new Vector<>();
        this.satrec.nevalues.f58t = ((((double) (((startYear - 1950) * 365) + ((startYear - 1949) / 4))) + startDay) - this.satrec.eptime) * 1440.0d;
        double stopTime = (((((double) (((stopYear - 1950) * 365) + ((stopYear - 1949) / 4))) + stopDay) - this.satrec.eptime) * 1440.0d) + deltamin;
        boolean endFlg = HELP;
        while (this.satrec.nevalues.f58t < stopTime && this.satrec.error == 0 && !endFlg) {
            if (this.satrec.nevalues.f58t > stopTime) {
                this.satrec.nevalues.f58t = stopTime;
                endFlg = true;
            }
            sgp4Results.addElement(sgp4());
            this.satrec.nevalues.f58t += deltamin;
        }
        return sgp4Results;
    }

    public Sgp4Data sgp4() throws ObjectDecayed {
        this.satrec.error = 0;
        double xmdf = this.satrec.f51mo + (this.satrec.nevalues.mdot * this.satrec.nevalues.f58t);
        double argpdf = this.satrec.argpo + (this.satrec.nevalues.argpdot * this.satrec.nevalues.f58t);
        double omegadf = this.satrec.omegao + (this.satrec.nevalues.omegadot * this.satrec.nevalues.f58t);
        this.argpm = argpdf;
        this.f62mm = xmdf;
        double t2 = this.satrec.nevalues.f58t * this.satrec.nevalues.f58t;
        this.omegam = (this.satrec.nevalues.omegacf * t2) + omegadf;
        double tempa = 1.0d - (this.satrec.nevalues.cc1 * this.satrec.nevalues.f58t);
        double tempe = this.satrec.bstar * this.satrec.nevalues.cc4 * this.satrec.nevalues.f58t;
        double templ = this.satrec.nevalues.t2cof * t2;
        if (this.satrec.nevalues.isimp != 1) {
            double tempB2 = (this.satrec.nevalues.omgcof * this.satrec.nevalues.f58t) + (this.satrec.nevalues.xmcof * (Math.pow(1.0d + (this.satrec.nevalues.eta * Math.cos(xmdf)), 3.0d) - this.satrec.nevalues.delmo));
            this.f62mm = xmdf + tempB2;
            this.argpm = argpdf - tempB2;
            double t3 = t2 * this.satrec.nevalues.f58t;
            double t4 = t3 * this.satrec.nevalues.f58t;
            tempa = ((tempa - (this.satrec.nevalues.f55d2 * t2)) - (this.satrec.nevalues.f56d3 * t3)) - (this.satrec.nevalues.f57d4 * t4);
            tempe += this.satrec.bstar * this.satrec.nevalues.cc5 * (Math.sin(this.f62mm) - this.satrec.nevalues.sinmao);
            templ = (this.satrec.nevalues.t3cof * t3) + templ + ((this.satrec.nevalues.t4cof + (this.satrec.nevalues.f58t * this.satrec.nevalues.t5cof)) * t4);
        }
        this.f63nm = this.satrec.f53no;
        this.f61em = this.satrec.ecco;
        this.inclm = this.satrec.inclo;
        if (this.satrec.nevalues.method == 2) {
            dspace(this.satrec.dsvalues.irez, this.satrec.dsvalues.d2201, this.satrec.dsvalues.d2211, this.satrec.dsvalues.d3210, this.satrec.dsvalues.d3222, this.satrec.dsvalues.d4410, this.satrec.dsvalues.d4422, this.satrec.dsvalues.d5220, this.satrec.dsvalues.d5232, this.satrec.dsvalues.d5421, this.satrec.dsvalues.d5433, this.satrec.dsvalues.dedt, this.satrec.dsvalues.del1, this.satrec.dsvalues.del2, this.satrec.dsvalues.del3, this.satrec.dsvalues.didt, this.satrec.dsvalues.dmdt, this.satrec.dsvalues.dnodt, this.satrec.dsvalues.domdt, this.satrec.argpo, this.satrec.nevalues.argpdot, this.satrec.nevalues.f58t, this.satrec.nevalues.f58t, this.satrec.dsvalues.gsto, this.satrec.dsvalues.xfact, this.satrec.dsvalues.xlamo, this.satrec.f53no);
        }
        if (this.f63nm <= 0.0d) {
            this.satrec.error = 2;
            throw new ObjectDecayed("Sgp4Unit.sgp4 ERROR mean motion is less than zero [nm: " + this.f63nm + "]");
        }
        double am = Math.pow(GravConstants.xke / this.f63nm, x2o3) * tempa * tempa;
        this.f63nm = GravConstants.xke / Math.pow(am, 1.5d);
        this.f61em -= tempe;
        if (this.f61em >= 1.0d || this.f61em < -0.001d) {
            this.satrec.error = 1;
            throw new ObjectDecayed("Sgp4Unit.sgp4 ERROR eccentricity out of bounds [em: " + this.f61em + "] [am: " + am + "] for sat: " + this.satrec.satnum);
        }
        if (this.f61em < 1.0E-6d) {
            this.f61em = 1.0E-6d;
        }
        this.f62mm += this.satrec.f53no * templ;
        double xlm = ((this.f62mm + this.argpm) + this.omegam) % 6.283185307179586d;
        this.omegam %= 6.283185307179586d;
        this.argpm %= 6.283185307179586d;
        this.f62mm = ((xlm - this.argpm) - this.omegam) % 6.283185307179586d;
        double sinim2 = Math.sin(this.inclm);
        double cosim2 = Math.cos(this.inclm);
        this.satrec.f50ep = this.f61em;
        this.satrec.xincp = this.inclm;
        this.satrec.argpp = this.argpm;
        this.satrec.omegap = this.omegam;
        this.satrec.f52mp = this.f62mm;
        double sinip = sinim2;
        double cosip = cosim2;
        if (this.satrec.nevalues.method == 2) {
            dpper(this.satrec.dsvalues.f54e3, this.satrec.dsvalues.ee2, this.satrec.dsvalues.se2, this.satrec.dsvalues.se3, this.satrec.dsvalues.sgh2, this.satrec.dsvalues.sgh3, this.satrec.dsvalues.sgh4, this.satrec.dsvalues.sh2, this.satrec.dsvalues.sh3, this.satrec.dsvalues.si2, this.satrec.dsvalues.si3, this.satrec.dsvalues.sl2, this.satrec.dsvalues.sl3, this.satrec.dsvalues.sl4, this.satrec.nevalues.f58t, this.satrec.dsvalues.xgh2, this.satrec.dsvalues.xgh3, this.satrec.dsvalues.xgh4, this.satrec.dsvalues.xh2, this.satrec.dsvalues.xh3, this.satrec.dsvalues.xi2, this.satrec.dsvalues.xi3, this.satrec.dsvalues.xl2, this.satrec.dsvalues.xl3, this.satrec.dsvalues.xl4, this.satrec.dsvalues.zmol, this.satrec.dsvalues.zmos, HELP);
            if (this.satrec.xincp < 0.0d) {
                this.satrec.xincp = -this.satrec.xincp;
                this.satrec.omegap += 3.141592653589793d;
                this.satrec.argpp -= 3.141592653589793d;
            }
            if (this.satrec.f50ep < 0.0d || this.satrec.f50ep > 1.0d) {
                this.satrec.error = 3;
                throw new ObjectDecayed("Sgp4Unit.sgp4 ERROR eccentricity out of bounds [ep: " + this.satrec.f50ep + "]");
            }
        }
        if (this.satrec.nevalues.method == 2) {
            sinip = Math.sin(this.satrec.xincp);
            cosip = Math.cos(this.satrec.xincp);
            this.satrec.nevalues.aycof = 0.0011694452793710002d * sinip;
            this.satrec.nevalues.xlcof = ((5.847226396855001E-4d * sinip) * (3.0d + (5.0d * cosip))) / (Math.abs(1.0d + cosip) > SMALL_VAL ? 1.0d + cosip : SMALL_VAL);
        }
        double axnl = this.satrec.f50ep * Math.cos(this.satrec.argpp);
        double tempB1 = 1.0d / ((1.0d - (this.satrec.f50ep * this.satrec.f50ep)) * am);
        double aynl = (this.satrec.f50ep * Math.sin(this.satrec.argpp)) + (this.satrec.nevalues.aycof * tempB1);
        double xl = this.satrec.f52mp + this.satrec.argpp + this.satrec.omegap + (this.satrec.nevalues.xlcof * tempB1 * axnl);
        double el2 = (axnl * axnl) + (aynl * aynl);
        double pl = am * (1.0d - el2);
        if (pl < 0.0d) {
            this.satrec.error = 4;
            throw new ObjectDecayed("Sgp4Unit.sgp4 ERROR [pl: " + pl + "]");
        }
        Vector2d solutn = solveKepler((xl - this.satrec.omegap) % 6.283185307179586d, axnl, aynl);
        double sineo1 = solutn.f14x;
        double coseo1 = solutn.f15y;
        double esine = (axnl * sineo1) - (aynl * coseo1);
        double rl = am * (1.0d - ((axnl * coseo1) + (aynl * sineo1)));
        double rdotl = (Math.sqrt(am) * esine) / rl;
        double rvdotl = Math.sqrt(pl) / rl;
        double betal = Math.sqrt(1.0d - el2);
        double tempA1 = esine / (1.0d + betal);
        double sinu = (am / rl) * ((sineo1 - aynl) - (axnl * tempA1));
        double cosu = (am / rl) * ((coseo1 - axnl) + (aynl * tempA1));
        double sin2u = (cosu + cosu) * sinu;
        double cos2u = 1.0d - ((2.0d * sinu) * sinu);
        double tempA2 = 1.0d / pl;
        double temp1 = 5.41314994525E-4d * tempA2;
        double temp2 = temp1 * tempA2;
        if (this.satrec.nevalues.method == 2) {
            double cosisq = cosip * cosip;
            this.satrec.nevalues.con41 = (3.0d * cosisq) - 1.0d;
            this.satrec.nevalues.x1mth2 = 1.0d - cosisq;
            this.satrec.nevalues.x7thm1 = (7.0d * cosisq) - 1.0d;
        }
        double mrt = ((1.0d - (((1.5d * temp2) * betal) * this.satrec.nevalues.con41)) * rl) + (0.5d * temp1 * this.satrec.nevalues.x1mth2 * cos2u);
        Sgp4Data result = mkOrientation(Math.atan2(sinu, cosu) - (((0.25d * temp2) * this.satrec.nevalues.x7thm1) * sin2u), mrt, this.satrec.omegap + (1.5d * temp2 * cosip * sin2u), this.satrec.xincp + (1.5d * temp2 * cosip * sinip * cos2u), rdotl - ((((this.f63nm * temp1) * this.satrec.nevalues.x1mth2) * sin2u) / GravConstants.xke), rvdotl + (((this.f63nm * temp1) * ((this.satrec.nevalues.x1mth2 * cos2u) + (1.5d * this.satrec.nevalues.con41))) / GravConstants.xke));
        if (mrt < 1.0d) {
            this.satrec.error = 6;
        }
        if (this.satrec.error <= 0 || this.satrec.error != 4) {
            return result;
        }
        throw new ObjectDecayed("Sgp4Unit.sgp4 Fatal SGP4 error [pl: " + pl + "]");
    }

    private static Sgp4Data mkOrientation(double su, double mrt, double xnode, double xinc, double mvt, double rvdot) {
        double sinsu = Math.sin(su);
        double cossu = Math.cos(su);
        double snod = Math.sin(xnode);
        double cnod = Math.cos(xnode);
        double sini = Math.sin(xinc);
        double cosi = Math.cos(xinc);
        double xmx = (-snod) * cosi;
        double xmy = cnod * cosi;
        double ux = (xmx * sinsu) + (cnod * cossu);
        double uy = (xmy * sinsu) + (snod * cossu);
        double uz = sini * sinsu;
        return new Sgp4Data(new Vector3d(mrt * ux, mrt * uy, mrt * uz), new Vector3d((mvt * ux) + (rvdot * ((xmx * cossu) - (cnod * sinsu))), (mvt * uy) + (rvdot * ((xmy * cossu) - (snod * sinsu))), (mvt * uz) + (rvdot * sini * cossu)));
    }

    private static Vector2d solveKepler(double u, double axnl, double aynl) {
        double sineo1 = 0.0d;
        double coseo1 = 0.0d;
        double eo1 = u;
        double tem5 = 9999.9d;
        int ktr = 1;
        while (Math.abs(tem5) >= 1.0E-12d && ktr <= 10) {
            sineo1 = Math.sin(eo1);
            coseo1 = Math.cos(eo1);
            tem5 = (((u - (aynl * coseo1)) + (axnl * sineo1)) - eo1) / ((1.0d - (coseo1 * axnl)) - (sineo1 * aynl));
            if (Math.abs(tem5) >= 0.95d) {
                tem5 = tem5 > 0.0d ? 0.95d : -0.95d;
            }
            eo1 += tem5;
            ktr++;
        }
        return new Vector2d(sineo1, coseo1);
    }

    private void sgp4init(int satn, int year, double epoch) throws ObjectDecayed {
        this.ss4 = 0.0d;
        this.ss3 = 0.0d;
        this.ss2 = 0.0d;
        this.ss1 = 0.0d;
        this.ss7 = 0.0d;
        this.ss6 = 0.0d;
        this.ss5 = 0.0d;
        this.sz13 = 0.0d;
        this.sz12 = 0.0d;
        this.sz11 = 0.0d;
        this.sz3 = 0.0d;
        this.sz2 = 0.0d;
        this.sz1 = 0.0d;
        this.sz33 = 0.0d;
        this.sz32 = 0.0d;
        this.sz31 = 0.0d;
        this.sz23 = 0.0d;
        this.sz22 = 0.0d;
        this.sz21 = 0.0d;
        this.z13 = 0.0d;
        this.z12 = 0.0d;
        this.z11 = 0.0d;
        this.f75z3 = 0.0d;
        this.f74z2 = 0.0d;
        this.f73z1 = 0.0d;
        this.z33 = 0.0d;
        this.z32 = 0.0d;
        this.z31 = 0.0d;
        this.z23 = 0.0d;
        this.z22 = 0.0d;
        this.z21 = 0.0d;
        double qzms2t = Math.pow(0.006584994960127072d, 4.0d);
        initl(satn, this.satrec.ecco, epoch, this.satrec.inclo);
        if (this.omeosq >= 0.0d || this.satrec.f53no >= 0.0d) {
            this.satrec.nevalues.isimp = 0;
            if (this.f64rp < 1.0344928307435228d) {
                this.satrec.nevalues.isimp = 1;
            }
            double sfour = 1.0122292763545218d;
            double qzms24 = qzms2t;
            double perige = (this.f64rp - 1.0d) * 6378.137d;
            if (perige < 156.0d) {
                double sfour2 = perige - 78.0d;
                if (perige < 98.0d) {
                    sfour2 = 20.0d;
                }
                qzms24 = Math.pow((120.0d - sfour2) / 6378.137d, 4.0d);
                sfour = (sfour2 / 6378.137d) + 1.0d;
            }
            double pinvsq = 1.0d / this.posq;
            double tsi = 1.0d / (this.f60ao - sfour);
            this.satrec.nevalues.eta = this.f60ao * this.satrec.ecco * tsi;
            double etasq = this.satrec.nevalues.eta * this.satrec.nevalues.eta;
            double eeta = this.satrec.ecco * this.satrec.nevalues.eta;
            double psisq = Math.abs(1.0d - etasq);
            double coef = qzms24 * Math.pow(tsi, 4.0d);
            double coef1 = coef / Math.pow(psisq, 3.5d);
            this.satrec.nevalues.cc1 = this.satrec.bstar * this.satrec.f53no * coef1 * ((this.f60ao * (1.0d + (1.5d * etasq) + ((4.0d + etasq) * eeta))) + (((4.0598624589375E-4d * tsi) / psisq) * this.satrec.nevalues.con41 * (8.0d + (3.0d * etasq * (8.0d + etasq)))));
            double cc3 = 0.0d;
            if (this.satrec.ecco > 1.0E-4d) {
                cc3 = (((((-2.0d * coef) * tsi) * -0.0023388905587420003d) * this.satrec.f53no) * this.sinio) / this.satrec.ecco;
            }
            this.satrec.nevalues.x1mth2 = 1.0d - this.cosio2;
            this.satrec.nevalues.cc4 = 2.0d * this.satrec.f53no * coef1 * this.f60ao * this.omeosq * (((this.satrec.nevalues.eta * (2.0d + (0.5d * etasq))) + (this.satrec.ecco * (0.5d + (2.0d * etasq)))) - (((0.00108262998905d * tsi) / (this.f60ao * psisq)) * (((-3.0d * this.satrec.nevalues.con41) * ((1.0d - (2.0d * eeta)) + ((1.5d - (0.5d * eeta)) * etasq))) + (((0.75d * this.satrec.nevalues.x1mth2) * ((2.0d * etasq) - ((1.0d + etasq) * eeta))) * Math.cos(2.0d * this.satrec.argpo)))));
            this.satrec.nevalues.cc5 = 2.0d * coef1 * this.f60ao * this.omeosq * (1.0d + (2.75d * (etasq + eeta)) + (eeta * etasq));
            double cosio4 = this.cosio2 * this.cosio2;
            double temp1 = 0.001623944983575d * pinvsq * this.satrec.f53no;
            double temp2 = 0.5d * temp1 * 0.00108262998905d * pinvsq;
            double temp3 = 7.551504421875001E-7d * pinvsq * pinvsq * this.satrec.f53no;
            this.satrec.nevalues.mdot = this.satrec.f53no + (0.5d * temp1 * this.rteosq * this.satrec.nevalues.con41) + (0.0625d * temp2 * this.rteosq * ((13.0d - (78.0d * this.cosio2)) + (137.0d * cosio4)));
            this.satrec.nevalues.argpdot = (-0.5d * temp1 * this.con42) + (0.0625d * temp2 * ((7.0d - (114.0d * this.cosio2)) + (395.0d * cosio4))) + (((3.0d - (36.0d * this.cosio2)) + (49.0d * cosio4)) * temp3);
            double xhdot1 = (-temp1) * this.cosio;
            this.satrec.nevalues.omegadot = (((0.5d * temp2 * (4.0d - (19.0d * this.cosio2))) + (2.0d * temp3 * (3.0d - (7.0d * this.cosio2)))) * this.cosio) + xhdot1;
            double xpidot = this.satrec.nevalues.argpdot + this.satrec.nevalues.omegadot;
            this.satrec.nevalues.omgcof = this.satrec.bstar * cc3 * Math.cos(this.satrec.argpo);
            this.satrec.nevalues.xmcof = 0.0d;
            if (this.satrec.ecco > 1.0E-4d) {
                this.satrec.nevalues.xmcof = ((-0.6666666666666666d * coef) * this.satrec.bstar) / eeta;
            }
            this.satrec.nevalues.omegacf = 3.5d * this.omeosq * xhdot1 * this.satrec.nevalues.cc1;
            this.satrec.nevalues.t2cof = 1.5d * this.satrec.nevalues.cc1;
            if (Math.abs(this.cosio + 1.0d) > SMALL_VAL) {
                this.satrec.nevalues.xlcof = ((5.847226396855001E-4d * this.sinio) * (3.0d + (5.0d * this.cosio))) / (1.0d + this.cosio);
            } else {
                this.satrec.nevalues.xlcof = ((5.847226396855001E-4d * this.sinio) * (3.0d + (5.0d * this.cosio))) / SMALL_VAL;
            }
            this.satrec.nevalues.aycof = 0.0011694452793710002d * this.sinio;
            this.satrec.nevalues.delmo = Math.pow(1.0d + (this.satrec.nevalues.eta * Math.cos(this.satrec.f51mo)), 3.0d);
            this.satrec.nevalues.sinmao = Math.sin(this.satrec.f51mo);
            this.satrec.nevalues.x7thm1 = (7.0d * this.cosio2) - 1.0d;
            this.satrec.init = 0;
            if (6.283185307179586d / this.satrec.f53no >= 225.0d) {
                this.satrec.nevalues.method = 2;
                this.satrec.nevalues.isimp = 1;
                this.inclm = this.satrec.inclo;
                dscom(epoch, this.satrec.ecco, this.satrec.argpo, 0.0d, this.satrec.inclo, this.satrec.omegao, this.satrec.f53no);
                this.satrec.f52mp = this.satrec.f51mo;
                this.satrec.argpp = this.satrec.argpo;
                this.satrec.f50ep = this.satrec.ecco;
                this.satrec.omegap = this.satrec.omegao;
                this.satrec.xincp = this.satrec.inclo;
                dpper(this.satrec.dsvalues.f54e3, this.satrec.dsvalues.ee2, this.satrec.dsvalues.se2, this.satrec.dsvalues.se3, this.satrec.dsvalues.sgh2, this.satrec.dsvalues.sgh3, this.satrec.dsvalues.sgh4, this.satrec.dsvalues.sh2, this.satrec.dsvalues.sh3, this.satrec.dsvalues.si2, this.satrec.dsvalues.si3, this.satrec.dsvalues.sl2, this.satrec.dsvalues.sl3, this.satrec.dsvalues.sl4, this.satrec.nevalues.f58t, this.satrec.dsvalues.xgh2, this.satrec.dsvalues.xgh3, this.satrec.dsvalues.xgh4, this.satrec.dsvalues.xh2, this.satrec.dsvalues.xh3, this.satrec.dsvalues.xi2, this.satrec.dsvalues.xi3, this.satrec.dsvalues.xl2, this.satrec.dsvalues.xl3, this.satrec.dsvalues.xl4, this.satrec.dsvalues.zmol, this.satrec.dsvalues.zmos, true);
                this.satrec.f51mo = this.satrec.f52mp;
                this.satrec.argpo = this.satrec.argpp;
                this.satrec.ecco = this.satrec.f50ep;
                this.satrec.omegao = this.satrec.omegap;
                this.satrec.inclo = this.satrec.xincp;
                this.argpm = 0.0d;
                this.omegam = 0.0d;
                this.f62mm = 0.0d;
                dsinit(this.cosim, this.emsq, this.satrec.argpo, this.f65s1, this.f66s2, this.f67s3, this.f68s4, this.f69s5, this.sinim, this.satrec.nevalues.f58t, 0.0d, this.satrec.dsvalues.gsto, this.satrec.f51mo, this.satrec.nevalues.mdot, this.satrec.f53no, this.satrec.omegao, this.satrec.nevalues.omegadot, xpidot, this.f73z1, this.f75z3, this.z11, this.z13, this.z21, this.z23, this.z31, this.z33, this.satrec.ecco, this.eccsq);
            }
            if (this.satrec.nevalues.isimp != 1) {
                double cc1sq = this.satrec.nevalues.cc1 * this.satrec.nevalues.cc1;
                this.satrec.nevalues.f55d2 = 4.0d * this.f60ao * tsi * cc1sq;
                double temp = ((this.satrec.nevalues.f55d2 * tsi) * this.satrec.nevalues.cc1) / 3.0d;
                this.satrec.nevalues.f56d3 = ((17.0d * this.f60ao) + sfour) * temp;
                this.satrec.nevalues.f57d4 = 0.5d * temp * this.f60ao * tsi * ((221.0d * this.f60ao) + (31.0d * sfour)) * this.satrec.nevalues.cc1;
                this.satrec.nevalues.t3cof = this.satrec.nevalues.f55d2 + (2.0d * cc1sq);
                this.satrec.nevalues.t4cof = 0.25d * ((3.0d * this.satrec.nevalues.f56d3) + (this.satrec.nevalues.cc1 * ((12.0d * this.satrec.nevalues.f55d2) + (10.0d * cc1sq))));
                this.satrec.nevalues.t5cof = 0.2d * ((3.0d * this.satrec.nevalues.f57d4) + (12.0d * this.satrec.nevalues.cc1 * this.satrec.nevalues.f56d3) + (6.0d * this.satrec.nevalues.f55d2 * this.satrec.nevalues.f55d2) + (15.0d * cc1sq * ((2.0d * this.satrec.nevalues.f55d2) + cc1sq)));
            }
        }
    }

    public void twoline2rv(SatElset satElset) throws ObjectDecayed {
        this.satrec.error = 0;
        this.satrec.satnum = satElset.getSatID();
        String orignalDesig = satElset.getIntDesig();
        if (orignalDesig != null) {
            this.satrec.intDesgination = String.valueOf(orignalDesig.substring(0, 2)) + "-" + orignalDesig.substring(2, 5) + "-" + orignalDesig.substring(5, 8);
        } else {
            this.satrec.intDesgination = "";
        }
        this.satrec.epochyr = satElset.getEpochYr();
        this.satrec.epochdays = satElset.getEpochDay();
        this.satrec.bstar = satElset.getBstar();
        this.satrec.inclo = satElset.getInclinationDeg();
        this.satrec.omegao = satElset.getRightAscensionDeg();
        this.satrec.ecco = satElset.getEccentricity();
        this.satrec.argpo = satElset.getArgPerigeeDeg();
        this.satrec.f51mo = satElset.getMeanAnomalyDeg();
        this.satrec.f53no = satElset.getMeanMotion();
        this.satrec.f53no /= 229.1831180523293d;
        this.satrec.f49a = Math.pow(this.satrec.f53no * GravConstants.tumin, -0.6666666666666666d);
        this.satrec.inclo /= rad;
        this.satrec.omegao /= rad;
        this.satrec.argpo /= rad;
        this.satrec.f51mo /= rad;
        int year = this.satrec.epochyr + (this.satrec.epochyr < 50 ? MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS : 1900);
        this.satrec.eptime = ((double) (((year - 1950) * 365) + ((year - 1949) / 4))) + this.satrec.epochdays;
        int jDays = (int) Math.floor(this.satrec.epochdays);
        double remainder = this.satrec.epochdays - ((double) jDays);
        int hrs = (int) Math.floor(24.0d * remainder);
        double remainder2 = (24.0d * remainder) - ((double) hrs);
        int min = (int) Math.floor(60.0d * remainder2);
        GregorianCalendar calendar = new GregorianCalendar(this.f72tz);
        calendar.set(1, year);
        calendar.set(6, jDays);
        calendar.set(11, hrs);
        calendar.set(12, min);
        double mjdsatepoch = julianday(year, calendar.get(2) + 1, calendar.get(5), hrs, min, ((60.0d * remainder2) - ((double) min)) * 60.0d) - 2400000.5d;
        this.satrec.init = 1;
        this.satrec.nevalues.f58t = 0.0d;
        sgp4init(this.satrec.satnum, year, mjdsatepoch - 33281.0d);
    }

    public String getIntDesig() {
        return this.satrec.intDesgination;
    }
}
