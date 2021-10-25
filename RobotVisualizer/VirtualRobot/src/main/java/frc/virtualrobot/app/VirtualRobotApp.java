package frc.virtualrobot.app;

import frc.virtualrobot.robot.VirtualRobot;
import processing.core.PApplet;
import processing.core.PGraphics;

public class VirtualRobotApp extends PApplet {
    private static final double MOTOR_SPEED = 480;

    private VirtualRobot robot;
    private MotorController left;
    private MotorController right;

    private boolean initialDelay = true;

    @Override
    public void settings() {
        size(400, 300, P2D);
    }

    @Override
    public void setup() {
        frameRate(50);

        robot = new VirtualRobot();
        left = new MotorController(robot.getLeft(), MOTOR_SPEED);
        right = new MotorController(robot.getRight(), MOTOR_SPEED);
    }

    @Override
    public void draw() {
        if (initialDelay) {
            println("Waiting a few seconds for the visualizer to start up...");
            delay(10000);
            println("Starting!");
            initialDelay = false;
        }

        background(0);
        robot.tick(1 / 50.0, frameRate);

        PGraphics g = getGraphics();

        translate(width / 3f - 37.5f, height / 2f - 75);
        left.draw(g, true);

        translate(width / 3f, 0);
        right.draw(g, false);
    }

    @Override
    public void keyPressed() {
        switch (key) {
            case 'w': left.pressCCW(); break;
            case 's': left.pressCW(); break;
            case 'i': right.pressCW(); break;
            case 'k': right.pressCCW(); break;
        }
    }

    @Override
    public void keyReleased() {
        switch (key) {
            case 'w': left.releaseCCW(); break;
            case 's': left.releaseCW(); break;
            case 'i': right.releaseCW(); break;
            case 'k': right.releaseCCW(); break;
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting virtual robot");
        PApplet.main(VirtualRobotApp.class.getName());
    }
}
