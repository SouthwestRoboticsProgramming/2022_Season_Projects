package frc.robot.subsystems.climber;

import frc.robot.control.Input;
import frc.robot.subsystems.Subsystem;
import frc.robot.util.Utils;

public class Climber extends Subsystem {
    private final Input input;

    private TelescopingArms telescoping;
    private SwingingArms swinging;

    public Climber(Input input) {
        this.input = input;

        telescoping = new TelescopingArms();
        swinging = new SwingingArms();
    }

    @Override
    public void teleopPeriodic() {
        if (input.testButton2()) {
            telescoping.manualMove(input.getClimbTele());
        } else {
            telescoping.extendToDistance(Utils.map(input.getClimbTele(), -1, 1, 0, 1));
        }

        swinging.swingToAngle(Utils.map(input.testSwingingArm(), -1, 1, 45, 135));
    }
}
