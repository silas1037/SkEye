package org.apache.commons.math3.optim.nonlinear.vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class MultiStartMultivariateVectorOptimizer extends BaseMultiStartMultivariateOptimizer<PointVectorValuePair> {
    private final List<PointVectorValuePair> optima = new ArrayList();
    private final MultivariateVectorOptimizer optimizer;

    public MultiStartMultivariateVectorOptimizer(MultivariateVectorOptimizer optimizer2, int starts, RandomVectorGenerator generator) throws NullArgumentException, NotStrictlyPositiveException {
        super(optimizer2, starts, generator);
        this.optimizer = optimizer2;
    }

    @Override // org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer
    public PointVectorValuePair[] getOptima() {
        Collections.sort(this.optima, getPairComparator());
        return (PointVectorValuePair[]) this.optima.toArray(new PointVectorValuePair[0]);
    }

    /* access modifiers changed from: protected */
    public void store(PointVectorValuePair optimum) {
        this.optima.add(optimum);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer
    public void clear() {
        this.optima.clear();
    }

    private Comparator<PointVectorValuePair> getPairComparator() {
        return new Comparator<PointVectorValuePair>() {
            /* class org.apache.commons.math3.optim.nonlinear.vector.MultiStartMultivariateVectorOptimizer.C03131 */
            private final RealVector target = new ArrayRealVector(MultiStartMultivariateVectorOptimizer.this.optimizer.getTarget(), false);
            private final RealMatrix weight = MultiStartMultivariateVectorOptimizer.this.optimizer.getWeight();

            public int compare(PointVectorValuePair o1, PointVectorValuePair o2) {
                if (o1 == null) {
                    return o2 == null ? 0 : 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return Double.compare(weightedResidual(o1), weightedResidual(o2));
            }

            private double weightedResidual(PointVectorValuePair pv) {
                RealVector r = this.target.subtract(new ArrayRealVector(pv.getValueRef(), false));
                return r.dotProduct(this.weight.operate(r));
            }
        };
    }
}
