/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     11/25/2023
 *
 *  Compilation: javac-algs4 Permutation.java
 *  Execution: java-algs4 Permutation k < standardInput
 *  Execution: java-algs4 Permutation 3 < distinct.txt
 *
 *  Takes an argument k and standard input with a number of strings size n.
 *  Prints out k or n random strings, whichever comes first
 *
 *  % java-algs4 Permutation 3 < distinct.txt
 *  C
 *  G
 *  A
 *
 *  % java-algs4 Permutation 3 < tinyTale.txt
 *  worst
 *  the
 *  it
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {

    // reads in a number of strings n from standard input and puts them in a RandomizedQueue
    // prints out argument k or n (whichever is smaller) number of strings randomly
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue<String>();

        while (!StdIn.isEmpty()) {
            queue.enqueue(StdIn.readString());
        }

        while (!queue.isEmpty() && k > 0) {
            StdOut.println(queue.dequeue());
            k--;
        }
    }
}
