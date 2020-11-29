package org.apache.commons.math3.geometry.spherical.twod;

import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.util.MathUtils;

public class Edge {
    private final Circle circle;
    private Vertex end;
    private final double length;
    private final Vertex start;

    Edge(Vertex start2, Vertex end2, double length2, Circle circle2) {
        this.start = start2;
        this.end = end2;
        this.length = length2;
        this.circle = circle2;
        start2.setOutgoing(this);
        end2.setIncoming(this);
    }

    public Vertex getStart() {
        return this.start;
    }

    public Vertex getEnd() {
        return this.end;
    }

    public double getLength() {
        return this.length;
    }

    public Circle getCircle() {
        return this.circle;
    }

    public Vector3D getPointAt(double alpha) {
        return this.circle.getPointAt(this.circle.getPhase(this.start.getLocation().getVector()) + alpha);
    }

    /* access modifiers changed from: package-private */
    public void setNextEdge(Edge next) {
        this.end = next.getStart();
        this.end.setIncoming(this);
        this.end.bindWith(getCircle());
    }

    /* access modifiers changed from: package-private */
    public void split(Circle splitCircle, List<Edge> outsideList, List<Edge> insideList) {
        double edgeStart = this.circle.getPhase(this.start.getLocation().getVector());
        Arc arc = this.circle.getInsideArc(splitCircle);
        double arcRelativeStart = MathUtils.normalizeAngle(arc.getInf(), 3.141592653589793d + edgeStart) - edgeStart;
        double arcRelativeEnd = arcRelativeStart + arc.getSize();
        double unwrappedEnd = arcRelativeEnd - 6.283185307179586d;
        double tolerance = this.circle.getTolerance();
        Vertex previousVertex = this.start;
        if (unwrappedEnd >= this.length - tolerance) {
            insideList.add(this);
            return;
        }
        double alreadyManagedLength = 0.0d;
        if (unwrappedEnd >= 0.0d) {
            previousVertex = addSubEdge(previousVertex, new Vertex(new S2Point(this.circle.getPointAt(edgeStart + unwrappedEnd))), unwrappedEnd, insideList, splitCircle);
            alreadyManagedLength = unwrappedEnd;
        }
        if (arcRelativeStart < this.length - tolerance) {
            Vertex previousVertex2 = addSubEdge(previousVertex, new Vertex(new S2Point(this.circle.getPointAt(edgeStart + arcRelativeStart))), arcRelativeStart - alreadyManagedLength, outsideList, splitCircle);
            if (arcRelativeEnd >= this.length - tolerance) {
                addSubEdge(previousVertex2, this.end, this.length - arcRelativeStart, insideList, splitCircle);
            } else {
                addSubEdge(addSubEdge(previousVertex2, new Vertex(new S2Point(this.circle.getPointAt(edgeStart + arcRelativeStart))), arcRelativeStart - arcRelativeStart, insideList, splitCircle), this.end, this.length - arcRelativeStart, outsideList, splitCircle);
            }
        } else if (unwrappedEnd >= 0.0d) {
            addSubEdge(previousVertex, this.end, this.length - alreadyManagedLength, outsideList, splitCircle);
        } else {
            outsideList.add(this);
        }
    }

    private Vertex addSubEdge(Vertex subStart, Vertex subEnd, double subLength, List<Edge> list, Circle splitCircle) {
        if (subLength <= this.circle.getTolerance()) {
            return subStart;
        }
        subEnd.bindWith(splitCircle);
        list.add(new Edge(subStart, subEnd, subLength, this.circle));
        return subEnd;
    }
}
