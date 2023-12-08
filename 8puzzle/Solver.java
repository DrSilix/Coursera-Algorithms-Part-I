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

import java.util.ArrayList;
import java.util.Iterator;

public class Solver {
    private static final int HASHES_TO_STORE = 100;

    private SearchNode solutionNode;
    private boolean isSolvable;
    private int moves;
    private int queueSize;
    // private Deque<SearchNode> hashes;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Initial board cannot be null");
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        MinPQ<SearchNode> twinPq = new MinPQ<SearchNode>();

        // hashes = new ArrayDeque<SearchNode>();

        isSolvable = true;

        moves = 0;

        // SearchNode minNode, twinMinNode;
        SearchNode finalNode;

        // SearchNode prevNode = new SearchNode(initial, moves, null);
        // pq.insert(prevNode);
        pq.insert(new SearchNode(initial, 0, null));

        twinPq.insert(new SearchNode(initial.twin(), 0, null));

        while (true) {
            // step forward a solver for the main board
            SearchNode r = stepSolution(pq, false);
            if (r != null) {
                finalNode = r;
                moves = finalNode.getMoves();
                queueSize = pq.size();
                solutionNode = finalNode;
                return;
            }

            // step forward a solver for the twin board simultaneously
            SearchNode tR = stepSolution(twinPq, true);
            if (tR != null) {
                moves = -1;
                solutionNode = null;
                isSolvable = false;
                return;
            }
        }
    }

    // pass pq
    private SearchNode stepSolution(MinPQ<SearchNode> q, boolean isTwin) {
        SearchNode minNode = q.delMin();
        SearchNode prevNode = minNode.getPrevNode();
        // if (!isTwin) StdOut.print("\nmove: " + minNode.getMoves() + " priority:" + minNode.getPriority() + " queueSize:" + q.size() + " hash:" + minNode.getBoard().hashCode());

        /* if (!isTwin) {
            int i = 0;
            int thisHash = minNode.getBoard().hashCode();
            for (Iterator it = hashes.iterator(); it.hasNext(); ) {
                SearchNode thatNode = (SearchNode) it.next();
                if (thisHash == thatNode.getBoard().hashCode())
                    StdOut.print(" - Duplicate Board " + i);
                i++;
            }

            hashes.addFirst(minNode);
            if (hashes.size() >= HASHES_TO_STORE) hashes.removeLast();
        }*/

        if (minNode.getBoard().isGoal()) {
            return minNode;
        }
        int move = minNode.getMoves()+1;
        for (Board nbrs : minNode.getBoard().neighbors()) {
            if (prevNode != null && nbrs.equals(prevNode.getBoard())) continue;
            q.insert(new SearchNode(nbrs, move, minNode));
        }
        return null;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() { return isSolvable; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() { return moves; }

    public int getQueueSize() { return queueSize; }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        ArrayList<Board> solution = new ArrayList<Board>();
        for (SearchNode node : solutionNode) {
            // StdOut.print(node.getBoard().toString());
            solution.add(node.getBoard());
        }
        return solution;
    }

    private class SearchNode implements Comparable<SearchNode>, Iterable<SearchNode> {
        private Board b;
        private int m;
        private SearchNode pN;
        private int priority;
        private int hash;


        public SearchNode(Board board, int moves, SearchNode prevNode) {
            b = board;
            m = moves;
            pN = prevNode;
            hash = b.hashCode();

            priority = moves + board.manhattan();
        }

        public Board getBoard() { return b; }
        public int getMoves() { return m; }
        public SearchNode getPrevNode() { return pN; }
        public int getPriority() { return priority; }
        public String toString() { return b.toString(); }
        public int hashCode() { return hash; }

        public int compareTo(SearchNode that) {
            int priorityComp = Integer.compare(priority, that.priority);
            if (priorityComp != 0) return priorityComp;
            int manhattanComp = Integer.compare(b.manhattan(), that.b.manhattan());
            if (manhattanComp != 0) return manhattanComp;
            int hamPriorityComp = Integer.compare((b.hamming() + getMoves()), (that.b.hamming() + that.getMoves()));
            if (hamPriorityComp != 0) return hamPriorityComp;
            int hamComp = Integer.compare(b.hamming(), that.b.hamming());
            if (hamComp != 0) return hamComp;
            return 0;

            /*// last resort
            int thisMinNeighborPriority = priority + 10;
            for (Board neighbor : b.neighbors()) {
                if (neighbor.manhattan() < thisMinNeighborPriority) thisMinNeighborPriority = neighbor.manhattan();
            }
            int thatMinNeighborPriority = priority + 10;
            for (Board neighbor : that.b.neighbors()) {
                if (neighbor.manhattan() < thatMinNeighborPriority) thatMinNeighborPriority = neighbor.manhattan();
            }
            return Integer.compare(thisMinNeighborPriority, thatMinNeighborPriority);*/
        }

        public Iterator<SearchNode> iterator() {
            return new SearchNodeIterator(this);
        }

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

        // StdOut.println("moves: " + solver.moves());

        // TODO: reformat to provided main jic
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            // StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.print(board);
            StdOut.println(" moves:" + solver.moves() + " size:" + n + " queueSize:" + solver.getQueueSize());
        }
    }

}
