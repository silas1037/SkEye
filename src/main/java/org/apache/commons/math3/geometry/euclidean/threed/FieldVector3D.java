package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class FieldVector3D<T extends RealFieldElement<T>> implements Serializable {
    private static final long serialVersionUID = 20130224;

    /* renamed from: x */
    private final T f186x;

    /* renamed from: y */
    private final T f187y;

    /* renamed from: z */
    private final T f188z;

    public FieldVector3D(T x, T y, T z) {
        this.f186x = x;
        this.f187y = y;
        this.f188z = z;
    }

    public FieldVector3D(T[] v) throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.f186x = v[0];
        this.f187y = v[1];
        this.f188z = v[2];
    }

    public FieldVector3D(T alpha, T delta) {
        RealFieldElement realFieldElement = (RealFieldElement) delta.cos();
        this.f186x = (T) ((RealFieldElement) ((RealFieldElement) alpha.cos()).multiply(realFieldElement));
        this.f187y = (T) ((RealFieldElement) ((RealFieldElement) alpha.sin()).multiply(realFieldElement));
        this.f188z = (T) ((RealFieldElement) delta.sin());
    }

    public FieldVector3D(T a, FieldVector3D<T> u) {
        this.f186x = (T) ((RealFieldElement) a.multiply(u.f186x));
        this.f187y = (T) ((RealFieldElement) a.multiply(u.f187y));
        this.f188z = (T) ((RealFieldElement) a.multiply(u.f188z));
    }

    public FieldVector3D(T a, Vector3D u) {
        this.f186x = (T) ((RealFieldElement) a.multiply(u.getX()));
        this.f187y = (T) ((RealFieldElement) a.multiply(u.getY()));
        this.f188z = (T) ((RealFieldElement) a.multiply(u.getZ()));
    }

    public FieldVector3D(double a, FieldVector3D<T> u) {
        this.f186x = (T) ((RealFieldElement) u.f186x.multiply(a));
        this.f187y = (T) ((RealFieldElement) u.f187y.multiply(a));
        this.f188z = (T) ((RealFieldElement) u.f188z.multiply(a));
    }

    public FieldVector3D(T a1, FieldVector3D<T> u1, T a2, FieldVector3D<T> u2) {
        this.f186x = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getX(), a2, u2.getX()));
        this.f187y = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getY(), a2, u2.getY()));
        this.f188z = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getZ(), a2, u2.getZ()));
    }

    public FieldVector3D(T a1, Vector3D u1, T a2, Vector3D u2) {
        this.f186x = (T) ((RealFieldElement) a1.linearCombination(u1.getX(), a1, u2.getX(), a2));
        this.f187y = (T) ((RealFieldElement) a1.linearCombination(u1.getY(), a1, u2.getY(), a2));
        this.f188z = (T) ((RealFieldElement) a1.linearCombination(u1.getZ(), a1, u2.getZ(), a2));
    }

    public FieldVector3D(double a1, FieldVector3D<T> u1, double a2, FieldVector3D<T> u2) {
        T prototype = u1.getX();
        this.f186x = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getX(), a2, u2.getX()));
        this.f187y = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getY(), a2, u2.getY()));
        this.f188z = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ()));
    }

    public FieldVector3D(T a1, FieldVector3D<T> u1, T a2, FieldVector3D<T> u2, T a3, FieldVector3D<T> u3) {
        this.f186x = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX()));
        this.f187y = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY()));
        this.f188z = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ()));
    }

    public FieldVector3D(T a1, Vector3D u1, T a2, Vector3D u2, T a3, Vector3D u3) {
        this.f186x = (T) ((RealFieldElement) a1.linearCombination(u1.getX(), a1, u2.getX(), a2, u3.getX(), a3));
        this.f187y = (T) ((RealFieldElement) a1.linearCombination(u1.getY(), a1, u2.getY(), a2, u3.getY(), a3));
        this.f188z = (T) ((RealFieldElement) a1.linearCombination(u1.getZ(), a1, u2.getZ(), a2, u3.getZ(), a3));
    }

    public FieldVector3D(double a1, FieldVector3D<T> u1, double a2, FieldVector3D<T> u2, double a3, FieldVector3D<T> u3) {
        T prototype = u1.getX();
        this.f186x = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX()));
        this.f187y = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY()));
        this.f188z = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ()));
    }

    public FieldVector3D(T a1, FieldVector3D<T> u1, T a2, FieldVector3D<T> u2, T a3, FieldVector3D<T> u3, T a4, FieldVector3D<T> u4) {
        this.f186x = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX(), a4, u4.getX()));
        this.f187y = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY(), a4, u4.getY()));
        this.f188z = (T) ((RealFieldElement) a1.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ(), a4, u4.getZ()));
    }

    public FieldVector3D(T a1, Vector3D u1, T a2, Vector3D u2, T a3, Vector3D u3, T a4, Vector3D u4) {
        this.f186x = (T) ((RealFieldElement) a1.linearCombination(u1.getX(), a1, u2.getX(), a2, u3.getX(), a3, u4.getX(), a4));
        this.f187y = (T) ((RealFieldElement) a1.linearCombination(u1.getY(), a1, u2.getY(), a2, u3.getY(), a3, u4.getY(), a4));
        this.f188z = (T) ((RealFieldElement) a1.linearCombination(u1.getZ(), a1, u2.getZ(), a2, u3.getZ(), a3, u4.getZ(), a4));
    }

    public FieldVector3D(double a1, FieldVector3D<T> u1, double a2, FieldVector3D<T> u2, double a3, FieldVector3D<T> u3, double a4, FieldVector3D<T> u4) {
        T prototype = u1.getX();
        this.f186x = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX(), a4, u4.getX()));
        this.f187y = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY(), a4, u4.getY()));
        this.f188z = (T) ((RealFieldElement) prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ(), a4, u4.getZ()));
    }

    public T getX() {
        return this.f186x;
    }

    public T getY() {
        return this.f187y;
    }

    public T getZ() {
        return this.f188z;
    }

    public T[] toArray() {
        T[] array = (T[]) ((RealFieldElement[]) MathArrays.buildArray(this.f186x.getField(), 3));
        array[0] = this.f186x;
        array[1] = this.f187y;
        array[2] = this.f188z;
        return array;
    }

    public Vector3D toVector3D() {
        return new Vector3D(this.f186x.getReal(), this.f187y.getReal(), this.f188z.getReal());
    }

    public T getNorm1() {
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f186x.abs()).add(this.f187y.abs())).add(this.f188z.abs()));
    }

    public T getNorm() {
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f186x.multiply(this.f186x)).add(this.f187y.multiply(this.f187y))).add(this.f188z.multiply(this.f188z))).sqrt());
    }

    public T getNormSq() {
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f186x.multiply(this.f186x)).add(this.f187y.multiply(this.f187y))).add(this.f188z.multiply(this.f188z)));
    }

    public T getNormInf() {
        T xAbs = (T) ((RealFieldElement) this.f186x.abs());
        T yAbs = (T) ((RealFieldElement) this.f187y.abs());
        T zAbs = (T) ((RealFieldElement) this.f188z.abs());
        return xAbs.getReal() <= yAbs.getReal() ? yAbs.getReal() <= zAbs.getReal() ? zAbs : yAbs : xAbs.getReal() > zAbs.getReal() ? xAbs : zAbs;
    }

    public T getAlpha() {
        return (T) ((RealFieldElement) this.f187y.atan2(this.f186x));
    }

    public T getDelta() {
        return (T) ((RealFieldElement) ((RealFieldElement) this.f188z.divide(getNorm())).asin());
    }

    public FieldVector3D<T> add(FieldVector3D<T> v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.add(v.f186x), (RealFieldElement) this.f187y.add(v.f187y), (RealFieldElement) this.f188z.add(v.f188z));
    }

    public FieldVector3D<T> add(Vector3D v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.add(v.getX()), (RealFieldElement) this.f187y.add(v.getY()), (RealFieldElement) this.f188z.add(v.getZ()));
    }

    public FieldVector3D<T> add(T factor, FieldVector3D<T> v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.getField().getOne(), this, factor, v);
    }

    public FieldVector3D<T> add(T factor, Vector3D v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.add(factor.multiply(v.getX())), (RealFieldElement) this.f187y.add(factor.multiply(v.getY())), (RealFieldElement) this.f188z.add(factor.multiply(v.getZ())));
    }

    public FieldVector3D<T> add(double factor, FieldVector3D<T> v) {
        return new FieldVector3D<>(1.0d, this, factor, v);
    }

    public FieldVector3D<T> add(double factor, Vector3D v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.add(v.getX() * factor), (RealFieldElement) this.f187y.add(v.getY() * factor), (RealFieldElement) this.f188z.add(v.getZ() * factor));
    }

    public FieldVector3D<T> subtract(FieldVector3D<T> v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.subtract(v.f186x), (RealFieldElement) this.f187y.subtract(v.f187y), (RealFieldElement) this.f188z.subtract(v.f188z));
    }

    public FieldVector3D<T> subtract(Vector3D v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.subtract(v.getX()), (RealFieldElement) this.f187y.subtract(v.getY()), (RealFieldElement) this.f188z.subtract(v.getZ()));
    }

    public FieldVector3D<T> subtract(T factor, FieldVector3D<T> v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.getField().getOne(), this, (RealFieldElement) factor.negate(), v);
    }

    public FieldVector3D<T> subtract(T factor, Vector3D v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.subtract(factor.multiply(v.getX())), (RealFieldElement) this.f187y.subtract(factor.multiply(v.getY())), (RealFieldElement) this.f188z.subtract(factor.multiply(v.getZ())));
    }

    public FieldVector3D<T> subtract(double factor, FieldVector3D<T> v) {
        return new FieldVector3D<>(1.0d, this, -factor, v);
    }

    public FieldVector3D<T> subtract(double factor, Vector3D v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.subtract(v.getX() * factor), (RealFieldElement) this.f187y.subtract(v.getY() * factor), (RealFieldElement) this.f188z.subtract(v.getZ() * factor));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.FieldVector3D<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public FieldVector3D<T> normalize() throws MathArithmeticException {
        T s = getNorm();
        if (s.getReal() != 0.0d) {
            return scalarMultiply((RealFieldElement) s.reciprocal());
        }
        throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
    }

    public FieldVector3D<T> orthogonal() throws MathArithmeticException {
        double threshold = 0.6d * getNorm().getReal();
        if (threshold == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        } else if (FastMath.abs(this.f186x.getReal()) <= threshold) {
            RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f187y.multiply(this.f187y)).add(this.f188z.multiply(this.f188z))).sqrt()).reciprocal();
            return new FieldVector3D<>((RealFieldElement) realFieldElement.getField().getZero(), (RealFieldElement) realFieldElement.multiply(this.f188z), (RealFieldElement) ((RealFieldElement) realFieldElement.multiply(this.f187y)).negate());
        } else if (FastMath.abs(this.f187y.getReal()) <= threshold) {
            RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f186x.multiply(this.f186x)).add(this.f188z.multiply(this.f188z))).sqrt()).reciprocal();
            return new FieldVector3D<>((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(this.f188z)).negate(), (RealFieldElement) realFieldElement2.getField().getZero(), (RealFieldElement) realFieldElement2.multiply(this.f186x));
        } else {
            RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f186x.multiply(this.f186x)).add(this.f187y.multiply(this.f187y))).sqrt()).reciprocal();
            return new FieldVector3D<>((RealFieldElement) realFieldElement3.multiply(this.f187y), (RealFieldElement) ((RealFieldElement) realFieldElement3.multiply(this.f186x)).negate(), (RealFieldElement) realFieldElement3.getField().getZero());
        }
    }

    public static <T extends RealFieldElement<T>> T angle(FieldVector3D<T> v1, FieldVector3D<T> v2) throws MathArithmeticException {
        RealFieldElement realFieldElement = (RealFieldElement) v1.getNorm().multiply(v2.getNorm());
        if (realFieldElement.getReal() == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        RealFieldElement dotProduct = dotProduct(v1, v2);
        double threshold = realFieldElement.getReal() * 0.9999d;
        if (dotProduct.getReal() >= (-threshold) && dotProduct.getReal() <= threshold) {
            return (T) ((RealFieldElement) ((RealFieldElement) dotProduct.divide(realFieldElement)).acos());
        }
        FieldVector3D<T> v3 = crossProduct(v1, v2);
        return dotProduct.getReal() >= 0.0d ? (T) ((RealFieldElement) ((RealFieldElement) v3.getNorm().divide(realFieldElement)).asin()) : (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) v3.getNorm().divide(realFieldElement)).asin()).subtract(3.141592653589793d)).negate());
    }

    public static <T extends RealFieldElement<T>> T angle(FieldVector3D<T> v1, Vector3D v2) throws MathArithmeticException {
        RealFieldElement realFieldElement = (RealFieldElement) v1.getNorm().multiply(v2.getNorm());
        if (realFieldElement.getReal() == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        RealFieldElement dotProduct = dotProduct(v1, v2);
        double threshold = realFieldElement.getReal() * 0.9999d;
        if (dotProduct.getReal() >= (-threshold) && dotProduct.getReal() <= threshold) {
            return (T) ((RealFieldElement) ((RealFieldElement) dotProduct.divide(realFieldElement)).acos());
        }
        FieldVector3D<T> v3 = crossProduct(v1, v2);
        return dotProduct.getReal() >= 0.0d ? (T) ((RealFieldElement) ((RealFieldElement) v3.getNorm().divide(realFieldElement)).asin()) : (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) v3.getNorm().divide(realFieldElement)).asin()).subtract(3.141592653589793d)).negate());
    }

    public static <T extends RealFieldElement<T>> T angle(Vector3D v1, FieldVector3D<T> v2) throws MathArithmeticException {
        return (T) angle(v2, v1);
    }

    public FieldVector3D<T> negate() {
        return new FieldVector3D<>((RealFieldElement) this.f186x.negate(), (RealFieldElement) this.f187y.negate(), (RealFieldElement) this.f188z.negate());
    }

    public FieldVector3D<T> scalarMultiply(T a) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.multiply(a), (RealFieldElement) this.f187y.multiply(a), (RealFieldElement) this.f188z.multiply(a));
    }

    public FieldVector3D<T> scalarMultiply(double a) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.multiply(a), (RealFieldElement) this.f187y.multiply(a), (RealFieldElement) this.f188z.multiply(a));
    }

    public boolean isNaN() {
        return Double.isNaN(this.f186x.getReal()) || Double.isNaN(this.f187y.getReal()) || Double.isNaN(this.f188z.getReal());
    }

    public boolean isInfinite() {
        return !isNaN() && (Double.isInfinite(this.f186x.getReal()) || Double.isInfinite(this.f187y.getReal()) || Double.isInfinite(this.f188z.getReal()));
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FieldVector3D)) {
            return false;
        }
        FieldVector3D<T> rhs = (FieldVector3D) other;
        if (rhs.isNaN()) {
            return isNaN();
        }
        return this.f186x.equals(rhs.f186x) && this.f187y.equals(rhs.f187y) && this.f188z.equals(rhs.f188z);
    }

    public int hashCode() {
        if (isNaN()) {
            return 409;
        }
        return ((this.f186x.hashCode() * 107) + (this.f187y.hashCode() * 83) + this.f188z.hashCode()) * 311;
    }

    public T dotProduct(FieldVector3D<T> v) {
        return (T) ((RealFieldElement) this.f186x.linearCombination(this.f186x, v.f186x, this.f187y, v.f187y, this.f188z, v.f188z));
    }

    public T dotProduct(Vector3D v) {
        return (T) ((RealFieldElement) this.f186x.linearCombination(v.getX(), this.f186x, v.getY(), this.f187y, v.getZ(), this.f188z));
    }

    public FieldVector3D<T> crossProduct(FieldVector3D<T> v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.linearCombination(this.f187y, v.f188z, this.f188z.negate(), v.f187y), (RealFieldElement) this.f187y.linearCombination(this.f188z, v.f186x, this.f186x.negate(), v.f188z), (RealFieldElement) this.f188z.linearCombination(this.f186x, v.f187y, this.f187y.negate(), v.f186x));
    }

    public FieldVector3D<T> crossProduct(Vector3D v) {
        return new FieldVector3D<>((RealFieldElement) this.f186x.linearCombination(v.getZ(), this.f187y, -v.getY(), this.f188z), (RealFieldElement) this.f187y.linearCombination(v.getX(), this.f188z, -v.getZ(), this.f186x), (RealFieldElement) this.f188z.linearCombination(v.getY(), this.f186x, -v.getX(), this.f187y));
    }

    public T distance1(FieldVector3D<T> v) {
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) v.f186x.subtract(this.f186x)).abs()).add((RealFieldElement) ((RealFieldElement) v.f187y.subtract(this.f187y)).abs())).add((RealFieldElement) ((RealFieldElement) v.f188z.subtract(this.f188z)).abs()));
    }

    public T distance1(Vector3D v) {
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f186x.subtract(v.getX())).abs()).add((RealFieldElement) ((RealFieldElement) this.f187y.subtract(v.getY())).abs())).add((RealFieldElement) ((RealFieldElement) this.f188z.subtract(v.getZ())).abs()));
    }

    public T distance(FieldVector3D<T> v) {
        RealFieldElement realFieldElement = (RealFieldElement) v.f186x.subtract(this.f186x);
        RealFieldElement realFieldElement2 = (RealFieldElement) v.f187y.subtract(this.f187y);
        RealFieldElement realFieldElement3 = (RealFieldElement) v.f188z.subtract(this.f188z);
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(realFieldElement)).add(realFieldElement2.multiply(realFieldElement2))).add(realFieldElement3.multiply(realFieldElement3))).sqrt());
    }

    public T distance(Vector3D v) {
        RealFieldElement realFieldElement = (RealFieldElement) this.f186x.subtract(v.getX());
        RealFieldElement realFieldElement2 = (RealFieldElement) this.f187y.subtract(v.getY());
        RealFieldElement realFieldElement3 = (RealFieldElement) this.f188z.subtract(v.getZ());
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(realFieldElement)).add(realFieldElement2.multiply(realFieldElement2))).add(realFieldElement3.multiply(realFieldElement3))).sqrt());
    }

    public T distanceInf(FieldVector3D<T> v) {
        T dx = (T) ((RealFieldElement) ((RealFieldElement) v.f186x.subtract(this.f186x)).abs());
        T dy = (T) ((RealFieldElement) ((RealFieldElement) v.f187y.subtract(this.f187y)).abs());
        T dz = (T) ((RealFieldElement) ((RealFieldElement) v.f188z.subtract(this.f188z)).abs());
        return dx.getReal() <= dy.getReal() ? dy.getReal() <= dz.getReal() ? dz : dy : dx.getReal() > dz.getReal() ? dx : dz;
    }

    public T distanceInf(Vector3D v) {
        T dx = (T) ((RealFieldElement) ((RealFieldElement) this.f186x.subtract(v.getX())).abs());
        T dy = (T) ((RealFieldElement) ((RealFieldElement) this.f187y.subtract(v.getY())).abs());
        T dz = (T) ((RealFieldElement) ((RealFieldElement) this.f188z.subtract(v.getZ())).abs());
        return dx.getReal() <= dy.getReal() ? dy.getReal() <= dz.getReal() ? dz : dy : dx.getReal() > dz.getReal() ? dx : dz;
    }

    public T distanceSq(FieldVector3D<T> v) {
        RealFieldElement realFieldElement = (RealFieldElement) v.f186x.subtract(this.f186x);
        RealFieldElement realFieldElement2 = (RealFieldElement) v.f187y.subtract(this.f187y);
        RealFieldElement realFieldElement3 = (RealFieldElement) v.f188z.subtract(this.f188z);
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(realFieldElement)).add(realFieldElement2.multiply(realFieldElement2))).add(realFieldElement3.multiply(realFieldElement3)));
    }

    public T distanceSq(Vector3D v) {
        RealFieldElement realFieldElement = (RealFieldElement) this.f186x.subtract(v.getX());
        RealFieldElement realFieldElement2 = (RealFieldElement) this.f187y.subtract(v.getY());
        RealFieldElement realFieldElement3 = (RealFieldElement) this.f188z.subtract(v.getZ());
        return (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(realFieldElement)).add(realFieldElement2.multiply(realFieldElement2))).add(realFieldElement3.multiply(realFieldElement3)));
    }

    public static <T extends RealFieldElement<T>> T dotProduct(FieldVector3D<T> v1, FieldVector3D<T> v2) {
        return v1.dotProduct(v2);
    }

    public static <T extends RealFieldElement<T>> T dotProduct(FieldVector3D<T> v1, Vector3D v2) {
        return v1.dotProduct(v2);
    }

    public static <T extends RealFieldElement<T>> T dotProduct(Vector3D v1, FieldVector3D<T> v2) {
        return v2.dotProduct(v1);
    }

    public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(FieldVector3D<T> v1, FieldVector3D<T> v2) {
        return v1.crossProduct(v2);
    }

    public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(FieldVector3D<T> v1, Vector3D v2) {
        return v1.crossProduct(v2);
    }

    public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(Vector3D v1, FieldVector3D<T> v2) {
        return new FieldVector3D<>((RealFieldElement) ((FieldVector3D) v2).f186x.linearCombination(v1.getY(), ((FieldVector3D) v2).f188z, -v1.getZ(), ((FieldVector3D) v2).f187y), (RealFieldElement) ((FieldVector3D) v2).f187y.linearCombination(v1.getZ(), ((FieldVector3D) v2).f186x, -v1.getX(), ((FieldVector3D) v2).f188z), (RealFieldElement) ((FieldVector3D) v2).f188z.linearCombination(v1.getX(), ((FieldVector3D) v2).f187y, -v1.getY(), ((FieldVector3D) v2).f186x));
    }

    public static <T extends RealFieldElement<T>> T distance1(FieldVector3D<T> v1, FieldVector3D<T> v2) {
        return v1.distance1(v2);
    }

    public static <T extends RealFieldElement<T>> T distance1(FieldVector3D<T> v1, Vector3D v2) {
        return v1.distance1(v2);
    }

    public static <T extends RealFieldElement<T>> T distance1(Vector3D v1, FieldVector3D<T> v2) {
        return v2.distance1(v1);
    }

    public static <T extends RealFieldElement<T>> T distance(FieldVector3D<T> v1, FieldVector3D<T> v2) {
        return v1.distance(v2);
    }

    public static <T extends RealFieldElement<T>> T distance(FieldVector3D<T> v1, Vector3D v2) {
        return v1.distance(v2);
    }

    public static <T extends RealFieldElement<T>> T distance(Vector3D v1, FieldVector3D<T> v2) {
        return v2.distance(v1);
    }

    public static <T extends RealFieldElement<T>> T distanceInf(FieldVector3D<T> v1, FieldVector3D<T> v2) {
        return v1.distanceInf(v2);
    }

    public static <T extends RealFieldElement<T>> T distanceInf(FieldVector3D<T> v1, Vector3D v2) {
        return v1.distanceInf(v2);
    }

    public static <T extends RealFieldElement<T>> T distanceInf(Vector3D v1, FieldVector3D<T> v2) {
        return v2.distanceInf(v1);
    }

    public static <T extends RealFieldElement<T>> T distanceSq(FieldVector3D<T> v1, FieldVector3D<T> v2) {
        return v1.distanceSq(v2);
    }

    public static <T extends RealFieldElement<T>> T distanceSq(FieldVector3D<T> v1, Vector3D v2) {
        return v1.distanceSq(v2);
    }

    public static <T extends RealFieldElement<T>> T distanceSq(Vector3D v1, FieldVector3D<T> v2) {
        return v2.distanceSq(v1);
    }

    public String toString() {
        return Vector3DFormat.getInstance().format(toVector3D());
    }

    public String toString(NumberFormat format) {
        return new Vector3DFormat(format).format(toVector3D());
    }
}
