package frc.robot.command;

import frc.robot.Constants;
import frc.robot.util.ShuffleWood;

public final class SaveShuffleWoodCommand implements Command {
    @Override
    public boolean run() {
        ShuffleWood.save();
        return false;
    }

    @Override
    public int getInterval() {
        return Constants.SHUFFLEWOOD_SAVE_INTERVAL;
    }
}
