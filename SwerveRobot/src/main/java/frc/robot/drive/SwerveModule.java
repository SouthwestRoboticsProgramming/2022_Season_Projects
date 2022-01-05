package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import frc.robot.Constants;
import frc.robot.util.Utils;

import static frc.robot.Constants.*;

// This was pretty much copied from the mini robot DriveTrain class,
// so it probably doesn't work.
// Wheel movements are commented out to prevent accidentally damaging the modules
public class SwerveModule {
    private final WPI_TalonSRX driveMotor;
    private final WPI_TalonSRX turnMotor;
    private final CANCoder canCoder;
    private final double canOffset;
    private final PIDController turnPID;



    public SwerveModule(int drivePort, int turnPort, int canPort ,double cancoderOffset) {

        driveMotor = new WPI_TalonSRX(drivePort);
        turnMotor = new WPI_TalonSRX(turnPort);
        canCoder = new CANCoder(canPort);
        canOffset = cancoderOffset;

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
        driveMotor.configAllSettings(config);
        turnMotor.configAllSettings(config);

        driveMotor.setNeutralMode(NeutralMode.Brake);
        driveMotor.setSelectedSensorPosition(0, 0, 30);
        driveMotor.stopMotor();

        //turnMotor.setNeutralMode(NeutralMode.Brake);
        turnMotor.setSelectedSensorPosition(0, 0, 30);
        turnMotor.stopMotor();

        turnPID = new PIDController(WHEEL_TURN_KP, WHEEL_TURN_KI, WHEEL_TURN_KD);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
        turnPID.setTolerance(WHEEL_TOLERANCE,WHEEL_DERVIVATIVE_TOLERANCE);
    }

    public void canCoderConfig() {
        CANCoderConfiguration config = new CANCoderConfiguration();
        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        config.magnetOffsetDegrees = Math.toDegrees(canOffset);
        config.sensorDirection = Constants.CANCODER_DIRECTION;

        canCoder.configAllSettings(config);
    }


    public void drive(double amount) {
        amount = Utils.clamp(amount, -1, 1);
        
        driveMotor.set(ControlMode.PercentOutput, amount);
    }

    public void update(SwerveModuleState swerveModuleState) {

        Rotation2d canRotation = new Rotation2d(canCoder.getAbsolutePosition());
        SwerveModuleState moduleState = SwerveModuleState.optimize(swerveModuleState, canRotation);
        double currentAngle = canCoder.getAbsolutePosition();
        Rotation2d targetAngle = moduleState.angle;
        double targetSpeed = moduleState.speedMetersPerSecond;

        // Turn to target angle
        double turnAmount = turnPID.calculate(currentAngle,targetAngle.getDegrees());
        







        // Spin the motors
        turnMotor.set(ControlMode.PercentOutput, turnAmount);






        // if (printAngle) {
        //     System.out.println(rotPos);
        // }

        // double target = targetAngle;

        // // System.out.print("Target angle: ");
        // // System.out.print(target);
        
        
        // double amount = turnPID.calculate(currentAngle, target);
        // amount = Utils.clamp(amount, -1, 1);
        // //double amount = 0.05 * Math.signum(Utils.normalizeAngle(target-currentAngle));
        // turnMotor.set(ControlMode.PercentOutput, amount);

        //turnMotor.set(ControlMode.PercentOutput, 0.25);
    }

    public void disable() {
        turnPID.reset();
    }
}
