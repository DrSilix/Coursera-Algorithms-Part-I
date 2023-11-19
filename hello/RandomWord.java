/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     November 13, 2023
 *
 *  Compilation: javac-algs4 RandomWord.java
 *  Execution: java-algs4 RandomWord
 *  Execution: java-algs4 RandomWord < file.txt
 *
 *  Takes standard input of one or more words and uses Knuth's Shuffle to evaluate
 *  each word when read. As each word is read it has a probability of 1/i (where
 *  i is the current number of read words starting at 1) of that word becoming
 *  the "champion". The winning champion is then printed after all words are read
 *
 *  Word# | Probability
 *  1       1/1
 *  2       1/2
 *  3       1/3
 *  n       1/n
 *
 *  % java-algs4 RandomWord
 *  rock paper scissors
 *  scissors
 *
 *  % java-algs4 RandomWord
 *  rock paper scissors
 *  paper
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {

        // initialize champion string and count integer at this scope
        String champion = "";
        int i = 0;

        // Loop through standard input while it is not empty evaluating each word
        // as a candidate to be the champion. This will also query for StdIn if not provided
        while (!StdIn.isEmpty()) {
            i++;
            String candidate = StdIn.readString();

            // select the nth word with the probability 1/i for it to be the champion
            if (StdRandom.bernoulli((double) 1 / i)) {
                champion = candidate;
            }
        }

        // print the champion string to the terminal
        StdOut.println(champion);
    }
}
