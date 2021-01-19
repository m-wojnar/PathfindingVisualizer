package pl.mwojnar.algorithms;

import pl.mwojnar.utils.Vector2d;

import java.util.List;

public class Node {

    private final Vector2d position;
    private final List<Integer> neighbours;
    private int distance;
    private int parent;
    private boolean visited;

    public Node(Vector2d position, List<Integer> neighbours) {
        this.position = position;
        this.neighbours = neighbours;
        this.parent = -1;
        this.distance = Integer.MAX_VALUE;
        this.visited = false;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Vector2d getPosition() {
        return position;
    }

    public List<Integer> getNeighbours() {
        return neighbours;
    }

    public int getDistance() {
        return distance;
    }

    public int getParent() {
        return parent;
    }

    public boolean isVisited() {
        return visited;
    }
}
