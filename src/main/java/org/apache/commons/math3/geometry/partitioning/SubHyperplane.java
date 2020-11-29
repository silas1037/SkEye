package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public interface SubHyperplane<S extends Space> {
    SubHyperplane<S> copySelf();

    Hyperplane<S> getHyperplane();

    double getSize();

    boolean isEmpty();

    SubHyperplane<S> reunite(SubHyperplane<S> subHyperplane);

    @Deprecated
    Side side(Hyperplane<S> hyperplane);

    SplitSubHyperplane<S> split(Hyperplane<S> hyperplane);

    public static class SplitSubHyperplane<U extends Space> {
        private final SubHyperplane<U> minus;
        private final SubHyperplane<U> plus;

        public SplitSubHyperplane(SubHyperplane<U> plus2, SubHyperplane<U> minus2) {
            this.plus = plus2;
            this.minus = minus2;
        }

        public SubHyperplane<U> getPlus() {
            return this.plus;
        }

        public SubHyperplane<U> getMinus() {
            return this.minus;
        }

        public Side getSide() {
            if (this.plus == null || this.plus.isEmpty()) {
                if (this.minus == null || this.minus.isEmpty()) {
                    return Side.HYPER;
                }
                return Side.MINUS;
            } else if (this.minus == null || this.minus.isEmpty()) {
                return Side.PLUS;
            } else {
                return Side.BOTH;
            }
        }
    }
}
