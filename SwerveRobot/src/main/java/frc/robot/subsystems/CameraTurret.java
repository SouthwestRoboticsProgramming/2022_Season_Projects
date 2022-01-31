package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.*;

public class CameraTurret extends SubsystemBase {

  private final PIDController pid; //FIXME: Is this necessary? Probably not...
  private final Servo turretServo;

  public CameraTurret() {
    pid = new PIDController(CAMERA_TURRET_KP, CAMERA_TURRET_KI, CAMERA_TURRET_KD);
    turretServo = new Servo(CAMERA_TURRET_SERVO_ID);
    turretServo.setAngle(90);
  }

  public double getAngle() {
    return turretServo.getAngle(); //FIXME: Add the camera angle to this
  }

  public double getDistance() {
    return 22.3; //FIXME: Ryan do stuff to get cameras in cameras.java please
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    double currentAngle = turretServo.getAngle();

    turretServo.setAngle(currentAngle); //FIXME Figure out if I can do a 360 or if something else has to be done
  }
}
