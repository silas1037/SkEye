package org.apache.commons.math3.p000ml.neuralnet.twod.util;

import org.apache.commons.math3.p000ml.neuralnet.twod.NeuronSquareMesh2D;

/* renamed from: org.apache.commons.math3.ml.neuralnet.twod.util.MapDataVisualization */
public interface MapDataVisualization {
    double[][] computeImage(NeuronSquareMesh2D neuronSquareMesh2D, Iterable<double[]> iterable);
}
