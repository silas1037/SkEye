package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class FieldRotation<T extends RealFieldElement<T>> implements Serializable {
    private static final long serialVersionUID = 20130224;

    /* renamed from: q0 */
    private final T f182q0;

    /* renamed from: q1 */
    private final T f183q1;

    /* renamed from: q2 */
    private final T f184q2;

    /* renamed from: q3 */
    private final T f185q3;

    public FieldRotation(T q0, T q1, T q2, T q3, boolean needsNormalization) {
        if (needsNormalization) {
            RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) q0.multiply(q0)).add(q1.multiply(q1))).add(q2.multiply(q2))).add(q3.multiply(q3))).sqrt()).reciprocal();
            this.f182q0 = (T) ((RealFieldElement) realFieldElement.multiply(q0));
            this.f183q1 = (T) ((RealFieldElement) realFieldElement.multiply(q1));
            this.f184q2 = (T) ((RealFieldElement) realFieldElement.multiply(q2));
            this.f185q3 = (T) ((RealFieldElement) realFieldElement.multiply(q3));
            return;
        }
        this.f182q0 = q0;
        this.f183q1 = q1;
        this.f184q2 = q2;
        this.f185q3 = q3;
    }

    @Deprecated
    public FieldRotation(FieldVector3D<T> axis, T angle) throws MathIllegalArgumentException {
        this(axis, angle, RotationConvention.VECTOR_OPERATOR);
    }

    public FieldRotation(FieldVector3D<T> axis, T angle, RotationConvention convention) throws MathIllegalArgumentException {
        T norm = axis.getNorm();
        if (norm.getReal() == 0.0d) {
            throw new MathIllegalArgumentException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_AXIS, new Object[0]);
        }
        RealFieldElement realFieldElement = (RealFieldElement) angle.multiply(convention == RotationConvention.VECTOR_OPERATOR ? -0.5d : 0.5d);
        RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) realFieldElement.sin()).divide(norm);
        this.f182q0 = (T) ((RealFieldElement) realFieldElement.cos());
        this.f183q1 = (T) ((RealFieldElement) realFieldElement2.multiply(axis.getX()));
        this.f184q2 = (T) ((RealFieldElement) realFieldElement2.multiply(axis.getY()));
        this.f185q3 = (T) ((RealFieldElement) realFieldElement2.multiply(axis.getZ()));
    }

    public FieldRotation(T[][] m, double threshold) throws NotARotationMatrixException {
        if (m.length == 3 && m[0].length == 3 && m[1].length == 3 && m[2].length == 3) {
            T[][] ort = orthogonalizeMatrix(m, threshold);
            RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ort[0][0].multiply((RealFieldElement) ((RealFieldElement) ort[1][1].multiply(ort[2][2])).subtract(ort[2][1].multiply(ort[1][2])))).subtract(ort[1][0].multiply((RealFieldElement) ((RealFieldElement) ort[0][1].multiply(ort[2][2])).subtract(ort[2][1].multiply(ort[0][2]))))).add(ort[2][0].multiply((RealFieldElement) ((RealFieldElement) ort[0][1].multiply(ort[1][2])).subtract(ort[1][1].multiply(ort[0][2]))));
            if (realFieldElement.getReal() < 0.0d) {
                throw new NotARotationMatrixException(LocalizedFormats.CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT, realFieldElement);
            }
            T[] quat = mat2quat(ort);
            this.f182q0 = quat[0];
            this.f183q1 = quat[1];
            this.f184q2 = quat[2];
            this.f185q3 = quat[3];
            return;
        }
        throw new NotARotationMatrixException(LocalizedFormats.ROTATION_MATRIX_DIMENSIONS, Integer.valueOf(m.length), Integer.valueOf(m[0].length));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r9v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.FieldRotation<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public FieldRotation(FieldVector3D<T> u1, FieldVector3D<T> u2, FieldVector3D<T> v1, FieldVector3D<T> v2) throws MathArithmeticException {
        FieldVector3D<T> u3 = FieldVector3D.crossProduct(u1, u2).normalize();
        FieldVector3D<T> u22 = FieldVector3D.crossProduct(u3, u1).normalize();
        FieldVector3D<T> u12 = u1.normalize();
        FieldVector3D<T> v3 = FieldVector3D.crossProduct(v1, v2).normalize();
        FieldVector3D<T> v22 = FieldVector3D.crossProduct(v3, v1).normalize();
        FieldVector3D<T> v12 = v1.normalize();
        RealFieldElement[][] realFieldElementArr = (RealFieldElement[][]) MathArrays.buildArray(u12.getX().getField(), 3, 3);
        realFieldElementArr[0][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getX().multiply(v12.getX())).add(u22.getX().multiply(v22.getX()))).add(u3.getX().multiply(v3.getX()));
        realFieldElementArr[0][1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getY().multiply(v12.getX())).add(u22.getY().multiply(v22.getX()))).add(u3.getY().multiply(v3.getX()));
        realFieldElementArr[0][2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getZ().multiply(v12.getX())).add(u22.getZ().multiply(v22.getX()))).add(u3.getZ().multiply(v3.getX()));
        realFieldElementArr[1][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getX().multiply(v12.getY())).add(u22.getX().multiply(v22.getY()))).add(u3.getX().multiply(v3.getY()));
        realFieldElementArr[1][1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getY().multiply(v12.getY())).add(u22.getY().multiply(v22.getY()))).add(u3.getY().multiply(v3.getY()));
        realFieldElementArr[1][2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getZ().multiply(v12.getY())).add(u22.getZ().multiply(v22.getY()))).add(u3.getZ().multiply(v3.getY()));
        realFieldElementArr[2][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getX().multiply(v12.getZ())).add(u22.getX().multiply(v22.getZ()))).add(u3.getX().multiply(v3.getZ()));
        realFieldElementArr[2][1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getY().multiply(v12.getZ())).add(u22.getY().multiply(v22.getZ()))).add(u3.getY().multiply(v3.getZ()));
        realFieldElementArr[2][2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) u12.getZ().multiply(v12.getZ())).add(u22.getZ().multiply(v22.getZ()))).add(u3.getZ().multiply(v3.getZ()));
        T[] quat = mat2quat(realFieldElementArr);
        this.f182q0 = quat[0];
        this.f183q1 = quat[1];
        this.f184q2 = quat[2];
        this.f185q3 = quat[3];
    }

    public FieldRotation(FieldVector3D<T> u, FieldVector3D<T> v) throws MathArithmeticException {
        RealFieldElement realFieldElement = (RealFieldElement) u.getNorm().multiply(v.getNorm());
        if (realFieldElement.getReal() == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR, new Object[0]);
        }
        RealFieldElement dotProduct = FieldVector3D.dotProduct(u, v);
        if (dotProduct.getReal() < -0.999999999999998d * realFieldElement.getReal()) {
            FieldVector3D<T> w = u.orthogonal();
            this.f182q0 = (T) ((RealFieldElement) realFieldElement.getField().getZero());
            this.f183q1 = (T) ((RealFieldElement) w.getX().negate());
            this.f184q2 = (T) ((RealFieldElement) w.getY().negate());
            this.f185q3 = (T) ((RealFieldElement) w.getZ().negate());
            return;
        }
        this.f182q0 = (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) dotProduct.divide(realFieldElement)).add(1.0d)).multiply(0.5d)).sqrt());
        RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(realFieldElement)).multiply(2.0d)).reciprocal();
        FieldVector3D<T> q = FieldVector3D.crossProduct(v, u);
        this.f183q1 = (T) ((RealFieldElement) realFieldElement2.multiply(q.getX()));
        this.f184q2 = (T) ((RealFieldElement) realFieldElement2.multiply(q.getY()));
        this.f185q3 = (T) ((RealFieldElement) realFieldElement2.multiply(q.getZ()));
    }

    @Deprecated
    public FieldRotation(RotationOrder order, T alpha1, T alpha2, T alpha3) {
        this(order, RotationConvention.VECTOR_OPERATOR, alpha1, alpha2, alpha3);
    }

    public FieldRotation(RotationOrder order, RotationConvention convention, T alpha1, T alpha2, T alpha3) {
        RealFieldElement realFieldElement = (RealFieldElement) alpha1.getField().getOne();
        FieldRotation<T> composed = new FieldRotation<>(new FieldVector3D(realFieldElement, order.getA1()), alpha1, convention).compose(new FieldRotation<>(new FieldVector3D(realFieldElement, order.getA2()), alpha2, convention).compose(new FieldRotation<>(new FieldVector3D(realFieldElement, order.getA3()), alpha3, convention), convention), convention);
        this.f182q0 = composed.f182q0;
        this.f183q1 = composed.f183q1;
        this.f184q2 = composed.f184q2;
        this.f185q3 = composed.f185q3;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v28, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX DEBUG: Multi-variable search result rejected for r3v52, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX DEBUG: Multi-variable search result rejected for r3v76, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX DEBUG: Multi-variable search result rejected for r3v100, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    private T[] mat2quat(T[][] ort) {
        T[] quat = (T[]) ((RealFieldElement[]) MathArrays.buildArray(ort[0][0].getField(), 4));
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ort[0][0].add(ort[1][1])).add(ort[2][2]);
        if (realFieldElement.getReal() > -0.19d) {
            quat[0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.add(1.0d)).sqrt()).multiply(0.5d);
            RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) quat[0].reciprocal()).multiply(0.25d);
            quat[1] = (RealFieldElement) realFieldElement2.multiply(ort[1][2].subtract(ort[2][1]));
            quat[2] = (RealFieldElement) realFieldElement2.multiply(ort[2][0].subtract(ort[0][2]));
            quat[3] = (RealFieldElement) realFieldElement2.multiply(ort[0][1].subtract(ort[1][0]));
        } else {
            RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) ort[0][0].subtract(ort[1][1])).subtract(ort[2][2]);
            if (realFieldElement3.getReal() > -0.19d) {
                quat[1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement3.add(1.0d)).sqrt()).multiply(0.5d);
                RealFieldElement realFieldElement4 = (RealFieldElement) ((RealFieldElement) quat[1].reciprocal()).multiply(0.25d);
                quat[0] = (RealFieldElement) realFieldElement4.multiply(ort[1][2].subtract(ort[2][1]));
                quat[2] = (RealFieldElement) realFieldElement4.multiply(ort[0][1].add(ort[1][0]));
                quat[3] = (RealFieldElement) realFieldElement4.multiply(ort[0][2].add(ort[2][0]));
            } else {
                RealFieldElement realFieldElement5 = (RealFieldElement) ((RealFieldElement) ort[1][1].subtract(ort[0][0])).subtract(ort[2][2]);
                if (realFieldElement5.getReal() > -0.19d) {
                    quat[2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement5.add(1.0d)).sqrt()).multiply(0.5d);
                    RealFieldElement realFieldElement6 = (RealFieldElement) ((RealFieldElement) quat[2].reciprocal()).multiply(0.25d);
                    quat[0] = (RealFieldElement) realFieldElement6.multiply(ort[2][0].subtract(ort[0][2]));
                    quat[1] = (RealFieldElement) realFieldElement6.multiply(ort[0][1].add(ort[1][0]));
                    quat[3] = (RealFieldElement) realFieldElement6.multiply(ort[2][1].add(ort[1][2]));
                } else {
                    quat[3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ort[2][2].subtract(ort[0][0])).subtract(ort[1][1])).add(1.0d)).sqrt()).multiply(0.5d);
                    RealFieldElement realFieldElement7 = (RealFieldElement) ((RealFieldElement) quat[3].reciprocal()).multiply(0.25d);
                    quat[0] = (RealFieldElement) realFieldElement7.multiply(ort[0][1].subtract(ort[1][0]));
                    quat[1] = (RealFieldElement) realFieldElement7.multiply(ort[0][2].add(ort[2][0]));
                    quat[2] = (RealFieldElement) realFieldElement7.multiply(ort[2][1].add(ort[1][2]));
                }
            }
        }
        return quat;
    }

    public FieldRotation<T> revert() {
        return new FieldRotation<>((RealFieldElement) this.f182q0.negate(), (RealFieldElement) this.f183q1, (RealFieldElement) this.f184q2, (RealFieldElement) this.f185q3, false);
    }

    public T getQ0() {
        return this.f182q0;
    }

    public T getQ1() {
        return this.f183q1;
    }

    public T getQ2() {
        return this.f184q2;
    }

    public T getQ3() {
        return this.f185q3;
    }

    @Deprecated
    public FieldVector3D<T> getAxis() {
        return getAxis(RotationConvention.VECTOR_OPERATOR);
    }

    public FieldVector3D<T> getAxis(RotationConvention convention) {
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(this.f183q1)).add(this.f184q2.multiply(this.f184q2))).add(this.f185q3.multiply(this.f185q3));
        if (realFieldElement.getReal() == 0.0d) {
            Field<T> field = realFieldElement.getField();
            return new FieldVector3D<>(convention == RotationConvention.VECTOR_OPERATOR ? field.getOne() : (RealFieldElement) field.getOne().negate(), field.getZero(), field.getZero());
        }
        double sgn = convention == RotationConvention.VECTOR_OPERATOR ? 1.0d : -1.0d;
        if (this.f182q0.getReal() < 0.0d) {
            RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.sqrt()).reciprocal()).multiply(sgn);
            return new FieldVector3D<>((RealFieldElement) this.f183q1.multiply(realFieldElement2), (RealFieldElement) this.f184q2.multiply(realFieldElement2), (RealFieldElement) this.f185q3.multiply(realFieldElement2));
        }
        RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.sqrt()).reciprocal()).negate()).multiply(sgn);
        return new FieldVector3D<>((RealFieldElement) this.f183q1.multiply(realFieldElement3), (RealFieldElement) this.f184q2.multiply(realFieldElement3), (RealFieldElement) this.f185q3.multiply(realFieldElement3));
    }

    public T getAngle() {
        return (this.f182q0.getReal() < -0.1d || this.f182q0.getReal() > 0.1d) ? (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(this.f183q1)).add(this.f184q2.multiply(this.f184q2))).add(this.f185q3.multiply(this.f185q3))).sqrt()).asin()).multiply(2)) : this.f182q0.getReal() < 0.0d ? (T) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.negate()).acos()).multiply(2)) : (T) ((RealFieldElement) ((RealFieldElement) this.f182q0.acos()).multiply(2));
    }

    @Deprecated
    public T[] getAngles(RotationOrder order) throws CardanEulerSingularityException {
        return getAngles(order, RotationConvention.VECTOR_OPERATOR);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r9v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.FieldRotation<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public T[] getAngles(RotationOrder order, RotationConvention convention) throws CardanEulerSingularityException {
        if (convention == RotationConvention.VECTOR_OPERATOR) {
            if (order == RotationOrder.XYZ) {
                FieldVector3D<T> v1 = applyTo(vector(0.0d, 0.0d, 1.0d));
                FieldVector3D<T> v2 = applyInverseTo(vector(1.0d, 0.0d, 0.0d));
                if (v2.getZ().getReal() >= -0.9999999999d && v2.getZ().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) ((RealFieldElement) v1.getY().negate()).atan2(v1.getZ()), (RealFieldElement) v2.getZ().asin(), (RealFieldElement) ((RealFieldElement) v2.getY().negate()).atan2(v2.getX()));
                }
                throw new CardanEulerSingularityException(true);
            } else if (order == RotationOrder.XZY) {
                FieldVector3D<T> v12 = applyTo(vector(0.0d, 1.0d, 0.0d));
                FieldVector3D<T> v22 = applyInverseTo(vector(1.0d, 0.0d, 0.0d));
                if (v22.getY().getReal() >= -0.9999999999d && v22.getY().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v12.getZ().atan2(v12.getY()), (RealFieldElement) ((RealFieldElement) v22.getY().asin()).negate(), (RealFieldElement) v22.getZ().atan2(v22.getX()));
                }
                throw new CardanEulerSingularityException(true);
            } else if (order == RotationOrder.YXZ) {
                FieldVector3D<T> v13 = applyTo(vector(0.0d, 0.0d, 1.0d));
                FieldVector3D<T> v23 = applyInverseTo(vector(0.0d, 1.0d, 0.0d));
                if (v23.getZ().getReal() >= -0.9999999999d && v23.getZ().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v13.getX().atan2(v13.getZ()), (RealFieldElement) ((RealFieldElement) v23.getZ().asin()).negate(), (RealFieldElement) v23.getX().atan2(v23.getY()));
                }
                throw new CardanEulerSingularityException(true);
            } else if (order == RotationOrder.YZX) {
                FieldVector3D<T> v14 = applyTo(vector(1.0d, 0.0d, 0.0d));
                FieldVector3D<T> v24 = applyInverseTo(vector(0.0d, 1.0d, 0.0d));
                if (v24.getX().getReal() >= -0.9999999999d && v24.getX().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) ((RealFieldElement) v14.getZ().negate()).atan2(v14.getX()), (RealFieldElement) v24.getX().asin(), (RealFieldElement) ((RealFieldElement) v24.getZ().negate()).atan2(v24.getY()));
                }
                throw new CardanEulerSingularityException(true);
            } else if (order == RotationOrder.ZXY) {
                FieldVector3D<T> v15 = applyTo(vector(0.0d, 1.0d, 0.0d));
                FieldVector3D<T> v25 = applyInverseTo(vector(0.0d, 0.0d, 1.0d));
                if (v25.getY().getReal() >= -0.9999999999d && v25.getY().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) ((RealFieldElement) v15.getX().negate()).atan2(v15.getY()), (RealFieldElement) v25.getY().asin(), (RealFieldElement) ((RealFieldElement) v25.getX().negate()).atan2(v25.getZ()));
                }
                throw new CardanEulerSingularityException(true);
            } else if (order == RotationOrder.ZYX) {
                FieldVector3D<T> v16 = applyTo(vector(1.0d, 0.0d, 0.0d));
                FieldVector3D<T> v26 = applyInverseTo(vector(0.0d, 0.0d, 1.0d));
                if (v26.getX().getReal() >= -0.9999999999d && v26.getX().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v16.getY().atan2(v16.getX()), (RealFieldElement) ((RealFieldElement) v26.getX().asin()).negate(), (RealFieldElement) v26.getY().atan2(v26.getZ()));
                }
                throw new CardanEulerSingularityException(true);
            } else if (order == RotationOrder.XYX) {
                FieldVector3D<T> v17 = applyTo(vector(1.0d, 0.0d, 0.0d));
                FieldVector3D<T> v27 = applyInverseTo(vector(1.0d, 0.0d, 0.0d));
                if (v27.getX().getReal() >= -0.9999999999d && v27.getX().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v17.getY().atan2(v17.getZ().negate()), (RealFieldElement) v27.getX().acos(), (RealFieldElement) v27.getY().atan2(v27.getZ()));
                }
                throw new CardanEulerSingularityException(false);
            } else if (order == RotationOrder.XZX) {
                FieldVector3D<T> v18 = applyTo(vector(1.0d, 0.0d, 0.0d));
                FieldVector3D<T> v28 = applyInverseTo(vector(1.0d, 0.0d, 0.0d));
                if (v28.getX().getReal() >= -0.9999999999d && v28.getX().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v18.getZ().atan2(v18.getY()), (RealFieldElement) v28.getX().acos(), (RealFieldElement) v28.getZ().atan2(v28.getY().negate()));
                }
                throw new CardanEulerSingularityException(false);
            } else if (order == RotationOrder.YXY) {
                FieldVector3D<T> v19 = applyTo(vector(0.0d, 1.0d, 0.0d));
                FieldVector3D<T> v29 = applyInverseTo(vector(0.0d, 1.0d, 0.0d));
                if (v29.getY().getReal() >= -0.9999999999d && v29.getY().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v19.getX().atan2(v19.getZ()), (RealFieldElement) v29.getY().acos(), (RealFieldElement) v29.getX().atan2(v29.getZ().negate()));
                }
                throw new CardanEulerSingularityException(false);
            } else if (order == RotationOrder.YZY) {
                FieldVector3D<T> v110 = applyTo(vector(0.0d, 1.0d, 0.0d));
                FieldVector3D<T> v210 = applyInverseTo(vector(0.0d, 1.0d, 0.0d));
                if (v210.getY().getReal() >= -0.9999999999d && v210.getY().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v110.getZ().atan2(v110.getX().negate()), (RealFieldElement) v210.getY().acos(), (RealFieldElement) v210.getZ().atan2(v210.getX()));
                }
                throw new CardanEulerSingularityException(false);
            } else if (order == RotationOrder.ZXZ) {
                FieldVector3D<T> v111 = applyTo(vector(0.0d, 0.0d, 1.0d));
                FieldVector3D<T> v211 = applyInverseTo(vector(0.0d, 0.0d, 1.0d));
                if (v211.getZ().getReal() >= -0.9999999999d && v211.getZ().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v111.getX().atan2(v111.getY().negate()), (RealFieldElement) v211.getZ().acos(), (RealFieldElement) v211.getX().atan2(v211.getY()));
                }
                throw new CardanEulerSingularityException(false);
            } else {
                FieldVector3D<T> v112 = applyTo(vector(0.0d, 0.0d, 1.0d));
                FieldVector3D<T> v212 = applyInverseTo(vector(0.0d, 0.0d, 1.0d));
                if (v212.getZ().getReal() >= -0.9999999999d && v212.getZ().getReal() <= 0.9999999999d) {
                    return (T[]) buildArray((RealFieldElement) v112.getY().atan2(v112.getX()), (RealFieldElement) v212.getZ().acos(), (RealFieldElement) v212.getY().atan2(v212.getX().negate()));
                }
                throw new CardanEulerSingularityException(false);
            }
        } else if (order == RotationOrder.XYZ) {
            FieldVector3D<T> v113 = applyTo(Vector3D.PLUS_I);
            FieldVector3D<T> v213 = applyInverseTo(Vector3D.PLUS_K);
            if (v213.getX().getReal() >= -0.9999999999d && v213.getX().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) ((RealFieldElement) v213.getY().negate()).atan2(v213.getZ()), (RealFieldElement) v213.getX().asin(), (RealFieldElement) ((RealFieldElement) v113.getY().negate()).atan2(v113.getX()));
            }
            throw new CardanEulerSingularityException(true);
        } else if (order == RotationOrder.XZY) {
            FieldVector3D<T> v114 = applyTo(Vector3D.PLUS_I);
            FieldVector3D<T> v214 = applyInverseTo(Vector3D.PLUS_J);
            if (v214.getX().getReal() >= -0.9999999999d && v214.getX().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v214.getZ().atan2(v214.getY()), (RealFieldElement) ((RealFieldElement) v214.getX().asin()).negate(), (RealFieldElement) v114.getZ().atan2(v114.getX()));
            }
            throw new CardanEulerSingularityException(true);
        } else if (order == RotationOrder.YXZ) {
            FieldVector3D<T> v115 = applyTo(Vector3D.PLUS_J);
            FieldVector3D<T> v215 = applyInverseTo(Vector3D.PLUS_K);
            if (v215.getY().getReal() >= -0.9999999999d && v215.getY().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v215.getX().atan2(v215.getZ()), (RealFieldElement) ((RealFieldElement) v215.getY().asin()).negate(), (RealFieldElement) v115.getX().atan2(v115.getY()));
            }
            throw new CardanEulerSingularityException(true);
        } else if (order == RotationOrder.YZX) {
            FieldVector3D<T> v116 = applyTo(Vector3D.PLUS_J);
            FieldVector3D<T> v216 = applyInverseTo(Vector3D.PLUS_I);
            if (v216.getY().getReal() >= -0.9999999999d && v216.getY().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) ((RealFieldElement) v216.getZ().negate()).atan2(v216.getX()), (RealFieldElement) v216.getY().asin(), (RealFieldElement) ((RealFieldElement) v116.getZ().negate()).atan2(v116.getY()));
            }
            throw new CardanEulerSingularityException(true);
        } else if (order == RotationOrder.ZXY) {
            FieldVector3D<T> v117 = applyTo(Vector3D.PLUS_K);
            FieldVector3D<T> v217 = applyInverseTo(Vector3D.PLUS_J);
            if (v217.getZ().getReal() >= -0.9999999999d && v217.getZ().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) ((RealFieldElement) v217.getX().negate()).atan2(v217.getY()), (RealFieldElement) v217.getZ().asin(), (RealFieldElement) ((RealFieldElement) v117.getX().negate()).atan2(v117.getZ()));
            }
            throw new CardanEulerSingularityException(true);
        } else if (order == RotationOrder.ZYX) {
            FieldVector3D<T> v118 = applyTo(Vector3D.PLUS_K);
            FieldVector3D<T> v218 = applyInverseTo(Vector3D.PLUS_I);
            if (v218.getZ().getReal() >= -0.9999999999d && v218.getZ().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v218.getY().atan2(v218.getX()), (RealFieldElement) ((RealFieldElement) v218.getZ().asin()).negate(), (RealFieldElement) v118.getY().atan2(v118.getZ()));
            }
            throw new CardanEulerSingularityException(true);
        } else if (order == RotationOrder.XYX) {
            FieldVector3D<T> v119 = applyTo(Vector3D.PLUS_I);
            FieldVector3D<T> v219 = applyInverseTo(Vector3D.PLUS_I);
            if (v219.getX().getReal() >= -0.9999999999d && v219.getX().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v219.getY().atan2(v219.getZ().negate()), (RealFieldElement) v219.getX().acos(), (RealFieldElement) v119.getY().atan2(v119.getZ()));
            }
            throw new CardanEulerSingularityException(false);
        } else if (order == RotationOrder.XZX) {
            FieldVector3D<T> v120 = applyTo(Vector3D.PLUS_I);
            FieldVector3D<T> v220 = applyInverseTo(Vector3D.PLUS_I);
            if (v220.getX().getReal() >= -0.9999999999d && v220.getX().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v220.getZ().atan2(v220.getY()), (RealFieldElement) v220.getX().acos(), (RealFieldElement) v120.getZ().atan2(v120.getY().negate()));
            }
            throw new CardanEulerSingularityException(false);
        } else if (order == RotationOrder.YXY) {
            FieldVector3D<T> v121 = applyTo(Vector3D.PLUS_J);
            FieldVector3D<T> v221 = applyInverseTo(Vector3D.PLUS_J);
            if (v221.getY().getReal() >= -0.9999999999d && v221.getY().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v221.getX().atan2(v221.getZ()), (RealFieldElement) v221.getY().acos(), (RealFieldElement) v121.getX().atan2(v121.getZ().negate()));
            }
            throw new CardanEulerSingularityException(false);
        } else if (order == RotationOrder.YZY) {
            FieldVector3D<T> v122 = applyTo(Vector3D.PLUS_J);
            FieldVector3D<T> v222 = applyInverseTo(Vector3D.PLUS_J);
            if (v222.getY().getReal() >= -0.9999999999d && v222.getY().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v222.getZ().atan2(v222.getX().negate()), (RealFieldElement) v222.getY().acos(), (RealFieldElement) v122.getZ().atan2(v122.getX()));
            }
            throw new CardanEulerSingularityException(false);
        } else if (order == RotationOrder.ZXZ) {
            FieldVector3D<T> v123 = applyTo(Vector3D.PLUS_K);
            FieldVector3D<T> v223 = applyInverseTo(Vector3D.PLUS_K);
            if (v223.getZ().getReal() >= -0.9999999999d && v223.getZ().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v223.getX().atan2(v223.getY().negate()), (RealFieldElement) v223.getZ().acos(), (RealFieldElement) v123.getX().atan2(v123.getY()));
            }
            throw new CardanEulerSingularityException(false);
        } else {
            FieldVector3D<T> v124 = applyTo(Vector3D.PLUS_K);
            FieldVector3D<T> v224 = applyInverseTo(Vector3D.PLUS_K);
            if (v224.getZ().getReal() >= -0.9999999999d && v224.getZ().getReal() <= 0.9999999999d) {
                return (T[]) buildArray((RealFieldElement) v224.getY().atan2(v224.getX()), (RealFieldElement) v224.getZ().acos(), (RealFieldElement) v124.getY().atan2(v124.getX().negate()));
            }
            throw new CardanEulerSingularityException(false);
        }
    }

    private T[] buildArray(T a0, T a1, T a2) {
        T[] array = (T[]) ((RealFieldElement[]) MathArrays.buildArray(a0.getField(), 3));
        array[0] = a0;
        array[1] = a1;
        array[2] = a2;
        return array;
    }

    private FieldVector3D<T> vector(double x, double y, double z) {
        RealFieldElement realFieldElement = (RealFieldElement) this.f182q0.getField().getZero();
        return new FieldVector3D<>((RealFieldElement) realFieldElement.add(x), (RealFieldElement) realFieldElement.add(y), (RealFieldElement) realFieldElement.add(z));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v11, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v12, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v13, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v14, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v15, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v16, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v17, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v18, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v19, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX WARN: Multi-variable type inference failed */
    public T[][] getMatrix() {
        RealFieldElement realFieldElement = (RealFieldElement) this.f182q0.multiply(this.f182q0);
        RealFieldElement realFieldElement2 = (RealFieldElement) this.f182q0.multiply(this.f183q1);
        RealFieldElement realFieldElement3 = (RealFieldElement) this.f182q0.multiply(this.f184q2);
        RealFieldElement realFieldElement4 = (RealFieldElement) this.f182q0.multiply(this.f185q3);
        RealFieldElement realFieldElement5 = (RealFieldElement) this.f183q1.multiply(this.f184q2);
        RealFieldElement realFieldElement6 = (RealFieldElement) this.f183q1.multiply(this.f185q3);
        RealFieldElement realFieldElement7 = (RealFieldElement) this.f184q2.multiply(this.f185q3);
        T[][] m = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(this.f182q0.getField(), 3, 3));
        m[0][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.add((RealFieldElement) this.f183q1.multiply(this.f183q1))).multiply(2)).subtract(1.0d);
        m[1][0] = (RealFieldElement) ((RealFieldElement) realFieldElement5.subtract(realFieldElement4)).multiply(2);
        m[2][0] = (RealFieldElement) ((RealFieldElement) realFieldElement6.add(realFieldElement3)).multiply(2);
        m[0][1] = (RealFieldElement) ((RealFieldElement) realFieldElement5.add(realFieldElement4)).multiply(2);
        m[1][1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.add((RealFieldElement) this.f184q2.multiply(this.f184q2))).multiply(2)).subtract(1.0d);
        m[2][1] = (RealFieldElement) ((RealFieldElement) realFieldElement7.subtract(realFieldElement2)).multiply(2);
        m[0][2] = (RealFieldElement) ((RealFieldElement) realFieldElement6.subtract(realFieldElement3)).multiply(2);
        m[1][2] = (RealFieldElement) ((RealFieldElement) realFieldElement7.add(realFieldElement2)).multiply(2);
        m[2][2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.add((RealFieldElement) this.f185q3.multiply(this.f185q3))).multiply(2)).subtract(1.0d);
        return m;
    }

    public Rotation toRotation() {
        return new Rotation(this.f182q0.getReal(), this.f183q1.getReal(), this.f184q2.getReal(), this.f185q3.getReal(), false);
    }

    public FieldVector3D<T> applyTo(FieldVector3D<T> u) {
        T x = u.getX();
        T y = u.getY();
        T z = u.getZ();
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        return new FieldVector3D<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) x.multiply(this.f182q0)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) y.multiply(this.f182q0)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) z.multiply(this.f182q0)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z));
    }

    public FieldVector3D<T> applyTo(Vector3D u) {
        double x = u.getX();
        double y = u.getY();
        double z = u.getZ();
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        return new FieldVector3D<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) this.f182q0.multiply(x)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) this.f182q0.multiply(y)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) this.f182q0.multiply(z)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r13v0, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    public void applyTo(T[] in, T[] out) {
        T x = in[0];
        T y = in[1];
        T z = in[2];
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        out[0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) x.multiply(this.f182q0)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x);
        out[1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) y.multiply(this.f182q0)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y);
        out[2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) z.multiply(this.f182q0)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r14v0, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    public void applyTo(double[] in, T[] out) {
        double x = in[0];
        double y = in[1];
        double z = in[2];
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        out[0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) this.f182q0.multiply(x)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x);
        out[1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) this.f182q0.multiply(y)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y);
        out[2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(((RealFieldElement) this.f182q0.multiply(z)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z);
    }

    public static <T extends RealFieldElement<T>> FieldVector3D<T> applyTo(Rotation r, FieldVector3D<T> u) {
        T x = u.getX();
        T y = u.getY();
        T z = u.getZ();
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) x.multiply(r.getQ1())).add(y.multiply(r.getQ2()))).add(z.multiply(r.getQ3()));
        return new FieldVector3D<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x.multiply(r.getQ0())).subtract(((RealFieldElement) z.multiply(r.getQ2())).subtract(y.multiply(r.getQ3())))).multiply(r.getQ0())).add(realFieldElement.multiply(r.getQ1()))).multiply(2)).subtract(x), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) y.multiply(r.getQ0())).subtract(((RealFieldElement) x.multiply(r.getQ3())).subtract(z.multiply(r.getQ1())))).multiply(r.getQ0())).add(realFieldElement.multiply(r.getQ2()))).multiply(2)).subtract(y), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) z.multiply(r.getQ0())).subtract(((RealFieldElement) y.multiply(r.getQ1())).subtract(x.multiply(r.getQ2())))).multiply(r.getQ0())).add(realFieldElement.multiply(r.getQ3()))).multiply(2)).subtract(z));
    }

    public FieldVector3D<T> applyInverseTo(FieldVector3D<T> u) {
        T x = u.getX();
        T y = u.getY();
        T z = u.getZ();
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        RealFieldElement realFieldElement2 = (RealFieldElement) this.f182q0.negate();
        return new FieldVector3D<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) x.multiply(realFieldElement2)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) y.multiply(realFieldElement2)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) z.multiply(realFieldElement2)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z));
    }

    public FieldVector3D<T> applyInverseTo(Vector3D u) {
        double x = u.getX();
        double y = u.getY();
        double z = u.getZ();
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        RealFieldElement realFieldElement2 = (RealFieldElement) this.f182q0.negate();
        return new FieldVector3D<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) realFieldElement2.multiply(x)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) realFieldElement2.multiply(y)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) realFieldElement2.multiply(z)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r13v0, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    public void applyInverseTo(T[] in, T[] out) {
        T x = in[0];
        T y = in[1];
        T z = in[2];
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        RealFieldElement realFieldElement2 = (RealFieldElement) this.f182q0.negate();
        out[0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) x.multiply(realFieldElement2)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x);
        out[1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) y.multiply(realFieldElement2)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y);
        out[2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) z.multiply(realFieldElement2)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r14v0, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    public void applyInverseTo(double[] in, T[] out) {
        double x = in[0];
        double y = in[1];
        double z = in[2];
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(x)).add(this.f184q2.multiply(y))).add(this.f185q3.multiply(z));
        RealFieldElement realFieldElement2 = (RealFieldElement) this.f182q0.negate();
        out[0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) realFieldElement2.multiply(x)).subtract(((RealFieldElement) this.f184q2.multiply(z)).subtract(this.f185q3.multiply(y))))).add(realFieldElement.multiply(this.f183q1))).multiply(2)).subtract(x);
        out[1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) realFieldElement2.multiply(y)).subtract(((RealFieldElement) this.f185q3.multiply(x)).subtract(this.f183q1.multiply(z))))).add(realFieldElement.multiply(this.f184q2))).multiply(2)).subtract(y);
        out[2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement2.multiply(((RealFieldElement) realFieldElement2.multiply(z)).subtract(((RealFieldElement) this.f183q1.multiply(y)).subtract(this.f184q2.multiply(x))))).add(realFieldElement.multiply(this.f185q3))).multiply(2)).subtract(z);
    }

    public static <T extends RealFieldElement<T>> FieldVector3D<T> applyInverseTo(Rotation r, FieldVector3D<T> u) {
        T x = u.getX();
        T y = u.getY();
        T z = u.getZ();
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) x.multiply(r.getQ1())).add(y.multiply(r.getQ2()))).add(z.multiply(r.getQ3()));
        double m0 = -r.getQ0();
        return new FieldVector3D<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x.multiply(m0)).subtract(((RealFieldElement) z.multiply(r.getQ2())).subtract(y.multiply(r.getQ3())))).multiply(m0)).add(realFieldElement.multiply(r.getQ1()))).multiply(2)).subtract(x), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) y.multiply(m0)).subtract(((RealFieldElement) x.multiply(r.getQ3())).subtract(z.multiply(r.getQ1())))).multiply(m0)).add(realFieldElement.multiply(r.getQ2()))).multiply(2)).subtract(y), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) z.multiply(m0)).subtract(((RealFieldElement) y.multiply(r.getQ1())).subtract(x.multiply(r.getQ2())))).multiply(m0)).add(realFieldElement.multiply(r.getQ3()))).multiply(2)).subtract(z));
    }

    public FieldRotation<T> applyTo(FieldRotation<T> r) {
        return compose(r, RotationConvention.VECTOR_OPERATOR);
    }

    public FieldRotation<T> compose(FieldRotation<T> r, RotationConvention convention) {
        return convention == RotationConvention.VECTOR_OPERATOR ? composeInternal(r) : r.composeInternal(this);
    }

    private FieldRotation<T> composeInternal(FieldRotation<T> r) {
        return new FieldRotation<>((RealFieldElement) ((RealFieldElement) r.f182q0.multiply(this.f182q0)).subtract(((RealFieldElement) ((RealFieldElement) r.f183q1.multiply(this.f183q1)).add(r.f184q2.multiply(this.f184q2))).add(r.f185q3.multiply(this.f185q3))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) r.f183q1.multiply(this.f182q0)).add(r.f182q0.multiply(this.f183q1))).add(((RealFieldElement) r.f184q2.multiply(this.f185q3)).subtract(r.f185q3.multiply(this.f184q2))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) r.f184q2.multiply(this.f182q0)).add(r.f182q0.multiply(this.f184q2))).add(((RealFieldElement) r.f185q3.multiply(this.f183q1)).subtract(r.f183q1.multiply(this.f185q3))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) r.f185q3.multiply(this.f182q0)).add(r.f182q0.multiply(this.f185q3))).add(((RealFieldElement) r.f183q1.multiply(this.f184q2)).subtract(r.f184q2.multiply(this.f183q1))), false);
    }

    public FieldRotation<T> applyTo(Rotation r) {
        return compose(r, RotationConvention.VECTOR_OPERATOR);
    }

    public FieldRotation<T> compose(Rotation r, RotationConvention convention) {
        return convention == RotationConvention.VECTOR_OPERATOR ? composeInternal(r) : applyTo(r, this);
    }

    private FieldRotation<T> composeInternal(Rotation r) {
        return new FieldRotation<>((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(r.getQ0())).subtract(((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(r.getQ1())).add(this.f184q2.multiply(r.getQ2()))).add(this.f185q3.multiply(r.getQ3()))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(r.getQ1())).add(this.f183q1.multiply(r.getQ0()))).add(((RealFieldElement) this.f185q3.multiply(r.getQ2())).subtract(this.f184q2.multiply(r.getQ3()))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(r.getQ2())).add(this.f184q2.multiply(r.getQ0()))).add(((RealFieldElement) this.f183q1.multiply(r.getQ3())).subtract(this.f185q3.multiply(r.getQ1()))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(r.getQ3())).add(this.f185q3.multiply(r.getQ0()))).add(((RealFieldElement) this.f184q2.multiply(r.getQ1())).subtract(this.f183q1.multiply(r.getQ2()))), false);
    }

    public static <T extends RealFieldElement<T>> FieldRotation<T> applyTo(Rotation r1, FieldRotation<T> rInner) {
        return new FieldRotation<>((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f182q0.multiply(r1.getQ0())).subtract(((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f183q1.multiply(r1.getQ1())).add(((FieldRotation) rInner).f184q2.multiply(r1.getQ2()))).add(((FieldRotation) rInner).f185q3.multiply(r1.getQ3()))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f183q1.multiply(r1.getQ0())).add(((FieldRotation) rInner).f182q0.multiply(r1.getQ1()))).add(((RealFieldElement) ((FieldRotation) rInner).f184q2.multiply(r1.getQ3())).subtract(((FieldRotation) rInner).f185q3.multiply(r1.getQ2()))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f184q2.multiply(r1.getQ0())).add(((FieldRotation) rInner).f182q0.multiply(r1.getQ2()))).add(((RealFieldElement) ((FieldRotation) rInner).f185q3.multiply(r1.getQ1())).subtract(((FieldRotation) rInner).f183q1.multiply(r1.getQ3()))), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f185q3.multiply(r1.getQ0())).add(((FieldRotation) rInner).f182q0.multiply(r1.getQ3()))).add(((RealFieldElement) ((FieldRotation) rInner).f183q1.multiply(r1.getQ2())).subtract(((FieldRotation) rInner).f184q2.multiply(r1.getQ1()))), false);
    }

    public FieldRotation<T> applyInverseTo(FieldRotation<T> r) {
        return composeInverse(r, RotationConvention.VECTOR_OPERATOR);
    }

    public FieldRotation<T> composeInverse(FieldRotation<T> r, RotationConvention convention) {
        return convention == RotationConvention.VECTOR_OPERATOR ? composeInverseInternal(r) : r.composeInternal(revert());
    }

    private FieldRotation<T> composeInverseInternal(FieldRotation<T> r) {
        return new FieldRotation<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) r.f182q0.multiply(this.f182q0)).add(((RealFieldElement) ((RealFieldElement) r.f183q1.multiply(this.f183q1)).add(r.f184q2.multiply(this.f184q2))).add(r.f185q3.multiply(this.f185q3)))).negate(), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) r.f182q0.multiply(this.f183q1)).add(((RealFieldElement) r.f184q2.multiply(this.f185q3)).subtract(r.f185q3.multiply(this.f184q2)))).subtract(r.f183q1.multiply(this.f182q0)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) r.f182q0.multiply(this.f184q2)).add(((RealFieldElement) r.f185q3.multiply(this.f183q1)).subtract(r.f183q1.multiply(this.f185q3)))).subtract(r.f184q2.multiply(this.f182q0)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) r.f182q0.multiply(this.f185q3)).add(((RealFieldElement) r.f183q1.multiply(this.f184q2)).subtract(r.f184q2.multiply(this.f183q1)))).subtract(r.f185q3.multiply(this.f182q0)), false);
    }

    public FieldRotation<T> applyInverseTo(Rotation r) {
        return composeInverse(r, RotationConvention.VECTOR_OPERATOR);
    }

    public FieldRotation<T> composeInverse(Rotation r, RotationConvention convention) {
        return convention == RotationConvention.VECTOR_OPERATOR ? composeInverseInternal(r) : applyTo(r, revert());
    }

    private FieldRotation<T> composeInverseInternal(Rotation r) {
        return new FieldRotation<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f182q0.multiply(r.getQ0())).add(((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(r.getQ1())).add(this.f184q2.multiply(r.getQ2()))).add(this.f185q3.multiply(r.getQ3())))).negate(), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f183q1.multiply(r.getQ0())).add(((RealFieldElement) this.f185q3.multiply(r.getQ2())).subtract(this.f184q2.multiply(r.getQ3())))).subtract(this.f182q0.multiply(r.getQ1())), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f184q2.multiply(r.getQ0())).add(((RealFieldElement) this.f183q1.multiply(r.getQ3())).subtract(this.f185q3.multiply(r.getQ1())))).subtract(this.f182q0.multiply(r.getQ2())), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.f185q3.multiply(r.getQ0())).add(((RealFieldElement) this.f184q2.multiply(r.getQ1())).subtract(this.f183q1.multiply(r.getQ2())))).subtract(this.f182q0.multiply(r.getQ3())), false);
    }

    public static <T extends RealFieldElement<T>> FieldRotation<T> applyInverseTo(Rotation rOuter, FieldRotation<T> rInner) {
        return new FieldRotation<>((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f182q0.multiply(rOuter.getQ0())).add(((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f183q1.multiply(rOuter.getQ1())).add(((FieldRotation) rInner).f184q2.multiply(rOuter.getQ2()))).add(((FieldRotation) rInner).f185q3.multiply(rOuter.getQ3())))).negate(), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f182q0.multiply(rOuter.getQ1())).add(((RealFieldElement) ((FieldRotation) rInner).f184q2.multiply(rOuter.getQ3())).subtract(((FieldRotation) rInner).f185q3.multiply(rOuter.getQ2())))).subtract(((FieldRotation) rInner).f183q1.multiply(rOuter.getQ0())), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f182q0.multiply(rOuter.getQ2())).add(((RealFieldElement) ((FieldRotation) rInner).f185q3.multiply(rOuter.getQ1())).subtract(((FieldRotation) rInner).f183q1.multiply(rOuter.getQ3())))).subtract(((FieldRotation) rInner).f184q2.multiply(rOuter.getQ0())), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((FieldRotation) rInner).f182q0.multiply(rOuter.getQ3())).add(((RealFieldElement) ((FieldRotation) rInner).f183q1.multiply(rOuter.getQ2())).subtract(((FieldRotation) rInner).f184q2.multiply(rOuter.getQ1())))).subtract(((FieldRotation) rInner).f185q3.multiply(rOuter.getQ0())), false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r47v93, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r47v94, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r47v95, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r47v96, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r47v97, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r47v98, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r47v99, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r47v100, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX WARN: Multi-variable type inference failed */
    private T[][] orthogonalizeMatrix(T[][] m, double threshold) throws NotARotationMatrixException {
        T t = m[0][0];
        T t2 = m[0][1];
        T t3 = m[0][2];
        T t4 = m[1][0];
        T t5 = m[1][1];
        T t6 = m[1][2];
        T t7 = m[2][0];
        T t8 = m[2][1];
        T t9 = m[2][2];
        double fn = 0.0d;
        T[][] o = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(m[0][0].getField(), 3, 3));
        int i = 0;
        T x00 = t;
        T x01 = t2;
        T x02 = t3;
        T x10 = t4;
        T x11 = t5;
        T x12 = t6;
        T x20 = t7;
        T x21 = t8;
        T x22 = t9;
        while (true) {
            i++;
            if (i < 11) {
                RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][0].multiply(x00)).add(m[1][0].multiply(x10))).add(m[2][0].multiply(x20));
                RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][1].multiply(x00)).add(m[1][1].multiply(x10))).add(m[2][1].multiply(x20));
                RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][2].multiply(x00)).add(m[1][2].multiply(x10))).add(m[2][2].multiply(x20));
                RealFieldElement realFieldElement4 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][0].multiply(x01)).add(m[1][0].multiply(x11))).add(m[2][0].multiply(x21));
                RealFieldElement realFieldElement5 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][1].multiply(x01)).add(m[1][1].multiply(x11))).add(m[2][1].multiply(x21));
                RealFieldElement realFieldElement6 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][2].multiply(x01)).add(m[1][2].multiply(x11))).add(m[2][2].multiply(x21));
                RealFieldElement realFieldElement7 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][0].multiply(x02)).add(m[1][0].multiply(x12))).add(m[2][0].multiply(x22));
                RealFieldElement realFieldElement8 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][1].multiply(x02)).add(m[1][1].multiply(x12))).add(m[2][1].multiply(x22));
                RealFieldElement realFieldElement9 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) m[0][2].multiply(x02)).add(m[1][2].multiply(x12))).add(m[2][2].multiply(x22));
                o[0][0] = (RealFieldElement) x00.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x00.multiply(realFieldElement)).add(x01.multiply(realFieldElement2))).add(x02.multiply(realFieldElement3))).subtract(m[0][0])).multiply(0.5d));
                o[0][1] = (RealFieldElement) x01.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x00.multiply(realFieldElement4)).add(x01.multiply(realFieldElement5))).add(x02.multiply(realFieldElement6))).subtract(m[0][1])).multiply(0.5d));
                o[0][2] = (RealFieldElement) x02.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x00.multiply(realFieldElement7)).add(x01.multiply(realFieldElement8))).add(x02.multiply(realFieldElement9))).subtract(m[0][2])).multiply(0.5d));
                o[1][0] = (RealFieldElement) x10.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x10.multiply(realFieldElement)).add(x11.multiply(realFieldElement2))).add(x12.multiply(realFieldElement3))).subtract(m[1][0])).multiply(0.5d));
                o[1][1] = (RealFieldElement) x11.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x10.multiply(realFieldElement4)).add(x11.multiply(realFieldElement5))).add(x12.multiply(realFieldElement6))).subtract(m[1][1])).multiply(0.5d));
                o[1][2] = (RealFieldElement) x12.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x10.multiply(realFieldElement7)).add(x11.multiply(realFieldElement8))).add(x12.multiply(realFieldElement9))).subtract(m[1][2])).multiply(0.5d));
                o[2][0] = (RealFieldElement) x20.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x20.multiply(realFieldElement)).add(x21.multiply(realFieldElement2))).add(x22.multiply(realFieldElement3))).subtract(m[2][0])).multiply(0.5d));
                o[2][1] = (RealFieldElement) x21.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x20.multiply(realFieldElement4)).add(x21.multiply(realFieldElement5))).add(x22.multiply(realFieldElement6))).subtract(m[2][1])).multiply(0.5d));
                o[2][2] = (RealFieldElement) x22.subtract(((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) x20.multiply(realFieldElement7)).add(x21.multiply(realFieldElement8))).add(x22.multiply(realFieldElement9))).subtract(m[2][2])).multiply(0.5d));
                double corr00 = o[0][0].getReal() - m[0][0].getReal();
                double corr01 = o[0][1].getReal() - m[0][1].getReal();
                double corr02 = o[0][2].getReal() - m[0][2].getReal();
                double corr10 = o[1][0].getReal() - m[1][0].getReal();
                double corr11 = o[1][1].getReal() - m[1][1].getReal();
                double corr12 = o[1][2].getReal() - m[1][2].getReal();
                double corr20 = o[2][0].getReal() - m[2][0].getReal();
                double corr21 = o[2][1].getReal() - m[2][1].getReal();
                double corr22 = o[2][2].getReal() - m[2][2].getReal();
                double fn1 = (corr00 * corr00) + (corr01 * corr01) + (corr02 * corr02) + (corr10 * corr10) + (corr11 * corr11) + (corr12 * corr12) + (corr20 * corr20) + (corr21 * corr21) + (corr22 * corr22);
                if (FastMath.abs(fn1 - fn) <= threshold) {
                    return o;
                }
                T x002 = o[0][0];
                T x012 = o[0][1];
                T x022 = o[0][2];
                T x102 = o[1][0];
                T x112 = o[1][1];
                T x122 = o[1][2];
                T x202 = o[2][0];
                fn = fn1;
                x00 = x002;
                x01 = x012;
                x02 = x022;
                x10 = x102;
                x11 = x112;
                x12 = x122;
                x20 = x202;
                x21 = o[2][1];
                x22 = o[2][2];
            } else {
                throw new NotARotationMatrixException(LocalizedFormats.UNABLE_TO_ORTHOGONOLIZE_MATRIX, Integer.valueOf(i - 1));
            }
        }
    }

    public static <T extends RealFieldElement<T>> T distance(FieldRotation<T> r1, FieldRotation<T> r2) {
        return r1.composeInverseInternal(r2).getAngle();
    }
}
