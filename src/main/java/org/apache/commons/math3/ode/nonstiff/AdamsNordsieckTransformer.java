package org.apache.commons.math3.ode.nonstiff;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public class AdamsNordsieckTransformer {
    private static final Map<Integer, AdamsNordsieckTransformer> CACHE = new HashMap();

    /* renamed from: c1 */
    private final double[] f262c1;
    private final Array2DRowRealMatrix update;

    private AdamsNordsieckTransformer(int n) {
        int rows = n - 1;
        FieldMatrix<BigFraction> bigP = buildP(rows);
        FieldDecompositionSolver<BigFraction> pSolver = new FieldLUDecomposition(bigP).getSolver();
        BigFraction[] u = new BigFraction[rows];
        Arrays.fill(u, BigFraction.ONE);
        BigFraction[] bigC1 = pSolver.solve(new ArrayFieldVector<>(u, false)).toArray();
        BigFraction[][] shiftedP = bigP.getData();
        for (int i = shiftedP.length - 1; i > 0; i--) {
            shiftedP[i] = shiftedP[i - 1];
        }
        shiftedP[0] = new BigFraction[rows];
        Arrays.fill(shiftedP[0], BigFraction.ZERO);
        this.update = MatrixUtils.bigFractionMatrixToRealMatrix(pSolver.solve(new Array2DRowFieldMatrix<>(shiftedP, false)));
        this.f262c1 = new double[rows];
        for (int i2 = 0; i2 < rows; i2++) {
            this.f262c1[i2] = bigC1[i2].doubleValue();
        }
    }

    public static AdamsNordsieckTransformer getInstance(int nSteps) {
        AdamsNordsieckTransformer t;
        synchronized (CACHE) {
            t = CACHE.get(Integer.valueOf(nSteps));
            if (t == null) {
                t = new AdamsNordsieckTransformer(nSteps);
                CACHE.put(Integer.valueOf(nSteps), t);
            }
        }
        return t;
    }

    @Deprecated
    public int getNSteps() {
        return this.f262c1.length;
    }

    private FieldMatrix<BigFraction> buildP(int rows) {
        BigFraction[][] pData = (BigFraction[][]) Array.newInstance(BigFraction.class, rows, rows);
        for (int i = 1; i <= pData.length; i++) {
            BigFraction[] pI = pData[i - 1];
            int factor = -i;
            int aj = factor;
            for (int j = 1; j <= pI.length; j++) {
                pI[j - 1] = new BigFraction((j + 1) * aj);
                aj *= factor;
            }
        }
        return new Array2DRowFieldMatrix((FieldElement[][]) pData, false);
    }

    public Array2DRowRealMatrix initializeHighOrderDerivatives(double h, double[] t, double[][] y, double[][] yDot) {
        double[][] a = (double[][]) Array.newInstance(Double.TYPE, this.f262c1.length + 1, this.f262c1.length + 1);
        double[][] b = (double[][]) Array.newInstance(Double.TYPE, this.f262c1.length + 1, y[0].length);
        double[] y0 = y[0];
        double[] yDot0 = yDot[0];
        for (int i = 1; i < y.length; i++) {
            double di = t[i] - t[0];
            double ratio = di / h;
            double dikM1Ohk = 1.0d / h;
            double[] aI = a[(i * 2) - 2];
            double[] aDotI = (i * 2) + -1 < a.length ? a[(i * 2) - 1] : null;
            for (int j = 0; j < aI.length; j++) {
                dikM1Ohk *= ratio;
                aI[j] = di * dikM1Ohk;
                if (aDotI != null) {
                    aDotI[j] = ((double) (j + 2)) * dikM1Ohk;
                }
            }
            double[] yI = y[i];
            double[] yDotI = yDot[i];
            double[] bI = b[(i * 2) - 2];
            double[] bDotI = (i * 2) + -1 < b.length ? b[(i * 2) - 1] : null;
            for (int j2 = 0; j2 < yI.length; j2++) {
                bI[j2] = (yI[j2] - y0[j2]) - (yDot0[j2] * di);
                if (bDotI != null) {
                    bDotI[j2] = yDotI[j2] - yDot0[j2];
                }
            }
        }
        RealMatrix x = new QRDecomposition(new Array2DRowRealMatrix(a, false)).getSolver().solve(new Array2DRowRealMatrix(b, false));
        Array2DRowRealMatrix truncatedX = new Array2DRowRealMatrix(x.getRowDimension() - 1, x.getColumnDimension());
        for (int i2 = 0; i2 < truncatedX.getRowDimension(); i2++) {
            for (int j3 = 0; j3 < truncatedX.getColumnDimension(); j3++) {
                truncatedX.setEntry(i2, j3, x.getEntry(i2, j3));
            }
        }
        return truncatedX;
    }

    public Array2DRowRealMatrix updateHighOrderDerivativesPhase1(Array2DRowRealMatrix highOrder) {
        return this.update.multiply(highOrder);
    }

    public void updateHighOrderDerivativesPhase2(double[] start, double[] end, Array2DRowRealMatrix highOrder) {
        double[][] data = highOrder.getDataRef();
        for (int i = 0; i < data.length; i++) {
            double[] dataI = data[i];
            double c1I = this.f262c1[i];
            for (int j = 0; j < dataI.length; j++) {
                dataI[j] = dataI[j] + ((start[j] - end[j]) * c1I);
            }
        }
    }
}
