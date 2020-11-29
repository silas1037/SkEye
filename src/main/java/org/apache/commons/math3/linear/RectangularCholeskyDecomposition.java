package org.apache.commons.math3.linear;

import java.lang.reflect.Array;
import org.apache.commons.math3.util.FastMath;

public class RectangularCholeskyDecomposition {
    private int rank;
    private final RealMatrix root;

    public RectangularCholeskyDecomposition(RealMatrix matrix) throws NonPositiveDefiniteMatrixException {
        this(matrix, 0.0d);
    }

    public RectangularCholeskyDecomposition(RealMatrix matrix, double small) throws NonPositiveDefiniteMatrixException {
        int order = matrix.getRowDimension();
        double[][] c = matrix.getData();
        double[][] b = (double[][]) Array.newInstance(Double.TYPE, order, order);
        int[] index = new int[order];
        for (int i = 0; i < order; i++) {
            index[i] = i;
        }
        int r = 0;
        boolean loop = true;
        while (loop) {
            int swapR = r;
            for (int i2 = r + 1; i2 < order; i2++) {
                int ii = index[i2];
                int isr = index[swapR];
                if (c[ii][ii] > c[isr][isr]) {
                    swapR = i2;
                }
            }
            if (swapR != r) {
                int tmpIndex = index[r];
                index[r] = index[swapR];
                index[swapR] = tmpIndex;
                double[] tmpRow = b[r];
                b[r] = b[swapR];
                b[swapR] = tmpRow;
            }
            int ir = index[r];
            if (c[ir][ir] > small) {
                double sqrt = FastMath.sqrt(c[ir][ir]);
                b[r][r] = sqrt;
                double inverse = 1.0d / sqrt;
                double inverse2 = 1.0d / c[ir][ir];
                for (int i3 = r + 1; i3 < order; i3++) {
                    int ii2 = index[i3];
                    double e = inverse * c[ii2][ir];
                    b[i3][r] = e;
                    double[] dArr = c[ii2];
                    dArr[ii2] = dArr[ii2] - ((c[ii2][ir] * c[ii2][ir]) * inverse2);
                    for (int j = r + 1; j < i3; j++) {
                        int ij = index[j];
                        double f = c[ii2][ij] - (b[j][r] * e);
                        c[ii2][ij] = f;
                        c[ij][ii2] = f;
                    }
                }
                r++;
                loop = r < order;
            } else if (r == 0) {
                throw new NonPositiveDefiniteMatrixException(c[ir][ir], ir, small);
            } else {
                for (int i4 = r; i4 < order; i4++) {
                    if (c[index[i4]][index[i4]] < (-small)) {
                        throw new NonPositiveDefiniteMatrixException(c[index[i4]][index[i4]], i4, small);
                    }
                }
                loop = false;
            }
        }
        this.rank = r;
        this.root = MatrixUtils.createRealMatrix(order, r);
        for (int i5 = 0; i5 < order; i5++) {
            for (int j2 = 0; j2 < r; j2++) {
                this.root.setEntry(index[i5], j2, b[i5][j2]);
            }
        }
    }

    public RealMatrix getRootMatrix() {
        return this.root;
    }

    public int getRank() {
        return this.rank;
    }
}
