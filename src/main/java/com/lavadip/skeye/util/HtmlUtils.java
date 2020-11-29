package com.lavadip.skeye.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Html;
import org.apache.commons.math3.dfp.DfpField;

public class HtmlUtils {
    @TargetApi(DfpField.FLAG_INEXACT)
    public static String escapeHtml(CharSequence text) {
        if (Build.VERSION.SDK_INT < 16) {
            return escapeHtmlImpl(text);
        }
        return Html.escapeHtml(text);
    }

    private static String escapeHtmlImpl(CharSequence text) {
        StringBuilder out = new StringBuilder();
        withinStyle(out, text, 0, text.length());
        return out.toString();
    }

    private static void withinStyle(StringBuilder out, CharSequence text, int start, int end) {
        char d;
        int i = start;
        while (i < end) {
            char c = text.charAt(i);
            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c < 55296 || c > 57343) {
                if (c > '~' || c < ' ') {
                    out.append("&#").append((int) c).append(";");
                } else if (c == ' ') {
                    while (i + 1 < end && text.charAt(i + 1) == ' ') {
                        out.append("&nbsp;");
                        i++;
                    }
                    out.append(' ');
                } else {
                    out.append(c);
                }
            } else if (c < 56320 && i + 1 < end && (d = text.charAt(i + 1)) >= 56320 && d <= 57343) {
                i++;
                out.append("&#").append(65536 | ((c - 55296) << 10) | (d - 56320)).append(";");
            }
            i++;
        }
    }
}
