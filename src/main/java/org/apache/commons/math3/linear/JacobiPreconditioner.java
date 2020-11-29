package org.apache.commons.math3.linear;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.util.MathArrays;

public class JacobiPreconditioner extends RealLinearOperator {
    private final ArrayRealVector diag;

    public JacobiPreconditioner(double[] diag2, boolean deep) {
        this.diag = new ArrayRealVector(diag2, deep);
    }

    public static JacobiPreconditioner create(RealLinearOperator a) throws NonSquareOperatorException {
        int n = a.getColumnDimension();
        if (a.getRowDimension() != n) {
            throw new NonSquareOperatorException(a.getRowDimension(), n);
        }
        double[] diag2 = new double[n];
        if (a instanceof AbstractRealMatrix) {
            AbstractRealMatrix m = (AbstractRealMatrix) a;
            for (int i = 0; i < n; i++) {
                diag2[i] = m.getEntry(i, i);
            }
        } else {
            ArrayRealVector x = new ArrayRealVector(n);
            for (int i2 = 0; i2 < n; i2++) {
                x.set(0.0d);
                x.setEntry(i2, 1.0d);
                diag2[i2] = a.operate(x).getEntry(i2);
            }
        }
        return new JacobiPreconditioner(diag2, false);
    }

    @Override // org.apache.commons.math3.linear.RealLinearOperator
    public int getColumnDimension() {
        return this.diag.getDimension();
    }

    @Override // org.apache.commons.math3.linear.RealLinearOperator
    public int getRowDimension() {
        return this.diag.getDimension();
    }

    @Override // org.apache.commons.math3.linear.RealLinearOperator
    public RealVector operate(RealVector x) {
        return new ArrayRealVector(MathArrays.ebeDivide(x.toArray(), this.diag.toArray()), false);
    }

    public RealLinearOperator sqrt() {
        final RealVector sqrtDiag = this.diag.map((UnivariateFunction) new Sqrt());
        return new RealLinearOperator() {
            /* class org.apache.commons.math3.linear.JacobiPreconditioner.C02701 */

            @Override // org.apache.commons.math3.linear.RealLinearOperator
            public RealVector operate(RealVector x) {
                return new ArrayRealVector(MathArrays.ebeDivide(x.toArray(), sqrtDiag.toArray()), false);
            }

            @Override // org.apache.commons.math3.linear.RealLinearOperator
            public int getRowDimension() {
                return sqrtDiag.getDimension();
            }

            @Override // org.apache.commons.math3.linear.RealLinearOperator
            public int getColumnDimension() {
                return sqrtDiag.getDimension();
            }
        };
    }
}
