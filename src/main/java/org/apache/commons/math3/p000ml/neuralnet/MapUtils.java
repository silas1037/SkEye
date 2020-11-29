package org.apache.commons.math3.p000ml.neuralnet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;
import org.apache.commons.math3.p000ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.util.Pair;

/* renamed from: org.apache.commons.math3.ml.neuralnet.MapUtils */
public class MapUtils {
    private MapUtils() {
    }

    public static Neuron findBest(double[] features, Iterable<Neuron> neurons, DistanceMeasure distance) {
        Neuron best = null;
        double min = Double.POSITIVE_INFINITY;
        for (Neuron n : neurons) {
            double d = distance.compute(n.getFeatures(), features);
            if (d < min) {
                min = d;
                best = n;
            }
        }
        return best;
    }

    public static Pair<Neuron, Neuron> findBestAndSecondBest(double[] features, Iterable<Neuron> neurons, DistanceMeasure distance) {
        Neuron[] best = {null, null};
        double[] min = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        for (Neuron n : neurons) {
            double d = distance.compute(n.getFeatures(), features);
            if (d < min[0]) {
                min[1] = min[0];
                best[1] = best[0];
                min[0] = d;
                best[0] = n;
            } else if (d < min[1]) {
                min[1] = d;
                best[1] = n;
            }
        }
        return new Pair<>(best[0], best[1]);
    }

    public static Neuron[] sort(double[] features, Iterable<Neuron> neurons, DistanceMeasure distance) {
        List<PairNeuronDouble> list = new ArrayList<>();
        for (Neuron n : neurons) {
            list.add(new PairNeuronDouble(n, distance.compute(n.getFeatures(), features)));
        }
        Collections.sort(list, PairNeuronDouble.COMPARATOR);
        int len = list.size();
        Neuron[] sorted = new Neuron[len];
        for (int i = 0; i < len; i++) {
            sorted[i] = list.get(i).getNeuron();
        }
        return sorted;
    }

    public static double[][] computeU(NeuronSquareMesh2D map, DistanceMeasure distance) {
        int numRows = map.getNumberOfRows();
        int numCols = map.getNumberOfColumns();
        double[][] uMatrix = (double[][]) Array.newInstance(Double.TYPE, numRows, numCols);
        Network net = map.getNetwork();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Neuron neuron = map.getNeuron(i, j);
                Collection<Neuron> neighbours = net.getNeighbours(neuron);
                double[] features = neuron.getFeatures();
                double d = 0.0d;
                int count = 0;
                for (Neuron n : neighbours) {
                    count++;
                    d += distance.compute(features, n.getFeatures());
                }
                uMatrix[i][j] = d / ((double) count);
            }
        }
        return uMatrix;
    }

    public static int[][] computeHitHistogram(Iterable<double[]> data, NeuronSquareMesh2D map, DistanceMeasure distance) {
        HashMap<Neuron, Integer> hit = new HashMap<>();
        Network net = map.getNetwork();
        for (double[] f : data) {
            Neuron best = findBest(f, net, distance);
            Integer count = hit.get(best);
            if (count == null) {
                hit.put(best, 1);
            } else {
                hit.put(best, Integer.valueOf(count.intValue() + 1));
            }
        }
        int numRows = map.getNumberOfRows();
        int numCols = map.getNumberOfColumns();
        int[][] histo = (int[][]) Array.newInstance(Integer.TYPE, numRows, numCols);
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Integer count2 = hit.get(map.getNeuron(i, j));
                if (count2 == null) {
                    histo[i][j] = 0;
                } else {
                    histo[i][j] = count2.intValue();
                }
            }
        }
        return histo;
    }

    public static double computeQuantizationError(Iterable<double[]> data, Iterable<Neuron> neurons, DistanceMeasure distance) {
        double d = 0.0d;
        int count = 0;
        for (double[] f : data) {
            count++;
            d += distance.compute(f, findBest(f, neurons, distance).getFeatures());
        }
        if (count != 0) {
            return d / ((double) count);
        }
        throw new NoDataException();
    }

    public static double computeTopographicError(Iterable<double[]> data, Network net, DistanceMeasure distance) {
        int notAdjacentCount = 0;
        int count = 0;
        for (double[] f : data) {
            count++;
            Pair<Neuron, Neuron> p = findBestAndSecondBest(f, net, distance);
            if (!net.getNeighbours(p.getFirst()).contains(p.getSecond())) {
                notAdjacentCount++;
            }
        }
        if (count != 0) {
            return ((double) notAdjacentCount) / ((double) count);
        }
        throw new NoDataException();
    }

    /* access modifiers changed from: private */
    /* renamed from: org.apache.commons.math3.ml.neuralnet.MapUtils$PairNeuronDouble */
    public static class PairNeuronDouble {
        static final Comparator<PairNeuronDouble> COMPARATOR = new Comparator<PairNeuronDouble>() {
            /* class org.apache.commons.math3.p000ml.neuralnet.MapUtils.PairNeuronDouble.C02831 */

            public int compare(PairNeuronDouble o1, PairNeuronDouble o2) {
                return Double.compare(o1.value, o2.value);
            }
        };
        private final Neuron neuron;
        private final double value;

        PairNeuronDouble(Neuron neuron2, double value2) {
            this.neuron = neuron2;
            this.value = value2;
        }

        public Neuron getNeuron() {
            return this.neuron;
        }
    }
}
