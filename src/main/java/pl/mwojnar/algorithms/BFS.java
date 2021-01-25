package pl.mwojnar.algorithms;

import java.util.LinkedList;
import java.util.List;

public class BFS extends PathfindingAlgorithm {
    public BFS(int start, List<Node> graph) {
        super(start, graph);
        bfs();
    }

    private void bfs() {
        var queue = new LinkedList<Integer>();
        queue.add(start);
        graph.get(start).setVisited(true);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            visitOrder.add(graph.get(v));

            for (int n : graph.get(v).getNeighbours()) {
                if (!graph.get(n).isVisited()) {
                    graph.get(n).setVisited(true);
                    graph.get(n).setParent(v);
                    queue.add(n);
                }
            }
        }
    }
}
