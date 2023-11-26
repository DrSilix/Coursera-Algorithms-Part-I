/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     11/25/2023
 *
 *  Compilation: javac-algs4 RandomizedQueue.java
 *  Execution: java-algs4 RandomizedQueue size verboseLog(Default: false) executeNonConstantOperations(Default: false)
 *  Execution: java-algs4 RandomizedQueue 10
 *  Execution: java-algs4 RandomizedQueue 10 false true
 *
 *  A queue that dequeues, iterates, and samples uniformly at random using a resizing array of generic items.
 *  Items may be enqueued to random position, and they are dequeued from the end and returned.
 *  Also supports sampling a random item without removing and iterating through in a uniquely random order.
 *  When the array is full it is doubled in size, and when it's 1/4 full it is halved in size.
 *
 *  Performance specs
 *  Non-iterator operations     Constant amortized time
 *  Iterator constructor        linear in current # of items
 *  Other iterator operations   Constant worst-case time
 *  Non-iterator memory use     Linear in current # of items
 *  Memory per iterator         Linear in current # of items
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class RandomizedQueue<Item> implements Iterable<Item> {
    /* @citation Adapted from: https://algs4.cs.princeton.edu/
     * 13stacks/ResizingArrayQueue.java.html.  Accessed: 11/21/2023 */

    private static final int INIT_CAPACITY = 8;

    private Item[] queue;
    private int last, size;

    // constructs an empty randomized queue (array-list) of size 8
    public RandomizedQueue() {
        queue = (Item[]) new Object[INIT_CAPACITY];
        last = -1;
        size = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() { return size == 0; }

    // returns the number of items on the randomized queue
    public int size() { return size; }

    // add the item, handles resizing if array is full
    // items are added to a uniformly random index and the existing item at that index is moved to the end
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Argument cannot be null.");
        // check if last is the same as the length then optimize
        if (size == queue.length) { resize(2 * queue.length); }
        if (size != 0) {
            int i = StdRandom.uniformInt(size);
            Item temp = queue[i];
            queue[i] = item;
            queue[++last] = temp;
        } else queue[++last] = item;
        size++;
    }

    // remove and return a random item, handles resizing if array becomes 1/4 full
    // simply removes the last item in the array and returns it
    public Item dequeue() {
        if (size == 0) throw new NoSuchElementException("Cannot call dequeue when RandomizedQueue is empty");
        Item item = queue[last];
        queue[last--] = null;
        size--;
        if (size > INIT_CAPACITY && size <= queue.length/4) { resize(queue.length/2); }
        return item;
    }

    // resizes the array, checks that the provided size is not below INIT_CAPACITY
    private void resize(int resize) {
        if (resize < INIT_CAPACITY) { resize = INIT_CAPACITY; }
        Item[] temp = queue;
        queue = (Item[]) new Object[resize];
        for (int i = 0; i < size; i++) {
            queue[i] = temp[i];
        }
    }

    // returns a random item (but does not remove it)
    public Item sample() {
        if (size == 0) throw new NoSuchElementException("Cannot call sample when RandomizedQueue is empty.");
        return queue[StdRandom.uniformInt(size)];
    }

    // returns an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomQIterator();
    }

    // array is copied to handle overlapping iterators
    // next works on the copy by choosing a random index and moving it to the end and pointing to a new last
    private class RandomQIterator implements Iterator<Item> {
        private Item[] tempQueue = (Item[]) new Object[size];
        private int current = last;

        private RandomQIterator() {
            for (int i = 0; i < size; i++) {
                tempQueue[i] = queue[i];
            }
        }

        public boolean hasNext() { return current >= 0 && size > 0; }
        public void remove() { throw new UnsupportedOperationException("remove operation is no longer supported."); }
        public Item next() {
            if (!hasNext()) { throw new NoSuchElementException(); }
            int i = StdRandom.uniformInt(current + 1);
            Item tempItem = tempQueue[i];
            tempQueue[i] = tempQueue[current];
            current--;
            return tempItem;
        }
    }


    // unit testing (required)
    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();
        double testStartTime, testEndTime;
        double[] results = new double[5];

        int size = Integer.parseInt(args[0]);
        boolean printLog = false, performNonConstantOperations = false;

        RandomizedQueue<Integer> rQueue = new RandomizedQueue<>();
        StringBuilder output;

        if (args.length >= 2) printLog = Objects.equals(args[1].toUpperCase(), "TRUE");
        if (args.length == 3) performNonConstantOperations = Objects.equals(args[2].toUpperCase(), "TRUE");


        StdOut.println("Unit Test : enqueue");
        testStartTime = timer.elapsedTime();
        for (int i = 1; i <= size; i++) {
            rQueue.enqueue(i);
            if (printLog) StdOut.println(printRandomizedQueue(rQueue));
        }
        testEndTime = timer.elapsedTime() - testStartTime;
        results[0] = testEndTime;
        StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime/size + " seconds/entry - RandomizedQueue has " + rQueue.size() + " Nodes");


        if (performNonConstantOperations) {
            StdOut.println("\nUnit Test : iterator while full");
            testStartTime = timer.elapsedTime();
            String iteratorResult = printRandomizedQueue(rQueue);
            if (printLog) StdOut.println(iteratorResult);
            testEndTime = timer.elapsedTime() - testStartTime;
            results[1] = testEndTime;
            StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime / size + " seconds/entry");


            StdOut.println("\nUnit Test : sample");
            testStartTime = timer.elapsedTime();
            output = new StringBuilder(size * 2);
            while (output.length() < size * 2) {
                output.append(rQueue.sample());
                output.append("-");
            }
            if (printLog) StdOut.println(output.substring(0, output.length() - 1));
            testEndTime = timer.elapsedTime() - testStartTime;
            results[2] = testEndTime;
            StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime / size + " seconds/entry");
        }


        StdOut.println("\nUnit Test : dequeue");
        testStartTime = timer.elapsedTime();
        output = new StringBuilder(size * 2);
        for (int i = 1; i <= size; i++) {
            if (printLog) {
                StdOut.println(printRandomizedQueue(rQueue));
                output.append(String.valueOf(rQueue.dequeue()));
                if (i != size) output.append("-");
            } else {
                rQueue.dequeue();
            }
        }
        if (printLog) StdOut.println(output.toString());
        testEndTime = timer.elapsedTime() - testStartTime;
        results[3] = testEndTime;
        StdOut.println("Test completed in " + testEndTime + " seconds or " + testEndTime/size + " seconds/entry - RandomizedQueue has " + rQueue.size() + " Nodes");


        if (performNonConstantOperations) {
            StdOut.println("\nUnit Test : calling random methods");
            testStartTime = timer.elapsedTime();
            int numMethodCalls = 0;
            Iterator<Integer> iterator = rQueue.iterator();
            for (int i = 1; i <= size * 10; i++) {
                switch (StdRandom.uniformInt(6)) {
                    case 0:
                        rQueue.enqueue(StdRandom.uniformInt(1000000) - 500000);
                        iterator = rQueue.iterator();
                        break;
                    case 1:
                        if (!rQueue.isEmpty()) rQueue.dequeue();
                        break;
                    case 2:
                        // second iterator
                        for (int j : rQueue) {
                            assert true;
                        }
                        break;
                    case 3:
                        if (!rQueue.isEmpty()) rQueue.sample();
                        break;
                    case 4:
                        rQueue.size();
                        break;
                    case 5:
                        if (iterator.hasNext()) iterator.next();
                }
            }
            testEndTime = timer.elapsedTime() - testStartTime;
            results[4] = testEndTime;
            StdOut.println("Test completed in " + testEndTime);
        }

        StdOut.println("\nResults: " + results[0] + "/" + results[1] + "/" + results[2] + "/" + results[3] + "/" + results[4] + " over " + timer.elapsedTime() + " seconds");
        StdOut.println("Result/element: " + results[0]/size + "/" + results[1]/size + "/" + results[2]/size + "/" + results[3]/size);
    }


    private static String printRandomizedQueue(RandomizedQueue<Integer> queue) {
        StringBuilder output = new StringBuilder(queue.size() * 2);
        for (int i : queue) {
            output.append(i);
            output.append("-");
        }
        return output.toString();
    }
}
