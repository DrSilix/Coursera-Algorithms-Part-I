/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private int numberOfSegments = 0;
    // TODO I have to initialize something to hold the line segments

    // finds all line segments containing 4 points, constructor
    public BruteCollinearPoints(Point[] points)  {
        // meat and potatoes
        if (points.length < 4) { return; }

        for (int i = 0; i < points.length - 1; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                for (int k = j + 1; k < points.length - 3; k++) {
                    for (int m = k + 1; m < points.length - 4; m++) {
                        double ijSlope = points[i].slopeTo(points[j]);
                        if (ijSlope != points[i].slopeTo(points[k])) { break; }
                        if (ijSlope != points[i].slopeTo(points[m])) { break; }
                        numberOfSegments++;
                        // TODO store the line segment from point i to m
                    }
                }
            }
        }

    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }


    // the line segments
    public LineSegment[] segments() {
        // object array of found 4 point line segments, this is the result to be printed
        // TODO wtf is this???
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {  // TODO where is this iterator???
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
