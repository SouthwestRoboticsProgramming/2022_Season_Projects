package frc.robot.constants;

public final class AutonomousConstants {
    public static final double AUTO_DRIVE_SCALE = 1; // Speed for the robot to drive in auto, percentage of max speed
    public static final double AUTO_TARGET_THRESHOLD = 0.1; // Distance that is considered to be at the target (meters)

    private AutonomousConstants() {
        throw new AssertionError();
    }
}
