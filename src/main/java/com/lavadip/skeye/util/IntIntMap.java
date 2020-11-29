package com.lavadip.skeye.util;

import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;

public final class IntIntMap {
    private static final String EOL = System.getProperty("line.separator", "\n");
    private Entry[] m_buckets;
    private final float m_loadFactor;
    private int m_size;
    private int m_sizeThreshold;

    public IntIntMap() {
        this(11, 0.75f);
    }

    public IntIntMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public IntIntMap(int initialCapacity, float loadFactor) {
        float f;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("negative input: initialCapacity [" + initialCapacity + "]");
        } else if (((double) loadFactor) <= 0.0d || ((double) loadFactor) >= 1.000001d) {
            throw new IllegalArgumentException("loadFactor not in (0.0, 1.0] range: " + loadFactor);
        } else {
            initialCapacity = initialCapacity == 0 ? 1 : initialCapacity;
            if (((double) loadFactor) > 1.0d) {
                f = 1.0f;
            } else {
                f = loadFactor;
            }
            this.m_loadFactor = f;
            this.m_sizeThreshold = (int) (((float) initialCapacity) * loadFactor);
            this.m_buckets = new Entry[initialCapacity];
        }
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        debugDump(s);
        return s.toString();
    }

    public int size() {
        return this.m_size;
    }

    public boolean contains(int key) {
        Entry[] buckets = this.m_buckets;
        for (Entry entry = buckets[(Integer.MAX_VALUE & key) % buckets.length]; entry != null; entry = entry.m_next) {
            if (key == entry.m_key) {
                return true;
            }
        }
        return false;
    }

    public boolean get(int key, int[] out) {
        return get(key, out, 0);
    }

    public boolean get(int key, int[] out, int index) {
        Entry[] buckets = this.m_buckets;
        for (Entry entry = buckets[(Integer.MAX_VALUE & key) % buckets.length]; entry != null; entry = entry.m_next) {
            if (key == entry.m_key) {
                out[index] = entry.m_value;
                return true;
            }
        }
        return false;
    }

    public int get(int key) {
        Entry[] buckets = this.m_buckets;
        for (Entry entry = buckets[(Integer.MAX_VALUE & key) % buckets.length]; entry != null; entry = entry.m_next) {
            if (key == entry.m_key) {
                return entry.m_value;
            }
        }
        return -1;
    }

    public int[] keys() {
        int[] result = new int[this.m_size];
        int scan = 0;
        Entry[] entryArr = this.m_buckets;
        int length = entryArr.length;
        int i = 0;
        while (i < length) {
            Entry entry = entryArr[i];
            int scan2 = scan;
            while (entry != null) {
                result[scan2] = entry.m_key;
                entry = entry.m_next;
                scan2++;
            }
            i++;
            scan = scan2;
        }
        return result;
    }

    public void put(int key, int value) {
        Entry currentKeyEntry = null;
        Entry entry = this.m_buckets[(key & BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT) % this.m_buckets.length];
        while (true) {
            if (entry == null) {
                break;
            } else if (key == entry.m_key) {
                currentKeyEntry = entry;
                break;
            } else {
                entry = entry.m_next;
            }
        }
        if (currentKeyEntry != null) {
            currentKeyEntry.m_value = value;
            return;
        }
        if (this.m_size >= this.m_sizeThreshold) {
            rehash();
        }
        Entry[] buckets = this.m_buckets;
        int bucketIndex = (key & BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT) % buckets.length;
        buckets[bucketIndex] = new Entry(key, value, buckets[bucketIndex]);
        this.m_size++;
    }

    public void remove(int key) {
        int bucketIndex = (Integer.MAX_VALUE & key) % this.m_buckets.length;
        Entry[] buckets = this.m_buckets;
        Entry entry = buckets[bucketIndex];
        Entry prev = entry;
        while (entry != null) {
            Entry next = entry.m_next;
            if (key == entry.m_key) {
                if (prev == entry) {
                    buckets[bucketIndex] = next;
                } else {
                    prev.m_next = next;
                }
                this.m_size--;
                return;
            }
            prev = entry;
            entry = next;
        }
    }

    /* access modifiers changed from: package-private */
    public void debugDump(StringBuffer out) {
        if (out != null) {
            out.append(super.toString());
            out.append(EOL);
            out.append("size = " + this.m_size + ", bucket table size = " + this.m_buckets.length + ", load factor = " + this.m_loadFactor + EOL);
            out.append("size threshold = " + this.m_sizeThreshold + EOL);
        }
    }

    /* access modifiers changed from: private */
    public static final class Entry {
        int m_key;
        Entry m_next;
        int m_value;

        Entry(int key, int value, Entry next) {
            this.m_key = key;
            this.m_value = value;
            this.m_next = next;
        }
    }

    private void rehash() {
        Entry[] buckets = this.m_buckets;
        int newBucketCount = (this.m_buckets.length << 1) + 1;
        Entry[] newBuckets = new Entry[newBucketCount];
        for (Entry entry : buckets) {
            while (entry != null) {
                Entry next = entry.m_next;
                int newBucketIndex = (entry.m_key & BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT) % newBucketCount;
                entry.m_next = newBuckets[newBucketIndex];
                newBuckets[newBucketIndex] = entry;
                entry = next;
            }
        }
        this.m_sizeThreshold = (int) (((float) newBucketCount) * this.m_loadFactor);
        this.m_buckets = newBuckets;
    }
}
