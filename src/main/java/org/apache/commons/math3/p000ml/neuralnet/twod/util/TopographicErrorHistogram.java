package org.apache.commons.math3.p000ml.neuralnet.twod.util;

import java.lang.reflect.Array;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;
import org.apache.commons.math3.p000ml.neuralnet.MapUtils;
import org.apache.commons.math3.p000ml.neuralnet.Network;
import org.apache.commons.math3.p000ml.neuralnet.Neuron;
import org.apache.commons.math3.p000ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.p000ml.neuralnet.twod.util.LocationFinder;
import org.apache.commons.math3.util.Pair;

/* renamed from: org.apache.commons.math3.ml.neuralnet.twod.util.TopographicErrorHistogram */
public class TopographicErrorHistogram implements MapDataVisualization {
    private final DistanceMeasure distance;
    private final boolean relativeCount;

    public TopographicErrorHistogram(boolean relativeCount2, DistanceMeasure distance2) {
        this.relativeCount = relativeCount2;
        this.distance = distance2;
    }

    @Override // org.apache.commons.math3.p000ml.neuralnet.twod.util.MapDataVisualization
    public double[][] computeImage(NeuronSquareMesh2D map, Iterable<double[]> data) {
        int nR = map.getNumberOfRows();
        int nC = map.getNumberOfColumns();
        Network net = map.getNetwork();
        LocationFinder finder = new LocationFinder(map);
        int[][] hit = (int[][]) Array.newInstance(Integer.TYPE, nR, nC);
        double[][] error = (double[][]) Array.newInstance(Double.TYPE, nR, nC);
        for (double[] sample : data) {
            Pair<Neuron, Neuron> p = MapUtils.findBestAndSecondBest(sample, map, this.distance);
            Neuron best = p.getFirst();
            LocationFinder.Location loc = finder.getLocation(best);
            int row = loc.getRow();
            int col = loc.getColumn();
            int[] iArr = hit[row];
            iArr[col] = iArr[col] + 1;
            if (!net.getNeighbours(best).contains(p.getSecond())) {
                double[] dArr = error[row];
                dArr[col] = dArr[col] + 1.0d;
            }
        }
        if (this.relativeCount) {
            for (int r = 0; r < nR; r++) {
                for (int c = 0; c < nC; c++) {
                    double[] dArr2 = error[r];
                    dArr2[c] = dArr2[c] / ((double) hit[r][c]);
                }
            }
        }
        return error;
    }
}
