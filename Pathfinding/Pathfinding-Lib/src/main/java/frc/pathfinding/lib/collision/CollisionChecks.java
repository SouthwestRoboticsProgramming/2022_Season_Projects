package frc.pathfinding.lib.collision;

final class CollisionChecks {
    static boolean checkCircleVsCircle(CircleCollider a, CircleCollider b) {
        // Check if distance is less than or equal to the sum of the radii
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double distSq = dx * dx + dy * dy;
        double collisionDist = a.getRadius() + b.getRadius();

        return distSq <= collisionDist * collisionDist;
    }

    static boolean checkCircleVsRectangle(CircleCollider c, RectangleCollider r) {
        // Get circle position relative to rectangle
        double cx = c.getX() - r.getX();
        double cy = c.getY() - r.getY();

        // Rotate the position
        double sin = Math.sin(-r.getRotation() + Math.PI / 2);
        double cos = Math.cos(-r.getRotation() + Math.PI / 2);
        double projCX = cx * sin - cy * cos;
        double projCY = cx * cos + cy * sin;

        // Find closest point on the rectangle
        double halfWidth = r.getWidth() / 2.0;
        double halfHeight = r.getHeight() / 2.0;
        double clampedX = clamp(projCX, -halfWidth, halfWidth);
        double clampedY = clamp(projCY, -halfHeight, halfHeight);

        // Check if closest point is inside circle
        double dx = projCX - clampedX;
        double dy = projCY - clampedY;
        return dx * dx + dy * dy <= c.getRadius() * c.getRadius();
    }

    static boolean checkRectangleVsRectangle(RectangleCollider a, RectangleCollider b) {
        return false;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(v, max));
    }

    private CollisionChecks() {
        throw new AssertionError();
    }
}
