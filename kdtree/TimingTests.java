/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class TimingTests {
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }

        Point2D[] randPoints = new Point2D[Integer.parseInt(args[1])];
        for (int i = 0; i < randPoints.length; i++) {
            randPoints[i] = new Point2D(StdRandom.uniformDouble(), StdRandom.uniformDouble());
        }

        Stopwatch timer = new Stopwatch();
        double testStartTime, testEndTime;
        testStartTime = timer.elapsedTime();
        for (int i = 0; i < randPoints.length; i++) {
            brute.nearest(randPoints[i]);
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println(randPoints.length + " Tests finished in " + testEndTime + " seconds at " + (testEndTime/ randPoints.length) + " seconds per nearest() call.");
    }
}
