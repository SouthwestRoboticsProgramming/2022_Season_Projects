package frc.robot.control;

import frc.robot.Constants;

public class Input {
    private final XboxController controller;

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

    public boolean getSwingLeft() {
        return controller.getLeftShoulderButton();
    }

    public boolean getSwingRight() {
        return controller.getRightShoulderButton();
    }

    public boolean getShoot() {
        return controller.getAButton();
    }

    public boolean getAim() {
        return controller.getRightShoulderButton();
    }
}
