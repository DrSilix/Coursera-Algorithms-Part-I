/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/2/2023
 *
 *  Compilation: javac-algs4 FastCollinearPoints.java
 *  Execution: java-algs4 FastCollinearPoints input8.txt
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

    // finds all line segments containing 4 or more points
    // loops through each point once (except last 3) and sorts all succeeding points by their slope
    // to that parent point. Finds sequences of 3+ similar slopes. Duplicate segments are removed after
    public FastCollinearPoints(Point[] points) {
        validatePoints(points);

        numberOfSegments = 0;
        segments = new ArrayList<LineSegment>();
        ArrayList<LineSegmentPoints> segmentsByPoints = new ArrayList<LineSegmentPoints>();

        for (int i = 0; i < points.length - 3; i++) {
            Point p = points[i];
            ArrayList<Point> collinearPoints = new ArrayList<Point>();
            double prevSlope = -999;
            boolean wasCollinear = false;

            Arrays.sort(points, i+1, points.length, p.slopeOrder()); // points succeeding parent sorted

            for (int j = i + 1; j < points.length; j++) {
                double slope = points[i].slopeTo(points[j]);

                // start of collinear segment. Records parent and prev point
                if (!wasCollinear && slope == prevSlope) {
                    wasCollinear = true;
                    collinearPoints = new ArrayList<Point>();
                    collinearPoints.add(points[i]); // add the parent point
                    collinearPoints.add(points[j-1]); // add the previous point
                }

                // middle of collinear segment. Records current point
                if (wasCollinear && slope == prevSlope) {
                    collinearPoints.add(points[j]);
                }

                // end of collinear segment. On end checks for segment 4+ points
                if (wasCollinear && (slope != prevSlope || j == points.length-1)) {
                    wasCollinear = false;
                    if (collinearPoints.size() >= 4) {
                        Point[] sortedTemp = collinearPoints.toArray(new Point[0]);
                        Arrays.sort(sortedTemp);    // points are sorted to get true first and last point
                        segmentsByPoints.add(new LineSegmentPoints(sortedTemp[0], sortedTemp[sortedTemp.length-1]));
                    }
                }
                prevSlope = slope;
            }
        }
        if (segmentsByPoints.size() > 0) removeDuplicateSegments(segmentsByPoints.toArray(new LineSegmentPoints[0]));
    }

    // Removes duplicate segments. When a segment is 5+ points long multiple segments are captured
    // This sorts by the last point in segments and removes segments with duplicate last points
    // except where the slopes are different.
    private void removeDuplicateSegments(LineSegmentPoints[] segmentsByPoints) {
        Arrays.sort(segmentsByPoints);
        segments.add(new LineSegment(segmentsByPoints[0].first, segmentsByPoints[0].last));
        numberOfSegments++;
        for (int i = 1; i < segmentsByPoints.length; i++) {
            // check for unequal last points, or equal last points and unequal slopes
            if (segmentsByPoints[i - 1].getLast() != segmentsByPoints[i].getLast() ||
                    segmentsByPoints[i - 1].getSlope() != segmentsByPoints[i].getSlope()) {
                segments.add(new LineSegment(segmentsByPoints[i].first, segmentsByPoints[i].last));
                numberOfSegments++;
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }

    // the line segments
    public LineSegment[] segments() { return segments.toArray(new LineSegment[0]); }

    // Loops through provided array of points and checks for null points or duplicates
    private void validatePoints(Point[] points) {
        // if (points == null || points.length < 4) { throw new IllegalArgumentException("at least 4 points must be provided"); }     // this check is not within specification
        Arrays.sort(points);
        for (int i = 1; i < points.length; i++) {
            if (points[i] == null) { throw new IllegalArgumentException("Point cannot be null"); }
            if (points[i-1].compareTo(points[i]) == 0) { throw new IllegalArgumentException("Duplicate points are not allowed"); }
        }
    }

    // Line segment object which stores the first and last point as accessible and contains a comparable
    // against the last point -> slopes.
    // Course provided LineSegment class provides no way to efficiently access a segments first and last point
    private class LineSegmentPoints implements Comparable<LineSegmentPoints> {
        private Point first, last;

        public LineSegmentPoints(Point a, Point b) {
            first = a;
            last = b;
        }

        public Point getLast() { return last; }
        public double getSlope() { return first.slopeTo(last); }

        // compares last points and if equal compares the slopes
        public int compareTo(LineSegmentPoints that) {
            int c = last.compareTo(that.last);
            if (c == 0) c = Double.compare(first.slopeTo(last), that.first.slopeTo(that.last));
            return c;
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

        // Stopwatch timer = new Stopwatch();
        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        // StdOut.println(timer.elapsedTime());

        StdDraw.setPenRadius(0.002);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
