/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {
    private int numberOfSegments;
    private ArrayList<LineSegment> segments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        validatePoints(points);

        numberOfSegments = 0;
        segments = new ArrayList<LineSegment>();
        ArrayList<LineSegmentPoints> segmentsByPoints = new ArrayList<LineSegmentPoints>();

        for (int i = 0; i < points.length - 4; i++) {
            Point p = points[i];
            // StdOut.println(p.toString());
            Arrays.sort(points, i+1, points.length, p.slopeOrder());
            ArrayList<Point> collinearPoints = new ArrayList<Point>();
            double prevSlope = -999; // points[i].slopeTo(points[i + 1]);
            // StdOut.println(points[i+1].toString() + " - " + prevSlope);
            boolean wasCollinear = false;
            for (int j = i + 1; j < points.length; j++) {
                double slope = points[i].slopeTo(points[j]);
                // StdOut.println(points[j].toString() + " - " + slope);

                // start of collinear segment
                if (!wasCollinear && slope == prevSlope) {
                    wasCollinear = true;
                    collinearPoints = new ArrayList<Point>();
                    collinearPoints.add(points[i]); // add the parent point
                    collinearPoints.add(points[j-1]); // add the previous point
                }

                // middle of collinear segment
                if (wasCollinear && slope == prevSlope) {
                    collinearPoints.add(points[j]);
                }

                // end of collinear segment
                if (wasCollinear && (slope != prevSlope || j == points.length-1)) {
                    wasCollinear = false;
                    if (collinearPoints.size() >= 4) {
                        Point[] sortedTemp = collinearPoints.toArray(new Point[0]);
                        Arrays.sort(sortedTemp);
                        segmentsByPoints.add(new LineSegmentPoints(sortedTemp[0], sortedTemp[sortedTemp.length-1]));
                        // segments.add(sortAndBuildSegment(collinearPoints.toArray(new Point[0])));
                    }
                }
                prevSlope = slope;
            }
            // StdOut.println("----------------");
        }
        if (segmentsByPoints.size() > 0) removeDuplicateSegments(segmentsByPoints.toArray(new LineSegmentPoints[0]));
    }

    private void removeDuplicateSegments(LineSegmentPoints[] segmentsByPoints) {
        Arrays.sort(segmentsByPoints);
        segments.add(new LineSegment(segmentsByPoints[0].first, segmentsByPoints[0].last));
        for (int i = 1; i < segmentsByPoints.length; i++) {
            if (segmentsByPoints[i - 1].getLast() != segmentsByPoints[i].getLast()) {
                segments.add(new LineSegment(segmentsByPoints[i].first, segmentsByPoints[i].last));
            }
            else if (segmentsByPoints[i-1].getSlope() != segmentsByPoints[i].getSlope()) {
                segments.add(new LineSegment(segmentsByPoints[i].first, segmentsByPoints[i].last));
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }

    // the line segments
    public LineSegment[] segments() { return segments.toArray(new LineSegment[0]); }

    private void validatePoints(Point[] points) {
        if (points == null || points.length < 4) { throw new IllegalArgumentException("at least 4 points must be provided"); }
        Arrays.sort(points);
        for (int i = 1; i < points.length; i++) {
            if (points[i] == null) { throw new IllegalArgumentException("Point cannot be null"); }
            if (points[i-1].compareTo(points[i]) == 0) { throw new IllegalArgumentException("Duplicate points are not allowed"); }
        }
    }

    private class LineSegmentPoints implements Comparable<LineSegmentPoints> {
        private Point first, last;

        public LineSegmentPoints(Point a, Point b) {
            first = a;
            last = b;
        }

        public Point getFirst() { return first; }
        public Point getLast() { return last; }
        public double getSlope() { return first.slopeTo(last); }

        public int compareTo(LineSegmentPoints that) {
            return last.compareTo(that.last);
        }
    }


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

        Stopwatch timer = new Stopwatch();
        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.println(timer.elapsedTime());

        StdDraw.setPenRadius(0.002);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
