/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

public class RandomTest {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        String[] perms = nPermutations(n);
        long[] results = new long[perms.length];


        for (int i = 0; i < trials; i++) {
            String trial = trial(n);
            for (int j = 0; j < perms.length; j++) {
                if (trial.equals(perms[j])) {
                    results[j]++;
                    break;
                }
            }
        }


        double expected = (double) trials /perms.length;
        double[] expectedArray = new double[perms.length];
        double gStat = 0, temp;
        for (int k = 0; k < perms.length; k++) {
            expectedArray[k] = expected;
            temp = results[k] * Math.log((double) results[k] /expected);
            StdOut.println(perms[k] + " - " + results[k] + " - " + expected + " - " + 2 * temp);
            gStat += 2 * temp;
        }
        StdOut.println("G-Statistic = " + gStat);
        //ChiSquareTest chi = new ChiSquareTest();
        //StdOut.println("p-value = " + chi.chiSquareTest(expectedArray, results));

    }

    private static String trial(int n) {
        RandomizedQueue<Character> q = new RandomizedQueue<Character>();

        for (int i = 0; i < n; i++) {
            q.enqueue((char) ('A' + i));
        }
        StringBuilder output = new StringBuilder(n);
        while (!q.isEmpty()) {
            output.append(q.dequeue());
        }
        return output.toString();
    }

    private static String[] nPermutations(int n) {
        if (n == 2) {
            return new String[] {"AB",
                                 "BA"};
        }
        if (n == 3) {
            return new String[] {"ABC",
                                 "ACB",
                                 "BAC",
                                 "BCA",
                                 "CAB",
                                 "CBA"};
        }
        if (n == 4) {
            return new String[] {"ABCD",
                                 "ABDC",
                                 "ACBD",
                                 "ACDB",
                                 "ADBC",
                                 "ADCB",
                                 "BACD",
                                 "BADC",
                                 "BCAD",
                                 "BCDA",
                                 "BDAC",
                                 "BDCA",
                                 "CABD",
                                 "CADB",
                                 "CBAD",
                                 "CBDA",
                                 "CDAB",
                                 "CDBA",
                                 "DABC",
                                 "DACB",
                                 "DBAC",
                                 "DBCA",
                                 "DCAB",
                                 "DCBA"};
        }
        return new String[] {""};
    }
}
