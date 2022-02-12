package frc.shufflewood.tools;

import frc.shufflewood.App;
import frc.shufflewood.MessengerAccess;
import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.tools.lidar.LidarTool;
import frc.shufflewood.tools.taskmanager.TaskManagerTool;

public class ToolPalette implements Tool {
    private final boolean[] enablePiTask, enableNanoTask, enableLidar, enableValueDisplay, enableValueEdit, enableProfiler;
    private final TaskManagerTool piTask;
    private final TaskManagerTool nanoTask;
    private final LidarTool lidar;
    private final ValueDisplayTool valueDisplay;
    private final ValueEditTool valueEdit;
    private final ProfilerTool profiler;

    public ToolPalette(App app) {
        enablePiTask = new boolean[] {false};
        enableNanoTask = new boolean[] {false};
        enableLidar = new boolean[] {false};
        enableValueDisplay = new boolean[] {false};
        enableValueEdit = new boolean[] {false};
        enableProfiler = new boolean[] {true};

        MessengerAccess msg = app.getMessenger();
        piTask = new TaskManagerTool(msg, "RPi");
        nanoTask = new TaskManagerTool(msg, "Nano");
        lidar = new LidarTool(app);
        valueDisplay = new ValueDisplayTool(msg);
        valueEdit = new ValueEditTool(msg);
        profiler = new ProfilerTool();
    }

    @Override
    public void draw(GuiContext gui) {
        gui.begin("Palette");

        gui.checkbox(enablePiTask);
        gui.sameLine();
        gui.text("TaskManager: RPi");

        gui.checkbox(enableNanoTask);
        gui.sameLine();
        gui.text("TaskManager: Nano");

        gui.checkbox(enableLidar);
        gui.sameLine();
        gui.text("Lidar");

        gui.checkbox(enableValueDisplay);
        gui.sameLine();
        gui.text("Value Display");

        gui.checkbox(enableValueEdit);
        gui.sameLine();
        gui.text("Value Edit");

        gui.checkbox(enableProfiler);
        gui.sameLine();
        gui.text("Profiler");

        gui.setWindowHeightAuto();
        gui.end();

        if (enablePiTask[0]) piTask.draw(gui);
        if (enableNanoTask[0]) nanoTask.draw(gui);
        if (enableLidar[0]) lidar.draw(gui);
        if (enableValueDisplay[0]) valueDisplay.draw(gui);
        if (enableValueEdit[0]) valueEdit.draw(gui);
        if (enableProfiler[0]) profiler.draw(gui);
    }
}
