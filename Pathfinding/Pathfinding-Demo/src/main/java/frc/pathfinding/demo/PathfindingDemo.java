package frc.pathfinding.demo;

import frc.pathfinding.lib.Cell;
import frc.pathfinding.lib.Grid;
import frc.pathfinding.lib.PathOptimizer;
import frc.pathfinding.lib.Pathfinder;
import frc.pathfinding.lib.collision.CircleCollider;
import frc.pathfinding.lib.collision.Collider;
import frc.pathfinding.lib.collision.CollisionGrid;
import frc.pathfinding.lib.collision.RectangleCollider;
import processing.core.PApplet;

import java.util.List;

public class PathfindingDemo extends PApplet {
    private CollisionGrid grid;
    private Pathfinder pathfinder;
    private PathOptimizer optimizer;
    private CircleCollider robot;
    private final int CELLS_Y = 26 * 12 / 6;
    private final int CELLS_X = 54 * 12 / 6;
    private float cellSize;
    private int startX, startY;
    private int endX, endY;
    private boolean playing = false;
    private float robotX, robotY;
    private float padding = 1; // Distance to try to stay away from obstacles

    @Override
    public void settings() {
        size(1280, 720);
    }

    @Override
    public void setup() {
        ellipseMode(CENTER);

        robot = new CircleCollider(0, 0, 5.89 + padding);
        grid = new CollisionGrid(CELLS_X, CELLS_Y, robot);
        cellSize = width / (float) CELLS_X;

        //grid.addObstacle(new CircleCollider(.5*CELLS_X, .5*CELLS_Y, 7));
        //grid.addObstacle(new RectangleCollider(.5*CELLS_X, .5*CELLS_Y, 12.313, 12.313, Math.toRadians(66)));
//        grid.addObstacle(new RectangleCollider(.5*CELLS_X, .5*CELLS_Y, 12.313 + 4.417, 4, Math.toRadians(66)));
//        grid.addObstacle(new RectangleCollider(.5*CELLS_X, .5*CELLS_Y, 12.313 + 4.417, 4, Math.toRadians(66+90)));
//        grid.addObstacle(new RectangleCollider(0, CELLS_Y, 15.42, 15.42, .25*Math.PI));
//        grid.addObstacle(new RectangleCollider(CELLS_X, 0, 15.42, 15.42, .25*Math.PI));
//        grid.addObstacle(new RectangleCollider(.5*CELLS_X, .5*CELLS_Y, 7.69, height, 0));
        grid.loadFromFile("scene.txt");

        pathfinder = new Pathfinder(grid);
        optimizer = new PathOptimizer(grid);

        startX = 1;
        startY = 1;
        robotX = startX;
        robotY = startY;
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

        stroke(0, 255, 0);
        strokeWeight(3);
        noFill();
        ellipse(robotX * cellSize, robotY * cellSize, (float) (robot.getRadius() - padding) * 2 * cellSize, (float) (robot.getRadius() - padding) * 2 * cellSize);

        for (Collider c : grid.getObstacles()) {
            if (c instanceof CircleCollider) {
                stroke(255, 128, 0);
                strokeWeight(3);
                noFill();

                CircleCollider circle = (CircleCollider) c;
                ellipse((float) circle.getX() * cellSize, (float) circle.getY() * cellSize, (float) circle.getRadius() * 2 * cellSize, (float) circle.getRadius() * 2 * cellSize);
            } else if (c instanceof RectangleCollider) {
                stroke(255, 128, 0);
                strokeWeight(3);
                noFill();

                RectangleCollider rect = (RectangleCollider) c;
                pushMatrix();
                translate((float) rect.getX() * cellSize, (float) rect.getY() * cellSize);
                rotate((float) -rect.getRotation());
                rect((float) -rect.getWidth() / 2 * cellSize, (float) -rect.getHeight() / 2 * cellSize, (float) rect.getWidth() * cellSize, (float) rect.getHeight() * cellSize);
                popMatrix();
            }
        }

        fill(0, 255, 0);
        stroke(0);
        strokeWeight(1);
        ellipse((startX + 0.5f) * cellSize, (startY + 0.5f) * cellSize, cellSize / 2, cellSize / 2);

        fill(0, 0, 255);
        ellipse((endX + 0.5f) * cellSize, (endY + 0.5f) * cellSize, cellSize / 2, cellSize / 2);

        List<Cell> path = pathfinder.getPath();
        if (path == null) {
            return;
        }

        // Raw path line
        stroke(0, 0, 255);
        strokeWeight(3);
        beginShape(LINES);
        for (int i = 0; i < path.size() - 1; i++) {
            Cell c1 = path.get(i);
            Cell c2 = path.get(i + 1);
            vertex((c1.getX() + 0.5f) * cellSize, (c1.getY() + 0.5f) * cellSize);
            vertex((c2.getX() + 0.5f) * cellSize, (c2.getY() + 0.5f) * cellSize);
        }
        endShape();

        path = optimizer.optimize(path);
        if (playing) play: {
            if (path.size() == 1) {
                playing = false;
                break play;
            }

            Cell target = path.get(1);
            double dx = startX - target.getX();
            double dy = startY - target.getY();
            double len = Math.sqrt(dx * dx + dy * dy);
            dx /= -len;
            dy /= -len;

            robotX += dx * 1;
            robotY += dy * 1;
            startX = (int) Math.floor(robotX);
            startY = (int) Math.floor(robotY);
            pathfinder.setStartCell(new Cell(startX, startY));
        }

        // Robot bounds highlight
        stroke(0, 128, 255, 64);
        strokeWeight((float) robot.getRadius() * cellSize * 2);
        noFill();
        beginShape();
        for (Cell c1 : path) {
            vertex((c1.getX() + 0.5f) * cellSize, (c1.getY() + 0.5f) * cellSize);
        }
        endShape();

        // Path line
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
        if (x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight()) {
            return;
        }

        if (mouseButton == LEFT) {
            startX = x;
            startY = y;
            robotX = x;
            robotY = y;
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

    @Override
    public void keyPressed() {
        playing = !playing;
    }

    public static void main(String[] args) {
        PApplet.main(PathfindingDemo.class.getName());
    }
}
