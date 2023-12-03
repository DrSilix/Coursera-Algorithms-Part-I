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
 *
 *
 *  two potential solutions
 *  1. use copy array and do not optimize the for loops. keep copy array in natural order
 *      and sort all points in original array by slope. If the first point in the segment
 *      is not the first in natural order then ignore the segment
 *
 *  2. somehow store the segments a point belongs to as you go
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastCollinearPoints {
    private int numberOfSegments;
    private ArrayList<LineSegment> segments;

    // finds all line segments containing 4 or more points
    // loops through each point once (except last 3) and sorts all succeeding points by their slope
    // to that parent point. Finds sequences of 3+ similar slopes. Duplicate segments are removed after
    public FastCollinearPoints(Point[] points) {
        Point[] pointsInNaturalOrder = new Point[points.length];

        validateAndCopyPoints(points, pointsInNaturalOrder);

        numberOfSegments = 0;
        segments = new ArrayList<LineSegment>();
        ArrayList<LineSegmentPoints> segmentsByPoints = new ArrayList<LineSegmentPoints>();

        Arrays.sort(pointsInNaturalOrder);


        int startIndex = -1;
        for (int i = 0; i < points.length - 3; i++) {
            Point p = pointsInNaturalOrder[i];
            ArrayList<Point> collinearPoints = new ArrayList<Point>();
            double prevSlope = -999;
            boolean wasCollinear = false;

            Arrays.sort(points, p.slopeOrder()); // points succeeding parent sorted

            for (int j = 1; j < points.length; j++) {
                double slope = p.slopeTo(points[j]);
                if (slope == Double.NEGATIVE_INFINITY) throw new IllegalArgumentException("Duplicate points are not allowed");

                // start of collinear segment. Records parent and prev point
                if (!wasCollinear && slope == prevSlope) {
                    wasCollinear = true;
                    startIndex = j-1;
                    // collinearPoints = new ArrayList<Point>();
                    // collinearPoints.add(p); // add the parent point
                    // collinearPoints.add(points[j-1]); // add the previous point
                }

                // middle of collinear segment. Records current point
                /* if (wasCollinear && slope == prevSlope) {
                    collinearPoints.add(points[j]);
                }*/

                // end of collinear segment. On end checks for segment 4+ points
                if (wasCollinear && (slope != prevSlope || j == points.length-1)) {
                    int endIndex = (j - 1);
                    if (j == points.length-1) endIndex = j;
                    int length = ((endIndex + 1) - startIndex) + 1;
                    wasCollinear = false;
                    if (length >= 4) {
                        boolean inOrder = true;
                        for (int m = startIndex; m <= endIndex; m++) {
                            if (p.compareTo(points[m]) > 0) inOrder = false;
                        }
                        if (inOrder) {
                            segments.add(new LineSegment(p, points[endIndex]));
                            numberOfSegments++;
                        }
                    }
                }
                        // Point[] sortedTemp = new Point[length];
                       /* sortedTemp[0] = p;
                        for (int m = 1, k = startIndex; m < sortedTemp.length; m++, k++) {
                            sortedTemp[m] = points[k];
                        }
                        // Point firstBeforeSort = sortedTemp[0];
                        Arrays.sort(sortedTemp);    // points are sorted to get true first and last point
                        Point firstAfterSort = sortedTemp[0];
                        if (p == firstAfterSort) {
                            segments.add(new LineSegment(sortedTemp[0], sortedTemp[sortedTemp.length - 1]));
                            numberOfSegments++;
                        }
                    }*/
                    /* if (collinearPoints.size() >= 4) {
                        Point[] sortedTemp = collinearPoints.toArray(new Point[0]);
                        Point firstBeforeSort = sortedTemp[0];
                        Arrays.sort(sortedTemp);    // points are sorted to get true first and last point
                        Point firstAfterSort = sortedTemp[0];
                        if (firstBeforeSort == firstAfterSort) {
                            segments.add(new LineSegment(sortedTemp[0], sortedTemp[sortedTemp.length - 1]));
                            numberOfSegments++;
                        }
                        // segmentsByPoints.add(new LineSegmentPoints(sortedTemp[0], sortedTemp[sortedTemp.length-1], sortedTemp.length));
                    }*/
                prevSlope = slope;
            }
        }
        // if (segmentsByPoints.size() > 0) removeDuplicateSegments(segmentsByPoints.toArray(new LineSegmentPoints[0]));
    }

    // Removes duplicate segments. When a segment is 5+ points long multiple segments are captured
    // This sorts by the last point in segments and removes segments with duplicate last points
    // except where the slopes are different.
    private void removeDuplicateSegments(LineSegmentPoints[] segmentsByPoints) {
        for (LineSegmentPoints seg : segmentsByPoints) {
            StdOut.println(seg.getFirst().toString() + ", " + seg.getLast().toString() + ", " + seg.getLength());
        }
        Arrays.sort(segmentsByPoints);
        StdOut.println("---");
        for (LineSegmentPoints seg : segmentsByPoints) {
            StdOut.println(seg.getFirst().toString() + ", " + seg.getLast().toString() + ", " + seg.getLength());
        }
        StdOut.println("---");
        segments.add(new LineSegment(segmentsByPoints[0].first, segmentsByPoints[0].last));
        numberOfSegments++;
        for (int i = 1; i < segmentsByPoints.length; i++) {
            // if the slopes and (first or last points)
            if (segmentsByPoints[i - 1].getSlope() == segmentsByPoints[i].getSlope() &&
                    (segmentsByPoints[i - 1].getFirst() == segmentsByPoints[i].getFirst() ||
                    segmentsByPoints[i - 1].getLast() == segmentsByPoints[i].getLast())) {
                continue;
            }
            segments.add(new LineSegment(segmentsByPoints[i].first, segmentsByPoints[i].last));
            numberOfSegments++;
        }
    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }

    // the line segments
    public LineSegment[] segments() { return segments.toArray(new LineSegment[0]); }

    // Loops through provided array of points and checks for null points or duplicates
    private void validateAndCopyPoints(Point[] points, Point[] copy) {
        if (points == null) { throw new IllegalArgumentException("points cannot be null"); }
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) { throw new IllegalArgumentException("Point cannot be null"); }
            copy[i] = points[i];
        }
        /* Arrays.sort(points);
        for (int i = 1; i < points.length; i++) {
            if (points[i-1].compareTo(points[i]) == 0) { throw new IllegalArgumentException("Duplicate points are not allowed"); }
        }*/
    }

    // Line segment object which stores the first and last point as accessible and contains a comparable
    // against the last point -> slopes.
    // Course provided LineSegment class provides no way to efficiently access a segments first and last point
    private class LineSegmentPoints implements Comparable<LineSegmentPoints> {
        private Point first, last;
        private int length;

        public LineSegmentPoints(Point a, Point b, int l) {
            first = a;
            last = b;
            length = l;
        }

        public int getLength() { return length; }
        public Point getFirst() { return first; }
        public Point getLast() { return last; }
        public double getSlope() { return first.slopeTo(last); }

        // compares last points and if equal compares the slopes
        public int compareTo(LineSegmentPoints that) {
            int c = Double.compare(first.slopeTo(last), that.first.slopeTo(that.last));
            if (c == 0) {
                c = first.compareTo(that.first);
                if (c != 0) c = last.compareTo(that.last);
            }
            if (c == 0) c = Integer.compare(that.length, length); // Descending order
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

        for (LineSegment segment : collinear.segments()) {
            StdDraw.setPenRadius(0.002);
            StdDraw.setPenColor(Color.black);
            StdOut.println(segment);
            segment.draw();
            Pattern pattern = Pattern.compile("\\((\\d+?),\\s(\\d+?)\\).*?\\((\\d+?),\\s(\\d+?)\\)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(segment.toString());
            if (matcher.matches()) {
                Point start = new Point(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
                Point end = new Point(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
                StdDraw.setPenColor(Color.red);
                StdDraw.setPenRadius(0.006);
                start.draw();
                end.draw();
            }
        }
        StdDraw.show();

        StdOut.println(collinear.numberOfSegments());
    }
}
