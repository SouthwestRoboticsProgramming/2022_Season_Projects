package frc.visualizer.overlays;

import frc.visualizer.Point;
import frc.visualizer.Robot;
import processing.core.PGraphics;

import java.util.List;

public class PathOverlay implements Overlay {
    @Override
    public void draw(PGraphics g, Robot robot) {
        List<Point> path = robot.getPath();

        g.strokeWeight(3/5f);
        g.stroke(0, 255, 0);
        g.noFill();

        g.translate(0, 0.1f, 0);
        g.beginShape(g.LINE_STRIP);
        g.vertex((float) robot.getPredictedX(), 0, (float) robot.getPredictedY());
        for (Point p : path) {
            g.vertex((float) p.getX(), 0, (float) p.getY());
        }
        g.endShape();

        if (!path.isEmpty()) {
            Point last = path.get(path.size() - 1);
            g.pushMatrix();
            g.rotateX((float) Math.PI / 2);
            g.ellipse((float) last.getX(), (float) last.getY(), 10, 10);
            g.popMatrix();

            g.translate(0, -0.1f, 0);
            g.stroke(255, 128, 0);
            g.line((float) robot.getPredictedX(), 0, (float) robot.getPredictedY(), (float) last.getX(), 0, (float) last.getY());
            g.translate(0, 0.1f, 0);
        }
    }

    @Override
    public void drawHUD(PGraphics g, Robot robot, int width, int height) {
        g.fill(0, 255, 0);
        g.text("Path info:", 0, 0);
        g.text("Steps: " + robot.getPath().size(), 10, 20);
        g.translate(0, 40);
    }
}
