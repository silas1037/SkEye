package org.apache.commons.math3.optim.nonlinear.scalar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.random.RandomVectorGenerator;

public class MultiStartMultivariateOptimizer extends BaseMultiStartMultivariateOptimizer<PointValuePair> {
    private final List<PointValuePair> optima = new ArrayList();
    private final MultivariateOptimizer optimizer;

    public MultiStartMultivariateOptimizer(MultivariateOptimizer optimizer2, int starts, RandomVectorGenerator generator) throws NullArgumentException, NotStrictlyPositiveException {
        super(optimizer2, starts, generator);
        this.optimizer = optimizer2;
    }

    @Override // org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer
    public PointValuePair[] getOptima() {
        Collections.sort(this.optima, getPairComparator());
        return (PointValuePair[]) this.optima.toArray(new PointValuePair[0]);
    }

    /* access modifiers changed from: protected */
    public void store(PointValuePair optimum) {
        this.optima.add(optimum);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseMultiStartMultivariateOptimizer
    public void clear() {
        this.optima.clear();
    }

    private Comparator<PointValuePair> getPairComparator() {
        return new Comparator<PointValuePair>() {
            /* class org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer.C03081 */

            public int compare(PointValuePair o1, PointValuePair o2) {
                if (o1 == null) {
                    return o2 == null ? 0 : 1;
                }
                if (o2 == null) {
                    return -1;
                }
                double v1 = ((Double) o1.getValue()).doubleValue();
                double v2 = ((Double) o2.getValue()).doubleValue();
                return MultiStartMultivariateOptimizer.this.optimizer.getGoalType() == GoalType.MINIMIZE ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        };
    }
}
