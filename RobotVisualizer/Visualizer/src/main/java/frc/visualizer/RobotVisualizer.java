package frc.visualizer;

import frc.visualizer.controller.RobotController;
import frc.visualizer.controller.VirtualRobotController;
import frc.visualizer.overlays.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

public class RobotVisualizer extends PApplet {
    private static final int TEAM_NUMBER = 2129;
    private static final String TEAM_NAME = "Ultraviolet";

    private PeasyCam camera;
    private Grid grid;
    private Robot robot;
    private RobotController controller;
    private List<Overlay> overlays;

    // Nothing except size should ever be in here!
    @Override
    public void settings() {
        size(1280, 720, P3D);
        //fullScreen(P3D);
    }

    @Override
    public void setup() {
        ellipseMode(CENTER);

        camera = new PeasyCam(this, 500);
        grid = new Grid(15, 10);
        robot = new Robot(28.5f, 7.62f / 2f, 2.2225f, 34, 1, 11.2f);
        //controller = new PhysicalRobotController("Visualizer", TEAM_NUMBER);
        controller = new VirtualRobotController();

        overlays = new ArrayList<>();
        overlays.add(new PerformanceOverlay());
        overlays.add(new PathTreeOverlay());
        overlays.add(new PathOverlay());
        overlays.add(new DebugOverlay());
    }

    @Override
    public void draw() {
        // Update robot controller
        controller.update(robot);

        //robot.setPredictedAngle(millis() / 1000f * PI);

        // Set up scene
        background(0);
        scale(5, -5, -5);
        ambientLight(128, 128, 128);
        directionalLight(128, 128, 128, -1, 1, -1);

        // Draw scene
        PGraphics g = getGraphics();
        grid.draw(g);
        robot.draw(g);
        for (Overlay overlay : overlays) {
            translate(0, 0.1f, 0);
            overlay.draw(g, robot);
        }

        // Draw Heads-Up Display
        camera.beginHUD();
        {
            // HUD should not be lit
            noLights();

            translate(10, 20);
            fill(255);
            text("Team " + TEAM_NUMBER + ": \"" + TEAM_NAME + "\"", 0, 0);
            text("FPS: " + frameRate, 0, 20);
            //text("Connected using " + controller.getClass().getSimpleName(), 0, 40);

            pushMatrix();

            translate(200, 100);
            ellipse(0, 0, 50, 50);
            stroke(255);
            strokeWeight(4);
            line(0, 0, 50 * cos((float) robot.getLeftRotation()), 50 * sin((float) robot.getLeftRotation()));

            popMatrix();
            pushMatrix();

            translate(400, 100);
            ellipse(0, 0, 50, 50);
            stroke(255);
            strokeWeight(4);
            line(0, 0, 50 * cos((float) robot.getRightRotation()), 50 * sin((float) robot.getRightRotation()));

            popMatrix();


            translate(0, 80);
            for (Overlay overlay : overlays) {
                overlay.drawHUD(getGraphics(), robot, width, height);
                translate(0, 20);
            }
        }
        camera.endHUD();
    }

    public static void main(String[] args) {
        PApplet.main(RobotVisualizer.class.getName());
    }
}
