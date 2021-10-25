package frc.visualizer;

import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Robot {
    // Information for drawing the model
    private final float wheelSpacing;
    private final float wheelRadius;
    private final float wheelThickness;
    private final float bodyDiameter;
    private final float bodyHeight;
    private final float rollerOffset;

    // Information from the actual robot
    private double predictedX, predictedY;
    private double predictedAngle;
    private double leftRotation, rightRotation;
    private List<Point> path;
    private TreeNode pathTree;
    private long memUsed, memTotal;
    private double tps;

    public Robot(float wheelSpacing, float wheelRadius, float wheelThickness, float bodyDiameter, float bodyHeight, float rollerOffset) {
        this.wheelSpacing = wheelSpacing;
        this.wheelRadius = wheelRadius;
        this.wheelThickness = wheelThickness;
        this.bodyDiameter = bodyDiameter;
        this.bodyHeight = bodyHeight;
        this.rollerOffset = rollerOffset;
        this.path = new ArrayList<>();
        this.pathTree = new TreeNode(0, 0, Collections.emptyList());

    }

    public void draw(PGraphics g) {
        g.pushMatrix();

        // Move into position
        g.translate((float) predictedX, wheelRadius, (float) predictedY);
        g.rotateY((float) predictedAngle);

        // Draw the body
        g.fill(255);
        g.noStroke();
        ShapeRenderer.cylinder(g, bodyDiameter / 2.0f, bodyHeight);

        // Draw left wheel
        g.rotateZ((float) Math.PI / 2);
        g.translate(0, wheelSpacing / 2, 0);
        g.fill(128);
        ShapeRenderer.cylinder(g, wheelRadius, wheelThickness);

        // Draw right wheel
        g.translate(0, -wheelSpacing, 0);
        ShapeRenderer.cylinder(g, wheelRadius, wheelThickness);

        // Draw roller
        g.translate(0, wheelSpacing / 2, rollerOffset);
        g.fill(64);
        g.sphere(wheelRadius);

        g.popMatrix();
    }

    public double getPredictedX() {
        return predictedX;
    }

    public void setPredictedX(double predictedX) {
        this.predictedX = predictedX;
    }

    public double getPredictedY() {
        return predictedY;
    }

    public void setPredictedY(double predictedY) {
        this.predictedY = predictedY;
    }

    public double getPredictedAngle() {
        return predictedAngle;
    }

    public void setPredictedAngle(double predictedAngle) {
        this.predictedAngle = predictedAngle;
    }

    public double getLeftRotation() {
        return leftRotation;
    }

    public void setLeftRotation(double leftRotation) {
        this.leftRotation = leftRotation;
    }

    public double getRightRotation() {
        return rightRotation;
    }

    public void setRightRotation(double rightRotation) {
        this.rightRotation = rightRotation;
    }

    public List<Point> getPath() {
        return path;
    }

    public TreeNode getPathTree() {
        return pathTree;
    }

    public void setPathTree(TreeNode pathTree) {
        this.pathTree = pathTree;
    }

    public long getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(long memUsed) {
        this.memUsed = memUsed;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }

    public double getTicksPerSecond() {
        return tps;
    }

    public void setTicksPerSecond(double tps) {
        this.tps = tps;
    }
}
