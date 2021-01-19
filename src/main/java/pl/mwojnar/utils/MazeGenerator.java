package pl.mwojnar.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MazeGenerator {

    private static final Random random = new Random();

    public static List<Vector2d> generate(int width, int height) {
        var path = new LinkedList<Vector2d>();
        horizontalDivision(path, 0, width - 1, 0, height - 1, width, height);
        return path;
    }

    private static void horizontalDivision(List<Vector2d> path, int xStart, int xEnd, int yStart, int yEnd, int width, int height) {
        if (xStart > xEnd || yStart > yEnd)
            return;

        var rows = new LinkedList<Integer>();
        for (int i = yStart; i <= yEnd; i += 2)
            rows.add(i);

        var cols = new LinkedList<Integer>();
        for (int i = xStart - 1; i <= xEnd + 1; i += 2)
            cols.add(i);

        int randRow = rows.get(random.nextInt(rows.size()));
        int randCol = cols.get(random.nextInt(cols.size()));

        for (int i = xStart - 1; i <= xEnd + 1; i++) {
            if (i >= 0 && i < width && i != randCol)
                path.add(new Vector2d(i, randRow));
        }

        if (randRow - 1 - yStart > xEnd - xStart)
            horizontalDivision(path, xStart, xEnd, yStart, randRow - 2, width, height);
        else
            verticalDivision(path, xStart, xEnd, yStart, randRow - 2, width, height);

        if (yEnd - 1 - randRow > xEnd - xStart)
            horizontalDivision(path, xStart, xEnd, randRow + 2, yEnd, width, height);
        else
            verticalDivision(path, xStart, xEnd, randRow + 2, yEnd, width, height);
    }

    private static void verticalDivision(List<Vector2d> path, int xStart, int xEnd, int yStart, int yEnd, int width, int height) {
        if (xStart > xEnd || yStart > yEnd)
            return;

        var rows = new LinkedList<Integer>();
        for (int i = yStart - 1; i <= yEnd + 1; i += 2)
            rows.add(i);

        var cols = new LinkedList<Integer>();
        for (int i = xStart; i <= xEnd; i += 2)
            cols.add(i);

        int randRow = rows.get(random.nextInt(rows.size()));
        int randCol = cols.get(random.nextInt(cols.size()));

        for (int i = yStart - 1; i <= yEnd + 1; i++) {
            if (i >= 0 && i < height && i != randRow)
                path.add(new Vector2d(randCol, i));
        }

        if (yEnd - yStart > randCol - 2 - xStart)
            horizontalDivision(path, xStart, randCol - 2, yStart, yEnd , width, height);
        else
            verticalDivision(path, xStart, randCol - 2, yStart, yEnd, width, height);

        if (yEnd - yStart > xEnd - 2 - randCol)
            horizontalDivision(path, randCol + 2, xEnd, yStart, yEnd, width, height);
        else
            verticalDivision(path, randCol + 2, xEnd, yStart, yEnd, width, height);
    }

}
