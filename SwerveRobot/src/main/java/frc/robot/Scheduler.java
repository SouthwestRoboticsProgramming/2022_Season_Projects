package frc.robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import frc.robot.command.Command;
import frc.robot.subsystems.Subsystem;

public final class Scheduler {
    private static final Scheduler INSTANCE = new Scheduler();
    public static Scheduler get() {
        return INSTANCE;
    }

    private final List<Subsystem> subsystems;
    private final List<CommandTimer> activeCommands;

    private Scheduler() {
        subsystems = new ArrayList<>();
        activeCommands = new ArrayList<>();
    }

    public void addSubsystem(Subsystem s) {
        subsystems.add(s);
    }

    public void scheduleCommand(Command cmd) {
        // System.out.println("Scheduling " + cmd);

        activeCommands.add(new CommandTimer(cmd));
    }

    public void cancelCommand(Command cmd) {
        // System.out.println("Cancelling " + cmd);

        CommandTimer toRemove = null;
        for (CommandTimer timer : activeCommands) {
            if (timer.cmd.equals(cmd)) {
                toRemove = timer;
                break;
            }
        }

        if (toRemove != null) {
            activeCommands.remove(toRemove);
        }
    }

    public void initState() {
        for (Subsystem subsystem : subsystems) {
            subsystem.doInit();
        }
    }

    public void update() {
        for (Subsystem subsystem : subsystems) {
            subsystem.doPeriodic();
        }

        Set<CommandTimer> toRemove = new HashSet<>();
        for (CommandTimer cmd : activeCommands) {
            if (cmd.update()) {
                toRemove.add(cmd);
                // System.out.println("Terminating " + cmd.cmd);
            }
        }
        activeCommands.removeAll(toRemove);
    }

    private static class CommandTimer {
        private final Command cmd;
        private int timer;

        public CommandTimer(Command cmd) {
            this.cmd = cmd;
            timer = 0;
        }

        public boolean update() {
            if (--timer <= 0) {
                timer = cmd.getInterval();
                return cmd.run();
            }

            return false;
        }
    }
}
