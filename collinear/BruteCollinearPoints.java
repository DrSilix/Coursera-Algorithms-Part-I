/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/2/2023
 *
 *  Compilation: javac-algs4 BruteCollinearPoints.java
 *  Execution: java-algs4 BruteCollinearPoints input8.txt
 *
 *  Library that takes standard input of a collection of points or direct instantiation
 *  and method calls and uses a brute force method to determine segments of exactly 4
 *  collinear points. Points must not contain duplicates or nulls.
 *
 *  For collinear segments of 5+ points each overlapping segment of 4 will be recorded
 *  and drawn.
 *
 *  Standard Input formatting:
 *  input4.txt
 *  4
 *  1000 1000
 *  2000 2000
 *  3000 3000
 *  4000 4000
 *
 *  % java-algs4 BruteCollinearPoints input8.txt
 *  (10000, 0) -> (0, 10000)
 *  (3000, 4000) -> (20000, 21000)
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private int numberOfSegments = 0;
    private ArrayList<LineSegment> segments;

    // finds all line segments containing exactly 4 points
    // loops through each unique combination of 4 points and determines if they are
    // collinear
    public BruteCollinearPoints(Point[] points)  {
        if (points == null) { throw new IllegalArgumentException("points cannot be null"); }
        segments = new ArrayList<LineSegment>();

        validatePoint(points[0]); // validate first 3 points
        validatePoint(points[1]); // 4th for loop will do the rest
        validatePoint(points[2]);

        // loop through every unique combination of 4 points
        for (int i = 0; i < points.length - 3; i++) {
            for (int j = i + 1; j < points.length - 2; j++) {
                double ijSlope = points[i].slopeTo(points[j]);
                if (ijSlope == Double.NEGATIVE_INFINITY) throw new IllegalArgumentException("Duplicate points are not allowed");

                for (int k = j + 1; k < points.length - 1; k++) {
                    if (i == 0 && j == 1) validatePoint(points[k]); // validate points only on first pass
                    double ikSlope = points[i].slopeTo(points[k]);
                    if (ikSlope == Double.NEGATIVE_INFINITY) throw new IllegalArgumentException("Duplicate points are not allowed");
                    if (ijSlope != ikSlope) { continue; } // optimization to bail early if 3 points are not collinear

                    for (int m = k + 1; m < points.length; m++) {
                        if (i == 0) validatePoint(points[m]); // validate points only on first pass
                        double imSlope = points[i].slopeTo(points[m]);
                        if (imSlope == Double.NEGATIVE_INFINITY) throw new IllegalArgumentException("Duplicate points are not allowed");
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

    // validate points are not null
    private void validatePoint(Point p) {
        if (p == null) { throw new IllegalArgumentException("Point cannot be null"); }
    }

    // Unit test for BruteCollinearPoints. Takes in standard input collection of points, draws them
    // to the screen, then finds and draws each collinear segment of exactly 4 points
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
