/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
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
    public FastCollinearPoints(Point[] points) {
        validatePoints(points);

        numberOfSegments = 0;
        segments = new ArrayList<LineSegment>();

        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            StdOut.println(p.toString());
            Arrays.sort(points, i, points.length - 1, p.slopeOrder()); // TODO no need to recheck already checked p points, sort a subset of array
            ArrayList<Point> collinearPoints;
            double prevSlope = points[i].slopeTo(points[i + 1]);
            boolean wasCollinear = false;
            for (int j = i; j < points.length; j++) {
                double slope = points[i].slopeTo(points[j]);
                StdOut.println(points[j].toString() + " - " + slope);

                // start of collinear segment
                if (!wasCollinear && slope == prevSlope) {
                    wasCollinear = true;
                    collinearPoints = new ArrayList<Point>();
                    collinearPoints.add(points[i]); // add the parent point
                    collinearPoints.add(points[j-1]); // add the previous point
                    collinearPoints.add(points[j]); // add this point
                }

                // middle of collinear segment
                if (wasCollinear && slope == prevSlope) collinearPoints.add(points[j]);

                // end of collinear segment
                if (wasCollinear && slope != prevSlope) {
                    wasCollinear = false;
                    if (collinearPoints.size() >= 4) {
                        segments.add(sortAndBuildSegment(collinearPoints.toArray(new Point[0])));
                    }
                }
                prevSlope = slope;
            }
            StdOut.println("----------------");
        }
        removeDuplicateSegments();
    }

    private LineSegment sortAndBuildSegment(Point[] points) {
        Arrays.sort(points);
        return new LineSegment(points[0], points[points.length-1]);
    }

    private void removeDuplicateSegments() {
        Arrays.sort(segments);
    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }

    // the line segments
    public LineSegment[] segments() { return segments.toArray(new LineSegment[0]); }

    private void validatePoints(Point[] points) {
        if (points == null || points.length < 4) { throw new IllegalArgumentException("at least 4 points must be provided"); }
        Arrays.sort(points);
        for (int i = 1; i < points.length; i++) {
            if (points[i-1].compareTo(points[i]) == 0) { throw new IllegalArgumentException("Duplicate points are not allowed"); }
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
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);

        for (LineSegment segment : collinear.segments()) {  // TODO where is this iterator???
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
