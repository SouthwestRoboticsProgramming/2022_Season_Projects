package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.lib.ADIS16448_IMU;
import frc.lib.ADIS16448_IMU.IMUAxis;
import frc.robot.lidar.LidarInterface;
import frc.robot.path.PathFollower;
import frc.robot.path.Point;
import frc.robot.taskmanager.client.Coprocessor;

public final class Robot extends TimedRobot {
  private DriveTrain driveTrain;
  private DriveController driveController;
  private VisualizerCommunicator visualizer;
  private Gyro gyro;
  private Localizer localizer;
  private PathFollower pathFollower;
  private List<Point> path;
  private Coprocessor rpi;
  private LidarInterface lidar;

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

    // Keep trying to connect until it is successful
    // There is probably a better way to do this, but I don't know any
    rpi = new Coprocessor(Constants.RPI_ADDRESS, Constants.RPI_PORT);
    while (true) {
      try {
        rpi.connect();
        break;
      } catch (RuntimeException e) {
        System.out.println("Raspberry Pi has not yet connected");
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // Ignore
      }
    }
    System.out.println("Connected to Raspberry Pi");

    lidar = new LidarInterface(rpi, Constants.LIDAR_TASK_NAME);
    lidar.setScanCallback((scan) -> {
      System.out.println("Scan received");
    });
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

      visualizer.setPath(path);
    }

    rpi.flushNetwork();
  }

  @Override
  public void disabledInit() {
    driveTrain.stopMotors();
    lidar.stopScan();
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void autonomousInit() {
    pathFollower = new PathFollower(localizer, driveTrain, 0.3, 0.03, 15, 45);

    //pathFollower.setPath(path);
    lidar.startScan();
  }

  @Override
  public void autonomousPeriodic() {
    //pathFollower.update();
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