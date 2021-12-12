package frc.robot;

// All distance measurements should be in meters for consistency
public final class Constants {
    public static final int LEFT_MOTOR_PORT = 1;
    public static final int RIGHT_MOTOR_PORT = 2;
    public static final double ENCODER_TICKS_PER_ROTATION = 177.32;
    
    public static final double WHEEL_CIRCUMFERENCE = 0.0762 * Math.PI;

    public static final String RPI_ADDRESS = "10.21.29.17";
    public static final int RPI_PORT = 8341;
}
