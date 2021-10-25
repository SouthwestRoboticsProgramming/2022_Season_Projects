package frc.virtualrobot.robot.path;

public class Point {
    private final double x;
    private final double y;
    private double cost = -1;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Point o) {
        double dx = x - o.x;
        double dy = y - o.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceToSq(Point o) {
        double dx = x - o.x;
        double dy = y - o.y;
        return dx * dx + dy * dy;
    }

    public Point lerpTo(Point o, double l) {
        return new Point(
                x * (1.0 - l) + o.x * l,
                y * (1.0 - l) + o.y * l
        );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
