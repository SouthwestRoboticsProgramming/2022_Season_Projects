package frc.robot.command;

import frc.robot.util.ShuffleWood;

import static frc.robot.constants.MessengerConstants.SHUFFLEWOOD_SAVE_INTERVAL;

public final class SaveShuffleWoodCommand implements Command {
    @Override
    public boolean run() {
        ShuffleWood.save();
        return false;
    }

    @Override
    public int getInterval() {
        return SHUFFLEWOOD_SAVE_INTERVAL;
    }
}
