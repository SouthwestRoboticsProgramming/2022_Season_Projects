package frc.robot.constants;

public class ClimberConstants {

    /* Settings */
    public static final double CLIMBER_SWING_MOTOR_KP = 0;
    public static final double CLIMBER_SWING_MOTOR_KI = 0;
    public static final double CLIMBER_SWING_MOTOR_KD = 0;
    public static final double CLIMBER_SWING_TOLERANCE = 2;

    public static final double CLIMBER_TELE_MOTOR_KP = 0;
    public static final double CLIMBER_TELE_MOTOR_KI = 0;
    public static final double CLIMBER_TELE_MOTOR_KD = 0;
    public static final double CLIMBER_TELE_TOLERANCE = 20;
    public static final double CLIMBER_MAX_SPEED = 0.20; // Percent 0 to 1

    /* Hardware */
    public static final int CLIMBER_LEFT_TELE_MOTOR_ONE_ID = 20; //FIXME
    public static final int CLIMBER_LEFT_TELE_MOTOR_TWO_ID = 21; //FIXME
    public static final int CLIMBER_RIGHT_TELE_MOTOR_ONE_ID = 22; //FIXME
    public static final int CLIMBER_RIGHT_TELE_MOTOR_TWO_ID = 23; //FIXME
    public static final int CLIMBER_TELE_BASE_HEIGHT = 1; //FIXME
    public static final int CLIMBER_TELE_PULLEY_DIAMETER = 1; //FIXME

    public static final int CLIMBER_LEFT_SWING_MOTOR_ID = 24;
    public static final int CLIMBER_RIGHT_SWING_MOTOR_ID = 25;
    public static final double CLIMBER_SWING_ARM = 15 + 1/8.0; // Length of the swinging arm measured from the pivot to the connection with the screw
    public static final double CLIMBER_SWING_BASE = 6 + 3/4.0; // Distance between pivot and start of screw
    public static final double CLIMBER_STARTING_DIST = 17 + 1/16.0; // Marked in sharpie on the screw (You may need to squint)
    public static final double CLIMBER_SWING_ROTS_PER_INCH = 11.0; // Distance that the screw travels up after each rotation of the motor


    private ClimberConstants() {
        throw new AssertionError();
    }
}
