package frc.shuffleplank.gui.draw;

import processing.core.PApplet;

public class DrawTextCommand implements DrawCommand {
    private final String str;
    private final float x, y;
    private final int col;

    public DrawTextCommand(String str, float x, float y, int col) {
        this.str = str;
        this.x = x;
        this.y = y;
        this.col = col;
    }

    @Override
    public void draw(PApplet p) {
        p.noStroke();
        p.fill(col);
        p.text(str, x, y);
    }
}
