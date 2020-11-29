package org.apache.commons.math3.p000ml.neuralnet.sofm;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.p000ml.distance.DistanceMeasure;
import org.apache.commons.math3.p000ml.neuralnet.MapUtils;
import org.apache.commons.math3.p000ml.neuralnet.Network;
import org.apache.commons.math3.p000ml.neuralnet.Neuron;
import org.apache.commons.math3.p000ml.neuralnet.UpdateAction;

/* renamed from: org.apache.commons.math3.ml.neuralnet.sofm.KohonenUpdateAction */
public class KohonenUpdateAction implements UpdateAction {
    private final DistanceMeasure distance;
    private final LearningFactorFunction learningFactor;
    private final NeighbourhoodSizeFunction neighbourhoodSize;
    private final AtomicLong numberOfCalls = new AtomicLong(0);

    public KohonenUpdateAction(DistanceMeasure distance2, LearningFactorFunction learningFactor2, NeighbourhoodSizeFunction neighbourhoodSize2) {
        this.distance = distance2;
        this.learningFactor = learningFactor2;
        this.neighbourhoodSize = neighbourhoodSize2;
    }

    @Override // org.apache.commons.math3.p000ml.neuralnet.UpdateAction
    public void update(Network net, double[] features) {
        long numCalls = this.numberOfCalls.incrementAndGet() - 1;
        double currentLearning = this.learningFactor.value(numCalls);
        Neuron best = findAndUpdateBestNeuron(net, features, currentLearning);
        int currentNeighbourhood = this.neighbourhoodSize.value(numCalls);
        Gaussian neighbourhoodDecay = new Gaussian(currentLearning, 0.0d, (double) currentNeighbourhood);
        if (currentNeighbourhood > 0) {
            Collection<Neuron> neighbours = new HashSet<>();
            neighbours.add(best);
            HashSet<Neuron> exclude = new HashSet<>();
            exclude.add(best);
            int radius = 1;
            do {
                neighbours = net.getNeighbours(neighbours, exclude);
                for (Neuron n : neighbours) {
                    updateNeighbouringNeuron(n, features, neighbourhoodDecay.value((double) radius));
                }
                exclude.addAll(neighbours);
                radius++;
            } while (radius <= currentNeighbourhood);
        }
    }

    public long getNumberOfCalls() {
        return this.numberOfCalls.get();
    }

    private boolean attemptNeuronUpdate(Neuron n, double[] features, double learningRate) {
        double[] expect = n.getFeatures();
        return n.compareAndSetFeatures(expect, computeFeatures(expect, features, learningRate));
    }

    private void updateNeighbouringNeuron(Neuron n, double[] features, double learningRate) {
        do {
        } while (!attemptNeuronUpdate(n, features, learningRate));
    }

    private Neuron findAndUpdateBestNeuron(Network net, double[] features, double learningRate) {
        Neuron best;
        do {
            best = MapUtils.findBest(features, net, this.distance);
        } while (!attemptNeuronUpdate(best, features, learningRate));
        return best;
    }

    private double[] computeFeatures(double[] current, double[] sample, double learningRate) {
        ArrayRealVector c = new ArrayRealVector(current, false);
        return new ArrayRealVector(sample, false).subtract((RealVector) c).mapMultiplyToSelf(learningRate).add(c).toArray();
    }
}
