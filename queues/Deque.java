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
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
        int startingSize = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        for (int i = 0; i < trials; i++) {
            int size = (int) (startingSize * Math.pow(2, i));
            StdOut.println(size + ", " + trial(size));
        }
    }

    private static double trial(int size) {
        Stopwatch timer = new Stopwatch();


        Deque<Integer> deque = new Deque<Integer>();
        //StdOut.println("Unit Test : addFirst");
        for (int i = 0; i < size; i++) {
            deque.addFirst(i);
        }
        //StdOut.println("Deque has " + deque.size() + " Nodes");
        //StdOut.println("\nUnit Test : iterator while full");
        String output = "";
        for (int i : deque) {
            output += i + " - ";
        }
        //StdOut.println(output);
        //StdOut.println("\nUnit Test : removeLast");
        while (!deque.isEmpty()) {
            //StdOut.println(
            deque.removeLast();
        }
        //StdOut.println("Deque has " + deque.size() + " Nodes");
        //StdOut.println("\nUnit Test : iterator while empty");
        output = "";
        for (int i : deque) {
            output += i + " - ";
        }

        //StdOut.println("\nUnit Test : addLast");
        for (int i = size; i > 0; i--) {
            deque.addLast(i);
        }
        //StdOut.println("Deque has " + deque.size() + " Nodes");
        //StdOut.println("\nUnit Test : addLast");
        for (int i : deque) {
            //StdOut.println(
            deque.removeFirst();
        }
        //StdOut.println("Deque has " + deque.size() + " Nodes");

        return timer.elapsedTime();
    }
}
