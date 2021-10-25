package frc.visualizer.overlays;

import frc.visualizer.Robot;
import processing.core.PGraphics;

public interface Overlay {
    void draw(PGraphics g, Robot robot);
    void drawHUD(PGraphics g, Robot robot, int width, int height);
}
