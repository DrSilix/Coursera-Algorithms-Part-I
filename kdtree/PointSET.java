/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/16/2023
 *
 *  Compilation: javac-algs4 PointSET.java
 *  Execution: java-algs4 PointSET
 *  Dependencies: Point2D.java, RectHV.java
 *
 *  Data type to catalog a (0.0, 1.0) x (0.0, 1.0) grid of points using a
 *  Balanced BST (R-B BST) and includes operations, isEmpty, size, insert,
 *  contains, draw, range, and nearest. Range is used to find points that are
 *  contained within a rectangular area and nearest finds the nearest point in
 *  the set to a query point.
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
 *  % java-algs4 PointSET input5.txt
 *  is PointSET empty true. Size: 0
 *  is PointSET empty false. Size: 5
 *  Points in range [0.39, 0.59] x [0.31, 0.71]: (0.4, 0.7)/(0.5, 0.4)/
 *  contains random point (0.45, 0.25): false
 *  Point (0.5, 0.4) was the nearest to (0.45, 0.25)
 *  contains point (0.5, 0.4): true
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class PointSET {
    private SET<Point2D> bst;
    private int size;

    /**
     * construct an empty SET (R-B BST) of size 0
     */
    public PointSET() {
        bst = new SET<Point2D>();
        size = 0;
    }

    /**
     * @return boolean is the SET empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return number of nodes in the SET
     */
    public int size() {
        return size;
    }

    /**
     * add the point to the SET (if it is not already in the set) <br />
     *
     * @param p the Point2D to be inserted into the SET
     * @throws IllegalArgumentException if <tt>p</tt> is null
     * @see Point2D
     */
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Argument cannot be null");
        if (bst.contains(p)) return;
        bst.add(p);
        size++;
    }

    /**
     * Traverses the SET to determine if the point <tt>p</tt> is present
     *
     * @param p the Point2D to search for
     * @return is the point <tt>p</tt> in the SET, false if KdTree is empty
     * @throws IllegalArgumentException if <tt>p</tt> is null
     * @see Point2D
     */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Argument cannot be null");
        return bst.contains(p);
    }

    /**
     * draw all points to standard draw
     */
    public void draw() {
        for (Point2D p : bst) {
            p.draw();
        }
    }

    /**
     * all points that are inside the rectangle (or on the boundary)
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
        for (Point2D p : bst) {
            if (rect.contains(p)) result.push(p);
        }
        return result;
    }

    /**
     * a nearest neighbor in the set to point p; null if the set is empty
     *
     * @param p Point2D. Finds the nearest point to <tt>p</tt>
     * @return the Point2D that is nearest to <tt>p</tt>, null if set is empty
     * @throws IllegalArgumentException if <tt>p</tt> is null
     * @see Point2D
     */
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Argument cannot be null");
        Point2D champion = null;
        double champDist = 1;
        for (Point2D n : bst) {
            double d = p.distanceTo(n);
            if (d < champDist) {
                champDist = d;
                champion = n;
            }
        }
        return champion;
    }

    // unit testing of the methods
    public static void main(String[] args) {
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        StdOut.println("is PointSET empty " + brute.isEmpty() + ". Size: " + brute.size());
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.clear();
        StdDraw.setCanvasSize(1000, 1000);

        StdOut.println("is PointSET empty " + brute.isEmpty() + ". Size: " + brute.size());
        double xMin = StdRandom.uniformInt(0, 70);
        double yMin = StdRandom.uniformInt(0, 70);
        double xMax = (double) StdRandom.uniformInt((int) xMin + 20, 100) / 100;
        double yMax = (double) StdRandom.uniformInt((int) yMin + 20, 100) / 100;
        xMin /= 100;
        yMin /= 100;
        RectHV testRect = new RectHV(xMin, yMin, xMax, yMax);
        StdDraw.setPenColor(StdDraw.MAGENTA);
        testRect.draw();

        StdOut.print("Points in range " + testRect.toString() + ": ");
        for (Point2D p : brute.range(testRect)) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius(0.03);
            p.draw();
            StdOut.print(p.toString() + "/");
        }


        double randX = (double) StdRandom.uniformInt(0, 100) / 100;
        double randY = (double) StdRandom.uniformInt(0, 100) / 100;
        Point2D testNear = new Point2D(randX, randY);
        StdOut.println("\ncontains random point " + testNear + ": " + brute.contains(testNear));
        Point2D nearest = brute.nearest(testNear);
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setPenRadius(0.02);
        nearest.draw();
        StdDraw.setPenRadius(0.015);
        testNear.draw();
        StdOut.println("Point " + nearest.toString() + " was the nearest to " + testNear);
        StdOut.println("contains point " + nearest.toString() + ": " + brute.contains(nearest));


        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        brute.draw();
        StdDraw.show();
    }
}
