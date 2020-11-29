package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;

class NestedLoops {
    private Vector2D[] loop;
    private boolean originalIsClockwise;
    private Region<Euclidean2D> polygon;
    private List<NestedLoops> surrounded;
    private final double tolerance;

    NestedLoops(double tolerance2) {
        this.surrounded = new ArrayList();
        this.tolerance = tolerance2;
    }

    /* JADX WARN: Type inference failed for: r2v14, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARN: Type inference failed for: r4v0, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private NestedLoops(org.apache.commons.math3.geometry.euclidean.twod.Vector2D[] r13, double r14) throws org.apache.commons.math3.exception.MathIllegalArgumentException {
        /*
        // Method dump skipped, instructions count: 125
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.NestedLoops.<init>(org.apache.commons.math3.geometry.euclidean.twod.Vector2D[], double):void");
    }

    public void add(Vector2D[] bLoop) throws MathIllegalArgumentException {
        add(new NestedLoops(bLoop, this.tolerance));
    }

    private void add(NestedLoops node) throws MathIllegalArgumentException {
        for (NestedLoops child : this.surrounded) {
            if (child.polygon.contains(node.polygon)) {
                child.add(node);
                return;
            }
        }
        Iterator<NestedLoops> iterator = this.surrounded.iterator();
        while (iterator.hasNext()) {
            NestedLoops child2 = iterator.next();
            if (node.polygon.contains(child2.polygon)) {
                node.surrounded.add(child2);
                iterator.remove();
            }
        }
        RegionFactory<Euclidean2D> factory = new RegionFactory<>();
        for (NestedLoops child3 : this.surrounded) {
            if (!factory.intersection(node.polygon, child3.polygon).isEmpty()) {
                throw new MathIllegalArgumentException(LocalizedFormats.CROSSING_BOUNDARY_LOOPS, new Object[0]);
            }
        }
        this.surrounded.add(node);
    }

    public void correctOrientation() {
        for (NestedLoops child : this.surrounded) {
            child.setClockWise(true);
        }
    }

    private void setClockWise(boolean clockwise) {
        if (this.originalIsClockwise ^ clockwise) {
            int min = -1;
            int max = this.loop.length;
            while (true) {
                min++;
                max--;
                if (min >= max) {
                    break;
                }
                Vector2D tmp = this.loop[min];
                this.loop[min] = this.loop[max];
                this.loop[max] = tmp;
            }
        }
        for (NestedLoops child : this.surrounded) {
            child.setClockWise(!clockwise);
        }
    }
}
