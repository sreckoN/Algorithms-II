import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {

    private final Map<String, List<Integer>> nouns;
    private final List<String> synsets;
    private int size;
    private final Digraph wordNet;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("filenames cannot be null");
        this.nouns = new HashMap<>();
        this.synsets = new ArrayList<>();
        this.size = 0;
        readSynsets(synsets);
        this.wordNet = new Digraph(size);
        readHypernyms(hypernyms);
        validateGraph();
        this.sap = new SAP(wordNet);
    }

    // validates graph
    private void validateGraph() {
        Topological ts = new Topological(wordNet);
        if (!ts.hasOrder()) {
            throw new IllegalArgumentException("not dag");
        }
        int roots = 0;
        for (int i = 0; i < wordNet.V(); i++) {
            if (wordNet.outdegree(i) == 0) roots++;
            if (roots > 1) throw new IllegalArgumentException("not rooted");
        }
    }

    // parses synset file
    private void readSynsets(String synsetFile) {
        In in = new In(synsetFile);
        if (in.isEmpty()) throw new IllegalArgumentException("file is empty");
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] lineParts = line.split(",");
            this.synsets.add(lineParts[1]);
            String[] nounsArray = lineParts[1].split(" ");
            for (String noun : nounsArray) {
                List<Integer> indices = this.nouns.get(noun);
                if (indices == null) {
                    indices = new ArrayList<>();
                    this.nouns.put(noun, indices);
                }
                indices.add(size);
            }
            size++;
        }
    }

    // parses hypernyms file
    private void readHypernyms(String hypernymFile) {
        In in = new In(hypernymFile);
        if (in.isEmpty()) throw new IllegalArgumentException("file is empty");
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] lineParts = line.split(",");
            int hyponym = Integer.parseInt(lineParts[0]);
            for (int i = 1; i < lineParts.length; i++) {
                int hypernym = Integer.parseInt(lineParts[i]);
                wordNet.addEdge(hyponym, hypernym);
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("word cannot be null");
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("not sysnet nouns");
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("not sysnet nouns");
        return synsets.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");

        System.out.println(wordNet.size);
        System.out.println(wordNet.wordNet.V());
        System.out.println(wordNet.wordNet.E());
        System.out.println(wordNet.nouns.size());
    }
}
