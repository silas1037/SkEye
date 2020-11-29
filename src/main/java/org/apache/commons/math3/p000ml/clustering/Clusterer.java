package org.apache.commons.math3.p000ml.clustering;

import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.p000ml.clustering.Clusterable;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;

/* renamed from: org.apache.commons.math3.ml.clustering.Clusterer */
public abstract class Clusterer<T extends Clusterable> {
    private DistanceMeasure measure;

    public abstract List<? extends Cluster<T>> cluster(Collection<T> collection) throws MathIllegalArgumentException, ConvergenceException;

    protected Clusterer(DistanceMeasure measure2) {
        this.measure = measure2;
    }

    public DistanceMeasure getDistanceMeasure() {
        return this.measure;
    }

    /* access modifiers changed from: protected */
    public double distance(Clusterable p1, Clusterable p2) {
        return this.measure.compute(p1.getPoint(), p2.getPoint());
    }
}
