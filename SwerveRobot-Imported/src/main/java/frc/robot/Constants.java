package frc.robot;
import edu.wpi.first.math.geometry.Rotation2d;

public final class Constants {
    // TODO: Get these values from the robot
    public static final int DRIVE_PORT_1 = 8;
    public static final int DRIVE_PORT_2 = 7;
    public static final int DRIVE_PORT_3 = 5;
    public static final int DRIVE_PORT_4 = 6;
    public static final int TURN_PORT_1 = 11;
    public static final int TURN_PORT_2 = 10;
    public static final int TURN_PORT_3 = 9;
    public static final int TURN_PORT_4 = 12;
    public static final int CAN_PORT_1 = 2;
    public static final int CAN_PORT_2 = 4;
    public static final int CAN_PORT_3 = 3;
    public static final int CAN_PORT_4 = 1;
    public static final int NAVX_PORT = 0;
    public static final Rotation2d OFFSET_1 = Rotation2d.fromDegrees(0.63); //FIXME
    public static final Rotation2d OFFSET_2 = Rotation2d.fromDegrees(-0.11); //FIXME
    public static final Rotation2d OFFSET_3 = Rotation2d.fromDegrees(1.58); //FIXME
    public static final Rotation2d OFFSET_4 = Rotation2d.fromDegrees(1.26); //FIXME
    public static final boolean CANCODER_DIRECTION = false; // False = Counterclockwise   True = Clockwise
    
    public static final double JOYSTICK_DEAD_ZONE = 0.1;
	public static final int DRIVE_CONTROLLER = 0;

    public static final double WHEEL_TURN_KP = .02;
    public static final double WHEEL_TURN_KI = 0; // Leave this at 0: There is no steady-state error in the system
    public static final double WHEEL_TURN_KD = 0.002;

    public static final Rotation2d WHEEL_TOLERANCE = Rotation2d.fromDegrees(1); // In degrees

    public static final double WHEEL_SPACING_FRONT_BACK = 1.0; // FIXME // Meters
    public static final double WHEEL_SPACING_LEFT_RIGHT = 1.0; // FIXME // Meters

    public static final double MAX_VELOCITY = 0.5; //FIXME // Meters per second
    public static final double MAX_ROTATION_SPEED = 0.1; // FIXME // Radians per second
}
