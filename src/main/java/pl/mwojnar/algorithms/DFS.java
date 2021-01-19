package pl.mwojnar.algorithms;

import java.util.List;

public class DFS extends PathfindingAlgorithm {
    public DFS(int start, List<Node> graph) {
        super(start, graph);
        dfs(start, visitOrder);
    }

    private void dfs(int v, List<Node> visitOrder) {
        visitOrder.add(graph.get(v));
        graph.get(v).setVisited(true);

        for (int n : graph.get(v).getNeighbours()) {
            if (!graph.get(n).isVisited()) {
                graph.get(n).setVisited(true);
                graph.get(n).setParent(v);
                dfs(n, visitOrder);
            }
        }
    }

    @Override
    public List<Node> getVisitOrder() {
        return visitOrder;
    }
}
