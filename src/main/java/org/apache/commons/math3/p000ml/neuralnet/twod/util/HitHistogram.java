package org.apache.commons.math3.p000ml.neuralnet.twod.util;

import java.lang.reflect.Array;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;
import org.apache.commons.math3.p000ml.neuralnet.MapUtils;
import org.apache.commons.math3.p000ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.p000ml.neuralnet.twod.util.LocationFinder;

/* renamed from: org.apache.commons.math3.ml.neuralnet.twod.util.HitHistogram */
public class HitHistogram implements MapDataVisualization {
    private final DistanceMeasure distance;
    private final boolean normalizeCount;

    public HitHistogram(boolean normalizeCount2, DistanceMeasure distance2) {
        this.normalizeCount = normalizeCount2;
        this.distance = distance2;
    }

    @Override // org.apache.commons.math3.p000ml.neuralnet.twod.util.MapDataVisualization
    public double[][] computeImage(NeuronSquareMesh2D map, Iterable<double[]> data) {
        int nR = map.getNumberOfRows();
        int nC = map.getNumberOfColumns();
        LocationFinder finder = new LocationFinder(map);
        int numSamples = 0;
        double[][] hit = (double[][]) Array.newInstance(Double.TYPE, nR, nC);
        for (double[] sample : data) {
            LocationFinder.Location loc = finder.getLocation(MapUtils.findBest(sample, map, this.distance));
            int row = loc.getRow();
            int col = loc.getColumn();
            double[] dArr = hit[row];
            dArr[col] = dArr[col] + 1.0d;
            numSamples++;
        }
        if (this.normalizeCount) {
            for (int r = 0; r < nR; r++) {
                for (int c = 0; c < nC; c++) {
                    double[] dArr2 = hit[r];
                    dArr2[c] = dArr2[c] / ((double) numSamples);
                }
            }
        }
        return hit;
    }
}
