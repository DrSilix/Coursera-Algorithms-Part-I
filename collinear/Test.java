/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        if (args.length == 0) {
            String[] fileNames = getFileNames();
            for (String name : fileNames) {
                StdOut.println(name);
                // read the n points from a file
                In in = new In(name);
                int n = in.readInt();
                Point[] points = new Point[n];
                for (int i = 0; i < n; i++) {
                    int x = in.readInt();
                    int y = in.readInt();
                    points[i] = new Point(x, y);
                }
                runTestOnPoints(points, false, true);
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StdDraw.clear();
            }
        } else {
            if (args[0].toLowerCase().equals("random")) {
                int trials = Integer.parseInt(args[2]);
                for (int i = 0; i < trials; i++) {
                    // int n = StdRandom.uniformInt(Integer.parseInt(args[1]));
                    int n = Integer.parseInt(args[1]);
                    Point[] points = new Point[n];
                    for (int j = 0; j < n; j++) {
                        int x = StdRandom.uniformInt(3276) * 10 + 4;
                        int y = StdRandom.uniformInt(3276) * 10 + 4;
                        points[j] = new Point(x, y);
                    }
                    runTestOnPoints(points, false, true);
                    try {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    StdDraw.clear();
                }
            }
            if (args[0].toLowerCase().equals("montecarlo")) {
                int trials = Integer.parseInt(args[1]);
                ArrayList<Double> resultsList = new ArrayList<Double>();

                double mean;
                double stddev;
                double confidenceLevel95;
                double confLo, confHi;

                for (int i = 0; i < trials; i++) {
                    ArrayList<Point> points = new ArrayList<Point>();
                    int x = StdRandom.uniformInt(3276);
                    int y = StdRandom.uniformInt(3276);
                    int winCon = 0;
                    points.add(new Point(x, y));
                    while (winCon == 0) {
                        x = StdRandom.uniformInt(3276);
                        y = StdRandom.uniformInt(3276);
                        points.add(new Point(x, y));
                        winCon = runTestOnPoints(points.toArray(new Point[0]), true, false);
                    }
                    if (winCon == 1) {
                        resultsList.add((double) points.size());
                        StdOut.println("3rd 4+ point collinear segment found at " + points.size()
                                               + " points");
                    } else {
                        StdOut.println("Trial Failure");
                    }
                }

                double[] results = new double[resultsList.size()];
                for (int i = 0; i < resultsList.size(); i++) {
                    results[i] = resultsList.get(i);
                }
                mean = StdStats.mean(results);
                stddev = StdStats.stddev(results);
                confidenceLevel95 = (1.96 * stddev) / Math.sqrt(trials);
                confLo = mean - confidenceLevel95;
                confHi = mean + confidenceLevel95;

                StdOut.println("sample size: " + results.length + " / mean: " + mean + " / stddev: " + stddev + " / 95% confidence interval = [" + confLo + ", " + confHi + "]");

            }
        }
    }

    private static int runTestOnPoints(Point[] points, boolean onlyRunFastTest, boolean drawResults) {

        if (drawResults) {
            // draw the points
            StdDraw.clear();
            StdDraw.enableDoubleBuffering();
            StdDraw.setXscale(0, 32768);
            StdDraw.setYscale(0, 32768);
            StdDraw.setPenRadius(0.01);
            StdDraw.setPenColor(Color.black);
            for (Point p : points) {
                p.draw();
            }
            StdDraw.show();
        }

        try {
            FastCollinearPoints fast = new FastCollinearPoints(points);

            if (drawResults) {
                StdDraw.setPenRadius(0.006);
                StdDraw.setPenColor(Color.blue);
                for (LineSegment segment : fast.segments()) {  // TODO where is this iterator???
                    StdOut.println(segment);
                    segment.draw();
                }
                StdDraw.show();
            }

            if (!onlyRunFastTest) {
                int numBrutePoints = -1;
                if (points.length < 5000) {
                    StdOut.println("-");
                    BruteCollinearPoints brute = new BruteCollinearPoints(points);

                    if (drawResults) {
                        StdDraw.setPenRadius(0.002);
                        StdDraw.setPenColor(Color.red);
                        for (LineSegment segment : brute.segments()) {  // TODO where is this iterator???
                            StdOut.println(segment);
                            segment.draw();
                        }
                        StdDraw.show();
                    }
                    numBrutePoints = brute.numberOfSegments();
                }
                StdOut.println(points.length + " points, " + fast.numberOfSegments() + "/" + numBrutePoints + " segments");
            }

            if (fast.numberOfSegments() > 2) return 1;
        }
        catch (IllegalArgumentException e) {
            if (e.getMessage().equals("at least 4 points must be provided")) {
                return 0;
            } else {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
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
