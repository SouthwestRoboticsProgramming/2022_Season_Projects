package frc.robot.control;

import frc.robot.drive.SwerveDrive;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import frc.robot.Constants;

public class SwerveDriveController {
    private final SwerveDrive drive;
    private final Input input;

    // Sets initial state of robot (In this case, staying still)
    private ChassisSpeeds speeds = new ChassisSpeeds(0.0, 0.0, 0.0);
    
    public SwerveDriveController(SwerveDrive drive, Input input) {
        this.drive = drive;
        this.input = input;
    }

    public void swerveInit(){
        drive.zeroGyroscope();
        
        // double startingAngle = Constants.STARTING_WHEEL_ANGLE;
        // drive.setWheelTargetAngle(startingAngle);
        drive.update(speeds);
    }

    public void update() {
        double driveX = input.getDriveX();
        double driveY = input.getDriveY();
        double rot = input.getRot();

        Rotation2d currentAngle = drive.getGyroscopeRotation();

        double fieldRelativeX = driveX * Constants.MAX_VELOCITY;
        double fieldRelativeY = driveY * Constants.MAX_VELOCITY;
        double targetRot = rot * Constants.MAX_ROTATION_SPEED;

        // Convert motion goals to ChassisSpeeds object
        speeds = ChassisSpeeds.fromFieldRelativeSpeeds(fieldRelativeX, -fieldRelativeY, -targetRot, currentAngle);
        drive.update(speeds);


        // TEMPORARY
        System.out.println(" ***** Debugging Controller ***** ");
        System.out.println("Drive X: " + driveX);
        System.out.println("Drive Y: " + driveY);
        System.out.println("Drive rotation: " + rot);
        System.out.println("Gyro angle: " + currentAngle);
        System.out.println("Field relative X: " + fieldRelativeX);
        System.out.println("Field relative Y: " + fieldRelativeY);
        System.out.println("Target rotation speed: " + targetRot);
        System.out.println(" ******************************** ");
    }
}
