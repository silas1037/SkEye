package org.apache.commons.math3.complex;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.CompositeFormat;

public class ComplexFormat {
    private static final String DEFAULT_IMAGINARY_CHARACTER = "i";
    private final String imaginaryCharacter;
    private final NumberFormat imaginaryFormat;
    private final NumberFormat realFormat;

    public ComplexFormat() {
        this.imaginaryCharacter = DEFAULT_IMAGINARY_CHARACTER;
        this.imaginaryFormat = CompositeFormat.getDefaultNumberFormat();
        this.realFormat = this.imaginaryFormat;
    }

    public ComplexFormat(NumberFormat format) throws NullArgumentException {
        if (format == null) {
            throw new NullArgumentException(LocalizedFormats.IMAGINARY_FORMAT, new Object[0]);
        }
        this.imaginaryCharacter = DEFAULT_IMAGINARY_CHARACTER;
        this.imaginaryFormat = format;
        this.realFormat = format;
    }

    public ComplexFormat(NumberFormat realFormat2, NumberFormat imaginaryFormat2) throws NullArgumentException {
        if (imaginaryFormat2 == null) {
            throw new NullArgumentException(LocalizedFormats.IMAGINARY_FORMAT, new Object[0]);
        } else if (realFormat2 == null) {
            throw new NullArgumentException(LocalizedFormats.REAL_FORMAT, new Object[0]);
        } else {
            this.imaginaryCharacter = DEFAULT_IMAGINARY_CHARACTER;
            this.imaginaryFormat = imaginaryFormat2;
            this.realFormat = realFormat2;
        }
    }

    public ComplexFormat(String imaginaryCharacter2) throws NullArgumentException, NoDataException {
        this(imaginaryCharacter2, CompositeFormat.getDefaultNumberFormat());
    }

    public ComplexFormat(String imaginaryCharacter2, NumberFormat format) throws NullArgumentException, NoDataException {
        this(imaginaryCharacter2, format, format);
    }

    public ComplexFormat(String imaginaryCharacter2, NumberFormat realFormat2, NumberFormat imaginaryFormat2) throws NullArgumentException, NoDataException {
        if (imaginaryCharacter2 == null) {
            throw new NullArgumentException();
        } else if (imaginaryCharacter2.length() == 0) {
            throw new NoDataException();
        } else if (imaginaryFormat2 == null) {
            throw new NullArgumentException(LocalizedFormats.IMAGINARY_FORMAT, new Object[0]);
        } else if (realFormat2 == null) {
            throw new NullArgumentException(LocalizedFormats.REAL_FORMAT, new Object[0]);
        } else {
            this.imaginaryCharacter = imaginaryCharacter2;
            this.imaginaryFormat = imaginaryFormat2;
            this.realFormat = realFormat2;
        }
    }

    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }

    public String format(Complex c) {
        return format(c, new StringBuffer(), new FieldPosition(0)).toString();
    }

    public String format(Double c) {
        return format(new Complex(c.doubleValue(), 0.0d), new StringBuffer(), new FieldPosition(0)).toString();
    }

    public StringBuffer format(Complex complex, StringBuffer toAppendTo, FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        CompositeFormat.formatDouble(complex.getReal(), getRealFormat(), toAppendTo, pos);
        double im = complex.getImaginary();
        if (im < 0.0d) {
            toAppendTo.append(" - ");
            toAppendTo.append(formatImaginary(-im, new StringBuffer(), pos));
            toAppendTo.append(getImaginaryCharacter());
        } else if (im > 0.0d || Double.isNaN(im)) {
            toAppendTo.append(" + ");
            toAppendTo.append(formatImaginary(im, new StringBuffer(), pos));
            toAppendTo.append(getImaginaryCharacter());
        }
        return toAppendTo;
    }

    private StringBuffer formatImaginary(double absIm, StringBuffer toAppendTo, FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        CompositeFormat.formatDouble(absIm, getImaginaryFormat(), toAppendTo, pos);
        if (toAppendTo.toString().equals("1")) {
            toAppendTo.setLength(0);
        }
        return toAppendTo;
    }

    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) throws MathIllegalArgumentException {
        if (obj instanceof Complex) {
            return format((Complex) obj, toAppendTo, pos);
        }
        if (obj instanceof Number) {
            return format(new Complex(((Number) obj).doubleValue(), 0.0d), toAppendTo, pos);
        }
        throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_FORMAT_INSTANCE_AS_COMPLEX, obj.getClass().getName());
    }

    public String getImaginaryCharacter() {
        return this.imaginaryCharacter;
    }

    public NumberFormat getImaginaryFormat() {
        return this.imaginaryFormat;
    }

    public static ComplexFormat getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static ComplexFormat getInstance(Locale locale) {
        return new ComplexFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }

    public static ComplexFormat getInstance(String imaginaryCharacter2, Locale locale) throws NullArgumentException, NoDataException {
        return new ComplexFormat(imaginaryCharacter2, CompositeFormat.getDefaultNumberFormat(locale));
    }

    public NumberFormat getRealFormat() {
        return this.realFormat;
    }

    public Complex parse(String source) throws MathParseException {
        ParsePosition parsePosition = new ParsePosition(0);
        Complex result = parse(source, parsePosition);
        if (parsePosition.getIndex() != 0) {
            return result;
        }
        throw new MathParseException(source, parsePosition.getErrorIndex(), Complex.class);
    }

    public Complex parse(String source, ParsePosition pos) {
        int sign;
        int initialIndex = pos.getIndex();
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        Number re = CompositeFormat.parseNumber(source, getRealFormat(), pos);
        if (re == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        int startIndex = pos.getIndex();
        switch (CompositeFormat.parseNextCharacter(source, pos)) {
            case 0:
                return new Complex(re.doubleValue(), 0.0d);
            case '+':
                sign = 1;
                break;
            case '-':
                sign = -1;
                break;
            default:
                pos.setIndex(initialIndex);
                pos.setErrorIndex(startIndex);
                return null;
        }
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        Number im = CompositeFormat.parseNumber(source, getRealFormat(), pos);
        if (im == null) {
            pos.setIndex(initialIndex);
            return null;
        } else if (!CompositeFormat.parseFixedstring(source, getImaginaryCharacter(), pos)) {
            return null;
        } else {
            return new Complex(re.doubleValue(), im.doubleValue() * ((double) sign));
        }
    }
}
