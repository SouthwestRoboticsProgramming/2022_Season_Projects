package frc.robot.constants;

public class IntakeConstants {

    /* Settings */
    public static final double INTAKE_MAX_SPEED = 2; // Rots/second/second
    public static final double INTAKE_KS = 0;
    public static final double INTAKE_KA = 0;
    public static final double INTAKE_KV = 0;

    public static final double INTAKE_TIME = 50 * 1; // 50 * Seconds

    /* Hardware */
    public static final int INTAKE_MOTOR_ID = 50943; //FIXME
    public static final int INTAKE_LIFT_ID = 432; //FIXME

    private IntakeConstants() {
        throw new AssertionError();
    }
}
