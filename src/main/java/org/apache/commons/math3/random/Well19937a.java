package org.apache.commons.math3.random;

public class Well19937a extends AbstractWell {

    /* renamed from: K */
    private static final int f360K = 19937;

    /* renamed from: M1 */
    private static final int f361M1 = 70;

    /* renamed from: M2 */
    private static final int f362M2 = 179;

    /* renamed from: M3 */
    private static final int f363M3 = 449;
    private static final long serialVersionUID = -7462102162223815419L;

    public Well19937a() {
        super(f360K, f361M1, f362M2, f363M3);
    }

    public Well19937a(int seed) {
        super((int) f360K, (int) f361M1, (int) f362M2, (int) f363M3, seed);
    }

    public Well19937a(int[] seed) {
        super((int) f360K, (int) f361M1, (int) f362M2, (int) f363M3, seed);
    }

    public Well19937a(long seed) {
        super((int) f360K, (int) f361M1, (int) f362M2, (int) f363M3, seed);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.random.BitsStreamGenerator, org.apache.commons.math3.random.AbstractWell
    public int next(int bits) {
        int indexRm1 = this.iRm1[this.index];
        int indexRm2 = this.iRm2[this.index];
        int v0 = this.f350v[this.index];
        int vM1 = this.f350v[this.f347i1[this.index]];
        int vM2 = this.f350v[this.f348i2[this.index]];
        int vM3 = this.f350v[this.f349i3[this.index]];
        int z1 = ((v0 << 25) ^ v0) ^ ((vM1 >>> 27) ^ vM1);
        int z2 = (vM2 >>> 9) ^ ((vM3 >>> 1) ^ vM3);
        int z3 = z1 ^ z2;
        int z4 = ((((z1 << 9) ^ z1) ^ ((Integer.MIN_VALUE & this.f350v[indexRm1]) ^ (Integer.MAX_VALUE & this.f350v[indexRm2]))) ^ ((z2 << 21) ^ z2)) ^ ((z3 >>> 21) ^ z3);
        this.f350v[this.index] = z3;
        this.f350v[indexRm1] = z4;
        int[] iArr = this.f350v;
        iArr[indexRm2] = iArr[indexRm2] & Integer.MIN_VALUE;
        this.index = indexRm1;
        return z4 >>> (32 - bits);
    }
}
