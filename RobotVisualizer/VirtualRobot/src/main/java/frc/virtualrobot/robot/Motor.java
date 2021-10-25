package frc.virtualrobot.robot;

public class Motor {
    public static final double TICKS_PER_ROTATION = 180;

    private double rotation;
    private double rotationPerSecond;

    public void setMovement(double rpm) {
        rotationPerSecond = rpm / 60.0;
    }

    public void update(double delta) {
        rotation += rotationPerSecond * delta;
    }

    public double getEncoderTicks() {
        return rotation / Math.PI / 2.0 * TICKS_PER_ROTATION;
    }
}
