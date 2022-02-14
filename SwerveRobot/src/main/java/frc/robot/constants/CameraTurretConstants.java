package frc.robot.constants;

public class CameraTurretConstants {

    /* Settings */
    public static final double CAMERA_TURRET_KP = 0.1;
    public static final double CAMERA_TURRET_KI = 0;
    public static final double CAMERA_TURRET_KD = 0;
    public static final double CAMERA_TURRET_MAX_TURN_PERCENT = 0.15;

    /* Hardware */
    public static final int CAMERA_TURRET_MOTOR_ID = 30;
    public static final double CAMERA_TURRET_ENCODER_TICKS_PER_ROTATION = 177.6;

    private CameraTurretConstants() {
        throw new AssertionError();
    }
}
