package frc.robot;

public final class Utils {
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private Utils() {
        throw new AssertionError();
    }
}
