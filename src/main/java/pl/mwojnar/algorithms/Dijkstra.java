package pl.mwojnar.algorithms;

import javafx.util.Pair;

import java.util.*;

public class Dijkstra extends PathfindingAlgorithm {
    public Dijkstra(int start, List<Node> graph) {
        super(start, graph);

        graph.get(start).setDistance(0);
        dijkstra();
    }

    private void dijkstra() {
        var queue = new PriorityQueue<Pair<Integer, Integer>>(Comparator.comparing(Pair::getKey));
        queue.add(new Pair<>(0, start));

        while (!queue.isEmpty()) {
            var temp = queue.poll();
            int v = temp.getValue();
            visitOrder.add(graph.get(v));

            for (int n : graph.get(v).getNeighbours()) {
                var neighbour = graph.get(n);

                if (neighbour.getDistance() > graph.get(v).getDistance() + 1) {
                    neighbour.setVisited(true);
                    neighbour.setParent(v);
                    neighbour.setDistance(graph.get(v).getDistance() + 1);
                    queue.add(new Pair<>(neighbour.getDistance(), n));
                }
            }
        }
    }

    @Override
    public List<Node> getVisitOrder() {
        return visitOrder;
    }
}
