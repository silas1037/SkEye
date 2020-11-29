package org.apache.commons.math3.p000ml.neuralnet.twod.util;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;
import org.apache.commons.math3.p000ml.neuralnet.MapUtils;
import org.apache.commons.math3.p000ml.neuralnet.Neuron;
import org.apache.commons.math3.p000ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.p000ml.neuralnet.twod.util.LocationFinder;

/* renamed from: org.apache.commons.math3.ml.neuralnet.twod.util.SmoothedDataHistogram */
public class SmoothedDataHistogram implements MapDataVisualization {
    private final DistanceMeasure distance;
    private final double membershipNormalization;
    private final int smoothingBins;

    public SmoothedDataHistogram(int smoothingBins2, DistanceMeasure distance2) {
        this.smoothingBins = smoothingBins2;
        this.distance = distance2;
        double sum = 0.0d;
        for (int i = 0; i < smoothingBins2; i++) {
            sum += (double) (smoothingBins2 - i);
        }
        this.membershipNormalization = 1.0d / sum;
    }

    @Override // org.apache.commons.math3.p000ml.neuralnet.twod.util.MapDataVisualization
    public double[][] computeImage(NeuronSquareMesh2D map, Iterable<double[]> data) {
        int nR = map.getNumberOfRows();
        int nC = map.getNumberOfColumns();
        int mapSize = nR * nC;
        if (mapSize < this.smoothingBins) {
            throw new NumberIsTooSmallException(Integer.valueOf(mapSize), Integer.valueOf(this.smoothingBins), true);
        }
        LocationFinder finder = new LocationFinder(map);
        double[][] histo = (double[][]) Array.newInstance(Double.TYPE, nR, nC);
        for (double[] sample : data) {
            Neuron[] sorted = MapUtils.sort(sample, map.getNetwork(), this.distance);
            for (int i = 0; i < this.smoothingBins; i++) {
                LocationFinder.Location loc = finder.getLocation(sorted[i]);
                int row = loc.getRow();
                int col = loc.getColumn();
                double[] dArr = histo[row];
                dArr[col] = dArr[col] + (((double) (this.smoothingBins - i)) * this.membershipNormalization);
            }
        }
        return histo;
    }
}
