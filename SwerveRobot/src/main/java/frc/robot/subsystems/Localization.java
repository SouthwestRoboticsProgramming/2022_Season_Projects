package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// https://www.desmos.com/calculator/w5x76wa3yd

/* ALL ANGLES IN DEGREES {-180,180} UNLESS NOTED  */
public class Localization extends SubsystemBase {
  private final AHRS gyro;
  private final CameraTurret cameraTurret;

  private double x, y;

  public Localization(AHRS gyro) {
    this.gyro = gyro;
    cameraTurret = new CameraTurret();
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  @Override
  public void periodic() {
    // ShuffleWood.set("Gyro Yaw", angle);
    // ShuffleWood.set("Random number", Math.random() * 1000);
    
    /* Steps *
    
    1. Get gyro angle
    2. Get camera turret angle and distance
    3. Find the points that work
    4. Narrow down points using odomery and rough location
    5. Check that result is possible
    
    */
    double gyroAngle = Math.toDegrees(Utils.normalizeAngle(Math.toRadians(gyro.getYaw)));
    double cameraAngle = cameraTurret.getAngle();
    double cameraDistance = cameraTurret.getDistance();

    double angleDiff = Math.toRadians(cameraAngle - gyroAngle);
    y = cameraDistance * Math.sin(angleDiff);
    x = cameraDistance * Math.cos(angleDiff);
  }
}