package frc.taskmanager.controller;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TaskViewPanel extends JPanel {
    private final TaskManagerAPI cp;
    private final JList<String> taskList;
    private final DefaultListModel<String> listModel;
    private final JButton runToggle;
    private final JButton upload;
    private final JButton delete;
    private final LogPanel log;
    private final JLabel taskName;
    private final JLabel taskStatus;

    private Task selectedTask = null;
    private boolean taskRunning = false;

    public TaskViewPanel(TaskManagerAPI cp) {
        super();
        this.cp = cp;

        setLayout(new BorderLayout());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(200);
        {
            JPanel left = new JPanel();
            left.setLayout(new BorderLayout());
            {
                JPanel header = new JPanel();
                header.setLayout(new BorderLayout());
                {
                    JLabel label = new JLabel("Tasks:");
                    label.setHorizontalTextPosition(SwingConstants.CENTER);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    header.add(label, BorderLayout.CENTER);
                }
                left.add(header, BorderLayout.NORTH);

                JPanel buttons = new JPanel();
                buttons.setLayout(new FlowLayout());
                {
                    JButton add = new JButton("Add New");
                    add.addActionListener(this::onAddNewButtonPressed);
                    buttons.add(add);

                    delete = new JButton("Delete");
                    delete.addActionListener(this::onDeleteButtonPressed);
                    buttons.add(delete);
                }
                left.add(buttons, BorderLayout.SOUTH);

                listModel = new DefaultListModel<>();
                taskList = new JList<>(listModel);
                taskList.addListSelectionListener(this::onTaskListSelectionChanged);
                JScrollPane taskListScroll = new JScrollPane(taskList);
                left.add(taskListScroll, BorderLayout.CENTER);
            }
            split.setLeftComponent(left);

            JPanel right = new JPanel();
            right.setLayout(new BorderLayout());
            {
                JPanel header = new JPanel();
                GridBagLayout gridBag = new GridBagLayout();
                header.setLayout(gridBag);
                {
                    GridBagConstraints c = new GridBagConstraints();
                    c.fill = GridBagConstraints.HORIZONTAL;
                    c.gridx = 0;
                    c.gridy = 0;

                    JLabel viewingTask = new JLabel("Viewing Task:");
                    viewingTask.setHorizontalTextPosition(SwingConstants.CENTER);
                    viewingTask.setHorizontalAlignment(SwingConstants.CENTER);
                    gridBag.setConstraints(viewingTask, c);
                    header.add(viewingTask);

                    c.gridy = 1;
                    taskName = new JLabel("[No Task Selected]");
                    taskName.setHorizontalTextPosition(SwingConstants.CENTER);
                    taskName.setHorizontalAlignment(SwingConstants.CENTER);
                    gridBag.setConstraints(taskName, c);
                    header.add(taskName);

                    c.gridy = 2;
                    Component spacer = Box.createVerticalStrut(10);
                    gridBag.setConstraints(spacer, c);
                    header.add(spacer);

                    c.gridy = 3;
                    taskStatus = new JLabel("Task Status: N/A");
                    taskStatus.setHorizontalTextPosition(SwingConstants.CENTER);
                    taskStatus.setHorizontalAlignment(SwingConstants.CENTER);
                    gridBag.setConstraints(taskStatus, c);
                    header.add(taskStatus);

                    c.gridy = 4;
                    JPanel buttons = new JPanel();
                    buttons.setLayout(new FlowLayout());
                    {
                        runToggle = new JButton("Start");
                        runToggle.addActionListener(this::onRunToggleButtonPressed);
                        buttons.add(runToggle);

                        upload = new JButton("Upload");
                        upload.addActionListener(this::onUploadButtonPressed);
                        buttons.add(upload);
                    }
                    gridBag.setConstraints(buttons, c);
                    header.add(buttons);

                    c.gridy = 5;
                    spacer = Box.createVerticalStrut(10);
                    gridBag.setConstraints(spacer, c);
                    header.add(spacer);

                    c.gridy = 6;
                    JLabel logLabel = new JLabel("Output Log:");
                    logLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                    logLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    gridBag.setConstraints(logLabel, c);
                    header.add(logLabel);
                }
                right.add(header, BorderLayout.NORTH);

                log = new LogPanel();
                log.logOut("Output");
                log.logErr("Error");
                right.add(log, BorderLayout.CENTER);
            }
            split.setRightComponent(right);
        }
        add(split, BorderLayout.CENTER);

        cp.setStdOutCallback(this::onTaskStdOut);
        cp.setStdErrCallback(this::onTaskStdErr);

        disableTaskView();
        delete.setEnabled(false);
        refresh();
    }

    public void refresh() {
        cp.flushNetwork();

        if (selectedTask != null) {
            selectedTask.isRunning().thenAccept((running) -> {
                taskRunning = running;
                runToggle.setText(running ? "Stop" : "Start");
                taskStatus.setText("Task Status: " + (running ? "RUNNING" : "IDLE"));
            });
        } else {
            taskRunning = false;
            runToggle.setText("Start");
        }

        // Get all of the tasks from the coprocessor and put them into the list
        cp.getAllTasks().thenAccept((taskSet) -> {
            List<Task> tasks = new ArrayList<>(taskSet);
            tasks.sort((t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getName(), t2.getName()));

            Set<String> taskNames = new HashSet<>();
            for (Task task : tasks) {
                taskNames.add(task.getName());
            }

            for (int i = 0; i < listModel.getSize(); i++) {
                String entry = listModel.elementAt(i);
                if (!taskNames.contains(entry)) {
                    listModel.removeElementAt(i);
                    i--;
                }
            }

            for (String task : taskNames) {
                if (!listModel.contains(task)) {
                    listModel.addElement(task);
                }
            }
        });
    }

    private void onAddNewButtonPressed(ActionEvent e) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JTextField nameField = new JTextField();
        JTextField filePath = new JTextField();

        JPanel filePanel = new JPanel();
        filePanel.setLayout(new FlowLayout());
        {
            JButton browse = new JButton("Browse");
            Dimension filePathSize = new Dimension(250, (int) browse.getPreferredSize().getHeight() + 10);
            filePath.setMinimumSize(filePathSize);
            filePath.setPreferredSize(filePathSize);
            filePath.setMaximumSize(filePathSize);

            filePanel.add(filePath);
            filePanel.add(browse);

            browse.addActionListener((event) -> {
                JFileChooser chooser = new JFileChooser(filePath.getText());
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archive", "zip"));
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    filePath.setText(chooser.getSelectedFile().getPath());
                }
            });
        }

        panel.add(new JLabel("Task file:"));
        panel.add(filePanel);
        panel.add(new JLabel("Name of task:"));
        panel.add(nameField);

        JOptionPane.showMessageDialog(null, panel, "Add New Task", JOptionPane.PLAIN_MESSAGE);

        String name = nameField.getText();
        if (name.equals("")) {
            JOptionPane.showMessageDialog(null, "Task name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (filePath.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "File path cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task task = cp.getTask(name);
        Path path = Paths.get(filePath.getText());
        if (!Files.exists(path)) {
            JOptionPane.showMessageDialog(null, "Specified task file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            task.upload(readTaskFiles(path));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private byte[] readTaskFiles(Path path) throws IOException {
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
    }

    private void disableTaskView() {
        runToggle.setText("Start");
        runToggle.setEnabled(false);
        upload.setEnabled(false);
        log.clear();
        log.setDisabled(true);
    }

    private void enableTaskView() {
        runToggle.setText("Start");
        runToggle.setEnabled(true);
        upload.setEnabled(true);
        log.clear();
        log.setDisabled(false);
    }

    private void onDeleteButtonPressed(ActionEvent e) {
        if (selectedTask == null) {
            throw new IllegalStateException("No task selected but delete button pressed");
        }

        selectedTask.delete();
        selectedTask = null;
        taskName.setText("[Nothing selected]");
        taskStatus.setText("Task Status: N/A");

        disableTaskView();
        delete.setEnabled(false);
    }

    private void onRunToggleButtonPressed(ActionEvent e) {
        if (selectedTask == null) {
            throw new IllegalStateException("No task selected but run button pressed");
        }

        if (taskRunning) {
            selectedTask.stop();
        } else {
            log.clear();
            selectedTask.start();
        }
    }

    private void onUploadButtonPressed(ActionEvent e) {
        if (selectedTask == null) {
            throw new IllegalStateException("No task selected but upload button pressed");
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileNameExtensionFilter("ZIP Archive", "zip"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            try {
                selectedTask.upload(readTaskFiles(path));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void onTaskListSelectionChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() || taskList.getSelectedValue() == null) {
            return;
        }

        selectedTask = cp.getTask(taskList.getSelectedValue());
        enableTaskView();
        delete.setEnabled(true);
        taskName.setText(selectedTask.getName());
    }

    private void onTaskStdOut(Task task, String line) {
        if (task.equals(selectedTask)) {
            log.logOut(line);
        }
    }

    private void onTaskStdErr(Task task, String line) {
        if (task.equals(selectedTask)) {
            log.logErr(line);
        }
    }
}
