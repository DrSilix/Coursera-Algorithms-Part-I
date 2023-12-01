/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        String[] fileNames = getFileNames();
        for (String name : fileNames) {
            StdOut.println(name);
            try {
                RunTestOnFileName(name);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StdDraw.clear();
        }
    }

    private static void RunTestOnFileName(String fileName){
        // read the n points from a file
        In in = new In(fileName);
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
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(Color.black);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        FastCollinearPoints fast = new FastCollinearPoints(points);

        StdDraw.setPenRadius(0.006);
        StdDraw.setPenColor(Color.blue);
        for (LineSegment segment : fast.segments()) {  // TODO where is this iterator???
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
        StdOut.println(fast.numberOfSegments());

        if (points.length < 5000) {
            BruteCollinearPoints brute = new BruteCollinearPoints(points);

            StdDraw.setPenRadius(0.002);
            StdDraw.setPenColor(Color.red);
            for (LineSegment segment : brute.segments()) {  // TODO where is this iterator???
                StdOut.println(segment);
                segment.draw();
            }
            StdDraw.show();
            StdOut.println(brute.numberOfSegments());
        }
    }

    private static String[] getFileNames() {
        List<String> results = new ArrayList<String>();


        File[] files = new File("C:\\Users\\Alex\\IdeaProjects\\Algorithms Part I\\collinear").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.substring(fileName.length()-3,fileName.length()).equals("txt")) {
                    results.add(file.getName());
                }
            }
        }
        return results.toArray(new String[0]);
    }
}
