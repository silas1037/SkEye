package org.apache.commons.math3.random;

import java.io.Serializable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;

public abstract class BitsStreamGenerator implements RandomGenerator, Serializable {
    private static final long serialVersionUID = 20130104;
    private double nextGaussian = Double.NaN;

    /* access modifiers changed from: protected */
    public abstract int next(int i);

    @Override // org.apache.commons.math3.random.RandomGenerator
    public abstract void setSeed(int i);

    @Override // org.apache.commons.math3.random.RandomGenerator
    public abstract void setSeed(long j);

    @Override // org.apache.commons.math3.random.RandomGenerator
    public abstract void setSeed(int[] iArr);

    @Override // org.apache.commons.math3.random.RandomGenerator
    public boolean nextBoolean() {
        return next(1) != 0;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public double nextDouble() {
        return ((double) (((long) next(26)) | (((long) next(26)) << 26))) * 2.220446049250313E-16d;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public float nextFloat() {
        return ((float) next(23)) * 1.1920929E-7f;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public double nextGaussian() {
        if (Double.isNaN(this.nextGaussian)) {
            double alpha = 6.283185307179586d * nextDouble();
            double r = FastMath.sqrt(-2.0d * FastMath.log(nextDouble()));
            double random = r * FastMath.cos(alpha);
            this.nextGaussian = FastMath.sin(alpha) * r;
            return random;
        }
        double random2 = this.nextGaussian;
        this.nextGaussian = Double.NaN;
        return random2;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public int nextInt() {
        return next(32);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public int nextInt(int n) throws IllegalArgumentException {
        int bits;
        int val;
        if (n <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(n));
        } else if (((-n) & n) == n) {
            return (int) ((((long) n) * ((long) next(31))) >> 31);
        } else {
            do {
                bits = next(31);
                val = bits % n;
            } while ((bits - val) + (n - 1) < 0);
            return val;
        }
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public long nextLong() {
        return (((long) next(32)) << 32) | (((long) next(32)) & 4294967295L);
    }

    public long nextLong(long n) throws IllegalArgumentException {
        long bits;
        long val;
        if (n > 0) {
            do {
                bits = (((long) next(31)) << 32) | (((long) next(32)) & 4294967295L);
                val = bits % n;
            } while ((bits - val) + (n - 1) < 0);
            return val;
        }
        throw new NotStrictlyPositiveException(Long.valueOf(n));
    }

    public void clear() {
        this.nextGaussian = Double.NaN;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void nextBytes(byte[] bytes) {
        nextBytesFill(bytes, 0, bytes.length);
    }

    public void nextBytes(byte[] bytes, int start, int len) {
        if (start < 0 || start >= bytes.length) {
            throw new OutOfRangeException(Integer.valueOf(start), 0, Integer.valueOf(bytes.length));
        } else if (len < 0 || len > bytes.length - start) {
            throw new OutOfRangeException(Integer.valueOf(len), 0, Integer.valueOf(bytes.length - start));
        } else {
            nextBytesFill(bytes, start, len);
        }
    }

    private void nextBytesFill(byte[] bytes, int start, int len) {
        int indexLoopLimit = start + (2147483644 & len);
        int index = start;
        while (index < indexLoopLimit) {
            int random = next(32);
            int index2 = index + 1;
            bytes[index] = (byte) random;
            int index3 = index2 + 1;
            bytes[index2] = (byte) (random >>> 8);
            int index4 = index3 + 1;
            bytes[index3] = (byte) (random >>> 16);
            index = index4 + 1;
            bytes[index4] = (byte) (random >>> 24);
        }
        int indexLimit = start + len;
        if (index < indexLimit) {
            int random2 = next(32);
            while (true) {
                index++;
                bytes[index] = (byte) random2;
                if (index >= indexLimit) {
                    break;
                }
                random2 >>>= 8;
            }
        }
    }
}
