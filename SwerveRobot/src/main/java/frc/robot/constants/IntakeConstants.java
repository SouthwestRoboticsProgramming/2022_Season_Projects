package frc.robot.constants;

public class IntakeConstants {

    /* Settings */
    public static final double INTAKE_MAX_SPEED = 2; // Rots/second/second
    public static final double INTAKE_KS = 0;
    public static final double INTAKE_KV = 0.1;
    public static final double INTAKE_KA = 0.1;

    public static final double INTAKE_TIME = 50 * 1; // 50 * Seconds
    public static final double INTAKE_RETRACTION_SPEED = 0.15; // Percent between 0 and 1;

    /* Hardware */
    public static final int INTAKE_MOTOR_ID = 40;
    public static final int INTAKE_LIFT_ID = 41; //FIXME

    private IntakeConstants() {
        throw new AssertionError();
    }
}
