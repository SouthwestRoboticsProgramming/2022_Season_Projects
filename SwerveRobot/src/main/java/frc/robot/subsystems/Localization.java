package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.ShuffleWood;

public class Localization extends SubsystemBase {
  private static AHRS gyro;

  public Localization(AHRS gyro) {
    Localization.gyro = gyro;

    /* Steps

    1. Get gyro angle
    2. Get camera turret angle
    3. Find the points that work
    4. Narrow down points using starting box
    5. Check that result is possible

    */

  }

  @Override
  public void periodic() {
    double angle = gyro.getYaw();
    ShuffleWood.set("Gyro Yaw", angle);
    ShuffleWood.set("Random number", Math.random() * 1000);
  }
}