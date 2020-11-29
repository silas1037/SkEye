package org.apache.commons.math3.geometry.euclidean.threed;

public final class RotationOrder {
    public static final RotationOrder XYX = new RotationOrder("XYX", Vector3D.PLUS_I, Vector3D.PLUS_J, Vector3D.PLUS_I);
    public static final RotationOrder XYZ = new RotationOrder("XYZ", Vector3D.PLUS_I, Vector3D.PLUS_J, Vector3D.PLUS_K);
    public static final RotationOrder XZX = new RotationOrder("XZX", Vector3D.PLUS_I, Vector3D.PLUS_K, Vector3D.PLUS_I);
    public static final RotationOrder XZY = new RotationOrder("XZY", Vector3D.PLUS_I, Vector3D.PLUS_K, Vector3D.PLUS_J);
    public static final RotationOrder YXY = new RotationOrder("YXY", Vector3D.PLUS_J, Vector3D.PLUS_I, Vector3D.PLUS_J);
    public static final RotationOrder YXZ = new RotationOrder("YXZ", Vector3D.PLUS_J, Vector3D.PLUS_I, Vector3D.PLUS_K);
    public static final RotationOrder YZX = new RotationOrder("YZX", Vector3D.PLUS_J, Vector3D.PLUS_K, Vector3D.PLUS_I);
    public static final RotationOrder YZY = new RotationOrder("YZY", Vector3D.PLUS_J, Vector3D.PLUS_K, Vector3D.PLUS_J);
    public static final RotationOrder ZXY = new RotationOrder("ZXY", Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_J);
    public static final RotationOrder ZXZ = new RotationOrder("ZXZ", Vector3D.PLUS_K, Vector3D.PLUS_I, Vector3D.PLUS_K);
    public static final RotationOrder ZYX = new RotationOrder("ZYX", Vector3D.PLUS_K, Vector3D.PLUS_J, Vector3D.PLUS_I);
    public static final RotationOrder ZYZ = new RotationOrder("ZYZ", Vector3D.PLUS_K, Vector3D.PLUS_J, Vector3D.PLUS_K);

    /* renamed from: a1 */
    private final Vector3D f199a1;

    /* renamed from: a2 */
    private final Vector3D f200a2;

    /* renamed from: a3 */
    private final Vector3D f201a3;
    private final String name;

    private RotationOrder(String name2, Vector3D a1, Vector3D a2, Vector3D a3) {
        this.name = name2;
        this.f199a1 = a1;
        this.f200a2 = a2;
        this.f201a3 = a3;
    }

    public String toString() {
        return this.name;
    }

    public Vector3D getA1() {
        return this.f199a1;
    }

    public Vector3D getA2() {
        return this.f200a2;
    }

    public Vector3D getA3() {
        return this.f201a3;
    }
}
