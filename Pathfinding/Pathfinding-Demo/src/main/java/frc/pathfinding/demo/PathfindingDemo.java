package frc.pathfinding.demo;

import frc.pathfinding.lib.Cell;
import frc.pathfinding.lib.Grid;
import frc.pathfinding.lib.PathOptimizer;
import frc.pathfinding.lib.Pathfinder;
import processing.core.PApplet;

import java.util.List;

public class PathfindingDemo extends PApplet {
    private Grid grid;
    private Pathfinder pathfinder;
    private PathOptimizer optimizer;
    private final int CELLS_Y = 26 * 12 / 6;
    private final int CELLS_X = 54 * 12 / 6;
    private float cellSize;
    private int startX, startY;
    private int endX, endY;

    @Override
    public void settings() {
        //size(800, 600);
        fullScreen();
    }

    @Override
    public void setup() {
        ellipseMode(CENTER);

        grid = new Grid(CELLS_X, CELLS_Y);
        cellSize = width / (float) CELLS_X;

        for (int i = 0; i < CELLS_X * CELLS_Y / 2; i++) {
            int x = (int) (random(0, CELLS_X));
            int y = (int) (random(0, CELLS_Y));

            grid.setCellBlocked(x, y, true);
        }

        pathfinder = new Pathfinder(grid);
        optimizer = new PathOptimizer(grid);

        startX = 1;
        startY = 1;
        pathfinder.setStartCell(new Cell(startX, startY));
        endX = CELLS_X - 2;
        endY = CELLS_Y - 2;
        pathfinder.setGoalCell(new Cell(endX, endY));
    }

    @Override
    public void draw() {
        background(255);

        stroke(0);
        strokeWeight(1);
        for (int i = 0; i < CELLS_X; i++) {
            for (int j = 0; j < CELLS_Y; j++) {
                if (grid.isCellBlocked(i, j)) {
                    fill(255, 0, 0);
                } else {
                    fill(255);
                }

                rect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }

        fill(0, 255, 0);
        ellipse((startX + 0.5f) * cellSize, (startY + 0.5f) * cellSize, cellSize / 2, cellSize / 2);

        fill(0, 0, 255);
        ellipse((endX + 0.5f) * cellSize, (endY + 0.5f) * cellSize, cellSize / 2, cellSize / 2);

        long start = System.currentTimeMillis();
        List<Cell> path = pathfinder.getPath();
        long end = System.currentTimeMillis();
        System.out.println("pathfinding took " + (end - start) + " milliseconds");
        if (path == null) {
            return;
        }

//        stroke(0, 0, 255);
//        strokeWeight(3);
//        beginShape(LINES);
//        for (int i = 0; i < path.size() - 1; i++) {
//            Cell c1 = path.get(i);
//            Cell c2 = path.get(i + 1);
//            vertex((c1.getX() + 0.5f) * cellSize, (c1.getY() + 0.5f) * cellSize);
//            vertex((c2.getX() + 0.5f) * cellSize, (c2.getY() + 0.5f) * cellSize);
//        }
//        endShape();

        path = optimizer.optimize(path);

        stroke(0, 128, 255);
        strokeWeight(3);
        beginShape(LINES);
        for (int i = 0; i < path.size() - 1; i++) {
            Cell c1 = path.get(i);
            Cell c2 = path.get(i + 1);
            vertex((c1.getX() + 0.5f) * cellSize, (c1.getY() + 0.5f) * cellSize);
            vertex((c2.getX() + 0.5f) * cellSize, (c2.getY() + 0.5f) * cellSize);
        }
        endShape();

//        if (optimizer.testLineOfSight(new Cell(startX, startY), new Cell(endX, endY))) {
//            stroke(0, 255, 0);
//        } else {
//            stroke(255, 128, 0);
//        }
//        translate(cellSize * 0.5f, cellSize * 0.5f);
//        line(startX * cellSize, startY * cellSize, endX * cellSize, endY * cellSize);
    }

    @Override
    public void mousePressed() {
        int x = (int) (mouseX / cellSize);
        int y = (int) (mouseY / cellSize);

        if (mouseButton == LEFT) {
            startX = x;
            startY = y;
            pathfinder.setStartCell(new Cell(startX, startY));
        } else if (mouseButton == RIGHT) {
            endX = x;
            endY = y;
            pathfinder.setGoalCell(new Cell(endX, endY));
        }
    }

    @Override
    public void mouseDragged() {
        mousePressed();
    }

    public static void main(String[] args) {
        PApplet.main(PathfindingDemo.class.getName());
    }
}
