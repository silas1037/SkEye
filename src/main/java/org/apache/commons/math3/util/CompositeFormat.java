package org.apache.commons.math3.util;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class CompositeFormat {
    private CompositeFormat() {
    }

    public static NumberFormat getDefaultNumberFormat() {
        return getDefaultNumberFormat(Locale.getDefault());
    }

    public static NumberFormat getDefaultNumberFormat(Locale locale) {
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(10);
        return nf;
    }

    public static void parseAndIgnoreWhitespace(String source, ParsePosition pos) {
        parseNextCharacter(source, pos);
        pos.setIndex(pos.getIndex() - 1);
    }

    public static char parseNextCharacter(String source, ParsePosition pos) {
        int index;
        char c;
        int index2 = pos.getIndex();
        int n = source.length();
        if (index2 >= n) {
            return 0;
        }
        while (true) {
            index = index2 + 1;
            c = source.charAt(index2);
            if (!Character.isWhitespace(c) || index >= n) {
                pos.setIndex(index);
            } else {
                index2 = index;
            }
        }
        pos.setIndex(index);
        if (index < n) {
            return c;
        }
        return 0;
    }

    private static Number parseNumber(String source, double value, ParsePosition pos) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        sb.append(value);
        sb.append(')');
        int n = sb.length();
        int startIndex = pos.getIndex();
        int endIndex = startIndex + n;
        if (endIndex >= source.length() || source.substring(startIndex, endIndex).compareTo(sb.toString()) != 0) {
            return null;
        }
        Number ret = Double.valueOf(value);
        pos.setIndex(endIndex);
        return ret;
    }

    public static Number parseNumber(String source, NumberFormat format, ParsePosition pos) {
        int startIndex = pos.getIndex();
        Number number = format.parse(source, pos);
        if (startIndex == pos.getIndex()) {
            double[] special = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
            int i = 0;
            while (i < special.length && (number = parseNumber(source, special[i], pos)) == null) {
                i++;
            }
        }
        return number;
    }

    public static boolean parseFixedstring(String source, String expected, ParsePosition pos) {
        int startIndex = pos.getIndex();
        int endIndex = startIndex + expected.length();
        if (startIndex >= source.length() || endIndex > source.length() || source.substring(startIndex, endIndex).compareTo(expected) != 0) {
            pos.setIndex(startIndex);
            pos.setErrorIndex(startIndex);
            return false;
        }
        pos.setIndex(endIndex);
        return true;
    }

    public static StringBuffer formatDouble(double value, NumberFormat format, StringBuffer toAppendTo, FieldPosition pos) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            toAppendTo.append('(');
            toAppendTo.append(value);
            toAppendTo.append(')');
        } else {
            format.format(value, toAppendTo, pos);
        }
        return toAppendTo;
    }
}
