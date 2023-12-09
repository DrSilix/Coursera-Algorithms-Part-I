/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/8/2023
 *
 *  Compilation: javac-algs4 Board.java
 *  Execution: java-algs4 Board puzzle04.txt
 *
 *  Library that takes a 2d board representing the state of a sliding puzzle
 *  and stores it and it's properties as efficiently as possible
 *
 *  Text file argument formatting:
 *  puzzle04.txt
 *  3
 *  0 1 3
 *  4 2 5
 *  7 8 6
 *
 *  % java-algs4 Board puzzle04.txt
 *  3
 *   0  1  3
 *   4  2  5
 *   7  8  6
 *  dimension: 3
 *  hamming: 4
 *  manhattan: 4
 *  isGoal (false): false
 *  isEqual (true): true
 *  Twin:
 *  3
 *   0  3  1
 *   4  2  5
 *   7  8  6
 *  Twin isEqual (false): false
 *  Neighbors:
 *  3
 *   4  1  3
 *   0  2  5
 *   7  8  6
 *  3
 *   1  0  3
 *   4  2  5
 *   7  8  6
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;

public class Board {
    private final short[][] board;
    private final int n;


    /**
     * Initializes a 2d board representing the state of a sliding puzzle
     *
     * @param  tiles 2d int array representing the initial board state
     */
    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        board = new short[n][n];
        for (int y = 0; y < n; y++)
            for (int x = 0; x < n; x++)
                board[y][x] = (short) tiles[y][x];
    }

    /*
     * Alternative constructor. Initializes a 2d board representing the state
     * of a sliding puzzle. Takes in a 2d short array
     */
    private Board(short[][] shortTiles) {
        n = shortTiles.length;
        board = new short[n][n];
        for (int y = 0; y < n; y++)
            for (int x = 0; x < n; x++)
                board[y][x] = shortTiles[y][x];
    }

    /**
     * Returns a multi-line string representation of this board
     * @return string representation in Format:<br />
     * 3<br />
     * &nbsp;1 2 3<br />
     * &nbsp;4 5 6<br />
     * &nbsp;7 8 9<br />
      */
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

    /**
     * @return int board dimension n
     */
    public int dimension() { return n; }

    /**
     * @return int number of tiles out of place
     */
    public int hamming() {
        int hamming = 0;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (y == n - 1 && x == n - 1) continue; // exclude the 0 space
                if (board[y][x] != n * y + (x + 1)) hamming++;
            }
        }
        return hamming;
    }

    /**
     * @return int sum of Manhattan distances between tiles and goal
     */
    public int manhattan() {
        int manhattan = 0;
        int eX, eY;  // expected x and y
        for (int cY = 0; cY < n; cY++) {
            for (int cX = 0; cX < n; cX++) { // current x and y
                int k = board[cY][cX];
                if (k == 0) continue; // exclude 0 space
                eX = (k - 1) % n;
                eY = (k - 1) / n;
                manhattan += Math.abs((eX - cX)) + Math.abs((eY - cY));
            }
        }
        return manhattan;
    }

    /**
     * @return boolean is this board the goal board?
      */
    public boolean isGoal() {
        return hamming() == 0;
    }

    /**
     * does this board equal y? Compares two boards
     * @return boolean are the two boards equal
      */
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        if (dimension() != ((Board) y).dimension()) return false;
        return Arrays.deepEquals(board, ((Board) y).board);
    }

    /**
     * all neighboring boards
     * @return Iterable containing all neighboring boards
     */
    public Iterable<Board> neighbors() {
        short[][] copy = new short[n][n];
        int zX = -1, zY = -1;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                copy[y][x] = board[y][x];
                if (board[y][x] == 0) {
                    zX = x; // zero x location
                    zY = y; // zero y location
                }
            }
        }

        LinkedList<Board> neighbors = new LinkedList<Board>();
        Board b1 = getNeighbor(copy, zX, zY, zX - 1, zY); // left neighbor
        if (b1 != null) neighbors.add(b1);
        Board b2 = getNeighbor(copy, zX, zY, zX, zY + 1); // down neighbor
        if (b2 != null) neighbors.add(b2);
        Board b3 = getNeighbor(copy, zX, zY, zX + 1, zY); // right neighbor
        if (b3 != null) neighbors.add(b3);
        Board b4 = getNeighbor(copy, zX, zY, zX, zY - 1); // up neighbor
        if (b4 != null) neighbors.add(b4);

        return neighbors;
    }

    /*
    * Swaps two board positions to create a neighbor board
    * Takes a 2d short array, the first position x and y, and neighbor x and y
    * the 2d short array is mutated but returned to it's original state
     */
    private Board getNeighbor(short[][] copy, int x, int y, int nX, int nY) {
        if (nX < 0 || nX >= n || nY < 0 || nY >= n) return null;
        short temp = copy[nY][nX];
        copy[nY][nX] = copy[y][x];
        copy[y][x] = temp;
        Board b = new Board(copy); // snap a board from the swapped state

        // undo the swap and return the input array to it's original state
        copy[y][x] = copy[nY][nX];
        copy[nY][nX] = temp;

        return b;
    }

    /**
     * @return Board that is obtained by exchanging the first two tiles found
      */
    public Board twin() {
        short[][] twin = new short[n][n];
        short sK = -1;
        for (int y = 0; y < n; y++) {
            // initialize the swap values per row (twin can only swap values in row)
            int sX = -1;
            int sY = -1;
            if (sK != -2) sK = -1; // the swap key value, used as flag
            for (int x = 0; x < n; x++) {
                short k = board[y][x];
                twin[y][x] = k;

                // if a swap key exists swaps the current x and y with the swap values
                if (sK > -1 && k != 0) {
                    twin[y][x] = sK;
                    twin[sY][sX] = k;
                    sK = -2;
                }

                // records the key, x and y of the first tile. The swap tile. Skips 0
                if (sK == -1 && k != 0) {
                    sK = k;
                    sX = x;
                    sY = y;
                }
            }
        }
        return new Board(twin);
    }

    // unit testing
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        int[][] same = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
                same[i][j] = tiles[i][j];
            }
        }

        Board initial = new Board(tiles);
        Board otherSame = new Board(same);

        StdOut.print(initial.toString());
        StdOut.println("dimension: " + initial.dimension());
        StdOut.println("hamming: " + initial.hamming());
        StdOut.println("manhattan: " + initial.manhattan());
        StdOut.println("isGoal (false): " + initial.isGoal());
        StdOut.println("isEqual (true): " + initial.equals(otherSame));
        StdOut.println("Twin:");
        StdOut.print(initial.twin().toString());
        StdOut.println("Twin isEqual (false): " + initial.equals(initial.twin()));
        StdOut.println("Neighbors:");
        for (Board b : initial.neighbors()) {
            StdOut.print(b.toString());
        }
    }
}
