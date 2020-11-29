package org.apache.commons.math3.random;

import java.io.Serializable;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.util.FastMath;

public class MersenneTwister extends BitsStreamGenerator implements Serializable {

    /* renamed from: M */
    private static final int f351M = 397;
    private static final int[] MAG01 = {0, -1727483681};

    /* renamed from: N */
    private static final int f352N = 624;
    private static final long serialVersionUID = 8661194735290153518L;

    /* renamed from: mt */
    private int[] f353mt;
    private int mti;

    public MersenneTwister() {
        this.f353mt = new int[f352N];
        setSeed(System.currentTimeMillis() + ((long) System.identityHashCode(this)));
    }

    public MersenneTwister(int seed) {
        this.f353mt = new int[f352N];
        setSeed(seed);
    }

    public MersenneTwister(int[] seed) {
        this.f353mt = new int[f352N];
        setSeed(seed);
    }

    public MersenneTwister(long seed) {
        this.f353mt = new int[f352N];
        setSeed(seed);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator, org.apache.commons.math3.random.BitsStreamGenerator
    public void setSeed(int seed) {
        long longMT = (long) seed;
        this.f353mt[0] = (int) longMT;
        this.mti = 1;
        while (this.mti < f352N) {
            longMT = ((1812433253 * ((longMT >> 30) ^ longMT)) + ((long) this.mti)) & 4294967295L;
            this.f353mt[this.mti] = (int) longMT;
            this.mti++;
        }
        clear();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator, org.apache.commons.math3.random.BitsStreamGenerator
    public void setSeed(int[] seed) {
        if (seed == null) {
            setSeed(System.currentTimeMillis() + ((long) System.identityHashCode(this)));
            return;
        }
        setSeed(19650218);
        int i = 1;
        int j = 0;
        for (int k = FastMath.max((int) f352N, seed.length); k != 0; k--) {
            long l0 = (2147483647L & ((long) this.f353mt[i])) | (this.f353mt[i] < 0 ? 2147483648L : 0);
            long l1 = (2147483647L & ((long) this.f353mt[i - 1])) | (this.f353mt[i + -1] < 0 ? 2147483648L : 0);
            this.f353mt[i] = (int) (4294967295L & (((((l1 >> 30) ^ l1) * 1664525) ^ l0) + ((long) seed[j]) + ((long) j)));
            i++;
            j++;
            if (i >= f352N) {
                this.f353mt[0] = this.f353mt[623];
                i = 1;
            }
            if (j >= seed.length) {
                j = 0;
            }
        }
        for (int k2 = 623; k2 != 0; k2--) {
            long l02 = (2147483647L & ((long) this.f353mt[i])) | (this.f353mt[i] < 0 ? 2147483648L : 0);
            long l12 = (2147483647L & ((long) this.f353mt[i - 1])) | (this.f353mt[i + -1] < 0 ? 2147483648L : 0);
            this.f353mt[i] = (int) (4294967295L & (((((l12 >> 30) ^ l12) * 1566083941) ^ l02) - ((long) i)));
            i++;
            if (i >= f352N) {
                this.f353mt[0] = this.f353mt[623];
                i = 1;
            }
        }
        this.f353mt[0] = Integer.MIN_VALUE;
        clear();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator, org.apache.commons.math3.random.BitsStreamGenerator
    public void setSeed(long seed) {
        setSeed(new int[]{(int) (seed >>> 32), (int) (4294967295L & seed)});
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.random.BitsStreamGenerator
    public int next(int bits) {
        if (this.mti >= f352N) {
            int mtNext = this.f353mt[0];
            for (int k = 0; k < 227; k++) {
                mtNext = this.f353mt[k + 1];
                int y = (mtNext & Integer.MIN_VALUE) | (mtNext & BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
                this.f353mt[k] = (this.f353mt[k + f351M] ^ (y >>> 1)) ^ MAG01[y & 1];
            }
            for (int k2 = 227; k2 < 623; k2++) {
                mtNext = this.f353mt[k2 + 1];
                int y2 = (mtNext & Integer.MIN_VALUE) | (mtNext & BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
                this.f353mt[k2] = (this.f353mt[k2 - 227] ^ (y2 >>> 1)) ^ MAG01[y2 & 1];
            }
            int y3 = (mtNext & Integer.MIN_VALUE) | (this.f353mt[0] & BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
            this.f353mt[623] = (this.f353mt[396] ^ (y3 >>> 1)) ^ MAG01[y3 & 1];
            this.mti = 0;
        }
        int[] iArr = this.f353mt;
        int i = this.mti;
        this.mti = i + 1;
        int y4 = iArr[i];
        int y5 = y4 ^ (y4 >>> 11);
        int y6 = y5 ^ ((y5 << 7) & -1658038656);
        int y7 = y6 ^ ((y6 << 15) & -272236544);
        return (y7 ^ (y7 >>> 18)) >>> (32 - bits);
    }
}
