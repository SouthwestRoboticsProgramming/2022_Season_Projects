package frc.robot.drive;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.robot.util.ShuffleWood;

import static frc.robot.constants.DriveConstants.*;

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
    private final SwerveModuleOld w1, w2, w3, w4;
    private final AHRS navx;
    private final SwerveDriveOdometry odometry;

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
        // ShuffleWood.setInt("Swerve module 1", 0);
        // ShuffleWood.setInt("Swerve module 2", 1);
        // ShuffleWood.setInt("Swerve module 3", 2);
        // ShuffleWood.setInt("Swerve module 4", 3);

        // SwerveModuleInfo info1 = SWERVE_MODULES[ShuffleWood.getInt("Swerve module 1", 0)];
        // SwerveModuleInfo info2 = SWERVE_MODULES[ShuffleWood.getInt("Swerve module 2", 1)];
        // SwerveModuleInfo info3 = SWERVE_MODULES[ShuffleWood.getInt("Swerve module 3", 2)];
        // SwerveModuleInfo info4 = SWERVE_MODULES[ShuffleWood.getInt("Swerve module 4", 3)];
        SwerveModuleInfo info1 = SWERVE_MODULES[0];
        SwerveModuleInfo info2 = SWERVE_MODULES[1];
        SwerveModuleInfo info3 = SWERVE_MODULES[2];
        SwerveModuleInfo info4 = SWERVE_MODULES[3];

        w1 = new SwerveModuleOld(info1.getDriveId(), TURN_PORT_1, info1.getCanCoderId(), OFFSET_1 + info1.getCanCoderOffset());
        w2 = new SwerveModuleOld(info2.getDriveId(), TURN_PORT_2, info2.getCanCoderId(), OFFSET_2 + info2.getCanCoderOffset());
        w3 = new SwerveModuleOld(info3.getDriveId(), TURN_PORT_3, info3.getCanCoderId(), OFFSET_3 + info3.getCanCoderOffset());
        w4 = new SwerveModuleOld(info4.getDriveId(), TURN_PORT_4, info4.getCanCoderId(), OFFSET_4 + info4.getCanCoderOffset());
        this.navx = navx;
        odometry = new SwerveDriveOdometry(kinematics, navx.getRotation2d());

        navx.setAngleAdjustment(90);
    }

    public void zeroGyro() {
        navx.calibrate();
        navx.zeroYaw();
    }

    public Pose2d getOdometry() {
        return odometry.getPoseMeters();
    }

    public Rotation2d getGyroscopeRotation() {
        return Rotation2d.fromDegrees(-navx.getAngle());
    }

    public void update(ChassisSpeeds chassisSpeeds) {
        
        
        // Calculate the movements of each indevidual module
        SwerveModuleState[] moduleStates = kinematics.toSwerveModuleStates(chassisSpeeds);

        w1.update(moduleStates[2]);
        w2.update(moduleStates[0]);
        w3.update(moduleStates[1]);
        w4.update(moduleStates[3]);

        odometry.update(getGyroscopeRotation(), moduleStates);

        // System.out.printf("%3.3f %3.3f %3.3f %3.3f %n",
        //w1.getCanRotation(), w2.getCanRotation(), w3.getCanRotation(), w4.getCanRotation());
    }

    public void disable() {
        w1.disable();
        w2.disable();
        w3.disable();
        w4.disable();
    }
}
