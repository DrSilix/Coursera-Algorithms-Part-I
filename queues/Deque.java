/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     11/26/2023
 *
 *  Compilation: javac-algs4 Deque.java
 *  Execution: java-algs4 Deque size verboseLog(Default: true) executeNonConstantOperations(Default: true)
 *  Execution: java-algs4 Deque 10
 *  Execution: java-algs4 Deque 10 false true
 *
 *  A double-ended queue library that maintains a bidirectional linked-list queue of generic items.
 *  Items may be added to the front or back of the queue and may also be removed from either.
 *  Includes a front to back iterator
 *
 *  Performance Specs
 *  Non-iterator operations      Constant worst-case time
 *  Iterator constructor         Constant worst-case time
 *  Other iterator operations    Constant worst-case time
 *  Non-iterator memory use      Linear in current # of items
 *  Memory per iterator          Constant
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    /* @citation Adapted from: https://algs4.cs.princeton.edu/
     * 13stacks/Queue.java.html.  Accessed: 11/21/2023 */

    private int size;
    private Node first, last;

    // constructs an empty deque with a link to the next and previous node
    public Deque() {
        first = null;
        last = null;
        size = 0;
    }

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null || last == null;
    }

    // returns the number of items on the deque
    public int size() {
        return size;
    }

    // adds the item to the front
    // swaps a new blank item with the old first item then assigns the other to next or last respectively
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("addFirst argument cannot be null.");
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = null;
        first.prev = null;
        if (isEmpty()) { last = first; }
        else {
            first.next = oldFirst;
            if (oldFirst != null) {
                oldFirst.prev = first;
            }
        }
        size++;
    }

    // adds the item to the back
    // swaps a new blank item with the old last item then assigns the other to last or next respectively
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("addLast argument cannot be null.");
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        last.prev = null;
        if (isEmpty()) { first = last; }
        else {
            if (oldLast != null) {
                oldLast.next = last;
            }
            last.prev = oldLast;
        }
        size++;
    }

    // removes and returns the item from the front
    // sets the first item to its next item and returns it and removes that next items value for prev
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Cannot call removeFirst when Deque is empty.");
        Item item = first.item;
        first = first.next;
        if (first != null) { first.prev = null; }
        size--;
        if (isEmpty()) { last = null; }
        return item;
    }

    // removes and returns the item from the back
    // sets the last item to its prev item and returns it and removes that prev items value for next
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Cannot call removeLast when Deque is empty.");
        // this one will be difficult because the last node has no direct reference to the node before it
        Item item = last.item;
        last = last.prev;
        if (last != null) { last.next = null; }
        size--;
        if (isEmpty()) { first = null; }
        return item;
    }

    // returns an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // returns items in the queue from front to back
    private class DequeIterator implements Iterator<Item> {
        /* @citation Copied from: https://algs4.cs.princeton.edu/
         * 13stacks/Queue.java.html.  Accessed: 11/21/2023 */
        private Node current = first;

        public boolean hasNext() { return current != null; }
        public void remove() { throw new UnsupportedOperationException("remove operation is no longer supported."); }
        public Item next() {
            if (!hasNext()) { throw new NoSuchElementException(); }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // unit testing
    // param 1  (int): size of Deque
    // param 2 (bool): Prints the Deque after each operation (Default: true)
    // param 3 (bool: Performs non-constant time operations (Default: true)
    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        double testStartTime, testEndTime;
        double[] results = new double[7];

        int size = Integer.parseInt(args[0]);
        boolean verboseLog = true, performNonConstantOperations = true;

        Deque<Integer> deque = new Deque<Integer>();
        StringBuilder output = new StringBuilder();

        if (args.length >= 2) verboseLog = Boolean.parseBoolean(args[1]);
        if (args.length >= 3) performNonConstantOperations = Boolean.parseBoolean(args[2]);


        StdOut.println("Unit Test : addFirst");
        testStartTime = timer.elapsedTime();
        for (int i = 1; i <= size; i++) {
            deque.addFirst(i);
            if (verboseLog) StdOut.println(printDeque(deque));
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        results[0] = testEndTime;
        StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime/size + " seconds/entry - Deque has " + deque.size() + " Nodes");


        if (performNonConstantOperations) {
            StdOut.println("\nUnit Test : iterator while full");
            testStartTime = timer.elapsedTime();
            String iteratorResult = printDeque(deque);
            if (verboseLog) StdOut.println(iteratorResult);
            testEndTime = timer.elapsedTime() - testStartTime;
            results[1] = testEndTime;
            StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime / size + " seconds/entry");
        }


        StdOut.println("\nUnit Test : removeLast");
        output = new StringBuilder(size * 2);
        testStartTime = timer.elapsedTime();
        while (!deque.isEmpty()) {
            if (verboseLog) {
                StdOut.println(printDeque(deque));
                output.append(String.valueOf(deque.removeLast())).append("-");
            } else {
                deque.removeLast();
            }
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        results[2] = testEndTime;
        if (output.length() >= 2) output.delete(output.length()-1, output.length());
        if (verboseLog) StdOut.println("Removed items: " + output.toString());
        StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime/size + " seconds/entry - Deque has " + deque.size() + " Nodes");


        if (performNonConstantOperations) {
            StdOut.println("\nUnit Test : iterator while empty");
            testStartTime = timer.elapsedTime();
            String iteratorResult = printDeque(deque);
            if (verboseLog) StdOut.println(iteratorResult);
            testEndTime = timer.elapsedTime() - testStartTime;
            results[3] = testEndTime;
            StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime / size + " seconds/entry");
        }


        StdOut.println("\nUnit Test : addLast");
        testStartTime = timer.elapsedTime();
        for (int i = size; i > 0; i--) {
            deque.addLast(i);
            if (verboseLog) StdOut.println(printDeque(deque));
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        results[4] = testEndTime;
        StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime/size + " seconds/entry - Deque has " + deque.size() + " Nodes");


        if (performNonConstantOperations) {
            StdOut.println("\nUnit Test : Nested Iterators");
            testStartTime = timer.elapsedTime();
            for (int p : deque) {
                output = new StringBuilder();
                for (int q : deque) {
                    if (verboseLog) output.append(p).append("-").append(q).append(" ");
                }
                if (verboseLog) StdOut.println(output);
            }
            testEndTime = timer.elapsedTime() - testStartTime;
            results[5] = testEndTime;
            StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime / size + " seconds/entry - Deque has " + deque.size() + " Nodes");
        }


        StdOut.println("\nUnit Test : removeFirst");
        output = new StringBuilder(size * 2);
        testStartTime = timer.elapsedTime();
        while (!deque.isEmpty()) {
            if (verboseLog) {
                StdOut.println(printDeque(deque));
                output.append(String.valueOf(deque.removeFirst())).append("-");
            } else {
                deque.removeFirst();
            }
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        results[6] = testEndTime;
        if (output.length() >= 2) output.delete(output.length()-1, output.length());
        if (verboseLog) StdOut.println("Removed items: " + output.toString());
        StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime/size + " seconds/entry - Deque has " + deque.size() + " Nodes");


        if (performNonConstantOperations) {
            StdOut.println("\nUnit Test : Calling Random Methods");
            int dummy = 0;
            testStartTime = timer.elapsedTime();
            Iterator<Integer> iterator = deque.iterator();
            for (int i = 1; i <= size * 10; i++) {
                switch (StdRandom.uniformInt(7)) {
                    case 0:
                        deque.addFirst(StdRandom.uniformInt(1000000) - 500000);
                        iterator = deque.iterator();
                        break;
                    case 1:
                        if (!deque.isEmpty()) deque.removeFirst();
                        break;
                    case 2:
                        deque.addLast(StdRandom.uniformInt(1000000) - 500000);
                        iterator = deque.iterator();
                        break;
                    case 3:
                        if (!deque.isEmpty()) deque.removeLast();
                        break;
                    case 4:
                        // second iterator
                        for (int j : deque) {
                            dummy = j;
                        }
                        break;
                    case 5:
                        dummy = deque.size();
                        break;
                    case 6:
                        if (iterator.hasNext()) iterator.next();
                }
            }
            testEndTime = timer.elapsedTime() - testStartTime;
            results[5] = testEndTime;
            StdOut.print(Integer.toString(dummy).substring(0, 0));
            StdOut.println("Test completed in " + testEndTime);
        }


        StdOut.println("\nResults: " + results[0] + "/" + results[1] + "/" + results[2] + "/" + results[3] + "/" + results[4] + "/" + results[5] + "/" + results[6] + " over " + timer.elapsedTime() + " seconds");
        StdOut.println("Result/element: " + results[0]/size + "/" + results[1]/size + "/" + results[2]/size + "/" + results[3]/size + "/" + results[4]/size + "/" + results[5]/size + "/" + results[6]/size);
    }

    private static String printDeque(Deque<Integer> deque) {
        StringBuilder output = new StringBuilder();
        for (int i : deque) {
            output.append(i).append("-");
        }
        if (output.length() >= 2) output.delete(output.length()-1, output.length());
        return output.toString();
    }
}
