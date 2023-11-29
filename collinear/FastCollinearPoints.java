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
        if (points == null || points.length < 4) { throw new IllegalArgumentException("at least 4 points must be provided"); }

        numberOfSegments = 0;
        segments = new ArrayList<LineSegment>();

        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            StdOut.println(p.toString());
            Arrays.sort(points, p.slopeOrder());
            // TODO loop through the array finding 3 or more slopes that match
            for (int j = 0; j < points.length; j++) {
                StdOut.println(points[j].toString() + " - " + points[0].slopeTo(points[j]));
            }
            StdOut.println("----------------");
        }
    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }

    // the line segments
    public LineSegment[] segments() { return segments.toArray(new LineSegment[0]); }


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
