package frc.shuffleplank.gui.draw;

import frc.shuffleplank.gui.draw.DrawCommand;
import processing.core.PApplet;

public class FillRectCommand implements DrawCommand {
    private final float x, y;
    private final float w, h;
    private final int col;

    public FillRectCommand(float x, float y, float w, float h, int col) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.col = col;
    }

    @Override
    public void draw(PApplet p) {
        p.noStroke();
        p.fill(col);
        p.rect(x, y, w, h);
    }
}
