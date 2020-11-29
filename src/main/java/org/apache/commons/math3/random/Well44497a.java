package org.apache.commons.math3.random;

public class Well44497a extends AbstractWell {

    /* renamed from: K */
    private static final int f368K = 44497;

    /* renamed from: M1 */
    private static final int f369M1 = 23;

    /* renamed from: M2 */
    private static final int f370M2 = 481;

    /* renamed from: M3 */
    private static final int f371M3 = 229;
    private static final long serialVersionUID = -3859207588353972099L;

    public Well44497a() {
        super(f368K, f369M1, f370M2, f371M3);
    }

    public Well44497a(int seed) {
        super((int) f368K, (int) f369M1, (int) f370M2, (int) f371M3, seed);
    }

    public Well44497a(int[] seed) {
        super((int) f368K, (int) f369M1, (int) f370M2, (int) f371M3, seed);
    }

    public Well44497a(long seed) {
        super((int) f368K, (int) f369M1, (int) f370M2, (int) f371M3, seed);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.random.BitsStreamGenerator, org.apache.commons.math3.random.AbstractWell
    public int next(int bits) {
        int z2Second;
        int indexRm1 = this.iRm1[this.index];
        int indexRm2 = this.iRm2[this.index];
        int v0 = this.f350v[this.index];
        int vM1 = this.f350v[this.f347i1[this.index]];
        int vM2 = this.f350v[this.f348i2[this.index]];
        int vM3 = this.f350v[this.f349i3[this.index]];
        int z0 = (this.f350v[indexRm1] & -32768) ^ (this.f350v[indexRm2] & 32767);
        int z1 = ((v0 << 24) ^ v0) ^ ((vM1 >>> 30) ^ vM1);
        int z2 = ((vM2 << 10) ^ vM2) ^ (vM3 << 26);
        int z3 = z1 ^ z2;
        int z2Prime = ((z2 << 9) ^ (z2 >>> f369M1)) & -67108865;
        if ((131072 & z2) != 0) {
            z2Second = z2Prime ^ -1221985044;
        } else {
            z2Second = z2Prime;
        }
        int z4 = ((((z1 >>> 20) ^ z1) ^ z0) ^ z2Second) ^ z3;
        this.f350v[this.index] = z3;
        this.f350v[indexRm1] = z4;
        int[] iArr = this.f350v;
        iArr[indexRm2] = iArr[indexRm2] & -32768;
        this.index = indexRm1;
        return z4 >>> (32 - bits);
    }
}
