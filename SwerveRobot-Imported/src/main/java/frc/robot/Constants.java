package frc.robot;
import edu.wpi.first.math.geometry.Rotation2d;

public final class Constants {
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
    public static final double OFFSET_1 = -276.59;
    public static final double OFFSET_2 = -349.54;
    public static final double OFFSET_3 = -311.31;
    public static final double OFFSET_4 = -218.85;
    public static final boolean CANCODER_DIRECTION = false; // False = Counterclockwise   True = Clockwise

    public static final int CLIMBER_LEFT_MOTOR_ID = 20; //FIXME
    public static final int CLIMBER_RIGHT_MOTOR_ID = 21; //FIXME
    
    public static final double JOYSTICK_DEAD_ZONE = 0.2;
	public static final int DRIVE_CONTROLLER = 0;

    
    
    public static final Rotation2d WHEEL_TOLERANCE = Rotation2d.fromDegrees(1); // In degrees
    
    public static final double WHEEL_SPACING_FRONT_BACK = 0.31;
    public static final double WHEEL_SPACING_LEFT_RIGHT = 0.30;
    
    public static final double MAX_VELOCITY = 1.5; // Meters per second
    public static final double ROBOT_MAX_VELOCITY = 4.11/*4.11*/;
    public static final double MAX_ROTATION_SPEED = 5.0; // Radians per second
    public static final double ROBOT_MAX_ROTATION_SPEED = 26.5; // Radians per second
    
    public static final double WHEEL_TURN_KP = .02;
    public static final double WHEEL_TURN_KI = 0; // Leave this at 0: There is no steady-state error in the system
    public static final double WHEEL_TURN_KD = 0.002;
    
    public static final double STABILIZATION_KP = .1;
    public static final double STABILIZATION_KI = 0;
    public static final double STABILIZATION_KD = 0.002;
}
