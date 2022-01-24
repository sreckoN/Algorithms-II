import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.io.File;

public class SAP {

    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v, w);
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(digraph, w);
        return getLength(ancestor(v, w), bfs1, bfs2);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v, w);
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(digraph, w);
        return getAncestor(bfs1, bfs2);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v, w);
        if (hasZeroLength(v, w)) return -1;
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(digraph, w);
        return getLength(ancestor(v, w), bfs1, bfs2);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateVertices(v, w);
        if (hasZeroLength(v, w)) return -1;
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(digraph, w);
        return getAncestor(bfs1, bfs2);
    }

    // returns the length of the shortest ancestral path
    private int getLength(int ancestor, BreadthFirstDirectedPaths bfsV,
                          BreadthFirstDirectedPaths bfsW) {
        int length = -1;
        if (ancestor > length) {
            return bfsV.distTo(ancestor) + bfsW.distTo(ancestor);
        }
        return length;
    }

    // returns the ancestor
    private int getAncestor(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int ancestor = -1;
        int minLength = Integer.MAX_VALUE;
        for (int i = 0; i < digraph.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                int currentLength = bfsV.distTo(i) + bfsW.distTo(i);
                if (currentLength < minLength) {
                    minLength = currentLength;
                    ancestor = i;
                }
            }
        }
        return ancestor;
    }

    // validates one vertex
    private void validateVertex(int v) {
        if (v < 0 || v > digraph.V() - 1) {
            throw new IllegalArgumentException("vertex" + v + " is out of bounds");
        }
    }

    // validates two vertices
    private void validateVertex(int v, int w) {
        validateVertex(v);
        validateVertex(w);
    }

    // validates vertices of Iterables
    private void validateVertices(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("iterables can't be null");
        }
        for (Integer i : v) {
            if (i == null) {
                throw new IllegalArgumentException("vertices in iterables can't be null");
            }
            validateVertex(i);
        }
        for (Integer i : w) {
            if (i == null) {
                throw new IllegalArgumentException("vertices in iterables can't be null");
            }
            validateVertex(i);
        }
    }

    // checks if Iterable has zero length
    private boolean hasZeroLength(Iterable<Integer> v, Iterable<Integer> w) {
        int count1 = 0;
        for (Integer i : v) {
            count1++;
        }
        int count2 = 0;
        for (Integer i : w) {
            count2++;
        }
        if (count1 == 0 || count2 == 0) {
            return true;
        }
        return false;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(new File("digraph2.txt"));
        Digraph digraph = new Digraph(in);
        System.out.println(digraph.V());

        SAP sap = new SAP(digraph);
        System.out.println(sap.length(1, 5));
    }
}
