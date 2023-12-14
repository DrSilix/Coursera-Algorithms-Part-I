/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/11/2023
 *
 *  Compilation: javac-algs4 PointSET.java
 *  Execution: java-algs4 PointSET
 *  Dependencies: Point2D.java, RectHV.java
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;

public class PointSET {
    private SET<Point2D> bst;
    private int size;

    // construct an empty set of points
    public PointSET() {
        bst = new SET<Point2D>();
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        bst.add(p);
        size++;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        return bst.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : bst) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        Stack<Point2D> result = new Stack<Point2D>();
        for (Point2D p : bst) {
            if (rect.contains(p)) result.push(p);
        }
        return result;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
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

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.clear();
        StdDraw.setPenColor(Color.black);
        StdDraw.setPenRadius(0.01);
        brute.draw();
        StdDraw.show();

        Point2D testNear = new Point2D(0.81, 0.30);
        Point2D nearest = brute.nearest(testNear);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.setPenRadius(0.01);
        nearest.draw();
        testNear.draw();
        StdDraw.show();
        StdOut.println("Point " + nearest.toString() + " was the nearest");
    }
}
