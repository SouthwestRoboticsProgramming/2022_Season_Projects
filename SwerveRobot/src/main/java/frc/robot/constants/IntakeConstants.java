package frc.robot.constants;

public class IntakeConstants {

    /* Settings */
    public static final double INTAKE_FULL_VELOCITY = 3 * 2048;
    public static final double INTAKE_NEUTRAL_VELOCITY = 1 * 2048;

    public static final double INTAKE_TIME = 50 * 1; // 50 * Seconds
    public static final double INTAKE_RETRACTION_SPEED = 0.15; // Percent between 0 and 1;

    public static final double INTAKE_KF = 0;
    public static final double INTAKE_KP = 0.1;
    public static final double INTAKE_KI = 0;
    public static final double INTAKE_KD = 0;


    /* Hardware */
    public static final int INTAKE_MOTOR_ID = 40;
    public static final int INTAKE_LIFT_ID = 41; //FIXME

    private IntakeConstants() {
        throw new AssertionError();
    }
}
