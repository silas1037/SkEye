package org.apache.commons.math3.dfp;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class DfpField implements Field<Dfp> {
    public static final int FLAG_DIV_ZERO = 2;
    public static final int FLAG_INEXACT = 16;
    public static final int FLAG_INVALID = 1;
    public static final int FLAG_OVERFLOW = 4;
    public static final int FLAG_UNDERFLOW = 8;
    private static String eString;
    private static String ln10String;
    private static String ln2String;
    private static String ln5String;
    private static String piString;
    private static String sqr2ReciprocalString;
    private static String sqr2String;
    private static String sqr3ReciprocalString;
    private static String sqr3String;

    /* renamed from: e */
    private final Dfp f154e;
    private final Dfp[] eSplit;
    private int ieeeFlags;
    private final Dfp ln10;
    private final Dfp ln2;
    private final Dfp[] ln2Split;
    private final Dfp ln5;
    private final Dfp[] ln5Split;
    private final Dfp one;

    /* renamed from: pi */
    private final Dfp f155pi;
    private final Dfp[] piSplit;
    private RoundingMode rMode;
    private final int radixDigits;
    private final Dfp sqr2;
    private final Dfp sqr2Reciprocal;
    private final Dfp[] sqr2Split;
    private final Dfp sqr3;
    private final Dfp sqr3Reciprocal;
    private final Dfp two;
    private final Dfp zero;

    public enum RoundingMode {
        ROUND_DOWN,
        ROUND_UP,
        ROUND_HALF_UP,
        ROUND_HALF_DOWN,
        ROUND_HALF_EVEN,
        ROUND_HALF_ODD,
        ROUND_CEIL,
        ROUND_FLOOR
    }

    public DfpField(int decimalDigits) {
        this(decimalDigits, true);
    }

    private DfpField(int decimalDigits, boolean computeConstants) {
        int i;
        if (decimalDigits < 13) {
            i = 4;
        } else {
            i = (decimalDigits + 3) / 4;
        }
        this.radixDigits = i;
        this.rMode = RoundingMode.ROUND_HALF_EVEN;
        this.ieeeFlags = 0;
        this.zero = new Dfp(this, 0);
        this.one = new Dfp(this, 1);
        this.two = new Dfp(this, 2);
        if (computeConstants) {
            synchronized (DfpField.class) {
                computeStringConstants(decimalDigits < 67 ? 200 : decimalDigits * 3);
                this.sqr2 = new Dfp(this, sqr2String);
                this.sqr2Split = split(sqr2String);
                this.sqr2Reciprocal = new Dfp(this, sqr2ReciprocalString);
                this.sqr3 = new Dfp(this, sqr3String);
                this.sqr3Reciprocal = new Dfp(this, sqr3ReciprocalString);
                this.f155pi = new Dfp(this, piString);
                this.piSplit = split(piString);
                this.f154e = new Dfp(this, eString);
                this.eSplit = split(eString);
                this.ln2 = new Dfp(this, ln2String);
                this.ln2Split = split(ln2String);
                this.ln5 = new Dfp(this, ln5String);
                this.ln5Split = split(ln5String);
                this.ln10 = new Dfp(this, ln10String);
            }
            return;
        }
        this.sqr2 = null;
        this.sqr2Split = null;
        this.sqr2Reciprocal = null;
        this.sqr3 = null;
        this.sqr3Reciprocal = null;
        this.f155pi = null;
        this.piSplit = null;
        this.f154e = null;
        this.eSplit = null;
        this.ln2 = null;
        this.ln2Split = null;
        this.ln5 = null;
        this.ln5Split = null;
        this.ln10 = null;
    }

    public int getRadixDigits() {
        return this.radixDigits;
    }

    public void setRoundingMode(RoundingMode mode) {
        this.rMode = mode;
    }

    public RoundingMode getRoundingMode() {
        return this.rMode;
    }

    public int getIEEEFlags() {
        return this.ieeeFlags;
    }

    public void clearIEEEFlags() {
        this.ieeeFlags = 0;
    }

    public void setIEEEFlags(int flags) {
        this.ieeeFlags = flags & 31;
    }

    public void setIEEEFlagsBits(int bits) {
        this.ieeeFlags |= bits & 31;
    }

    public Dfp newDfp() {
        return new Dfp(this);
    }

    public Dfp newDfp(byte x) {
        return new Dfp(this, x);
    }

    public Dfp newDfp(int x) {
        return new Dfp(this, x);
    }

    public Dfp newDfp(long x) {
        return new Dfp(this, x);
    }

    public Dfp newDfp(double x) {
        return new Dfp(this, x);
    }

    public Dfp newDfp(Dfp d) {
        return new Dfp(d);
    }

    public Dfp newDfp(String s) {
        return new Dfp(this, s);
    }

    public Dfp newDfp(byte sign, byte nans) {
        return new Dfp(this, sign, nans);
    }

    @Override // org.apache.commons.math3.Field
    public Dfp getZero() {
        return this.zero;
    }

    @Override // org.apache.commons.math3.Field
    public Dfp getOne() {
        return this.one;
    }

    @Override // org.apache.commons.math3.Field
    public Class<? extends FieldElement<Dfp>> getRuntimeClass() {
        return Dfp.class;
    }

    public Dfp getTwo() {
        return this.two;
    }

    public Dfp getSqr2() {
        return this.sqr2;
    }

    public Dfp[] getSqr2Split() {
        return (Dfp[]) this.sqr2Split.clone();
    }

    public Dfp getSqr2Reciprocal() {
        return this.sqr2Reciprocal;
    }

    public Dfp getSqr3() {
        return this.sqr3;
    }

    public Dfp getSqr3Reciprocal() {
        return this.sqr3Reciprocal;
    }

    public Dfp getPi() {
        return this.f155pi;
    }

    public Dfp[] getPiSplit() {
        return (Dfp[]) this.piSplit.clone();
    }

    public Dfp getE() {
        return this.f154e;
    }

    public Dfp[] getESplit() {
        return (Dfp[]) this.eSplit.clone();
    }

    public Dfp getLn2() {
        return this.ln2;
    }

    public Dfp[] getLn2Split() {
        return (Dfp[]) this.ln2Split.clone();
    }

    public Dfp getLn5() {
        return this.ln5;
    }

    public Dfp[] getLn5Split() {
        return (Dfp[]) this.ln5Split.clone();
    }

    public Dfp getLn10() {
        return this.ln10;
    }

    private Dfp[] split(String a) {
        Dfp[] result = new Dfp[2];
        boolean leading = true;
        int sp = 0;
        int sig = 0;
        char[] buf = new char[a.length()];
        int i = 0;
        while (true) {
            if (i >= buf.length) {
                break;
            }
            buf[i] = a.charAt(i);
            if (buf[i] >= '1' && buf[i] <= '9') {
                leading = false;
            }
            if (buf[i] == '.') {
                sig += (400 - sig) % 4;
                leading = false;
            }
            if (sig == (this.radixDigits / 2) * 4) {
                sp = i;
                break;
            }
            if (buf[i] >= '0' && buf[i] <= '9' && !leading) {
                sig++;
            }
            i++;
        }
        result[0] = new Dfp(this, new String(buf, 0, sp));
        for (int i2 = 0; i2 < buf.length; i2++) {
            buf[i2] = a.charAt(i2);
            if (buf[i2] >= '0' && buf[i2] <= '9' && i2 < sp) {
                buf[i2] = '0';
            }
        }
        result[1] = new Dfp(this, new String(buf));
        return result;
    }

    private static void computeStringConstants(int highPrecisionDecimalDigits) {
        if (sqr2String == null || sqr2String.length() < highPrecisionDecimalDigits - 3) {
            DfpField highPrecisionField = new DfpField(highPrecisionDecimalDigits, false);
            Dfp highPrecisionOne = new Dfp(highPrecisionField, 1);
            Dfp highPrecisionTwo = new Dfp(highPrecisionField, 2);
            Dfp highPrecisionThree = new Dfp(highPrecisionField, 3);
            Dfp highPrecisionSqr2 = highPrecisionTwo.sqrt();
            sqr2String = highPrecisionSqr2.toString();
            sqr2ReciprocalString = highPrecisionOne.divide(highPrecisionSqr2).toString();
            Dfp highPrecisionSqr3 = highPrecisionThree.sqrt();
            sqr3String = highPrecisionSqr3.toString();
            sqr3ReciprocalString = highPrecisionOne.divide(highPrecisionSqr3).toString();
            piString = computePi(highPrecisionOne, highPrecisionTwo, highPrecisionThree).toString();
            eString = computeExp(highPrecisionOne, highPrecisionOne).toString();
            ln2String = computeLn(highPrecisionTwo, highPrecisionOne, highPrecisionTwo).toString();
            ln5String = computeLn(new Dfp(highPrecisionField, 5), highPrecisionOne, highPrecisionTwo).toString();
            ln10String = computeLn(new Dfp(highPrecisionField, 10), highPrecisionOne, highPrecisionTwo).toString();
        }
    }

    private static Dfp computePi(Dfp one2, Dfp two2, Dfp three) {
        Dfp sqrt2 = two2.sqrt();
        Dfp yk = sqrt2.subtract(one2);
        Dfp four = two2.add(two2);
        Dfp two2kp3 = two2;
        Dfp ak = two2.multiply(three.subtract(two2.multiply(sqrt2)));
        for (int i = 1; i < 20; i++) {
            Dfp y2 = yk.multiply(yk);
            Dfp s = one2.subtract(y2.multiply(y2)).sqrt().sqrt();
            yk = one2.subtract(s).divide(one2.add(s));
            two2kp3 = two2kp3.multiply(four);
            Dfp p = one2.add(yk);
            Dfp p2 = p.multiply(p);
            ak = ak.multiply(p2.multiply(p2)).subtract(two2kp3.multiply(yk).multiply(one2.add(yk).add(yk.multiply(yk))));
            if (yk.equals(yk)) {
                break;
            }
        }
        return one2.divide(ak);
    }

    public static Dfp computeExp(Dfp a, Dfp one2) {
        Dfp y = new Dfp(one2);
        Dfp py = new Dfp(one2);
        Dfp f = new Dfp(one2);
        Dfp fi = new Dfp(one2);
        Dfp x = new Dfp(one2);
        for (int i = 0; i < 10000; i++) {
            x = x.multiply(a);
            y = y.add(x.divide(f));
            fi = fi.add(one2);
            f = f.multiply(fi);
            if (y.equals(py)) {
                break;
            }
            py = new Dfp(y);
        }
        return y;
    }

    public static Dfp computeLn(Dfp a, Dfp one2, Dfp two2) {
        int den = 1;
        Dfp x = a.add(new Dfp(a.getField(), -1)).divide(a.add(one2));
        Dfp y = new Dfp(x);
        Dfp num = new Dfp(x);
        Dfp py = new Dfp(y);
        for (int i = 0; i < 10000; i++) {
            num = num.multiply(x).multiply(x);
            den += 2;
            y = y.add(num.divide(den));
            if (y.equals(py)) {
                break;
            }
            py = new Dfp(y);
        }
        return y.multiply(two2);
    }
}
