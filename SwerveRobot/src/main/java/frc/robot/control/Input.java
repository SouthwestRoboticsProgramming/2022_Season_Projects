package frc.robot.control;

import static frc.robot.constants.ControlConstants.*;

public class Input {
    private final XboxController drive;
    private final XboxController manipulator;

    public Input() {
        drive = new XboxController(DRIVE_CONTROLLER);
        manipulator = new XboxController(MANIPULATOR_CONTROLLER);
    }

    public double getDriveX() {
        return drive.getLeftStickX();
    }

    public double getDriveY() {
        return drive.getLeftStickY();
    }

    public double getRot() {
        return -drive.getRightStickX();
    }

    public boolean getSwingLeft() {
        return drive.getLeftShoulderButton();
    }

    public boolean getSwingRight() {
        return drive.getRightShoulderButton();
    }

    public boolean getShoot() {
        return drive.getAButton();
    }

    public boolean getAim() {
        return drive.getRightShoulderButton();
    }





    /* Tests */

    public double testHoodAngle() {
        double angle = -1;
        
        // FIXME: @mvog2501 choose angles
        if (manipulator.getDpadDown()) angle = 0;
        if (manipulator.getDpadUp()) angle = 0;
        if (manipulator.getDpadLeft()) angle = 0;
        if (manipulator.getDpadRight()) angle = 0;

        return angle;
    }

    public double testShooterSpeed() {
        return manipulator.getRightTrigger() * 3;
    }

    public boolean testShoot() {
        return manipulator.getRightShoulderButton();
    }

    public boolean testIntakeLiftUp() {
        return manipulator.getXButton();
    }

    public boolean testIntakeLiftDown() {
        return manipulator.getAButton();
    }
}
