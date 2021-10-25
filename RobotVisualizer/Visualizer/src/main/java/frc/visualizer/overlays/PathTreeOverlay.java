package frc.visualizer.overlays;

import frc.visualizer.Robot;
import frc.visualizer.TreeNode;
import processing.core.PGraphics;

public class PathTreeOverlay implements Overlay {
    @Override
    public void draw(PGraphics g, Robot robot) {
        g.strokeWeight(1 / 5f);
        g.stroke(255, 255, 0);
        g.noFill();

        g.pushMatrix();
        g.rotateX((float) Math.PI / 2); // TODO: check if backwards
        drawNode(robot.getPathTree(), g);
        g.popMatrix();
    }

    @Override
    public void drawHUD(PGraphics g, Robot robot, int width, int height) {
        g.fill(255, 255, 0);
        g.text("Path Tree Info:", 0, 0);
        g.text("Node count: " + (robot.getPathTree().getChildCount() + 1), 10, 20);
        g.translate(0, 40);
    }

    private void drawNode(TreeNode node, PGraphics g) {
        double x = node.getX();
        double y = node.getY();
        //g.ellipse((float) x, (float) y, 2, 2);

        for (TreeNode child : node.getChildren()) {
            double cx = child.getX();
            double cy = child.getY();
            g.line((float) x, (float) y, (float) cx, (float) cy);
            drawNode(child, g);
        }
    }
}
