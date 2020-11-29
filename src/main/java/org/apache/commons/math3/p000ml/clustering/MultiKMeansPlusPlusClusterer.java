package org.apache.commons.math3.p000ml.clustering;

import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.p000ml.clustering.Clusterable;
import org.apache.commons.math3.p000ml.clustering.evaluation.ClusterEvaluator;
import org.apache.commons.math3.p000ml.clustering.evaluation.SumOfClusterVariances;

/* renamed from: org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer */
public class MultiKMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T> {
    private final KMeansPlusPlusClusterer<T> clusterer;
    private final ClusterEvaluator<T> evaluator;
    private final int numTrials;

    public MultiKMeansPlusPlusClusterer(KMeansPlusPlusClusterer<T> clusterer2, int numTrials2) {
        this(clusterer2, numTrials2, new SumOfClusterVariances(clusterer2.getDistanceMeasure()));
    }

    public MultiKMeansPlusPlusClusterer(KMeansPlusPlusClusterer<T> clusterer2, int numTrials2, ClusterEvaluator<T> evaluator2) {
        super(clusterer2.getDistanceMeasure());
        this.clusterer = clusterer2;
        this.numTrials = numTrials2;
        this.evaluator = evaluator2;
    }

    public KMeansPlusPlusClusterer<T> getClusterer() {
        return this.clusterer;
    }

    public int getNumTrials() {
        return this.numTrials;
    }

    public ClusterEvaluator<T> getClusterEvaluator() {
        return this.evaluator;
    }

    @Override // org.apache.commons.math3.p000ml.clustering.Clusterer
    public List<CentroidCluster<T>> cluster(Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
        List<CentroidCluster<T>> best = null;
        double bestVarianceSum = Double.POSITIVE_INFINITY;
        for (int i = 0; i < this.numTrials; i++) {
            List<CentroidCluster<T>> clusters = this.clusterer.cluster(points);
            double varianceSum = this.evaluator.score(clusters);
            if (this.evaluator.isBetterScore(varianceSum, bestVarianceSum)) {
                best = clusters;
                bestVarianceSum = varianceSum;
            }
        }
        return best;
    }
}
