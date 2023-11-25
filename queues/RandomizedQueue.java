/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 *
 *  requirements
 *  Non-iterator operations     Constant amortized time
 *  Iterator constructor        linear in current # of items
 *  Other iterator operations   Constant worst-case time
 *  Non-iterator memory use     Linear in current # of items
 *  Memory per iterator         Linear in current # of items
 *
 *  pretty sure have to use array-list in order to randomly access the array, linked list would
 *  only allow you to go in order
 *
 *  actually this will be a hybrid array linked list. as items are enqueued they are added to the end
 *  as they are dequeued they are removed at RANDOM. (a bonus the first will always be 0 unless it's picked?)
 *  this will leave not only the end of the array empty but holes in it. I'll loop through the array and optimize
 *  it when the last entry reaches the end (possibly resize at the same time to be efficient). When the
 *  size reaches 1/4 I'll reduce and optimize. Anyway the linked list part of this is that each array element
 *  is either null or has a node with has a link to the next element. This will allow efficient
 *  iteration and resizing. When choosing a random number it will be based on a seed and repeat until you
 *  get an element not null between first and last.
 *
 *  okay.. two solutions
 *  1. hybrid linked list and array
 *      -for random access it just uses a random index
 *      -keep track of blank spaces with a bidirectional linked list layout
 *      -cons
 *          -need to store a whole wrapper object instead of just the incoming object
 *              -aside from teh object overhead it would only also be storing two integers
 *          -would need to optimize the array
 *  2. array
 *      -uses shuffle for randomization instead of grabbing a random index grab last
 *      -cons
 *          -to output a random item you would need to shuffle the array (even if it was shuffled, if a new item was added
 *              -unless if you add items in random positions (swap new item with random index)
 *          -iterator would need to copy the array and shuffle to remain unique per iterator yet consistent
 *
 * ... would you need to shuffle the array?? just always add randomly
 * remove randomly would just remove last
 * iterator could possibly be tricked, pick a random index and start there??? (multiple iterators would overlap eventually)
 *      -what about starting at random index, store new first/last, flip coin on whether to take the next value from first or last (could they wrap??)
 *
 * make a copy of the array, store last
 * pick a random index, swap with last
 * output and decrement last
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int INIT_CAPACITY = 8;

    private Item[] queue;
    private int last, size;

    // construct an empty randomized queue
    public RandomizedQueue() {
        queue = (Item[]) new Object[INIT_CAPACITY];
        last = -1;
        size = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() { return size == 0; }

    // return the number of items on the randomized queue
    public int size() { return size; }

    // add the item
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

    // remove and return a random item
    public Item dequeue() {
        if (size == 0) throw new NoSuchElementException("Cannot call dequeue when RandomizedQueue is empty");
        if (size > INIT_CAPACITY && size <= queue.length/4) { resize(queue.length/2); }
        Item item = queue[last];
        queue[last--] = null;
        size--;
        return item;
    }

    private void resize(int resize) {
        if (resize < INIT_CAPACITY) { resize = INIT_CAPACITY; }
        Item[] temp = queue;
        queue = (Item[]) new Object[resize];
        for (int i = 0; i < size; i++) {
            queue[i] = temp[i];
        }
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (size == 0) throw new NoSuchElementException("Cannot call sample when RandomizedQueue is empty.");
        return queue[StdRandom.uniformInt(size)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomQIterator();
    }

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
        int size = Integer.parseInt(args[0]);
        boolean printLog = false, performNonConstantOperations = false;
        RandomizedQueue<Integer> rQueue = new RandomizedQueue<>();
        double testStartTime, testEndTime;
        double[] results = new double[5];
        StringBuilder output;

        if (args.length >= 2) {
            printLog = Objects.equals(args[1].toUpperCase(), "TRUE");
        }
        if (args.length == 3) {
            performNonConstantOperations = Objects.equals(args[2].toUpperCase(), "TRUE");
        }


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
