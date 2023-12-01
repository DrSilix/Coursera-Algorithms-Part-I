/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

public class Test {
    public static void main(String[] args) {
        //Point a = new Point(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        //Point b = new Point(Integer.parseInt(args[2]), Integer.parseInt(args[3]));

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        String[] myPoints = new String[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
            String sX = Integer.toString(x);
            String sY = Integer.toString(y);
            myPoints[i] = "(" + sX + ", " + sY + ")";
        }

        /* for (int i = 0; i < points.length - 1; i++) {
            for (int j = i + 1; j < points.length; j++) {
                StdOut.println(myPoints[i] + " " + myPoints[j] + ": " + points[i].slopeTo(points[j]) + " - " + points[i].compareTo(points[j]));
            }
        }*/
    }
}
