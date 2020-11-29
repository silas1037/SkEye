package org.apache.commons.math3.p000ml.neuralnet.twod.util;

import java.lang.reflect.Array;
import java.util.Collection;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;
import org.apache.commons.math3.p000ml.neuralnet.Network;
import org.apache.commons.math3.p000ml.neuralnet.Neuron;
import org.apache.commons.math3.p000ml.neuralnet.twod.NeuronSquareMesh2D;

/* renamed from: org.apache.commons.math3.ml.neuralnet.twod.util.UnifiedDistanceMatrix */
public class UnifiedDistanceMatrix implements MapVisualization {
    private final DistanceMeasure distance;
    private final boolean individualDistances;

    public UnifiedDistanceMatrix(boolean individualDistances2, DistanceMeasure distance2) {
        this.individualDistances = individualDistances2;
        this.distance = distance2;
    }

    @Override // org.apache.commons.math3.p000ml.neuralnet.twod.util.MapVisualization
    public double[][] computeImage(NeuronSquareMesh2D map) {
        if (this.individualDistances) {
            return individualDistances(map);
        }
        return averageDistances(map);
    }

    private double[][] individualDistances(NeuronSquareMesh2D map) {
        double right2Bottom;
        int numRows = map.getNumberOfRows();
        int numCols = map.getNumberOfColumns();
        double[][] uMatrix = (double[][]) Array.newInstance(Double.TYPE, (numRows * 2) + 1, (numCols * 2) + 1);
        for (int i = 0; i < numRows; i++) {
            int iR = (i * 2) + 1;
            for (int j = 0; j < numCols; j++) {
                int jR = (j * 2) + 1;
                double[] current = map.getNeuron(i, j).getFeatures();
                Neuron neighbour = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.CENTER);
                if (neighbour != null) {
                    uMatrix[iR][jR + 1] = this.distance.compute(current, neighbour.getFeatures());
                }
                Neuron neighbour2 = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.CENTER, NeuronSquareMesh2D.VerticalDirection.DOWN);
                if (neighbour2 != null) {
                    uMatrix[iR + 1][jR] = this.distance.compute(current, neighbour2.getFeatures());
                }
            }
        }
        for (int i2 = 0; i2 < numRows; i2++) {
            int iR2 = (i2 * 2) + 1;
            for (int j2 = 0; j2 < numCols; j2++) {
                int jR2 = (j2 * 2) + 1;
                Neuron current2 = map.getNeuron(i2, j2);
                Neuron right = map.getNeuron(i2, j2, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.CENTER);
                Neuron bottom = map.getNeuron(i2, j2, NeuronSquareMesh2D.HorizontalDirection.CENTER, NeuronSquareMesh2D.VerticalDirection.DOWN);
                Neuron bottomRight = map.getNeuron(i2, j2, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.DOWN);
                double current2BottomRight = bottomRight == null ? 0.0d : this.distance.compute(current2.getFeatures(), bottomRight.getFeatures());
                if (right == null || bottom == null) {
                    right2Bottom = 0.0d;
                } else {
                    right2Bottom = this.distance.compute(right.getFeatures(), bottom.getFeatures());
                }
                uMatrix[iR2 + 1][jR2 + 1] = 0.5d * (current2BottomRight + right2Bottom);
            }
        }
        int lastRow = uMatrix.length - 1;
        uMatrix[0] = uMatrix[lastRow];
        int lastCol = uMatrix[0].length - 1;
        for (int r = 0; r < lastRow; r++) {
            uMatrix[r][0] = uMatrix[r][lastCol];
        }
        return uMatrix;
    }

    private double[][] averageDistances(NeuronSquareMesh2D map) {
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
                    d += this.distance.compute(features, n.getFeatures());
                }
                uMatrix[i][j] = d / ((double) count);
            }
        }
        return uMatrix;
    }
}
