package frc.robot.drive;

import static frc.robot.Constants.*;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

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
    private final SwerveDriveOdometry odometry;
    private Pose2d currentPose;

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

    public SwerveDrive(AHRS navx) {
        w1 = new SwerveModule(DRIVE_PORT_1, TURN_PORT_1, CAN_PORT_1, OFFSET_1);
        w2 = new SwerveModule(DRIVE_PORT_2, TURN_PORT_2, CAN_PORT_2, OFFSET_2);
        w3 = new SwerveModule(DRIVE_PORT_3, TURN_PORT_3, CAN_PORT_3, OFFSET_3);
        w4 = new SwerveModule(DRIVE_PORT_4, TURN_PORT_4, CAN_PORT_4, OFFSET_4);
        this.navx = navx;
        odometry = new SwerveDriveOdometry(kinematics, navx.getRotation2d());

    }

    public void zeroGyro() {
        navx.calibrate();
        navx.zeroYaw();
    }

    public void setPosition(Pose2d position){
        odometry.resetPosition(position, navx.getRotation2d());
    }

    public Rotation2d getGyroscopeRotation() {
        return Rotation2d.fromDegrees(navx.getYaw());
    }

    public void update(ChassisSpeeds chassisSpeeds) {
        
        
        // Calculate the movements of each indevidual module
        SwerveModuleState[] moduleStates = kinematics.toSwerveModuleStates(chassisSpeeds);
        
        currentPose = odometry.update(navx.getRotation2d(), moduleStates);

        w1.update(moduleStates[2]);
        w2.update(moduleStates[0]);
        w3.update(moduleStates[1]);
        w4.update(moduleStates[3]);

        // System.out.printf(
        //     "ANGLES: w1: %3.3f  w2: %3.3f  w3: %3.3f  w4: %3.3f %n",
        //     w1.getCanRotation(), w2.getCanRotation(), w3.getCanRotation(), w4.getCanRotation()
        // );
    }

    public void disable() {
        w1.disable();
        w2.disable();
        w3.disable();
        w4.disable();
    }
}
