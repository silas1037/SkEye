package org.apache.commons.math3.fraction;

import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class ProperBigFractionFormat extends BigFractionFormat {
    private static final long serialVersionUID = -6337346779577272307L;
    private NumberFormat wholeFormat;

    public ProperBigFractionFormat() {
        this(getDefaultNumberFormat());
    }

    public ProperBigFractionFormat(NumberFormat format) {
        this(format, (NumberFormat) format.clone(), (NumberFormat) format.clone());
    }

    public ProperBigFractionFormat(NumberFormat wholeFormat2, NumberFormat numeratorFormat, NumberFormat denominatorFormat) {
        super(numeratorFormat, denominatorFormat);
        setWholeFormat(wholeFormat2);
    }

    @Override // org.apache.commons.math3.fraction.BigFractionFormat
    public StringBuffer format(BigFraction fraction, StringBuffer toAppendTo, FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        BigInteger num = fraction.getNumerator();
        BigInteger den = fraction.getDenominator();
        BigInteger whole = num.divide(den);
        BigInteger num2 = num.remainder(den);
        if (!BigInteger.ZERO.equals(whole)) {
            getWholeFormat().format(whole, toAppendTo, pos);
            toAppendTo.append(' ');
            if (num2.compareTo(BigInteger.ZERO) < 0) {
                num2 = num2.negate();
            }
        }
        getNumeratorFormat().format(num2, toAppendTo, pos);
        toAppendTo.append(" / ");
        getDenominatorFormat().format(den, toAppendTo, pos);
        return toAppendTo;
    }

    public NumberFormat getWholeFormat() {
        return this.wholeFormat;
    }

    @Override // org.apache.commons.math3.fraction.BigFractionFormat, org.apache.commons.math3.fraction.BigFractionFormat
    public BigFraction parse(String source, ParsePosition pos) {
        BigFraction ret = super.parse(source, pos);
        if (ret != null) {
            return ret;
        }
        int initialIndex = pos.getIndex();
        parseAndIgnoreWhitespace(source, pos);
        BigInteger whole = parseNextBigInteger(source, pos);
        if (whole == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        parseAndIgnoreWhitespace(source, pos);
        BigInteger num = parseNextBigInteger(source, pos);
        if (num == null) {
            pos.setIndex(initialIndex);
            return null;
        } else if (num.compareTo(BigInteger.ZERO) < 0) {
            pos.setIndex(initialIndex);
            return null;
        } else {
            int startIndex = pos.getIndex();
            switch (parseNextCharacter(source, pos)) {
                case 0:
                    return new BigFraction(num);
                case '/':
                    parseAndIgnoreWhitespace(source, pos);
                    BigInteger den = parseNextBigInteger(source, pos);
                    if (den == null) {
                        pos.setIndex(initialIndex);
                        return null;
                    } else if (den.compareTo(BigInteger.ZERO) < 0) {
                        pos.setIndex(initialIndex);
                        return null;
                    } else {
                        boolean wholeIsNeg = whole.compareTo(BigInteger.ZERO) < 0;
                        if (wholeIsNeg) {
                            whole = whole.negate();
                        }
                        BigInteger num2 = whole.multiply(den).add(num);
                        if (wholeIsNeg) {
                            num2 = num2.negate();
                        }
                        return new BigFraction(num2, den);
                    }
                default:
                    pos.setIndex(initialIndex);
                    pos.setErrorIndex(startIndex);
                    return null;
            }
        }
    }

    public void setWholeFormat(NumberFormat format) {
        if (format == null) {
            throw new NullArgumentException(LocalizedFormats.WHOLE_FORMAT, new Object[0]);
        }
        this.wholeFormat = format;
    }
}
