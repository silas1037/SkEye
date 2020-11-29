package org.apache.commons.math3.stat;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;

public class Frequency implements Serializable {
    private static final long serialVersionUID = -3845586908418844111L;
    private final SortedMap<Comparable<?>, Long> freqTable;

    public Frequency() {
        this.freqTable = new TreeMap();
    }

    public Frequency(Comparator<?> comparator) {
        this.freqTable = new TreeMap(comparator);
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getPercentInstance();
        StringBuilder outBuffer = new StringBuilder();
        outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
        for (Comparable<?> value : this.freqTable.keySet()) {
            outBuffer.append(value);
            outBuffer.append('\t');
            outBuffer.append(getCount(value));
            outBuffer.append('\t');
            outBuffer.append(nf.format(getPct(value)));
            outBuffer.append('\t');
            outBuffer.append(nf.format(getCumPct(value)));
            outBuffer.append('\n');
        }
        return outBuffer.toString();
    }

    public void addValue(Comparable<?> v) throws MathIllegalArgumentException {
        incrementValue(v, 1);
    }

    public void addValue(int v) throws MathIllegalArgumentException {
        addValue(Long.valueOf((long) v));
    }

    public void addValue(long v) throws MathIllegalArgumentException {
        addValue(Long.valueOf(v));
    }

    public void addValue(char v) throws MathIllegalArgumentException {
        addValue(Character.valueOf(v));
    }

    public void incrementValue(Comparable<?> v, long increment) throws MathIllegalArgumentException {
        Comparable<?> obj = v;
        if (v instanceof Integer) {
            obj = Long.valueOf(((Integer) v).longValue());
        }
        try {
            Long count = this.freqTable.get(obj);
            if (count == null) {
                this.freqTable.put(obj, Long.valueOf(increment));
            } else {
                this.freqTable.put(obj, Long.valueOf(count.longValue() + increment));
            }
        } catch (ClassCastException e) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES, v.getClass().getName());
        }
    }

    public void incrementValue(int v, long increment) throws MathIllegalArgumentException {
        incrementValue(Long.valueOf((long) v), increment);
    }

    public void incrementValue(long v, long increment) throws MathIllegalArgumentException {
        incrementValue(Long.valueOf(v), increment);
    }

    public void incrementValue(char v, long increment) throws MathIllegalArgumentException {
        incrementValue(Character.valueOf(v), increment);
    }

    public void clear() {
        this.freqTable.clear();
    }

    public Iterator<Comparable<?>> valuesIterator() {
        return this.freqTable.keySet().iterator();
    }

    public Iterator<Map.Entry<Comparable<?>, Long>> entrySetIterator() {
        return this.freqTable.entrySet().iterator();
    }

    public long getSumFreq() {
        long result = 0;
        for (Long l : this.freqTable.values()) {
            result += l.longValue();
        }
        return result;
    }

    public long getCount(Comparable<?> v) {
        if (v instanceof Integer) {
            return getCount(((Integer) v).longValue());
        }
        try {
            Long count = this.freqTable.get(v);
            if (count != null) {
                return count.longValue();
            }
            return 0;
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public long getCount(int v) {
        return getCount(Long.valueOf((long) v));
    }

    public long getCount(long v) {
        return getCount(Long.valueOf(v));
    }

    public long getCount(char v) {
        return getCount(Character.valueOf(v));
    }

    public int getUniqueCount() {
        return this.freqTable.keySet().size();
    }

    public double getPct(Comparable<?> v) {
        long sumFreq = getSumFreq();
        if (sumFreq == 0) {
            return Double.NaN;
        }
        return ((double) getCount(v)) / ((double) sumFreq);
    }

    public double getPct(int v) {
        return getPct(Long.valueOf((long) v));
    }

    public double getPct(long v) {
        return getPct(Long.valueOf(v));
    }

    public double getPct(char v) {
        return getPct(Character.valueOf(v));
    }

    public long getCumFreq(Comparable<?> v) {
        if (getSumFreq() == 0) {
            return 0;
        }
        if (v instanceof Integer) {
            return getCumFreq(((Integer) v).longValue());
        }
        Comparator<? super Comparable<?>> comparator = this.freqTable.comparator();
        if (comparator == null) {
            comparator = new NaturalComparator<>();
        }
        long result = 0;
        try {
            Long value = this.freqTable.get(v);
            if (value != null) {
                result = value.longValue();
            }
            if (comparator.compare(v, this.freqTable.firstKey()) < 0) {
                return 0;
            }
            if (comparator.compare(v, this.freqTable.lastKey()) >= 0) {
                return getSumFreq();
            }
            Iterator<Comparable<?>> values = valuesIterator();
            while (values.hasNext()) {
                Comparable<?> nextValue = values.next();
                if (comparator.compare(v, nextValue) <= 0) {
                    return result;
                }
                result += getCount(nextValue);
            }
            return result;
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public long getCumFreq(int v) {
        return getCumFreq(Long.valueOf((long) v));
    }

    public long getCumFreq(long v) {
        return getCumFreq(Long.valueOf(v));
    }

    public long getCumFreq(char v) {
        return getCumFreq(Character.valueOf(v));
    }

    public double getCumPct(Comparable<?> v) {
        long sumFreq = getSumFreq();
        if (sumFreq == 0) {
            return Double.NaN;
        }
        return ((double) getCumFreq(v)) / ((double) sumFreq);
    }

    public double getCumPct(int v) {
        return getCumPct(Long.valueOf((long) v));
    }

    public double getCumPct(long v) {
        return getCumPct(Long.valueOf(v));
    }

    public double getCumPct(char v) {
        return getCumPct(Character.valueOf(v));
    }

    public List<Comparable<?>> getMode() {
        long mostPopular = 0;
        for (Long l : this.freqTable.values()) {
            long frequency = l.longValue();
            if (frequency > mostPopular) {
                mostPopular = frequency;
            }
        }
        List<Comparable<?>> modeList = new ArrayList<>();
        for (Map.Entry<Comparable<?>, Long> ent : this.freqTable.entrySet()) {
            if (ent.getValue().longValue() == mostPopular) {
                modeList.add(ent.getKey());
            }
        }
        return modeList;
    }

    public void merge(Frequency other) throws NullArgumentException {
        MathUtils.checkNotNull(other, LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
        Iterator<Map.Entry<Comparable<?>, Long>> iter = other.entrySetIterator();
        while (iter.hasNext()) {
            Map.Entry<Comparable<?>, Long> entry = iter.next();
            incrementValue(entry.getKey(), entry.getValue().longValue());
        }
    }

    public void merge(Collection<Frequency> others) throws NullArgumentException {
        MathUtils.checkNotNull(others, LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
        for (Frequency freq : others) {
            merge(freq);
        }
    }

    /* access modifiers changed from: private */
    public static class NaturalComparator<T extends Comparable<T>> implements Comparator<Comparable<T>>, Serializable {
        private static final long serialVersionUID = -3852193713161395148L;

        private NaturalComparator() {
        }

        @Override // java.util.Comparator
        public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
            return compare((Comparable) ((Comparable) obj), (Comparable) ((Comparable) obj2));
        }

        public int compare(Comparable<T> o1, Comparable<T> o2) {
            return o1.compareTo(o2);
        }
    }

    public int hashCode() {
        return (this.freqTable == null ? 0 : this.freqTable.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Frequency)) {
            return false;
        }
        Frequency other = (Frequency) obj;
        return this.freqTable == null ? other.freqTable == null : this.freqTable.equals(other.freqTable);
    }
}
