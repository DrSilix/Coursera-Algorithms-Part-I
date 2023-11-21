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
 **************************************************************************** */

import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {
    private int size;
    private Item first, last;

    // construct an empty deque
    public Deque() {
        size = 0;
        first = null;
        last = null;
    }

    // is the deque empty?
    public boolean isEmpty() {
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
    }

    // add the item to the back
    public void addLast(Item item) {
    }

    // remove and return the item from the front
    public Item removeFirst() {
    }

    // remove and return the item from the back
    public Item removeLast() {
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // need to understand this more
    private class DequeIterator implements Iterator<Item> {
        public boolean hasNext() {
            return false;
        }

        public Item next() {
            return null;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
    }

}
