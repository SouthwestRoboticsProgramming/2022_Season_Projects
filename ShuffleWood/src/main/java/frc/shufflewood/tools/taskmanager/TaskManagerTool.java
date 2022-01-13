package frc.shufflewood.tools.taskmanager;

import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.tools.Tool;

public class TaskManagerTool implements Tool {
    /*
    private List<Task> tasks;
    private Task selectedTask;
    private boolean taskRunning;
    private List<String> log;
    */

    @Override
    public void draw(GuiContext gui) {
        if (selectedTask != null) {
            selectedTask.isRunning().thenAccept((running) -> taskRunning = running);
        }

        gui.begin("Task Manager");

        /*
        gui.beginTable(1, 3);

        // Left column
        gui.text("Tasks");
        gui.beginChild(gui.getAvailableContentSize());
        for (Task task : tasks) {
            boolean[] selected = {task == selectedTask};
            gui.selectableText(task.getName(), selected);
            if (selected[0] && selectedTask != task) {
                selectedTask = task;
                log.clear();
            }
        }
        gui.endChild();

        gui.tableNextColumn();

        // Right column
        if (selectedTask == null) {
            gui.text("No task selected");
        } else {
            gui.text("Viewing task: %s", selectedTask.getName());
            gui.text("Task status: %s", taskRunning ? "RUNNING" : "IDLE");

            gui.separator();

            if (gui.button(taskRunning ? "Stop" : "Start", new Vec2(gui.getAvailableContentSize().x, -1))) {
                if (taskRunning) {
                    task.stop();
                } else {
                    task.start();
                }
            }
            gui.sameLine();
            if (gui.button("Upload data")) {
                // Upload data
            }

            gui.separator();

            gui.text("Output log");
            gui.beginChild(gui.getAvailableContentSize());
            for (String line : log) {
                gui.text(line);
            }
            // TODO: Scroll to bottom automatically
            gui.endChild();
        }

        gui.endTable();
        */

        gui.end();
    }
}
