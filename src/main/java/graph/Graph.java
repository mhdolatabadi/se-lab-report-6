package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import lombok.Getter;
import org.javatuples.Pair;

@Getter
public record Graph(ArrayList<Node> graph) {

    public void resetVisits() {
        for (Node v : this.graph()) {
            v.setVisited(false);
        }
    }

    public void bfs(Node s) {
        this.resetVisits();

        Queue<Pair<Node, Integer>> nodes = new LinkedList<>();

        nodes.add(new Pair<>(s, 0));
        while (!nodes.isEmpty()) {
            Pair<Node, Integer> front = nodes.poll();
            Node frontNode = front.getValue0();
            if (!frontNode.isVisited()) {
                frontNode.setVisited(true);
                int distance = front.getValue1();
                frontNode.setDistance(distance);
                List<Pair<Node, Integer>> availableNeighbors = frontNode.getAvailableNeighbors()
                        .stream()
                        .map(neighbor -> new Pair<>(neighbor, distance + 1))
                        .toList();
                nodes.addAll(availableNeighbors);

            }
        }
    }

    public void dijkstra(Node s) {
        this.resetVisits();

        PriorityQueue<Pair<Integer, Node>> nodes = new PriorityQueue<>();

        nodes.add(new Pair<>(0, s));
        while (!nodes.isEmpty()) {
            Pair<Integer, Node> front = nodes.poll();
            Node frontNode = front.getValue1();
            if (!frontNode.isVisited()) {
                frontNode.setVisited(true);
                int distance = front.getValue0();
                frontNode.setDistance(distance);
                List<Pair<Integer, Node>> availableNeighbors = frontNode.getAvailableWeightedNeighbors()
                        .stream()
                        .map(neighbor -> new Pair<>(neighbor.getValue1() + distance,
                                neighbor.getValue0()))
                        .toList();
                nodes.addAll(availableNeighbors);
            }
        }
    }

}
