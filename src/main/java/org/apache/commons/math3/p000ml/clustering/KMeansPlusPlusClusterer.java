package org.apache.commons.math3.p000ml.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.p000ml.clustering.Clusterable;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;
import org.apache.commons.math3.p000ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.util.MathUtils;

/* renamed from: org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer */
public class KMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T> {
    private final EmptyClusterStrategy emptyStrategy;

    /* renamed from: k */
    private final int f243k;
    private final int maxIterations;
    private final RandomGenerator random;

    /* renamed from: org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer$EmptyClusterStrategy */
    public enum EmptyClusterStrategy {
        LARGEST_VARIANCE,
        LARGEST_POINTS_NUMBER,
        FARTHEST_POINT,
        ERROR
    }

    public KMeansPlusPlusClusterer(int k) {
        this(k, -1);
    }

    public KMeansPlusPlusClusterer(int k, int maxIterations2) {
        this(k, maxIterations2, new EuclideanDistance());
    }

    public KMeansPlusPlusClusterer(int k, int maxIterations2, DistanceMeasure measure) {
        this(k, maxIterations2, measure, new JDKRandomGenerator());
    }

    public KMeansPlusPlusClusterer(int k, int maxIterations2, DistanceMeasure measure, RandomGenerator random2) {
        this(k, maxIterations2, measure, random2, EmptyClusterStrategy.LARGEST_VARIANCE);
    }

    public KMeansPlusPlusClusterer(int k, int maxIterations2, DistanceMeasure measure, RandomGenerator random2, EmptyClusterStrategy emptyStrategy2) {
        super(measure);
        this.f243k = k;
        this.maxIterations = maxIterations2;
        this.random = random2;
        this.emptyStrategy = emptyStrategy2;
    }

    public int getK() {
        return this.f243k;
    }

    public int getMaxIterations() {
        return this.maxIterations;
    }

    public RandomGenerator getRandomGenerator() {
        return this.random;
    }

    public EmptyClusterStrategy getEmptyClusterStrategy() {
        return this.emptyStrategy;
    }

    @Override // org.apache.commons.math3.p000ml.clustering.Clusterer
    public List<CentroidCluster<T>> cluster(Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
        Clusterable newCenter;
        MathUtils.checkNotNull(points);
        if (points.size() < this.f243k) {
            throw new NumberIsTooSmallException(Integer.valueOf(points.size()), Integer.valueOf(this.f243k), false);
        }
        List<CentroidCluster<T>> clusters = chooseInitialCenters(points);
        int[] assignments = new int[points.size()];
        assignPointsToClusters(clusters, points, assignments);
        int max = this.maxIterations < 0 ? BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT : this.maxIterations;
        for (int count = 0; count < max; count++) {
            boolean emptyCluster = false;
            List<CentroidCluster<T>> newClusters = new ArrayList<>();
            for (CentroidCluster<T> cluster : clusters) {
                if (cluster.getPoints().isEmpty()) {
                    switch (this.emptyStrategy) {
                        case LARGEST_VARIANCE:
                            newCenter = getPointFromLargestVarianceCluster(clusters);
                            break;
                        case LARGEST_POINTS_NUMBER:
                            newCenter = getPointFromLargestNumberCluster(clusters);
                            break;
                        case FARTHEST_POINT:
                            newCenter = getFarthestPoint(clusters);
                            break;
                        default:
                            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
                    }
                    emptyCluster = true;
                } else {
                    newCenter = centroidOf(cluster.getPoints(), cluster.getCenter().getPoint().length);
                }
                newClusters.add(new CentroidCluster<>(newCenter));
            }
            clusters = newClusters;
            if (assignPointsToClusters(newClusters, points, assignments) == 0 && !emptyCluster) {
                return clusters;
            }
        }
        return clusters;
    }

    private int assignPointsToClusters(List<CentroidCluster<T>> clusters, Collection<T> points, int[] assignments) {
        int assignedDifferently = 0;
        int pointIndex = 0;
        for (T p : points) {
            int clusterIndex = getNearestCluster(clusters, p);
            if (clusterIndex != assignments[pointIndex]) {
                assignedDifferently++;
            }
            clusters.get(clusterIndex).addPoint(p);
            assignments[pointIndex] = clusterIndex;
            pointIndex++;
        }
        return assignedDifferently;
    }

    private List<CentroidCluster<T>> chooseInitialCenters(Collection<T> points) {
        List<T> pointList = Collections.unmodifiableList(new ArrayList(points));
        int numPoints = pointList.size();
        boolean[] taken = new boolean[numPoints];
        List<CentroidCluster<T>> resultSet = new ArrayList<>();
        int firstPointIndex = this.random.nextInt(numPoints);
        T firstPoint = pointList.get(firstPointIndex);
        resultSet.add(new CentroidCluster<>(firstPoint));
        taken[firstPointIndex] = true;
        double[] minDistSquared = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            if (i != firstPointIndex) {
                double d = distance(firstPoint, pointList.get(i));
                minDistSquared[i] = d * d;
            }
        }
        while (resultSet.size() < this.f243k) {
            double distSqSum = 0.0d;
            for (int i2 = 0; i2 < numPoints; i2++) {
                if (!taken[i2]) {
                    distSqSum += minDistSquared[i2];
                }
            }
            double r = this.random.nextDouble() * distSqSum;
            int nextPointIndex = -1;
            double sum = 0.0d;
            int i3 = 0;
            while (true) {
                if (i3 >= numPoints) {
                    break;
                }
                if (!taken[i3]) {
                    sum += minDistSquared[i3];
                    if (sum >= r) {
                        nextPointIndex = i3;
                        break;
                    }
                }
                i3++;
            }
            if (nextPointIndex == -1) {
                int i4 = numPoints - 1;
                while (true) {
                    if (i4 < 0) {
                        break;
                    } else if (!taken[i4]) {
                        nextPointIndex = i4;
                        break;
                    } else {
                        i4--;
                    }
                }
            }
            if (nextPointIndex < 0) {
                break;
            }
            T p = pointList.get(nextPointIndex);
            resultSet.add(new CentroidCluster<>(p));
            taken[nextPointIndex] = true;
            if (resultSet.size() < this.f243k) {
                for (int j = 0; j < numPoints; j++) {
                    if (!taken[j]) {
                        double d2 = distance(p, pointList.get(j));
                        double d22 = d2 * d2;
                        if (d22 < minDistSquared[j]) {
                            minDistSquared[j] = d22;
                        }
                    }
                }
            }
        }
        return resultSet;
    }

    private T getPointFromLargestVarianceCluster(Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
        double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster<T> selected = null;
        for (CentroidCluster<T> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {
                Clusterable center = cluster.getCenter();
                Variance stat = new Variance();
                for (T point : cluster.getPoints()) {
                    stat.increment(distance(point, center));
                }
                double variance = stat.getResult();
                if (variance > maxVariance) {
                    maxVariance = variance;
                    selected = cluster;
                }
            }
        }
        if (selected == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
        }
        List<T> selectedPoints = selected.getPoints();
        return selectedPoints.remove(this.random.nextInt(selectedPoints.size()));
    }

    private T getPointFromLargestNumberCluster(Collection<? extends Cluster<T>> clusters) throws ConvergenceException {
        int maxNumber = 0;
        Cluster<T> selected = null;
        for (Cluster<T> cluster : clusters) {
            int number = cluster.getPoints().size();
            if (number > maxNumber) {
                maxNumber = number;
                selected = cluster;
            }
        }
        if (selected == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
        }
        List<T> selectedPoints = selected.getPoints();
        return selectedPoints.remove(this.random.nextInt(selectedPoints.size()));
    }

    private T getFarthestPoint(Collection<CentroidCluster<T>> clusters) throws ConvergenceException {
        double maxDistance = Double.NEGATIVE_INFINITY;
        Cluster<T> selectedCluster = null;
        int selectedPoint = -1;
        for (CentroidCluster<T> cluster : clusters) {
            Clusterable center = cluster.getCenter();
            List<T> points = cluster.getPoints();
            for (int i = 0; i < points.size(); i++) {
                double distance = distance(points.get(i), center);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    selectedCluster = cluster;
                    selectedPoint = i;
                }
            }
        }
        if (selectedCluster != null) {
            return selectedCluster.getPoints().remove(selectedPoint);
        }
        throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS, new Object[0]);
    }

    private int getNearestCluster(Collection<CentroidCluster<T>> clusters, T point) {
        double minDistance = Double.MAX_VALUE;
        int clusterIndex = 0;
        int minCluster = 0;
        for (CentroidCluster<T> c : clusters) {
            double distance = distance(point, c.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                minCluster = clusterIndex;
            }
            clusterIndex++;
        }
        return minCluster;
    }

    private Clusterable centroidOf(Collection<T> points, int dimension) {
        double[] centroid = new double[dimension];
        for (T p : points) {
            double[] point = p.getPoint();
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] = centroid[i] + point[i];
            }
        }
        for (int i2 = 0; i2 < centroid.length; i2++) {
            centroid[i2] = centroid[i2] / ((double) points.size());
        }
        return new DoublePoint(centroid);
    }
}
