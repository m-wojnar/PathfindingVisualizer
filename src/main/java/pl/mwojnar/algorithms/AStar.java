package pl.mwojnar.algorithms;

import javafx.util.Pair;
import pl.mwojnar.utils.Vector2d;

import java.util.*;

public class AStar extends PathfindingAlgorithm {

    private final int width;
    private final int end;

    public AStar(int start, int end, List<Node> graph, int width) {
        super(start, graph);
        this.width = width;
        this.end = end;

        graph.get(start).setDistance(0);
        aStar();
    }

    private int heuristics(int u, int v) {
        return Vector2d.fromInt(u, width).manhattanDistance(Vector2d.fromInt(v, width));
    }

    private void aStar() {
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
                    queue.add(new Pair<>(neighbour.getDistance() + heuristics(n, end), n));
                }
            }
        }
    }

    @Override
    public List<Node> getVisitOrder() {
        return visitOrder;
    }
}
