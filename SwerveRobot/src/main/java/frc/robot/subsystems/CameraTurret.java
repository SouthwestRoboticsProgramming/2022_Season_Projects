package frc.robot.subsystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.ShuffleWood;
import frc.robot.util.Utils;

import static frc.robot.Constants.*;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

public class CameraTurret extends SubsystemBase {
  private final TalonSRX motor;
  private final PIDController pid;
  private final Cameras cameras;
  private double target;
  private boolean isSweepback;
  private double encoder;
  
  public CameraTurret(Cameras cameras) {
    motor = new TalonSRX(CAMERA_TURRET_MOTOR_ID);

    TalonSRXConfiguration config = new TalonSRXConfiguration();
    config.primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder;
    config.neutralDeadband = 0.001;
    config.slot0.kF = 0;
    config.slot0.kP = 0;
    config.slot0.kI = 0;
    config.slot0.kD = 0;
    config.slot0.closedLoopPeakOutput = 1;
    config.openloopRamp = 0.5;
    config.closedloopRamp = 0.5;
    motor.configAllSettings(config);

    pid = new PIDController(0.005, 0, 0);
    pid.setTolerance(4);
  }

  public double getAngle() {
    return 0;
  }

  public double getDistance() {
    return 22.3;
  }

  private double getEncoderDegrees() {
    return motor.getSelectedSensorPosition() / CAMERA_TURRET_ENCODER_TICKS_PER_ROTATION * 360;
  }

  @Override
  public void teleopPeriodic() {
    encoder = getEncoderDegrees();
    camera = cameras.getHubAngle();
    ShuffleWood.set("Encoder", encoder);
    if (camera != 360.0) {
      rawTarget = encoder + camera;
    } else {
      rawTarget = encoder;
    }
    target = Utils.noralizeCameraTurret(rawTarget);

    double calc = pid.calculate(encoder, target);
    ShuffleWood.set("pid", calc);
    if (pid.atSetpoint()) {
      isSweepback = !isSweepback;
      if (isSweepback) {
        target = -90;
      } else {
        target = 90;
      }
    }
    ShuffleWood.set("target", target);
    
    motor.set(ControlMode.PercentOutput, Utils.clamp(calc, -0.15, 0.15));
  }
}
