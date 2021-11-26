package frc.pathfinding.lib.collision;

public class CircleCollider extends Collider {
    private double radius;

    public CircleCollider(double x, double y, double radius) {
        super(x, y);
        this.radius = radius;
    }

    @Override
    public boolean collidesWith(Collider other) {
        if (other instanceof CircleCollider) {
            return CollisionChecks.checkCircleVsCircle(this, (CircleCollider) other);
        } else if (other instanceof RectangleCollider) {
            return CollisionChecks.checkCircleVsRectangle(this, (RectangleCollider) other);
        }

        throw new UnsupportedOperationException("Collision not implemented: " + other.getClass());
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
