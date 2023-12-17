/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/17/2023
 *
 *  Compilation: javac-algs4 PercolationStats.java
 *  Execution: java-algs4 PercolationStats 100 200
 *
 *  Library that can be called to handle a Monte Carlo simulation using the library
 *  Percolation. A number of trials is performed on a grid of n size and then the
 *  results are computed.
 *
 *  This can also be run itself with provided arguments
 *
 *  % java-algs4 PercolationStats 100 200
 *  mean                    = 0.5924435
 *  stddev                  = 0.014926094574476984
 *  95% confidence interval = [0.5903748488327121, 0.594512151167288]
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double mean;
    private double stddev;
    private double confidenceLevel95;
    private double confLo, confHi;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        validate(n, trials);

        double[] trialResult = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation trial = new Percolation(n);
            while (!trial.percolates()) {
                trial.open(StdRandom.uniformInt(n) + 1, StdRandom.uniformInt(n) + 1);
            }
            trialResult[i] = (double) trial.numberOfOpenSites() / (n * n);
            // StdOut.println("Trial " + (i + 1) + " completed.");
        }

        mean = StdStats.mean(trialResult);
        stddev = StdStats.stddev(trialResult);
        confidenceLevel95 = (1.96 * stddev) / Math.sqrt(trials);
        confLo = mean - confidenceLevel95;
        confHi = mean + confidenceLevel95;
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confHi;
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
        // Stopwatch timer = new Stopwatch();
        int row = Integer.parseInt(args[0]);
        int col = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(row, col);
        StdOut.println("mean                    = " + ps.mean);
        StdOut.println("stddev                  = " + ps.stddev);
        StdOut.println(
                "95% confidence interval = [" + ps.confLo + ", " + ps.confHi + "]");
        // StdOut.println(timer.elapsedTime());
    }
}
