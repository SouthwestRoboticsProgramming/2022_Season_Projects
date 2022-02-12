package frc.robot.constants;

public class ControlConstants {
    public static final double JOYSTICK_DEAD_ZONE = 0.2;
	public static final int DRIVE_CONTROLLER = 0;

    public static final double MAX_ROTATION_SPEED = 6.0; // Radians per second
    public static final double MAX_VELOCITY = 3.0; // Meters per second

    private ControlConstants() {
        throw new AssertionError();
    }
}
