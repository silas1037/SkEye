package org.apache.commons.math3.fraction;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class ProperFractionFormat extends FractionFormat {
    private static final long serialVersionUID = 760934726031766749L;
    private NumberFormat wholeFormat;

    public ProperFractionFormat() {
        this(getDefaultNumberFormat());
    }

    public ProperFractionFormat(NumberFormat format) {
        this(format, (NumberFormat) format.clone(), (NumberFormat) format.clone());
    }

    public ProperFractionFormat(NumberFormat wholeFormat2, NumberFormat numeratorFormat, NumberFormat denominatorFormat) {
        super(numeratorFormat, denominatorFormat);
        setWholeFormat(wholeFormat2);
    }

    @Override // org.apache.commons.math3.fraction.FractionFormat
    public StringBuffer format(Fraction fraction, StringBuffer toAppendTo, FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        int num = fraction.getNumerator();
        int den = fraction.getDenominator();
        int whole = num / den;
        int num2 = num % den;
        if (whole != 0) {
            getWholeFormat().format((long) whole, toAppendTo, pos);
            toAppendTo.append(' ');
            num2 = FastMath.abs(num2);
        }
        getNumeratorFormat().format((long) num2, toAppendTo, pos);
        toAppendTo.append(" / ");
        getDenominatorFormat().format((long) den, toAppendTo, pos);
        return toAppendTo;
    }

    public NumberFormat getWholeFormat() {
        return this.wholeFormat;
    }

    @Override // org.apache.commons.math3.fraction.FractionFormat, org.apache.commons.math3.fraction.FractionFormat
    public Fraction parse(String source, ParsePosition pos) {
        Fraction ret = super.parse(source, pos);
        if (ret != null) {
            return ret;
        }
        int initialIndex = pos.getIndex();
        parseAndIgnoreWhitespace(source, pos);
        Number whole = getWholeFormat().parse(source, pos);
        if (whole == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        parseAndIgnoreWhitespace(source, pos);
        Number num = getNumeratorFormat().parse(source, pos);
        if (num == null) {
            pos.setIndex(initialIndex);
            return null;
        } else if (num.intValue() < 0) {
            pos.setIndex(initialIndex);
            return null;
        } else {
            int startIndex = pos.getIndex();
            switch (parseNextCharacter(source, pos)) {
                case 0:
                    return new Fraction(num.intValue(), 1);
                case '/':
                    parseAndIgnoreWhitespace(source, pos);
                    Number den = getDenominatorFormat().parse(source, pos);
                    if (den == null) {
                        pos.setIndex(initialIndex);
                        return null;
                    } else if (den.intValue() < 0) {
                        pos.setIndex(initialIndex);
                        return null;
                    } else {
                        int w = whole.intValue();
                        int n = num.intValue();
                        int d = den.intValue();
                        return new Fraction(((FastMath.abs(w) * d) + n) * MathUtils.copySign(1, w), d);
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
