/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(input);
        char[] chars = new char[input.length()];
        for (int i = 0; i < csa.length(); i++) {
            int index = csa.index(i);
            if (index > 0) {
                chars[i] = input.charAt(index - 1);
            }
            else {
                chars[i] = input.charAt(input.length() - 1);
            }
            if (index == 0) {
                BinaryStdOut.write(i);
            }
        }
        for (char c : chars) {
            BinaryStdOut.write(c, 8);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int number = BinaryStdIn.readInt();
        String input = BinaryStdIn.readString();
        int length = input.length();
        int[] count = new int[R + 1];
        for (int i = 0; i < length; i++) {
            count[input.charAt(i) + 1]++;
        }
        for (int i = 0; i < R; i++) {
            count[i + 1] += count[i];
        }

        int[] next = new int[length];
        for (int i = 0; i < length; ++i) {
            next[count[input.charAt(i)]++] = i;
        }
        for (int i = 0; i < length; ++i) {
            number = next[number];
            BinaryStdOut.write(input.charAt(number));
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
