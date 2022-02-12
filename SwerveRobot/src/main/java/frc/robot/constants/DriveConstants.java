package frc.robot.constants;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.drive.SwerveModuleInfo;

public class DriveConstants {
    public static final double WHEEL_SPACING_FRONT_BACK = 0.4699;
    public static final double WHEEL_SPACING_LEFT_RIGHT = 0.4699;

    public static final int TURN_PORT_1 = 2;
    public static final int TURN_PORT_2 = 5;
    public static final int TURN_PORT_3 = 8;
    public static final int TURN_PORT_4 = 11;
    public static final int NAVX_PORT = 0;
    public static final double OFFSET_1 = -133.945 + 180;
    public static final double OFFSET_2 = -220.781 + 180;
    public static final double OFFSET_3 = -77.607;
    public static final double OFFSET_4 = -3.955;
    public static final boolean CANCODER_DIRECTION = false; // False = Counterclockwise   True = Clockwise

    public static final SwerveModuleInfo[] SWERVE_MODULES = {
        new SwerveModuleInfo(1, 3, 0),
        new SwerveModuleInfo(4, 6, 0),
        new SwerveModuleInfo(7, 9, 0),
        new SwerveModuleInfo(10, 12, 0)
    };

    public static final double STABILIZATION_KP = 0.027;
    public static final double STABILIZATION_KI = 0;
    public static final double STABILIZATION_KD = 0.0022;

    public static final double WHEEL_TURN_KP = 0.009;
    public static final double WHEEL_TURN_KI = 0; // Leave this at 0: There is no steady-state error in the system
    public static final double WHEEL_TURN_KD = 0.0001;
    
    public static final double ROBOT_MAX_VELOCITY = 4.11/*4.11*/;
    public static final double ROBOT_MAX_ROTATION_SPEED = 26.5; // Radians per second

    public static final Rotation2d WHEEL_TOLERANCE = Rotation2d.fromDegrees(1); // In degrees

    private DriveConstants() {
        throw new AssertionError();
    }
}
