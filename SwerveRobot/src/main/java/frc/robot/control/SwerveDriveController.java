package frc.robot.control;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import frc.robot.Constants;
import frc.robot.drive.SwerveDrive;
import frc.robot.util.ShuffleWood;
import frc.robot.util.Utils;

public class SwerveDriveController {
    private final SwerveDrive drive;
    private final Input input;
    private final PIDController rotPID;

    private double autoRot;
    private boolean autoControl;

    // Sets initial state of robot (In this case, staying still)
    private ChassisSpeeds speeds = new ChassisSpeeds(0.0, 0.0, 0.0);
    
    public SwerveDriveController(SwerveDrive drive, Input input) {
        this.drive = drive;
        this.input = input;
        rotPID = new PIDController(Constants.STABILIZATION_KP, Constants.STABILIZATION_KI, Constants.STABILIZATION_KD);
        rotPID.enableContinuousInput(-180, 180);
        autoControl = false;
    }

    public void swerveInit(){
        drive.zeroGyro();
        drive.update(speeds);
    }
    
    public void update() {
        double driveX = input.getDriveX();
        double driveY = input.getDriveY();
        double rot = input.getRot();
        Rotation2d currentAngle = drive.getGyroscopeRotation();

        ShuffleWood.show("currentAngle", currentAngle);
        
        if (Math.abs(driveX) < Constants.JOYSTICK_DEAD_ZONE) {
            driveX = 0;
        }
        if (Math.abs(driveY) < Constants.JOYSTICK_DEAD_ZONE) {
            driveY = 0;
        }
        if (Math.abs(rot) < Constants.JOYSTICK_DEAD_ZONE) {
            rot = 0;
        }
        
        // Eliminate deadzone jump

        if (driveX > 0) {
                driveX = Utils.map(driveX, Constants.JOYSTICK_DEAD_ZONE, 1, 0, 1);
        } else if (driveX < 0){
                driveX = -Utils.map(-driveX, Constants.JOYSTICK_DEAD_ZONE, 1, 0, 1);
        }
        if (driveY > 0) {
                driveY = Utils.map(driveY, Constants.JOYSTICK_DEAD_ZONE, 1, 0, 1);
        } else if (driveY <0){
                driveY = -Utils.map(-driveY, Constants.JOYSTICK_DEAD_ZONE, 1, 0, 1);
        }
        if (rot > 0) {
                rot = Utils.map(rot, Constants.JOYSTICK_DEAD_ZONE, 1, 0, 1);
        } else if (rot < 0) {
                rot = -Utils.map(-rot, Constants.JOYSTICK_DEAD_ZONE, 1, 0, 1);
        }

        if (autoControl) {
            rot = autoRot;
            System.out.println("Automatically Rotating");
        }

        double fieldRelativeX = driveX * Constants.MAX_VELOCITY;
        double fieldRelativeY = driveY * Constants.MAX_VELOCITY;
        double targetRot = rot * Constants.MAX_ROTATION_SPEED;
                            
        System.out.println(driveX + " " + driveY + " " + rot);
        
        // Convert motion goals to ChassisSpeeds object
        speeds = ChassisSpeeds.fromFieldRelativeSpeeds(fieldRelativeX, fieldRelativeY, targetRot, currentAngle);
        //speeds = new ChassisSpeeds(fieldRelativeX,fieldRelativeY,targetRot);
        drive.update(speeds);

        autoControl = false;
    }

    public void turnToTarget(double angleTargetDegrees) {
        double targetRotPercent = rotPID.calculate(drive.getGyroscopeRotation().getDegrees(),angleTargetDegrees);
        autoControl = true;
        this.autoRot = targetRotPercent;
    }
}
