package frc.shuffleplank;

import processing.core.PApplet;

public class DrawLineCommand implements DrawCommand {
    private final float x1, y1;
    private final float x2, y2;
    private final int col;

    public DrawLineCommand(float x1, float y1, float x2, float y2, int col) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.col = col;
    }

    @Override
    public void draw(PApplet p) {
        p.stroke(col);
        p.line(x1, y1, x2, y2);
    }
}
