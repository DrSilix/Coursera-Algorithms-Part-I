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
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int INIT_CAPACITY = 8;

    private Item[] queue;
    private int size;
    private int first, last;

    // construct an empty randomized queue
    public RandomizedQueue() {
        size = 0;
        queue = (Item[]) new Object[INIT_CAPACITY];
        first = 0;
        last = 0;
    }

    // need to store a whole class object instead of just the item unfortunately
    // this will be a hybrid array linked list
    private class Node {
        Item item;
        int next;
    }

    // is the randomized queue empty?
    public boolean isEmpty() { return size == 0; }

    // return the number of items on the randomized queue
    public int size() { return size; }

    // add the item
    public void enqueue(Item item) {
        // check if last is the same as the length then optimize
        if (size == queue.length) { resize(2 * queue.length); }
        queue[last++] = item;
        if (last == queue.length) { last = 0; }
        size++;
    }

    // remove and return a random item
    public Item dequeue() {
        int i = StdRandom.uniformInt(first, last+1);
        Item item = queue[i];
        queue[i] = null;

    }

    private void resize(int size){

    }

    // return a random item (but do not remove it)
    public Item sample() {}

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {}

    // unit testing (required)
    public static void main(String[] args) {}

}
