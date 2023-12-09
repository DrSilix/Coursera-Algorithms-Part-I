/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TestAllPuzzles {
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

    private static class PuzzleReference {
        private String name;
        private int min, bord, eq, man;

        public PuzzleReference(String fileName, int delMin, int board, int equals, int manhattan) {
            name = fileName;
            min = delMin;
            bord = board;
            eq = equals;
            man = manhattan;
        }

        public String getName() { return name; }
        public int getMin() { return min; }
        public int getBord() { return bord; }
        public int getEq() { return eq; }
        public int getMan() { return man; }
    }


    public static void main(String[] args) {
        In ni = new In("AutograderExpectedValues.txt");
        ArrayList<PuzzleReference> pR = new ArrayList<PuzzleReference>();
        while (!ni.isEmpty()) {
            String s = ni.readString();
            int d = Integer.parseInt(ni.readString());
            int b = Integer.parseInt(ni.readString());
            int e = Integer.parseInt(ni.readString());
            int m = Integer.parseInt(ni.readString());
            pR.add(new PuzzleReference(s, d, b, e, m));
        }

        DecimalFormat dF = new DecimalFormat("#.##");


        //String[] fileNames = getFileNames();
        //for (String name : fileNames) {
        for (PuzzleReference ref : pR) {
            String name = ref.getName();
            StdOut.print(name);
            // read the n points from a file
            In in = new In(name);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    tiles[i][j] = in.readInt();
            Board initial = new Board(tiles);

            String fileMovesString = name.substring(name.length() - 6, name.length() - 4);
            int expectedMoves;
            if (fileMovesString.startsWith("e") || fileMovesString.startsWith("l")) expectedMoves = -1;
            else expectedMoves = Integer.parseInt(fileMovesString);

            /* if (expectedMoves > 35) {
                StdOut.println(" - skipped");
                continue;
            }*/

            Solver solver = new Solver(initial);

            if (!solver.isSolvable())
                if (expectedMoves == solver.moves()) StdOut.println(" No solution possible");
                else {
                    StdOut.println(" ERROR! - on file " + name + " expected solvable in " + expectedMoves + " got unsolvable");
                    return;
                }
            else {
                if (expectedMoves == solver.moves()) {
                    StdOut.print(" size:" + n + " moves:" + solver.moves());
                    StdOut.print(" delMin:" + solver.delMinCalls() + "/" + dF.format((double) solver.delMinCalls()/ref.getMin()));
                    StdOut.print(" manhattan:" + solver.manhattanCalls() + "/" + dF.format((double) solver.manhattanCalls()/ref.getMan()));
                    StdOut.print(" board:" + solver.boardCalls() + "/" + dF.format((double) solver.boardCalls()/ref.getBord()));
                    StdOut.println(" equals:" + solver.boardEqualsCalls() + "/" + dF.format((double) solver.boardEqualsCalls()/ref.getEq()));
                }
                else {
                    StdOut.println(" ERROR! - on file " + name + " expected # of moves " + expectedMoves + ", took " + solver.moves() + " moves");
                    return;
                }
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
}

