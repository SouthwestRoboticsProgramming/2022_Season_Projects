package frc.pathfinding.lib.collision;

public abstract class Collider {
    private double x;
    private double y;

    public Collider(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract boolean collidesWith(Collider other);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
