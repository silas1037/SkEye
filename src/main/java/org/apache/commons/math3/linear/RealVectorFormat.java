package org.apache.commons.math3.linear;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.util.CompositeFormat;

public class RealVectorFormat {
    private static final String DEFAULT_PREFIX = "{";
    private static final String DEFAULT_SEPARATOR = "; ";
    private static final String DEFAULT_SUFFIX = "}";
    private final NumberFormat format;
    private final String prefix;
    private final String separator;
    private final String suffix;
    private final String trimmedPrefix;
    private final String trimmedSeparator;
    private final String trimmedSuffix;

    public RealVectorFormat() {
        this("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
    }

    public RealVectorFormat(NumberFormat format2) {
        this("{", "}", "; ", format2);
    }

    public RealVectorFormat(String prefix2, String suffix2, String separator2) {
        this(prefix2, suffix2, separator2, CompositeFormat.getDefaultNumberFormat());
    }

    public RealVectorFormat(String prefix2, String suffix2, String separator2, NumberFormat format2) {
        this.prefix = prefix2;
        this.suffix = suffix2;
        this.separator = separator2;
        this.trimmedPrefix = prefix2.trim();
        this.trimmedSuffix = suffix2.trim();
        this.trimmedSeparator = separator2.trim();
        this.format = format2;
    }

    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getSeparator() {
        return this.separator;
    }

    public NumberFormat getFormat() {
        return this.format;
    }

    public static RealVectorFormat getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static RealVectorFormat getInstance(Locale locale) {
        return new RealVectorFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }

    public String format(RealVector v) {
        return format(v, new StringBuffer(), new FieldPosition(0)).toString();
    }

    public StringBuffer format(RealVector vector, StringBuffer toAppendTo, FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        toAppendTo.append(this.prefix);
        for (int i = 0; i < vector.getDimension(); i++) {
            if (i > 0) {
                toAppendTo.append(this.separator);
            }
            CompositeFormat.formatDouble(vector.getEntry(i), this.format, toAppendTo, pos);
        }
        toAppendTo.append(this.suffix);
        return toAppendTo;
    }

    public ArrayRealVector parse(String source) {
        ParsePosition parsePosition = new ParsePosition(0);
        ArrayRealVector result = parse(source, parsePosition);
        if (parsePosition.getIndex() != 0) {
            return result;
        }
        throw new MathParseException(source, parsePosition.getErrorIndex(), ArrayRealVector.class);
    }

    public ArrayRealVector parse(String source, ParsePosition pos) {
        int initialIndex = pos.getIndex();
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, this.trimmedPrefix, pos)) {
            return null;
        }
        List<Number> components = new ArrayList<>();
        boolean loop = true;
        while (loop) {
            if (!components.isEmpty()) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                if (!CompositeFormat.parseFixedstring(source, this.trimmedSeparator, pos)) {
                    loop = false;
                }
            }
            if (loop) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                Number component = CompositeFormat.parseNumber(source, this.format, pos);
                if (component != null) {
                    components.add(component);
                } else {
                    pos.setIndex(initialIndex);
                    return null;
                }
            }
        }
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, this.trimmedSuffix, pos)) {
            return null;
        }
        double[] data = new double[components.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = components.get(i).doubleValue();
        }
        return new ArrayRealVector(data, false);
    }
}
