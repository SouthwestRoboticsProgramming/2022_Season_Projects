package frc.robot;

public class Input {
    private final XboxController controller;

    public Input(XboxController controller) {
        this.controller = controller;
    }

    public double getTurnSim() {
        return controller.getLeftStickY();
    }
    
}
