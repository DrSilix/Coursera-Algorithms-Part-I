/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 *
 * We define a search node of the game to be a board, the number of moves made
 * to reach the board, and the previous search node. First, insert the initial
 * search node (the initial board, 0 moves, and a null previous search node) into
 * a priority queue. Then, delete from the priority queue the search node with
 * the minimum priority, and insert onto the priority queue all neighboring
 * search nodes (those that can be reached in one move from the dequeued search
 * node). Repeat this procedure until the search node dequeued corresponds to
 * the goal board.
 *
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class Solver {

    boolean isSolvable;
    int moves;
    // ArrayList<Board> solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        isSolvable = true;
        moves = 0;
        SearchNode minNode;
        SearchNode prevNode = new SearchNode(initial, moves, null);
        pq.insert(prevNode);
        while (true) {   // TODO: there has to be some fundamental problem like not properly removing boards
            minNode = pq.delMin();
            if (minNode.getBoard().isGoal()) { break; }
            moves = minNode.getMoves()+1;
            for (Board nbrs : minNode.getBoard().neighbors()) {
                if (nbrs.equals(prevNode.getBoard())) continue;
                pq.insert(new SearchNode(nbrs, moves, minNode));
            }
            prevNode = minNode;
        }

        for (SearchNode node : minNode) {
            StdOut.print(node.getBoard().toString());
        }

    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() { return isSolvable; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() { return moves; }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return new Stack<>();
    }

    private class SearchNode implements Comparable<SearchNode>, Iterable<SearchNode> {
        private Board b;
        private int m;
        private SearchNode pN;
        private int priority;


        public SearchNode(Board board, int moves, SearchNode prevNode) {
            b = board;
            m = moves;
            pN = prevNode;

            priority = moves + board.manhattan();
        }

        public Board getBoard() { return b; }
        public int getMoves() { return m; }
        public SearchNode getPrevNode() { return pN; }
        public int getPriority() { return priority; }
        public String toString() { return b.toString(); }

        public int compareTo(SearchNode that) {
            return Integer.compare(priority, that.priority);
        }

        public Iterator<SearchNode> iterator() {
            return new SearchNodeIterator(this);
        }

        private class SearchNodeIterator implements Iterator<SearchNode> {
            private SearchNode current;
            private Stack<SearchNode> reverseOrder = new Stack<SearchNode>();

            public SearchNodeIterator(SearchNode s) {
                current = s;
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

    // test client (see below)
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

        StdOut.println("moves: " + solver.moves());


        // print solution to standard output
        /* if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }*/
    }

}
