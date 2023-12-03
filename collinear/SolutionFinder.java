/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolutionFinder {
    private int numberOfSegments = 0;
    private ArrayList<LineSegmentPoints> segments;

    // finds all line segments containing exactly 4 points
    // loops through each unique combination of 4 points and determines if they are
    // collinear
    public SolutionFinder(Point[] points)  {
        validatePoints(points);

        numberOfSegments = 0;
        segments = new ArrayList<LineSegmentPoints>();
        ArrayList<LineSegmentPoints> segmentsByPoints = new ArrayList<LineSegmentPoints>();

        Arrays.sort(points);

        for (int i = 0; i < points.length - 3; i++) {
            Point p = points[i];
            ArrayList<Point> collinearPoints = new ArrayList<Point>();
            double prevSlope = -999;
            boolean wasCollinear = false;

            Arrays.sort(points, i+1, points.length, p.slopeOrder()); // points succeeding parent sorted

            for (int j = i + 1; j < points.length; j++) {
                double slope = points[i].slopeTo(points[j]);
                if (slope == Double.NEGATIVE_INFINITY) throw new IllegalArgumentException("Duplicate points are not allowed");

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
                        segmentsByPoints.add(new LineSegmentPoints(sortedTemp));
                    }
                }
                prevSlope = slope;
            }
        }
        if (segmentsByPoints.size() > 0) removeDuplicateSegments(segmentsByPoints.toArray(new LineSegmentPoints[0]));
        StdOut.println("done");
    }

    // the number of line segments
    public int numberOfSegments() { return numberOfSegments; }


    // the line segments
    public LineSegmentPoints[] segments() {
        return segments.toArray(new LineSegmentPoints[0]);
    }

    // validate points are not null
    private void validatePoints(Point[] points) {
        if (points == null) { throw new IllegalArgumentException("points cannot be null"); }
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) { throw new IllegalArgumentException("Point cannot be null"); }
        }
    }

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
        segments.add(segmentsByPoints[0]);
        numberOfSegments++;
        for (int i = 1; i < segmentsByPoints.length; i++) {
            // if the slopes and (first or last points)
            if (segmentsByPoints[i - 1].getSlope() == segmentsByPoints[i].getSlope() &&
                    (segmentsByPoints[i - 1].getFirst() == segmentsByPoints[i].getFirst() ||
                            segmentsByPoints[i - 1].getLast() == segmentsByPoints[i].getLast())) {
                continue;
            }
            segments.add(segmentsByPoints[i]);
            numberOfSegments++;
        }

        segmentsByPoints = segments.toArray(segments.toArray(new LineSegmentPoints[0]));

        for (int i = 0; i < segmentsByPoints.length; i++) {
            Point parentFirst = segmentsByPoints[i].getFirst();
            Point parentLast = segmentsByPoints[i].getLast();
            double parentSlope = segmentsByPoints[i].getSlope();

            for (int j = i+1; j < segmentsByPoints.length; j++) {
                Point targetFirst = segmentsByPoints[j].getFirst();
                Point targetLast = segmentsByPoints[j].getLast();
                double targetSlope = segmentsByPoints[j].getSlope();

                if (parentSlope == targetSlope) {
                    double compareSlope;
                    boolean shit = false;
                    if (targetFirst.slopeTo(parentLast) == parentSlope && parentFirst.slopeTo(targetLast) == parentSlope) shit = true;

                    if (shit) {
                        StdOut.println("You dun fuked up! " + segmentsByPoints[i].toString() + ", "
                                               + segmentsByPoints[j].toString());
                    }
                }
            }
        }
    }

    private class LineSegmentPoints implements Comparable<LineSegmentPoints> {
        private Point[] points;
        private int length;

        public LineSegmentPoints(Point[] a) {
            points = a;
            length = points.length;
        }

        public Point getFirst() { return points[0]; }
        public Point getLast() { return points[points.length-1]; }
        public Point[] getPoints() { return points; }
        public double getSlope() { return getFirst().slopeTo(getLast()); }
        public double getLength() { return length; }

        public void draw() {
            getFirst().drawTo(getLast());
        }

        public String toString() {
            return getFirst() + " -> " + getLast();
        }

        // compares last points and if equal compares the slopes
        public int compareTo(LineSegmentPoints that) {
            int c = Double.compare(getFirst().slopeTo(getLast()), that.getFirst().slopeTo(that.getLast()));
            if (c == 0) {
                c = getFirst().compareTo(that.getFirst());
                if (c != 0) c = getLast().compareTo(that.getLast());
            }
            if (c == 0) c = Integer.compare(that.length, length); // Descending order
            return c;
        }
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
        SolutionFinder collinear = new SolutionFinder(points);
        for (LineSegmentPoints segment : collinear.segments()) {
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
