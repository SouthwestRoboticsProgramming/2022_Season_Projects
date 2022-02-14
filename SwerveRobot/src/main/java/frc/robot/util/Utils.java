package frc.robot.util;

import edu.wpi.first.math.geometry.Rotation2d;

public final class Utils {
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    // Maps a value from one range to another -> Used for removing controller deadzone jump
    public static double map(double value, double min, double max, double newMin, double newMax) {
        return (value - min) / (max - min) * (newMax - newMin) + newMin;
    }

    // Makes the angle be in the interval [-PI, PI)
    public static double normalizeAngle(double angle) {
        return -Math.PI + ((Math.PI * 2 + ((angle + Math.PI) % (Math.PI * 2))) % (Math.PI * 2));
    }

    // Makes the angle be in the interval [-180, 180)
    public static double normalizeAngleDegrees(double angle) {
        return -180 + ((360 + ((angle + 180) % 360)) % 360);
    }

    // Makes the angle be in the interval (Equivalent) [-180,180)
    public static Rotation2d normalizeRotation2d(Rotation2d angle) {
        return new Rotation2d(-180 + ((180 * 2 + ((angle.getDegrees() + 180) % (180 * 2))) % (180 * 2)));
    }

    public static Rotation2d normalizeModuleState(Rotation2d angle) {
        if(angle.getRadians()<0) {
            return new Rotation2d(angle.getRadians()+Math.PI);
        } else {
            return new Rotation2d(angle.getRadians());
        }
    }

    public static double fixCurrentAngle(double degreesAngle) {
        if(degreesAngle<90){return degreesAngle;}
        if(degreesAngle>270){return degreesAngle-360;}
        if(degreesAngle<270){return degreesAngle-180;} else {return 0;}
    }

    public static double normalizeCameraTurret(double degrees) {
        if (degrees > 360) {
            return degrees - 360;
        }
        return degrees;
    }


    private Utils() {
        throw new AssertionError();
    }
}
