package org.apache.commons.math3.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.OutOfRangeException;

public class Combinations implements Iterable<int[]> {
    private final IterationOrder iterationOrder;

    /* renamed from: k */
    private final int f407k;

    /* renamed from: n */
    private final int f408n;

    /* access modifiers changed from: private */
    public enum IterationOrder {
        LEXICOGRAPHIC
    }

    public Combinations(int n, int k) {
        this(n, k, IterationOrder.LEXICOGRAPHIC);
    }

    private Combinations(int n, int k, IterationOrder iterationOrder2) {
        CombinatoricsUtils.checkBinomial(n, k);
        this.f408n = n;
        this.f407k = k;
        this.iterationOrder = iterationOrder2;
    }

    public int getN() {
        return this.f408n;
    }

    public int getK() {
        return this.f407k;
    }

    @Override // java.lang.Iterable
    public Iterator<int[]> iterator() {
        if (this.f407k == 0 || this.f407k == this.f408n) {
            return new SingletonIterator(MathArrays.natural(this.f407k));
        }
        switch (this.iterationOrder) {
            case LEXICOGRAPHIC:
                return new LexicographicIterator(this.f408n, this.f407k);
            default:
                throw new MathInternalError();
        }
    }

    public Comparator<int[]> comparator() {
        return new LexicographicComparator(this.f408n, this.f407k);
    }

    /* access modifiers changed from: private */
    public static class LexicographicIterator implements Iterator<int[]> {

        /* renamed from: c */
        private final int[] f412c;

        /* renamed from: j */
        private int f413j;

        /* renamed from: k */
        private final int f414k;
        private boolean more = true;

        LexicographicIterator(int n, int k) {
            this.f414k = k;
            this.f412c = new int[(k + 3)];
            if (k == 0 || k >= n) {
                this.more = false;
                return;
            }
            for (int i = 1; i <= k; i++) {
                this.f412c[i] = i - 1;
            }
            this.f412c[k + 1] = n;
            this.f412c[k + 2] = 0;
            this.f413j = k;
        }

        public boolean hasNext() {
            return this.more;
        }

        @Override // java.util.Iterator
        public int[] next() {
            if (!this.more) {
                throw new NoSuchElementException();
            }
            int[] ret = new int[this.f414k];
            System.arraycopy(this.f412c, 1, ret, 0, this.f414k);
            int x = 0;
            if (this.f413j > 0) {
                this.f412c[this.f413j] = this.f413j;
                this.f413j--;
            } else if (this.f412c[1] + 1 < this.f412c[2]) {
                int[] iArr = this.f412c;
                iArr[1] = iArr[1] + 1;
            } else {
                this.f413j = 2;
                boolean stepDone = false;
                while (!stepDone) {
                    this.f412c[this.f413j - 1] = this.f413j - 2;
                    x = this.f412c[this.f413j] + 1;
                    if (x == this.f412c[this.f413j + 1]) {
                        this.f413j++;
                    } else {
                        stepDone = true;
                    }
                }
                if (this.f413j > this.f414k) {
                    this.more = false;
                } else {
                    this.f412c[this.f413j] = x;
                    this.f413j--;
                }
            }
            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /* access modifiers changed from: private */
    public static class SingletonIterator implements Iterator<int[]> {
        private boolean more = true;
        private final int[] singleton;

        SingletonIterator(int[] singleton2) {
            this.singleton = singleton2;
        }

        public boolean hasNext() {
            return this.more;
        }

        @Override // java.util.Iterator
        public int[] next() {
            if (this.more) {
                this.more = false;
                return this.singleton;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class LexicographicComparator implements Comparator<int[]>, Serializable {
        private static final long serialVersionUID = 20130906;

        /* renamed from: k */
        private final int f410k;

        /* renamed from: n */
        private final int f411n;

        LexicographicComparator(int n, int k) {
            this.f411n = n;
            this.f410k = k;
        }

        public int compare(int[] c1, int[] c2) {
            if (c1.length != this.f410k) {
                throw new DimensionMismatchException(c1.length, this.f410k);
            } else if (c2.length != this.f410k) {
                throw new DimensionMismatchException(c2.length, this.f410k);
            } else {
                int[] c1s = MathArrays.copyOf(c1);
                Arrays.sort(c1s);
                int[] c2s = MathArrays.copyOf(c2);
                Arrays.sort(c2s);
                long v1 = lexNorm(c1s);
                long v2 = lexNorm(c2s);
                if (v1 < v2) {
                    return -1;
                }
                if (v1 > v2) {
                    return 1;
                }
                return 0;
            }
        }

        private long lexNorm(int[] c) {
            long ret = 0;
            for (int i = 0; i < c.length; i++) {
                int digit = c[i];
                if (digit < 0 || digit >= this.f411n) {
                    throw new OutOfRangeException(Integer.valueOf(digit), 0, Integer.valueOf(this.f411n - 1));
                }
                ret += (long) (c[i] * ArithmeticUtils.pow(this.f411n, i));
            }
            return ret;
        }
    }
}
