/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestMonteCarloParallelCompute {
    private static class Result {
        private final double val;
        public Result(double code) {
            this.val = code;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        int trials = Integer.parseInt(args[0]);
        // ArrayList<Double> resultsList = new ArrayList<Double>();

        double mean;
        double stddev;
        double confidenceLevel95;
        double confLo, confHi;

        List<Object> objects = new ArrayList<Object>();
        for (int i = 0; i < trials; i++) {
            objects.add(new Object());
        }

        List<Callable<Result>> tasks = new ArrayList<Callable<Result>>();
        for (final Object object : objects) {
            Callable<Result> c = new Callable<Result>() {
                @Override
                public Result call() throws Exception {
                    return compute(object);
                }
            };
            tasks.add(c);
        }

        // ExecutorService exec = Executors.newCachedThreadPool();
        // some other exectuors you could try to see the different behaviours
        ExecutorService exec = Executors.newFixedThreadPool(12);
        // ExecutorService exec = Executors.newSingleThreadExecutor();
        ArrayList<Double> resultsList = new ArrayList<Double>();
        try {
            List<Future<Result>> execResults = exec.invokeAll(tasks);
            for (Future<Result> fr : execResults) {
                if (fr.get().val > 1) resultsList.add(fr.get().val);
            }
        }
        finally {
            exec.shutdown();
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

    public static Result compute(Object obj) throws InterruptedException {
        ArrayList<Point> points = new ArrayList<Point>();
        int x = StdRandom.uniformInt(1000);
        int y = StdRandom.uniformInt(1000);
        int winCon = 0;
        points.add(new Point(x, y));
        while (winCon == 0) {
            x = StdRandom.uniformInt(1000);
            y = StdRandom.uniformInt(1000);
            points.add(new Point(x, y));
            winCon = runTestOnPoints(points.toArray(new Point[0]));
        }
        if (winCon == 1) {
            StdOut.println("10th 4+ point collinear segment found at " + points.size()
                                   + " points");
            return new Result((double) points.size());
        } else {
            StdOut.println("Trial Failure");
            return new Result(-1.0);
        }
    }


    private static int runTestOnPoints(Point[] points) {

        try {
            FastCollinearPoints fast = new FastCollinearPoints(points);
            if (fast.numberOfSegments() > 9) return 1;
        }
        catch (IllegalArgumentException e) {
            if (e.getMessage().equals("at least 4 points must be provided")) {
                return 0;
            } else {
                // e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }
}
