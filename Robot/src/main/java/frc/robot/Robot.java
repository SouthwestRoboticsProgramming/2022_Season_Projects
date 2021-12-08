package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.lib.ADIS16448_IMU;
import frc.lib.ADIS16448_IMU.IMUAxis;
import frc.robot.path.PathFollower;
import frc.robot.path.Point;

public final class Robot extends TimedRobot {
  private DriveTrain driveTrain;
  private DriveController driveController;
  private VisualizerCommunicator visualizer;
  private Gyro gyro;
  private Localizer localizer;
  private PathFollower pathFollower;
  private List<Point> path;

  @Override
  public void robotInit() {
    XboxController controller = new XboxController(0);
    Input input = new Input(controller);
    
    driveTrain = new DriveTrain();
    driveController = new DriveController(driveTrain, input);

    visualizer = new VisualizerCommunicator();

    gyro = new ADIS16448_IMU(IMUAxis.kZ, SPI.Port.kMXP, 10);
    localizer = new Localizer(driveTrain, gyro);

    path = new ArrayList<>();
    path.add(new Point(1, 0));
    path.add(new Point(1, 1));
    path.add(new Point(0, 0));
  }

  @Override
  public void robotPeriodic() {
    localizer.update();

    //System.out.println(driveTrain.getLeftEncoderTicks() + " | " + driveTrain.getRightEncoderTicks() );

    if (visualizer.connected()) {
      visualizer.setMemUsage();
      visualizer.setTPS(50);

      visualizer.setPredictedAngle(localizer.getRotationRadians());
      visualizer.setPredictedX(localizer.getX() * 100); // Visualizer expects measurements in centimeters
      visualizer.setPredictedY(localizer.getY() * 100);

      visualizer.setPath(path);
    }
  }

  @Override
  public void disabledInit() {
    driveTrain.stopMotors();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void autonomousInit() {
    pathFollower = new PathFollower(localizer, driveTrain, 0.3, 0.03, 15, 45);

    pathFollower.setPath(path);
  }

  @Override
  public void autonomousPeriodic() {
    pathFollower.update();
    if (pathFollower.isDone()) {
      pathFollower.setPath(path);
    }
  }

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