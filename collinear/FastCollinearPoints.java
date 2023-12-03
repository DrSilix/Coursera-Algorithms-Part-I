/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/3/2023
 *
 *  Compilation: javac-algs4 FastCollinearPoints.java
 *  Execution: java-algs4 FastCollinearPoints input8.txt
 *  Dependencies: Point.java, LineSegment.java
 *
 *  Library that takes standard input of a collection of points or direct instantiation
 *  and method calls and uses a fast slope-sorted method to determine segments of 4+
 *  collinear points. Points must not contain duplicates or nulls.
 *
 *  Standard Input formatting:
 *  input4.txt
 *  4
 *  1000 1000
 *  2000 2000
 *  3000 3000
 *  4000 4000
 *
 *  % java-algs4 FastCollinearPoints input8.txt
 *  (10000, 0) -> (0, 10000)
 *  (3000, 4000) -> (20000, 21000)
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private int numberOfSegments;
    private ArrayList<LineSegment> segments;

    /**
     * Initializes a new evaluation of collinear points with fast algorithm.
     * finds all collinear line segments containing 4 or more points with no
     * overlapping segments.
     *
     * @param  points array of points on a grid
     * @throws IllegalArgumentException if <tt>points</tt> or an
     *         element of <tt>points</tt> is <tt>null</tt> or contains
     *         a duplicate point
     */
    // Stores copy of points array with points in natural order. Sorts original
    // by slope order to parent then finds sequence of 3+ similar slopes and
    // stores the segment if the parent is the least point
    public FastCollinearPoints(Point[] points) {
        if (points == null) { throw new IllegalArgumentException("points cannot be null"); }
        Point[] pointsInNaturalOrder = new Point[points.length];
        Point[] pointsBySlopeOrder = new Point[points.length];
        copyPoints(points, pointsBySlopeOrder, pointsInNaturalOrder);
        Arrays.sort(pointsInNaturalOrder);
        validatePoints(pointsInNaturalOrder);

        numberOfSegments = 0;
        segments = new ArrayList<LineSegment>();
        if (pointsBySlopeOrder.length < 4) return;

        for (int i = 0; i < pointsBySlopeOrder.length - 3; i++) {
            Point p = pointsInNaturalOrder[i];
            double prevSlope = Double.NEGATIVE_INFINITY; // set to unreachable value for first pass
            int startIndex = -1;
            boolean wasCollinear = false;

            Arrays.sort(pointsBySlopeOrder, p.slopeOrder()); // points succeeding parent sorted

            for (int j = 1; j < pointsBySlopeOrder.length; j++) {
                double slope = p.slopeTo(pointsBySlopeOrder[j]);

                // start of collinear segment. Records parent and prev point
                if (!wasCollinear && slope == prevSlope) {
                    wasCollinear = true;
                    startIndex = j-1;
                }

                // end of collinear segment. On end checks for segment 4+ points
                if (wasCollinear && (slope != prevSlope || j == pointsBySlopeOrder.length-1)) {
                    int endIndex = (j - 1);
                    if (j == pointsBySlopeOrder.length - 1 && slope == prevSlope) endIndex = j;
                    int length = ((endIndex + 1) - startIndex) + 1;
                    wasCollinear = false;
                    if (length >= 4) {
                        boolean parentIsLeast = true;
                        for (int m = startIndex; m <= endIndex; m++) {
                            if (p.compareTo(pointsBySlopeOrder[m]) > 0) parentIsLeast = false;
                        }
                        if (parentIsLeast) {
                            Arrays.sort(pointsBySlopeOrder, startIndex, endIndex + 1);
                            segments.add(new LineSegment(p, pointsBySlopeOrder[endIndex]));
                            numberOfSegments++;
                        }
                    }
                }
                prevSlope = slope;
            }
        }
    }

    /**
     * the number of line segments
     */
    public int numberOfSegments() { return numberOfSegments; }

    /**
     * the line segments
     */
    public LineSegment[] segments() { return segments.toArray(new LineSegment[0]); }

    // Loops through provided array of points and checks for null points or duplicates
    private void validatePoints(Point[] points) {
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException("Point cannot be null");
            if (i != 0 && points[i] == points[i-1]) throw new IllegalArgumentException("Duplicate points are not allowed");
        }
    }

    // Creates 2 copies of points. One for sorting by natural order, one for by slope
    private void copyPoints(Point[] points, Point[] copy1, Point[] copy2) {
        for (int i = 0; i < points.length; i++) {
            copy1[i] = points[i];
            copy2[i] = points[i];
        }
    }

    // Unit test for FastCollinearPoints. Takes in standard input collection of points, draws them
    // to the screen, then finds and draws each collinear segment of 4+
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.setPenRadius(0.006);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);

        StdDraw.setPenRadius(0.002);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
