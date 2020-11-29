package org.apache.commons.math3.geometry.euclidean.threed;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.VectorFormat;
import org.apache.commons.math3.util.CompositeFormat;

public class Vector3DFormat extends VectorFormat<Euclidean3D> {
    public Vector3DFormat() {
        super(VectorFormat.DEFAULT_PREFIX, VectorFormat.DEFAULT_SUFFIX, VectorFormat.DEFAULT_SEPARATOR, CompositeFormat.getDefaultNumberFormat());
    }

    public Vector3DFormat(NumberFormat format) {
        super(VectorFormat.DEFAULT_PREFIX, VectorFormat.DEFAULT_SUFFIX, VectorFormat.DEFAULT_SEPARATOR, format);
    }

    public Vector3DFormat(String prefix, String suffix, String separator) {
        super(prefix, suffix, separator, CompositeFormat.getDefaultNumberFormat());
    }

    public Vector3DFormat(String prefix, String suffix, String separator, NumberFormat format) {
        super(prefix, suffix, separator, format);
    }

    public static Vector3DFormat getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static Vector3DFormat getInstance(Locale locale) {
        return new Vector3DFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }

    @Override // org.apache.commons.math3.geometry.VectorFormat
    public StringBuffer format(Vector<Euclidean3D> vector, StringBuffer toAppendTo, FieldPosition pos) {
        Vector3D v3 = (Vector3D) vector;
        return format(toAppendTo, pos, v3.getX(), v3.getY(), v3.getZ());
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    /* JADX WARN: Type inference failed for: r1v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.VectorFormat
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> parse(java.lang.String r6) throws org.apache.commons.math3.exception.MathParseException {
        /*
            r5 = this;
            java.text.ParsePosition r0 = new java.text.ParsePosition
            r2 = 0
            r0.<init>(r2)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r5.parse(r6, r0)
            int r2 = r0.getIndex()
            if (r2 != 0) goto L_0x001c
            org.apache.commons.math3.exception.MathParseException r2 = new org.apache.commons.math3.exception.MathParseException
            int r3 = r0.getErrorIndex()
            java.lang.Class<org.apache.commons.math3.geometry.euclidean.threed.Vector3D> r4 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.class
            r2.<init>(r6, r3, r4)
            throw r2
        L_0x001c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Vector3DFormat.parse(java.lang.String):org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.VectorFormat
    public Vector<Euclidean3D> parse(String source, ParsePosition pos) {
        double[] coordinates = parseCoordinates(3, source, pos);
        if (coordinates == null) {
            return null;
        }
        return new Vector3D(coordinates[0], coordinates[1], coordinates[2]);
    }
}
