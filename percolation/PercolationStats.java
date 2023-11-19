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
    private double confidenceLevel95;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        validate(n, trials);

        for (int i = 0; i < trials; i++) {
            Percolation trial = new Percolation(n);
            while (!trial.percolates()) {
                trial.open(StdRandom.uniformInt(n) + 1, StdRandom.uniformInt(n) + 1);
            }
            trialResult.add((double) trial.numberOfOpenSites() / (n * n));
            // StdOut.println("Trial " + (i + 1) + " completed.");
        }

        mean = mean();

        double stddev = stddev();
        confidenceLevel95 = (1.96 * stddev) / Math.sqrt(trials);

        double confLo = confidenceLo();
        double confHi = confidenceHi();

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

    private void validate(int n, int trials) {
        if (n <= 0) {
            throw new IllegalArgumentException("Grid size " + n + " is not greater than 0");
        }
        if (trials <= 0) {
            throw new IllegalArgumentException(
                    "Number of trials " + trials + " is not greater than 0");
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        int row = Integer.parseInt(args[0]);
        int col = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(row, col);
        StdOut.println(timer.elapsedTime());
    }
}
