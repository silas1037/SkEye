package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.util.MathArrays;

public class AdamsNordsieckFieldTransformer<T extends RealFieldElement<T>> {
    private static final Map<Integer, Map<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>>> CACHE = new HashMap();

    /* renamed from: c1 */
    private final T[] f261c1;
    private final Field<T> field;
    private final Array2DRowFieldMatrix<T> update;

    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r6v9, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r6v12, resolved type: java.lang.Object[] */
    /* JADX WARN: Multi-variable type inference failed */
    private AdamsNordsieckFieldTransformer(Field<T> field2, int n) {
        this.field = field2;
        int rows = n - 1;
        FieldMatrix<T> bigP = buildP(rows);
        FieldDecompositionSolver<T> pSolver = new FieldLUDecomposition(bigP).getSolver();
        RealFieldElement[] realFieldElementArr = (RealFieldElement[]) MathArrays.buildArray(field2, rows);
        Arrays.fill(realFieldElementArr, field2.getOne());
        this.f261c1 = pSolver.solve(new ArrayFieldVector<>((T[]) realFieldElementArr, false)).toArray();
        T[][] shiftedP = bigP.getData();
        for (int i = shiftedP.length - 1; i > 0; i--) {
            shiftedP[i] = shiftedP[i - 1];
        }
        shiftedP[0] = (RealFieldElement[]) MathArrays.buildArray(field2, rows);
        Arrays.fill(shiftedP[0], field2.getZero());
        this.update = new Array2DRowFieldMatrix<>(pSolver.solve(new Array2DRowFieldMatrix<>(shiftedP, false)).getData());
    }

    public static <T extends RealFieldElement<T>> AdamsNordsieckFieldTransformer<T> getInstance(Field<T> field2, int nSteps) {
        AdamsNordsieckFieldTransformer t;
        synchronized (CACHE) {
            Map<Field<? extends RealFieldElement<?>>, AdamsNordsieckFieldTransformer<? extends RealFieldElement<?>>> map = CACHE.get(Integer.valueOf(nSteps));
            if (map == null) {
                map = new HashMap<>();
                CACHE.put(Integer.valueOf(nSteps), map);
            }
            t = (AdamsNordsieckFieldTransformer<T>) map.get(field2);
            if (t == null) {
                t = (AdamsNordsieckFieldTransformer<T>) new AdamsNordsieckFieldTransformer(field2, nSteps);
                map.put(field2, t);
            }
        }
        return (AdamsNordsieckFieldTransformer<T>) t;
    }

    private FieldMatrix<T> buildP(int rows) {
        RealFieldElement[][] realFieldElementArr = (RealFieldElement[][]) MathArrays.buildArray(this.field, rows, rows);
        for (int i = 1; i <= realFieldElementArr.length; i++) {
            RealFieldElement[] realFieldElementArr2 = realFieldElementArr[i - 1];
            int factor = -i;
            RealFieldElement realFieldElement = (RealFieldElement) this.field.getZero().add((double) factor);
            for (int j = 1; j <= realFieldElementArr2.length; j++) {
                realFieldElementArr2[j - 1] = (RealFieldElement) realFieldElement.multiply(j + 1);
                realFieldElement = (RealFieldElement) realFieldElement.multiply(factor);
            }
        }
        return new Array2DRowFieldMatrix((FieldElement[][]) realFieldElementArr, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r15v0, resolved type: org.apache.commons.math3.linear.Array2DRowFieldMatrix<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public Array2DRowFieldMatrix<T> initializeHighOrderDerivatives(T h, T[] t, T[][] y, T[][] yDot) {
        RealFieldElement[][] realFieldElementArr = (RealFieldElement[][]) MathArrays.buildArray(this.field, this.f261c1.length + 1, this.f261c1.length + 1);
        RealFieldElement[][] realFieldElementArr2 = (RealFieldElement[][]) MathArrays.buildArray(this.field, this.f261c1.length + 1, y[0].length);
        T[] y0 = y[0];
        T[] yDot0 = yDot[0];
        for (int i = 1; i < y.length; i++) {
            RealFieldElement realFieldElement = (RealFieldElement) t[i].subtract(t[0]);
            RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElement.divide(h);
            RealFieldElement realFieldElement3 = (RealFieldElement) h.reciprocal();
            RealFieldElement[] realFieldElementArr3 = realFieldElementArr[(i * 2) - 2];
            RealFieldElement[] realFieldElementArr4 = (i * 2) + -1 < realFieldElementArr.length ? realFieldElementArr[(i * 2) - 1] : null;
            for (int j = 0; j < realFieldElementArr3.length; j++) {
                realFieldElement3 = (RealFieldElement) realFieldElement3.multiply(realFieldElement2);
                realFieldElementArr3[j] = (RealFieldElement) realFieldElement.multiply(realFieldElement3);
                if (realFieldElementArr4 != null) {
                    realFieldElementArr4[j] = (RealFieldElement) realFieldElement3.multiply(j + 2);
                }
            }
            T[] yI = y[i];
            T[] yDotI = yDot[i];
            RealFieldElement[] realFieldElementArr5 = realFieldElementArr2[(i * 2) - 2];
            RealFieldElement[] realFieldElementArr6 = (i * 2) + -1 < realFieldElementArr2.length ? realFieldElementArr2[(i * 2) - 1] : null;
            for (int j2 = 0; j2 < yI.length; j2++) {
                realFieldElementArr5[j2] = (RealFieldElement) ((RealFieldElement) yI[j2].subtract(y0[j2])).subtract(realFieldElement.multiply(yDot0[j2]));
                if (realFieldElementArr6 != null) {
                    realFieldElementArr6[j2] = (RealFieldElement) yDotI[j2].subtract(yDot0[j2]);
                }
            }
        }
        FieldMatrix<T> x = new FieldLUDecomposition<>(new Array2DRowFieldMatrix((FieldElement[][]) realFieldElementArr, false)).getSolver().solve(new Array2DRowFieldMatrix<>((T[][]) realFieldElementArr2, false));
        Array2DRowFieldMatrix<T> truncatedX = (Array2DRowFieldMatrix<T>) new Array2DRowFieldMatrix(this.field, x.getRowDimension() - 1, x.getColumnDimension());
        for (int i2 = 0; i2 < truncatedX.getRowDimension(); i2++) {
            for (int j3 = 0; j3 < truncatedX.getColumnDimension(); j3++) {
                truncatedX.setEntry(i2, j3, x.getEntry(i2, j3));
            }
        }
        return truncatedX;
    }

    public Array2DRowFieldMatrix<T> updateHighOrderDerivativesPhase1(Array2DRowFieldMatrix<T> highOrder) {
        return this.update.multiply(highOrder);
    }

    public void updateHighOrderDerivativesPhase2(T[] start, T[] end, Array2DRowFieldMatrix<T> highOrder) {
        T[][] dataRef = highOrder.getDataRef();
        for (int i = 0; i < dataRef.length; i++) {
            RealFieldElement[] realFieldElementArr = dataRef[i];
            T c1I = this.f261c1[i];
            for (int j = 0; j < realFieldElementArr.length; j++) {
                realFieldElementArr[j] = (RealFieldElement) realFieldElementArr[j].add(c1I.multiply(start[j].subtract(end[j])));
            }
        }
    }
}
