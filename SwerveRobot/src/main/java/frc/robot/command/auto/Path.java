package frc.robot.command.auto;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public static class Point {
        public double x;
        public double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private final List<Point> path;

    public Path() {
        path = new ArrayList<>();
    }

    public void addPoint(double x, double y) {
        path.add(new Point(x, y));
    }

    public List<Point> getPath() {
        return new ArrayList<>(path);
    }
}
