package org.apache.commons.math3.dfp;

import com.lavadip.skeye.catalog.CatalogManager;
import java.util.Arrays;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.interpolation.MicrosphereInterpolator;
import org.apache.commons.math3.dfp.DfpField;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.util.FastMath;

public class Dfp implements RealFieldElement<Dfp> {
    private static final String ADD_TRAP = "add";
    private static final String ALIGN_TRAP = "align";
    private static final String DIVIDE_TRAP = "divide";
    public static final int ERR_SCALE = 32760;
    public static final byte FINITE = 0;
    private static final String GREATER_THAN_TRAP = "greaterThan";
    public static final byte INFINITE = 1;
    private static final String LESS_THAN_TRAP = "lessThan";
    public static final int MAX_EXP = 32768;
    public static final int MIN_EXP = -32767;
    private static final String MULTIPLY_TRAP = "multiply";
    private static final String NAN_STRING = "NaN";
    private static final String NEG_INFINITY_STRING = "-Infinity";
    private static final String NEW_INSTANCE_TRAP = "newInstance";
    private static final String NEXT_AFTER_TRAP = "nextAfter";
    private static final String POS_INFINITY_STRING = "Infinity";
    public static final byte QNAN = 3;
    public static final int RADIX = 10000;
    public static final byte SNAN = 2;
    private static final String SQRT_TRAP = "sqrt";
    private static final String TRUNC_TRAP = "trunc";
    protected int exp;
    private final DfpField field;
    protected int[] mant;
    protected byte nans;
    protected byte sign;

    protected Dfp(DfpField field2) {
        this.mant = new int[field2.getRadixDigits()];
        this.sign = 1;
        this.exp = 0;
        this.nans = 0;
        this.field = field2;
    }

    protected Dfp(DfpField field2, byte x) {
        this(field2, (long) x);
    }

    protected Dfp(DfpField field2, int x) {
        this(field2, (long) x);
    }

    protected Dfp(DfpField field2, long x) {
        this.mant = new int[field2.getRadixDigits()];
        this.nans = 0;
        this.field = field2;
        boolean isLongMin = false;
        if (x == Long.MIN_VALUE) {
            isLongMin = true;
            x++;
        }
        if (x < 0) {
            this.sign = -1;
            x = -x;
        } else {
            this.sign = 1;
        }
        this.exp = 0;
        while (x != 0) {
            System.arraycopy(this.mant, this.mant.length - this.exp, this.mant, (this.mant.length - 1) - this.exp, this.exp);
            this.mant[this.mant.length - 1] = (int) (x % 10000);
            x /= 10000;
            this.exp++;
        }
        if (isLongMin) {
            for (int i = 0; i < this.mant.length - 1; i++) {
                if (this.mant[i] != 0) {
                    int[] iArr = this.mant;
                    iArr[i] = iArr[i] + 1;
                    return;
                }
            }
        }
    }

    protected Dfp(DfpField field2, double x) {
        this.mant = new int[field2.getRadixDigits()];
        this.sign = 1;
        this.exp = 0;
        this.nans = 0;
        this.field = field2;
        long bits = Double.doubleToLongBits(x);
        long mantissa = bits & 4503599627370495L;
        int exponent = ((int) ((9218868437227405312L & bits) >> 52)) - 1023;
        if (exponent == -1023) {
            if (x != 0.0d) {
                exponent++;
                while ((4503599627370496L & mantissa) == 0) {
                    exponent--;
                    mantissa <<= 1;
                }
                mantissa &= 4503599627370495L;
            } else if ((Long.MIN_VALUE & bits) != 0) {
                this.sign = -1;
                return;
            } else {
                return;
            }
        }
        if (exponent != 1024) {
            Dfp xdfp = new Dfp(field2, mantissa).divide(new Dfp(field2, 4503599627370496L)).add(field2.getOne()).multiply(DfpMath.pow(field2.getTwo(), exponent));
            xdfp = (Long.MIN_VALUE & bits) != 0 ? xdfp.negate() : xdfp;
            System.arraycopy(xdfp.mant, 0, this.mant, 0, this.mant.length);
            this.sign = xdfp.sign;
            this.exp = xdfp.exp;
            this.nans = xdfp.nans;
        } else if (x != x) {
            this.sign = 1;
            this.nans = 3;
        } else if (x < 0.0d) {
            this.sign = -1;
            this.nans = 1;
        } else {
            this.sign = 1;
            this.nans = 1;
        }
    }

    public Dfp(Dfp d) {
        this.mant = (int[]) d.mant.clone();
        this.sign = d.sign;
        this.exp = d.exp;
        this.nans = d.nans;
        this.field = d.field;
    }

    protected Dfp(DfpField field2, String s) {
        String fpdecimal;
        this.mant = new int[field2.getRadixDigits()];
        this.sign = 1;
        this.exp = 0;
        this.nans = 0;
        this.field = field2;
        boolean decimalFound = false;
        char[] striped = new char[((getRadixDigits() * 4) + 8)];
        if (s.equals(POS_INFINITY_STRING)) {
            this.sign = 1;
            this.nans = 1;
        } else if (s.equals(NEG_INFINITY_STRING)) {
            this.sign = -1;
            this.nans = 1;
        } else if (s.equals(NAN_STRING)) {
            this.sign = 1;
            this.nans = 3;
        } else {
            int p = s.indexOf("e");
            p = p == -1 ? s.indexOf("E") : p;
            int sciexp = 0;
            if (p != -1) {
                fpdecimal = s.substring(0, p);
                String fpexp = s.substring(p + 1);
                boolean negative = false;
                for (int i = 0; i < fpexp.length(); i++) {
                    if (fpexp.charAt(i) == '-') {
                        negative = true;
                    } else if (fpexp.charAt(i) >= '0' && fpexp.charAt(i) <= '9') {
                        sciexp = ((sciexp * 10) + fpexp.charAt(i)) - 48;
                    }
                }
                if (negative) {
                    sciexp = -sciexp;
                }
            } else {
                fpdecimal = s;
            }
            if (fpdecimal.indexOf("-") != -1) {
                this.sign = -1;
            }
            int p2 = 0;
            int decimalPos = 0;
            do {
                if (fpdecimal.charAt(p2) >= '1' && fpdecimal.charAt(p2) <= '9') {
                    break;
                }
                if (decimalFound && fpdecimal.charAt(p2) == '0') {
                    decimalPos--;
                }
                decimalFound = fpdecimal.charAt(p2) == '.' ? true : decimalFound;
                p2++;
            } while (p2 != fpdecimal.length());
            int q = 4;
            striped[0] = '0';
            striped[1] = '0';
            striped[2] = '0';
            striped[3] = '0';
            int significantDigits = 0;
            while (p2 != fpdecimal.length() && q != (this.mant.length * 4) + 4 + 1) {
                if (fpdecimal.charAt(p2) == '.') {
                    decimalFound = true;
                    decimalPos = significantDigits;
                    p2++;
                } else if (fpdecimal.charAt(p2) < '0' || fpdecimal.charAt(p2) > '9') {
                    p2++;
                } else {
                    striped[q] = fpdecimal.charAt(p2);
                    q++;
                    p2++;
                    significantDigits++;
                }
            }
            if (decimalFound && q != 4) {
                while (true) {
                    q--;
                    if (q == 4 || striped[q] != '0') {
                        break;
                    }
                    significantDigits--;
                }
            }
            if (decimalFound && significantDigits == 0) {
                decimalPos = 0;
            }
            decimalPos = !decimalFound ? q - 4 : decimalPos;
            int p3 = (significantDigits - 1) + 4;
            while (p3 > 4 && striped[p3] == '0') {
                p3--;
            }
            int i2 = ((400 - decimalPos) - (sciexp % 4)) % 4;
            int q2 = 4 - i2;
            int decimalPos2 = decimalPos + i2;
            while (p3 - q2 < this.mant.length * 4) {
                for (int i3 = 0; i3 < 4; i3++) {
                    p3++;
                    striped[p3] = '0';
                }
            }
            for (int i4 = this.mant.length - 1; i4 >= 0; i4--) {
                this.mant[i4] = ((striped[q2] - '0') * EmpiricalDistribution.DEFAULT_BIN_COUNT) + ((striped[q2 + 1] - '0') * 100) + ((striped[q2 + 2] - '0') * 10) + (striped[q2 + 3] - '0');
                q2 += 4;
            }
            this.exp = (decimalPos2 + sciexp) / 4;
            if (q2 < striped.length) {
                round((striped[q2] - '0') * EmpiricalDistribution.DEFAULT_BIN_COUNT);
            }
        }
    }

    protected Dfp(DfpField field2, byte sign2, byte nans2) {
        this.field = field2;
        this.mant = new int[field2.getRadixDigits()];
        this.sign = sign2;
        this.exp = 0;
        this.nans = nans2;
    }

    public Dfp newInstance() {
        return new Dfp(getField());
    }

    public Dfp newInstance(byte x) {
        return new Dfp(getField(), x);
    }

    public Dfp newInstance(int x) {
        return new Dfp(getField(), x);
    }

    public Dfp newInstance(long x) {
        return new Dfp(getField(), x);
    }

    public Dfp newInstance(double x) {
        return new Dfp(getField(), x);
    }

    public Dfp newInstance(Dfp d) {
        if (this.field.getRadixDigits() == d.field.getRadixDigits()) {
            return new Dfp(d);
        }
        this.field.setIEEEFlagsBits(1);
        Dfp result = newInstance(getZero());
        result.nans = 3;
        return dotrap(1, NEW_INSTANCE_TRAP, d, result);
    }

    public Dfp newInstance(String s) {
        return new Dfp(this.field, s);
    }

    public Dfp newInstance(byte sig, byte code) {
        return this.field.newDfp(sig, code);
    }

    @Override // org.apache.commons.math3.FieldElement
    public DfpField getField() {
        return this.field;
    }

    public int getRadixDigits() {
        return this.field.getRadixDigits();
    }

    public Dfp getZero() {
        return this.field.getZero();
    }

    public Dfp getOne() {
        return this.field.getOne();
    }

    public Dfp getTwo() {
        return this.field.getTwo();
    }

    /* access modifiers changed from: protected */
    public void shiftLeft() {
        for (int i = this.mant.length - 1; i > 0; i--) {
            this.mant[i] = this.mant[i - 1];
        }
        this.mant[0] = 0;
        this.exp--;
    }

    /* access modifiers changed from: protected */
    public void shiftRight() {
        for (int i = 0; i < this.mant.length - 1; i++) {
            this.mant[i] = this.mant[i + 1];
        }
        this.mant[this.mant.length - 1] = 0;
        this.exp++;
    }

    /* access modifiers changed from: protected */
    public int align(int e) {
        int lostdigit = 0;
        boolean inexact = false;
        int diff = this.exp - e;
        int adiff = diff;
        if (adiff < 0) {
            adiff = -adiff;
        }
        if (diff == 0) {
            return 0;
        }
        if (adiff > this.mant.length + 1) {
            Arrays.fill(this.mant, 0);
            this.exp = e;
            this.field.setIEEEFlagsBits(16);
            dotrap(16, ALIGN_TRAP, this, this);
            return 0;
        }
        for (int i = 0; i < adiff; i++) {
            if (diff < 0) {
                if (lostdigit != 0) {
                    inexact = true;
                }
                lostdigit = this.mant[0];
                shiftRight();
            } else {
                shiftLeft();
            }
        }
        if (inexact) {
            this.field.setIEEEFlagsBits(16);
            dotrap(16, ALIGN_TRAP, this, this);
        }
        return lostdigit;
    }

    public boolean lessThan(Dfp x) {
        boolean z = true;
        if (this.field.getRadixDigits() != x.field.getRadixDigits()) {
            this.field.setIEEEFlagsBits(1);
            Dfp result = newInstance(getZero());
            result.nans = 3;
            dotrap(1, LESS_THAN_TRAP, x, result);
            return false;
        } else if (isNaN() || x.isNaN()) {
            this.field.setIEEEFlagsBits(1);
            dotrap(1, LESS_THAN_TRAP, x, newInstance(getZero()));
            return false;
        } else {
            if (compare(this, x) >= 0) {
                z = false;
            }
            return z;
        }
    }

    public boolean greaterThan(Dfp x) {
        boolean z = true;
        if (this.field.getRadixDigits() != x.field.getRadixDigits()) {
            this.field.setIEEEFlagsBits(1);
            Dfp result = newInstance(getZero());
            result.nans = 3;
            dotrap(1, GREATER_THAN_TRAP, x, result);
            return false;
        } else if (isNaN() || x.isNaN()) {
            this.field.setIEEEFlagsBits(1);
            dotrap(1, GREATER_THAN_TRAP, x, newInstance(getZero()));
            return false;
        } else {
            if (compare(this, x) <= 0) {
                z = false;
            }
            return z;
        }
    }

    public boolean negativeOrNull() {
        if (!isNaN()) {
            return this.sign < 0 || (this.mant[this.mant.length + -1] == 0 && !isInfinite());
        }
        this.field.setIEEEFlagsBits(1);
        dotrap(1, LESS_THAN_TRAP, this, newInstance(getZero()));
        return false;
    }

    public boolean strictlyNegative() {
        boolean z = true;
        if (isNaN()) {
            this.field.setIEEEFlagsBits(1);
            dotrap(1, LESS_THAN_TRAP, this, newInstance(getZero()));
            return false;
        }
        if (this.sign >= 0 || (this.mant[this.mant.length - 1] == 0 && !isInfinite())) {
            z = false;
        }
        return z;
    }

    public boolean positiveOrNull() {
        if (!isNaN()) {
            return this.sign > 0 || (this.mant[this.mant.length + -1] == 0 && !isInfinite());
        }
        this.field.setIEEEFlagsBits(1);
        dotrap(1, LESS_THAN_TRAP, this, newInstance(getZero()));
        return false;
    }

    public boolean strictlyPositive() {
        boolean z = true;
        if (isNaN()) {
            this.field.setIEEEFlagsBits(1);
            dotrap(1, LESS_THAN_TRAP, this, newInstance(getZero()));
            return false;
        }
        if (this.sign <= 0 || (this.mant[this.mant.length - 1] == 0 && !isInfinite())) {
            z = false;
        }
        return z;
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp abs() {
        Dfp result = newInstance(this);
        result.sign = 1;
        return result;
    }

    public boolean isInfinite() {
        return this.nans == 1;
    }

    public boolean isNaN() {
        return this.nans == 3 || this.nans == 2;
    }

    public boolean isZero() {
        boolean z = true;
        if (isNaN()) {
            this.field.setIEEEFlagsBits(1);
            dotrap(1, LESS_THAN_TRAP, this, newInstance(getZero()));
            return false;
        }
        if (this.mant[this.mant.length - 1] != 0 || isInfinite()) {
            z = false;
        }
        return z;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Dfp)) {
            return false;
        }
        Dfp x = (Dfp) other;
        if (isNaN() || x.isNaN() || this.field.getRadixDigits() != x.field.getRadixDigits() || compare(this, x) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (isZero() ? 0 : this.sign << 8) + 17 + (this.nans << 16) + this.exp + Arrays.hashCode(this.mant);
    }

    public boolean unequal(Dfp x) {
        if (isNaN() || x.isNaN() || this.field.getRadixDigits() != x.field.getRadixDigits()) {
            return false;
        }
        if (greaterThan(x) || lessThan(x)) {
            return true;
        }
        return false;
    }

    private static int compare(Dfp a, Dfp b) {
        if (a.mant[a.mant.length - 1] == 0 && b.mant[b.mant.length - 1] == 0 && a.nans == 0 && b.nans == 0) {
            return 0;
        }
        if (a.sign != b.sign) {
            return a.sign == -1 ? -1 : 1;
        }
        if (a.nans == 1 && b.nans == 0) {
            return a.sign;
        }
        if (a.nans == 0 && b.nans == 1) {
            return -b.sign;
        }
        if (a.nans == 1 && b.nans == 1) {
            return 0;
        }
        if (!(b.mant[b.mant.length - 1] == 0 || a.mant[b.mant.length - 1] == 0)) {
            if (a.exp < b.exp) {
                return -a.sign;
            }
            if (a.exp > b.exp) {
                return a.sign;
            }
        }
        for (int i = a.mant.length - 1; i >= 0; i--) {
            if (a.mant[i] > b.mant[i]) {
                return a.sign;
            }
            if (a.mant[i] < b.mant[i]) {
                return -a.sign;
            }
        }
        return 0;
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp rint() {
        return trunc(DfpField.RoundingMode.ROUND_HALF_EVEN);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp floor() {
        return trunc(DfpField.RoundingMode.ROUND_FLOOR);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp ceil() {
        return trunc(DfpField.RoundingMode.ROUND_CEIL);
    }

    public Dfp remainder(Dfp d) {
        Dfp result = subtract(divide(d).rint().multiply(d));
        if (result.mant[this.mant.length - 1] == 0) {
            result.sign = this.sign;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public Dfp trunc(DfpField.RoundingMode rmode) {
        boolean changed = false;
        if (isNaN()) {
            return newInstance(this);
        }
        if (this.nans == 1) {
            return newInstance(this);
        }
        if (this.mant[this.mant.length - 1] == 0) {
            return newInstance(this);
        }
        if (this.exp < 0) {
            this.field.setIEEEFlagsBits(16);
            return dotrap(16, TRUNC_TRAP, this, newInstance(getZero()));
        } else if (this.exp >= this.mant.length) {
            return newInstance(this);
        } else {
            Dfp result = newInstance(this);
            for (int i = 0; i < this.mant.length - result.exp; i++) {
                changed |= result.mant[i] != 0;
                result.mant[i] = 0;
            }
            if (!changed) {
                return result;
            }
            switch (rmode) {
                case ROUND_FLOOR:
                    if (result.sign == -1) {
                        result = result.add(newInstance(-1));
                        break;
                    }
                    break;
                case ROUND_CEIL:
                    if (result.sign == 1) {
                        result = result.add(getOne());
                        break;
                    }
                    break;
                default:
                    Dfp half = newInstance("0.5");
                    Dfp a = subtract(result);
                    a.sign = 1;
                    if (a.greaterThan(half)) {
                        a = newInstance(getOne());
                        a.sign = this.sign;
                        result = result.add(a);
                    }
                    if (a.equals(half) && result.exp > 0 && (result.mant[this.mant.length - result.exp] & 1) != 0) {
                        Dfp a2 = newInstance(getOne());
                        a2.sign = this.sign;
                        result = result.add(a2);
                        break;
                    }
            }
            this.field.setIEEEFlagsBits(16);
            return dotrap(16, TRUNC_TRAP, this, result);
        }
    }

    public int intValue() {
        int result = 0;
        Dfp rounded = rint();
        if (rounded.greaterThan(newInstance(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT))) {
            return BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT;
        }
        if (rounded.lessThan(newInstance(Integer.MIN_VALUE))) {
            return Integer.MIN_VALUE;
        }
        for (int i = this.mant.length - 1; i >= this.mant.length - rounded.exp; i--) {
            result = (result * RADIX) + rounded.mant[i];
        }
        if (rounded.sign == -1) {
            result = -result;
        }
        return result;
    }

    public int log10K() {
        return this.exp - 1;
    }

    public Dfp power10K(int e) {
        Dfp d = newInstance(getOne());
        d.exp = e + 1;
        return d;
    }

    public int intLog10() {
        if (this.mant[this.mant.length - 1] > 1000) {
            return (this.exp * 4) - 1;
        }
        if (this.mant[this.mant.length - 1] > 100) {
            return (this.exp * 4) - 2;
        }
        if (this.mant[this.mant.length - 1] > 10) {
            return (this.exp * 4) - 3;
        }
        return (this.exp * 4) - 4;
    }

    public Dfp power10(int e) {
        Dfp d = newInstance(getOne());
        if (e >= 0) {
            d.exp = (e / 4) + 1;
        } else {
            d.exp = (e + 1) / 4;
        }
        switch (((e % 4) + 4) % 4) {
            case 0:
                return d;
            case 1:
                return d.multiply(10);
            case 2:
                return d.multiply(100);
            default:
                return d.multiply(EmpiricalDistribution.DEFAULT_BIN_COUNT);
        }
    }

    /* access modifiers changed from: protected */
    public int complement(int extra) {
        int extra2 = 10000 - extra;
        for (int i = 0; i < this.mant.length; i++) {
            this.mant[i] = (10000 - this.mant[i]) - 1;
        }
        int rh = extra2 / RADIX;
        int extra3 = extra2 - (rh * RADIX);
        for (int i2 = 0; i2 < this.mant.length; i2++) {
            int r = this.mant[i2] + rh;
            rh = r / RADIX;
            this.mant[i2] = r - (rh * RADIX);
        }
        return extra3;
    }

    public Dfp add(Dfp x) {
        if (this.field.getRadixDigits() != x.field.getRadixDigits()) {
            this.field.setIEEEFlagsBits(1);
            Dfp result = newInstance(getZero());
            result.nans = 3;
            return dotrap(1, ADD_TRAP, x, result);
        }
        if (!(this.nans == 0 && x.nans == 0)) {
            if (isNaN()) {
                return this;
            }
            if (x.isNaN()) {
                return x;
            }
            if (this.nans == 1 && x.nans == 0) {
                return this;
            }
            if (x.nans == 1 && this.nans == 0) {
                return x;
            }
            if (x.nans == 1 && this.nans == 1 && this.sign == x.sign) {
                return x;
            }
            if (x.nans == 1 && this.nans == 1 && this.sign != x.sign) {
                this.field.setIEEEFlagsBits(1);
                Dfp result2 = newInstance(getZero());
                result2.nans = 3;
                return dotrap(1, ADD_TRAP, x, result2);
            }
        }
        Dfp a = newInstance(this);
        Dfp b = newInstance(x);
        Dfp result3 = newInstance(getZero());
        byte asign = a.sign;
        byte bsign = b.sign;
        a.sign = 1;
        b.sign = 1;
        byte rsign = bsign;
        if (compare(a, b) > 0) {
            rsign = asign;
        }
        if (b.mant[this.mant.length - 1] == 0) {
            b.exp = a.exp;
        }
        if (a.mant[this.mant.length - 1] == 0) {
            a.exp = b.exp;
        }
        int aextradigit = 0;
        int bextradigit = 0;
        if (a.exp < b.exp) {
            aextradigit = a.align(b.exp);
        } else {
            bextradigit = b.align(a.exp);
        }
        if (asign != bsign) {
            if (asign == rsign) {
                bextradigit = b.complement(bextradigit);
            } else {
                aextradigit = a.complement(aextradigit);
            }
        }
        int rh = 0;
        for (int i = 0; i < this.mant.length; i++) {
            int r = a.mant[i] + b.mant[i] + rh;
            rh = r / RADIX;
            result3.mant[i] = r - (rh * RADIX);
        }
        result3.exp = a.exp;
        result3.sign = rsign;
        if (rh != 0 && asign == bsign) {
            int lostdigit = result3.mant[0];
            result3.shiftRight();
            result3.mant[this.mant.length - 1] = rh;
            int excp = result3.round(lostdigit);
            if (excp != 0) {
                result3 = dotrap(excp, ADD_TRAP, x, result3);
            }
        }
        for (int i2 = 0; i2 < this.mant.length && result3.mant[this.mant.length - 1] == 0; i2++) {
            result3.shiftLeft();
            if (i2 == 0) {
                result3.mant[0] = aextradigit + bextradigit;
                aextradigit = 0;
                bextradigit = 0;
            }
        }
        if (result3.mant[this.mant.length - 1] == 0) {
            result3.exp = 0;
            if (asign != bsign) {
                result3.sign = 1;
            }
        }
        int excp2 = result3.round(aextradigit + bextradigit);
        if (excp2 != 0) {
            result3 = dotrap(excp2, ADD_TRAP, x, result3);
        }
        return result3;
    }

    @Override // org.apache.commons.math3.FieldElement
    public Dfp negate() {
        Dfp result = newInstance(this);
        result.sign = (byte) (-result.sign);
        return result;
    }

    public Dfp subtract(Dfp x) {
        return add(x.negate());
    }

    /* access modifiers changed from: protected */
    public int round(int n) {
        boolean inc;
        switch (C02181.$SwitchMap$org$apache$commons$math3$dfp$DfpField$RoundingMode[this.field.getRoundingMode().ordinal()]) {
            case 2:
                if (this.sign != 1 || n == 0) {
                    inc = false;
                    break;
                } else {
                    inc = true;
                    break;
                }
            case 3:
                if (n > 5000 || (n == 5000 && (this.mant[0] & 1) == 1)) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case 4:
                inc = false;
                break;
            case 5:
                if (n != 0) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case 6:
                if (n >= 5000) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case CatalogManager.CATALOG_ID_SATELLITE:
                if (n > 5000) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            case 8:
                if (n > 5000 || (n == 5000 && (this.mant[0] & 1) == 0)) {
                    inc = true;
                    break;
                } else {
                    inc = false;
                    break;
                }
            default:
                if (this.sign != -1 || n == 0) {
                    inc = false;
                    break;
                } else {
                    inc = true;
                    break;
                }
        }
        if (inc) {
            int rh = 1;
            for (int i = 0; i < this.mant.length; i++) {
                int r = this.mant[i] + rh;
                rh = r / RADIX;
                this.mant[i] = r - (rh * RADIX);
            }
            if (rh != 0) {
                shiftRight();
                this.mant[this.mant.length - 1] = rh;
            }
        }
        if (this.exp < -32767) {
            this.field.setIEEEFlagsBits(8);
            return 8;
        } else if (this.exp > 32768) {
            this.field.setIEEEFlagsBits(4);
            return 4;
        } else if (n == 0) {
            return 0;
        } else {
            this.field.setIEEEFlagsBits(16);
            return 16;
        }
    }

    public Dfp multiply(Dfp x) {
        int excp;
        int i = 1;
        if (this.field.getRadixDigits() != x.field.getRadixDigits()) {
            this.field.setIEEEFlagsBits(1);
            Dfp result = newInstance(getZero());
            result.nans = 3;
            return dotrap(1, MULTIPLY_TRAP, x, result);
        }
        Dfp result2 = newInstance(getZero());
        if (!(this.nans == 0 && x.nans == 0)) {
            if (isNaN()) {
                return this;
            }
            if (x.isNaN()) {
                return x;
            }
            if (this.nans == 1 && x.nans == 0 && x.mant[this.mant.length - 1] != 0) {
                Dfp result3 = newInstance(this);
                result3.sign = (byte) (this.sign * x.sign);
                return result3;
            } else if (x.nans == 1 && this.nans == 0 && this.mant[this.mant.length - 1] != 0) {
                Dfp result4 = newInstance(x);
                result4.sign = (byte) (this.sign * x.sign);
                return result4;
            } else if (x.nans == 1 && this.nans == 1) {
                Dfp result5 = newInstance(this);
                result5.sign = (byte) (this.sign * x.sign);
                return result5;
            } else if ((x.nans == 1 && this.nans == 0 && this.mant[this.mant.length - 1] == 0) || (this.nans == 1 && x.nans == 0 && x.mant[this.mant.length - 1] == 0)) {
                this.field.setIEEEFlagsBits(1);
                Dfp result6 = newInstance(getZero());
                result6.nans = 3;
                return dotrap(1, MULTIPLY_TRAP, x, result6);
            }
        }
        int[] product = new int[(this.mant.length * 2)];
        for (int i2 = 0; i2 < this.mant.length; i2++) {
            int rh = 0;
            for (int j = 0; j < this.mant.length; j++) {
                int r = (this.mant[i2] * x.mant[j]) + product[i2 + j] + rh;
                rh = r / RADIX;
                product[i2 + j] = r - (rh * RADIX);
            }
            product[this.mant.length + i2] = rh;
        }
        int md = (this.mant.length * 2) - 1;
        int i3 = (this.mant.length * 2) - 1;
        while (true) {
            if (i3 < 0) {
                break;
            } else if (product[i3] != 0) {
                md = i3;
                break;
            } else {
                i3--;
            }
        }
        for (int i4 = 0; i4 < this.mant.length; i4++) {
            result2.mant[(this.mant.length - i4) - 1] = product[md - i4];
        }
        result2.exp = (((this.exp + x.exp) + md) - (this.mant.length * 2)) + 1;
        if (this.sign != x.sign) {
            i = -1;
        }
        result2.sign = (byte) i;
        if (result2.mant[this.mant.length - 1] == 0) {
            result2.exp = 0;
        }
        if (md > this.mant.length - 1) {
            excp = result2.round(product[md - this.mant.length]);
        } else {
            excp = result2.round(0);
        }
        if (excp != 0) {
            result2 = dotrap(excp, MULTIPLY_TRAP, x, result2);
        }
        return result2;
    }

    @Override // org.apache.commons.math3.FieldElement
    public Dfp multiply(int x) {
        if (x < 0 || x >= 10000) {
            return multiply(newInstance(x));
        }
        return multiplyFast(x);
    }

    private Dfp multiplyFast(int x) {
        Dfp result = newInstance(this);
        if (this.nans != 0) {
            if (isNaN()) {
                return this;
            }
            if (this.nans == 1 && x != 0) {
                return newInstance(this);
            }
            if (this.nans == 1 && x == 0) {
                this.field.setIEEEFlagsBits(1);
                Dfp result2 = newInstance(getZero());
                result2.nans = 3;
                return dotrap(1, MULTIPLY_TRAP, newInstance(getZero()), result2);
            }
        }
        if (x < 0 || x >= 10000) {
            this.field.setIEEEFlagsBits(1);
            Dfp result3 = newInstance(getZero());
            result3.nans = 3;
            return dotrap(1, MULTIPLY_TRAP, result3, result3);
        }
        int rh = 0;
        for (int i = 0; i < this.mant.length; i++) {
            int r = (this.mant[i] * x) + rh;
            rh = r / RADIX;
            result.mant[i] = r - (rh * RADIX);
        }
        int lostdigit = 0;
        if (rh != 0) {
            lostdigit = result.mant[0];
            result.shiftRight();
            result.mant[this.mant.length - 1] = rh;
        }
        if (result.mant[this.mant.length - 1] == 0) {
            result.exp = 0;
        }
        int excp = result.round(lostdigit);
        if (excp != 0) {
            result = dotrap(excp, MULTIPLY_TRAP, result, result);
        }
        return result;
    }

    public Dfp divide(Dfp divisor) {
        int excp;
        int trial = 0;
        if (this.field.getRadixDigits() != divisor.field.getRadixDigits()) {
            this.field.setIEEEFlagsBits(1);
            Dfp result = newInstance(getZero());
            result.nans = 3;
            return dotrap(1, DIVIDE_TRAP, divisor, result);
        }
        Dfp result2 = newInstance(getZero());
        if (!(this.nans == 0 && divisor.nans == 0)) {
            if (isNaN()) {
                return this;
            }
            if (divisor.isNaN()) {
                return divisor;
            }
            if (this.nans == 1 && divisor.nans == 0) {
                Dfp result3 = newInstance(this);
                result3.sign = (byte) (this.sign * divisor.sign);
                return result3;
            } else if (divisor.nans == 1 && this.nans == 0) {
                Dfp result4 = newInstance(getZero());
                result4.sign = (byte) (this.sign * divisor.sign);
                return result4;
            } else if (divisor.nans == 1 && this.nans == 1) {
                this.field.setIEEEFlagsBits(1);
                Dfp result5 = newInstance(getZero());
                result5.nans = 3;
                return dotrap(1, DIVIDE_TRAP, divisor, result5);
            }
        }
        if (divisor.mant[this.mant.length - 1] == 0) {
            this.field.setIEEEFlagsBits(2);
            Dfp result6 = newInstance(getZero());
            result6.sign = (byte) (this.sign * divisor.sign);
            result6.nans = 1;
            return dotrap(2, DIVIDE_TRAP, divisor, result6);
        }
        int[] dividend = new int[(this.mant.length + 1)];
        int[] quotient = new int[(this.mant.length + 2)];
        int[] remainder = new int[(this.mant.length + 1)];
        dividend[this.mant.length] = 0;
        quotient[this.mant.length] = 0;
        quotient[this.mant.length + 1] = 0;
        remainder[this.mant.length] = 0;
        for (int i = 0; i < this.mant.length; i++) {
            dividend[i] = this.mant[i];
            quotient[i] = 0;
            remainder[i] = 0;
        }
        int nsqd = 0;
        for (int qd = this.mant.length + 1; qd >= 0; qd--) {
            int divMsb = (dividend[this.mant.length] * RADIX) + dividend[this.mant.length - 1];
            int min = divMsb / (divisor.mant[this.mant.length - 1] + 1);
            int max = (divMsb + 1) / divisor.mant[this.mant.length - 1];
            boolean trialgood = false;
            while (!trialgood) {
                trial = (min + max) / 2;
                int rh = 0;
                int i2 = 0;
                while (i2 < this.mant.length + 1) {
                    int r = ((i2 < this.mant.length ? divisor.mant[i2] : 0) * trial) + rh;
                    rh = r / RADIX;
                    remainder[i2] = r - (rh * RADIX);
                    i2++;
                }
                int rh2 = 1;
                for (int i3 = 0; i3 < this.mant.length + 1; i3++) {
                    int r2 = (9999 - remainder[i3]) + dividend[i3] + rh2;
                    rh2 = r2 / RADIX;
                    remainder[i3] = r2 - (rh2 * RADIX);
                }
                if (rh2 == 0) {
                    max = trial - 1;
                } else {
                    int minadj = ((remainder[this.mant.length] * RADIX) + remainder[this.mant.length - 1]) / (divisor.mant[this.mant.length - 1] + 1);
                    if (minadj >= 2) {
                        min = trial + minadj;
                    } else {
                        trialgood = false;
                        for (int i4 = this.mant.length - 1; i4 >= 0; i4--) {
                            if (divisor.mant[i4] > remainder[i4]) {
                                trialgood = true;
                            }
                            if (divisor.mant[i4] < remainder[i4]) {
                                break;
                            }
                        }
                        if (remainder[this.mant.length] != 0) {
                            trialgood = false;
                        }
                        if (!trialgood) {
                            min = trial + 1;
                        }
                    }
                }
            }
            quotient[qd] = trial;
            if (!(trial == 0 && nsqd == 0)) {
                nsqd++;
            }
            if ((this.field.getRoundingMode() == DfpField.RoundingMode.ROUND_DOWN && nsqd == this.mant.length) || nsqd > this.mant.length) {
                break;
            }
            dividend[0] = 0;
            for (int i5 = 0; i5 < this.mant.length; i5++) {
                dividend[i5 + 1] = remainder[i5];
            }
        }
        int md = this.mant.length;
        int i6 = this.mant.length + 1;
        while (true) {
            if (i6 < 0) {
                break;
            } else if (quotient[i6] != 0) {
                md = i6;
                break;
            } else {
                i6--;
            }
        }
        for (int i7 = 0; i7 < this.mant.length; i7++) {
            result2.mant[(this.mant.length - i7) - 1] = quotient[md - i7];
        }
        result2.exp = ((this.exp - divisor.exp) + md) - this.mant.length;
        result2.sign = (byte) (this.sign == divisor.sign ? 1 : -1);
        if (result2.mant[this.mant.length - 1] == 0) {
            result2.exp = 0;
        }
        if (md > this.mant.length - 1) {
            excp = result2.round(quotient[md - this.mant.length]);
        } else {
            excp = result2.round(0);
        }
        if (excp != 0) {
            result2 = dotrap(excp, DIVIDE_TRAP, divisor, result2);
        }
        return result2;
    }

    public Dfp divide(int divisor) {
        if (this.nans != 0) {
            if (isNaN()) {
                return this;
            }
            if (this.nans == 1) {
                return newInstance(this);
            }
        }
        if (divisor == 0) {
            this.field.setIEEEFlagsBits(2);
            Dfp result = newInstance(getZero());
            result.sign = this.sign;
            result.nans = 1;
            return dotrap(2, DIVIDE_TRAP, getZero(), result);
        } else if (divisor < 0 || divisor >= 10000) {
            this.field.setIEEEFlagsBits(1);
            Dfp result2 = newInstance(getZero());
            result2.nans = 3;
            return dotrap(1, DIVIDE_TRAP, result2, result2);
        } else {
            Dfp result3 = newInstance(this);
            int rl = 0;
            for (int i = this.mant.length - 1; i >= 0; i--) {
                int r = (rl * RADIX) + result3.mant[i];
                int rh = r / divisor;
                rl = r - (rh * divisor);
                result3.mant[i] = rh;
            }
            if (result3.mant[this.mant.length - 1] == 0) {
                result3.shiftLeft();
                int r2 = rl * RADIX;
                int rh2 = r2 / divisor;
                rl = r2 - (rh2 * divisor);
                result3.mant[0] = rh2;
            }
            int excp = result3.round((rl * RADIX) / divisor);
            if (excp != 0) {
                return dotrap(excp, DIVIDE_TRAP, result3, result3);
            }
            return result3;
        }
    }

    @Override // org.apache.commons.math3.FieldElement, org.apache.commons.math3.RealFieldElement
    public Dfp reciprocal() {
        return this.field.getOne().divide(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp sqrt() {
        if (this.nans == 0 && this.mant[this.mant.length - 1] == 0) {
            return newInstance(this);
        }
        if (this.nans != 0) {
            if (this.nans == 1 && this.sign == 1) {
                return newInstance(this);
            }
            if (this.nans == 3) {
                return newInstance(this);
            }
            if (this.nans == 2) {
                this.field.setIEEEFlagsBits(1);
                return dotrap(1, SQRT_TRAP, null, newInstance(this));
            }
        }
        if (this.sign == -1) {
            this.field.setIEEEFlagsBits(1);
            Dfp result = newInstance(this);
            result.nans = 3;
            return dotrap(1, SQRT_TRAP, null, result);
        }
        Dfp x = newInstance(this);
        if (x.exp < -1 || x.exp > 1) {
            x.exp = this.exp / 2;
        }
        switch (x.mant[this.mant.length - 1] / MicrosphereInterpolator.DEFAULT_MICROSPHERE_ELEMENTS) {
            case 0:
                x.mant[this.mant.length - 1] = (x.mant[this.mant.length - 1] / 2) + 1;
                break;
            case 1:
            default:
                x.mant[this.mant.length - 1] = 3000;
                break;
            case 2:
                x.mant[this.mant.length - 1] = 1500;
                break;
            case 3:
                x.mant[this.mant.length - 1] = 2200;
                break;
        }
        newInstance(x);
        Dfp px = getZero();
        getZero();
        while (x.unequal(px)) {
            Dfp dx = newInstance(x);
            dx.sign = -1;
            Dfp dx2 = dx.add(divide(x)).divide(2);
            px = x;
            x = x.add(dx2);
            if (!x.equals(px)) {
                if (dx2.mant[this.mant.length - 1] == 0) {
                }
            }
            return x;
        }
        return x;
    }

    public String toString() {
        if (this.nans != 0) {
            if (this.nans == 1) {
                return this.sign < 0 ? NEG_INFINITY_STRING : POS_INFINITY_STRING;
            }
            return NAN_STRING;
        } else if (this.exp > this.mant.length || this.exp < -1) {
            return dfp2sci();
        } else {
            return dfp2string();
        }
    }

    /* access modifiers changed from: protected */
    public String dfp2sci() {
        char[] rawdigits = new char[(this.mant.length * 4)];
        char[] outputbuffer = new char[((this.mant.length * 4) + 20)];
        int p = 0;
        for (int i = this.mant.length - 1; i >= 0; i--) {
            int p2 = p + 1;
            rawdigits[p] = (char) ((this.mant[i] / EmpiricalDistribution.DEFAULT_BIN_COUNT) + 48);
            int p3 = p2 + 1;
            rawdigits[p2] = (char) (((this.mant[i] / 100) % 10) + 48);
            int p4 = p3 + 1;
            rawdigits[p3] = (char) (((this.mant[i] / 10) % 10) + 48);
            p = p4 + 1;
            rawdigits[p4] = (char) ((this.mant[i] % 10) + 48);
        }
        int p5 = 0;
        while (p5 < rawdigits.length && rawdigits[p5] == '0') {
            p5++;
        }
        int q = 0;
        if (this.sign == -1) {
            outputbuffer[0] = '-';
            q = 0 + 1;
        }
        if (p5 != rawdigits.length) {
            int q2 = q + 1;
            int p6 = p5 + 1;
            outputbuffer[q] = rawdigits[p5];
            int q3 = q2 + 1;
            outputbuffer[q2] = '.';
            while (p6 < rawdigits.length) {
                p6++;
                outputbuffer[q3] = rawdigits[p6];
                q3++;
            }
            int q4 = q3 + 1;
            outputbuffer[q3] = 'e';
            int e = ((this.exp * 4) - p5) - 1;
            int ae = e;
            if (e < 0) {
                ae = -e;
            }
            int p7 = 1000000000;
            while (p7 > ae) {
                p7 /= 10;
            }
            if (e < 0) {
                outputbuffer[q4] = '-';
                q4++;
            }
            while (p7 > 0) {
                outputbuffer[q4] = (char) ((ae / p7) + 48);
                ae %= p7;
                p7 /= 10;
                q4++;
            }
            return new String(outputbuffer, 0, q4);
        }
        int q5 = q + 1;
        outputbuffer[q] = '0';
        int q6 = q5 + 1;
        outputbuffer[q5] = '.';
        int q7 = q6 + 1;
        outputbuffer[q6] = '0';
        int q8 = q7 + 1;
        outputbuffer[q7] = 'e';
        int i2 = q8 + 1;
        outputbuffer[q8] = '0';
        return new String(outputbuffer, 0, 5);
    }

    /* access modifiers changed from: protected */
    public String dfp2string() {
        int p;
        int p2;
        int p3;
        char[] buffer = new char[((this.mant.length * 4) + 20)];
        int e = this.exp;
        boolean pointInserted = false;
        buffer[0] = ' ';
        if (e <= 0) {
            int p4 = 1 + 1;
            buffer[1] = '0';
            buffer[p4] = '.';
            pointInserted = true;
            p = p4 + 1;
        } else {
            p = 1;
        }
        while (e < 0) {
            int p5 = p + 1;
            buffer[p] = '0';
            int p6 = p5 + 1;
            buffer[p5] = '0';
            int p7 = p6 + 1;
            buffer[p6] = '0';
            p = p7 + 1;
            buffer[p7] = '0';
            e++;
        }
        int i = this.mant.length - 1;
        while (i >= 0) {
            int p8 = p + 1;
            buffer[p] = (char) ((this.mant[i] / EmpiricalDistribution.DEFAULT_BIN_COUNT) + 48);
            int p9 = p8 + 1;
            buffer[p8] = (char) (((this.mant[i] / 100) % 10) + 48);
            int p10 = p9 + 1;
            buffer[p9] = (char) (((this.mant[i] / 10) % 10) + 48);
            int p11 = p10 + 1;
            buffer[p10] = (char) ((this.mant[i] % 10) + 48);
            e--;
            if (e == 0) {
                p3 = p11 + 1;
                buffer[p11] = '.';
                pointInserted = true;
            } else {
                p3 = p11;
            }
            i--;
            p = p3;
        }
        while (e > 0) {
            int p12 = p + 1;
            buffer[p] = '0';
            int p13 = p12 + 1;
            buffer[p12] = '0';
            int p14 = p13 + 1;
            buffer[p13] = '0';
            p = p14 + 1;
            buffer[p14] = '0';
            e--;
        }
        if (!pointInserted) {
            p2 = p + 1;
            buffer[p] = '.';
        } else {
            p2 = p;
        }
        int q = 1;
        while (buffer[q] == '0') {
            q++;
        }
        if (buffer[q] == '.') {
            q--;
        }
        while (buffer[p2 - 1] == '0') {
            p2--;
        }
        if (this.sign < 0) {
            q--;
            buffer[q] = '-';
        }
        return new String(buffer, q, p2 - q);
    }

    public Dfp dotrap(int type, String what, Dfp oper, Dfp result) {
        Dfp def = result;
        switch (type) {
            case 1:
                def = newInstance(getZero());
                def.sign = result.sign;
                def.nans = 3;
                break;
            case 2:
                if (this.nans == 0 && this.mant[this.mant.length - 1] != 0) {
                    def = newInstance(getZero());
                    def.sign = (byte) (this.sign * oper.sign);
                    def.nans = 1;
                }
                if (this.nans == 0 && this.mant[this.mant.length - 1] == 0) {
                    def = newInstance(getZero());
                    def.nans = 3;
                }
                if (this.nans == 1 || this.nans == 3) {
                    def = newInstance(getZero());
                    def.nans = 3;
                }
                if (this.nans == 1 || this.nans == 2) {
                    def = newInstance(getZero());
                    def.nans = 3;
                    break;
                }
            case 3:
            case 5:
            case 6:
            case CatalogManager.CATALOG_ID_SATELLITE:
            default:
                def = result;
                break;
            case 4:
                result.exp -= 32760;
                def = newInstance(getZero());
                def.sign = result.sign;
                def.nans = 1;
                break;
            case 8:
                if (result.exp + this.mant.length < -32767) {
                    def = newInstance(getZero());
                    def.sign = result.sign;
                } else {
                    def = newInstance(result);
                }
                result.exp += ERR_SCALE;
                break;
        }
        return trap(type, what, oper, def, result);
    }

    /* access modifiers changed from: protected */
    public Dfp trap(int type, String what, Dfp oper, Dfp def, Dfp result) {
        return def;
    }

    public int classify() {
        return this.nans;
    }

    public static Dfp copysign(Dfp x, Dfp y) {
        Dfp result = x.newInstance(x);
        result.sign = y.sign;
        return result;
    }

    public Dfp nextAfter(Dfp x) {
        Dfp result;
        if (this.field.getRadixDigits() != x.field.getRadixDigits()) {
            this.field.setIEEEFlagsBits(1);
            Dfp result2 = newInstance(getZero());
            result2.nans = 3;
            return dotrap(1, NEXT_AFTER_TRAP, x, result2);
        }
        boolean up = false;
        if (lessThan(x)) {
            up = true;
        }
        if (compare(this, x) == 0) {
            return newInstance(x);
        }
        if (lessThan(getZero())) {
            up = !up;
        }
        if (up) {
            Dfp inc = newInstance(getOne());
            inc.exp = (this.exp - this.mant.length) + 1;
            inc.sign = this.sign;
            if (equals(getZero())) {
                inc.exp = -32767 - this.mant.length;
            }
            result = add(inc);
        } else {
            Dfp inc2 = newInstance(getOne());
            inc2.exp = this.exp;
            inc2.sign = this.sign;
            if (equals(inc2)) {
                inc2.exp = this.exp - this.mant.length;
            } else {
                inc2.exp = (this.exp - this.mant.length) + 1;
            }
            if (equals(getZero())) {
                inc2.exp = -32767 - this.mant.length;
            }
            result = subtract(inc2);
        }
        if (result.classify() == 1 && classify() != 1) {
            this.field.setIEEEFlagsBits(16);
            result = dotrap(16, NEXT_AFTER_TRAP, x, result);
        }
        if (!result.equals(getZero()) || equals(getZero())) {
            return result;
        }
        this.field.setIEEEFlagsBits(16);
        return dotrap(16, NEXT_AFTER_TRAP, x, result);
    }

    public double toDouble() {
        if (isInfinite()) {
            if (lessThan(getZero())) {
                return Double.NEGATIVE_INFINITY;
            }
            return Double.POSITIVE_INFINITY;
        } else if (isNaN()) {
            return Double.NaN;
        } else {
            Dfp y = this;
            boolean negate = false;
            int cmp0 = compare(this, getZero());
            if (cmp0 == 0) {
                return this.sign < 0 ? -0.0d : 0.0d;
            }
            if (cmp0 < 0) {
                y = negate();
                negate = true;
            }
            int exponent = (int) (((double) y.intLog10()) * 3.32d);
            if (exponent < 0) {
                exponent--;
            }
            Dfp tempDfp = DfpMath.pow(getTwo(), exponent);
            while (true) {
                if (!tempDfp.lessThan(y) && !tempDfp.equals(y)) {
                    break;
                }
                tempDfp = tempDfp.multiply(2);
                exponent++;
            }
            int exponent2 = exponent - 1;
            Dfp y2 = y.divide(DfpMath.pow(getTwo(), exponent2));
            if (exponent2 > -1023) {
                y2 = y2.subtract(getOne());
            }
            if (exponent2 < -1074) {
                return 0.0d;
            }
            if (exponent2 > 1023) {
                return negate ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            String str = y2.multiply(newInstance(4503599627370496L)).rint().toString();
            long mantissa = Long.parseLong(str.substring(0, str.length() - 1));
            if (mantissa == 4503599627370496L) {
                mantissa = 0;
                exponent2++;
            }
            if (exponent2 <= -1023) {
                exponent2--;
            }
            while (exponent2 < -1023) {
                exponent2++;
                mantissa >>>= 1;
            }
            double x = Double.longBitsToDouble(mantissa | ((((long) exponent2) + 1023) << 52));
            if (negate) {
                x = -x;
            }
            return x;
        }
    }

    public double[] toSplitDouble() {
        double[] split = new double[2];
        split[0] = Double.longBitsToDouble(Double.doubleToLongBits(toDouble()) & -1073741824);
        split[1] = subtract(newInstance(split[0])).toDouble();
        return split;
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public double getReal() {
        return toDouble();
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp add(double a) {
        return add(newInstance(a));
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp subtract(double a) {
        return subtract(newInstance(a));
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp multiply(double a) {
        return multiply(newInstance(a));
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp divide(double a) {
        return divide(newInstance(a));
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp remainder(double a) {
        return remainder(newInstance(a));
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public long round() {
        return FastMath.round(toDouble());
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp signum() {
        if (isNaN() || isZero()) {
            return this;
        }
        return newInstance(this.sign > 0 ? 1 : -1);
    }

    public Dfp copySign(Dfp s) {
        if (this.sign < 0 || s.sign < 0) {
            return (this.sign >= 0 || s.sign >= 0) ? negate() : this;
        }
        return this;
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp copySign(double s) {
        long sb = Double.doubleToLongBits(s);
        if (this.sign < 0 || sb < 0) {
            return (this.sign >= 0 || sb >= 0) ? negate() : this;
        }
        return this;
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp scalb(int n) {
        return multiply(DfpMath.pow(getTwo(), n));
    }

    public Dfp hypot(Dfp y) {
        return multiply(this).add(y.multiply(y)).sqrt();
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp cbrt() {
        return rootN(3);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp rootN(int n) {
        return this.sign >= 0 ? DfpMath.pow(this, getOne().divide(n)) : DfpMath.pow(negate(), getOne().divide(n)).negate();
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp pow(double p) {
        return DfpMath.pow(this, newInstance(p));
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp pow(int n) {
        return DfpMath.pow(this, n);
    }

    public Dfp pow(Dfp e) {
        return DfpMath.pow(this, e);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp exp() {
        return DfpMath.exp(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp expm1() {
        return DfpMath.exp(this).subtract(getOne());
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp log() {
        return DfpMath.log(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp log1p() {
        return DfpMath.log(add(getOne()));
    }

    @Deprecated
    public int log10() {
        return intLog10();
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp cos() {
        return DfpMath.cos(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp sin() {
        return DfpMath.sin(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp tan() {
        return DfpMath.tan(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp acos() {
        return DfpMath.acos(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp asin() {
        return DfpMath.asin(this);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp atan() {
        return DfpMath.atan(this);
    }

    public Dfp atan2(Dfp x) throws DimensionMismatchException {
        Dfp r = x.multiply(x).add(multiply(this)).sqrt();
        if (x.sign >= 0) {
            return getTwo().multiply(divide(r.add(x)).atan());
        }
        Dfp tmp = getTwo().multiply(divide(r.subtract(x)).atan());
        return newInstance(tmp.sign <= 0 ? -3.141592653589793d : 3.141592653589793d).subtract(tmp);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp cosh() {
        return DfpMath.exp(this).add(DfpMath.exp(negate())).divide(2);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp sinh() {
        return DfpMath.exp(this).subtract(DfpMath.exp(negate())).divide(2);
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp tanh() {
        Dfp ePlus = DfpMath.exp(this);
        Dfp eMinus = DfpMath.exp(negate());
        return ePlus.subtract(eMinus).divide(ePlus.add(eMinus));
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp acosh() {
        return multiply(this).subtract(getOne()).sqrt().add(this).log();
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp asinh() {
        return multiply(this).add(getOne()).sqrt().add(this).log();
    }

    @Override // org.apache.commons.math3.RealFieldElement
    public Dfp atanh() {
        return getOne().add(this).divide(getOne().subtract(this)).log().divide(2);
    }

    public Dfp linearCombination(Dfp[] a, Dfp[] b) throws DimensionMismatchException {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        Dfp r = getZero();
        for (int i = 0; i < a.length; i++) {
            r = r.add(a[i].multiply(b[i]));
        }
        return r;
    }

    public Dfp linearCombination(double[] a, Dfp[] b) throws DimensionMismatchException {
        if (a.length != b.length) {
            throw new DimensionMismatchException(a.length, b.length);
        }
        Dfp r = getZero();
        for (int i = 0; i < a.length; i++) {
            r = r.add(b[i].multiply(a[i]));
        }
        return r;
    }

    public Dfp linearCombination(Dfp a1, Dfp b1, Dfp a2, Dfp b2) {
        return a1.multiply(b1).add(a2.multiply(b2));
    }

    public Dfp linearCombination(double a1, Dfp b1, double a2, Dfp b2) {
        return b1.multiply(a1).add(b2.multiply(a2));
    }

    public Dfp linearCombination(Dfp a1, Dfp b1, Dfp a2, Dfp b2, Dfp a3, Dfp b3) {
        return a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3));
    }

    public Dfp linearCombination(double a1, Dfp b1, double a2, Dfp b2, double a3, Dfp b3) {
        return b1.multiply(a1).add(b2.multiply(a2)).add(b3.multiply(a3));
    }

    public Dfp linearCombination(Dfp a1, Dfp b1, Dfp a2, Dfp b2, Dfp a3, Dfp b3, Dfp a4, Dfp b4) {
        return a1.multiply(b1).add(a2.multiply(b2)).add(a3.multiply(b3)).add(a4.multiply(b4));
    }

    public Dfp linearCombination(double a1, Dfp b1, double a2, Dfp b2, double a3, Dfp b3, double a4, Dfp b4) {
        return b1.multiply(a1).add(b2.multiply(a2)).add(b3.multiply(a3)).add(b4.multiply(a4));
    }
}
