/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestAllPuzzles {
    public static void main(String[] args) {
        String[] fileNames = getFileNames();
        for (String name : fileNames) {
            StdOut.print(name);
            // read the n points from a file
            In in = new In(name);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    tiles[i][j] = in.readInt();
            Board initial = new Board(tiles);

            // solve the puzzle
            Solver solver = new Solver(initial);

            if (!solver.isSolvable())
                StdOut.println(" No solution possible");
            else {
                StdOut.println(" moves:" + solver.moves() + " size:" + n + " queueSize:" + solver.getQueueSize());
            }

            solver = null;

            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String[] getFileNames() {
        List<String> results = new ArrayList<String>();


        File[] files = new File("./").listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.endsWith("txt")) {
                    results.add(file.getName());
                }
            }
        }
        return results.toArray(new String[0]);
    }
}

