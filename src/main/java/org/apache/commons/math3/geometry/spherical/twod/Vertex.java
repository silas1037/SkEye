package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Vertex {
    private final List<Circle> circles = new ArrayList();
    private Edge incoming = null;
    private final S2Point location;
    private Edge outgoing = null;

    Vertex(S2Point location2) {
        this.location = location2;
    }

    public S2Point getLocation() {
        return this.location;
    }

    /* access modifiers changed from: package-private */
    public void bindWith(Circle circle) {
        this.circles.add(circle);
    }

    /* access modifiers changed from: package-private */
    public Circle sharedCircleWith(Vertex vertex) {
        for (Circle circle1 : this.circles) {
            Iterator i$ = vertex.circles.iterator();
            while (true) {
                if (i$.hasNext()) {
                    if (circle1 == i$.next()) {
                        return circle1;
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void setIncoming(Edge incoming2) {
        this.incoming = incoming2;
        bindWith(incoming2.getCircle());
    }

    public Edge getIncoming() {
        return this.incoming;
    }

    /* access modifiers changed from: package-private */
    public void setOutgoing(Edge outgoing2) {
        this.outgoing = outgoing2;
        bindWith(outgoing2.getCircle());
    }

    public Edge getOutgoing() {
        return this.outgoing;
    }
}
