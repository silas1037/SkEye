package org.apache.commons.math3.fraction;

import java.io.Serializable;
import java.math.BigInteger;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;

public class Fraction extends Number implements FieldElement<Fraction>, Comparable<Fraction>, Serializable {
    private static final double DEFAULT_EPSILON = 1.0E-5d;
    public static final Fraction FOUR_FIFTHS = new Fraction(4, 5);
    public static final Fraction MINUS_ONE = new Fraction(-1, 1);
    public static final Fraction ONE = new Fraction(1, 1);
    public static final Fraction ONE_FIFTH = new Fraction(1, 5);
    public static final Fraction ONE_HALF = new Fraction(1, 2);
    public static final Fraction ONE_QUARTER = new Fraction(1, 4);
    public static final Fraction ONE_THIRD = new Fraction(1, 3);
    public static final Fraction THREE_FIFTHS = new Fraction(3, 5);
    public static final Fraction THREE_QUARTERS = new Fraction(3, 4);
    public static final Fraction TWO = new Fraction(2, 1);
    public static final Fraction TWO_FIFTHS = new Fraction(2, 5);
    public static final Fraction TWO_QUARTERS = new Fraction(2, 4);
    public static final Fraction TWO_THIRDS = new Fraction(2, 3);
    public static final Fraction ZERO = new Fraction(0, 1);
    private static final long serialVersionUID = 3698073679419233275L;
    private final int denominator;
    private final int numerator;

    public Fraction(double value) throws FractionConversionException {
        this(value, DEFAULT_EPSILON, 100);
    }

    public Fraction(double value, double epsilon, int maxIterations) throws FractionConversionException {
        this(value, epsilon, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, maxIterations);
    }

    public Fraction(double value, int maxDenominator) throws FractionConversionException {
        this(value, 0.0d, maxDenominator, 100);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0070, code lost:
        if (r40 != 0.0d) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x007b, code lost:
        if (org.apache.commons.math3.util.FastMath.abs(r30) >= ((long) r42)) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0092, code lost:
        throw new org.apache.commons.math3.fraction.FractionConversionException(r38, r14, r16);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private Fraction(double r38, double r40, int r42, int r43) throws org.apache.commons.math3.fraction.FractionConversionException {
        /*
        // Method dump skipped, instructions count: 229
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.fraction.Fraction.<init>(double, double, int, int):void");
    }

    public Fraction(int num) {
        this(num, 1);
    }

    public Fraction(int num, int den) {
        if (den == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, Integer.valueOf(num), Integer.valueOf(den));
        }
        if (den < 0) {
            if (num == Integer.MIN_VALUE || den == Integer.MIN_VALUE) {
                throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, Integer.valueOf(num), Integer.valueOf(den));
            }
            num = -num;
            den = -den;
        }
        int d = ArithmeticUtils.gcd(num, den);
        if (d > 1) {
            num /= d;
            den /= d;
        }
        if (den < 0) {
            num = -num;
            den = -den;
        }
        this.numerator = num;
        this.denominator = den;
    }

    public Fraction abs() {
        if (this.numerator >= 0) {
            return this;
        }
        return negate();
    }

    public int compareTo(Fraction object) {
        long nOd = ((long) this.numerator) * ((long) object.denominator);
        long dOn = ((long) this.denominator) * ((long) object.numerator);
        if (nOd < dOn) {
            return -1;
        }
        return nOd > dOn ? 1 : 0;
    }

    public double doubleValue() {
        return ((double) this.numerator) / ((double) this.denominator);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Fraction)) {
            return false;
        }
        Fraction rhs = (Fraction) other;
        return this.numerator == rhs.numerator && this.denominator == rhs.denominator;
    }

    public float floatValue() {
        return (float) doubleValue();
    }

    public int getDenominator() {
        return this.denominator;
    }

    public int getNumerator() {
        return this.numerator;
    }

    public int hashCode() {
        return ((this.numerator + 629) * 37) + this.denominator;
    }

    public int intValue() {
        return (int) doubleValue();
    }

    public long longValue() {
        return (long) doubleValue();
    }

    @Override // org.apache.commons.math3.FieldElement
    public Fraction negate() {
        if (this.numerator != Integer.MIN_VALUE) {
            return new Fraction(-this.numerator, this.denominator);
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, Integer.valueOf(this.numerator), Integer.valueOf(this.denominator));
    }

    @Override // org.apache.commons.math3.FieldElement
    public Fraction reciprocal() {
        return new Fraction(this.denominator, this.numerator);
    }

    public Fraction add(Fraction fraction) {
        return addSub(fraction, true);
    }

    public Fraction add(int i) {
        return new Fraction(this.numerator + (this.denominator * i), this.denominator);
    }

    public Fraction subtract(Fraction fraction) {
        return addSub(fraction, false);
    }

    public Fraction subtract(int i) {
        return new Fraction(this.numerator - (this.denominator * i), this.denominator);
    }

    private Fraction addSub(Fraction fraction, boolean isAdd) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (this.numerator == 0) {
            if (isAdd) {
                return fraction;
            }
            return fraction.negate();
        } else if (fraction.numerator == 0) {
            return this;
        } else {
            int d1 = ArithmeticUtils.gcd(this.denominator, fraction.denominator);
            if (d1 == 1) {
                int uvp = ArithmeticUtils.mulAndCheck(this.numerator, fraction.denominator);
                int upv = ArithmeticUtils.mulAndCheck(fraction.numerator, this.denominator);
                return new Fraction(isAdd ? ArithmeticUtils.addAndCheck(uvp, upv) : ArithmeticUtils.subAndCheck(uvp, upv), ArithmeticUtils.mulAndCheck(this.denominator, fraction.denominator));
            }
            BigInteger uvp2 = BigInteger.valueOf((long) this.numerator).multiply(BigInteger.valueOf((long) (fraction.denominator / d1)));
            BigInteger upv2 = BigInteger.valueOf((long) fraction.numerator).multiply(BigInteger.valueOf((long) (this.denominator / d1)));
            BigInteger t = isAdd ? uvp2.add(upv2) : uvp2.subtract(upv2);
            int tmodd1 = t.mod(BigInteger.valueOf((long) d1)).intValue();
            int d2 = tmodd1 == 0 ? d1 : ArithmeticUtils.gcd(tmodd1, d1);
            BigInteger w = t.divide(BigInteger.valueOf((long) d2));
            if (w.bitLength() <= 31) {
                return new Fraction(w.intValue(), ArithmeticUtils.mulAndCheck(this.denominator / d1, fraction.denominator / d2));
            }
            throw new MathArithmeticException(LocalizedFormats.NUMERATOR_OVERFLOW_AFTER_MULTIPLY, w);
        }
    }

    public Fraction multiply(Fraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (this.numerator == 0 || fraction.numerator == 0) {
            return ZERO;
        } else {
            int d1 = ArithmeticUtils.gcd(this.numerator, fraction.denominator);
            int d2 = ArithmeticUtils.gcd(fraction.numerator, this.denominator);
            return getReducedFraction(ArithmeticUtils.mulAndCheck(this.numerator / d1, fraction.numerator / d2), ArithmeticUtils.mulAndCheck(this.denominator / d2, fraction.denominator / d1));
        }
    }

    @Override // org.apache.commons.math3.FieldElement
    public Fraction multiply(int i) {
        return multiply(new Fraction(i));
    }

    public Fraction divide(Fraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (fraction.numerator != 0) {
            return multiply(fraction.reciprocal());
        } else {
            throw new MathArithmeticException(LocalizedFormats.ZERO_FRACTION_TO_DIVIDE_BY, Integer.valueOf(fraction.numerator), Integer.valueOf(fraction.denominator));
        }
    }

    public Fraction divide(int i) {
        return divide(new Fraction(i));
    }

    public double percentageValue() {
        return 100.0d * doubleValue();
    }

    public static Fraction getReducedFraction(int numerator2, int denominator2) {
        if (denominator2 == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, Integer.valueOf(numerator2), Integer.valueOf(denominator2));
        } else if (numerator2 == 0) {
            return ZERO;
        } else {
            if (denominator2 == Integer.MIN_VALUE && (numerator2 & 1) == 0) {
                numerator2 /= 2;
                denominator2 /= 2;
            }
            if (denominator2 < 0) {
                if (numerator2 == Integer.MIN_VALUE || denominator2 == Integer.MIN_VALUE) {
                    throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, Integer.valueOf(numerator2), Integer.valueOf(denominator2));
                }
                numerator2 = -numerator2;
                denominator2 = -denominator2;
            }
            int gcd = ArithmeticUtils.gcd(numerator2, denominator2);
            return new Fraction(numerator2 / gcd, denominator2 / gcd);
        }
    }

    public String toString() {
        if (this.denominator == 1) {
            return Integer.toString(this.numerator);
        }
        if (this.numerator == 0) {
            return "0";
        }
        return this.numerator + " / " + this.denominator;
    }

    /* Return type fixed from 'org.apache.commons.math3.fraction.FractionField' to match base method */
    @Override // org.apache.commons.math3.FieldElement
    public Field<Fraction> getField() {
        return FractionField.getInstance();
    }
}
