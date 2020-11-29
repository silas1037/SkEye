package org.apache.commons.math3.fitting;

import java.io.Serializable;

public class WeightedObservedPoint implements Serializable {
    private static final long serialVersionUID = 5306874947404636157L;
    private final double weight;

    /* renamed from: x */
    private final double f177x;

    /* renamed from: y */
    private final double f178y;

    public WeightedObservedPoint(double weight2, double x, double y) {
        this.weight = weight2;
        this.f177x = x;
        this.f178y = y;
    }

    public double getWeight() {
        return this.weight;
    }

    public double getX() {
        return this.f177x;
    }

    public double getY() {
        return this.f178y;
    }
}
