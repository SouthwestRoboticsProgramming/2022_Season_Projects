package frc.robot;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;

public final class Localizer {
    private final DriveTrain drive;
    private final Gyro gyro;
    private final DifferentialDriveOdometry odometry;
    private Pose2d pose;

    public Localizer(DriveTrain drive, Gyro gyro) {
        this.drive = drive;
        this.gyro = gyro;
        odometry = new DifferentialDriveOdometry(getGyroAngle());
    }

    public void update() {
        double leftRotations = drive.getLeftEncoderTicks() / Constants.ENCODER_TICKS_PER_ROTATION;
        double rightRotations = drive.getRightEncoderTicks() / Constants.ENCODER_TICKS_PER_ROTATION;

        double leftDistance = leftRotations * Constants.WHEEL_CIRCUMFERENCE;
        double rightDistance = rightRotations * Constants.WHEEL_CIRCUMFERENCE;

        pose = odometry.update(getGyroAngle(), leftDistance, rightDistance);
    }

    public double getRotationDegrees() {
        return pose.getRotation().getDegrees();
    }

    public double getRotationRadians() {
        return pose.getRotation().getRadians();
    }

    public double getX() {
        return pose.getX();
    }

    public double getY() {
        return pose.getY();
    }

    private Rotation2d getGyroAngle() {
        return Rotation2d.fromDegrees(gyro.getAngle());
    }
}
