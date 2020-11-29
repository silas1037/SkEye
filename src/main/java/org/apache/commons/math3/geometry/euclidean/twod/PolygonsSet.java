package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class PolygonsSet extends AbstractRegion<Euclidean2D, Euclidean1D> {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;
    private Vector2D[][] vertices;

    public PolygonsSet(double tolerance) {
        super(tolerance);
    }

    public PolygonsSet(BSPTree<Euclidean2D> tree, double tolerance) {
        super(tree, tolerance);
    }

    public PolygonsSet(Collection<SubHyperplane<Euclidean2D>> boundary, double tolerance) {
        super(boundary, tolerance);
    }

    public PolygonsSet(double xMin, double xMax, double yMin, double yMax, double tolerance) {
        super(boxBoundary(xMin, xMax, yMin, yMax, tolerance), tolerance);
    }

    public PolygonsSet(double hyperplaneThickness, Vector2D... vertices2) {
        super(verticesToTree(hyperplaneThickness, vertices2), hyperplaneThickness);
    }

    @Deprecated
    public PolygonsSet() {
        this(1.0E-10d);
    }

    @Deprecated
    public PolygonsSet(BSPTree<Euclidean2D> tree) {
        this(tree, 1.0E-10d);
    }

    @Deprecated
    public PolygonsSet(Collection<SubHyperplane<Euclidean2D>> boundary) {
        this(boundary, 1.0E-10d);
    }

    @Deprecated
    public PolygonsSet(double xMin, double xMax, double yMin, double yMax) {
        this(xMin, xMax, yMin, yMax, 1.0E-10d);
    }

    private static Line[] boxBoundary(double xMin, double xMax, double yMin, double yMax, double tolerance) {
        if (xMin >= xMax - tolerance || yMin >= yMax - tolerance) {
            return null;
        }
        Vector2D minMin = new Vector2D(xMin, yMin);
        Vector2D minMax = new Vector2D(xMin, yMax);
        Vector2D maxMin = new Vector2D(xMax, yMin);
        Vector2D maxMax = new Vector2D(xMax, yMax);
        return new Line[]{new Line(minMin, maxMin, tolerance), new Line(maxMin, maxMax, tolerance), new Line(maxMax, minMax, tolerance), new Line(minMax, minMin, tolerance)};
    }

    private static BSPTree<Euclidean2D> verticesToTree(double hyperplaneThickness, Vector2D... vertices2) {
        int n = vertices2.length;
        if (n == 0) {
            return new BSPTree<>(Boolean.TRUE);
        }
        Vertex[] vArray = new Vertex[n];
        for (int i = 0; i < n; i++) {
            vArray[i] = new Vertex(vertices2[i]);
        }
        List<Edge> edges = new ArrayList<>(n);
        for (int i2 = 0; i2 < n; i2++) {
            Vertex start = vArray[i2];
            Vertex end = vArray[(i2 + 1) % n];
            Line line = start.sharedLineWith(end);
            if (line == null) {
                line = new Line(start.getLocation(), end.getLocation(), hyperplaneThickness);
            }
            edges.add(new Edge(start, end, line));
            for (Vertex vertex : vArray) {
                if (!(vertex == start || vertex == end || FastMath.abs(line.getOffset((Point<Euclidean2D>) vertex.getLocation())) > hyperplaneThickness)) {
                    vertex.bindWith(line);
                }
            }
        }
        BSPTree<Euclidean2D> tree = new BSPTree<>();
        insertEdges(hyperplaneThickness, tree, edges);
        return tree;
    }

    private static void insertEdges(double hyperplaneThickness, BSPTree<Euclidean2D> node, List<Edge> edges) {
        Edge inserted = null;
        int index = 0;
        while (inserted == null && index < edges.size()) {
            int index2 = index + 1;
            inserted = edges.get(index);
            if (inserted.getNode() != null) {
                inserted = null;
                index = index2;
            } else if (node.insertCut(inserted.getLine())) {
                inserted.setNode(node);
                index = index2;
            } else {
                inserted = null;
                index = index2;
            }
        }
        if (inserted == null) {
            BSPTree<Euclidean2D> parent = node.getParent();
            if (parent == null || node == parent.getMinus()) {
                node.setAttribute(Boolean.TRUE);
            } else {
                node.setAttribute(Boolean.FALSE);
            }
        } else {
            List<Edge> plusList = new ArrayList<>();
            List<Edge> minusList = new ArrayList<>();
            for (Edge edge : edges) {
                if (edge != inserted) {
                    double startOffset = inserted.getLine().getOffset((Point<Euclidean2D>) edge.getStart().getLocation());
                    double endOffset = inserted.getLine().getOffset((Point<Euclidean2D>) edge.getEnd().getLocation());
                    Side startSide = FastMath.abs(startOffset) <= hyperplaneThickness ? Side.HYPER : startOffset < 0.0d ? Side.MINUS : Side.PLUS;
                    Side endSide = FastMath.abs(endOffset) <= hyperplaneThickness ? Side.HYPER : endOffset < 0.0d ? Side.MINUS : Side.PLUS;
                    switch (startSide) {
                        case PLUS:
                            if (endSide != Side.MINUS) {
                                plusList.add(edge);
                                break;
                            } else {
                                Vertex splitPoint = edge.split(inserted.getLine());
                                minusList.add(splitPoint.getOutgoing());
                                plusList.add(splitPoint.getIncoming());
                                continue;
                            }
                        case MINUS:
                            if (endSide != Side.PLUS) {
                                minusList.add(edge);
                                break;
                            } else {
                                Vertex splitPoint2 = edge.split(inserted.getLine());
                                minusList.add(splitPoint2.getIncoming());
                                plusList.add(splitPoint2.getOutgoing());
                                continue;
                            }
                        default:
                            if (endSide != Side.PLUS) {
                                if (endSide == Side.MINUS) {
                                    minusList.add(edge);
                                    break;
                                } else {
                                    break;
                                }
                            } else {
                                plusList.add(edge);
                                continue;
                            }
                    }
                }
            }
            if (!plusList.isEmpty()) {
                insertEdges(hyperplaneThickness, node.getPlus(), plusList);
            } else {
                node.getPlus().setAttribute(Boolean.FALSE);
            }
            if (!minusList.isEmpty()) {
                insertEdges(hyperplaneThickness, node.getMinus(), minusList);
            } else {
                node.getMinus().setAttribute(Boolean.TRUE);
            }
        }
    }

    /* access modifiers changed from: private */
    public static class Vertex {
        private Edge incoming = null;
        private final List<Line> lines = new ArrayList();
        private final Vector2D location;
        private Edge outgoing = null;

        Vertex(Vector2D location2) {
            this.location = location2;
        }

        public Vector2D getLocation() {
            return this.location;
        }

        public void bindWith(Line line) {
            this.lines.add(line);
        }

        public Line sharedLineWith(Vertex vertex) {
            for (Line line1 : this.lines) {
                Iterator i$ = vertex.lines.iterator();
                while (true) {
                    if (i$.hasNext()) {
                        if (line1 == i$.next()) {
                            return line1;
                        }
                    }
                }
            }
            return null;
        }

        public void setIncoming(Edge incoming2) {
            this.incoming = incoming2;
            bindWith(incoming2.getLine());
        }

        public Edge getIncoming() {
            return this.incoming;
        }

        public void setOutgoing(Edge outgoing2) {
            this.outgoing = outgoing2;
            bindWith(outgoing2.getLine());
        }

        public Edge getOutgoing() {
            return this.outgoing;
        }
    }

    /* access modifiers changed from: private */
    public static class Edge {
        private final Vertex end;
        private final Line line;
        private BSPTree<Euclidean2D> node = null;
        private final Vertex start;

        Edge(Vertex start2, Vertex end2, Line line2) {
            this.start = start2;
            this.end = end2;
            this.line = line2;
            start2.setOutgoing(this);
            end2.setIncoming(this);
        }

        public Vertex getStart() {
            return this.start;
        }

        public Vertex getEnd() {
            return this.end;
        }

        public Line getLine() {
            return this.line;
        }

        public void setNode(BSPTree<Euclidean2D> node2) {
            this.node = node2;
        }

        public BSPTree<Euclidean2D> getNode() {
            return this.node;
        }

        public Vertex split(Line splitLine) {
            Vertex splitVertex = new Vertex(this.line.intersection(splitLine));
            splitVertex.bindWith(splitLine);
            Edge startHalf = new Edge(this.start, splitVertex, this.line);
            Edge endHalf = new Edge(splitVertex, this.end, this.line);
            startHalf.node = this.node;
            endHalf.node = this.node;
            return splitVertex;
        }
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractRegion, org.apache.commons.math3.geometry.partitioning.AbstractRegion, org.apache.commons.math3.geometry.partitioning.Region
    public AbstractRegion<Euclidean2D, Euclidean1D> buildNew(BSPTree<Euclidean2D> tree) {
        return new PolygonsSet(tree, getTolerance());
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractRegion
    public void computeGeometricalProperties() {
        Vector2D[][] v = getVertices();
        if (v.length == 0) {
            BSPTree<Euclidean2D> tree = getTree(false);
            if (tree.getCut() != null || !((Boolean) tree.getAttribute()).booleanValue()) {
                setSize(0.0d);
                setBarycenter((Point) new Vector2D(0.0d, 0.0d));
                return;
            }
            setSize(Double.POSITIVE_INFINITY);
            setBarycenter((Point) Vector2D.NaN);
        } else if (v[0][0] == null) {
            setSize(Double.POSITIVE_INFINITY);
            setBarycenter((Point) Vector2D.NaN);
        } else {
            double sum = 0.0d;
            double sumX = 0.0d;
            double sumY = 0.0d;
            for (Vector2D[] loop : v) {
                double x1 = loop[loop.length - 1].getX();
                double y1 = loop[loop.length - 1].getY();
                for (Vector2D point : loop) {
                    x1 = point.getX();
                    y1 = point.getY();
                    double factor = (x1 * y1) - (y1 * x1);
                    sum += factor;
                    sumX += (x1 + x1) * factor;
                    sumY += (y1 + y1) * factor;
                }
            }
            if (sum < 0.0d) {
                setSize(Double.POSITIVE_INFINITY);
                setBarycenter((Point) Vector2D.NaN);
                return;
            }
            setSize(sum / 2.0d);
            setBarycenter((Point) new Vector2D(sumX / (3.0d * sum), sumY / (3.0d * sum)));
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v16, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v18, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v1, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D[] */
    /* JADX DEBUG: Multi-variable search result rejected for r0v30, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D[][] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r19v36, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARN: Type inference failed for: r19v41, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.twod.Vector2D[][] getVertices() {
        /*
        // Method dump skipped, instructions count: 624
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet.getVertices():org.apache.commons.math3.geometry.euclidean.twod.Vector2D[][]");
    }

    private int naturalFollowerConnections(List<ConnectableSegment> segments) {
        int connected = 0;
        for (ConnectableSegment segment : segments) {
            if (segment.getNext() == null) {
                BSPTree<Euclidean2D> node = segment.getNode();
                BSPTree<Euclidean2D> end = segment.getEndNode();
                Iterator i$ = segments.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    ConnectableSegment candidateNext = i$.next();
                    if (candidateNext.getPrevious() == null && candidateNext.getNode() == end && candidateNext.getStartNode() == node) {
                        segment.setNext(candidateNext);
                        candidateNext.setPrevious(segment);
                        connected++;
                        break;
                    }
                }
            }
        }
        return connected;
    }

    private int splitEdgeConnections(List<ConnectableSegment> segments) {
        int connected = 0;
        for (ConnectableSegment segment : segments) {
            if (segment.getNext() == null) {
                Hyperplane<Euclidean2D> hyperplane = segment.getNode().getCut().getHyperplane();
                BSPTree<Euclidean2D> end = segment.getEndNode();
                Iterator i$ = segments.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    ConnectableSegment candidateNext = i$.next();
                    if (candidateNext.getPrevious() == null && candidateNext.getNode().getCut().getHyperplane() == hyperplane && candidateNext.getStartNode() == end) {
                        segment.setNext(candidateNext);
                        candidateNext.setPrevious(segment);
                        connected++;
                        break;
                    }
                }
            }
        }
        return connected;
    }

    private int closeVerticesConnections(List<ConnectableSegment> segments) {
        int connected = 0;
        for (ConnectableSegment segment : segments) {
            if (segment.getNext() == null && segment.getEnd() != null) {
                Vector2D end = segment.getEnd();
                ConnectableSegment selectedNext = null;
                double min = Double.POSITIVE_INFINITY;
                for (ConnectableSegment candidateNext : segments) {
                    if (candidateNext.getPrevious() == null && candidateNext.getStart() != null) {
                        double distance = Vector2D.distance(end, candidateNext.getStart());
                        if (distance < min) {
                            selectedNext = candidateNext;
                            min = distance;
                        }
                    }
                }
                if (min <= getTolerance()) {
                    segment.setNext(selectedNext);
                    selectedNext.setPrevious(segment);
                    connected++;
                }
            }
        }
        return connected;
    }

    private ConnectableSegment getUnprocessed(List<ConnectableSegment> segments) {
        for (ConnectableSegment segment : segments) {
            if (!segment.isProcessed()) {
                return segment;
            }
        }
        return null;
    }

    private List<Segment> followLoop(ConnectableSegment defining) {
        List<Segment> loop = new ArrayList<>();
        loop.add(defining);
        defining.setProcessed(true);
        ConnectableSegment next = defining.getNext();
        while (next != defining && next != null) {
            loop.add(next);
            next.setProcessed(true);
            next = next.getNext();
        }
        if (next == null) {
            for (ConnectableSegment previous = defining.getPrevious(); previous != null; previous = previous.getPrevious()) {
                loop.add(0, previous);
                previous.setProcessed(true);
            }
        }
        filterSpuriousVertices(loop);
        if (loop.size() != 2 || loop.get(0).getStart() == null) {
            return loop;
        }
        return null;
    }

    private void filterSpuriousVertices(List<Segment> loop) {
        int i = 0;
        while (i < loop.size()) {
            Segment previous = loop.get(i);
            int j = (i + 1) % loop.size();
            Segment next = loop.get(j);
            if (next != null && Precision.equals(previous.getLine().getAngle(), next.getLine().getAngle(), Precision.EPSILON)) {
                loop.set(j, new Segment(previous.getStart(), next.getEnd(), previous.getLine()));
                loop.remove(i);
                i--;
            }
            i++;
        }
    }

    /* access modifiers changed from: private */
    public static class ConnectableSegment extends Segment {
        private final BSPTree<Euclidean2D> endNode;
        private ConnectableSegment next = null;
        private final BSPTree<Euclidean2D> node;
        private ConnectableSegment previous = null;
        private boolean processed = false;
        private final BSPTree<Euclidean2D> startNode;

        ConnectableSegment(Vector2D start, Vector2D end, Line line, BSPTree<Euclidean2D> node2, BSPTree<Euclidean2D> startNode2, BSPTree<Euclidean2D> endNode2) {
            super(start, end, line);
            this.node = node2;
            this.startNode = startNode2;
            this.endNode = endNode2;
        }

        public BSPTree<Euclidean2D> getNode() {
            return this.node;
        }

        public BSPTree<Euclidean2D> getStartNode() {
            return this.startNode;
        }

        public BSPTree<Euclidean2D> getEndNode() {
            return this.endNode;
        }

        public ConnectableSegment getPrevious() {
            return this.previous;
        }

        public void setPrevious(ConnectableSegment previous2) {
            this.previous = previous2;
        }

        public ConnectableSegment getNext() {
            return this.next;
        }

        public void setNext(ConnectableSegment next2) {
            this.next = next2;
        }

        public void setProcessed(boolean processed2) {
            this.processed = processed2;
        }

        public boolean isProcessed() {
            return this.processed;
        }
    }

    /* access modifiers changed from: private */
    public static class SegmentsBuilder implements BSPTreeVisitor<Euclidean2D> {
        private final List<ConnectableSegment> segments = new ArrayList();
        private final double tolerance;

        SegmentsBuilder(double tolerance2) {
            this.tolerance = tolerance2;
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public BSPTreeVisitor.Order visitOrder(BSPTree<Euclidean2D> bSPTree) {
            return BSPTreeVisitor.Order.MINUS_SUB_PLUS;
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public void visitInternalNode(BSPTree<Euclidean2D> node) {
            BoundaryAttribute<Euclidean2D> attribute = (BoundaryAttribute) node.getAttribute();
            Iterable<BSPTree<Euclidean2D>> splitters = attribute.getSplitters();
            if (attribute.getPlusOutside() != null) {
                addContribution(attribute.getPlusOutside(), node, splitters, false);
            }
            if (attribute.getPlusInside() != null) {
                addContribution(attribute.getPlusInside(), node, splitters, true);
            }
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public void visitLeafNode(BSPTree<Euclidean2D> bSPTree) {
        }

        private void addContribution(SubHyperplane<Euclidean2D> sub, BSPTree<Euclidean2D> node, Iterable<BSPTree<Euclidean2D>> splitters, boolean reversed) {
            Vector2D endV;
            Line line = (Line) sub.getHyperplane();
            for (Interval i : ((IntervalsSet) ((AbstractSubHyperplane) sub).getRemainingRegion()).asList()) {
                Vector2D startV = Double.isInfinite(i.getInf()) ? null : line.toSpace((Point<Euclidean1D>) new Vector1D(i.getInf()));
                if (Double.isInfinite(i.getSup())) {
                    endV = null;
                } else {
                    endV = line.toSpace((Point<Euclidean1D>) new Vector1D(i.getSup()));
                }
                BSPTree<Euclidean2D> startN = selectClosest(startV, splitters);
                BSPTree<Euclidean2D> endN = selectClosest(endV, splitters);
                if (reversed) {
                    this.segments.add(new ConnectableSegment(endV, startV, line.getReverse(), node, endN, startN));
                } else {
                    this.segments.add(new ConnectableSegment(startV, endV, line, node, startN, endN));
                }
            }
        }

        private BSPTree<Euclidean2D> selectClosest(Vector2D point, Iterable<BSPTree<Euclidean2D>> candidates) {
            BSPTree<Euclidean2D> selected = null;
            double min = Double.POSITIVE_INFINITY;
            for (BSPTree<Euclidean2D> node : candidates) {
                double distance = FastMath.abs(node.getCut().getHyperplane().getOffset(point));
                if (distance < min) {
                    selected = node;
                    min = distance;
                }
            }
            if (min <= this.tolerance) {
                return selected;
            }
            return null;
        }

        public List<ConnectableSegment> getSegments() {
            return this.segments;
        }
    }
}
