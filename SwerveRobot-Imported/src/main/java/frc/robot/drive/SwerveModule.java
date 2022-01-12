package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.MathUtil;
//import edu.wpi.first.math.controller.ArmFeedforward;
import frc.robot.Constants;
import frc.robot.util.Utils;

import static frc.robot.Constants.*;

// This was pretty much copied from the mini robot DriveTrain class,
// so it probably doesn't work.
// Wheel movements are commented out to prevent accidentally damaging the modules
public class SwerveModule {
    private static final double WHEEL_TURN_KF = 0;
    private final WPI_TalonSRX driveMotor;
    private final WPI_TalonSRX turnMotor;
    private final CANCoder canCoder;
    private final double canOffset;
    private final PIDController turnPID;
    private final SimpleMotorFeedforward turnFeed;
    //private final ArmFeedforward turnFeed;

    // TEMPORARY, TODO: REMOVE
    private boolean printDebugging;

    

    public SwerveModule(int drivePort, int turnPort, int canPort ,double cancoderOffset) {

        driveMotor = new WPI_TalonSRX(drivePort);
        turnMotor = new WPI_TalonSRX(turnPort);
        canCoder = new CANCoder(canPort);
        canOffset = cancoderOffset;

        // TEMPORARY
        printDebugging = turnPort == TURN_PORT_1;

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
        turnPID.enableContinuousInput(-180, 180);
        turnPID.setTolerance(WHEEL_TURN_TOLERANCE);
        //turnPID.setTolerance(WHEEL_TOLERANCE,WHEEL_DERVIVATIVE_TOLERANCE);

        turnFeed = new SimpleMotorFeedforward(1, 0);

        //turnFeed = new ArmFeedforward(cancoderOffset, cancoderOffset, cancoderOffset); // TODO: Figure this out
    }

    public void canCoderConfig() {
        CANCoderConfiguration config = new CANCoderConfiguration();
        config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
        config.magnetOffsetDegrees = Math.toDegrees(canOffset);
        config.sensorDirection = Constants.CANCODER_DIRECTION;

        canCoder.configAllSettings(config);
    }

    public void update(SwerveModuleState swerveModuleState) {

        Rotation2d canRotation = new Rotation2d(canCoder.getAbsolutePosition());
        SwerveModuleState moduleState = SwerveModuleState.optimize(swerveModuleState, canRotation);
        Rotation2d currentAngle = new Rotation2d(Math.toRadians(canCoder.getAbsolutePosition()));
        Rotation2d normalizedAngle = Utils.normalizeRotation2d(currentAngle);
        Rotation2d targetAngle = moduleState.angle;
        double targetSpeed = moduleState.speedMetersPerSecond;

        // Turn to target angle
        double turnAmount = turnPID.calculate(normalizedAngle.getDegrees(),0);//This 0 should be targetAngle
        turnAmount = MathUtil.clamp(turnAmount,-1.0,1.0);

        // Drive the target speed
        double driveAmount = targetSpeed / MAX_VELOCITY;
        driveAmount = MathUtil.clamp(driveAmount,-1.0,1.0);

        // Spin the motors
        turnMotor.set(ControlMode.PercentOutput, turnAmount); 
        driveMotor.set(ControlMode.PercentOutput, driveAmount);

        if(printDebugging){
            System.out.println(turnAmount);
            System.out.println(normalizedAngle);
        }
    }

    public void disable() {
        turnPID.reset();
    }
}
