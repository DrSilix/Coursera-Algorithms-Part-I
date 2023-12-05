/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Board {
    private final int[][] board;
    private final int[][] isTheBoardImmutable;
    private final int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        isTheBoardImmutable = tiles;
        n = tiles.length;
        board = new int[n][n];  // TODO: does this need to be 1-based indexed?
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                board[i][j] = tiles[i][j];
    }

    // string representation of this board
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result.append(String.format("%2d ", board[i][j]));
            }
            result.append("\n");
        }
        return result.toString();
    }

    // board dimension n
    public int dimension() { return n; }

    // number of tiles out of place
    public int hamming() {
        int h = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == n - 1 && j == n - 1) continue;
                if (board[i][j] == n * i + (j + 1)) h++;
            }
        }
        return h;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int m = 0;
        int eX, eY;  // expected x and y
        for (int cX = 0; cX < n; cX++) {
            for (int cY = 0; cY < n; cY++) { // current x and y
                int k = board[cX][cY];
                if (k == 0) continue;
                eX = (k - 1) % n;
                eY = k / n; // TODO: will this just automatically do integer division e.g. 8/3 = 2
                m += Math.abs((eX - cX) + (eY - cY));
            }
        }
        return m;
    }

    // is this board the goal board?
    public boolean isGoal() {
        int eX, eY;  // expected x and y
        for (int cX = 0; cX < n; cX++) {
            for (int cY = 0; cY < n; cY++) { // current x and y
                int k = board[cX][cY];
                if (k == 0) continue;
                eX = k / n; // TODO: not calculated correctly
                eY = (k - 1) % n;
                if (eX != cX || eY != cY) return false;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        return Arrays.deepEquals(board, ((Board) y).board);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> r = new Stack<Board>();
        return r;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() { return new Board(new int[3][3]); }

    // unit testing (not graded)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        int[][] same = new int[n][n];
        int[][] diff = new int[n][n];
        int[][] goal = new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
                same[i][j] = tiles[i][j];
                diff[i][j] = tiles[i][j];
            }
        }
        int temp = diff[1][2];
        diff[1][2] = diff[0][1];
        diff[0][1] = temp;

        Board initial = new Board(tiles);
        Board otherSame = new Board(same);
        Board otherDiff = new Board(diff);
        Board otherGoal = new Board(goal);

        StdOut.println(initial.toString());
        StdOut.println("dimension: " + initial.dimension());
        StdOut.println("hamming: " + initial.hamming());
        StdOut.println("manhattan: " + initial.manhattan());
        StdOut.println("isGoal (no): " + initial.isGoal());
        StdOut.println("isEqual (equal): " + initial.equals(otherSame) + "\n");

        StdOut.println(otherDiff.toString());
        StdOut.println("isEqual (different): " + initial.equals(otherDiff) + "\n");

        StdOut.println(otherGoal.toString());
        StdOut.println("isGoal (yes): " + otherGoal.isGoal());
    }

}
