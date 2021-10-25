package frc.virtualrobot.robot.path;

public class Environment {
    private final double left;
    private final double top;
    private final double right;
    private final double bottom;

    public Environment(double left, double top, double right, double bottom){
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    private boolean isPointPassable(Point p) {
        return (p.getX() < 50 || p.getX() > 100) || (p.getY() < 50 || p.getY() > 100);
    }

    public boolean isLinePassable(Point p1, Point p2) {
        int precision = 10;

        for (int i = 0; i <= precision; i++) {
            if (!isPointPassable(p1.lerpTo(p2, i / (double) precision))) {
                return false;
            }
        }

        return true;
    }
}
