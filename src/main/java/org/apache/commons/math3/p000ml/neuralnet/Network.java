package org.apache.commons.math3.p000ml.neuralnet;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalStateException;

/* renamed from: org.apache.commons.math3.ml.neuralnet.Network */
public class Network implements Iterable<Neuron>, Serializable {
    private static final long serialVersionUID = 20130207;
    private final int featureSize;
    private final ConcurrentHashMap<Long, Set<Long>> linkMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Neuron> neuronMap = new ConcurrentHashMap<>();
    private final AtomicLong nextId;

    /* renamed from: org.apache.commons.math3.ml.neuralnet.Network$NeuronIdentifierComparator */
    public static class NeuronIdentifierComparator implements Comparator<Neuron>, Serializable {
        private static final long serialVersionUID = 20130207;

        public int compare(Neuron a, Neuron b) {
            long aId = a.getIdentifier();
            long bId = b.getIdentifier();
            if (aId < bId) {
                return -1;
            }
            return aId > bId ? 1 : 0;
        }
    }

    Network(long nextId2, int featureSize2, Neuron[] neuronList, long[][] neighbourIdList) {
        int numNeurons = neuronList.length;
        if (numNeurons != neighbourIdList.length) {
            throw new MathIllegalStateException();
        }
        for (Neuron n : neuronList) {
            long id = n.getIdentifier();
            if (id >= nextId2) {
                throw new MathIllegalStateException();
            }
            this.neuronMap.put(Long.valueOf(id), n);
            this.linkMap.put(Long.valueOf(id), new HashSet());
        }
        for (int i = 0; i < numNeurons; i++) {
            Set<Long> aLinks = this.linkMap.get(Long.valueOf(neuronList[i].getIdentifier()));
            for (long j : neighbourIdList[i]) {
                Long bId = Long.valueOf(j);
                if (this.neuronMap.get(bId) == null) {
                    throw new MathIllegalStateException();
                }
                addLinkToLinkSet(aLinks, bId.longValue());
            }
        }
        this.nextId = new AtomicLong(nextId2);
        this.featureSize = featureSize2;
    }

    public Network(long initialIdentifier, int featureSize2) {
        this.nextId = new AtomicLong(initialIdentifier);
        this.featureSize = featureSize2;
    }

    public synchronized Network copy() {
        Network copy;
        copy = new Network(this.nextId.get(), this.featureSize);
        for (Map.Entry<Long, Neuron> e : this.neuronMap.entrySet()) {
            copy.neuronMap.put(e.getKey(), e.getValue().copy());
        }
        for (Map.Entry<Long, Set<Long>> e2 : this.linkMap.entrySet()) {
            copy.linkMap.put(e2.getKey(), new HashSet(e2.getValue()));
        }
        return copy;
    }

    @Override // java.lang.Iterable
    public Iterator<Neuron> iterator() {
        return this.neuronMap.values().iterator();
    }

    public Collection<Neuron> getNeurons(Comparator<Neuron> comparator) {
        List<Neuron> neurons = new ArrayList<>();
        neurons.addAll(this.neuronMap.values());
        Collections.sort(neurons, comparator);
        return neurons;
    }

    public long createNeuron(double[] features) {
        if (features.length != this.featureSize) {
            throw new DimensionMismatchException(features.length, this.featureSize);
        }
        long id = createNextId().longValue();
        this.neuronMap.put(Long.valueOf(id), new Neuron(id, features));
        this.linkMap.put(Long.valueOf(id), new HashSet());
        return id;
    }

    public void deleteNeuron(Neuron neuron) {
        for (Neuron n : getNeighbours(neuron)) {
            deleteLink(n, neuron);
        }
        this.neuronMap.remove(Long.valueOf(neuron.getIdentifier()));
    }

    public int getFeaturesSize() {
        return this.featureSize;
    }

    public void addLink(Neuron a, Neuron b) {
        long aId = a.getIdentifier();
        long bId = b.getIdentifier();
        if (a != getNeuron(aId)) {
            throw new NoSuchElementException(Long.toString(aId));
        } else if (b != getNeuron(bId)) {
            throw new NoSuchElementException(Long.toString(bId));
        } else {
            addLinkToLinkSet(this.linkMap.get(Long.valueOf(aId)), bId);
        }
    }

    private void addLinkToLinkSet(Set<Long> linkSet, long id) {
        linkSet.add(Long.valueOf(id));
    }

    public void deleteLink(Neuron a, Neuron b) {
        long aId = a.getIdentifier();
        long bId = b.getIdentifier();
        if (a != getNeuron(aId)) {
            throw new NoSuchElementException(Long.toString(aId));
        } else if (b != getNeuron(bId)) {
            throw new NoSuchElementException(Long.toString(bId));
        } else {
            deleteLinkFromLinkSet(this.linkMap.get(Long.valueOf(aId)), bId);
        }
    }

    private void deleteLinkFromLinkSet(Set<Long> linkSet, long id) {
        linkSet.remove(Long.valueOf(id));
    }

    public Neuron getNeuron(long id) {
        Neuron n = this.neuronMap.get(Long.valueOf(id));
        if (n != null) {
            return n;
        }
        throw new NoSuchElementException(Long.toString(id));
    }

    public Collection<Neuron> getNeighbours(Iterable<Neuron> neurons) {
        return getNeighbours(neurons, (Iterable<Neuron>) null);
    }

    public Collection<Neuron> getNeighbours(Iterable<Neuron> neurons, Iterable<Neuron> exclude) {
        Set<Long> idList = new HashSet<>();
        for (Neuron n : neurons) {
            idList.addAll(this.linkMap.get(Long.valueOf(n.getIdentifier())));
        }
        if (exclude != null) {
            for (Neuron n2 : exclude) {
                idList.remove(Long.valueOf(n2.getIdentifier()));
            }
        }
        List<Neuron> neuronList = new ArrayList<>();
        for (Long id : idList) {
            neuronList.add(getNeuron(id.longValue()));
        }
        return neuronList;
    }

    public Collection<Neuron> getNeighbours(Neuron neuron) {
        return getNeighbours(neuron, (Iterable<Neuron>) null);
    }

    public Collection<Neuron> getNeighbours(Neuron neuron, Iterable<Neuron> exclude) {
        Set<Long> idList = this.linkMap.get(Long.valueOf(neuron.getIdentifier()));
        if (exclude != null) {
            for (Neuron n : exclude) {
                idList.remove(Long.valueOf(n.getIdentifier()));
            }
        }
        List<Neuron> neuronList = new ArrayList<>();
        for (Long id : idList) {
            neuronList.add(getNeuron(id.longValue()));
        }
        return neuronList;
    }

    private Long createNextId() {
        return Long.valueOf(this.nextId.getAndIncrement());
    }

    private void readObject(ObjectInputStream in) {
        throw new IllegalStateException();
    }

    private Object writeReplace() {
        Neuron[] neuronList = (Neuron[]) this.neuronMap.values().toArray(new Neuron[0]);
        long[][] neighbourIdList = new long[neuronList.length][];
        for (int i = 0; i < neuronList.length; i++) {
            Collection<Neuron> neighbours = getNeighbours(neuronList[i]);
            long[] neighboursId = new long[neighbours.size()];
            int count = 0;
            for (Neuron n : neighbours) {
                neighboursId[count] = n.getIdentifier();
                count++;
            }
            neighbourIdList[i] = neighboursId;
        }
        return new SerializationProxy(this.nextId.get(), this.featureSize, neuronList, neighbourIdList);
    }

    /* renamed from: org.apache.commons.math3.ml.neuralnet.Network$SerializationProxy */
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 20130207;
        private final int featureSize;
        private final long[][] neighbourIdList;
        private final Neuron[] neuronList;
        private final long nextId;

        SerializationProxy(long nextId2, int featureSize2, Neuron[] neuronList2, long[][] neighbourIdList2) {
            this.nextId = nextId2;
            this.featureSize = featureSize2;
            this.neuronList = neuronList2;
            this.neighbourIdList = neighbourIdList2;
        }

        private Object readResolve() {
            return new Network(this.nextId, this.featureSize, this.neuronList, this.neighbourIdList);
        }
    }
}
