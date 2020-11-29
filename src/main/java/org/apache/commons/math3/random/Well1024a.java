package org.apache.commons.math3.random;

public class Well1024a extends AbstractWell {

    /* renamed from: K */
    private static final int f356K = 1024;

    /* renamed from: M1 */
    private static final int f357M1 = 3;

    /* renamed from: M2 */
    private static final int f358M2 = 24;

    /* renamed from: M3 */
    private static final int f359M3 = 10;
    private static final long serialVersionUID = 5680173464174485492L;

    public Well1024a() {
        super(f356K, 3, 24, f359M3);
    }

    public Well1024a(int seed) {
        super((int) f356K, 3, 24, (int) f359M3, seed);
    }

    public Well1024a(int[] seed) {
        super((int) f356K, 3, 24, (int) f359M3, seed);
    }

    public Well1024a(long seed) {
        super((int) f356K, 3, 24, (int) f359M3, seed);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.random.BitsStreamGenerator, org.apache.commons.math3.random.AbstractWell
    public int next(int bits) {
        int indexRm1 = this.iRm1[this.index];
        int v0 = this.f350v[this.index];
        int vM1 = this.f350v[this.f347i1[this.index]];
        int vM2 = this.f350v[this.f348i2[this.index]];
        int vM3 = this.f350v[this.f349i3[this.index]];
        int z0 = this.f350v[indexRm1];
        int z1 = v0 ^ ((vM1 >>> 8) ^ vM1);
        int z2 = ((vM2 << 19) ^ vM2) ^ ((vM3 << 14) ^ vM3);
        int z4 = (((z0 << 11) ^ z0) ^ ((z1 << 7) ^ z1)) ^ ((z2 << 13) ^ z2);
        this.f350v[this.index] = z1 ^ z2;
        this.f350v[indexRm1] = z4;
        this.index = indexRm1;
        return z4 >>> (32 - bits);
    }
}
