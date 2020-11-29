package org.apache.commons.math3.linear;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;

public class FieldLUDecomposition<T extends FieldElement<T>> {
    private FieldMatrix<T> cachedL;
    private FieldMatrix<T> cachedP;
    private FieldMatrix<T> cachedU;
    private boolean even;
    private final Field<T> field;

    /* renamed from: lu */
    private T[][] f219lu;
    private int[] pivot;
    private boolean singular;

    public FieldLUDecomposition(FieldMatrix<T> matrix) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        int m = matrix.getColumnDimension();
        this.field = matrix.getField();
        this.f219lu = matrix.getData();
        this.pivot = new int[m];
        this.cachedL = null;
        this.cachedU = null;
        this.cachedP = null;
        for (int row = 0; row < m; row++) {
            this.pivot[row] = row;
        }
        this.even = true;
        this.singular = false;
        for (int col = 0; col < m; col++) {
            this.field.getZero();
            for (int row2 = 0; row2 < col; row2++) {
                FieldElement[] fieldElementArr = this.f219lu[row2];
                FieldElement fieldElement = fieldElementArr[col];
                for (int i = 0; i < row2; i++) {
                    fieldElement = (FieldElement) fieldElement.subtract(fieldElementArr[i].multiply(this.f219lu[i][col]));
                }
                fieldElementArr[col] = fieldElement;
            }
            int nonZero = col;
            for (int row3 = col; row3 < m; row3++) {
                FieldElement[] fieldElementArr2 = this.f219lu[row3];
                FieldElement fieldElement2 = fieldElementArr2[col];
                for (int i2 = 0; i2 < col; i2++) {
                    fieldElement2 = (FieldElement) fieldElement2.subtract(fieldElementArr2[i2].multiply(this.f219lu[i2][col]));
                }
                fieldElementArr2[col] = fieldElement2;
                if (this.f219lu[nonZero][col].equals(this.field.getZero())) {
                    nonZero++;
                }
            }
            if (nonZero >= m) {
                this.singular = true;
                return;
            }
            if (nonZero != col) {
                this.field.getZero();
                for (int i3 = 0; i3 < m; i3++) {
                    T tmp = this.f219lu[nonZero][i3];
                    this.f219lu[nonZero][i3] = this.f219lu[col][i3];
                    this.f219lu[col][i3] = tmp;
                }
                int temp = this.pivot[nonZero];
                this.pivot[nonZero] = this.pivot[col];
                this.pivot[col] = temp;
                this.even = !this.even;
            }
            T luDiag = this.f219lu[col][col];
            for (int row4 = col + 1; row4 < m; row4++) {
                FieldElement[] fieldElementArr3 = this.f219lu[row4];
                fieldElementArr3[col] = (FieldElement) fieldElementArr3[col].divide(luDiag);
            }
        }
    }

    public FieldMatrix<T> getL() {
        if (this.cachedL == null && !this.singular) {
            int m = this.pivot.length;
            this.cachedL = new Array2DRowFieldMatrix(this.field, m, m);
            for (int i = 0; i < m; i++) {
                T[] luI = this.f219lu[i];
                for (int j = 0; j < i; j++) {
                    this.cachedL.setEntry(i, j, luI[j]);
                }
                this.cachedL.setEntry(i, i, this.field.getOne());
            }
        }
        return this.cachedL;
    }

    public FieldMatrix<T> getU() {
        if (this.cachedU == null && !this.singular) {
            int m = this.pivot.length;
            this.cachedU = new Array2DRowFieldMatrix(this.field, m, m);
            for (int i = 0; i < m; i++) {
                T[] luI = this.f219lu[i];
                for (int j = i; j < m; j++) {
                    this.cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return this.cachedU;
    }

    public FieldMatrix<T> getP() {
        if (this.cachedP == null && !this.singular) {
            int m = this.pivot.length;
            this.cachedP = new Array2DRowFieldMatrix(this.field, m, m);
            for (int i = 0; i < m; i++) {
                this.cachedP.setEntry(i, this.pivot[i], this.field.getOne());
            }
        }
        return this.cachedP;
    }

    public int[] getPivot() {
        return (int[]) this.pivot.clone();
    }

    public T getDeterminant() {
        if (this.singular) {
            return this.field.getZero();
        }
        int m = this.pivot.length;
        T one = this.even ? this.field.getOne() : (FieldElement) this.field.getZero().subtract(this.field.getOne());
        for (int i = 0; i < m; i++) {
            one = (T) ((FieldElement) one.multiply(this.f219lu[i][i]));
        }
        return (T) one;
    }

    public FieldDecompositionSolver<T> getSolver() {
        return new Solver(this.field, this.f219lu, this.pivot, this.singular);
    }

    private static class Solver<T extends FieldElement<T>> implements FieldDecompositionSolver<T> {
        private final Field<T> field;

        /* renamed from: lu */
        private final T[][] f220lu;
        private final int[] pivot;
        private final boolean singular;

        private Solver(Field<T> field2, T[][] lu, int[] pivot2, boolean singular2) {
            this.field = field2;
            this.f220lu = lu;
            this.pivot = pivot2;
            this.singular = singular2;
        }

        @Override // org.apache.commons.math3.linear.FieldDecompositionSolver
        public boolean isNonSingular() {
            return !this.singular;
        }

        @Override // org.apache.commons.math3.linear.FieldDecompositionSolver
        public FieldVector<T> solve(FieldVector<T> b) {
            try {
                return solve((ArrayFieldVector) ((ArrayFieldVector) b));
            } catch (ClassCastException e) {
                int m = this.pivot.length;
                if (b.getDimension() != m) {
                    throw new DimensionMismatchException(b.getDimension(), m);
                } else if (this.singular) {
                    throw new SingularMatrixException();
                } else {
                    FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, m);
                    for (int row = 0; row < m; row++) {
                        fieldElementArr[row] = b.getEntry(this.pivot[row]);
                    }
                    for (int col = 0; col < m; col++) {
                        FieldElement fieldElement = fieldElementArr[col];
                        for (int i = col + 1; i < m; i++) {
                            fieldElementArr[i] = (FieldElement) fieldElementArr[i].subtract(fieldElement.multiply(this.f220lu[i][col]));
                        }
                    }
                    for (int col2 = m - 1; col2 >= 0; col2--) {
                        fieldElementArr[col2] = (FieldElement) fieldElementArr[col2].divide(this.f220lu[col2][col2]);
                        FieldElement fieldElement2 = fieldElementArr[col2];
                        for (int i2 = 0; i2 < col2; i2++) {
                            fieldElementArr[i2] = (FieldElement) fieldElementArr[i2].subtract(fieldElement2.multiply(this.f220lu[i2][col2]));
                        }
                    }
                    return new ArrayFieldVector((Field) this.field, fieldElementArr, false);
                }
            }
        }

        public ArrayFieldVector<T> solve(ArrayFieldVector<T> b) {
            int m = this.pivot.length;
            int length = b.getDimension();
            if (length != m) {
                throw new DimensionMismatchException(length, m);
            } else if (this.singular) {
                throw new SingularMatrixException();
            } else {
                FieldElement[] fieldElementArr = (FieldElement[]) MathArrays.buildArray(this.field, m);
                for (int row = 0; row < m; row++) {
                    fieldElementArr[row] = b.getEntry(this.pivot[row]);
                }
                for (int col = 0; col < m; col++) {
                    FieldElement fieldElement = fieldElementArr[col];
                    for (int i = col + 1; i < m; i++) {
                        fieldElementArr[i] = (FieldElement) fieldElementArr[i].subtract(fieldElement.multiply(this.f220lu[i][col]));
                    }
                }
                for (int col2 = m - 1; col2 >= 0; col2--) {
                    fieldElementArr[col2] = (FieldElement) fieldElementArr[col2].divide(this.f220lu[col2][col2]);
                    FieldElement fieldElement2 = fieldElementArr[col2];
                    for (int i2 = 0; i2 < col2; i2++) {
                        fieldElementArr[i2] = (FieldElement) fieldElementArr[i2].subtract(fieldElement2.multiply(this.f220lu[i2][col2]));
                    }
                }
                return new ArrayFieldVector<>(fieldElementArr, false);
            }
        }

        @Override // org.apache.commons.math3.linear.FieldDecompositionSolver
        public FieldMatrix<T> solve(FieldMatrix<T> b) {
            int m = this.pivot.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            } else if (this.singular) {
                throw new SingularMatrixException();
            } else {
                int nColB = b.getColumnDimension();
                FieldElement[][] fieldElementArr = (FieldElement[][]) MathArrays.buildArray(this.field, m, nColB);
                for (int row = 0; row < m; row++) {
                    FieldElement[] fieldElementArr2 = fieldElementArr[row];
                    int pRow = this.pivot[row];
                    for (int col = 0; col < nColB; col++) {
                        fieldElementArr2[col] = b.getEntry(pRow, col);
                    }
                }
                for (int col2 = 0; col2 < m; col2++) {
                    FieldElement[] fieldElementArr3 = fieldElementArr[col2];
                    for (int i = col2 + 1; i < m; i++) {
                        FieldElement[] fieldElementArr4 = fieldElementArr[i];
                        T luICol = this.f220lu[i][col2];
                        for (int j = 0; j < nColB; j++) {
                            fieldElementArr4[j] = (FieldElement) fieldElementArr4[j].subtract(fieldElementArr3[j].multiply(luICol));
                        }
                    }
                }
                for (int col3 = m - 1; col3 >= 0; col3--) {
                    FieldElement[] fieldElementArr5 = fieldElementArr[col3];
                    T luDiag = this.f220lu[col3][col3];
                    for (int j2 = 0; j2 < nColB; j2++) {
                        fieldElementArr5[j2] = (FieldElement) fieldElementArr5[j2].divide(luDiag);
                    }
                    for (int i2 = 0; i2 < col3; i2++) {
                        FieldElement[] fieldElementArr6 = fieldElementArr[i2];
                        T luICol2 = this.f220lu[i2][col3];
                        for (int j3 = 0; j3 < nColB; j3++) {
                            fieldElementArr6[j3] = (FieldElement) fieldElementArr6[j3].subtract(fieldElementArr5[j3].multiply(luICol2));
                        }
                    }
                }
                return new Array2DRowFieldMatrix((Field) this.field, fieldElementArr, false);
            }
        }

        @Override // org.apache.commons.math3.linear.FieldDecompositionSolver
        public FieldMatrix<T> getInverse() {
            int m = this.pivot.length;
            T one = this.field.getOne();
            FieldMatrix<T> identity = new Array2DRowFieldMatrix<>(this.field, m, m);
            for (int i = 0; i < m; i++) {
                identity.setEntry(i, i, one);
            }
            return solve(identity);
        }
    }
}
