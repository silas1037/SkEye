package org.apache.commons.math3.p000ml.distance;

import java.io.Serializable;
import org.apache.commons.math3.exception.DimensionMismatchException;

/* renamed from: org.apache.commons.math3.ml.distance.DistanceMeasure */
public interface DistanceMeasure extends Serializable {
    double compute(double[] dArr, double[] dArr2) throws DimensionMismatchException;
}
