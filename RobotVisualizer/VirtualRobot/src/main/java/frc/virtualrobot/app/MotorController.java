package frc.virtualrobot.app;

import frc.virtualrobot.robot.Motor;
import processing.core.PGraphics;

public class MotorController {
    private final Motor motor;
    private final double speed;

    private boolean cw = false, ccw = false;
    private int currentMovement = 0;

    public MotorController(Motor motor, double speed) {
        this.motor = motor;
        this.speed = speed;
    }

    public void draw(PGraphics g, boolean flip) {
        g.stroke(255);
        g.strokeWeight(3);

        g.fill(currentMovement == (flip ? 1 : -1) ? 100 : ((flip ? ccw : cw) ? 64 : 32));
        g.rect(0, 0, 75, 75);

        g.fill(currentMovement == (flip ? -1 : 1) ? 100 : ((flip ? cw : ccw) ? 64 : 32));
        g.rect(0, 75, 75, 75);

        g.fill(255);
        g.noStroke();
        g.triangle(37.5f, 10, 65, 65, 10, 65);
        g.triangle(10, 85, 65, 85, 37.5f, 140);
    }

    public void pressCW() {
        cw = true;
        motor.setMovement(-speed);
        currentMovement = -1;
    }

    public void pressCCW() {
        ccw = true;
        motor.setMovement(speed);
        currentMovement = 1;
    }

    public void releaseCW() {
        cw = false;
        motor.setMovement(ccw ? speed : 0);
        currentMovement = ccw ? 1 : 0;
    }

    public void releaseCCW() {
        ccw = false;
        motor.setMovement(cw ? -speed : 0);
        currentMovement = cw ? -1 : 0;
    }
}
