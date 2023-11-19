/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;

public class PercolationStats {
    private ArrayList<Double> trialResult = new ArrayList<Double>();
    private double mean;
    private double stddev;
    private double confLo, confHi;
    private double confidenceLevel95;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        for (int i = 0; i < trials; i++) {
            Percolation trial = new Percolation(n);
            while (!trial.percolates()) {
                trial.open(StdRandom.uniformInt(n) + 1, StdRandom.uniformInt(n) + 1);
            }
            trialResult.add((double) trial.numberOfOpenSites() / (n * n));
            // StdOut.println("Trial " + (i + 1) + " completed.");
        }

        mean = mean();
        stddev = stddev();
        confidenceLevel95 = (1.96 * stddev) / Math.sqrt(trials);
        confLo = confidenceLo();
        confHi = confidenceHi();

        StdOut.println("mean                    = " + mean);
        StdOut.println("stddev                  = " + stddev);
        StdOut.println(
                "95% confidence interval = [" + confLo + ", " + confHi + "]");
    }

    // sample mean of percolation threshold
    public double mean() {
        double[] temp = trialResult.stream().mapToDouble(Double::doubleValue).toArray();
        return StdStats.mean(temp);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        double[] temp = trialResult.stream().mapToDouble(Double::doubleValue).toArray();
        return StdStats.stddev(temp);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - confidenceLevel95;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + confidenceLevel95;
    }

    // test client (see below)
    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]),
                                                   Integer.parseInt(args[1]));
        StdOut.println(timer.elapsedTime());
    }
}
