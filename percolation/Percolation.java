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
    private int gridSize;
    private WeightedQuickUnionUF uf;
    private boolean[] open;
    private boolean[] full;
    private boolean percolates;
    private int numOpen;
    private int topFakeNode;
    private int bottomFakeNode;

    // creates n-by-n grid, with all sites initially blocked
    // id 0 and the last id are the fake nodes, the top and bottom respectively
    // the size of the data structures are 2 greater than needed, the extra 2 are for the fake nodes
    public Percolation(int n) {
        validate(n);

        gridSize = n;
        int gridSquared = gridSize * gridSize;

        uf = new WeightedQuickUnionUF(gridSquared + 2);

        open = new boolean[gridSquared + 2];
        full = new boolean[gridSquared + 2];

        numOpen = 0;

        topFakeNode = 0;
        bottomFakeNode = gridSquared + 1;

        open[topFakeNode] = true; // the top fake node needs to be open and full
        full[topFakeNode] = true;

        open[bottomFakeNode] = true; // the bottom fake node needs to be open and not full
    }

    // returns the 1d index for the given coordinates if it is within bounds otherwise it returns -1
    private int xyTo1D(int x, int y) {
        if (x < 1 || x > gridSize || y < 1 || y > gridSize) {
            return -1;
        }  // possibly use an exception to handle oob
        return (gridSize * (x - 1)) + y;
    }

    // returns array of 1d indices for valid neighbors to a node {up, down, left, right}
    // if the node is in the top or bottom row the relevant fake node id is swapped
    private int[] retrieveNeighborsTo1D(int x, int y) {
        int up = xyTo1D(x - 1, y);
        int down = xyTo1D(x + 1, y);
        int left = xyTo1D(x, y - 1);
        int right = xyTo1D(x, y + 1);

        if (up == -1) {
            up = topFakeNode;
        }
        if (down == -1) {
            down = bottomFakeNode;
        }

        return new int[] { up, down, left, right };
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);

        // get the index of the node and its nearby nodes
        int p = xyTo1D(row, col);
        int[] nearby = retrieveNeighborsTo1D(row, col);

        if (open[p]) {
            return;
        }

        checkNeighbors(p, nearby[0]);
        checkNeighbors(p, nearby[1]);
        checkNeighbors(p, nearby[2]);
        checkNeighbors(p, nearby[3]);

        open[p] = true;
        numOpen++;
    }

    private void checkNeighbors(int p, int q) {
        // neighbor value is -1 for invalid neighbors
        if (q < 0 || !open[q]) {
            return;
        }

        // call to union-find data structure that connects nodes
        uf.union(p, q);  // look into not checking if they are part of the same set

        // checks if the neighbor we are evaluating is full and marks the current node full
        if (full[q] && !full[p]) {
            full[p] = true;
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return open[xyTo1D(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);

        int n = xyTo1D(row, col);

        // quick check on the site
        if (full[n]) {
            return true;
        }

        if (uf.find(n) == uf.find(topFakeNode)) {
            full[n] = true;
            return true;
        }
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOpen;
    }

    // does the system percolate?
    public boolean percolates() {
        // quick check percolates value then do a more thorough check
        if (!percolates && uf.find(bottomFakeNode) == uf.find(topFakeNode)) {
            percolates = true;
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
            StdOut.println(p + " " + q);

            int opened = 0;
            for (int row = 1; row <= n; row++) {
                for (int col = 1; col <= n; col++) {
                    if (test.isFull(row, col)) {
                        opened++;
                    }
                    else if (test.isOpen(row, col)) {
                        opened++;
                    }
                }
            }
        }
        StdOut.println(test.uf.count() + " components");
    }
}


