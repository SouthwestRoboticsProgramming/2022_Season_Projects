package frc.robot.constants;

public class ShooterConstants {

    /* Settings */

    // FIXME: @rmheuer Add all of these to suffleboard and retreive them in the shooter class.
    public static final double SHOOTER_MAX_ACCEL = 2; // Rots/second/second
    public static final double SHOOTER_KS = 0; // How much it takes to just move it
    public static final double SHOOTER_KV = 0.1;
    public static final double SHOOTER_KA = 0.1;

    public static final double HOOD_KP = 0;
    public static final double HOOD_KI = 0;
    public static final double HOOD_KD = 0;

    public static final double INDEX_SPEED = 1;
    public static final int INDEX_TIME = 50 * 1; // 50 * seconds

    /* Hardware */
    public static final int FLYWHEEL_MOTOR_ID = 30; //FIXME
    public static final int INDEX_MOTOR_ID = 33;
    public static final int HOOD_MOTOR_ID = 32; //FIXME

    public static final double ROTS_PER_DEGREE = 2;
    public static final double MIN_ANGLE = 20;
    public static final double MAX_ANGLE = 50;
    
}
