package org.apache.commons.math3.stat.descriptive.rank;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.util.KthSelector;

public class Median extends Percentile implements Serializable {
    private static final double FIXED_QUANTILE_50 = 50.0d;
    private static final long serialVersionUID = -3961477041290915687L;

    public Median() {
        super((double) FIXED_QUANTILE_50);
    }

    public Median(Median original) throws NullArgumentException {
        super(original);
    }

    private Median(Percentile.EstimationType estimationType, NaNStrategy nanStrategy, KthSelector kthSelector) throws MathIllegalArgumentException {
        super(FIXED_QUANTILE_50, estimationType, nanStrategy, kthSelector);
    }

    @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile
    public Median withEstimationType(Percentile.EstimationType newEstimationType) {
        return new Median(newEstimationType, getNaNStrategy(), getKthSelector());
    }

    @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile
    public Median withNaNStrategy(NaNStrategy newNaNStrategy) {
        return new Median(getEstimationType(), newNaNStrategy, getKthSelector());
    }

    @Override // org.apache.commons.math3.stat.descriptive.rank.Percentile
    public Median withKthSelector(KthSelector newKthSelector) {
        return new Median(getEstimationType(), getNaNStrategy(), newKthSelector);
    }
}
