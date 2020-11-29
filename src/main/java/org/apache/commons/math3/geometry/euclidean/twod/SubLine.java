package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;

public class SubLine extends AbstractSubHyperplane<Euclidean2D, Euclidean1D> {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;

    public SubLine(Hyperplane<Euclidean2D> hyperplane, Region<Euclidean1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }

    public SubLine(Vector2D start, Vector2D end, double tolerance) {
        super(new Line(start, end, tolerance), buildIntervalSet(start, end, tolerance));
    }

    @Deprecated
    public SubLine(Vector2D start, Vector2D end) {
        this(start, end, 1.0E-10d);
    }

    public SubLine(Segment segment) {
        super(segment.getLine(), buildIntervalSet(segment.getStart(), segment.getEnd(), segment.getLine().getTolerance()));
    }

    /* JADX WARN: Type inference failed for: r6v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<org.apache.commons.math3.geometry.euclidean.twod.Segment> getSegments() {
        /*
            r10 = this;
            org.apache.commons.math3.geometry.partitioning.Hyperplane r3 = r10.getHyperplane()
            org.apache.commons.math3.geometry.euclidean.twod.Line r3 = (org.apache.commons.math3.geometry.euclidean.twod.Line) r3
            org.apache.commons.math3.geometry.partitioning.Region r7 = r10.getRemainingRegion()
            org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet r7 = (org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet) r7
            java.util.List r4 = r7.asList()
            java.util.ArrayList r5 = new java.util.ArrayList
            int r7 = r4.size()
            r5.<init>(r7)
            java.util.Iterator r1 = r4.iterator()
        L_0x001d:
            boolean r7 = r1.hasNext()
            if (r7 == 0) goto L_0x004c
            java.lang.Object r2 = r1.next()
            org.apache.commons.math3.geometry.euclidean.oned.Interval r2 = (org.apache.commons.math3.geometry.euclidean.oned.Interval) r2
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r7 = new org.apache.commons.math3.geometry.euclidean.oned.Vector1D
            double r8 = r2.getInf()
            r7.<init>(r8)
            org.apache.commons.math3.geometry.euclidean.twod.Vector2D r6 = r3.toSpace(r7)
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r7 = new org.apache.commons.math3.geometry.euclidean.oned.Vector1D
            double r8 = r2.getSup()
            r7.<init>(r8)
            org.apache.commons.math3.geometry.euclidean.twod.Vector2D r0 = r3.toSpace(r7)
            org.apache.commons.math3.geometry.euclidean.twod.Segment r7 = new org.apache.commons.math3.geometry.euclidean.twod.Segment
            r7.<init>(r6, r0, r3)
            r5.add(r7)
            goto L_0x001d
        L_0x004c:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.SubLine.getSegments():java.util.List");
    }

    public Vector2D intersection(SubLine subLine, boolean includeEndPoints) {
        Line line1 = (Line) getHyperplane();
        Line line2 = (Line) subLine.getHyperplane();
        Vector2D v2D = line1.intersection(line2);
        if (v2D == null) {
            return null;
        }
        Region.Location loc1 = getRemainingRegion().checkPoint(line1.toSubSpace((Point<Euclidean2D>) v2D));
        Region.Location loc2 = subLine.getRemainingRegion().checkPoint(line2.toSubSpace((Point<Euclidean2D>) v2D));
        if (includeEndPoints) {
            if (loc1 == Region.Location.OUTSIDE || loc2 == Region.Location.OUTSIDE) {
                return null;
            }
            return v2D;
        } else if (loc1 == Region.Location.INSIDE && loc2 == Region.Location.INSIDE) {
            return v2D;
        } else {
            return null;
        }
    }

    /* JADX WARN: Type inference failed for: r2v0, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARN: Type inference failed for: r4v0, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet buildIntervalSet(org.apache.commons.math3.geometry.euclidean.twod.Vector2D r8, org.apache.commons.math3.geometry.euclidean.twod.Vector2D r9, double r10) {
        /*
            org.apache.commons.math3.geometry.euclidean.twod.Line r0 = new org.apache.commons.math3.geometry.euclidean.twod.Line
            r0.<init>(r8, r9, r10)
            org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet r1 = new org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r2 = r0.toSubSpace(r8)
            double r2 = r2.getX()
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r4 = r0.toSubSpace(r9)
            double r4 = r4.getX()
            r6 = r10
            r1.<init>(r2, r4, r6)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.SubLine.buildIntervalSet(org.apache.commons.math3.geometry.euclidean.twod.Vector2D, org.apache.commons.math3.geometry.euclidean.twod.Vector2D, double):org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet");
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    public AbstractSubHyperplane<Euclidean2D, Euclidean1D> buildNew(Hyperplane<Euclidean2D> hyperplane, Region<Euclidean1D> remainingRegion) {
        return new SubLine(hyperplane, remainingRegion);
    }

    /* JADX WARN: Type inference failed for: r15v0, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.partitioning.SubHyperplane.SplitSubHyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> split(org.apache.commons.math3.geometry.partitioning.Hyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r24) {
        /*
        // Method dump skipped, instructions count: 294
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.SubLine.split(org.apache.commons.math3.geometry.partitioning.Hyperplane):org.apache.commons.math3.geometry.partitioning.SubHyperplane$SplitSubHyperplane");
    }
}
