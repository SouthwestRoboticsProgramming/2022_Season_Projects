package frc.visualizer.overlays;

import frc.visualizer.Robot;
import processing.core.PGraphics;

public class PerformanceOverlay implements Overlay {
    @Override
    public void draw(PGraphics g, Robot robot) {

    }

    @Override
    public void drawHUD(PGraphics g, Robot robot, int width, int height) {
        long memUsage = robot.getMemUsed() / 1024;
        long memTotal = robot.getMemTotal() / 1024;
        if (memTotal == 0) return;
        int memFreePercent = (int) Math.round((double) (memTotal - memUsage) / memTotal * 100);

        g.fill(255);
        g.text("Ticks Per Second: " + robot.getTicksPerSecond(), 0, 0);
        g.text("Memory Usage: " + memUsage + "KB / " + memTotal + "KB", 0, 20);
        g.text("Memory Free: " + memFreePercent + "%", 0, 40);
        g.translate(0, 60);
    }
}
