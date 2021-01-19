package pl.mwojnar.algorithms;

import java.util.LinkedList;
import java.util.List;

public abstract class PathfindingAlgorithm {

    protected final List<Node> visitOrder;
    protected final List<Node> graph;
    protected final int start;

    public PathfindingAlgorithm(int start, List<Node> graph) {
        if (start < 0 || start >= graph.size())
            throw new IllegalArgumentException("Invalid start index");

        if (graph.isEmpty())
            throw new IllegalArgumentException("Empty graph");

        this.start = start;
        this.graph = graph;
        this.visitOrder = new LinkedList<>();
    }

    public abstract List<Node> getVisitOrder();
}
