/* *****************************************************************************
 *  Name:              Alex Hackl
 *  Coursera User ID:  alexhackl@live.com
 *  Last modified:     November 13, 2023
 *
 *  Compilation: javac HelloGoodbye.java
 *  Execution: java HelloGoodbye name1 name2
 *
 *  Takes two names as command-line arguments and prints hello and goodbye
 *  messages with the names for the hello message in the same order as the
 *  command-line arguments and the names for the goodbye message in reverse
 *  order.
 *
 *  % java HelloGoodbye George Kevin
 *  Hello George and Kevin.
 *  Goodbye Kevin and George.
 *
 **************************************************************************** */

public class HelloGoodbye {
    public static void main(String[] args) {

        // prints hello and goodbye messages to the terminal window with a newline between them
        System.out.println("Hello " + args[0] + " and " + args[1] + ".");
        System.out.println("Goodbye " + args[1] + " and " + args[0] + ".");
    }
}
