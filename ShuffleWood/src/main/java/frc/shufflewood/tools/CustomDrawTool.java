package frc.shufflewood.tools;

import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.Rectangle;
import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class CustomDrawTool implements Tool {
    private final PApplet app;
    private final String windowTitle;
    private PGraphics g;
    private float lastW = -1, lastH = -1;

    public CustomDrawTool(PApplet app, String windowTitle) {
        this.app = app;
        this.windowTitle = windowTitle;
    }

    protected abstract void drawView(PGraphics g);
    protected abstract void onResize(int width, int height);

    @Override
    public void draw(GuiContext gui) {
        gui.begin(windowTitle);

        Rectangle region = gui.getContentRegion();
        if (region.getW() != lastW || region.getH() != lastH) {
            lastW = region.getW();
            lastH = region.getH();

            g = app.createGraphics((int) lastW, (int) lastH);
            onResize((int) lastW, (int) lastH);
        }
        g.beginDraw();
        drawView(g);
        g.endDraw();
        gui.image(g);

        gui.end();
    }
}
