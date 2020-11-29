package org.apache.commons.math3.dfp;

import com.lavadip.skeye.catalog.CatalogManager;
import org.apache.commons.math3.dfp.DfpField;
import org.apache.commons.math3.random.EmpiricalDistribution;

public class DfpDec extends Dfp {
    protected DfpDec(DfpField factory) {
        super(factory);
    }

    protected DfpDec(DfpField factory, byte x) {
        super(factory, x);
    }

    protected DfpDec(DfpField factory, int x) {
        super(factory, x);
    }

    protected DfpDec(DfpField factory, long x) {
        super(factory, x);
    }

    protected DfpDec(DfpField factory, double x) {
        super(factory, x);
        round(0);
    }

    public DfpDec(Dfp d) {
        super(d);
        round(0);
    }

    protected DfpDec(DfpField factory, String s) {
        super(factory, s);
        round(0);
    }

    protected DfpDec(DfpField factory, byte sign, byte nans) {
        super(factory, sign, nans);
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance() {
        return new DfpDec(getField());
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance(byte x) {
        return new DfpDec(getField(), x);
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance(int x) {
        return new DfpDec(getField(), x);
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance(long x) {
        return new DfpDec(getField(), x);
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance(double x) {
        return new DfpDec(getField(), x);
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance(Dfp d) {
        if (getField().getRadixDigits() == d.getField().getRadixDigits()) {
            return new DfpDec(d);
        }
        getField().setIEEEFlagsBits(1);
        Dfp result = newInstance(getZero());
        result.nans = 3;
        return dotrap(1, "newInstance", d, result);
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance(String s) {
        return new DfpDec(getField(), s);
    }

    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp newInstance(byte sign, byte nans) {
        return new DfpDec(getField(), sign, nans);
    }

    /* access modifiers changed from: protected */
    public int getDecimalDigits() {
        return (getRadixDigits() * 4) - 3;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.dfp.Dfp
    public int round(int in) {
        int n;
        int discarded;
        boolean inc;
        int msb = this.mant[this.mant.length - 1];
        if (msb == 0) {
            return 0;
        }
        int cmaxdigits = this.mant.length * 4;
        int lsbthreshold = EmpiricalDistribution.DEFAULT_BIN_COUNT;
        while (lsbthreshold > msb) {
            lsbthreshold /= 10;
            cmaxdigits--;
        }
        int digits = getDecimalDigits();
        int lsbshift = cmaxdigits - digits;
        int lsd = lsbshift / 4;
        int lsbthreshold2 = 1;
        for (int i = 0; i < lsbshift % 4; i++) {
            lsbthreshold2 *= 10;
        }
        int lsb = this.mant[lsd];
        if (lsbthreshold2 <= 1 && digits == (this.mant.length * 4) - 3) {
            return super.round(in);
        }
        if (lsbthreshold2 == 1) {
            n = (this.mant[lsd - 1] / EmpiricalDistribution.DEFAULT_BIN_COUNT) % 10;
            int[] iArr = this.mant;
            int i2 = lsd - 1;
            iArr[i2] = iArr[i2] % EmpiricalDistribution.DEFAULT_BIN_COUNT;
            discarded = in | this.mant[lsd - 1];
        } else {
            n = ((lsb * 10) / lsbthreshold2) % 10;
            discarded = in | (lsb % (lsbthreshold2 / 10));
        }
        for (int i3 = 0; i3 < lsd; i3++) {
            discarded |= this.mant[i3];
            this.mant[i3] = 0;
        }
        this.mant[lsd] = (lsb / lsbthreshold2) * lsbthreshold2;
        switch (C02191.$SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[getField().getRoundingMode().ordinal()]) {
            case 1:
                inc = false;
                break;
            case 2:
                if (n != 0 || discarded != 0) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case 3:
                if (n >= 5) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case 4:
                if (n > 5) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case 5:
                if (n > 5 || ((n == 5 && discarded != 0) || (n == 5 && discarded == 0 && ((lsb / lsbthreshold2) & 1) == 1))) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
                break;
            case 6:
                if (n > 5 || ((n == 5 && discarded != 0) || (n == 5 && discarded == 0 && ((lsb / lsbthreshold2) & 1) == 0))) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case CatalogManager.CATALOG_ID_SATELLITE:
                if (this.sign != 1 || (n == 0 && discarded == 0)) {
                    inc = false;
                    break;
                } else {
                    inc = true;
                    break;
                }
            default:
                if (this.sign != -1 || (n == 0 && discarded == 0)) {
                    inc = false;
                    break;
                } else {
                    inc = true;
                    break;
                }
        }
        if (inc) {
            int rh = lsbthreshold2;
            for (int i4 = lsd; i4 < this.mant.length; i4++) {
                int r = this.mant[i4] + rh;
                rh = r / Dfp.RADIX;
                this.mant[i4] = r % Dfp.RADIX;
            }
            if (rh != 0) {
                shiftRight();
                this.mant[this.mant.length - 1] = rh;
            }
        }
        if (this.exp < -32767) {
            getField().setIEEEFlagsBits(8);
            return 8;
        } else if (this.exp > 32768) {
            getField().setIEEEFlagsBits(4);
            return 4;
        } else if (n == 0 && discarded == 0) {
            return 0;
        } else {
            getField().setIEEEFlagsBits(16);
            return 16;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: org.apache.commons.math3.dfp.DfpDec$1 */
    public static /* synthetic */ class C02191 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode = new int[DfpField.RoundingMode.values().length];

        static {
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_DOWN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_UP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_HALF_UP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_HALF_DOWN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_HALF_EVEN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_HALF_ODD.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_CEIL.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[DfpField.RoundingMode.ROUND_FLOOR.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r8v0, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.dfp.Dfp
    public Dfp nextAfter(Dfp x) {
        Dfp inc;
        Dfp result;
        if (getField().getRadixDigits() != x.getField().getRadixDigits()) {
            getField().setIEEEFlagsBits(1);
            Dfp result2 = newInstance(getZero());
            result2.nans = 3;
            return dotrap(1, "nextAfter", x, result2);
        }
        boolean up = false;
        if (lessThan(x)) {
            up = true;
        }
        if (equals(x)) {
            return newInstance(x);
        }
        if (lessThan(getZero())) {
            up = !up;
        }
        if (up) {
            Dfp inc2 = copysign(power10((intLog10() - getDecimalDigits()) + 1), this);
            if (equals(getZero())) {
                inc2 = power10K((-32767 - this.mant.length) - 1);
            }
            if (inc2.equals(getZero())) {
                result = copysign(newInstance(getZero()), this);
            } else {
                result = add(inc2);
            }
        } else {
            Dfp inc3 = copysign(power10(intLog10()), this);
            if (equals(inc3)) {
                inc = inc3.divide(power10(getDecimalDigits()));
            } else {
                inc = inc3.divide(power10(getDecimalDigits() - 1));
            }
            if (equals(getZero())) {
                inc = power10K((-32767 - this.mant.length) - 1);
            }
            if (inc.equals(getZero())) {
                result = copysign(newInstance(getZero()), this);
            } else {
                result = subtract(inc);
            }
        }
        if (result.classify() == 1 && classify() != 1) {
            getField().setIEEEFlagsBits(16);
            result = dotrap(16, "nextAfter", x, result);
        }
        if (!result.equals(getZero()) || equals(getZero())) {
            return result;
        }
        getField().setIEEEFlagsBits(16);
        return dotrap(16, "nextAfter", x, result);
    }
}
