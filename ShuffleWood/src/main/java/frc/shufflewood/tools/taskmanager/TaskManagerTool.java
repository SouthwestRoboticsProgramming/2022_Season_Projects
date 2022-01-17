package frc.shufflewood.tools.taskmanager;

import frc.shufflewood.FileChooser;
import frc.shufflewood.MessengerAccess;
import frc.shufflewood.gui.GuiContext;
import frc.shufflewood.gui.Vec2;
import frc.shufflewood.tools.Tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TaskManagerTool implements Tool {
    private final String prefix;
    private final TaskManagerAPI api;
    private List<Task> tasks;
    private Task selectedTask;
    private boolean taskRunning;
    private List<String> log;
    private boolean first = true;
    private StringBuffer newTaskBuf;

    public TaskManagerTool(MessengerAccess msg, String prefix) {
        this.prefix = prefix;
        api = new TaskManagerAPI(msg, prefix);
	api.setStdOutCallback(this::stdOutCallback);
	api.setStdErrCallback(this::stdErrCallback);
        tasks = new ArrayList<>();
        log = new ArrayList<>();
        newTaskBuf = new StringBuffer();
    }

    private void stdOutCallback(Task task, String line) {
	if (selectedTask == task) {
	    log.add("[OUT] " + line);
	}
    }

    private void stdErrCallback(Task task, String line) {
	if (selectedTask == task) {
	    log.add("[ERR] " + line);
	}
    }

    @Override
    public void draw(GuiContext gui) {
        if (selectedTask != null) {
            selectedTask.isRunning().thenAccept((running) -> taskRunning = running);
        }
        api.getAllTasks().thenAccept((taskSet) -> {
            tasks = new ArrayList<>(taskSet);
            tasks.sort((t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getName(), t2.getName()));
        });

        gui.begin("Task Manager: " + prefix);
        if (first) {
            gui.setWindowSize(640, 480);
            first = false;
        }

        gui.beginTable(true, 1, 3);

        // Left column
        gui.text("Tasks");
        gui.beginTable(false, 3, 1);
        gui.editString(newTaskBuf);
        gui.tableNextColumn();
        if (gui.button("Add")) {
            FileChooser.chooseZipOrFolder((f) -> {
		if (f == null) return;
		
                Task task = api.getTask(newTaskBuf.toString());
                byte[] payload = encodeFileToPayload(f);
                if (payload != null) {
                    task.upload(payload);
                }
            });
        }
        gui.endTable();

        gui.beginChild(gui.getAvailableContentSize(), true);
        for (Task task : tasks) {
            boolean[] selected = {task == selectedTask};
            gui.selectableText(task.getName(), selected);
            //gui.text(task.getName());
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

            Vec2 buttonSize = new Vec2((gui.getAvailableContentSize().x - gui.getStyle().widgetPadding) / 3, -1);
            if (gui.button(taskRunning ? "Stop" : "Start", buttonSize)) {
                if (taskRunning) {
                    selectedTask.stop();
                } else {
		    log.clear();
                    selectedTask.start();
                }
            }
            gui.sameLine();
            if (gui.button("Upload data", buttonSize)) {
                FileChooser.chooseZipOrFolder((f) -> {
		    if (f == null) return;
		    
		    byte[] payload = encodeFileToPayload(f);
                    if (payload != null) {
                        selectedTask.upload(payload);
                    }
                });
            }
            gui.sameLine();
            if (gui.button("Delete")) {
                selectedTask.delete();
                selectedTask = null;
            }

            gui.separator();

            gui.text("Output log");
            gui.beginChild(gui.getAvailableContentSize(), true);
            for (String line : log) {
                gui.text(line);
            }
            // TODO: Scroll to bottom automatically
            gui.endChild();
        }

        gui.endTable();

        gui.end();
    }

    private byte[] encodeFileToPayload(File file) {
        try {
            Path path = file.toPath();
            if (Files.isDirectory(path)) {
                Path pathAbsolute = path.toAbsolutePath();

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                ZipOutputStream zip = new ZipOutputStream(b);

                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                        if (Files.isDirectory(file)) {
                            return FileVisitResult.CONTINUE;
                        }

                        Path absolute = file.toAbsolutePath();
                        Path relative = pathAbsolute.relativize(absolute);
                        System.out.println("Adding " + relative.toString());

                        ZipEntry entry = new ZipEntry(relative.toString());
                        zip.putNextEntry(entry);

                        byte[] fileData = Files.readAllBytes(file);
                        zip.write(fileData);
                        zip.closeEntry();

                        return FileVisitResult.CONTINUE;
                    }
                });

                zip.close();

                return b.toByteArray();
            } else {
                return Files.readAllBytes(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
