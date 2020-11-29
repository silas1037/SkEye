package org.apache.commons.math3.analysis.interpolation;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class FieldHermiteInterpolator<T extends FieldElement<T>> {
    private final List<T> abscissae = new ArrayList();
    private final List<T[]> bottomDiagonal = new ArrayList();
    private final List<T[]> topDiagonal = new ArrayList();

    /* JADX DEBUG: Multi-variable search result rejected for r9v4, resolved type: java.util.List<T extends org.apache.commons.math3.FieldElement<T>[]> */
    /* JADX DEBUG: Multi-variable search result rejected for r9v5, resolved type: java.util.List<T extends org.apache.commons.math3.FieldElement<T>[]> */
    /* JADX WARN: Multi-variable type inference failed */
    public void addSamplePoint(T x, T[]... value) throws ZeroException, MathArithmeticException, DimensionMismatchException, NullArgumentException {
        MathUtils.checkNotNull(x);
        FieldElement fieldElement = (FieldElement) x.getField().getOne();
        for (int i = 0; i < value.length; i++) {
            FieldElement[] fieldElementArr = (FieldElement[]) value[i].clone();
            if (i > 1) {
                fieldElement = (FieldElement) fieldElement.multiply(i);
                FieldElement fieldElement2 = (FieldElement) fieldElement.reciprocal();
                for (int j = 0; j < fieldElementArr.length; j++) {
                    fieldElementArr[j] = (FieldElement) fieldElementArr[j].multiply(fieldElement2);
                }
            }
            int n = this.abscissae.size();
            this.bottomDiagonal.add(n - i, fieldElementArr);
            FieldElement[] fieldElementArr2 = fieldElementArr;
            for (int j2 = i; j2 < n; j2++) {
                T[] tArr = this.bottomDiagonal.get(n - (j2 + 1));
                if (x.equals(this.abscissae.get(n - (j2 + 1)))) {
                    throw new ZeroException(LocalizedFormats.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, x);
                }
                FieldElement fieldElement3 = (FieldElement) ((FieldElement) x.subtract(this.abscissae.get(n - (j2 + 1)))).reciprocal();
                for (int k = 0; k < fieldElementArr.length; k++) {
                    tArr[k] = (FieldElement) fieldElement3.multiply(fieldElementArr2[k].subtract(tArr[k]));
                }
                fieldElementArr2 = tArr;
            }
            this.topDiagonal.add(fieldElementArr2.clone());
            this.abscissae.add(x);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r6v13, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    public T[] value(T x) throws NoDataException, NullArgumentException {
        MathUtils.checkNotNull(x);
        if (this.abscissae.isEmpty()) {
            throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
        }
        T[] value = (T[]) ((FieldElement[]) MathArrays.buildArray(x.getField(), this.topDiagonal.get(0).length));
        FieldElement fieldElement = (FieldElement) x.getField().getOne();
        for (int i = 0; i < this.topDiagonal.size(); i++) {
            T[] dividedDifference = this.topDiagonal.get(i);
            for (int k = 0; k < value.length; k++) {
                value[k] = (FieldElement) value[k].add(dividedDifference[k].multiply(fieldElement));
            }
            fieldElement = (FieldElement) fieldElement.multiply((FieldElement) x.subtract(this.abscissae.get(i)));
        }
        return value;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v1, resolved type: T extends org.apache.commons.math3.FieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r11v19, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r12v6, resolved type: org.apache.commons.math3.FieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r11v27, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r11v28, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    public T[][] derivatives(T x, int order) throws NoDataException, NullArgumentException {
        MathUtils.checkNotNull(x);
        if (this.abscissae.isEmpty()) {
            throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
        }
        FieldElement fieldElement = (FieldElement) x.getField().getOne();
        FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(x.getField(), order + 1);
        fieldElementArr[0] = (FieldElement) x.getField().getZero();
        for (int i = 0; i < order; i++) {
            fieldElementArr[i + 1] = (FieldElement) fieldElementArr[i].add(fieldElement);
        }
        T[][] derivatives = (T[][]) ((FieldElement[][]) MathArrays.buildArray(x.getField(), order + 1, this.topDiagonal.get(0).length));
        FieldElement[] fieldElementArr2 = (FieldElement[]) MathArrays.buildArray(x.getField(), order + 1);
        fieldElementArr2[0] = (FieldElement) x.getField().getOne();
        for (int i2 = 0; i2 < this.topDiagonal.size(); i2++) {
            T[] dividedDifference = this.topDiagonal.get(i2);
            FieldElement fieldElement2 = (FieldElement) x.subtract(this.abscissae.get(i2));
            for (int j = order; j >= 0; j--) {
                for (int k = 0; k < derivatives[j].length; k++) {
                    derivatives[j][k] = (FieldElement) derivatives[j][k].add(dividedDifference[k].multiply(fieldElementArr2[j]));
                }
                fieldElementArr2[j] = (FieldElement) fieldElementArr2[j].multiply(fieldElement2);
                if (j > 0) {
                    fieldElementArr2[j] = (FieldElement) fieldElementArr2[j].add(fieldElementArr[j].multiply(fieldElementArr2[j - 1]));
                }
            }
        }
        return derivatives;
    }
}
