/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/16/2023
 *
 *  Compilation: javac-algs4 KdTree.java
 *  Execution: java-algs4 KdTree
 *  Dependencies: Point2D.java, RectHV.java
 *
 *  Data type to catalog a (0.0, 1.0) x (0.0, 1.0) grid of points using a
 *  2D Kd-Tree and includes operations, isEmpty, size, insert, contains, draw,
 *  range, and nearest. Range is used to find points that are contained within
 *  a rectangular area and nearest finds the nearest point in the set to a
 *  query point.
 *
 *  Can be called directly with a text file as argument
 *
 *  Argument text file formatting:
 *  input5.txt
 *  0.7 0.2
 *  0.5 0.4
 *  0.2 0.3
 *  0.4 0.7
 *  0.9 0.6
 *
 *  % java-algs4 KdTree input5.txt
 *  is KdTree empty true. Size: 0
 *  is KdTree empty false. Size: 5
 *  Points in range [0.39, 0.59] x [0.31, 0.71]: (0.4, 0.7)/(0.5, 0.4)/
 *  contains random point (0.45, 0.25): false
 *  Point (0.5, 0.4) was the nearest to (0.45, 0.25)
 *  contains point (0.5, 0.4): true
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.awt.Color;

public class KdTree {
    private static final boolean X_ALIGNED = false;
    private static final boolean Y_ALIGNED = true;
    private static final byte X_MIN = 1, X_MAX = 3;
    private static final byte Y_MIN = 2, Y_MAX = 4;
    private static final RectHV UNIT_SQUARE = new RectHV(0.0, 0.0, 1.0, 1.0);

    private int size;
    private Node root;

    private static class Node {
        private Point2D p;
        private Node lb;
        private Node rt;

        public Node(Point2D point) {
            p = point;
        }
    }

    /**
     * construct an empty KdTree of size 0
     */
    public KdTree() {
        size = 0;
    }

    /**
     * @return boolean is the KdTree empty
     */
    public boolean isEmpty() { return size == 0; }

    /**
     * @return number of nodes in the KdTree
     */
    public int size() { return size; }

    /**
     * add the point to the KdTree (if it is not already in the set) <br />
     * <br />
     * recursively inserts a point into the 2D KdTree alternating between comparing
     * the <tt>x</tt> value and the <tt>y</tt> value based on tree depth
     *
     * @param p the Point2D to be inserted into the KdTree
     * @throws IllegalArgumentException if <tt>p</tt> is null
     * @see Point2D
     */
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
        // X_ALIGNED = false; Y_ALIGNED = TRUE;
        // orientation is swapped when recursively calling insert
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
    // takes in a rect and creates a new rect with the value determined by
    // xyMinMax swapped with the current point
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

    /**
     * Traverses the KdTree to determine if the point <tt>p</tt> is present
     *
     * @param p the Point2D to search for
     * @return is the point <tt>p</tt> in the KdTree, false if KdTree is empty
     * @throws IllegalArgumentException if <tt>p</tt> is null
     * @see Point2D
     */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Argument cannot be null");
        return contains(root, p, X_ALIGNED);
    }

    private boolean contains(Node node, Point2D point, boolean orientation) {
        if (node == null) { return false; }
        if (node.p.compareTo(point) == 0) { return true; }

        // X_ALIGNED = false; Y_ALIGNED = TRUE;
        // orientation is swapped when recursively calling contains
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

    /**
     * draw all points to standard draw, along with axis aligned dissecting
     * line segment
     */
    public void draw() { draw(root, X_ALIGNED, UNIT_SQUARE); }

    private void draw(Node node, boolean orientation, RectHV rect) {
        if (node == null) return;
        StdDraw.setPenRadius();

        // X_ALIGNED = false; Y_ALIGNED = TRUE;
        // orientation is swapped when recursively calling draw
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
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        StdDraw.show();
    }

    /**
     * all points that are inside the rectangle (or on the boundary) <br />
     * <br />
     * Recursively traverses the KdTree considering the axis alignment based on
     * node depth. First determines if <tt>rect</tt>> intersects the nodes
     * splitting line segment, and traverses both left and right subtrees if it
     * does. If it doesn't intersect then an arbitrary point on <tt>rect</tt> is
     * compared with the <tt>x</tt> or <tt>y</tt> value of the node based on the
     * axis alignment and only the relevant subtree is traversed
     *
     * @param rect RectHV to search for containing points
     * @return Iterable of type Point2D containing points inside <tt>rect</tt>,
     * empty iterable if no point is inside <tt>rect</tt>
     * @throws IllegalArgumentException if <tt>rect</tt> is null
     * @see RectHV
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Argument cannot be null");
        Stack<Point2D> result = new Stack<Point2D>();
        return range(root, rect, result, X_ALIGNED);
    }

    private Stack<Point2D> range(Node node, RectHV queryRect, Stack<Point2D> result, boolean orientation) {
        if (node == null) return result;

        // X_ALIGNED = false; Y_ALIGNED = TRUE;
        // orientation is swapped when recursively calling range
        // if intersectedSplittingLineSegment is true the other if-condition is ignored
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

    /**
     * a nearest neighbor in the set to point p; null if the set is empty<br />
     * <br />
     * Recursively traverses the KdTree considering the axis alignment, based on
     * node depth, to find the nearest point to <tt>p</tt>. Traverses both
     * subtrees, starting with the most relevant subtree unless if the currently
     * known nearest point is closer than the distance from <tt>p</tt> to the
     * potential subtrees containing rectangle
     *
     * @param p Point2D. Finds the nearest point to <tt>p</tt>
     * @return the Point2D that is nearest to <tt>p</tt>, null if set is empty
     * @throws IllegalArgumentException if <tt>p</tt> is null
     * @see Point2D
     */
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

        // X_ALIGNED = false; Y_ALIGNED = TRUE;
        // orientation is swapped when recursively calling nearest
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

    // unit testing of the methods
    public static void main(String[] args) {
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        StdOut.println("is KdTree empty " + kdtree.isEmpty() + ". Size: " + kdtree.size());
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.clear();
        StdDraw.setCanvasSize(1000, 1000);

        StdOut.println("is KdTree empty " + kdtree.isEmpty() + ". Size: " + kdtree.size());
        double xMin = StdRandom.uniformInt(0, 70);
        double yMin = StdRandom.uniformInt(0, 70);
        double xMax = (double) StdRandom.uniformInt((int) xMin + 20, 100) / 100;
        double yMax = (double) StdRandom.uniformInt((int) yMin + 20, 100) / 100;
        xMin /= 100;
        yMin /= 100;
        RectHV testRect = new RectHV(xMin, yMin, xMax, yMax);
        StdDraw.setPenColor(Color.MAGENTA);
        testRect.draw();

        StdOut.print("Points in range " + testRect.toString() + ": ");
        for (Point2D p : kdtree.range(testRect)) {
            StdDraw.setPenColor(Color.red);
            StdDraw.setPenRadius(0.03);
            p.draw();
            StdOut.print(p.toString() + "/");
        }

        double randX = (double) StdRandom.uniformInt(0, 100) / 100;
        double randY = (double) StdRandom.uniformInt(0, 100) / 100;
        Point2D testNear = new Point2D(randX, randY);
        StdOut.println("\ncontains random point " + testNear + ": " + kdtree.contains(testNear));
        Point2D nearest = kdtree.nearest(testNear);
        StdDraw.setPenColor(Color.green);
        StdDraw.setPenRadius(0.02);
        nearest.draw();
        StdDraw.setPenRadius(0.015);
        testNear.draw();
        StdOut.println("Point " + nearest.toString() + " was the nearest to " + testNear);
        StdOut.println("contains point " + nearest.toString() + ": " + kdtree.contains(nearest));

        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.01);
        kdtree.draw();
        StdDraw.show();
    }
}
