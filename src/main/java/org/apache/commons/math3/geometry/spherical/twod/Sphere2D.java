package org.apache.commons.math3.geometry.spherical.twod;

import java.io.Serializable;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;

public class Sphere2D implements Serializable, Space {
    private static final long serialVersionUID = 20131218;

    private Sphere2D() {
    }

    public static Sphere2D getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override // org.apache.commons.math3.geometry.Space
    public int getDimension() {
        return 2;
    }

    @Override // org.apache.commons.math3.geometry.Space
    public Sphere1D getSubSpace() {
        return Sphere1D.getInstance();
    }

    /* access modifiers changed from: private */
    public static class LazyHolder {
        private static final Sphere2D INSTANCE = new Sphere2D();

        private LazyHolder() {
        }
    }

    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
}
