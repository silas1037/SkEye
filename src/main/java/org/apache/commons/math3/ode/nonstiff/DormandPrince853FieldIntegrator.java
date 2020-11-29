package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class DormandPrince853FieldIntegrator<T extends RealFieldElement<T>> extends EmbeddedRungeKuttaFieldIntegrator<T> {
    private static final String METHOD_NAME = "Dormand-Prince 8 (5, 3)";
    private final T e1_01 = fraction(1.16092271E8d, 8.84846592E9d);
    private final T e1_06 = fraction(-1871647.0d, 1527680.0d);
    private final T e1_07 = fraction(-6.9799717E7d, 1.4079366E8d);
    private final T e1_08 = fraction(1.230164450203E12d, 7.39113984E11d);
    private final T e1_09 = fraction(-1.980813971228885E15d, 5.654156025964544E15d);
    private final T e1_10 = fraction(4.64500805E8d, 1.389975552E9d);
    private final T e1_11 = fraction(1.606764981773E12d, 1.9613062656E13d);
    private final T e1_12 = fraction(-137909.0d, 6168960.0d);
    private final T e2_01 = fraction(-364463.0d, 1920240.0d);
    private final T e2_06 = fraction(3399327.0d, 763840.0d);
    private final T e2_07 = fraction(6.6578432E7d, 3.5198415E7d);
    private final T e2_08 = fraction(-1.674902723E9d, 2.887164E8d);
    private final T e2_09 = fraction(-7.4684743568175E13d, 1.76692375811392E14d);
    private final T e2_10 = fraction(-734375.0d, 4826304.0d);
    private final T e2_11 = fraction(1.71414593E8d, 8.512614E8d);
    private final T e2_12 = fraction(69869.0d, 3084480.0d);

    public DormandPrince853FieldIntegrator(Field<T> field, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
        super(field, METHOD_NAME, 12, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    public DormandPrince853FieldIntegrator(Field<T> field, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
        super(field, METHOD_NAME, 12, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getC() {
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getOne().multiply(6)).sqrt();
        T[] c = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 15));
        c[0] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-67.5d);
        c[1] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-45.0d);
        c[2] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-30.0d);
        c[3] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(6.0d)).divide(30.0d);
        c[4] = fraction(1, 3);
        c[5] = fraction(1, 4);
        c[6] = fraction(4, 13);
        c[7] = fraction(127, 195);
        c[8] = fraction(3, 5);
        c[9] = fraction(6, 7);
        c[10] = getField().getOne();
        c[11] = getField().getOne();
        c[12] = fraction(1.0d, 10.0d);
        c[13] = fraction(1.0d, 5.0d);
        c[14] = fraction(7.0d, 9.0d);
        return c;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v2, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v3, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v4, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v5, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v6, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v7, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v8, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v9, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v10, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v55, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v13, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v14, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v15, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v16, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v75, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v19, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v20, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v21, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v22, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v101, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v102, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v26, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v27, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v28, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v29, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v123, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v124, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v126, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v35, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v36, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v37, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v38, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v150, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v152, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v154, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v156, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v45, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v46, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v47, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v48, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v180, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v182, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v184, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v186, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v188, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v56, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v57, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v58, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v59, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v212, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v214, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v216, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v218, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v220, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v222, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v68, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v69, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v70, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v71, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v246, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v248, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v250, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v252, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v254, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v256, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v258, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v81, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v82, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v83, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v84, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v276, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v278, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v280, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v282, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v284, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v286, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v288, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v290, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v95, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v96, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v97, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v98, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v99, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v312, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v314, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v316, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v318, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v320, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v322, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v324, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v326, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v110, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v111, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v112, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v113, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v344, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v346, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v348, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v118, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v119, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v358, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v360, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v362, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v364, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v366, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v126, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v127, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v128, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v129, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v384, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v386, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v388, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v390, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v135, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v136, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v137, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v404, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v406, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v408, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[][] getA() {
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getOne().multiply(6)).sqrt();
        T[][] a = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(getField(), 15, -1));
        for (int i = 0; i < a.length; i++) {
            a[i] = (RealFieldElement[]) MathArrays.buildArray(getField(), i + 1);
        }
        a[0][0] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-67.5d);
        a[1][0] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-180.0d);
        a[1][1] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-60.0d);
        a[2][0] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-120.0d);
        a[2][1] = getField().getZero();
        a[2][2] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-6.0d)).divide(-40.0d);
        a[3][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(107)).add(462.0d)).divide(3000.0d);
        a[3][1] = getField().getZero();
        a[3][2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(197)).add(402.0d)).divide(-1000.0d);
        a[3][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(73)).add(168.0d)).divide(375.0d);
        a[4][0] = fraction(1, 27);
        a[4][1] = getField().getZero();
        a[4][2] = getField().getZero();
        a[4][3] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(16.0d)).divide(108.0d);
        a[4][4] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(-16.0d)).divide(-108.0d);
        a[5][0] = fraction(19, 512);
        a[5][1] = getField().getZero();
        a[5][2] = getField().getZero();
        a[5][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(23)).add(118.0d)).divide(1024.0d);
        a[5][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-23)).add(118.0d)).divide(1024.0d);
        a[5][5] = fraction(-9, 512);
        a[6][0] = fraction(13772, 371293);
        a[6][1] = getField().getZero();
        a[6][2] = getField().getZero();
        a[6][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(4784)).add(51544.0d)).divide(371293.0d);
        a[6][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-4784)).add(51544.0d)).divide(371293.0d);
        a[6][5] = fraction(-5688, 371293);
        a[6][6] = fraction(3072, 371293);
        a[7][0] = fraction(5.8656157643E10d, 9.3983540625E10d);
        a[7][1] = getField().getZero();
        a[7][2] = getField().getZero();
        a[7][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-3.18801444819E11d)).add(-1.324889724104E12d)).divide(6.265569375E11d);
        a[7][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(3.18801444819E11d)).add(-1.324889724104E12d)).divide(6.265569375E11d);
        a[7][5] = fraction(9.6044563816E10d, 3.480871875E9d);
        a[7][6] = fraction(5.682451879168E12d, 2.81950621875E11d);
        a[7][7] = fraction(-1.65125654E8d, 3796875.0d);
        a[8][0] = fraction(8909899.0d, 1.8653125E7d);
        a[8][1] = getField().getZero();
        a[8][2] = getField().getZero();
        a[8][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-1137963.0d)).add(-4521408.0d)).divide(2937500.0d);
        a[8][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(1137963.0d)).add(-4521408.0d)).divide(2937500.0d);
        a[8][5] = fraction(9.6663078E7d, 4553125.0d);
        a[8][6] = fraction(2.107245056E9d, 1.37915625E8d);
        a[8][7] = fraction(-4.913652016E9d, 1.47609375E8d);
        a[8][8] = fraction(-7.889427E7d, 3.880452869E9d);
        a[9][0] = fraction(-2.0401265806E10d, 2.1769653311E10d);
        a[9][1] = getField().getZero();
        a[9][2] = getField().getZero();
        a[9][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(94326.0d)).add(354216.0d)).divide(112847.0d);
        a[9][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-94326.0d)).add(354216.0d)).divide(112847.0d);
        a[9][5] = fraction(-4.3306765128E10d, 5.313852383E9d);
        a[9][6] = fraction(-2.0866708358144E13d, 1.126708119789E12d);
        a[9][7] = fraction(1.488600343802E13d, 6.54632330667E11d);
        a[9][8] = fraction(3.5290686222309376E16d, 1.4152473387134412E16d);
        a[9][9] = fraction(-1.477884375E9d, 4.85066827E8d);
        a[10][0] = fraction(3.9815761E7d, 1.7514443E7d);
        a[10][1] = getField().getZero();
        a[10][2] = getField().getZero();
        a[10][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-960905.0d)).add(-3457480.0d)).divide(551636.0d);
        a[10][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(960905.0d)).add(-3457480.0d)).divide(551636.0d);
        a[10][5] = fraction(-8.44554132E8d, 4.7026969E7d);
        a[10][6] = fraction(8.444996352E9d, 3.02158619E8d);
        a[10][7] = fraction(-2.509602342E9d, 8.77790785E8d);
        a[10][8] = fraction(-2.8388795297996248E16d, 3.199510091356783E15d);
        a[10][9] = fraction(2.2671625E8d, 1.8341897E7d);
        a[10][10] = fraction(1.371316744E9d, 2.131383595E9d);
        a[11][0] = fraction(104257.0d, 1920240.0d);
        a[11][1] = getField().getZero();
        a[11][2] = getField().getZero();
        a[11][3] = getField().getZero();
        a[11][4] = getField().getZero();
        a[11][5] = fraction(3399327.0d, 763840.0d);
        a[11][6] = fraction(6.6578432E7d, 3.5198415E7d);
        a[11][7] = fraction(-1.674902723E9d, 2.887164E8d);
        a[11][8] = fraction(5.4980371265625E13d, 1.76692375811392E14d);
        a[11][9] = fraction(-734375.0d, 4826304.0d);
        a[11][10] = fraction(1.71414593E8d, 8.512614E8d);
        a[11][11] = fraction(137909.0d, 3084480.0d);
        a[12][0] = fraction(1.3481885573E10d, 2.4003E11d);
        a[12][1] = getField().getZero();
        a[12][2] = getField().getZero();
        a[12][3] = getField().getZero();
        a[12][4] = getField().getZero();
        a[12][5] = getField().getZero();
        a[12][6] = fraction(1.39418837528E11d, 5.49975234375E11d);
        a[12][7] = fraction(-1.1108320068443E13d, 4.51119375E13d);
        a[12][8] = fraction(-1.769651421925959E15d, 1.424938514608E16d);
        a[12][9] = fraction(5.7799439E7d, 3.77055E8d);
        a[12][10] = fraction(7.93322643029E11d, 9.673425E13d);
        a[12][11] = fraction(1.458939311E9d, 1.9278E11d);
        a[12][12] = fraction(-4149.0d, 500000.0d);
        a[13][0] = fraction(1.595561272731E12d, 5.01202735E13d);
        a[13][1] = getField().getZero();
        a[13][2] = getField().getZero();
        a[13][3] = getField().getZero();
        a[13][4] = getField().getZero();
        a[13][5] = fraction(9.75183916491E11d, 3.445768803125E13d);
        a[13][6] = fraction(3.8492013932672E13d, 7.18912673015625E14d);
        a[13][7] = fraction(-1.114881286517557E15d, 2.02987107675E16d);
        a[13][8] = getField().getZero();
        a[13][9] = getField().getZero();
        a[13][10] = fraction(-2.538710946863E12d, 2.343122786125E16d);
        a[13][11] = fraction(8.824659001E9d, 2.306671678125E13d);
        a[13][12] = fraction(-1.1518334563E10d, 3.38311846125E13d);
        a[13][13] = fraction(1.912306948E9d, 1.3532473845E10d);
        a[14][0] = fraction(-1.3613986967E10d, 3.1741908048E10d);
        a[14][1] = getField().getZero();
        a[14][2] = getField().getZero();
        a[14][3] = getField().getZero();
        a[14][4] = getField().getZero();
        a[14][5] = fraction(-4.755612631E9d, 1.012344804E9d);
        a[14][6] = fraction(4.2939257944576E13d, 5.588559685701E12d);
        a[14][7] = fraction(7.7881972900277E13d, 1.9140370552944E13d);
        a[14][8] = fraction(2.2719829234375E13d, 6.3689648654052E13d);
        a[14][9] = getField().getZero();
        a[14][10] = getField().getZero();
        a[14][11] = getField().getZero();
        a[14][12] = fraction(-1.199007803E9d, 8.57031517296E11d);
        a[14][13] = fraction(1.57882067E11d, 5.3564469831E10d);
        a[14][14] = fraction(-2.90468882375E11d, 3.1741908048E10d);
        return a;
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getB() {
        T[] b = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 16));
        b[0] = fraction(104257, 1920240);
        b[1] = getField().getZero();
        b[2] = getField().getZero();
        b[3] = getField().getZero();
        b[4] = getField().getZero();
        b[5] = fraction(3399327.0d, 763840.0d);
        b[6] = fraction(6.6578432E7d, 3.5198415E7d);
        b[7] = fraction(-1.674902723E9d, 2.887164E8d);
        b[8] = fraction(5.4980371265625E13d, 1.76692375811392E14d);
        b[9] = fraction(-734375.0d, 4826304.0d);
        b[10] = fraction(1.71414593E8d, 8.512614E8d);
        b[11] = fraction(137909.0d, 3084480.0d);
        b[12] = getField().getZero();
        b[13] = getField().getZero();
        b[14] = getField().getZero();
        b[15] = getField().getZero();
        return b;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator
    public DormandPrince853FieldStepInterpolator<T> createInterpolator(boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldEquationsMapper<T> mapper) {
        return new DormandPrince853FieldStepInterpolator<>(getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator
    public int getOrder() {
        return 8;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator
    public T estimateError(T[][] yDotK, T[] y0, T[] y1, T h) {
        RealFieldElement realFieldElement = (RealFieldElement) h.getField().getZero();
        RealFieldElement realFieldElement2 = (RealFieldElement) h.getField().getZero();
        for (int j = 0; j < this.mainSetDimension; j++) {
            RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) yDotK[0][j].multiply(this.e1_01)).add(yDotK[5][j].multiply(this.e1_06))).add(yDotK[6][j].multiply(this.e1_07))).add(yDotK[7][j].multiply(this.e1_08))).add(yDotK[8][j].multiply(this.e1_09))).add(yDotK[9][j].multiply(this.e1_10))).add(yDotK[10][j].multiply(this.e1_11))).add(yDotK[11][j].multiply(this.e1_12));
            RealFieldElement realFieldElement4 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) yDotK[0][j].multiply(this.e2_01)).add(yDotK[5][j].multiply(this.e2_06))).add(yDotK[6][j].multiply(this.e2_07))).add(yDotK[7][j].multiply(this.e2_08))).add(yDotK[8][j].multiply(this.e2_09))).add(yDotK[9][j].multiply(this.e2_10))).add(yDotK[10][j].multiply(this.e2_11))).add(yDotK[11][j].multiply(this.e2_12));
            RealFieldElement max = MathUtils.max((RealFieldElement) y0[j].abs(), (RealFieldElement) y1[j].abs());
            RealFieldElement realFieldElement5 = (RealFieldElement) (this.vecAbsoluteTolerance == null ? ((RealFieldElement) max.multiply(this.scalRelativeTolerance)).add(this.scalAbsoluteTolerance) : ((RealFieldElement) max.multiply(this.vecRelativeTolerance[j])).add(this.vecAbsoluteTolerance[j]));
            RealFieldElement realFieldElement6 = (RealFieldElement) realFieldElement3.divide(realFieldElement5);
            realFieldElement = (RealFieldElement) realFieldElement.add(realFieldElement6.multiply(realFieldElement6));
            RealFieldElement realFieldElement7 = (RealFieldElement) realFieldElement4.divide(realFieldElement5);
            realFieldElement2 = (RealFieldElement) realFieldElement2.add(realFieldElement7.multiply(realFieldElement7));
        }
        RealFieldElement realFieldElement8 = (RealFieldElement) realFieldElement.add(realFieldElement2.multiply(0.01d));
        if (realFieldElement8.getReal() <= 0.0d) {
            realFieldElement8 = (RealFieldElement) h.getField().getOne();
        }
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) h.abs()).multiply(realFieldElement)).divide(((RealFieldElement) realFieldElement8.multiply(this.mainSetDimension)).sqrt()));
    }
}
