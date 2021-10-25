package frc.virtualrobot.robot;

import java.util.HashSet;
import java.util.Set;

public class Scheduler {
    private Set<RepeatingTask> tasks;

    public Scheduler() {
        tasks = new HashSet<>();
    }

    public void scheduleRepeating(int interval, Runnable task) {
        tasks.add(new RepeatingTask(interval, task));
    }

    public void tick() {
        for (RepeatingTask task : tasks) {
            task.tick();
        }
    }

    private static class RepeatingTask {
        private final int interval;
        private final Runnable task;
        private int counter;

        private RepeatingTask(int interval, Runnable task) {
            this.interval = interval;
            this.task = task;
            this.counter = 0;
        }

        private void tick() {
            if (++counter == interval) {
                counter = 0;
                task.run();
            }
        }
    }
}
