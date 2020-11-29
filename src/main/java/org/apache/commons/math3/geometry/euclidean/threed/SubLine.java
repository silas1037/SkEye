package org.apache.commons.math3.geometry.euclidean.threed;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.Region;

public class SubLine {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;
    private final Line line;
    private final IntervalsSet remainingRegion;

    public SubLine(Line line2, IntervalsSet remainingRegion2) {
        this.line = line2;
        this.remainingRegion = remainingRegion2;
    }

    public SubLine(Vector3D start, Vector3D end, double tolerance) throws MathIllegalArgumentException {
        this(new Line(start, end, tolerance), buildIntervalSet(start, end, tolerance));
    }

    public SubLine(Vector3D start, Vector3D end) throws MathIllegalArgumentException {
        this(start, end, 1.0E-10d);
    }

    public SubLine(Segment segment) throws MathIllegalArgumentException {
        this(segment.getLine(), buildIntervalSet(segment.getStart(), segment.getEnd(), segment.getLine().getTolerance()));
    }

    public List<Segment> getSegments() {
        List<Interval> list = this.remainingRegion.asList();
        List<Segment> segments = new ArrayList<>(list.size());
        for (Interval interval : list) {
            segments.add(new Segment(this.line.toSpace((Point<Euclidean1D>) new Vector1D(interval.getInf())), this.line.toSpace((Point<Euclidean1D>) new Vector1D(interval.getSup())), this.line));
        }
        return segments;
    }

    public Vector3D intersection(SubLine subLine, boolean includeEndPoints) {
        Vector3D v1D = this.line.intersection(subLine.line);
        if (v1D == null) {
            return null;
        }
        Region.Location loc1 = this.remainingRegion.checkPoint((Point) this.line.toSubSpace((Point<Euclidean3D>) v1D));
        Region.Location loc2 = subLine.remainingRegion.checkPoint((Point) subLine.line.toSubSpace((Point<Euclidean3D>) v1D));
        if (includeEndPoints) {
            if (loc1 == Region.Location.OUTSIDE || loc2 == Region.Location.OUTSIDE) {
                return null;
            }
            return v1D;
        } else if (loc1 == Region.Location.INSIDE && loc2 == Region.Location.INSIDE) {
            return v1D;
        } else {
            return null;
        }
    }

    private static IntervalsSet buildIntervalSet(Vector3D start, Vector3D end, double tolerance) throws MathIllegalArgumentException {
        Line line2 = new Line(start, end, tolerance);
        return new IntervalsSet(line2.toSubSpace((Point<Euclidean3D>) start).getX(), line2.toSubSpace((Point<Euclidean3D>) end).getX(), tolerance);
    }
}
