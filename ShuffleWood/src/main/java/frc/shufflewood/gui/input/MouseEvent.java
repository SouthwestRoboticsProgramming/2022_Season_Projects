package frc.shufflewood.gui.input;

public class MouseEvent implements Event {
    private final float x;
    private final float y;

    public MouseEvent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
