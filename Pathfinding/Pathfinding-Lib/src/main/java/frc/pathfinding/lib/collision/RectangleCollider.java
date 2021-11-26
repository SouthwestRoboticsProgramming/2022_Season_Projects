package frc.pathfinding.lib.collision;

public class RectangleCollider extends Collider {
    private double width;
    private double height;
    private double rotation;

    public RectangleCollider(double x, double y, double width, double height, double rotation) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    @Override
    public boolean collidesWith(Collider other) {
        if (other instanceof CircleCollider) {
            return CollisionChecks.checkCircleVsRectangle((CircleCollider) other, this);
        } else if (other instanceof RectangleCollider) {
            return CollisionChecks.checkRectangleVsRectangle((RectangleCollider) other, this);
        }

        throw new UnsupportedOperationException("Collision not implemented: " + other.getClass());
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getRotation() {
        return rotation;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
