package frc.robot.util;

public final class Utils {
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    // Makes the angle be in the interval [-PI, PI)
    public static double normalizeAngle(double angle) {
        return -Math.PI + ((Math.PI * 2 + ((angle + Math.PI) % (Math.PI * 2))) % (Math.PI * 2));
    }

    // Makes the angle be in the interval [-180, 180)
    public static double normalizeAngleDegrees(double angle) {
        return -180 + ((180 * 2 + ((angle + 180) % (180 * 2))) % (180 * 2));
    }

    private Utils() {
        throw new AssertionError();
    }
}
