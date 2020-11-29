package org.apache.commons.math3.geometry.euclidean.twod;

import java.io.Serializable;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;

public class Euclidean2D implements Serializable, Space {
    private static final long serialVersionUID = 4793432849757649566L;

    private Euclidean2D() {
    }

    public static Euclidean2D getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override // org.apache.commons.math3.geometry.Space
    public int getDimension() {
        return 2;
    }

    @Override // org.apache.commons.math3.geometry.Space
    public Euclidean1D getSubSpace() {
        return Euclidean1D.getInstance();
    }

    private static class LazyHolder {
        private static final Euclidean2D INSTANCE = new Euclidean2D();

        private LazyHolder() {
        }
    }

    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
}
