package frc.robot.command;

import java.util.ArrayList;
import java.util.List;

public class CommandSequence implements Command {
    private final List<Command> sequence;
    private int currentIndex = 0;
    
    public CommandSequence() {
        sequence = new ArrayList<>();
    }

    public CommandSequence append(Command cmd) {
        sequence.add(cmd);
        return this;
    }

    public void reset() {
        currentIndex = 0;
    }

    @Override
    public boolean run() {
        if (currentIndex >= sequence.size()) {
            return true;
        }

        Command activeCommand = sequence.get(currentIndex);
        if (activeCommand.run()) {
            currentIndex++;
        }

        return false;
    }
}
