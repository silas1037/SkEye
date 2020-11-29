package org.apache.commons.math3.fraction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class BigFraction extends Number implements FieldElement<BigFraction>, Comparable<BigFraction>, Serializable {
    public static final BigFraction FOUR_FIFTHS = new BigFraction(4, 5);
    public static final BigFraction MINUS_ONE = new BigFraction(-1);
    public static final BigFraction ONE = new BigFraction(1);
    public static final BigFraction ONE_FIFTH = new BigFraction(1, 5);
    public static final BigFraction ONE_HALF = new BigFraction(1, 2);
    private static final BigInteger ONE_HUNDRED = BigInteger.valueOf(100);
    public static final BigFraction ONE_QUARTER = new BigFraction(1, 4);
    public static final BigFraction ONE_THIRD = new BigFraction(1, 3);
    public static final BigFraction THREE_FIFTHS = new BigFraction(3, 5);
    public static final BigFraction THREE_QUARTERS = new BigFraction(3, 4);
    public static final BigFraction TWO = new BigFraction(2);
    public static final BigFraction TWO_FIFTHS = new BigFraction(2, 5);
    public static final BigFraction TWO_QUARTERS = new BigFraction(2, 4);
    public static final BigFraction TWO_THIRDS = new BigFraction(2, 3);
    public static final BigFraction ZERO = new BigFraction(0);
    private static final long serialVersionUID = -5630213147331578515L;
    private final BigInteger denominator;
    private final BigInteger numerator;

    public BigFraction(BigInteger num) {
        this(num, BigInteger.ONE);
    }

    public BigFraction(BigInteger num, BigInteger den) {
        MathUtils.checkNotNull(num, LocalizedFormats.NUMERATOR, new Object[0]);
        MathUtils.checkNotNull(den, LocalizedFormats.DENOMINATOR, new Object[0]);
        if (den.signum() == 0) {
            throw new ZeroException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        } else if (num.signum() == 0) {
            this.numerator = BigInteger.ZERO;
            this.denominator = BigInteger.ONE;
        } else {
            BigInteger gcd = num.gcd(den);
            if (BigInteger.ONE.compareTo(gcd) < 0) {
                num = num.divide(gcd);
                den = den.divide(gcd);
            }
            if (den.signum() == -1) {
                num = num.negate();
                den = den.negate();
            }
            this.numerator = num;
            this.denominator = den;
        }
    }

    public BigFraction(double value) throws MathIllegalArgumentException {
        if (Double.isNaN(value)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NAN_VALUE_CONVERSION, new Object[0]);
        } else if (Double.isInfinite(value)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INFINITE_VALUE_CONVERSION, new Object[0]);
        } else {
            long bits = Double.doubleToLongBits(value);
            long sign = bits & Long.MIN_VALUE;
            long exponent = bits & 9218868437227405312L;
            long m = bits & 4503599627370495L;
            m = exponent != 0 ? m | 4503599627370496L : m;
            m = sign != 0 ? -m : m;
            int k = ((int) (exponent >> 52)) - 1075;
            while ((9007199254740990L & m) != 0 && (1 & m) == 0) {
                m >>= 1;
                k++;
            }
            if (k < 0) {
                this.numerator = BigInteger.valueOf(m);
                this.denominator = BigInteger.ZERO.flipBit(-k);
                return;
            }
            this.numerator = BigInteger.valueOf(m).multiply(BigInteger.ZERO.flipBit(k));
            this.denominator = BigInteger.ONE;
        }
    }

    public BigFraction(double value, double epsilon, int maxIterations) throws FractionConversionException {
        this(value, epsilon, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, maxIterations);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x006c, code lost:
        if (r40 != 0.0d) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0077, code lost:
        if (org.apache.commons.math3.util.FastMath.abs(r30) >= ((long) r42)) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x008e, code lost:
        throw new org.apache.commons.math3.fraction.FractionConversionException(r38, r14, r16);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private BigFraction(double r38, double r40, int r42, int r43) throws org.apache.commons.math3.fraction.FractionConversionException {
        /*
        // Method dump skipped, instructions count: 231
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.fraction.BigFraction.<init>(double, double, int, int):void");
    }

    public BigFraction(double value, int maxDenominator) throws FractionConversionException {
        this(value, 0.0d, maxDenominator, 100);
    }

    public BigFraction(int num) {
        this(BigInteger.valueOf((long) num), BigInteger.ONE);
    }

    public BigFraction(int num, int den) {
        this(BigInteger.valueOf((long) num), BigInteger.valueOf((long) den));
    }

    public BigFraction(long num) {
        this(BigInteger.valueOf(num), BigInteger.ONE);
    }

    public BigFraction(long num, long den) {
        this(BigInteger.valueOf(num), BigInteger.valueOf(den));
    }

    public static BigFraction getReducedFraction(int numerator2, int denominator2) {
        if (numerator2 == 0) {
            return ZERO;
        }
        return new BigFraction(numerator2, denominator2);
    }

    public BigFraction abs() {
        return this.numerator.signum() == 1 ? this : negate();
    }

    public BigFraction add(BigInteger bg) throws NullArgumentException {
        MathUtils.checkNotNull(bg);
        if (this.numerator.signum() == 0) {
            return new BigFraction(bg);
        }
        return bg.signum() != 0 ? new BigFraction(this.numerator.add(this.denominator.multiply(bg)), this.denominator) : this;
    }

    public BigFraction add(int i) {
        return add(BigInteger.valueOf((long) i));
    }

    public BigFraction add(long l) {
        return add(BigInteger.valueOf(l));
    }

    public BigFraction add(BigFraction fraction) {
        BigInteger num;
        BigInteger den;
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (fraction.numerator.signum() == 0) {
            return this;
        } else {
            if (this.numerator.signum() == 0) {
                return fraction;
            }
            if (this.denominator.equals(fraction.denominator)) {
                num = this.numerator.add(fraction.numerator);
                den = this.denominator;
            } else {
                num = this.numerator.multiply(fraction.denominator).add(fraction.numerator.multiply(this.denominator));
                den = this.denominator.multiply(fraction.denominator);
            }
            if (num.signum() == 0) {
                return ZERO;
            }
            return new BigFraction(num, den);
        }
    }

    public BigDecimal bigDecimalValue() {
        return new BigDecimal(this.numerator).divide(new BigDecimal(this.denominator));
    }

    public BigDecimal bigDecimalValue(int roundingMode) {
        return new BigDecimal(this.numerator).divide(new BigDecimal(this.denominator), roundingMode);
    }

    public BigDecimal bigDecimalValue(int scale, int roundingMode) {
        return new BigDecimal(this.numerator).divide(new BigDecimal(this.denominator), scale, roundingMode);
    }

    public int compareTo(BigFraction object) {
        int lhsSigNum = this.numerator.signum();
        int rhsSigNum = object.numerator.signum();
        if (lhsSigNum != rhsSigNum) {
            return lhsSigNum > rhsSigNum ? 1 : -1;
        }
        if (lhsSigNum == 0) {
            return 0;
        }
        return this.numerator.multiply(object.denominator).compareTo(this.denominator.multiply(object.numerator));
    }

    public BigFraction divide(BigInteger bg) {
        if (bg == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (bg.signum() == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        } else if (this.numerator.signum() == 0) {
            return ZERO;
        } else {
            return new BigFraction(this.numerator, this.denominator.multiply(bg));
        }
    }

    public BigFraction divide(int i) {
        return divide(BigInteger.valueOf((long) i));
    }

    public BigFraction divide(long l) {
        return divide(BigInteger.valueOf(l));
    }

    public BigFraction divide(BigFraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (fraction.numerator.signum() == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        } else if (this.numerator.signum() == 0) {
            return ZERO;
        } else {
            return multiply(fraction.reciprocal());
        }
    }

    public double doubleValue() {
        double result = this.numerator.doubleValue() / this.denominator.doubleValue();
        if (!Double.isNaN(result)) {
            return result;
        }
        int shift = FastMath.max(this.numerator.bitLength(), this.denominator.bitLength()) - FastMath.getExponent(Double.MAX_VALUE);
        return this.numerator.shiftRight(shift).doubleValue() / this.denominator.shiftRight(shift).doubleValue();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BigFraction)) {
            return false;
        }
        BigFraction rhs = ((BigFraction) other).reduce();
        BigFraction thisOne = reduce();
        return thisOne.numerator.equals(rhs.numerator) && thisOne.denominator.equals(rhs.denominator);
    }

    public float floatValue() {
        float result = this.numerator.floatValue() / this.denominator.floatValue();
        if (!Double.isNaN((double) result)) {
            return result;
        }
        int shift = FastMath.max(this.numerator.bitLength(), this.denominator.bitLength()) - FastMath.getExponent(Float.MAX_VALUE);
        return this.numerator.shiftRight(shift).floatValue() / this.denominator.shiftRight(shift).floatValue();
    }

    public BigInteger getDenominator() {
        return this.denominator;
    }

    public int getDenominatorAsInt() {
        return this.denominator.intValue();
    }

    public long getDenominatorAsLong() {
        return this.denominator.longValue();
    }

    public BigInteger getNumerator() {
        return this.numerator;
    }

    public int getNumeratorAsInt() {
        return this.numerator.intValue();
    }

    public long getNumeratorAsLong() {
        return this.numerator.longValue();
    }

    public int hashCode() {
        return ((this.numerator.hashCode() + 629) * 37) + this.denominator.hashCode();
    }

    public int intValue() {
        return this.numerator.divide(this.denominator).intValue();
    }

    public long longValue() {
        return this.numerator.divide(this.denominator).longValue();
    }

    public BigFraction multiply(BigInteger bg) {
        if (bg == null) {
            throw new NullArgumentException();
        } else if (this.numerator.signum() == 0 || bg.signum() == 0) {
            return ZERO;
        } else {
            return new BigFraction(bg.multiply(this.numerator), this.denominator);
        }
    }

    @Override // org.apache.commons.math3.FieldElement
    public BigFraction multiply(int i) {
        if (i == 0 || this.numerator.signum() == 0) {
            return ZERO;
        }
        return multiply(BigInteger.valueOf((long) i));
    }

    public BigFraction multiply(long l) {
        if (l == 0 || this.numerator.signum() == 0) {
            return ZERO;
        }
        return multiply(BigInteger.valueOf(l));
    }

    public BigFraction multiply(BigFraction fraction) {
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (this.numerator.signum() == 0 || fraction.numerator.signum() == 0) {
            return ZERO;
        } else {
            return new BigFraction(this.numerator.multiply(fraction.numerator), this.denominator.multiply(fraction.denominator));
        }
    }

    @Override // org.apache.commons.math3.FieldElement
    public BigFraction negate() {
        return new BigFraction(this.numerator.negate(), this.denominator);
    }

    public double percentageValue() {
        return multiply(ONE_HUNDRED).doubleValue();
    }

    public BigFraction pow(int exponent) {
        if (exponent == 0) {
            return ONE;
        }
        if (this.numerator.signum() == 0) {
            return this;
        }
        if (exponent < 0) {
            return new BigFraction(this.denominator.pow(-exponent), this.numerator.pow(-exponent));
        }
        return new BigFraction(this.numerator.pow(exponent), this.denominator.pow(exponent));
    }

    public BigFraction pow(long exponent) {
        if (exponent == 0) {
            return ONE;
        }
        if (this.numerator.signum() == 0) {
            return this;
        }
        if (exponent < 0) {
            return new BigFraction(ArithmeticUtils.pow(this.denominator, -exponent), ArithmeticUtils.pow(this.numerator, -exponent));
        }
        return new BigFraction(ArithmeticUtils.pow(this.numerator, exponent), ArithmeticUtils.pow(this.denominator, exponent));
    }

    public BigFraction pow(BigInteger exponent) {
        if (exponent.signum() == 0) {
            return ONE;
        }
        if (this.numerator.signum() == 0) {
            return this;
        }
        if (exponent.signum() != -1) {
            return new BigFraction(ArithmeticUtils.pow(this.numerator, exponent), ArithmeticUtils.pow(this.denominator, exponent));
        }
        BigInteger eNeg = exponent.negate();
        return new BigFraction(ArithmeticUtils.pow(this.denominator, eNeg), ArithmeticUtils.pow(this.numerator, eNeg));
    }

    public double pow(double exponent) {
        return FastMath.pow(this.numerator.doubleValue(), exponent) / FastMath.pow(this.denominator.doubleValue(), exponent);
    }

    @Override // org.apache.commons.math3.FieldElement
    public BigFraction reciprocal() {
        return new BigFraction(this.denominator, this.numerator);
    }

    public BigFraction reduce() {
        BigInteger gcd = this.numerator.gcd(this.denominator);
        if (BigInteger.ONE.compareTo(gcd) < 0) {
            return new BigFraction(this.numerator.divide(gcd), this.denominator.divide(gcd));
        }
        return this;
    }

    public BigFraction subtract(BigInteger bg) {
        if (bg == null) {
            throw new NullArgumentException();
        } else if (bg.signum() == 0) {
            return this;
        } else {
            if (this.numerator.signum() == 0) {
                return new BigFraction(bg.negate());
            }
            return new BigFraction(this.numerator.subtract(this.denominator.multiply(bg)), this.denominator);
        }
    }

    public BigFraction subtract(int i) {
        return subtract(BigInteger.valueOf((long) i));
    }

    public BigFraction subtract(long l) {
        return subtract(BigInteger.valueOf(l));
    }

    public BigFraction subtract(BigFraction fraction) {
        BigInteger num;
        BigInteger den;
        if (fraction == null) {
            throw new NullArgumentException(LocalizedFormats.FRACTION, new Object[0]);
        } else if (fraction.numerator.signum() == 0) {
            return this;
        } else {
            if (this.numerator.signum() == 0) {
                return fraction.negate();
            }
            if (this.denominator.equals(fraction.denominator)) {
                num = this.numerator.subtract(fraction.numerator);
                den = this.denominator;
            } else {
                num = this.numerator.multiply(fraction.denominator).subtract(fraction.numerator.multiply(this.denominator));
                den = this.denominator.multiply(fraction.denominator);
            }
            return new BigFraction(num, den);
        }
    }

    public String toString() {
        if (BigInteger.ONE.equals(this.denominator)) {
            return this.numerator.toString();
        }
        if (BigInteger.ZERO.equals(this.numerator)) {
            return "0";
        }
        return this.numerator + " / " + this.denominator;
    }

    /* Return type fixed from 'org.apache.commons.math3.fraction.BigFractionField' to match base method */
    @Override // org.apache.commons.math3.FieldElement
    public Field<BigFraction> getField() {
        return BigFractionField.getInstance();
    }
}
