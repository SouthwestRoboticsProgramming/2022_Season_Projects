package frc.robot.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public final class XboxController {
    private final Joystick joystick;
    private final JoystickButton a;
    private final JoystickButton b;
    private final JoystickButton x;
    private final JoystickButton y;
    private final JoystickButton menu;
    private final JoystickButton window;
    private final JoystickButton leftShoulder;
    private final JoystickButton rightShoulder;
    private final JoystickButton leftStick;
    private final JoystickButton rightStick;

    public XboxController(int id) {
        joystick = new Joystick(id);
        a = new JoystickButton(joystick, 1);
        b = new JoystickButton(joystick, 2);
        x = new JoystickButton(joystick, 3);
        y = new JoystickButton(joystick, 4);
        leftShoulder = new JoystickButton(joystick, 5);
        rightShoulder = new JoystickButton(joystick, 6);
        window = new JoystickButton(joystick, 7);
        menu = new JoystickButton(joystick, 8);
        leftStick = new JoystickButton(joystick, 9);
        rightStick = new JoystickButton(joystick, 10);
    }

    public double getLeftStickX() {
        return joystick.getRawAxis(0);
    }

    public double getLeftStickY() {
        return -joystick.getRawAxis(1);
    }

    public double getRightStickX() {
        return joystick.getRawAxis(4  );
    }

    public double getRightStickY() {
        return -joystick.getRawAxis(5);
    }

    public double getLeftTrigger() {
        return joystick.getRawAxis(2);
    }

    public double getRightTrigger() {
        return joystick.getRawAxis(3);
    }

    public boolean getLeftStickButton() {
        return leftStick.get();
    }

    public boolean getRightStickButton() {
        return rightStick.get();
    }

    public boolean getAButton() {
        return a.get();
    }

    public boolean getBButton() {
        return b.get();
    }

    public boolean getXButton() {
        return x.get();
    }

    public boolean getYButton() {
        return y.get();
    }

    public boolean getDpadUp() {
        int pov = joystick.getPOV();
        return pov == 0 || pov == 45 || pov == 315;
    }

    public boolean getDpadDown() {
        int pov = joystick.getPOV();
        return pov == 180 || pov == 135 || pov == 225;
    }

    public boolean getDpadLeft() {
        int pov = joystick.getPOV();
        return pov == 270 || pov == 225 || pov == 315;
    }

    public boolean getDpadRight() {
        int pov = joystick.getPOV();
        return pov == 90 || pov == 45 || pov == 135;
    }

    public boolean getLeftShoulderButton() {
        return leftShoulder.get();
    }

    public boolean getRightShoulderButton() {
        return rightShoulder.get();
    }

    public boolean getMenuButton() {
        return menu.get();
    }

    public boolean getWindowButton() {
        return window.get();
    }
}
