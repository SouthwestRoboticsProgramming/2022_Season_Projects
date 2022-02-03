package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class CameraTurret extends SubsystemBase {

  private final PIDController pid; //FIXME: Is this necessary? Probably not...
  private final TalonSRX motor;

  public CameraTurret() {
    motor = new TalonSRX(CAMERA_TURRET_MOTOR_ID);
    pid = new PIDController(CAMERA_TURRET_KP, CAMERA_TURRET_KI, CAMERA_TURRET_KD);
  }

  public double getAngle() {
    return 2;
  }

  public double getDistance() {
    return 22.3; //FIXME: Ryan do stuff to get cameras in cameras.java please
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
