package com.lavadip.skeye.astro.ephemeris;

/* access modifiers changed from: package-private */
public final class MajorPlanet {
    public final double lat;
    public final double lon;

    /* renamed from: r */
    public final double f41r;

    private MajorPlanet(double lon2, double lat2, double r) {
        this.lon = lon2;
        this.lat = lat2;
        this.f41r = r;
    }

    static MajorPlanet Sun(double JD) {
        double dnr = JD - 2451545.0d;
        double t = (dnr / 36525.0d) + 1.0d;
        double ls = 360.0d * Utils.frac(0.779072d + (0.00273790931d * dnr));
        double gs = 360.0d * Utils.frac(0.993126d + (0.0027377785d * dnr));
        double g2 = 360.0d * Utils.frac(0.140023d + (0.00445036173d * dnr));
        double g4 = 360.0d * Utils.frac(0.053856d + (0.00145561327d * dnr));
        double g5 = 360.0d * Utils.frac(0.056531d + (2.3080893E-4d * dnr));
        double sgs = Utils.sind(gs);
        double cgs = Utils.cosd(gs);
        double sg2 = Utils.sind(g2);
        double cg2 = Utils.cosd(g2);
        double sg5 = Utils.sind(g5);
        double cg5 = Utils.cosd(g5);
        double c5 = (cgs * cg5) + (sgs * sg5);
        double s2 = (sgs * cg2) - (cgs * sg2);
        double c2 = (cgs * cg2) + (sgs * sg2);
        return new MajorPlanet(ls + ((((((((((((6910.0d - (17.0d * t)) * sgs) + (72.0d * ((2.0d * sgs) * cgs))) - (7.0d * c5)) + (6.0d * Utils.sind((360.0d * Utils.frac(0.606434d + (0.03660110129d * dnr))) - ls))) + (6.4d * Utils.sind((((4.0d * gs) - (8.0d * g4)) + (3.0d * g5)) + 38.9d))) - (5.0d * (((2.0d * c2) * c2) - 1.0d))) - (4.0d * s2)) + ((6.0d * s2) * c2)) - (3.0d * sg5)) - ((6.0d * ((sgs * cg5) - (cgs * sg5))) * c5)) / 3600.0d), 0.0d, (1.00014d - (0.01675d * cgs)) - (1.4E-4d * (((2.0d * cgs) * cgs) - 1.0d)));
    }

    static MajorPlanet Moon(double JD) {
        double t = (JD - 2415020.0d) / 36525.0d;
        double t2 = t * t;
        double l = Utils.rev360(270.434164d + (481267.8831d * t)) + ((-0.001133d + (1.9E-6d * t)) * t2);
        double sm = Utils.rev360(358.475833d + ((35999.0498d + ((-1.5E-4d - (3.3E-6d * t)) * t)) * t));
        double mm = Utils.rev360(296.104608d + (477198.8491d * t)) + ((0.009192d + (1.44E-5d * t)) * t2);
        double d = Utils.rev360(350.737486d + (445267.1142d * t)) + ((-0.001436d + (1.9E-6d * t)) * t2);
        double n = Utils.rev360(259.183275d + ((-1934.142d + ((0.002078d + (2.2E-6d * t)) * t)) * t));
        double f = l - n;
        double sadd1 = Utils.sind(51.2d + (20.2d * t));
        double sadd = 0.003964d * Utils.sind(346.56d + ((132.87d - (0.0091731d * t)) * t));
        double sn = Utils.sind(n);
        double cn = Utils.cosd(n);
        double sn2 = Utils.sind((275.05d + n) - (2.3d * t));
        double cn2 = Utils.cosd((275.05d + n) - (2.3d * t));
        double sm2 = sm - (0.001778d * sadd1);
        double mm2 = (8.17E-4d * sadd1) + mm + sadd + (0.002541d * sn);
        double d2 = (0.002011d * sadd1) + d + sadd + (0.001964d * sn);
        double f2 = ((f - (0.004328d * sn2)) + sadd) - (0.024691d * sn);
        double e = 1.0d - ((0.002495d + (7.52E-6d * t)) * t);
        double e2 = e * e;
        double ssm = Utils.sind(sm2);
        double csm = Utils.cosd(sm2);
        double s2sm = 2.0d * ssm * csm;
        double c2sm = ((2.0d * csm) * csm) - 1.0d;
        double smm = Utils.sind(mm2);
        double cmm = Utils.cosd(mm2);
        double s2mm = 2.0d * smm * cmm;
        double c2mm = ((2.0d * cmm) * cmm) - 1.0d;
        double s3mm = (s2mm * cmm) + (c2mm * smm);
        double c3mm = (c2mm * cmm) - (s2mm * smm);
        double ssmpmm = (ssm * cmm) + (csm * smm);
        double csmpmm = (csm * cmm) - (ssm * smm);
        double ssmmmm = (ssm * cmm) - (csm * smm);
        double csmmmm = (csm * cmm) + (ssm * smm);
        double ssmp2m = (ssm * c2mm) + (csm * s2mm);
        double csmp2m = (csm * c2mm) - (ssm * s2mm);
        double ssmm2m = (ssm * c2mm) - (csm * s2mm);
        double csmm2m = (csm * c2mm) + (ssm * s2mm);
        double smmm2m = (smm * c2sm) - (cmm * s2sm);
        double cmmm2m = (cmm * c2sm) + (smm * s2sm);
        double sd = Utils.sind(d2);
        double cd = Utils.cosd(d2);
        double s2d = 2.0d * sd * cd;
        double c2d = ((2.0d * cd) * cd) - 1.0d;
        double s3d = (s2d * cd) + (c2d * sd);
        double c3d = (c2d * cd) - (s2d * sd);
        double s4d = 2.0d * s2d * c2d;
        double c4d = ((2.0d * c2d) * c2d) - 1.0d;
        double s2dmmm = (s2d * cmm) - (c2d * smm);
        double c2dmmm = (c2d * cmm) + (s2d * smm);
        double s2dmsm = (s2d * csm) - (c2d * ssm);
        double c2dmsm = (c2d * csm) + (s2d * ssm);
        double sf = Utils.sind(f2);
        double cf = Utils.cosd(f2);
        double s2f = 2.0d * sf * cf;
        double c2f = ((2.0d * cf) * cf) - 1.0d;
        double s3f = (s2f * cf) + (c2f * sf);
        double c3f = (c2f * cf) - (s2f * sf);
        double s2dpf = (s2d * cf) + (c2d * sf);
        double c2dpf = (c2d * cf) - (s2d * sf);
        double s2dmf = (s2d * cf) - (c2d * sf);
        double c2dmf = (c2d * cf) + (s2d * sf);
        double s2dp2f = (s2d * c2f) + (c2d * s2f);
        double c2dp2f = (c2d * c2f) - (s2d * s2f);
        double s2dm2f = (s2d * c2f) - (c2d * s2f);
        double c2dm2f = (c2d * c2f) + (s2d * s2f);
        double s4dpf = (s4d * cf) + (c4d * sf);
        double c4dpf = (c4d * cf) - (s4d * sf);
        double s4dmf = (s4d * cf) - (c4d * sf);
        return new MajorPlanet((6.28875d * smm) + (2.33E-4d * sadd1) + l + sadd + (0.001964d * sn) + (1.274018d * s2dmmm) + (0.658309d * s2d) + ((((((((((((((((((((((((((((((((((((((0.213616d * s2mm) - ((0.185596d * e) * ssm)) - (0.114336d * s2f)) + (0.058793d * ((s2d * c2mm) - (c2d * s2mm)))) + ((0.057212d * e) * ((s2d * csmpmm) - (c2d * ssmpmm)))) + (0.05332d * ((s2d * cmm) + (c2d * smm)))) + ((0.045874d * e) * ((s2d * csm) - (c2d * ssm)))) - ((0.041024d * e) * ssmmmm)) - (0.034718d * sd)) - ((0.030465d * e) * ssmpmm)) + (0.015326d * ((s2d * c2f) - (c2d * s2f)))) - (0.012528d * ((s2f * cmm) + (c2f * smm)))) - (0.01098d * ((s2f * cmm) - (c2f * smm)))) + (0.010674d * ((s4d * cmm) - (c4d * smm)))) + (0.010034d * s3mm)) + (0.008548d * ((s4d * c2mm) - (c4d * s2mm)))) - ((0.00791d * e) * ((ssmmmm * c2d) + (csmmmm * s2d)))) - ((0.006783d * e) * ((s2d * csm) + (c2d * ssm)))) + (0.005162d * ((smm * cd) - (cmm * sd)))) + ((0.005d * e) * ((ssm * cd) + (csm * sd)))) + ((0.004049d * e) * ((s2d * csmmmm) - (c2d * ssmmmm)))) + (0.003996d * ((s2mm * c2d) + (c2mm * s2d)))) + (0.003862d * s4d)) + (0.003665d * ((s2d * c3mm) - (c2d * s3mm)))) + ((0.002695d * e) * ((s2mm * csm) - (c2mm * ssm)))) + (0.002602d * ((smm * c2dp2f) - (cmm * s2dp2f)))) + ((0.002396d * e) * ((s2dmsm * c2mm) - (c2dmsm * s2mm)))) - (0.002349d * ((smm * cd) + (cmm * sd)))) + ((0.002249d * e2) * ((s2d * c2sm) - (c2d * s2sm)))) - ((0.002125d * e) * ((s2mm * csm) + (c2mm * ssm)))) - ((0.002079d * e2) * s2sm)) + ((0.002059d * e2) * ((s2dmmm * c2sm) - (c2dmmm * s2sm)))) - (0.001773d * ((smm * c2dm2f) + (cmm * s2dm2f)))) - (0.001595d * s2dp2f)) + ((0.00122d * e) * ((s4d * csmpmm) - (c4d * ssmpmm)))) - (0.00111d * ((s2mm * c2f) + (c2mm * s2f)))) + (8.92E-4d * ((smm * c3d) - (cmm * s3d)))) - ((8.11E-4d * e) * ((ssmpmm * c2d) + (csmpmm * s2d)))) + (7.61E-4d * e * ((s4d * csmp2m) - (c4d * ssmp2m))) + (7.17E-4d * e2 * smmm2m) + (7.04E-4d * e2 * ((smmm2m * c2d) - (cmmm2m * s2d))) + (6.93E-4d * e * ((ssmm2m * c2d) + (csmm2m * s2d))) + (5.98E-4d * e * ((s2dm2f * csm) - (c2dm2f * ssm))) + (5.5E-4d * ((smm * c4d) + (cmm * s4d))) + (5.38E-4d * 2.0d * s2mm * c2mm) + (5.21E-4d * e * ((s4d * csm) - (c4d * ssm))) + (4.86E-4d * ((s2mm * cd) - (c2mm * sd))), ((5.128189d * sf) + ((((((((((((((((((((((((((((((((((((((((((((0.280606d * ((smm * cf) + (cmm * sf))) + (0.277693d * ((smm * cf) - (cmm * sf)))) + (0.173238d * s2dmf)) + (0.055413d * ((s2dpf * cmm) - (c2dpf * smm)))) + (0.046272d * ((s2dmf * cmm) - (c2dmf * smm)))) + (0.032573d * s2dpf)) + (0.017198d * ((s2mm * cf) + (c2mm * sf)))) + (0.009267d * ((s2dmf * cmm) + (c2dmf * smm)))) + (0.008823d * ((s2mm * cf) - (c2mm * sf)))) + ((0.008247d * e) * ((s2dmf * csm) - (c2dmf * ssm)))) + (0.004323d * ((s2dmf * c2mm) - (c2dmf * s2mm)))) + (0.0042d * ((s2dpf * cmm) + (c2dpf * smm)))) - ((0.003372d * e) * ((s2dmf * csm) + (c2dmf * ssm)))) + ((0.002472d * e) * ((s2dpf * csmpmm) - (c2dpf * ssmpmm)))) + ((0.002222d * e) * ((s2dpf * csm) - (c2dpf * ssm)))) + ((0.002072d * e) * ((s2dmf * csmpmm) - (c2dmf * ssmpmm)))) + ((0.001877d * e) * ((sf * csmmmm) - (cf * ssmmmm)))) + (0.001828d * ((s4dmf * cmm) - (((c4d * cf) + (s4d * sf)) * smm)))) - ((0.001803d * e) * ((sf * csm) + (cf * ssm)))) - (0.00175d * s3f)) - ((0.00157d * e) * ((ssmmmm * cf) + (csmmmm * sf)))) - (0.001487d * ((sf * cd) + (cf * sd)))) - ((0.001481d * e) * ((sf * csmpmm) + (cf * ssmpmm)))) + ((0.001417d * e) * ((sf * csmpmm) - (cf * ssmpmm)))) + ((0.00135d * e) * ((sf * csm) - (cf * ssm)))) + (0.00133d * ((sf * cd) - (cf * sd)))) + (0.001106d * ((sf * c3mm) + (cf * s3mm)))) + (0.00102d * s4dmf)) + (8.33E-4d * ((s4dpf * cmm) - (c4dpf * smm)))) + (7.81E-4d * ((smm * c3f) - (cmm * s3f)))) + (6.7E-4d * ((s4dpf * c2mm) - (c4dpf * s2mm)))) + (6.06E-4d * ((s2d * c3f) - (c2d * s3f)))) + (5.97E-4d * ((s2dmf * c2mm) + (c2dmf * s2mm)))) + ((4.92E-4d * e) * ((s2dmf * csmmmm) - (c2dmf * ssmmmm)))) + (4.5E-4d * ((s2mm * c2dpf) - (c2mm * s2dpf)))) + (4.39E-4d * ((s3mm * cf) - (c3mm * sf)))) + (4.23E-4d * ((s2dpf * c2mm) + (c2dpf * s2mm)))) + (4.22E-4d * ((s2dmf * c3mm) - (c2dmf * s3mm)))) + ((3.67E-4d * e) * ((s2dpf * csmmmm) + (c2dpf * ssmmmm)))) - ((3.53E-4d * e) * ((s2dpf * csm) + (c2dpf * ssm)))) + (3.31E-4d * ((sf * c4d) + (cf * s4d)))) + ((3.17E-4d * e) * ((s2dpf * csmmmm) - (c2dpf * ssmmmm)))) + ((3.06E-4d * e2) * ((s2dmf * c2sm) - (c2dmf * s2sm)))) - (2.83E-4d * ((smm * c3f) + (cmm * s3f))))) * ((1.0d - (4.664E-4d * cn)) - (7.54E-5d * cn2)), 4.263496512454038E-5d / Utils.sind(0.950724d + ((((((((((((((((((((((((((((((0.051818d * cmm) + (0.009531d * c2dmmm)) + (0.007843d * c2d)) + (0.002824d * c2mm)) + (8.57E-4d * ((c2d * cmm) - (s2d * smm)))) + ((5.33E-4d * e) * ((c2d * csm) + (s2d * ssm)))) + ((4.01E-4d * e) * ((c2d * csmpmm) + (s2d * ssmpmm)))) + ((3.2E-4d * e) * csmmmm)) - (2.71E-4d * cd)) - ((2.64E-4d * e) * csmpmm)) - (1.98E-4d * ((c2f * cmm) + (s2f * smm)))) + (1.73E-4d * c3mm)) + (1.67E-4d * ((c4d * cmm) + (s4d * smm)))) - ((1.11E-4d * e) * csm)) + (1.03E-4d * ((c4d * c2mm) + (s4d * s2mm)))) - (8.4E-5d * ((c2mm * c2d) + (s2mm * s2d)))) - ((8.3E-5d * e) * ((c2d * csm) - (s2d * ssm)))) + (7.9E-5d * ((c2d * c2mm) - (s2d * s2mm)))) + (7.2E-5d * c4d)) + ((6.4E-5d * e) * ((c2d * csmmmm) + (s2d * ssmmmm)))) - ((6.3E-5d * e) * ((c2d * csmmmm) - (s2d * ssmmmm)))) + ((4.1E-5d * e) * ((csm * cd) - (ssm * sd)))) + ((3.5E-5d * e) * csmm2m)) - (3.3E-5d * ((c3mm * c2d) + (s3mm * s2d)))) - (3.0E-5d * ((cmm * cd) - (smm * sd)))) - (2.9E-5d * ((c2f * c2d) + (s2f * s2d)))) - ((2.9E-5d * e) * ((c2mm * csm) - (s2mm * ssm)))) + ((2.6E-5d * e2) * ((c2d * c2sm) + (s2d * s2sm)))) - (2.3E-5d * ((c2dm2f * cmm) + (s2dm2f * smm)))) + ((1.9E-5d * e) * ((c4d * csmpmm) + (s4d * ssmpmm))))));
    }

    static MajorPlanet Mercury(double JD) {
        double dnr = JD - 2451545.0d;
        double t = (dnr / 36525.0d) + 1.0d;
        double g2 = 360.0d * Utils.frac(0.140023d + (0.00445036173d * dnr));
        double l1 = 360.0d * Utils.frac(0.700695d + (0.011367714d * dnr));
        double g1 = 360.0d * Utils.frac(0.485541d + (0.01136759566d * dnr));
        double f1 = 360.0d * Utils.frac(0.566441d + (0.01136762384d * dnr));
        double sg1 = Utils.sind(g1);
        double cg1 = Utils.cosd(g1);
        double sf1 = Utils.sind(f1);
        double cf1 = Utils.cosd(f1);
        double s2g1 = 2.0d * sg1 * cg1;
        double c2g1 = ((2.0d * cg1) * cg1) - 1.0d;
        double s3g1 = (s2g1 * cg1) + (c2g1 * sg1);
        double c3g1 = (c2g1 * cg1) - (s2g1 * sg1);
        double s4g1 = 2.0d * s2g1 * c2g1;
        double c4g1 = ((2.0d * c2g1) * c2g1) - 1.0d;
        double s5g1 = (s4g1 * cg1) + (c4g1 * sg1);
        double s2f1 = 2.0d * sf1 * cf1;
        double c2f1 = ((2.0d * cf1) * cf1) - 1.0d;
        double s3f1 = (s2f1 * cf1) + (c2f1 * sf1);
        return new MajorPlanet(l1 + ((((((((((((((((84378.0d + (8.0d * t)) * sg1) + (10733.0d * s2g1)) + (1892.0d * s3g1)) - (646.0d * s2f1)) + (381.0d * s4g1)) - (306.0d * ((sg1 * c2f1) - (cg1 * s2f1)))) - (274.0d * ((sg1 * c2f1) + (cg1 * s2f1)))) - (92.0d * ((s2g1 * c2f1) + (c2g1 * s2f1)))) + (83.0d * s5g1)) - (28.0d * ((s3g1 * c2f1) + (c3g1 * s2f1)))) + (25.0d * ((s2g1 * c2f1) - (c2g1 * s2f1)))) + (19.0d * ((2.0d * s3g1) * c3g1))) - (9.0d * ((s4g1 * c2f1) + (c4g1 * s2f1)))) + (7.0d * Utils.cosd((2.0d * g1) - (5.0d * g2)))) / 3600.0d), (((((((((((24134.0d * sf1) + (5180.0d * ((sg1 * cf1) - (cg1 * sf1)))) + (4910.0d * ((sg1 * cf1) + (cg1 * sf1)))) + (1124.0d * ((s2g1 * cf1) + (c2g1 * sf1)))) + (271.0d * ((s3g1 * cf1) + (c3g1 * sf1)))) + (132.0d * ((s2g1 * cf1) - (c2g1 * sf1)))) + (67.0d * ((s4g1 * cf1) + (c4g1 * sf1)))) + (18.0d * ((s3g1 * cf1) - (c3g1 * sf1)))) + (17.0d * ((s5g1 * cf1) + (((c4g1 * cg1) - (s4g1 * sg1)) * sf1)))) - (10.0d * s3f1)) - (9.0d * ((sg1 * ((c2f1 * cf1) - (s2f1 * sf1))) - (cg1 * s3f1)))) / 3600.0d, (((0.39528d - (0.07834d * cg1)) - (0.00795d * c2g1)) - (0.00121d * c3g1)) - (2.2E-4d * c4g1));
    }

    static MajorPlanet Venus(double JD) {
        double dnr = JD - 2451545.0d;
        double t = (dnr / 36525.0d) + 1.0d;
        double gs = 360.0d * Utils.frac(0.993126d + (0.0027377785d * dnr));
        double sgs = Utils.sind(gs);
        double cgs = Utils.cosd(gs);
        double l2 = 360.0d * Utils.frac(0.505498d + (0.00445046867d * dnr));
        double g2 = 360.0d * Utils.frac(0.140023d + (0.00445036173d * dnr));
        double f2 = 360.0d * Utils.frac(0.292498d + (0.00445040017d * dnr));
        double sg2 = Utils.sind(g2);
        double cg2 = Utils.cosd(g2);
        double sf2 = Utils.sind(f2);
        double cf2 = Utils.cosd(f2);
        double s2g2 = 2.0d * sg2 * cg2;
        double c2g2 = ((2.0d * cg2) * cg2) - 1.0d;
        double s2gs = 2.0d * sgs * cgs;
        double c2gs = ((2.0d * cgs) * cgs) - 1.0d;
        return new MajorPlanet(l2 + (((((((2814.0d - (20.0d * t)) * sg2) - (181.0d * ((2.0d * sf2) * cf2))) + (12.0d * s2g2)) - (10.0d * ((c2gs * c2g2) + (s2gs * s2g2)))) + (7.0d * ((((c2gs * cgs) - (s2gs * sgs)) * ((c2g2 * cg2) - (s2g2 * sg2))) + (((s2gs * cgs) + (c2gs * sgs)) * ((s2g2 * cg2) + (c2g2 * sg2)))))) / 3600.0d), (((12215.0d * sf2) + (83.0d * ((sg2 * cf2) + (cg2 * sf2)))) + (83.0d * ((sg2 * cf2) - (cg2 * sf2)))) / 3600.0d, 0.72335d - (0.00493d * cg2));
    }

    static MajorPlanet Mars(double JD) {
        double dnr = JD - 2451545.0d;
        double t = (dnr / 36525.0d) + 1.0d;
        double gs = 360.0d * Utils.frac(0.993126d + (0.0027377785d * dnr));
        double sgs = Utils.sind(gs);
        double cgs = Utils.cosd(gs);
        double g2 = 360.0d * Utils.frac(0.140023d + (0.00445036173d * dnr));
        double g5 = 360.0d * Utils.frac(0.056531d + (2.3080893E-4d * dnr));
        double l4 = 360.0d * Utils.frac(0.987353d + (0.00145575328d * dnr));
        double g4 = 360.0d * Utils.frac(0.053856d + (0.00145561327d * dnr));
        double f4 = 360.0d * Utils.frac(0.849694d + (0.00145569465d * dnr));
        double sg4 = Utils.sind(g4);
        double cg4 = Utils.cosd(g4);
        double sf4 = Utils.sind(f4);
        double cf4 = Utils.cosd(f4);
        double sg2 = Utils.sind(g2);
        double cg2 = Utils.cosd(g2);
        double sg5 = Utils.sind(g5);
        double cg5 = Utils.cosd(g5);
        double s2gs = 2.0d * sgs * cgs;
        double c2gs = ((2.0d * cgs) * cgs) - 1.0d;
        double s2g4 = 2.0d * sg4 * cg4;
        double c2g4 = ((2.0d * cg4) * cg4) - 1.0d;
        double s3g4 = (s2g4 * cg4) + (c2g4 * sg4);
        double c3g4 = (c2g4 * cg4) - (s2g4 * sg4);
        double s4g4 = 2.0d * s2g4 * c2g4;
        double s2f4 = 2.0d * sf4 * cf4;
        double c2f4 = ((2.0d * cf4) * cf4) - 1.0d;
        double s2g5 = 2.0d * sg5 * cg5;
        double c2g5 = ((2.0d * cg5) * cg5) - 1.0d;
        return new MajorPlanet(l4 + (((((((((((((((((((((((38451.0d + (37.0d * t)) * sg4) + ((2238.0d + (4.0d * t)) * s2g4)) + (181.0d * s3g4)) - (52.0d * s2f4)) - (22.0d * ((cg4 * c2g5) + (sg4 * s2g5)))) - (19.0d * ((sg4 * cg5) - (cg4 * sg5)))) + (17.0d * ((cg4 * cg5) + (sg4 * sg5)))) + (17.0d * s4g4)) - (16.0d * ((c2g4 * c2g5) + (s2g4 * s2g5)))) + (13.0d * ((cgs * c2g4) + (sgs * s2g4)))) - (10.0d * ((sg4 * c2f4) - (cg4 * s2f4)))) - (10.0d * ((sg4 * c2f4) + (cg4 * s2f4)))) + (7.0d * ((cgs * cg4) + (sgs * sg4)))) - (7.0d * ((c2gs * c3g4) + (s2gs * s3g4)))) - (5.0d * ((sg2 * c3g4) - (cg2 * s3g4)))) - (5.0d * ((sgs * cg4) - (cgs * sg4)))) - (5.0d * ((sgs * c2g4) - (cgs * s2g4)))) - (4.0d * ((c2gs * (((2.0d * c2g4) * c2g4) - 1.0d)) + (s2gs * s4g4)))) + (4.0d * cg5)) + (3.0d * ((cg2 * c3g4) + (sg2 * s3g4)))) + (3.0d * ((s2g4 * c2g5) - (c2g4 * s2g5)))) / 3600.0d), ((((6603.0d * sf4) + (622.0d * ((sg4 * cf4) - (cg4 * sf4)))) + (615.0d * ((sg4 * cf4) + (cg4 * sf4)))) + (64.0d * ((s2g4 * cf4) + (c2g4 * sf4)))) / 3600.0d, ((1.53031d - (0.1417d * cg4)) - (0.0066d * c2g4)) - (4.7E-4d * c3g4));
    }

    static MajorPlanet Jupiter(double JD) {
        double t = (JD - 2415020.0d) / 36525.0d;
        double u = (0.2d * t) + 0.1d;
        double ex = 0.04833475d + ((1.6418E-4d + ((-4.676E-7d - (1.7E-9d * t)) * t)) * t);
        double w = 273.277558d + ((0.5994317d + ((7.0405E-4d + (5.08E-6d * t)) * t)) * t);
        double n = 99.443414d + ((1.01053d + (3.5222E-4d - (8.51E-6d * t))) * t);
        double M = Utils.rev180(((Utils.rev180(238.049257d + (3036.301986d * t)) + ((t * t) * (3.347E-4d - (1.65E-6d * t)))) - w) - n);
        double q = 265.9165d + (1222.1139d * t);
        double v = 134.6314d + (40.7573d * t);
        double z = 28.44095d - (1812.7922d * t);
        double sinq = Utils.sind(q);
        double cosq = Utils.cosd(q);
        double sin2q = 2.0d * sinq * cosq;
        double cos2q = ((2.0d * cosq) * cosq) - 1.0d;
        double sinv = Utils.sind(v);
        double cosv = Utils.cosd(v);
        double sin2v = 2.0d * sinv * cosv;
        double sinz = Utils.sind(z);
        double cosz = Utils.cosd(z);
        double sin2z = 2.0d * sinz * cosz;
        double cos2z = ((2.0d * cosz) * cosz) - 1.0d;
        double sin3z = (sinz * cos2z) + (cosz * sin2z);
        double cos3z = (cosz * cos2z) - (sinz * sin2z);
        double sin4z = 2.0d * sin2z * cos2z;
        double cos4z = ((2.0d * cos2z) * cos2z) - 1.0d;
        double dw = (((((((((((((((((((0.007192d - (0.003147d * u)) * sinv) + ((-0.020428d + ((-6.75E-4d + (1.97E-4d * u)) * u)) * cosv)) + (((0.007269d + (6.72E-4d * u)) * sinz) * sinq)) - (0.004344d * sinq)) + ((0.034036d * cosz) * sinq)) + ((0.005614d * cos2z) * sinq)) + ((0.002964d * cos3z) * sinq)) + ((0.037761d * sinz) * cosq)) + ((0.006158d * sin2z) * cosq)) - ((0.006603d * cosz) * cosq)) - ((0.005356d * sinz) * sin2q)) + ((0.002722d * sin2z) * sin2q)) + ((0.004483d * cosz) * sin2q)) - ((0.002642d * cos2z) * sin2q)) + ((0.004403d * sinz) * cos2q)) - ((0.002536d * sin2z) * cos2q)) + ((0.005547d * cosz) * cos2q)) - ((0.002689d * cos2z) * cos2q)) / ex;
        double M2 = M + (((((((((((((((((((((((((0.331364d - ((0.010281d + (0.004692d * u)) * u)) * sinv) + ((0.003228d + ((-0.064436d + (0.002075d * u)) * u)) * cosv)) - ((0.003083d + ((2.75E-4d - (4.89E-4d * u)) * u)) * sin2v)) + (0.002472d * Utils.sind(330.00373d + (22.5319d * t)))) + (0.013619d * sinz)) + (0.018472d * sin2z)) + (0.006717d * sin3z)) + (0.002775d * sin4z)) + (((0.007275d - (0.001253d * u)) * sinz) * sinq)) + ((0.006417d * sin2z) * sinq)) + ((0.002439d * sin3z) * sinq)) - (((0.033839d + (0.001125d * u)) * cosz) * sinq)) - ((0.003767d * cos2z) * sinq)) - (((0.035681d + (0.001208d * u)) * sinz) * cosq)) - ((0.004261d * sin2z) * cosq)) + (0.002178d * cosq)) + (((-0.006333d + (0.001161d * u)) * cosz) * cosq)) - ((0.006675d * cos2z) * cosq)) - ((0.002664d * cos3z) * cosq)) - ((0.002572d * sinz) * sin2q)) - ((0.003567d * sin2z) * sin2q)) + ((0.002094d * cosz) * cos2q)) + ((0.003342d * cos2z) * cos2q)) - dw);
        Pol2 p2 = Utils.m2vr(M2, ex + ((((((((((((((((((((((((((((((3.606E-4d + ((1.3E-5d - (4.3E-6d * u)) * u)) * sinv) + ((1.289E-4d - (5.8E-5d * u)) * cosv)) - ((6.764E-4d * sinz) * sinq)) - ((1.11E-4d * sin2z) * sinq)) - ((2.24E-5d * sin3z) * sinq)) - (2.04E-5d * sinq)) + (((1.284E-4d + (1.16E-5d * u)) * cosz) * sinq)) + ((1.88E-5d * cos2z) * sinq)) + (((1.46E-4d + (1.3E-5d * u)) * sinz) * cosq)) + ((2.24E-5d * sin2z) * cosq)) - (8.17E-5d * cosq)) + ((6.074E-4d * cosz) * cosq)) + ((9.92E-5d * cos2z) * cosq)) + ((5.08E-5d * cos3z) * cosq)) + ((2.3E-5d * cos4z) * cosq)) + ((1.08E-5d * ((cosz * cos4z) - (sinz * sin4z))) * cosq)) - (((9.56E-5d + (7.3E-6d * u)) * sinz) * sin2q)) + ((4.48E-5d * sin2z) * sin2q)) + ((1.37E-5d * sin3z) * sin2q)) + (((-9.97E-5d + (1.08E-5d * u)) * cosz) * sin2q)) + ((4.8E-5d * cos2z) * sin2q)) + ((1.48E-5d * cos3z) * sin2q)) + (((-9.56E-5d + (9.9E-6d * u)) * sinz) * cos2q)) + ((4.9E-5d * sin2z) * cos2q)) + ((1.58E-5d * sin3z) * cos2q)) + (1.79E-5d * cos2q)) + (((1.024E-4d + (7.5E-6d * u)) * cosz) * cos2q)) - ((4.37E-5d * cos2z) * cos2q)) - ((1.32E-5d * cos3z) * cos2q)), 5.202561d + (((((((((((-2.63E-4d * cosv) + (2.05E-4d * cosz)) + (6.93E-4d * cos2z)) + (3.12E-4d * cos3z)) + (1.47E-4d * cos4z)) + ((2.99E-4d * sinz) * sinq)) + ((1.81E-4d * cos2z) * sinq)) + ((2.04E-4d * sin2z) * cosq)) + ((1.11E-4d * sin3z) * cosq)) - ((3.37E-4d * cosz) * cosq)) - ((1.11E-4d * cos2z) * cosq)));
        double d = p2.f43v;
        double d2 = p2.f42r;
        Rect3 r3 = Utils.helpos(d, d2, w + dw, 1.308736d + ((-0.0056961d + (3.9E-6d * t)) * t), n);
        return new MajorPlanet(Utils.atan2d(r3.f45y, r3.f44x), Utils.atan2d(r3.f46z, Utils.sqsum(r3.f44x, r3.f45y)), Utils.sqsum(r3.f44x, r3.f45y, r3.f46z));
    }

    static MajorPlanet Saturn(double JD) {
        double t = (JD - 2415020.0d) / 36525.0d;
        double u = (0.2d * t) + 0.1d;
        double ex = 0.05589232d + ((-3.455E-4d + ((-7.28E-7d - (7.4E-10d * t)) * t)) * t);
        double w = 338.3078d + ((1.0852207d + ((9.7854E-4d + (9.92E-6d * t)) * t)) * t);
        double n = 112.790414d + ((0.8731951d + (-1.5218E-4d - (5.31E-6d * t))) * t);
        double M = Utils.rev180(((Utils.rev180(266.564377d + (1223.509884d * t)) + ((t * t) * (3.245E-4d - (5.8E-6d * t)))) - w) - n);
        double q = 265.9165d + (1222.1139d * t);
        double v = 134.6314d + (40.7573d * t);
        double z = 28.44095d - (1812.7922d * t);
        double psi = 337.60071d - (793.6462d * t);
        double sinq = Utils.sind(q);
        double cosq = Utils.cosd(q);
        double sin2q = 2.0d * sinq * cosq;
        double cos2q = ((2.0d * cosq) * cosq) - 1.0d;
        double sin3q = (sinq * cos2q) + (cosq * sin2q);
        double cos3q = (cosq * cos2q) - (sinq * sin2q);
        double sinv = Utils.sind(v);
        double cosv = Utils.cosd(v);
        double sin2v = 2.0d * sinv * cosv;
        double cos2v = ((2.0d * cosv) * cosv) - 1.0d;
        double sinz = Utils.sind(z);
        double cosz = Utils.cosd(z);
        double sin2z = 2.0d * sinz * cosz;
        double cos2z = ((2.0d * cosz) * cosz) - 1.0d;
        double sin3z = (sinz * cos2z) + (cosz * sin2z);
        double cos3z = (cosz * cos2z) - (sinz * sin2z);
        double sin4z = 2.0d * sin2z * cos2z;
        double cos4z = ((2.0d * cos2z) * cos2z) - 1.0d;
        double sin5z = (sinz * cos4z) + (cosz * sin4z);
        double cos5z = (cosz * cos4z) - (sinz * sin4z);
        double sinps = Utils.sind(psi);
        double cosps = Utils.cosd(psi);
        double sin2ps = 2.0d * sinps * cosps;
        double cos2ps = ((2.0d * cosps) * cosps) - 1.0d;
        double sin3ps = (sinps * cos2ps) + (cosps * sin2ps);
        double cos3ps = (cosps * cos2ps) - (sinps * sin2ps);
        double dw = ((((((((((((((((((0.077108d + ((0.007186d - (0.001533d * u)) * u)) * sinv) + ((0.045803d - ((0.014766d + (5.36E-4d * u)) * u)) * cosv)) - (0.007075d * sinz)) - ((0.075825d * sinz) * sinq)) - ((0.024839d * sin2z) * sinq)) - ((0.008631d * sin3z) * sinq)) - (0.072586d * cosq)) - ((0.150383d * cosz) * cosq)) + ((0.026897d * cos2z) * cosq)) + ((0.010053d * cos3z) * cosq)) - (((0.013597d + (0.001719d * u)) * sinz) * sin2q)) + (((-0.007742d + (0.001517d * u)) * cosz) * sin2q)) + (((0.013586d - (0.001375d * u)) * cos2z) * sin2q)) + (((-0.013667d + (0.001239d * u)) * sinz) * cos2q)) + ((0.011981d * sin2z) * cos2q)) + (((0.014861d + (0.001136d * u)) * cosz) * cos2q)) - (((0.013064d + (0.001628d * u)) * cos2z) * cos2q)) / ex;
        double M2 = M + ((((((((((((((((((((((((((-0.814181d + ((0.01815d + (0.016714d * u)) * u)) * sinv) + ((-0.010497d + ((0.160906d - (0.0041d * u)) * u)) * cosv)) + (0.007581d * sin2v)) - (0.007986d * Utils.sind(330.00373d + (22.5319d * t)))) - (0.148811d * sinz)) - (0.040786d * sin2z)) - (0.015208d * sin3z)) - (0.006339d * sin4z)) - (0.006244d * sinq)) + (((0.008931d + (0.002728d * u)) * sinz) * sinq)) - ((0.0165d * sin2z) * sinq)) - ((0.005775d * sin3z) * sinq)) + (((0.081344d + (0.003206d * u)) * cosz) * sinq)) + ((0.015019d * cos2z) * sinq)) + (((0.085581d + (0.002494d * u)) * sinz) * cosq)) + (((0.025328d - (0.003117d * u)) * cosz) * cosq)) + ((0.014394d * cos2z) * cosq)) + ((0.006319d * cos3z) * cosq)) + ((0.006369d * sinz) * sin2q)) + ((0.009156d * sin2z) * sin2q)) + ((0.007525d * sin3ps) * sin2q)) - ((0.005236d * cosz) * cos2q)) - ((0.007736d * cos2z) * cos2q)) - ((0.007528d * cos3ps) * cos2q)) - dw);
        double dlat = ((((((7.47E-4d * cosz) * sinq) + ((0.001069d * cosz) * cosq)) + ((0.002108d * sin2z) * sin2q)) + ((0.001261d * cos2z) * sin2q)) + ((0.001236d * sin2z) * cos2q)) - ((0.002075d * cos2z) * cos2q);
        Pol2 p2 = Utils.m2vr(M2, ex + ((((((((((((((((((((((((((((((((((((((((((((((((((-7.927E-4d + ((2.548E-4d + (9.1E-6d * u)) * u)) * sinv) + ((0.0013381d + ((1.226E-4d - (2.53E-5d * u)) * u)) * cosv)) + ((2.48E-5d - (1.21E-5d * u)) * sin2v)) - ((3.05E-5d + (9.1E-6d * u)) * cos2v)) + (4.12E-5d * sin2z)) + (0.0012415d * sinq)) + (((3.9E-5d - (6.17E-5d * u)) * sinz) * sinq)) + (((1.65E-5d - (2.04E-5d * u)) * sin2z) * sinq)) + ((0.0026599d * cosz) * sinq)) - ((4.687E-4d * cos2z) * sinq)) - ((1.87E-4d * cos3z) * sinq)) - ((8.21E-5d * cos4z) * sinq)) - ((3.77E-5d * cos5z) * sinq)) + ((4.97E-5d * cos2ps) * sinq)) + ((1.63E-5d - (6.11E-5d * u)) * cosq)) - ((0.0012696d * sinz) * cosq)) - ((4.2E-4d * sin2z) * cosq)) - ((1.503E-4d * sin3z) * cosq)) - ((6.19E-5d * sin4z) * cosq)) - ((2.68E-5d * sin5z) * cosq)) - (((2.82E-5d + (1.306E-4d * u)) * cosz) * cosq)) + (((-8.6E-6d + (2.3E-5d * u)) * cos2z) * cosq)) + ((4.61E-5d * sin2ps) * cosq)) - (3.5E-5d * sin2q)) + (((2.211E-4d - (2.86E-5d * u)) * sinz) * sin2q)) - ((2.208E-4d * sin2z) * sin2q)) - ((5.68E-5d * sin3z) * sin2q)) - ((3.46E-5d * sin4z) * sin2q)) - (((2.78E-4d + (2.22E-5d * u)) * cosz) * sin2q)) + (((2.022E-4d + (2.63E-5d * u)) * cos2z) * sin2q)) + ((2.48E-5d * cos3z) * sin2q)) + ((2.42E-5d * sin3ps) * sin2q)) + ((4.67E-5d * cos3ps) * sin2q)) - (4.9E-5d * cos2q)) - (((2.842E-4d + (2.79E-5d * u)) * sinz) * cos2q)) + (((1.28E-5d + (2.26E-5d * u)) * sin2z) * cos2q)) + ((2.24E-5d * sin3z) * cos2q)) + (((-1.594E-4d + (2.82E-5d * u)) * cosz) * cos2q)) + (((2.162E-4d - (2.07E-5d * u)) * cos2z) * cos2q)) + ((5.61E-5d * cos3z) * cos2q)) + ((3.43E-5d * cos4z) * cos2q)) + ((4.69E-5d * sin3ps) * cos2q)) - ((2.42E-5d * cos3ps) * cos2q)) - ((2.05E-5d * sinz) * sin3q)) + ((2.62E-5d * sin3z) * sin3q)) + ((2.08E-5d * cosz) * cos3q)) - ((2.71E-5d * cos3z) * cos3q)) - ((3.82E-5d * cos3z) * ((2.0d * sin2q) * cos2q))) - ((3.76E-5d * sin3z) * (((2.0d * cos2q) * cos2q) - 1.0d))), 9.554747d + (((((((((((((((((((((((((((((((((((((5.72E-4d * u) * sinv) + (0.002933d * cosv)) + (0.033629d * cosz)) - (0.003081d * cos2z)) - (0.001423d * cos3z)) - (6.71E-4d * cos4z)) - (3.2E-4d * cos5z)) + (0.001098d * sinq)) - ((0.002812d * sinz) * sinq)) + ((6.88E-4d * sin2z) * sinq)) - ((3.93E-4d * sin3z) * sinq)) - ((2.28E-4d * sin4z) * sinq)) + ((0.002138d * cosz) * sinq)) - ((9.99E-4d * cos2z) * sinq)) - ((6.42E-4d * cos3z) * sinq)) - ((3.25E-4d * cos4z) * sinq)) - (8.9E-4d * cosq)) + ((0.002206d * sinz) * cosq)) - ((0.00159d * sin2z) * cosq)) - ((6.47E-4d * sin3z) * cosq)) - ((3.44E-4d * sin4z) * cosq)) + ((0.002885d * cosz) * cosq)) + (((0.002172d + (1.02E-4d * u)) * cos2z) * cosq)) + ((2.96E-4d * cos3z) * cosq)) - ((2.67E-4d * sin2z) * sin2q)) - ((7.78E-4d * cosz) * sin2q)) + ((4.95E-4d * cos2z) * sin2q)) + ((2.5E-4d * cos3z) * sin2q)) - ((8.56E-4d * sinz) * cos2q)) + ((4.41E-4d * sin2z) * cos2q)) + ((2.96E-4d * cos2z) * cos2q)) + ((2.11E-4d * cos3z) * cos2q)) - ((4.27E-4d * sinz) * sin3q)) + ((3.98E-4d * sin3z) * sin3q)) + ((3.44E-4d * cosz) * cos3q)) - ((4.27E-4d * cos3z) * cos3q)));
        double d = p2.f43v;
        double d2 = p2.f42r;
        Rect3 r3 = Utils.helpos(d, d2, w + dw, 2.492519d + ((-0.0039189d + -1.549E-5d + (4.0E-8d * t)) * t), n);
        return new MajorPlanet(Utils.atan2d(r3.f45y, r3.f44x), Utils.atan2d(r3.f46z, Utils.sqsum(r3.f44x, r3.f45y)) + dlat, Utils.sqsum(r3.f44x, r3.f45y, r3.f46z));
    }

    static MajorPlanet Uranus(double JD) {
        double t = (JD - 2415020.0d) / 36525.0d;
        double u = (0.2d * t) + 0.1d;
        double s = 243.51721d + (428.4677d * t);
        double g = 83.76922d + (218.4901d * t);
        double h = (2.0d * g) - s;
        double th = g - s;
        double sinh = Utils.sind(h);
        double cosh = Utils.cosd(h);
        double sin2h = 2.0d * sinh * cosh;
        double cos2h = ((2.0d * cosh) * cosh) - 1.0d;
        double sinth = Utils.sind(th);
        double costh = Utils.cosd(th);
        double sin2th = 2.0d * sinth * costh;
        double cos2th = ((2.0d * costh) * costh) - 1.0d;
        double ex = 0.0463444d + ((-2.658E-5d + (7.7E-8d * t)) * t);
        double w = 98.071581d + ((0.985765d + ((-0.0010745d - (6.1E-7d * t)) * t)) * t);
        double n = 73.477111d + ((0.4986678d + (0.0013117d * t)) * t);
        double M = Utils.rev180(((Utils.rev180(244.19747d + (429.863546d * t)) + ((t * t) * (3.16E-4d - (6.0E-7d * t)))) - w) - n);
        double zt = s - (237.47555d + (3034.9061d * t));
        double et = s - (265.9165d + (1222.1139d * t));
        double sinzt = Utils.sind(zt);
        double coszt = Utils.cosd(zt);
        double sinet = Utils.sind(et);
        double coset = Utils.cosd(et);
        double sin3th = (sinth * cos2th) + (costh * sin2th);
        double cos3th = (costh * cos2th) - (sinth * sin2th);
        double sins = Utils.sind(s);
        double coss = Utils.cosd(s);
        double sin2s = 2.0d * sins * coss;
        double cos2s = ((2.0d * coss) * coss) - 1.0d;
        double dw = (((0.120303d * sinh) + ((0.019472d - (9.47E-4d * u)) * cosh)) + (0.006197d * sin2h)) / ex;
        double dlon = ((((((((((0.010122d - (9.88E-4d * u)) * ((sins * coset) + (coss * sinet))) + ((-0.038581d + ((0.002031d - (0.00191d * u)) * u)) * ((coss * coset) - (sins * sinet)))) + ((0.034964d + ((-0.001038d + (8.68E-4d * u)) * u)) * ((cos2s * coset) - (sin2s * sinet)))) + (0.005594d * ((sins * cos3th) + (coss * sin3th)))) - (0.014808d * sinzt)) - (0.005794d * sinet)) + (0.002347d * coset)) + (0.009872d * sinth)) + (0.008803d * sin2th)) - (0.004308d * sin3th);
        double dlat = (((((4.58E-4d * sinet) - (6.42E-4d * coset)) - (5.17E-4d * (((2.0d * cos2th) * cos2th) - 1.0d))) * sins) - ((((3.47E-4d * sinet) + (8.53E-4d * coset)) + (5.17E-4d * (((4.0d * sinet) * coset) * (1.0d - ((2.0d * sinet) * sinet))))) * coss)) + (4.03E-4d * ((cos2th * sin2s) + (sin2th * cos2s)));
        double dr = ((-0.025948d + (0.004985d * coszt)) - (0.00123d * coss)) + (0.003354d * coset) + ((((0.005795d * coss) - (0.001165d * sins)) + (0.001388d * cos2s)) * sinet) + (((0.001351d * coss) + (0.005702d * sins) + (0.001388d * sin2s)) * coset) + (9.04E-4d * cos2th) + (8.94E-4d * (costh - cos3th));
        double M2 = M + (((((((0.864319d - (0.001583d * u)) * sinh) + ((0.082222d - (0.006833d * u)) * cosh)) + (0.036017d * sin2h)) - (0.003019d * cos2h)) + (0.008122d * Utils.sind(330.00373d + (22.5319d * t)))) - dw);
        Pol2 p2 = Utils.m2vr(M2, ex + ((-3.349E-4d + (1.63E-5d * u)) * sinh) + (0.0020981d * cosh) + (1.311E-4d * cos2h), 19.21814d + (-0.003825d * cosh));
        double d = p2.f43v;
        double d2 = p2.f42r;
        Rect3 r3 = Utils.helpos(d, d2, w + dw, 0.772464d + ((6.253E-4d + (3.95E-5d * t)) * t), n);
        return new MajorPlanet(Utils.atan2d(r3.f45y, r3.f44x) + dlon, Utils.atan2d(r3.f46z, Math.sqrt((r3.f44x * r3.f44x) + (r3.f45y * r3.f45y))) + dlat, Math.sqrt((r3.f44x * r3.f44x) + (r3.f45y * r3.f45y) + (r3.f46z * r3.f46z)) + dr);
    }

    static MajorPlanet Neptune(double JD) {
        double t = (JD - 2415020.0d) / 36525.0d;
        double u = (0.2d * t) + 0.1d;
        double s = 243.51721d + (428.4677d * t);
        double g = 83.76922d + (218.4901d * t);
        double h = (2.0d * g) - s;
        double th = g - s;
        double sinh = Utils.sind(h);
        double cosh = Utils.cosd(h);
        double sin2h = 2.0d * sinh * cosh;
        double cos2h = ((2.0d * cosh) * cosh) - 1.0d;
        double sinth = Utils.sind(th);
        double costh = Utils.cosd(th);
        double sin2th = 2.0d * sinth * costh;
        double cos2th = ((2.0d * costh) * costh) - 1.0d;
        double ex = 0.00899704d + ((6.33E-6d - (2.0E-9d * t)) * t);
        double w = 276.045975d + ((0.3256394d + ((1.4095E-4d + (4.113E-6d * t)) * t)) * t);
        double n = 130.681389d + ((1.098935d + ((2.4987E-4d - (4.718E-6d * t)) * t)) * t);
        double M = Utils.rev180(((Utils.rev180(84.457994d + (219.885914d * t)) + ((t * t) * (3.205E-4d - (6.0E-7d * t)))) - w) - n);
        double zt = g - (237.47555d + (3034.9061d * t));
        double et = g - (265.9165d + (1222.1139d * t));
        double sinzt = Utils.sind(zt);
        double coszt = Utils.cosd(zt);
        double sinet = Utils.sind(et);
        double coset = Utils.cosd(et);
        double sing = Utils.sind(g);
        double cosg = Utils.cosd(g);
        double dw = ((((0.024039d * sinh) - (0.025303d * cosh)) + (0.006206d * sin2h)) - (0.005992d * cos2h)) / ex;
        double dlon = ((((-0.009556d * sinzt) - (0.005178d * sinet)) + (0.002572d * sin2th)) - ((0.002972d * cos2th) * sing)) - ((0.002833d * sin2th) * cosg);
        double dlat = (3.36E-4d * cos2th * sing) + (3.64E-4d * sin2th * cosg);
        double dr = -0.040596d + (0.004992d * coszt) + (0.002744d * coset) + (0.002044d * costh) + (0.001051d * cos2th);
        double M2 = M + (((((-0.589833d + (0.001089d * u)) * sinh) + ((-0.056094d + (0.004658d * u)) * cosh)) - (0.024286d * sin2h)) - dw);
        Pol2 p2 = Utils.m2vr(M2, ex + (4.389E-4d * sinh) + (4.262E-4d * cosh) + (1.129E-4d * sin2h) + (1.089E-4d * cos2h), 30.10957d + (-8.17E-4d * sinh) + (0.008189d * cosh) + (7.81E-4d * cos2h));
        double d = p2.f43v;
        double d2 = p2.f42r;
        Rect3 r3 = Utils.helpos(d, d2, w + dw, 1.779242d + ((-0.0095436d - (9.1E-6d * t)) * t), n);
        return new MajorPlanet(Utils.atan2d(r3.f45y, r3.f44x) + dlon, Utils.atan2d(r3.f46z, Math.sqrt((r3.f44x * r3.f44x) + (r3.f45y * r3.f45y))) + dlat, Utils.sqsum(r3.f44x, r3.f45y, r3.f46z) + dr);
    }

    private static MajorPlanet R2P(Rect3 r3) {
        return new MajorPlanet(Utils.atan2d(r3.f45y, r3.f44x), Utils.atan2d(r3.f46z, Utils.sqsum(r3.f44x, r3.f45y)), Utils.sqsum(r3.f44x, r3.f45y, r3.f46z));
    }

    static MajorPlanet geocentric(MajorPlanet planet, MajorPlanet sun) {
        return R2P(Utils.R3sum(Utils.P2R(planet), Utils.P2R(sun)));
    }

    static MajorPlanet geocentric(MajorPlanet planet, double JD) {
        return geocentric(planet, Sun(JD));
    }
}
