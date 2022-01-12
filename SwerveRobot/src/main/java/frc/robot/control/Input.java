package frc.robot.control;

import frc.robot.Constants;

public class Input {
    public final XboxController controller;

    public Input() {
        controller = new XboxController(Constants.DRIVE_CONTROLLER);
    }

    public double getDriveX() {
        return controller.getLeftStickX();
    }

    public double getDriveY() {
        return controller.getLeftStickY();
    }

    public double getRot() {
        return controller.getRightStickX();
    }
}
