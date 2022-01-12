package frc.robot.util;

import edu.wpi.first.math.geometry.Rotation2d;

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

    public static Rotation2d normalizeRotation2d(Rotation2d angle) {
        return new Rotation2d(-Math.PI + ((Math.PI * 2 + ((angle.getRadians() + Math.PI) % (Math.PI * 2))) % (Math.PI * 2)));
    }



    private Utils() {
        throw new AssertionError();
    }
}
