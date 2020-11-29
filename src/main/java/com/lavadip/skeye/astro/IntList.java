package com.lavadip.skeye.astro;

import java.util.Arrays;

public final class IntList {
    public static final IntList ALWAYS_EMPTY_LIST = new IntList(0);
    protected int[] data;
    public transient int size = 0;

    public boolean equals(Object o) {
        if (!IntList.class.isInstance(o)) {
            return false;
        }
        IntList that = (IntList) o;
        if (this.size != that.size) {
            return false;
        }
        for (int i = 0; i < this.size; i++) {
            if (this.data[i] != that.data[i]) {
                return false;
            }
        }
        return true;
    }

    public IntList(int initialSize) {
        this.data = new int[initialSize];
    }

    public void clearList() {
        this.size = 0;
    }

    public int get(int index) {
        return this.data[index];
    }

    public void add(int x) {
        if (this.data.length == this.size) {
            resize(this.data.length * 2);
        }
        int[] iArr = this.data;
        int i = this.size;
        this.size = i + 1;
        iArr[i] = x;
    }

    public boolean addUnique(int x) {
        for (int i = 0; i < this.size; i++) {
            if (this.data[i] == x) {
                return false;
            }
        }
        add(x);
        return true;
    }

    public void addUnique(IntList that) {
        int thatSize = that.size;
        for (int i = 0; i < thatSize; i++) {
            addUnique(that.get(i));
        }
    }

    /* access modifiers changed from: protected */
    public void resize(int newsize) {
        int[] newdata = new int[newsize];
        System.arraycopy(this.data, 0, newdata, 0, this.size);
        this.data = newdata;
    }

    public String toString() {
        String result = "IntList: [";
        for (int i = 0; i < this.size; i++) {
            result = String.valueOf(result) + this.data[i] + ", ";
        }
        return String.valueOf(result) + "]";
    }

    public boolean contains(int x) {
        for (int i = 0; i < this.size; i++) {
            if (this.data[i] == x) {
                return true;
            }
        }
        return false;
    }

    public void sortAscending() {
        Arrays.sort(this.data, 0, this.size);
    }

    public IntList makeCopy() {
        IntList result = new IntList(this.size);
        System.arraycopy(this.data, 0, result.data, 0, this.size);
        result.size = this.size;
        return result;
    }

    public void set(int index, int x) {
        if (index >= this.data.length) {
            resize(Math.max(this.data.length << 1, index + 2));
        }
        if (index > this.size) {
            this.size = index;
        }
        this.data[index] = x;
    }
}
