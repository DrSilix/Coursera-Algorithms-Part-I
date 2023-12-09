/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     12/7/2023
 *
 *  Compilation: javac-algs4 Solver.java
 *  Execution: java-algs4 Solver puzzle04.txt
 *  Dependencies: Board.java
 *
 *  Library that takes a 2d board representing the state of a sliding puzzle
 *  and uses an efficient A* search algorithm using a minimum priority queue
 *  binary tree to iterate over the board and find the least moves required to
 *  find the solution.
 *
 *  Text file argument formatting:
 *  puzzle04.txt
 *  3
 *  0 1 3
 *  4 2 5
 *  7 8 6
 *
 *  % java-algs4 Solver puzzle04.txt
 *  Minimum number of moves = 4
 *  3
 *   0  1  3
 *   4  2  5
 *   7  8  6
 *
 *  3
 *   1  0  3
 *   4  2  5
 *   7  8  6
 *
 *  3
 *   1  2  3
 *   4  0  5
 *   7  8  6
 *
 *  3
 *   1  2  3
 *   4  5  0
 *   7  8  6
 *
 *  3
 *   1  2  3
 *   4  5  6
 *   7  8  0
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;

public class Solver {
    private SearchNode solutionNode;
    private boolean isSolvable;
    private int moves;

    /**
     * Initializes a 2d board representing the state of a sliding puzzle
     * and uses an efficient A* search algorithm using a minimum priority queue
     * binary tree to iterate over the board and find the least moves required to
     * find the solution.<br />
     * <br />
     * Also, simultaneously solves a twin of the initial board (initial board with
     * 2 tiles flipped) to determine if the board is solvable
     *
     * @param  initial Board object representing the initial board state
     * @throws IllegalArgumentException if <tt>initial</tt> is null
     */
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Initial board cannot be null");

        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        isSolvable = true;
        moves = 0;

        pq.insert(new SearchNode(initial, 0, null, false));
        pq.insert(new SearchNode(initial.twin(), 0, null, true));

        while (true) {
            // step forward a solver for the main board
            SearchNode r = stepSolution(pq);
            if (r != null) {
                if (r.isTwin()) {
                    moves = -1;
                    solutionNode = null;
                    isSolvable = false;
                    return;
                }
                moves = r.getMoves();
                solutionNode = r;
                return;
            }
        }
    }

    /*
    * Steps forward a priority queue by de-queueing the minimum keyed item,
    * checking if it is the goal board, and then queueing the neighbor boards.
    * The previous board (guaranteed neighbor) and a neighbor board with a lower
    * priority are skipped.
    *
    * Returns a SearchNode if it is the goal board
     */
    private SearchNode stepSolution(MinPQ<SearchNode> q) {
        SearchNode minNode = q.delMin();
        SearchNode prevNode = minNode.getPrevNode();

        if (minNode.getBoard().isGoal()) {
            return minNode;
        }
        for (Board nbrs : minNode.getBoard().neighbors()) {
            if (prevNode != null && nbrs.equals(prevNode.getBoard())) continue;
            SearchNode nbrNode = new SearchNode(nbrs, minNode.getMoves() + 1, minNode, minNode.isTwin());
            if (nbrNode.getPriority() < minNode.getPriority()) continue;
            q.insert(nbrNode);
        }
        return null;
    }

    /**
     * @return is the initial board state solvable
     */
    public boolean isSolvable() { return isSolvable; }

    /**
     * @return min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves() { return moves; }

    /**
     * @return sequence of boards in a shortest solution; null if unsolvable
     */
    public Iterable<Board> solution() {
        ArrayList<Board> solution = new ArrayList<Board>();
        for (SearchNode node : solutionNode) {
            solution.add(node.getBoard());
        }
        return solution;
    }

    /*
    * Wrapper class for a board state which includes the board, the number of
    * moves to reach, and the previous board state
     */
    private class SearchNode implements Comparable<SearchNode>, Iterable<SearchNode> {
        private Board b;
        private int m;
        private SearchNode pN;
        private int manhattan, hamming;
        private boolean twin;


        public SearchNode(Board board, int moves, SearchNode prevNode, boolean isTwin) {
            b = board;
            m = moves;
            pN = prevNode;
            manhattan = board.manhattan();
            hamming = board.hamming();
            twin = isTwin;
        }

        public Board getBoard() { return b; }
        public int getMoves() { return m; }
        public SearchNode getPrevNode() { return pN; }
        public int getPriority() { return manhattan + m; }
        public String toString() { return b.toString(); }
        public boolean isTwin() { return twin; }

        /*
        * Heuristic analysis that drives the A* algorithm on most likely correct
        * path. Compares two board states to determine which is "minimum" or more
        * likely to be the shortest path to the solution.
         */
        public int compareTo(SearchNode that) {
            // Compares manhattan priorities (manhattan value + # of moves)
            int priorityComp = Integer.compare(manhattan + m, that.manhattan + that.m);
            if (priorityComp != 0) return priorityComp;

            // Compares the hamming priority (hamming value + moves)
            int hamPriorityComp = Integer.compare((hamming + m), (that.hamming + that.m));
            if (hamPriorityComp != 0) return hamPriorityComp;

            // Compares the manhattan values
            int manhattanComp = Integer.compare(manhattan, that.manhattan);
            if (manhattanComp != 0) return manhattanComp;

            // Compares the hamming values
            return Integer.compare(hamming, that.hamming);
        }

        // returns an iterator for the search nodes
        public Iterator<SearchNode> iterator() {
            return new SearchNodeIterator(this);
        }

        // traverses up the SearchNodes by previous nodes until at the initial board state
        // does so in reverse (initial state -> solution)
        private class SearchNodeIterator implements Iterator<SearchNode> {
            private Stack<SearchNode> reverseOrder = new Stack<SearchNode>();

            public SearchNodeIterator(SearchNode s) {
                SearchNode current = s;
                reverseOrder.push(current);
                while (current.getPrevNode() != null) {
                    reverseOrder.push(current.getPrevNode());
                    current = current.getPrevNode();
                }
            }

            public boolean hasNext() { return !reverseOrder.isEmpty(); }
            public SearchNode next() { return reverseOrder.pop(); }
        }
    }

    // test client
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
