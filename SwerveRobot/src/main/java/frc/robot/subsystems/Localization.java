package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.ShuffleWood;

public class Localization extends SubsystemBase {
  private static AHRS gyro;

  public Localization(AHRS gyro) {
    this.gyro = gyro;
  }

  @Override
  public void periodic() {
    double angle = gyro.getYaw();
    ShuffleWood.set("Gyro Yaw", angle);
    ShuffleWood.set("Random number", Math.random() * 1000);
  }
}