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

public class BruteCollinearPoints {

    private int numberOfSegments = 0;
    private ArrayList<LineSegment> segments;

    // finds all line segments containing 4 points, constructor
    public BruteCollinearPoints(Point[] points)  {
        if (points == null || points.length < 4) { throw new IllegalArgumentException("at least 4 points must be provided"); }
        segments = new ArrayList<LineSegment>();

        // loop through every unique combination of 4 points
        for (int i = 0; i < points.length - 3; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                double ijSlope = points[i].slopeTo(points[j]);

                for (int k = j + 1; k < points.length - 1; k++) {
                    double ikSlope = points[i].slopeTo(points[k]);
                    if (ijSlope != ikSlope) { continue; } // optimization to bail early if 3 points are not collinear

                    for (int m = k + 1; m < points.length; m++) {
                        double imSlope = points[i].slopeTo(points[m]);
                        if (ijSlope == Double.NEGATIVE_INFINITY || ikSlope == Double.NEGATIVE_INFINITY || imSlope == Double.NEGATIVE_INFINITY) {
                            throw new IllegalArgumentException("Duplicate points are not allowed"); // the slope of two points is only -Infinity if they are the same
                        }
                        if (ijSlope != imSlope) { continue; }

                        // Add collinear segment of 4 points, order the points
                        Point[] temp = new Point[]{points[i], points[j], points[k], points[m]};
                        Arrays.sort(temp);
                        segments.add(new LineSegment(temp[0], temp[temp.length-1]));
                        numberOfSegments++;
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }


    // the line segments
    public LineSegment[] segments() {
        return segments.toArray(new LineSegment[0]);
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
