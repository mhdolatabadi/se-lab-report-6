package graph;

import lombok.Getter;
import org.javatuples.Pair;

@Getter
public class Edge {
    private final Pair<Node, Node> nodes;
    private final boolean directed;
    private final int weight;

    private Edge(Node a, Node b, boolean directed, int weight) {
        nodes = new Pair<>(a, b);
        this.directed = directed;
        this.weight = weight;
    }

    public static void createEdge(Node a, Node b, boolean directed, int weight) {
        Edge newEdge = new Edge(a, b, directed, weight);
        a.getEdges().add(newEdge);
        b.getEdges().add(newEdge);
    }

}
