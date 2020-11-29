package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;

public class HermiteRuleFactory extends BaseRuleFactory<Double> {

    /* renamed from: H0 */
    private static final double f125H0 = 0.7511255444649425d;

    /* renamed from: H1 */
    private static final double f126H1 = 1.0622519320271968d;
    private static final double SQRT_PI = 1.772453850905516d;

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.integration.gauss.BaseRuleFactory
    public Pair<Double[], Double[]> computeRule(int numberOfPoints) throws DimensionMismatchException {
        double b;
        if (numberOfPoints == 1) {
            return new Pair<>(new Double[]{Double.valueOf(0.0d)}, new Double[]{Double.valueOf((double) SQRT_PI)});
        }
        int lastNumPoints = numberOfPoints - 1;
        Double[] previousPoints = (Double[]) getRuleInternal(lastNumPoints).getFirst();
        Double[] points = new Double[numberOfPoints];
        Double[] weights = new Double[numberOfPoints];
        double sqrtTwoTimesLastNumPoints = FastMath.sqrt((double) (lastNumPoints * 2));
        double sqrtTwoTimesNumPoints = FastMath.sqrt((double) (numberOfPoints * 2));
        int iMax = numberOfPoints / 2;
        int i = 0;
        while (i < iMax) {
            double a = i == 0 ? -sqrtTwoTimesLastNumPoints : previousPoints[i - 1].doubleValue();
            if (iMax == 1) {
                b = -0.5d;
            } else {
                b = previousPoints[i].doubleValue();
            }
            double hma = f125H0;
            double ha = f126H1 * a;
            double hmb = f125H0;
            double hb = f126H1 * b;
            for (int j = 1; j < numberOfPoints; j++) {
                double jp1 = (double) (j + 1);
                double s = FastMath.sqrt(2.0d / jp1);
                double sm = FastMath.sqrt(((double) j) / jp1);
                double hpa = ((s * a) * ha) - (sm * hma);
                hma = ha;
                ha = hpa;
                hmb = hb;
                hb = ((s * b) * hb) - (sm * hmb);
            }
            double c = 0.5d * (a + b);
            double hmc = f125H0;
            double d = f126H1 * c;
            boolean done = false;
            while (!done) {
                done = b - a <= Math.ulp(c);
                hmc = f125H0;
                double hc = f126H1 * c;
                for (int j2 = 1; j2 < numberOfPoints; j2++) {
                    double jp12 = (double) (j2 + 1);
                    hmc = hc;
                    hc = ((FastMath.sqrt(2.0d / jp12) * c) * hc) - (FastMath.sqrt(((double) j2) / jp12) * hmc);
                }
                if (!done) {
                    if (ha * hc < 0.0d) {
                        b = c;
                    } else {
                        a = c;
                        ha = hc;
                    }
                    c = 0.5d * (a + b);
                }
            }
            double d2 = sqrtTwoTimesNumPoints * hmc;
            double w = 2.0d / (d2 * d2);
            points[i] = Double.valueOf(c);
            weights[i] = Double.valueOf(w);
            int idx = lastNumPoints - i;
            points[idx] = Double.valueOf(-c);
            weights[idx] = Double.valueOf(w);
            i++;
        }
        if (numberOfPoints % 2 != 0) {
            double hm = f125H0;
            for (int j3 = 1; j3 < numberOfPoints; j3 += 2) {
                hm *= -FastMath.sqrt(((double) j3) / ((double) (j3 + 1)));
            }
            double d3 = sqrtTwoTimesNumPoints * hm;
            points[iMax] = Double.valueOf(0.0d);
            weights[iMax] = Double.valueOf(2.0d / (d3 * d3));
        }
        return new Pair<>(points, weights);
    }
}
