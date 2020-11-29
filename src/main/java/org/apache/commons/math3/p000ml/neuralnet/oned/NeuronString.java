package org.apache.commons.math3.p000ml.neuralnet.oned;

import java.io.ObjectInputStream;
import java.io.Serializable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.p000ml.neuralnet.FeatureInitializer;
import org.apache.commons.math3.p000ml.neuralnet.Network;

/* renamed from: org.apache.commons.math3.ml.neuralnet.oned.NeuronString */
public class NeuronString implements Serializable {
    private static final long serialVersionUID = 1;
    private final long[] identifiers;
    private final Network network;
    private final int size;
    private final boolean wrap;

    NeuronString(boolean wrap2, double[][] featuresList) {
        this.size = featuresList.length;
        if (this.size < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(this.size), 2, true);
        }
        this.wrap = wrap2;
        this.network = new Network(0, featuresList[0].length);
        this.identifiers = new long[this.size];
        for (int i = 0; i < this.size; i++) {
            this.identifiers[i] = this.network.createNeuron(featuresList[i]);
        }
        createLinks();
    }

    public NeuronString(int num, boolean wrap2, FeatureInitializer[] featureInit) {
        if (num < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(num), 2, true);
        }
        this.size = num;
        this.wrap = wrap2;
        this.identifiers = new long[num];
        int fLen = featureInit.length;
        this.network = new Network(0, fLen);
        for (int i = 0; i < num; i++) {
            double[] features = new double[fLen];
            for (int fIndex = 0; fIndex < fLen; fIndex++) {
                features[fIndex] = featureInit[fIndex].value();
            }
            this.identifiers[i] = this.network.createNeuron(features);
        }
        createLinks();
    }

    public Network getNetwork() {
        return this.network;
    }

    public int getSize() {
        return this.size;
    }

    public double[] getFeatures(int i) {
        if (i >= 0 && i < this.size) {
            return this.network.getNeuron(this.identifiers[i]).getFeatures();
        }
        throw new OutOfRangeException(Integer.valueOf(i), 0, Integer.valueOf(this.size - 1));
    }

    private void createLinks() {
        for (int i = 0; i < this.size - 1; i++) {
            this.network.addLink(this.network.getNeuron((long) i), this.network.getNeuron((long) (i + 1)));
        }
        for (int i2 = this.size - 1; i2 > 0; i2--) {
            this.network.addLink(this.network.getNeuron((long) i2), this.network.getNeuron((long) (i2 - 1)));
        }
        if (this.wrap) {
            this.network.addLink(this.network.getNeuron(0), this.network.getNeuron((long) (this.size - 1)));
            this.network.addLink(this.network.getNeuron((long) (this.size - 1)), this.network.getNeuron(0));
        }
    }

    private void readObject(ObjectInputStream in) {
        throw new IllegalStateException();
    }

    private Object writeReplace() {
        double[][] featuresList = new double[this.size][];
        for (int i = 0; i < this.size; i++) {
            featuresList[i] = getFeatures(i);
        }
        return new SerializationProxy(this.wrap, featuresList);
    }

    /* renamed from: org.apache.commons.math3.ml.neuralnet.oned.NeuronString$SerializationProxy */
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 20130226;
        private final double[][] featuresList;
        private final boolean wrap;

        SerializationProxy(boolean wrap2, double[][] featuresList2) {
            this.wrap = wrap2;
            this.featuresList = featuresList2;
        }

        private Object readResolve() {
            return new NeuronString(this.wrap, this.featuresList);
        }
    }
}
