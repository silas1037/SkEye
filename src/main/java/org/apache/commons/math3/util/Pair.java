package org.apache.commons.math3.util;

public class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public Pair(Pair<? extends K, ? extends V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public K getFirst() {
        return this.key;
    }

    public V getSecond() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> oP = (Pair) o;
        if (this.key != null ? this.key.equals(oP.key) : oP.key == null) {
            if (this.value == null) {
                if (oP.value == null) {
                    return true;
                }
            } else if (this.value.equals(oP.value)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = this.key == null ? 0 : this.key.hashCode();
        int h = this.value == null ? 0 : this.value.hashCode();
        return ((result * 37) + h) ^ (h >>> 16);
    }

    public String toString() {
        return "[" + ((Object) getKey()) + ", " + ((Object) getValue()) + "]";
    }

    public static <K, V> Pair<K, V> create(K k, V v) {
        return new Pair<>(k, v);
    }
}
