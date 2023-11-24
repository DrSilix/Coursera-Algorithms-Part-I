/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 *
 *  Requires
 *  Non-iterator operations      Constant worst-case time       (linked list)
 *  Iterator constructor         Constant worst-case time       (probably linked list)
 *  Other iterator operations    Constant worst-case time       (has next, next, remove)
 *  Non-iterator memory use      Linear in current # of items   (a linked list would be linear, an array list linear amortized??)
 *  Memory per iterator          Constant
 *
 *  pretty sure have to use linked list to get constant non-iterator operations time worst-case
 *
 *  crap, it seems like the output time is quadratic, logarithmic then quadratic, and there's spikes????
 *  need to implement exceptions
 *  need to test nested iterators
 *
 *  to properly test method timing, create a deque of specified size and then time performing
 *  exponential operations on it.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Deque<Item> implements Iterable<Item> {
    private int size;
    private Node first, last;

    // construct an empty deque
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

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
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

    // add the item to the back
    public void addLast(Item item) {
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        last.prev = null;
        if (isEmpty()) { first = last; }
        else {
            oldLast.next = last;
            if (last != null) {
                last.prev = oldLast;
            }
        }
        size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        Item item = first.item;
        first = first.next;
        if (first != null) { first.prev = null; }
        size--;
        if (isEmpty()) { last = null; }
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        // this one will be difficult because the last node has no direct reference to the node before it
        Item item = last.item;
        last = last.prev;
        if (last != null) { last.next = null; }
        size--;
        if (isEmpty()) { first = null; }
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // need to understand this more
    private class DequeIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() { return current != null; }
        public void remove() { /* not supported handler */ }
        public Item next() {
            if (!hasNext()) { throw new NoSuchElementException(); }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        int size = Integer.parseInt(args[0]);
        boolean printLog = false;
        Deque<Integer> deque = new Deque<Integer>();
        double testStartTime, testEndTime;
        double[] results = new double[7];

        if (args.length == 2) {
            printLog = Objects.equals(args[1].toUpperCase(), "TRUE");
        }

        StdOut.println("Unit Test : addFirst");
        testStartTime = timer.elapsedTime();
        for (int i = 1; i <= size; i++) {
            deque.addFirst(i);
            if (printLog) StdOut.println(printDeque(deque));
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println("Test completed in " + (results[0] = testEndTime) + "seconds or " + testEndTime/size + "seconds/entry - Deque has " + deque.size() + " Nodes");


        /*StdOut.println("\nUnit Test : iterator while full");
        testStartTime = timer.elapsedTime();
        if (printLog) StdOut.println(printDeque(deque));
        else printDeque(deque);
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println("Test completed in " + (results[1] = testEndTime) + "seconds or " + testEndTime/size + "seconds/entry");*/


        StdOut.println("\nUnit Test : removeLast");
        testStartTime = timer.elapsedTime();
        while (!deque.isEmpty()) {
            if (printLog) StdOut.println(printDeque(deque));
            deque.removeLast();
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println("Test completed in " + (results[2] = testEndTime) + "seconds or " + testEndTime/size + "seconds/entry - Deque has " + deque.size() + " Nodes");


        /*StdOut.println("\nUnit Test : iterator while empty");
        testStartTime = timer.elapsedTime();
        if (printLog) StdOut.println(printDeque(deque));
        else printDeque(deque);
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println("Test completed in " + (results[3] = testEndTime) + "seconds or " + testEndTime/size + "seconds/entry");*/


        StdOut.println("\nUnit Test : addLast");
        testStartTime = timer.elapsedTime();
        for (int i = size; i > 0; i--) {
            deque.addLast(i);
            if (printLog) StdOut.println(printDeque(deque));
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println("Test completed in " + (results[4] = testEndTime) + "seconds or " + testEndTime/size + "seconds/entry - Deque has " + deque.size() + " Nodes");


        /*StdOut.println("\nUnit Test : Nested Iterators");
        testStartTime = timer.elapsedTime();
        for (int p : deque) {
            String output = "";
            for (int q : deque) {
                if (printLog) output += p + " - " + q + "  ";
            }
            if (printLog) StdOut.println(output);
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println("Test completed in " + (results[5] = testEndTime) + "seconds or " + testEndTime/size + "seconds/entry - Deque has " + deque.size() + " Nodes");*/


        StdOut.println("\nUnit Test : removeFirst");
        testStartTime = timer.elapsedTime();
        for (int i : deque) {
            if (printLog) StdOut.println(printDeque(deque));
            deque.removeFirst();
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        StdOut.println("Test completed in " + (results[6] = testEndTime) + "seconds or " + testEndTime/size + "seconds/entry - Deque has " + deque.size() + " Nodes");

        StdOut.println("\n Results: " + results[0] + "/"/* + results[1] + "/"*/ + results[2] + "/"/* + results[3] + "/"*/ + results[4] + "/"/* + results[5] + "/"*/ + results[6] + " over " + timer.elapsedTime() + " seconds");
        StdOut.println("Result/element: " + results[0]/size + "/"/* + results[1] + "/"*/ + results[2]/size + "/"/* + results[3] + "/"*/ + results[4]/size + "/"/* + results[5] + "/"*/ + results[6]/size + " over " + timer.elapsedTime() + " seconds");
    }

    private static String printDeque(Deque<Integer> deque) {
        String output = "";
        for (int i : deque) {
            output += i + " - ";
        }
        if (output.length() > 2) output = output.substring(0, output.length() - 2);
        return output;
    }
}
