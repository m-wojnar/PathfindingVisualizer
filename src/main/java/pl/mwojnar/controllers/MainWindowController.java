package pl.mwojnar.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import pl.mwojnar.algorithms.*;
import pl.mwojnar.utils.GraphConverter;
import pl.mwojnar.utils.MazeGenerator;
import pl.mwojnar.utils.Vector2d;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainWindowController  {

    private static final double BORDER_THICKNESS = 0.3;
    private static final double RANDOM_FILL_FACTOR = 0.35;
    private static final int DEFAULT_CANVAS_SIZE = 800;
    private static final int TIME_STEP = 10;

    private static final Paint BACKGROUND_COLOR = Paint.valueOf("#496878");
    private static final Paint CELL_COLOR = Paint.valueOf("#333333");
    private static final Paint WALL_COLOR = Paint.valueOf("#777777");
    private static final Paint VISITED_COLOR = Paint.valueOf("#FFFE6A");
    private static final Paint START_COLOR = Paint.valueOf("#0695AA");
    private static final Paint END_COLOR = Paint.valueOf("#8B39C6");
    private static final Paint PATH_COLOR = Paint.valueOf("#00E66F");

    private Vector2d lowerLeft;
    private Vector2d upperRight;
    private int width;
    private int height;

    private PathfindingAlgorithm algorithm;
    private AtomicInteger currentIndex;
    private Timer timer;

    private List<Node> graph;
    private List<Vector2d> walls;
    private Vector2d start;
    private Vector2d end;
    private boolean block;
    private boolean paused;

    @FXML TextField widthText;
    @FXML TextField heightText;
    @FXML Canvas canvas;
    @FXML Button startButton;
    @FXML Button stopButton;
    @FXML ChoiceBox<String> algorithmText;

    @FXML
    private void initialize() {
        this.currentIndex = new AtomicInteger();
        this.timer = new Timer();
        this.graph = new LinkedList<>();
        this.block = false;
        this.paused = false;
        setupSize();
        setupCanvas();
    }

    private void setupCanvas() {
        setupCanvas(new LinkedList<>());
    }

    private void setupCanvas(List<Vector2d> walls) {
        timer.cancel();
        timer = new Timer();
        block = false;

        canvas.setWidth(DEFAULT_CANVAS_SIZE);
        canvas.setHeight(DEFAULT_CANVAS_SIZE);

        if (width > height)
            canvas.setHeight((canvas.getHeight() * height) / width);
        else if (width < height)
            canvas.setWidth((canvas.getWidth() * width) / height);

        var graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(BACKGROUND_COLOR);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++)
                drawRectangle(new Vector2d(x, y), CELL_COLOR);
        }

        this.walls = walls;
        for (var wall : walls)
            drawRectangle(wall, WALL_COLOR);
    }

    private boolean setupSize() {
        try {
            width = Integer.parseInt(widthText.getText());
            height = Integer.parseInt(heightText.getText());
        } catch (NumberFormatException e) {
            showAlert("Provided values must be positive integers");
            return false;
        }

        if (width < 2 || height < 2) {
            showAlert("Provided values must be higher than 1");
            return false;
        }

        this.walls = new LinkedList<>();

        lowerLeft = new Vector2d(0, 0);
        upperRight = new Vector2d(width - 1, height - 1);

        start = new Vector2d(width / 4, height / 2);
        end = new Vector2d(3 * width / 4, height / 2);
        return true;
    }

    private void drawCircle(Vector2d position, Paint fill) {
        if (isOutside(position))
            throw new IllegalArgumentException("Painting beyond canvas");

        canvas.getGraphicsContext2D().setFill(fill);
        canvas.getGraphicsContext2D().fillOval(
                (canvas.getWidth() * position.getX()) / width + BORDER_THICKNESS,
                (canvas.getHeight() * position.getY()) / height + BORDER_THICKNESS,
                (canvas.getWidth()) / width - 2 * BORDER_THICKNESS,
                (canvas.getHeight()) / height - 2 * BORDER_THICKNESS
        );
    }

    private void drawRectangle(Vector2d position, Paint fill) {
        if (isOutside(position))
            throw new IllegalArgumentException("Painting beyond canvas");

        canvas.getGraphicsContext2D().setFill(fill);
        canvas.getGraphicsContext2D().fillRect(
                (canvas.getWidth() * position.getX()) / width + BORDER_THICKNESS,
                (canvas.getHeight() * position.getY()) / height + BORDER_THICKNESS,
                (canvas.getWidth()) / width - 2 * BORDER_THICKNESS,
                (canvas.getHeight()) / height - 2 * BORDER_THICKNESS
        );

        if (position.equals(start))
            drawCircle(start, START_COLOR);

        if (position.equals(end))
            drawCircle(end, END_COLOR);
    }

    private boolean isOutside(Vector2d position) {
        return !(position.follows(lowerLeft) && position.precedes(upperRight));
    }

    @FXML
    private void resize() {
        algorithmFinished();

        if (setupSize()) {
            setupCanvas();
            paused = false;
        }
    }

    @FXML
    private void reset() {
        reset(new LinkedList<>());
    }

    private void reset(List<Vector2d> walls) {
        algorithmFinished();
        setupCanvas(walls);
        paused = false;
    }

    @FXML
    private void randomWalls() {
        reset();

        var random = new Random();
        for (int i = 0; i < width * height * RANDOM_FILL_FACTOR; i++)
            createWall(random.nextInt(width), random.nextInt(height));
    }

    @FXML
    private void generateMaze() {
        walls = MazeGenerator.generate(width, height);
        walls.remove(start);
        walls.remove(end);

        reset(walls);
    }

    @FXML
    private void start() {
        if (!paused) {
            try {
                setupAlgorithm();
            } catch (StackOverflowError e) {
                showAlert("Stack overflow - use smaller map or different algorithm");
                return;
            }
        }

        if (algorithm == null)
            return;

        block = true;
        paused = false;

        var visitOrder = algorithm.getVisitOrder();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (currentIndex.get() >= visitOrder.size())
                        algorithmFinished();
                    else if (end.equals(visitOrder.get(currentIndex.get()).getPosition()))
                        targetFound(graph);
                    else {
                        cellVisited(visitOrder.get(currentIndex.get()).getPosition());
                        currentIndex.addAndGet(1);
                    }
                });
            }
        }, 0, TIME_STEP);

        stopButton.setDisable(false);
        startButton.setDisable(true);
    }

    private void setupAlgorithm() {
        if (block)
            setupCanvas(walls);

        int s = start.toInt(width);
        int e = end.toInt(width);

        if (algorithmText.getValue() == null) {
            showAlert("Algorithm is not selected");
            return;
        }

        graph = GraphConverter.toGraph(width, height, walls);
        algorithm = switch (algorithmText.getValue()) {
            case "DFS" -> new DFS(s, graph);
            case "BFS" -> new BFS(s, graph);
            case "Dijkstra" -> new Dijkstra(s, graph);
            case "A*" -> new AStar(s, e, graph, width);
            default -> null;
        };

        if (algorithm == null)
            showAlert("Algorithm is not selected");
    }

    @FXML
    private void stop() {
        int i = currentIndex.get();
        algorithmFinished();

        if (block) {
            paused = true;
            currentIndex.set(i);
        }
    }

    @FXML
    private void addWall(MouseEvent event) {
        int x = (int) ((event.getX() * width) / canvas.getWidth());
        int y = (int) ((event.getY() * height) / canvas.getHeight());
        createWall(x, y);
    }

    private void createWall(int x, int y) {
        var position = new Vector2d(x, y);
        if (block || position.equals(start) || position.equals(end))
            return;

        walls.add(position);
        drawRectangle(position, WALL_COLOR);
    }

    public void cellVisited(Vector2d position) {
        drawRectangle(position, VISITED_COLOR);
    }

    public void targetFound(List<Node> graph) {
        algorithmFinished();
        var path = GraphConverter.toPath(graph, end.toInt(width));

        for (var position : path)
            drawRectangle(position, PATH_COLOR);
    }

    public void algorithmFinished() {
        currentIndex.set(0);
        stopButton.setDisable(true);
        startButton.setDisable(false);

        timer.cancel();
        timer = new Timer();
    }

    private void showAlert(String message) {
        var alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.YES);
        alert.showAndWait();
    }
}
