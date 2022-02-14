package frc.robot.subsystems;

import frc.robot.util.ShuffleWood;
import frc.robot.util.Utils;

import edu.wpi.first.math.controller.PIDController;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

import static frc.robot.constants.CameraTurretConstants.*;

public class CameraTurret extends Subsystem {
  private final TalonSRX motor;
  private final PIDController pid;
  private final Cameras cameras;

  private double target;
  private boolean isSweepback;
  
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

    this.cameras = cameras;
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
    double encoderAngle = getEncoderDegrees();
    double cameraAngle = cameras.getHubAngle();
    //ShuffleWood.show("Encoder Angle", encoderAngle);
    double rawTarget;
    if (cameraAngle != 360.0) {
      rawTarget = encoderAngle + cameraAngle;
    } else {
      rawTarget = encoderAngle;
    }
    target = Utils.normalizeCameraTurret(rawTarget);

    double calc = pid.calculate(encoderAngle, target);
    //ShuffleWood.show("pid", calc);
    if (pid.atSetpoint()) {
      isSweepback = !isSweepback;
      if (isSweepback) {
        target = -90;
      } else {
        target = 90;
      }
    }
    ShuffleWood.show("target", target);
    
    motor.set(ControlMode.PercentOutput, Utils.clamp(calc, -CAMERA_TURRET_MAX_TURN_PERCENT, CAMERA_TURRET_MAX_TURN_PERCENT));
  }
}
