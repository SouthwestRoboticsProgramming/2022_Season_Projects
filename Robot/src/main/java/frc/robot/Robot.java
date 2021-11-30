package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.lib.ADIS16448_IMU;
import frc.lib.ADIS16448_IMU.IMUAxis;

public final class Robot extends TimedRobot {
  private DriveTrain driveTrain;
  private DriveController driveController;
  private VisualizerCommunicator visualizer;
  private Gyro gyro;
  private Localizer localizer;

  @Override
  public void robotInit() {
    XboxController controller = new XboxController(0);
    Input input = new Input(controller);
    
    driveTrain = new DriveTrain();
    driveController = new DriveController(driveTrain, input);

    visualizer = new VisualizerCommunicator();

    gyro = new ADIS16448_IMU(IMUAxis.kZ, edu.wpi.first.wpilibj.SPI.Port.kMXP, 10);
    localizer = new Localizer(driveTrain, gyro);
  }

  @Override
  public void robotPeriodic() {
    localizer.update();

    if (visualizer.connected()) {
      visualizer.setMemUsage();
      visualizer.setTPS(50);

      visualizer.setPredictedAngle(localizer.getRotationRadians());
      visualizer.setPredictedX(localizer.getX() * 100); // Visualizer expects measurements in centimeters
      visualizer.setPredictedY(localizer.getY() * 100);
    }
  }

  @Override
  public void disabledInit() {
    driveTrain.stopMotors();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    driveController.update();
  }

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}