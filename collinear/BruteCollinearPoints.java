/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private static final int INIT_CAPACITY = 8;

    private int numberOfSegments = 0;
    private LineSegment[] segments;

    // TODO verify I'm only iterating through all combinations of points
    // TODO optimize on the basis that you don't need to check the remaining slopes if the first 2-3 aren't collinear
    // finds all line segments containing 4 points, constructor
    public BruteCollinearPoints(Point[] points)  {
        if (points == null || points.length < 4) { throw new IllegalArgumentException("at least 4 points must be provided"); }

        segments = new LineSegment[INIT_CAPACITY];

        for (int i = 0; i < points.length - 3; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                for (int k = j + 1; k < points.length - 1; k++) {
                    for (int m = k + 1; m < points.length; m++) {
                        double ijSlope = points[i].slopeTo(points[j]);
                        double ikSlope = points[i].slopeTo(points[k]);
                        double imSlope = points[i].slopeTo(points[m]);
                        if (ijSlope == Double.NEGATIVE_INFINITY || ikSlope == Double.NEGATIVE_INFINITY || imSlope == Double.NEGATIVE_INFINITY) {
                            throw new IllegalArgumentException("Duplicate points are not allowed");
                        }
                        if (ijSlope != ikSlope) { break; }
                        if (ijSlope != imSlope) { break; }
                        segments[numberOfSegments] = new LineSegment(points[i], points[m]);
                        numberOfSegments++;
                        StdOut.println(points[i].toString() + " > " + points[j].toString() + " > " + points[k].toString() + " > " + points[m].toString());
                        if (numberOfSegments == segments.length) { resizeArray(numberOfSegments * 2); }
                    }
                }
            }
        }
        if (segments.length != numberOfSegments) { resizeArray(numberOfSegments); }

    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }


    // the line segments
    public LineSegment[] segments() {
        return segments;
    }

    private void resizeArray(int size) {
        LineSegment[] temp = segments;
        segments = new LineSegment[size];
        for (int i = 0; i < numberOfSegments; i++) {
            segments[i] = temp[i];
        }
    }

    private void validatePoint(Point p) {
        if (p == null) { throw new IllegalArgumentException("Point cannot be null"); }
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

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        StdDraw.setPenRadius(0.002);
        for (LineSegment segment : collinear.segments()) {  // TODO where is this iterator???
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
