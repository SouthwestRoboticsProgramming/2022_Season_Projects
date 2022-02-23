package frc.robot.constants;

public class ShooterConstants {

    /* Settings */

    // FIXME: @rmheuer Add all of these to suffleboard and retreive them in the shooter class.

    public static final double SHOOTER_IDLE_VELOCITY = 2.5 * 2048; // Should be just enought to eject the ball from the robot when the hood is all the way down.

    public static final double FLYWHEEL_KF = 0; // How much it takes to just move it
    public static final double FLYWHEEL_KP = 0.1;

    public static final double FLYWHEEL_KI = 0;
    public static final double FLYWHEEL_KD = 0.01;

    public static final double INDEX_KF = 0; // How much it takes to just move it
    public static final double INDEX_KP = 0.1;
    public static final double INDEX_KI = 0;
    public static final double INDEX_KD = 0;
    
    public static final double HOOD_KF = 0; 
    public static final double HOOD_KP = 5;
    public static final double HOOD_KI = 0;
    public static final double HOOD_KD = 0.6;

    public static final double INDEX_SPEED = 1;
    public static final int INDEX_TIME = 50 * 1; // 50 * seconds

    /* Hardware */
    public static final int FLYWHEEL_MOTOR_ID = 30; //FIXME
    public static final int INDEX_MOTOR_ID = 33;
    public static final int HOOD_MOTOR_ID = 31; //FIXME

    public static final int HOOD_LIMIT_CHANNEL = 0;

    public static final double ROTS_PER_MIN_MAX = 40/22;
    public static final double TICKS_PER_ROT = 177.6;
    
}
