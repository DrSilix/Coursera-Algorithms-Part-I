/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     11/19/2023
 *
 *  Compilation: javac-algs4 Percolation.java
 *  Execution: java-algs4 Percolation < input10.txt
 *
 *  Library for use with PercolationVisualizer or PercolationStats. Assists
 *  with the construction of a blank n-by-n grid union-find data structure and
 *  then can be used to perform operations on that data structure including opening
 *  nodes, checking if they are open or full, checking the number of sites, and
 *  checking to see if the system percolates
 *
 *  This can also be run itself with a text file standard input and will output
 *  the connection pairs ending with the number of components
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final byte CLOSED = 0, OPEN = 1, OPEN_FULL = 2;
    private static final int TOP_FAKE_NODE = 0;

    private short gridSize;
    private WeightedQuickUnionUF uf;
    private byte[] openFull;
    private boolean percolates, backwashFix;
    private int numOpen;

    // creates n-by-n grid, with all sites initially blocked
    // id 0 and the last id are the fake nodes, the top and bottom respectively
    // the size of the data structures are 2 greater than needed, the extra 2 are for the fake nodes
    public Percolation(int n) {
        validate(n);

        gridSize = (short) n;
        uf = new WeightedQuickUnionUF(gridSize * gridSize + 2);
        openFull = new byte[gridSize * gridSize + 2];

        numOpen = 0;
        openFull[TOP_FAKE_NODE] = OPEN_FULL;
        openFull[bottomFakeNode(gridSize)] = OPEN;
    }

    // returns the 1d index for the given coordinates if it is within bounds otherwise it returns -1
    private int getXYTo1D(int row, int col) {
        if (row < 1 || row > gridSize || col < 1 || col > gridSize) return -1;
        return (gridSize * (row - 1)) + col;
    }

    // returns
    private int[] get1DtoXY(int n) {
        return new int[] { ((n - 1) / gridSize) + 1, ((n - 1) % gridSize) + 1 };
    }

    private static int bottomFakeNode(int gridSize) { return gridSize * gridSize + 1; }

    // returns array of 1d indices for valid neighbors to a node {up, down, left, right}
    // if the node is in the top or bottom row the relevant fake node id is swapped
    private int[] retrieveNeighborsTo1D(int row, int col) {
        int up = getXYTo1D(row - 1, col);
        int down = getXYTo1D(row + 1, col);
        int left = getXYTo1D(row, col - 1);
        int right = getXYTo1D(row, col + 1);

        if (up == -1) up = TOP_FAKE_NODE;
        if (down == -1) down = bottomFakeNode(gridSize);

        return new int[] { up, down, left, right };
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);

        int p = getXYTo1D(row, col);
        if (openFull[p] > CLOSED) return;
        int[] nearby = retrieveNeighborsTo1D(row, col);
        union(p, nearby[0]);
        union(p, nearby[1]);
        union(p, nearby[2]);
        union(p, nearby[3]);
        openFull[p] = OPEN;
        numOpen++;

        percolates();
    }

    // q is -1 for invalid neighbors
    private void union(int p, int q) {
        if (q < 0 || openFull[q] == CLOSED || (percolates && q == bottomFakeNode(gridSize))) return;
        uf.union(p, q);
        if (openFull[q] == OPEN_FULL && openFull[p] < OPEN_FULL) openFull[p] = OPEN;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return openFull[getXYTo1D(row, col)] > CLOSED;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);

        if (backwashFix) backwashFix();

        int n = getXYTo1D(row, col);
        if (openFull[n] == OPEN_FULL || uf.find(n) == uf.find(TOP_FAKE_NODE)) {
            openFull[n] = OPEN_FULL;
            return true;
        }
        return false;
    }

    // wipe and rebuild the union-find then check percolates on each open bottom row node
    private void backwashFix() {
        uf = new WeightedQuickUnionUF(gridSize * gridSize + 2);
        for (int p = 1; p < openFull.length - 1; p++) {
            if (openFull[p] > CLOSED) {
                int[] xy = get1DtoXY(p);
                int[] nearby = retrieveNeighborsTo1D(xy[0], xy[1]);

                union(p, nearby[0]);
                if (nearby[1] != bottomFakeNode(gridSize)) union(p, nearby[1]);
                union(p, nearby[2]);
                union(p, nearby[3]);
            }
        }
        int startOfBottomRow = (openFull.length - 1) - gridSize;
        for (int p = startOfBottomRow; p < openFull.length - 1; p++) {
            if (openFull[p] > CLOSED && uf.find(p) == uf.find(TOP_FAKE_NODE)) {
                uf.union(p, bottomFakeNode(gridSize));
                break;
            }
        }
        backwashFix = false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() { return numOpen; }

    // does the system percolate?
    public boolean percolates() {
        if (!percolates && uf.find(bottomFakeNode(gridSize)) == uf.find(TOP_FAKE_NODE)) {
            percolates = true;
            backwashFix = true;
        }
        return percolates;
    }

    private void validate(int row, int col) {
        if (col < 1 || col > gridSize) {
            throw new IllegalArgumentException(
                    "column index " + col + " is not between 1 and " + gridSize);
        }
        if (row < 1 || row > gridSize) {
            throw new IllegalArgumentException(
                    "row index " + row + " is not between 1 and " + gridSize);
        }
    }

    private void validate(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Grid size " + n + " is not greater than 0");
        }
    }

    // test client (optional)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        Percolation test = new Percolation(n);
        while (!StdIn.isEmpty()) {
            int p = StdIn.readInt();
            int q = StdIn.readInt();
            test.open(p, q);
            // StdOut.println(p + " " + q);
        }
        StdOut.println(test.uf.count() + " components");
    }
}


