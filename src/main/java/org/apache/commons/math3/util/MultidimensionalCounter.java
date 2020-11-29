package org.apache.commons.math3.util;

import java.util.NoSuchElementException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;

public class MultidimensionalCounter implements Iterable<Integer> {
    private final int dimension;
    private final int last = (this.dimension - 1);
    private final int[] size;
    private final int totalSize;
    private final int[] uniCounterOffset = new int[this.dimension];

    public class Iterator implements java.util.Iterator<Integer> {
        private int count = -1;
        private final int[] counter = new int[MultidimensionalCounter.this.dimension];
        private final int maxCount = (MultidimensionalCounter.this.totalSize - 1);

        Iterator() {
            this.counter[MultidimensionalCounter.this.last] = -1;
        }

        public boolean hasNext() {
            return this.count < this.maxCount;
        }

        @Override // java.util.Iterator
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int i = MultidimensionalCounter.this.last;
            while (true) {
                if (i >= 0) {
                    if (this.counter[i] != MultidimensionalCounter.this.size[i] - 1) {
                        int[] iArr = this.counter;
                        iArr[i] = iArr[i] + 1;
                        break;
                    }
                    this.counter[i] = 0;
                    i--;
                } else {
                    break;
                }
            }
            int i2 = this.count + 1;
            this.count = i2;
            return Integer.valueOf(i2);
        }

        public int getCount() {
            return this.count;
        }

        public int[] getCounts() {
            return MathArrays.copyOf(this.counter);
        }

        public int getCount(int dim) {
            return this.counter[dim];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public MultidimensionalCounter(int... size2) throws NotStrictlyPositiveException {
        this.dimension = size2.length;
        this.size = MathArrays.copyOf(size2);
        int tS = size2[this.last];
        for (int i = 0; i < this.last; i++) {
            int count = 1;
            for (int j = i + 1; j < this.dimension; j++) {
                count *= size2[j];
            }
            this.uniCounterOffset[i] = count;
            tS *= size2[i];
        }
        this.uniCounterOffset[this.last] = 0;
        if (tS <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(tS));
        }
        this.totalSize = tS;
    }

    /* Return type fixed from 'org.apache.commons.math3.util.MultidimensionalCounter$Iterator' to match base method */
    @Override // java.lang.Iterable
    public java.util.Iterator<Integer> iterator() {
        return new Iterator();
    }

    public int getDimension() {
        return this.dimension;
    }

    public int[] getCounts(int index) throws OutOfRangeException {
        if (index < 0 || index >= this.totalSize) {
            throw new OutOfRangeException(Integer.valueOf(index), 0, Integer.valueOf(this.totalSize));
        }
        int[] indices = new int[this.dimension];
        int count = 0;
        for (int i = 0; i < this.last; i++) {
            int idx = 0;
            int offset = this.uniCounterOffset[i];
            while (count <= index) {
                count += offset;
                idx++;
            }
            count -= offset;
            indices[i] = idx - 1;
        }
        indices[this.last] = index - count;
        return indices;
    }

    public int getCount(int... c) throws OutOfRangeException, DimensionMismatchException {
        if (c.length != this.dimension) {
            throw new DimensionMismatchException(c.length, this.dimension);
        }
        int count = 0;
        for (int i = 0; i < this.dimension; i++) {
            int index = c[i];
            if (index < 0 || index >= this.size[i]) {
                throw new OutOfRangeException(Integer.valueOf(index), 0, Integer.valueOf(this.size[i] - 1));
            }
            count += this.uniCounterOffset[i] * c[i];
        }
        return c[this.last] + count;
    }

    public int getSize() {
        return this.totalSize;
    }

    public int[] getSizes() {
        return MathArrays.copyOf(this.size);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.dimension; i++) {
            sb.append("[").append(getCount(i)).append("]");
        }
        return sb.toString();
    }
}
