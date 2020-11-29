package org.apache.commons.math3.geometry.euclidean.threed;

public class Segment {
    private final Vector3D end;
    private final Line line;
    private final Vector3D start;

    public Segment(Vector3D start2, Vector3D end2, Line line2) {
        this.start = start2;
        this.end = end2;
        this.line = line2;
    }

    public Vector3D getStart() {
        return this.start;
    }

    public Vector3D getEnd() {
        return this.end;
    }

    public Line getLine() {
        return this.line;
    }
}
