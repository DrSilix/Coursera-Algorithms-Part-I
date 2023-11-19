/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 *
 *
 *  backwash: maybe continually reset/unlink the bottom node. I could keep a list
 *  of the bottom nodes that open with unique parents. When checking for percolation
 *  I iterate unioning those lists... massive issue with causing errors in the uf datastruct
 *  once unioned unlinking a node won't update it's neighbors that were also linked
 *
 *  - using n separate fake bottom nodes (ignores the purpose of the fake bottom "node"
 *  - faq suggests using a second uf object (wouldn't this double everything for the non visualizer stuff)
 *  - not linking nodes to the bottom unless full (again ignores purpose of bottom node)
 *  - create a sort of custom filtered union find bottom contraption
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int gridSize;
    private WeightedQuickUnionUF uf;
    private boolean[] open;
    private boolean[] full;
    private int numOpen;
    private boolean percolates;
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
        percolates = false; // this is necessary to efficiently check percolation for backwash

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

        // check each nearby node if it is open, if so then union
        for (int i = 0, len = nearby.length; i < len; i++) {
            int q = nearby[i];

            // neighbor value is -1 for invalid neighbors
            if (q < 0 || !open[q] || (percolates && q == bottomFakeNode)) {
                continue;
            }

            if (uf.find(p) != uf.find(q)) {
                uf.union(p, q);
            }

            // this is a computation saving measure
            // it will not do a union-find query for the fullness of the canonical node
            // it checks if the neighbor we are evaluating is full and marks the current node full
            if (full[q] && !full[p]) {
                full[p] = true;

                // loop through all neighbors to fill them
                for (int j = 0; j < len; j++) {
                    if (nearby[j] < 0 || !open[nearby[j]] || nearby[j] == bottomFakeNode) {
                        continue; // continues if neighbor value is invalid, closed, or the bottom node
                    }
                    full[nearby[j]] = true;
                }
            }
        }

        open[p] = true;
        numOpen++;
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

        // check if the parent is full, mark the node full if so
        int parent = uf.find(n);
        if (full[parent]) {
            full[n] = true;
            return true;
        }

        // check if the parent of the node is the same parent as the top
        // mark full if so
        if (parent == uf.find(topFakeNode)) {
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
                    if (test.isFull(-1, -1)) {
                        StdOut.println(opened);
                    }
                }
            }
        }
        StdOut.println(test.uf.count() + " components");
    }
}


