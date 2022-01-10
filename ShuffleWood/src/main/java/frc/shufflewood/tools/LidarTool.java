package frc.shufflewood.tools;

import processing.core.PApplet;
import processing.core.PGraphics;

public class LidarTool extends CustomDrawTool {
    public LidarTool(PApplet app) {
        super(app, "Lidar");
    }

    @Override
    protected void drawView(PGraphics g) {
        g.background(0, 0, 255);
    }

    @Override
    protected void onResize(int width, int height) {

    }
}
