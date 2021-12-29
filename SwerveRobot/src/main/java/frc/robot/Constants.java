package frc.robot;

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
    public static final double STARTING_POS_1 = 0.63;
    public static final double STARTING_POS_2 = -0.11;
    public static final double STARTING_POS_3 = 1.58;
    public static final double STARTING_POS_4 = 1.26;
    
    public static final double JOYSTICK_DEAD_ZONE = 0.1;
	public static final int DRIVE_CONTROLLER = 0;

    public static final double WHEEL_TURN_KP = .2;
    public static final double WHEEL_TURN_KI = .08;
    public static final double WHEEL_TURN_KD = 0.8;

    public static final double WHEEL_TURN_TOLERANCE = 10; // In degrees
    public static final double WHEEL_TOLERANCE = .1; // In radians
    public static final double WHEEL_DERVIVATIVE_TOLERANCE = .2; // In radians
	public static final double STARTING_WHEEL_ANGLE = 0;
}
