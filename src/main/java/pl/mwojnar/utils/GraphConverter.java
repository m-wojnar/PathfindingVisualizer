package pl.mwojnar.utils;

import pl.mwojnar.algorithms.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphConverter {

    private static final List<Integer> XS = List.of(1, 0, -1, 0);
    private static final List<Integer> YS = List.of(0, -1, 0, 1);

    public static List<Node> toGraph(int width, int height, List<Vector2d> walls) {
        var graph = new ArrayList<Node>(width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var neighbours = new LinkedList<Integer>();
                var position = new Vector2d(x, y);

                if (walls.stream().noneMatch(c -> c.equals(position))) {
                    for (int i = 0; i < 4; i++) {
                        if (isConnected(width, height, i, x, y, walls))
                            neighbours.add(new Vector2d(x + XS.get(i), y + YS.get(i)).toInt(width));
                    }
                }

                graph.add(new Node(position, neighbours));
            }
        }

        return graph;
    }

    private static boolean isConnected(int width, int height, int i, int x, int y, List<Vector2d> walls) {
        int newX = x + XS.get(i), newY = y + YS.get(i);

        if (newX < 0 || newX >= width || newY < 0 || newY >= height)
            return false;

        return walls.stream().noneMatch(c -> c.equals(new Vector2d(newX, newY)));
    }

    public static List<Vector2d> toPath(List<Node> graph, int end) {
        var path = new LinkedList<Vector2d>();
        int current = end;

        while (current != -1) {
            path.add(graph.get(current).getPosition());
            current = graph.get(current).getParent();
        }

        return path;
    }

}
