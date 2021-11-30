package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public final class XboxController {
    private final Joystick joystick;

    public XboxController(int id) {
        joystick = new Joystick(id);
    }

    public double getLeftStickX() {
        return joystick.getRawAxis(0);
    }

    public double getLeftStickY() {
        return joystick.getRawAxis(1);
    }
}
