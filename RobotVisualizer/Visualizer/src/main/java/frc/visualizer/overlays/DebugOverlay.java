package frc.visualizer.overlays;

import frc.visualizer.Robot;
import processing.core.PGraphics;

public class DebugOverlay implements Overlay {
    @Override
    public void draw(PGraphics g, Robot robot) {
        g.pushMatrix();
        g.translate(50, 0, 0);
        g.fill(255, 0, 0);
        g.noStroke();
        g.box(10);
        g.popMatrix();

        g.pushMatrix();
        g.translate(0, 0, 50);
        g.fill(0, 0, 255);
        g.noStroke();
        g.box(10);
        g.popMatrix();
    }

    @Override
    public void drawHUD(PGraphics g, Robot robot, int width, int height) {

    }
}
