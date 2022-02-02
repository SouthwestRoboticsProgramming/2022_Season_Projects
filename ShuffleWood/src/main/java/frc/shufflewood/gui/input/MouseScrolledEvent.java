package frc.shufflewood.gui.input;

public class MouseScrolledEvent extends MouseEvent {
    private final float scrollX;
    private final float scrollY;

    public MouseScrolledEvent(float x, float y, float scrollX, float scrollY) {
        super(x, y);
        this.scrollX = scrollX;
        this.scrollY = scrollY;
    }

    public float getScrollX() {
        return scrollX;
    }

    public float getScrollY() {
        return scrollY;
    }
}
