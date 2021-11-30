package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public final class DriveTrain {
    private final WPI_TalonSRX leftMotor;
    private final WPI_TalonSRX rightMotor;

    public DriveTrain() {
        leftMotor = new WPI_TalonSRX(Constants.LEFT_MOTOR_PORT);
        rightMotor = new WPI_TalonSRX(Constants.RIGHT_MOTOR_PORT);

        // Configure motors
        leftMotor.configFactoryDefault();
        rightMotor.configFactoryDefault();
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
        leftMotor.configAllSettings(config);
        rightMotor.configAllSettings(config);

        leftMotor.setInverted(false);
        leftMotor.setSensorPhase(true);
        leftMotor.setNeutralMode(NeutralMode.Brake);
        leftMotor.setSelectedSensorPosition(0, 0, 30);

        rightMotor.setInverted(true);
        rightMotor.setSensorPhase(true);
        rightMotor.setNeutralMode(NeutralMode.Brake);
        rightMotor.setSelectedSensorPosition(0, 0, 30);

        stopMotors();
    }

    public int getLeftEncoderTicks() {
        return (int) leftMotor.getSelectedSensorPosition();
    }

    public int getRightEncoderTicks() {
        return (int) rightMotor.getSelectedSensorPosition();
    }

    public void driveMotors(double left, double right) {
        left = Utils.clamp(left, -1, 1);
        right = Utils.clamp(right, -1, 1);
        leftMotor.set(ControlMode.PercentOutput, left);
        rightMotor.set(ControlMode.PercentOutput, right);
    }

    public void stopMotors() {
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }
}
