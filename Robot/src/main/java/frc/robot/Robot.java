package frc.robot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.lib.ADIS16448_IMU;
import frc.lib.ADIS16448_IMU.IMUAxis;
import frc.messenger.client.MessengerClient;
import frc.robot.path.PathFollower;
import frc.robot.path.Point;
import frc.robot.util.Vec2d;

public final class Robot extends TimedRobot {
  private DriveTrain driveTrain;
  private DriveController driveController;
  private VisualizerCommunicator visualizer;
  private Gyro gyro;
  private Localizer localizer;
  private PathFollower pathFollower;
  private List<Point> path;
  private MessengerClient msg;
  private Input input;

  @Override
  public void robotInit() {
    XboxController controller = new XboxController(0);
      input = new Input(controller);
    
    driveTrain = new DriveTrain();
    driveController = new DriveController(driveTrain, input);

    visualizer = new VisualizerCommunicator();

    gyro = new ADIS16448_IMU(IMUAxis.kZ, SPI.Port.kMXP, 10);
    localizer = new Localizer(driveTrain, gyro);

    path = new ArrayList<>();
    path.add(new Point(1, 0));
    path.add(new Point(1, 1));
    path.add(new Point(0, 0));

    msg = new MessengerClient(Constants.RPI_ADDRESS, Constants.RPI_PORT, "RoboRIO");
    msg.listen("Vision:Xangle");
    msg.setCallback(this::messageCallback);
  }

  @Override
  public void robotPeriodic() {
    msg.read();

    localizer.update();

    try {
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      DataOutputStream d = new DataOutputStream(b);
      d.writeDouble(localizer.getX());
      d.writeDouble(localizer.getY());
      d.writeDouble(localizer.getRotationRadians());

      msg.sendMessage("RoboRIO:Location", b.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
    }

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

    //pathFollower.setPath(path);
  }

  private double visionAngle = 0;

  @Override
  public void autonomousPeriodic() {
    //pathFollower.update();
    //if (pathFollower.isDone()) {
    //  pathFollower.setPath(path);
      double gyroAngle = gyro.getAngle()%360;
      double visionDiff = gyroAngle + visionAngle;

      if (visionDiff>5){
        driveTrain.driveMotors(.3,-.3);
      } else if (visionDiff<-5){
        driveTrain.driveMotors(-.3,.3);
      } else {
        driveTrain.driveMotors(.3, .3);
      }
    }


  private void messageCallback(String type, byte[] data) {
    if (type.equals("Vision:Xangle")) {
      try {
      ByteArrayInputStream b = new ByteArrayInputStream(data);
      DataInputStream in = new DataInputStream(b);

      visionAngle = in.readDouble();

      in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    Vec2d drive = driveController.update();

    if (this.input.pointAtTarget()) {
      double gyroAngle = gyro.getAngle()%360;
      double visionDiff = gyroAngle + visionAngle;

      if (visionDiff>5){
        driveTrain.driveMotors(drive.x + .2, drive.y - .2);
      } else if (visionDiff<-5){
        driveTrain.driveMotors(drive.x - .2, drive.y + .2);
      } else {
        driveTrain.driveMotors(drive.x, drive.y);
      }
    } else {
      driveTrain.driveMotors(drive.x, drive.y);
    }
  }

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}
}