/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;

public class Board {
    private final short[][] board;
    private final int n;
    private int manhattan, hamming;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        manhattan = -1;
        hamming = -1;
        n = tiles.length;
        board = new short[n][n];
        for (int y = 0; y < n; y++)
            for (int x = 0; x < n; x++)
                board[y][x] = (short) tiles[y][x];
    }

    private Board(short[][] shortTiles) {
        manhattan = -1;
        hamming = -1;
        n = shortTiles.length;
        board = new short[n][n];
        for (int y = 0; y < n; y++)
            for (int x = 0; x < n; x++)
                board[y][x] = shortTiles[y][x];
    }

    // string representation of this board
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(n + "\n");
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                result.append(String.format("%2d ", board[y][x]));
            }
            result.append("\n");
        }
        return result.toString();
    }

    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    // board dimension n
    public int dimension() { return n; }

    // number of tiles out of place
    public int hamming() {
        if (hamming >= 0) return hamming;
        hamming = 0;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (y == n - 1 && x == n - 1) continue;
                if (board[y][x] == n * y + (x + 1)) hamming++;
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        if (manhattan >= 0) return manhattan;
        manhattan = 0;
        int eX, eY;  // expected x and y
        for (int cY = 0; cY < n; cY++) {
            for (int cX = 0; cX < n; cX++) { // current x and y
                int k = board[cY][cX];
                if (k == 0) continue;
                eX = (k - 1) % n;
                eY = (k - 1) / n;
                manhattan += Math.abs((eX - cX) + (eY - cY));
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        int eX, eY;  // expected x and y
        for (int cY = 0; cY < n; cY++) {
            for (int cX = 0; cX < n; cX++) { // current x and y
                int k = board[cY][cX];
                if (k == 0) continue;
                eX = (k - 1) % n;
                eY = (k - 1) / n;
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
        if (dimension() != ((Board) y).dimension()) return false;
        return Arrays.deepEquals(board, ((Board) y).board);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        short[][] copy = new short[n][n];
        int zX = -1, zY = -1;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                copy[y][x] = board[y][x];
                if (board[y][x] == 0) {
                    zX = x;
                    zY = y;
                }
            }
        }

        LinkedList<Board> neighbors = new LinkedList<Board>();
        // left neighbor
        Board b1 = getNeighbor(copy, zX, zY, zX - 1, zY);
        if (b1 != null) neighbors.add(b1);
        // down neighbor
        Board b2 = getNeighbor(copy, zX, zY, zX, zY + 1);
        if (b2 != null) neighbors.add(b2);
        // right neighbor
        Board b3 = getNeighbor(copy, zX, zY, zX + 1, zY);
        if (b3 != null) neighbors.add(b3);
        // up neighbor
        Board b4 = getNeighbor(copy, zX, zY, zX, zY - 1);
        if (b4 != null) neighbors.add(b4);

        return neighbors;
    }

    // TODO: apparently there is an optimization in considering neighbors are +- 1 manhattan
    private Board getNeighbor(short[][] copy, int x, int y, int nX, int nY) {
        if (nX < 0 || nX >= n || nY < 0 || nY >= n) return null;
        short temp;
        temp = copy[nY][nX];
        copy[nY][nX] = copy[y][x];
        copy[y][x] = temp;
        Board b = new Board(copy);
        copy[y][x] = copy[nY][nX];
        copy[nY][nX] = temp;

        return b;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        short[][] twin = new short[n][n];
        int sX = -1;
        int sY = -1;
        short sK = -1;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                short k = board[y][x];
                twin[y][x] = k;
                if (sK > -1 && k != 0) {
                    twin[y][x] = sK;
                    twin[sY][sX] = k;
                    sK = -2;
                }
                if (sK == -1 && k != 0) {
                    sK = k;
                    sX = x;
                    sY = y;
                }
            }
        }
        return new Board(twin);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        int[][] same = new int[n][n];
        int[][] diff = new int[n][n];
        int[][] goal = new int[][] { { 1, 2, 3}, { 4, 5, 6}, { 7, 8, 0}};
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
        Board otherGoal = new Board(goal);

        StdOut.print(initial.toString());
        StdOut.println("dimension: " + initial.dimension());
        StdOut.println("hamming: " + initial.hamming());
        StdOut.println("manhattan: " + initial.manhattan());
        StdOut.println("isGoal (false): " + initial.isGoal());
        StdOut.println("isEqual (true): " + initial.equals(otherSame) + "\n");
        StdOut.println("Twin:");
        StdOut.print(initial.twin().toString());
        StdOut.println("Twin isEqual (false): " + initial.equals(initial.twin()) + "\n");

        StdOut.print(otherGoal.toString());
        StdOut.println("isGoal (true): " + otherGoal.isGoal());
        StdOut.println();
        StdOut.print(initial.toString());
        for (Board b : initial.neighbors()) {
            StdOut.print(b.toString());
        }
        StdOut.println(initial.hashCode());
        StdOut.println(otherSame.hashCode());
    }

}
