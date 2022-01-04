package frc.shuffleplank.gui.draw;

import processing.core.PApplet;

public class FillRoundRectCommand implements DrawCommand {
    private final float x, y;
    private final float w, h;
    private final int col;
    private final float round;

    public FillRoundRectCommand(float x, float y, float w, float h, float round, int col) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.round = round;
        this.col = col;
    }

    @Override
    public void draw(PApplet p) {
        p.noStroke();
        p.fill(col);
        p.rect(x, y, w, h, round, round, round, round);
    }
}
