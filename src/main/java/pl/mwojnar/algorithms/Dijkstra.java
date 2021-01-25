package pl.mwojnar.algorithms;

import java.util.List;

public class Dijkstra extends AStar {
    public Dijkstra(int start, int end, List<Node> graph, int width) {
        super(start, end, graph, width);

        graph.get(start).setDistance(0);
        aStar();
    }

    @Override
    protected int heuristics(int u, int v) {
        return 0;
    }
}
