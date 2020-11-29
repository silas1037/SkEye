package org.apache.commons.math3.random;

public class Well512a extends AbstractWell {

    /* renamed from: K */
    private static final int f376K = 512;

    /* renamed from: M1 */
    private static final int f377M1 = 13;

    /* renamed from: M2 */
    private static final int f378M2 = 9;

    /* renamed from: M3 */
    private static final int f379M3 = 5;
    private static final long serialVersionUID = -6104179812103820574L;

    public Well512a() {
        super(f376K, f377M1, 9, 5);
    }

    public Well512a(int seed) {
        super((int) f376K, (int) f377M1, 9, 5, seed);
    }

    public Well512a(int[] seed) {
        super((int) f376K, (int) f377M1, 9, 5, seed);
    }

    public Well512a(long seed) {
        super((int) f376K, (int) f377M1, 9, 5, seed);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.random.BitsStreamGenerator, org.apache.commons.math3.random.AbstractWell
    public int next(int bits) {
        int indexRm1 = this.iRm1[this.index];
        int vi = this.f350v[this.index];
        int vi1 = this.f350v[this.f347i1[this.index]];
        int vi2 = this.f350v[this.f348i2[this.index]];
        int z0 = this.f350v[indexRm1];
        int z1 = ((vi << 16) ^ vi) ^ ((vi1 << 15) ^ vi1);
        int z2 = vi2 ^ (vi2 >>> 11);
        int z3 = z1 ^ z2;
        int z4 = ((((z0 << 2) ^ z0) ^ ((z1 << 18) ^ z1)) ^ (z2 << 28)) ^ (((z3 << 5) & -633066204) ^ z3);
        this.f350v[this.index] = z3;
        this.f350v[indexRm1] = z4;
        this.index = indexRm1;
        return z4 >>> (32 - bits);
    }
}
