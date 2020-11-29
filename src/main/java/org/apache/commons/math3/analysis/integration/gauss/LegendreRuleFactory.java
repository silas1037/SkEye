package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.Pair;

public class LegendreRuleFactory extends BaseRuleFactory<Double> {
    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.integration.gauss.BaseRuleFactory
    public Pair<Double[], Double[]> computeRule(int numberOfPoints) throws DimensionMismatchException {
        double b;
        if (numberOfPoints == 1) {
            return new Pair<>(new Double[]{Double.valueOf(0.0d)}, new Double[]{Double.valueOf(2.0d)});
        }
        Double[] previousPoints = (Double[]) getRuleInternal(numberOfPoints - 1).getFirst();
        Double[] points = new Double[numberOfPoints];
        Double[] weights = new Double[numberOfPoints];
        int iMax = numberOfPoints / 2;
        int i = 0;
        while (i < iMax) {
            double a = i == 0 ? -1.0d : previousPoints[i - 1].doubleValue();
            if (iMax == 1) {
                b = 1.0d;
            } else {
                b = previousPoints[i].doubleValue();
            }
            double pma = 1.0d;
            double pa = a;
            double pmb = 1.0d;
            double pb = b;
            for (int j = 1; j < numberOfPoints; j++) {
                int two_j_p_1 = (j * 2) + 1;
                int j_p_1 = j + 1;
                double ppa = (((((double) two_j_p_1) * a) * pa) - (((double) j) * pma)) / ((double) j_p_1);
                pma = pa;
                pa = ppa;
                pmb = pb;
                pb = (((((double) two_j_p_1) * b) * pb) - (((double) j) * pmb)) / ((double) j_p_1);
            }
            double c = 0.5d * (a + b);
            double pmc = 1.0d;
            double pc = c;
            boolean done = false;
            while (!done) {
                done = b - a <= Math.ulp(c);
                pmc = 1.0d;
                pc = c;
                for (int j2 = 1; j2 < numberOfPoints; j2++) {
                    pmc = pc;
                    pc = (((((double) ((j2 * 2) + 1)) * c) * pc) - (((double) j2) * pmc)) / ((double) (j2 + 1));
                }
                if (!done) {
                    if (pa * pc <= 0.0d) {
                        b = c;
                    } else {
                        a = c;
                        pa = pc;
                    }
                    c = 0.5d * (a + b);
                }
            }
            double d = ((double) numberOfPoints) * (pmc - (c * pc));
            double w = (2.0d * (1.0d - (c * c))) / (d * d);
            points[i] = Double.valueOf(c);
            weights[i] = Double.valueOf(w);
            int idx = (numberOfPoints - i) - 1;
            points[idx] = Double.valueOf(-c);
            weights[idx] = Double.valueOf(w);
            i++;
        }
        if (numberOfPoints % 2 != 0) {
            double pmc2 = 1.0d;
            for (int j3 = 1; j3 < numberOfPoints; j3 += 2) {
                pmc2 = (((double) (-j3)) * pmc2) / ((double) (j3 + 1));
            }
            double d2 = ((double) numberOfPoints) * pmc2;
            points[iMax] = Double.valueOf(0.0d);
            weights[iMax] = Double.valueOf(2.0d / (d2 * d2));
        }
        return new Pair<>(points, weights);
    }
}
