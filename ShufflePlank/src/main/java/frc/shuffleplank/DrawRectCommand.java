package frc.shuffleplank;

import processing.core.PApplet;

public class DrawRectCommand implements DrawCommand {
    private final float x, y;
    private final float w, h;
    private final int col;

    public DrawRectCommand(float x, float y, float w, float h, int col) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.col = col;
    }

    @Override
    public void draw(PApplet p) {
        p.noFill();
        p.stroke(col);
        p.rect(x, y, w, h);
    }
}
