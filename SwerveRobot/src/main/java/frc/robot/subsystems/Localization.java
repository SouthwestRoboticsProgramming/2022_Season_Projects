package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.drive.SwerveDrive;
import frc.robot.util.Utils;

// https://www.desmos.com/calculator/w5x76wa3yd

/* ALL ANGLES IN DEGREES {-180,180} UNLESS NOTED  */
public class Localization extends Subsystem {
  private final AHRS gyro;
  private final CameraTurret cameraTurret;
  private final SwerveDrive drive;

  private double x, y;

  public Localization(AHRS gyro, /*CameraTurret cameraTurret,*/ SwerveDrive drive) {
    this.gyro = gyro;
    this.cameraTurret = null;
    this.drive = drive;
  }

  public double getX() {
    return drive.getOdometry().getX();
  }

  public double getY() {
    return drive.getOdometry().getY();
  }

  public Pose2d getOdometry() {
    return drive.getOdometry();
  }

  public void setPosition(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public void robotPeriodic() {
    // double gyroAngle = Math.toDegrees(Utils.normalizeAngle(Math.toRadians(gyro.getYaw())));
    // double cameraAngle = cameraTurret.getAngle();
    // double cameraDistance = cameraTurret.getDistance();

    // double angleDiff = Math.toRadians(cameraAngle - gyroAngle);
    // y = cameraDistance * Math.sin(angleDiff);
    // x = cameraDistance * Math.cos(angleDiff);
  }
}