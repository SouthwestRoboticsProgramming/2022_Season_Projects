package frc.robot.drive;

import static frc.robot.Constants.*;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

public class SwerveDrive {
    /*
     * Wheel Layout:
     * 
     * w1 ------- w2
     *  |         |
     *  |  ---->  |
     *  |         |
     * w4 ------- w3
     */
    private final SwerveModule w1, w2, w3, w4;
    private final AHRS navx;
    private final ChassisSpeeds chassisSpeeds;
    private double currentAngle; // In radians

    public SwerveDrive() {
        w1 = new SwerveModule(DRIVE_PORT_1, TURN_PORT_1, CAN_PORT_1);
        w2 = new SwerveModule(DRIVE_PORT_2, TURN_PORT_2, CAN_PORT_2);
        w3 = new SwerveModule(DRIVE_PORT_3, TURN_PORT_3, CAN_PORT_3);
        w4 = new SwerveModule(DRIVE_PORT_4, TURN_PORT_4, CAN_PORT_4);
        navx = new AHRS(SPI.Port.kMXP, (byte) 200);
        chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0); // Sets the default speed of the robot to zero
        currentAngle = 0;
    }

    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
        // Front Left
        new Translation2d(WHEEL_SPACING_FRONT_BACK / 2.0, WHEEL_SPACING_LEFT_RIGHT / 2.0),
        // Front Right
        new Translation2d(WHEEL_SPACING_FRONT_BACK / 2.0, -WHEEL_SPACING_LEFT_RIGHT / 2.0),
        // Back Left
        new Translation2d(-WHEEL_SPACING_FRONT_BACK / 2.0, WHEEL_SPACING_LEFT_RIGHT / 2.0),
        // Back Right
        new Translation2d(-WHEEL_SPACING_FRONT_BACK / 2.0, -WHEEL_SPACING_LEFT_RIGHT / 2.0)
    );

    public void setTargetRelative(x,y,relativeAngle) {
        chassisSeeds.fromFieldRelativeSpeeds(x,y,currentAngle + relativeAngle, currentAngle)
    }

    public void zeroGyroscope() {
        navx.zeroYaw();
    }

    public Rotation2d getGyroscopeRotation() {
        if (navx.isMagnetometerCalibrated()) {
            return Rotation2d.fromDegrees(navx.getAngle());
        }
        return Rotation2d.fromDegrees(360.0 - navx.getYaw());
    }

    public void setWheelTargetAngle(double angle) {
        w1.setTargetAngle(angle + OFFSET_1);
        w2.setTargetAngle(angle + OFFSET_2);
        w3.setTargetAngle(angle + OFFSET_3);
        w4.setTargetAngle(angle + OFFSET_4);
    }

    public boolean wheelsAtTargetAngle() {
        return w1.isAtTargetAngle()
            && w2.isAtTargetAngle()
            && w3.isAtTargetAngle()
            && w4.isAtTargetAngle();
    }

    public double[] getVelocity() {
        double[] velocity = new double[3];
        velocity[0] = navx.getVelocityX(); // May not work, experimental
        velocity[1] = navx.getVelocityY(); // May not work, experimental
        velocity[2] = Math.toRadians(navx.getRate()); //FIXME Check if this doesn't work, if it doesn't try this: https://github.com/kauailabs/navxmxp/issues/69
        return velocity;
    }

    public void driveWheels(double amount) {
        w1.drive(amount);
        w2.drive(amount);
        w3.drive(amount);
        w4.drive(amount);
    }

    public void update() {

        // Get current position and state of robot
        currentAngle = getGyroscopeRotation();

        // Calculate the movements
        setTargetRelative(/*Put stuff in here */);



        w1.update();
        w2.update();
        w3.update();
        w4.update();

    }

    public void disable() {
        w1.disable();
        w2.disable();
        w3.disable();
        w4.disable();
    }
}
