package frc.shufflewood.gui;

public final class Rect {
    public Vec2 min;
    public Vec2 max;

    public Rect() {
        min = new Vec2();
        max = new Vec2();
    }

    public Rect(Vec2 min, Vec2 max) {
        this.min = min;
        this.max = max;
    }

    public Rect(float minX, float minY, float maxX, float maxY) {
        min = new Vec2(minX, minY);
        max = new Vec2(maxX, maxY);
    }

    public Rect(Rect r) {
        min = new Vec2(r.min);
        max = new Vec2(r.max);
    }

    public Vec2 getMin() {
        return min;
    }

    public void setMin(Vec2 min) {
        this.min = min;
    }

    public Vec2 getMax() {
        return max;
    }

    public void setMax(Vec2 max) {
        this.max = max;
    }

    public float getWidth() {
        return max.x - min.x;
    }

    public float getHeight() {
        return max.y - min.y;
    }

    public void move(Vec2 v) {
        min.x += v.x;
        min.y += v.y;
        max.x += v.x;
        max.y += v.y;
    }

    public Rect clip(Rect r) {
        // Can't clip if no overlap
        if (min.x > r.max.x || min.y > r.max.y || max.x < r.min.x || max.y < r.min.y)
            return new Rect(0, 0, 0, 0);

        return new Rect(
                Math.max(min.x, r.min.x),
                Math.max(min.y, r.min.y),
                Math.min(max.x, r.max.x),
                Math.min(max.y, r.max.y)
        );
    }

    @Override
    public String toString() {
        return "Rect{(" + min.x + ", " + min.y + ") -> (" + max.x + ", " + max.y + ")}";
    }
}
