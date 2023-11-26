/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

public class MemoryTest {
    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<Integer>();
        int n = Integer.parseInt(args[0]);

        StdOut.println("Test Started");
        q.enqueue(999);
        StdOut.println("Queue with 1 element");
        for (int i = 1; i < n; i++) {
            q.enqueue(i);
        }
        StdOut.println("Queue Full");
        for (int i = 1; i < n; i++) {
            q.dequeue();
        }
        StdOut.println("Queue empty except 1");
    }
}
