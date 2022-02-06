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
    private final List<ScheduledCommand> activeCommands;

    private Scheduler() {
        subsystems = new ArrayList<>();
        activeCommands = new ArrayList<>();
    }

    public void addSubsystem(Subsystem s) {
        subsystems.add(s);
    }

    public void scheduleCommand(Command cmd) {
        activeCommands.add(new ScheduledCommand(cmd));
    }

    public void update() {
        for (Subsystem subsystem : subsystems) {
            subsystem.doPeriodic();
        }

        Set<ScheduledCommand> toRemove = new HashSet<>();
        for (ScheduledCommand cmd : activeCommands) {
            if (cmd.update()) toRemove.add(cmd);
        }
        activeCommands.removeAll(toRemove);
    }

    private static class ScheduledCommand {
        private final Command cmd;
        private int timer;

        public ScheduledCommand(Command cmd) {
            this.cmd = cmd;
            timer = 0;
        }

        public boolean update() {
            if (timer-- <= 0) {
                timer = cmd.getInterval();
                return cmd.run();
            }

            return false;
        }
    }
}
