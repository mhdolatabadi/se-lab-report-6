package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Setter;
import org.javatuples.Pair;

@Data
public class Node implements Comparable<Node> {
    private final ArrayList<Edge> edges;
    @Setter
    private boolean visited;
    @Setter
    private int distance;

    public Node() {
        edges = new ArrayList<>();
    }

    public List<Pair<Node, Integer>> getAvailableWeightedNeighbors() {
        return getEdges()
                .stream()
                .filter(edge -> !edge.isDirected() || edge.getNodes().getValue0() == this)
                .map(edge -> new Pair<>(
                        (edge.getNodes().getValue0().equals(this)) ? edge.getNodes().getValue1()
                                : edge.getNodes().getValue0(), edge.getWeight()))
                .filter(pair -> !pair.getValue0().isVisited())
                .toList();
    }

    public ArrayList<Node> getAvailableNeighbors() {
        return getAvailableWeightedNeighbors()
                .stream()
                .map(Pair::getValue0)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public int compareTo(Node o) {
        return o.hashCode() - hashCode();
    }
}
