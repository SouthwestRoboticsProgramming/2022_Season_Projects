package frc.shuffleplank;

import processing.core.PApplet;

public class SetClipCommand implements DrawCommand {
    private final float x, y;
    private final float w, h;

    public SetClipCommand(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public void draw(PApplet p) {
        p.clip(x, y, w, h);
    }
}
