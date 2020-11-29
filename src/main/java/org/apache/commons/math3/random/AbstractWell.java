package org.apache.commons.math3.random;

import java.io.Serializable;
import org.apache.commons.math3.util.FastMath;

public abstract class AbstractWell extends BitsStreamGenerator implements Serializable {
    private static final long serialVersionUID = -817701723016583596L;

    /* renamed from: i1 */
    protected final int[] f347i1;

    /* renamed from: i2 */
    protected final int[] f348i2;

    /* renamed from: i3 */
    protected final int[] f349i3;
    protected final int[] iRm1;
    protected final int[] iRm2;
    protected int index;

    /* renamed from: v */
    protected final int[] f350v;

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.random.BitsStreamGenerator
    public abstract int next(int i);

    protected AbstractWell(int k, int m1, int m2, int m3) {
        this(k, m1, m2, m3, (int[]) null);
    }

    protected AbstractWell(int k, int m1, int m2, int m3, int seed) {
        this(k, m1, m2, m3, new int[]{seed});
    }

    protected AbstractWell(int k, int m1, int m2, int m3, int[] seed) {
        int r = ((k + 32) - 1) / 32;
        this.f350v = new int[r];
        this.index = 0;
        this.iRm1 = new int[r];
        this.iRm2 = new int[r];
        this.f347i1 = new int[r];
        this.f348i2 = new int[r];
        this.f349i3 = new int[r];
        for (int j = 0; j < r; j++) {
            this.iRm1[j] = ((j + r) - 1) % r;
            this.iRm2[j] = ((j + r) - 2) % r;
            this.f347i1[j] = (j + m1) % r;
            this.f348i2[j] = (j + m2) % r;
            this.f349i3[j] = (j + m3) % r;
        }
        setSeed(seed);
    }

    protected AbstractWell(int k, int m1, int m2, int m3, long seed) {
        this(k, m1, m2, m3, new int[]{(int) (seed >>> 32), (int) (4294967295L & seed)});
    }

    @Override // org.apache.commons.math3.random.RandomGenerator, org.apache.commons.math3.random.BitsStreamGenerator
    public void setSeed(int seed) {
        setSeed(new int[]{seed});
    }

    @Override // org.apache.commons.math3.random.RandomGenerator, org.apache.commons.math3.random.BitsStreamGenerator
    public void setSeed(int[] seed) {
        if (seed == null) {
            setSeed(System.currentTimeMillis() + ((long) System.identityHashCode(this)));
            return;
        }
        System.arraycopy(seed, 0, this.f350v, 0, FastMath.min(seed.length, this.f350v.length));
        if (seed.length < this.f350v.length) {
            for (int i = seed.length; i < this.f350v.length; i++) {
                long l = (long) this.f350v[i - seed.length];
                this.f350v[i] = (int) (((1812433253 * ((l >> 30) ^ l)) + ((long) i)) & 4294967295L);
            }
        }
        this.index = 0;
        clear();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator, org.apache.commons.math3.random.BitsStreamGenerator
    public void setSeed(long seed) {
        setSeed(new int[]{(int) (seed >>> 32), (int) (4294967295L & seed)});
    }
}
