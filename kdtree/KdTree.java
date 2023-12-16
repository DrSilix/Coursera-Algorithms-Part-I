/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/15/2023
 *
 *  Compilation: javac-algs4 KdTree.java
 *  Execution: java-algs4 KdTree
 *  Dependencies: Point2D.java, RectHV.java
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.ArrayList;

public class KdTree {
    private static final boolean X_ALIGNED = false;
    private static final boolean Y_ALIGNED = true;
    private static final byte X_MIN = 1, X_MAX = 3;
    private static final byte Y_MIN = 2, Y_MAX = 4;
    private static final RectHV UNIT_SQUARE = new RectHV(0.0, 0.0, 1.0, 1.0);

    private int size;
    private Node root;
    private int debugRangeCalls;

    private static class Node {
        private Point2D p;
        private Node lb;
        private Node rt;

        public Node(Point2D point) {
            p = point;
        }

        public String toString() { return p.toString(); }
    }

    // construct an empty points KdTree
    public KdTree() {
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() { return size == 0; }

    // number of points in the set
    public int size() { return size; }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Argument cannot be null");
        root = insert(root, p, X_ALIGNED);
    }

    private Node insert(Node node, Point2D point, boolean orientation) {
        if (node == null) {
            size++;
            return new Node(point);
        }

        if (point.compareTo(node.p) == 0) { return node; }
        if (!orientation) {
            if (point.x() < node.p.x()) {
                node.lb = insert(node.lb, point, Y_ALIGNED);
            } else {  // if greater than or the same
                node.rt = insert(node.rt, point, Y_ALIGNED);
            }
        } else {
            if (point.y() < node.p.y()) {
                node.lb = insert(node.lb, point, X_ALIGNED);
            } else {  // if greater than or the same
                node.rt = insert(node.rt, point, X_ALIGNED);
            }

        }

        return node;
    }

    // x_aligned left branch = buildNextNodeRect(rect, node.p, X_MAX)
    // x_aligned right branch = buildNextNodeRect(rect, node.p, X_MIN)
    // y_aligned left/bottom branch = buildNextNodeRect(rect, node.p, Y_MAX)
    // y_aligned right/top branch = buildNextNodeRect(rect, node.p, Y_MIN)
    // takes in a rect and uses a point to create and change a new rect with the
    // xy value determined by xyMinMax
    private static RectHV buildNextNodeRect(RectHV prevRect, Point2D point, byte xyMinMax) {
        switch(xyMinMax) {
            case X_MIN:
                return new RectHV(point.x(), prevRect.ymin(), prevRect.xmax(), prevRect.ymax());
            case Y_MIN:
                return new RectHV(prevRect.xmin(), point.y(), prevRect.xmax(), prevRect.ymax());
            case X_MAX:
                return new RectHV(prevRect.xmin(), prevRect.ymin(), point.x(), prevRect.ymax());
            default: // Y_MAX
                return new RectHV(prevRect.xmin(), prevRect.ymin(), prevRect.xmax(), point.y());
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Argument cannot be null");
        return contains(root, p, X_ALIGNED);
    }

    private boolean contains(Node node, Point2D point, boolean orientation) {
        if (node == null) { return false; }
        if (node.p.compareTo(point) == 0) { return true; }
        if (!orientation) {
            if (point.x() < node.p.x()) {
                return contains(node.lb, point, Y_ALIGNED);
            } else {  // if greater than or the same
                return contains(node.rt, point, Y_ALIGNED);
            }
        } else {
            if (point.y() < node.p.y()) {
                return contains(node.lb, point, X_ALIGNED);
            } else {  // if greater than or the same
                return contains(node.rt, point, X_ALIGNED);
            }
        }
    }

    // draw all points to standard draw
    public void draw() { draw(root, X_ALIGNED, UNIT_SQUARE); }

    private void draw(Node node, boolean orientation, RectHV rect) {
        if (node == null) return;
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        StdDraw.setPenRadius();
        if (!orientation) {
            StdDraw.setPenColor(Color.red);
            StdDraw.line(node.p.x(), rect.ymin(), node.p.x(), rect.ymax());
            if (node.lb != null) draw(node.lb, Y_ALIGNED, buildNextNodeRect(rect, node.p, X_MAX));
            if (node.rt != null) draw(node.rt, Y_ALIGNED, buildNextNodeRect(rect, node.p, X_MIN));
        } else {
            StdDraw.setPenColor(Color.blue);
            StdDraw.line(rect.xmin(), node.p.y(), rect.xmax(), node.p.y());
            if (node.lb != null) draw(node.lb, X_ALIGNED, buildNextNodeRect(rect, node.p, Y_MAX));
            if (node.rt != null) draw(node.rt, X_ALIGNED, buildNextNodeRect(rect, node.p, Y_MIN));
        }
        StdDraw.show();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Argument cannot be null");
        debugRangeCalls = 0;
        Stack<Point2D> result = new Stack<Point2D>();
        return range(root, rect, result, X_ALIGNED);
    }

    private Stack<Point2D> range(Node node, RectHV queryRect, Stack<Point2D> result, boolean orientation) {
        if (node == null) return result;
        debugRangeCalls++;

        if (!orientation) {
            boolean intrsSplittingLineSeg = queryRect.intersects(new RectHV(node.p.x(), 0, node.p.x(), 1));
            if (intrsSplittingLineSeg || queryRect.xmin() < node.p.x()) {
                result = range(node.lb, queryRect, result, Y_ALIGNED);
            }
            if (intrsSplittingLineSeg || queryRect.xmin() > node.p.x()) {
                result = range(node.rt, queryRect, result, Y_ALIGNED);
            }
        } else {
            boolean intrsSplittingLineSeg = queryRect.intersects(new RectHV(0, node.p.y(), 1, node.p.y()));
            if (intrsSplittingLineSeg || queryRect.ymin() < node.p.y()) {
                result = range(node.lb, queryRect, result, X_ALIGNED);
            }
            if (intrsSplittingLineSeg || queryRect.ymin() > node.p.y()) {
                result = range(node.rt, queryRect, result, X_ALIGNED);
            }
        }
        if (queryRect.contains(node.p)) result.push(node.p);
        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Argument cannot be null");
        if (size == 0) return null;
        Point2D champion = root.p;
        return nearest(root, p, champion, X_ALIGNED, UNIT_SQUARE);
    }

    private Point2D nearest(Node node, Point2D point, Point2D champion, boolean orientation, RectHV rect) {
        if (node == null) return champion;
        if (champion.distanceSquaredTo(point) < rect.distanceSquaredTo(point)) return champion;

        if (champion.distanceSquaredTo(point) > node.p.distanceSquaredTo(point)) {
            champion = node.p;
        }

        if (!orientation) {
            if (point.x() < node.p.x()) {
                champion = nearest(node.lb, point, champion, Y_ALIGNED, buildNextNodeRect(rect, node.p, X_MAX));
                champion = nearest(node.rt, point, champion, Y_ALIGNED, buildNextNodeRect(rect, node.p, X_MIN));
            } else {  // if greater than or the same
                champion = nearest(node.rt, point, champion, Y_ALIGNED, buildNextNodeRect(rect, node.p, X_MIN));
                champion = nearest(node.lb, point, champion, Y_ALIGNED, buildNextNodeRect(rect, node.p, X_MAX));
            }
        } else {
            if (point.y() < node.p.y()) {
                champion = nearest(node.lb, point, champion, X_ALIGNED, buildNextNodeRect(rect, node.p, Y_MAX));
                champion = nearest(node.rt, point, champion, X_ALIGNED, buildNextNodeRect(rect, node.p, Y_MIN));
            } else {  // if greater than or the same
                champion = nearest(node.rt, point, champion, X_ALIGNED, buildNextNodeRect(rect, node.p, Y_MIN));
                champion = nearest(node.lb, point, champion, X_ALIGNED, buildNextNodeRect(rect, node.p, Y_MAX));
            }
        }
        return champion;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        StdOut.println(kdtree.size());

        StdDraw.enableDoubleBuffering();
        StdDraw.clear();
        StdDraw.setCanvasSize(1000, 1000);
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            points.add(p);
            // StdOut.println(kdtree.size());
        }

        StdDraw.clear();
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.01);
        kdtree.draw();
        StdDraw.show();

        /* StdOut.println("random point: " + kdtree.contains(new Point2D(StdRandom.uniformDouble(), StdRandom.uniformDouble()))); // + " - contains was called:" + kdtree.debugContainsCalls);
        StdOut.println(kdtree.size());*/

        for (Point2D p : points) {
            StdOut.println(p.toString() + ": " + kdtree.contains(p));
        }

        RectHV testRect = new RectHV(0.75, 0.7, 0.9, 1);
        StdDraw.setPenColor(Color.green);
        testRect.draw();
        StdDraw.show();

        for (Point2D p : kdtree.range(testRect)) {
            StdDraw.setPenColor(Color.red);
            StdDraw.setPenRadius(0.03);
            p.draw();
            StdDraw.show();
        }
        StdOut.println("Range was called: " + kdtree.debugRangeCalls);

        Point2D testNear = new Point2D(0.81, 0.30);
        Point2D nearest = kdtree.nearest(testNear);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.setPenRadius(0.01);
        nearest.draw();
        testNear.draw();
        StdDraw.show();
        StdOut.println("Point " + nearest.toString() + " was the nearest"); // and nearest() was called: " + kdtree.debugNearestCalls + " the champion was found after " + kdtree.debugNearestCallsWhenChampFound + " calls, number of champs: " + kdtree.debugNearestNumberOfChamps);
        // StdOut.println(nearest.toString() + ": " + kdtree.contains(nearest) + " at depth " + kdtree.pointDepth(nearest) + " - contains was called:" + kdtree.debugContainsCalls);
    }
}
