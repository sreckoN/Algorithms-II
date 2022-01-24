/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
import java.util.List;

public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] chars = new int[R];
        int[] positions = new int[R];
        for (int i = 0; i < R; i++) {
            chars[i] = i;
            positions[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write((char) positions[c]);

            for (int i = positions[c]; i > 0; i--) {
                positions[chars[i - 1]]++;
                chars[i] = chars[i - 1];
            }
            chars[0] = c;
            positions[c] = 0;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        List<Character> chars = new ArrayList<>(R);
        for (int i = 0; i < R; i++) {
            chars.add((char) (255 - i));
        }
        while (!BinaryStdIn.isEmpty()) {
            int in = BinaryStdIn.readChar();
            char c = chars.remove(255 - in);
            BinaryStdOut.write(c);
            chars.add(c);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        if (args[0].equals("+")) {
            decode();
        }
    }
}
