package frc.robot.control;

public class Input {
    private final XboxController controller;

    public Input() {
        controller = new XboxController(0);
    }

    public double getDriveX() {
        return controller.getLeftStickX();
    }

    public double getDriveY() {
        return controller.getLeftStickY();
    }
}
