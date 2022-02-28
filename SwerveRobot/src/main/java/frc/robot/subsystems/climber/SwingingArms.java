package frc.robot.subsystems.climber;

import static frc.robot.constants.ClimberConstants.*;

public class SwingingArms {
    private NewSwingingArm left;
    private NewSwingingArm right;

    public SwingingArms() {
        left = new NewSwingingArm(CLIMBER_LEFT_SWING_MOTOR_ID);
        right = new NewSwingingArm(CLIMBER_RIGHT_SWING_MOTOR_ID);
    }

    public void swingToAngle(double degrees) {
        left.swingToAngle(degrees);
        right.swingToAngle(degrees);
    }
}
