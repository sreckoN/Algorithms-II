import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoggleSolver {

    private final TST<Integer> dictionary;
    private BoggleBoard board;
    private Set<String> words;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new TST<>();
        int counter = 0;
        for (int i = 0; i < dictionary.length; i++) {
            this.dictionary.put(dictionary[i], counter++);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        this.words = new HashSet<>();
        this.board = board;
        int rows = board.rows();
        int cols = board.cols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                bfs(i, j, new boolean[rows][cols], "");
            }
        }
        return words;
    }

    private void bfs(int row, int col, boolean[][] visited, String p) {
        if (visited[row][col]) return;
        char letter = board.getLetter(row, col);

        if (letter == 'Q') {
            p += "QU";
        }
        else {
            p += letter;
        }

        Queue<String> wordsWithPrefix = (Queue<String>) dictionary.keysWithPrefix(p);
        if (wordsWithPrefix.size() == 0) return;

        if (p.length() > 2 && dictionary.contains(p)) {
            words.add(p);
        }

        visited[row][col] = true;

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue;
                int newRow = row + i;
                int newCol = col + j;
                if (validateRowCol(newRow, newCol)) {
                    bfs(newRow, newCol, visited, p);
                }
            }
        }

        visited[row][col] = false;
    }

    private boolean validateRowCol(int row, int col) {
        return row >= 0 && row < board.rows() && col >= 0 && col < board.cols();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dictionary.contains(word)) {
            switch (word.length()) {
                case 0:
                case 1:
                case 2:
                    return 0;
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return 11;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        BoggleBoard board = new BoggleBoard("board-q.txt");
        In in = new In("dictionary-algs4.txt");
        List<String> strList = new ArrayList<>();
        while (in.hasNextLine()) {
            strList.add(in.readLine());
        }
        String[] dict = new String[strList.size()];
        int counter = 0;
        for (String s : strList) {
            dict[counter] = strList.get(counter);
            counter++;
        }
        BoggleSolver solver = new BoggleSolver(dict);
        System.out.println(solver.dictionary.size());
        System.out.println(solver.getAllValidWords(board));
        System.out.println(solver.words.size());
    }
}
