// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveTrainSubsystem extends SubsystemBase {
  private TalonSRX simBackLeft = new TalonSRX(Constants.SIM_BACK_LEFT_ID);
  private TalonSRX simBackRight = new TalonSRX(Constants.SIM_BACK_RIGHT_ID);
  private TalonSRX simFrontLeft = new TalonSRX(Constants.SIM_FRONT_LEFT_ID);
  private TalonSRX simFrontRight = new TalonSRX(Constants.SIM_FRONT_RIGHT_ID);



  /** Creates a new DriveTrainSubsystem. */
  public DriveTrainSubsystem() {}

  public void rotateModule(int module, double angle) {

    TalonSRX simMotor = null;

    switch(module) {
      case 1:
        simMotor = simFrontLeft;
      case 2:
        simMotor = simFrontRight;
      case 3:
        simMotor = simBackRight;
      case 4:
        simMotor = simBackLeft;
      
    simMotor.set(ControlMode.PercentOutput, .3); //.3 Will be changed later

    }
  }

  public void rotateModuleAbsolute(int module, double angle) {

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
