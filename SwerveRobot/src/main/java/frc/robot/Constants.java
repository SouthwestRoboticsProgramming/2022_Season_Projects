package frc.robot;
import edu.wpi.first.math.geometry.Rotation2d;

public final class Constants {
    public static final int DRIVE_PORT_1 = 1;
    public static final int DRIVE_PORT_2 = 4;
    public static final int DRIVE_PORT_3 = 7;
    public static final int DRIVE_PORT_4 = 10;
    public static final int TURN_PORT_1 = 2;
    public static final int TURN_PORT_2 = 5;
    public static final int TURN_PORT_3 = 8;
    public static final int TURN_PORT_4 = 11;
    public static final int CAN_PORT_1 = 3;
    public static final int CAN_PORT_2 = 6;
    public static final int CAN_PORT_3 = 9;
    public static final int CAN_PORT_4 = 12;
    public static final int NAVX_PORT = 0;
    public static final double OFFSET_1 = -133.945 + 180;
    public static final double OFFSET_2 = -220.781 + 180;
    public static final double OFFSET_3 = -77.607;
    public static final double OFFSET_4 = -3.955;
    public static final boolean CANCODER_DIRECTION = false; // False = Counterclockwise   True = Clockwise

    
    public static final String MESSENGER_HOST = "10.21.29.3";
    public static final int MESSENGER_PORT = 5805;
    
    /* Climber */
    public static final int CLIMBER_LEFT_TELE_MOTOR_ONE_ID = 20; //FIXME
    public static final int CLIMBER_LEFT_TELE_MOTOR_TWO_ID = 21; //FIXME
    public static final int CLIMBER_RIGHT_TELE_MOTOR_ONE_ID = 22; //FIXME
    public static final int CLIMBER_RIGHT_TELE_MOTOR_TWO_ID = 23; //FIXME
    public static final int CLIMBER_LEFT_SWING_MOTOR_ID = 24; //FIXME
    public static final int CLIMBER_RIGHT_SWING_MOTOR_ID = 25; //FIXME
    
    public static final double JOYSTICK_DEAD_ZONE = 0.2;
	public static final int DRIVE_CONTROLLER = 0;
    
    
    
    public static final Rotation2d WHEEL_TOLERANCE = Rotation2d.fromDegrees(1); // In degrees
    
    public static final double WHEEL_SPACING_FRONT_BACK = 0.4699;
    public static final double WHEEL_SPACING_LEFT_RIGHT = 0.4699;
    
    public static final double MAX_VELOCITY = 1.0; // Meters per second
    public static final double ROBOT_MAX_VELOCITY = 4.11/*4.11*/;
    public static final double MAX_ROTATION_SPEED = 4.0; // Radians per second
    public static final double ROBOT_MAX_ROTATION_SPEED = 26.5; // Radians per second
    
    public static final double WHEEL_TURN_KP = 0.02;
    public static final double WHEEL_TURN_KI = 0; // Leave this at 0: There is no steady-state error in the system
    public static final double WHEEL_TURN_KD = 0.002;
    
    public static final double STABILIZATION_KP = 0.1;
    public static final double STABILIZATION_KI = 0;
    public static final double STABILIZATION_KD = 0.002;
    
    public static final int CAMERA_TURRET_MOTOR_ID = 30;
    public static final double CAMERA_TURRET_ENCODER_TICKS_PER_ROTATION = 177.6;
    public static final double CAMERA_TURRET_KP = 0.1;
    public static final double CAMERA_TURRET_KI = 0;
    public static final double CAMERA_TURRET_KD = 0;

    public static final int INTAKE_MOTOR_ID = 50943; //FIXME
    public static final double INTAKE_KS = 0;
    public static final double INTAKE_KA = 0;
    public static final double INTAKE_KV = 0;

    public static final int SHUFFLEWOOD_SAVE_INTERVAL = 50;
}
