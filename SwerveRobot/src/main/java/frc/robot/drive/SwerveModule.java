package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.MathUtil;
import frc.robot.util.ShuffleWood;
import frc.robot.util.Utils;

import static frc.robot.constants.DriveConstants.*;


public class SwerveModule {

    private final WPI_TalonFX driveMotor;
    private final WPI_TalonSRX turnMotor;
    private final CANCoder canCoder;
    private final double canOffset;
    private final PIDController turnPID;

    

    public SwerveModule(int drivePort, int turnPort, int canPort ,double cancoderOffset) {

        driveMotor = new WPI_TalonFX(drivePort);
        turnMotor = new WPI_TalonSRX(turnPort);
        canCoder = new CANCoder(canPort);
        canOffset = cancoderOffset;

        TalonFXConfiguration driveConfig = new TalonFXConfiguration();
        driveMotor.configAllSettings(driveConfig);

        TalonSRXConfiguration turnConfig = new TalonSRXConfiguration();
        turnMotor.configAllSettings(turnConfig);
        
        driveMotor.setNeutralMode(NeutralMode.Brake);
        driveMotor.setSelectedSensorPosition(0, 0, 30);
        driveMotor.stopMotor();

        turnMotor.setNeutralMode(NeutralMode.Brake);
        turnMotor.setSelectedSensorPosition(0, 0, 30);
        turnMotor.stopMotor();

        CANCoderConfiguration canConfig = new CANCoderConfiguration();
        canConfig.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        canConfig.magnetOffsetDegrees = canOffset;
        canConfig.sensorDirection = CANCODER_DIRECTION;

        canCoder.configAllSettings(canConfig);

        turnPID = new PIDController(WHEEL_TURN_KP, WHEEL_TURN_KI, WHEEL_TURN_KD);
        turnPID.enableContinuousInput(-90, 90);
        turnPID.setTolerance(WHEEL_TOLERANCE.getDegrees());
    }

    public void update(SwerveModuleState swerveModuleState) {
        // turnPID.setP(ShuffleWood.getDouble("Wheel turn KP", WHEEL_TURN_KP));
        // turnPID.setI(ShuffleWood.getDouble("Wheel turn KI", WHEEL_TURN_KI));
        // turnPID.setD(ShuffleWood.getDouble("Wheel turn KD", WHEEL_TURN_KD));
        // ShuffleWood.show("P", turnPID.getP());
        // ShuffleWood.show("I", turnPID.getI());
        // ShuffleWood.show("D", turnPID.getD());
        //System.out.println("PID: " + turnPID.getP() + " " + turnPID.getI() + " " + turnPID.getD());

        Rotation2d canRotation = new Rotation2d(Math.toRadians(canCoder.getAbsolutePosition()));
        Rotation2d currentAngle = new Rotation2d(Math.toRadians(Utils.fixCurrentAngle(canCoder.getAbsolutePosition())));
        SwerveModuleState moduleState = SwerveModuleState.optimize(swerveModuleState, canRotation);
        Rotation2d targetAngle = moduleState.angle;
        double targetSpeed = moduleState.speedMetersPerSecond;

        // Turn to target angle
        double turnAmount = turnPID.calculate(currentAngle.getDegrees(),targetAngle.getDegrees());
        turnAmount = MathUtil.clamp(turnAmount,-1.0,1.0);

        // Drive the target speed
        double driveAmount = targetSpeed / ROBOT_MAX_VELOCITY;
        driveAmount = MathUtil.clamp(driveAmount,-1.0,1.0);

        // Spin the motors
        if (!turnPID.atSetpoint())
            turnMotor.set(ControlMode.PercentOutput, turnAmount); 
        else
            turnMotor.set(ControlMode.PercentOutput, 0);
        driveMotor.set(ControlMode.PercentOutput, driveAmount);

    }

    public double getCanRotation() {
        return canCoder.getAbsolutePosition();
    }

    public void disable() {
        turnPID.reset();
    }
}
