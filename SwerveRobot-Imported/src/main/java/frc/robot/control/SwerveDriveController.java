package frc.robot.control;

import frc.robot.drive.SwerveDrive;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.Constants;

public class SwerveDriveController {
    private final SwerveDrive drive;
    private final Input input;
    private final PIDController rotPID;

    private double targetAngle = 0.0;
    //private final PIDController rotPID;

    // Sets initial state of robot (In this case, staying still)
    private ChassisSpeeds speeds = new ChassisSpeeds(0.0, 0.0, 0.0);
    
    public SwerveDriveController(SwerveDrive drive, Input input) {
        this.drive = drive;
        this.input = input;
        rotPID = new PIDController(Constants.STABILIZATION_KP, Constants.STABILIZATION_KI, Constants.STABILIZATION_KD);
        rotPID.enableContinuousInput(-180, 180);
    }

    public void swerveInit(){
        drive.zeroGyro();
        
        // double startingAngle = Constants.STARTING_WHEEL_ANGLE;
        // drive.setWheelTargetAngle(startingAngle);
        drive.update(speeds);
    }
    
    public void update() {
        double driveX = input.getDriveX();
        double driveY = input.getDriveY();
        double rot = input.getRot();
        Rotation2d currentAngle = drive.getGyroscopeRotation();
        
        if (Math.abs(driveX) < Constants.JOYSTICK_DEAD_ZONE) {
            driveX = 0;
        }

        if (Math.abs(driveY) < Constants.JOYSTICK_DEAD_ZONE) {
            driveY = 0;
        }
        
        double targetRot = rot * Constants.MAX_ROTATION_SPEED;
        
        if (Math.abs(rot) < Constants.JOYSTICK_DEAD_ZONE) {
            if(Math.abs(currentAngle.getDegrees()-targetAngle) < 200 && Math.abs(currentAngle.getDegrees()-targetAngle) > 2){
                targetRot = rotPID.calculate(currentAngle.getDegrees(),targetAngle);
            } else {
                targetRot = 0;
            }
        } else {
            targetAngle = currentAngle.getDegrees();
        }


        //Rotation2d targetAngle = Rotation2d(rot * Constants.MAX_ROTATION_SPEED / 50);


        double fieldRelativeX = driveX * Constants.MAX_VELOCITY;
        double fieldRelativeY = driveY * Constants.MAX_VELOCITY;

        // Convert motion goals to ChassisSpeeds object
        speeds = ChassisSpeeds.fromFieldRelativeSpeeds(fieldRelativeX, fieldRelativeY, targetRot, currentAngle);
        drive.update(speeds);

        System.out.println("Current Angle: " + currentAngle);
        System.out.println("Target Angle: " + targetAngle);
    }
}
