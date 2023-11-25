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

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int INIT_CAPACITY = 8;

    private Item[] queue;
    private int last;

    // construct an empty randomized queue
    public RandomizedQueue() {
        queue = (Item[]) new Object[INIT_CAPACITY];
        last = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() { return last == 0; }

    // return the number of items on the randomized queue
    public int size() { return last + 1; }

    // add the item
    public void enqueue(Item item) {
        // check if last is the same as the length then optimize
        if (size() == queue.length) { resize(2 * queue.length); }
        int i = StdRandom.uniformInt(size());
        Item temp = queue[i];
        queue[i] = item;
        queue[last++] = temp;
        if (last == queue.length) { last = 0; }
    }

    // remove and return a random item
    public Item dequeue() {
        if (size() <= queue.length/4) { resize(queue.length/2); }
        Item item = queue[last];
        queue[last--] = null;
        return item;
    }

    private void resize(int resize) {
        if (resize < INIT_CAPACITY) { resize = INIT_CAPACITY; }
        Item[] temp = queue;
        queue = (Item[]) new Object[resize];
        for (int i = 0; i < size(); i++) {
            queue[i] = temp[i];
        }
    }

    // return a random item (but do not remove it)
    public Item sample() { return queue[StdRandom.uniformInt(size())]; }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomQIterator();
    }

    private class RandomQIterator implements Iterator<Item> {
        private Item[] tempQueue = (Item[]) new Object[size()];
        private int current = last;

        private RandomQIterator() {
            for (int i = 0; i < size(); i++) {
                tempQueue[i] = queue[i];
            }
        }


        public boolean hasNext() { return current == 0; }
        public void remove() { /* not supported handler */ }
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
    public static void main(String[] args) {}

}
