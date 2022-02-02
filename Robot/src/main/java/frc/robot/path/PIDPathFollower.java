package frc.robot.path;

import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.DriveTrain;
import frc.robot.Localizer;
import frc.robot.ShuffleboardManager;
import frc.robot.util.Utils;

public class PIDPathFollower {
    private final Localizer loc;
    private final DriveTrain drive;
    private PIDController turnPID;

    private final double targetAngle = 0;

    public PIDPathFollower(Localizer loc, DriveTrain drive) {
        this.loc = loc;
        this.drive = drive;
        reload();
    }

    public void update() {
        double turnVal = turnPID.calculate(Utils.normalizeAngle(loc.getRotationRadians()), targetAngle);
        System.out.println(turnVal);

        drive.driveMotors(-turnVal, turnVal);
    }

    public void reload() {
        double p = ShuffleboardManager.pathKP.getDouble(0.5);
        double i = ShuffleboardManager.pathKI.getDouble(0);
        double d = ShuffleboardManager.pathKD.getDouble(0.05);
        turnPID = new PIDController(p, i, d);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
    }
}
