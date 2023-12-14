/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/13/2023
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
import java.awt.Font;
import java.util.ArrayList;

public class KdTree {
    private static final boolean X_ALIGNED = false;
    private static final boolean Y_ALIGNED = true;

    private int size;
    private Node root;
    private int debugNum;
    private int debugContainsCalls;
    private int debugRangeCalls;
    private int debugNearestCalls;
    private int debugNearestCallsWhenChampFound;

    private static class Node {
        private Point2D p;
        private RectHV r;
        private Node lb;
        private Node rt;
        private int i;

        public Node(Point2D point) {
            p = point;
        }

        public String toString() { return p.toString() + ":" + r.toString(); }
    }

    public int getDebugRangeCalls() { return debugRangeCalls; }
    public int getDebugNearestCalls() { return debugNearestCalls; }
    public int getDebugNearestCallsWhenChampFound() { return debugNearestCallsWhenChampFound; }

    // construct an empty points KdTree
    public KdTree() {
        size = 0;
        debugNum = 0;
    }

    // is the set empty?
    public boolean isEmpty() { return size == 0; }

    // number of points in the set
    public int size() { return size; }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        root = insert(root, p, X_ALIGNED);
        // draw();
        // sleep(1000);
    }

    private Node insert(Node node, Point2D point, boolean orientation) {
        if (node == null) {
            Node n = new Node(point);
            if (size == 0) { n.r = new RectHV(0.0, 0.0, 1.0, 1.0); }
            size++;
            n.i = ++debugNum;
            return n;
        }
        if (point.compareTo(node.p) == 0) { return node; }
        if (!orientation) {
            if (point.x() < node.p.x()) {
                node.lb = insert(node.lb, point, Y_ALIGNED);
                node.lb.r = new RectHV(node.r.xmin(), node.r.ymin(), node.p.x(), node.r.ymax());
            } else {  // if greater than or the same
                node.rt = insert(node.rt, point, Y_ALIGNED);
                node.rt.r = new RectHV(node.p.x(), node.r.ymin(), node.r.xmax(), node.r.ymax());
            }
        } else {
            if (point.y() < node.p.y()) {
                node.lb = insert(node.lb, point, X_ALIGNED);
                node.lb.r = new RectHV(node.r.xmin(), node.r.ymin(), node.r.xmax(), node.p.y());
            } else {  // if greater than or the same
                node.rt = insert(node.rt, point, X_ALIGNED);
                node.rt.r = new RectHV(node.r.xmin(), node.p.y(), node.r.xmax(), node.r.ymax());
            }

        }
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        debugContainsCalls = 1;
        return contains(root, p, X_ALIGNED);
    }

    private boolean contains(Node node, Point2D point, boolean orientation) {
        debugContainsCalls++;
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
    public void draw() { draw(root, X_ALIGNED); }

    private void draw(Node node, boolean orientation) {
        /* StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.01);
        node.p.draw(); */
        StdDraw.setFont(new Font("Sans Serif", Font.PLAIN, 10));
        StdDraw.text(node.p.x() + 0.008, node.p.y() - 0.008, Integer.toString(node.i));
        // StdOut.println("drawing point " + node.toString());
        StdDraw.setPenRadius(0.001);
        if (!orientation) {
            StdDraw.setPenColor(Color.red);
            StdDraw.line(node.p.x(), node.r.ymin(), node.p.x(), node.r.ymax());
            if (node.lb != null) { draw(node.lb, Y_ALIGNED); }
            if (node.rt != null) { draw(node.rt, Y_ALIGNED); }
        } else {
            StdDraw.setPenColor(Color.blue);
            StdDraw.line(node.r.xmin(), node.p.y(), node.r.xmax(), node.p.y());
            if (node.lb != null) { draw(node.lb, X_ALIGNED); }
            if (node.rt != null) { draw(node.rt, X_ALIGNED); }
        }
        // sleep(1000);
        StdDraw.show();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        debugRangeCalls = 1;
        Stack<Point2D> result = new Stack<Point2D>();
        return range(root, rect, result);
    }

    private Stack<Point2D> range(Node node, RectHV rect, Stack<Point2D> result) {
        debugRangeCalls++;
        if (node.lb != null && rect.intersects(node.lb.r)) result = range(node.lb, rect, result);
        if (node.rt != null && rect.intersects(node.rt.r)) result = range(node.rt, rect, result);
        if (rect.contains(node.p)) result.push(node.p);
        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        debugNearestCalls = 1;
        Point2D champion = root.p;
        return nearest(root, p, champion, X_ALIGNED);
    }

    private Point2D nearest(Node node, Point2D point, Point2D champion, boolean orientation) {
        if (node == null) return champion;
        if (champion.distanceSquaredTo(point) < node.r.distanceSquaredTo(point)) return champion;

        debugNearestCalls++;
        StdDraw.setPenColor(Color.blue);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        if (champion.distanceSquaredTo(point) > node.p.distanceSquaredTo(point)) {
            debugNearestCallsWhenChampFound = debugNearestCalls;
            champion = node.p;
        }

        if (!orientation) {
            if (point.x() < node.p.x()) {
                champion = nearest(node.lb, point, champion, Y_ALIGNED);
                champion = nearest(node.rt, point, champion, Y_ALIGNED);
            } else {  // if greater than or the same
                champion = nearest(node.rt, point, champion, Y_ALIGNED);
                champion = nearest(node.lb, point, champion, Y_ALIGNED);
            }
        } else {
            if (point.y() < node.p.y()) {
                champion = nearest(node.lb, point, champion, X_ALIGNED);
                champion = nearest(node.rt, point, champion, X_ALIGNED);
            } else {  // if greater than or the same
                champion = nearest(node.rt, point, champion, X_ALIGNED);
                champion = nearest(node.lb, point, champion, X_ALIGNED);
            }

        }
        return champion;
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        // StdDraw.setPenColor(Color.black);
        // StdDraw.setPenRadius(0.01);
        // kdtree.draw();
        // StdDraw.show();

        StdOut.println("random point: " + kdtree.contains(new Point2D(0.920132, 0.228911)) + " - contains was called:" + kdtree.debugContainsCalls);
        StdOut.println(kdtree.size());

        /* for (Point2D p : points) {
            StdOut.println(p.toString() + ": " + kdtree.contains(p) + " - contains was called:" + kdtree.debugContainsCalls);
        }*/

        /* RectHV testRect = new RectHV(0.75, 0.7, 0.9, 1);
        StdDraw.setPenColor(Color.green);
        testRect.draw();
        StdDraw.show();

        for (Point2D p : kdtree.range(testRect)) {
            StdDraw.setPenColor(Color.red);
            StdDraw.setPenRadius(0.03);
            p.draw();
            StdDraw.show();
        }
        StdOut.println("Range was called: " + kdtree.debugRangeCalls);*/

        Point2D testNear = new Point2D(0.81, 0.30);
        Point2D nearest = kdtree.nearest(testNear);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.setPenRadius(0.01);
        nearest.draw();
        testNear.draw();
        StdDraw.show();
        StdOut.println("Point " + nearest.toString() + " was the nearest and nearest() was called: " + kdtree.debugNearestCalls + " the champion was found after " + kdtree.debugNearestCallsWhenChampFound + " calls");

        RectHV exampleRect = new RectHV(0.4, 0.3, 0.8, 0.6);
        Point2D exampleP1 = new Point2D(0.1, 0.4);
        Point2D exampleP2 = new Point2D(0.0, 0.0);
        StdOut.println("exampleP1 distance = " + exampleRect.distanceTo(exampleP1) + ", exampleP2 distance = " + exampleRect.distanceTo(exampleP2));
    }
}
