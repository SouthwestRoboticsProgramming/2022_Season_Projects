package frc.robot;

public class Input {
    private final XboxController controller;

    public Input(XboxController controller) {
        this.controller = controller;
    }

    public double getDrive() {
        return -controller.getLeftStickY();
    }

    public double getTurn() {
        return controller.getLeftStickX();
    }
}
