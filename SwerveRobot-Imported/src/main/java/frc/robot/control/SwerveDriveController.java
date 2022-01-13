package frc.robot.control;

import frc.robot.drive.SwerveDrive;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
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
        drive.init();
        
        // double startingAngle = Constants.STARTING_WHEEL_ANGLE;
        // drive.setWheelTargetAngle(startingAngle);
        drive.update(speeds);
    }

    public void update() {
        double driveX = input.getDriveX();
        double driveY = input.getDriveY();
        double rot = input.getRot();

        if (Math.abs(driveX) < Constants.JOYSTICK_DEAD_ZONE) {
            driveX = 0;
        }

        if (Math.abs(driveY) < Constants.JOYSTICK_DEAD_ZONE) {
            driveY = 0;
        }
        if (Math.abs(rot) < Constants.JOYSTICK_DEAD_ZONE) {
            rot = 0;
        }


        Rotation2d currentAngle = drive.getGyroscopeRotation();

        double fieldRelativeX = driveX * Constants.MAX_VELOCITY;
        double fieldRelativeY = driveY * Constants.MAX_VELOCITY;
        double targetRot = rot * Constants.MAX_ROTATION_SPEED;

        // Convert motion goals to ChassisSpeeds object
        speeds = ChassisSpeeds.fromFieldRelativeSpeeds(fieldRelativeX, fieldRelativeY, targetRot, currentAngle);
        drive.update(speeds);

        System.out.println("X: " + driveX);
        System.out.println("Y: " + driveY);
        System.out.println("Rot: " + rot);

    }
}
