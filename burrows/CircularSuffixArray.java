/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class CircularSuffixArray {

    private final static int OFFSET = 12;

    private final char[] chars;
    private final int[] index;
    private final int length;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        this.chars = s.toCharArray();
        this.length = s.length();
        this.index = new int[this.length()];

        for (int i = 0; i < length; i++) {
            index[i] = i;
        }

        sort(0, length - 1, 0);
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return index[i];
    }

    private void sort(int lo, int hi, int dx) {
        if (lo + dx >= 2 * length || hi + dx >= 2 * length) return;
        if (hi <= lo + OFFSET) {
            insertion(lo, hi, dx);
            return;
        }
        int lt = lo;
        int rt = hi;
        char c = chars[(index[lo] + dx) % length];
        int i = lo + 1;
        while (i <= rt) {
            int t = chars[(index[i] + dx) % length];
            if (t < c) swap(lt++, i++);
            else if (t > c) swap(i, rt--);
            else i++;
        }
        sort(lo, lt - 1, dx);
        sort(lt, rt, dx + 1);
        sort(rt + 1, hi, dx);
    }

    private void swap(int i, int j) {
        int swap = index[i];
        index[i] = index[j];
        index[j] = swap;
    }

    private void insertion(int lo, int hi, int dx) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(index[j], index[j - 1], dx); j--)
                swap(j, j - 1);
    }

    private boolean less(int i, int j, int d) {
        if (i == j) return false;
        i = i + d;
        j = j + d;
        while (i < 2 * length && j < 2 * length) {
            if (chars[i % length] < chars[j % length]) return true;
            if (chars[i % length] > chars[j % length]) return false;
            i++;
            j++;
        }
        return false;
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray suffixArray = new CircularSuffixArray("ABRACADABRA!");
        System.out.println(suffixArray.index(2));
    }
}
