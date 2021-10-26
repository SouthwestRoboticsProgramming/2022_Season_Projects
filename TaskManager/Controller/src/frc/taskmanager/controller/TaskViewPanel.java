package frc.taskmanager.controller;

import frc.taskmanager.client.Coprocessor;
import frc.taskmanager.client.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaskViewPanel extends JPanel {
    private final JScrollPane listScrollPane;
    private final JList<String> taskList;

    public TaskViewPanel(Coprocessor cp) {
        super();

        setLayout(new BorderLayout());

        // Initialize and add a list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);

        listScrollPane = new JScrollPane(taskList);
        add(listScrollPane, BorderLayout.WEST);

        Dimension listSize = new Dimension(200, -1);
        listScrollPane.setMinimumSize(listSize);
        listScrollPane.setPreferredSize(listSize);
        listScrollPane.setMaximumSize(listSize);

        // Get all of the tasks from the coprocessor and put them into the list
        cp.getAllTasks().thenAccept((taskSet) -> {
            List<Task> tasks = new ArrayList<>(taskSet);
            tasks.sort((t1, t2) -> String.CASE_INSENSITIVE_ORDER.compare(t1.getName(), t2.getName()));

            for (Task task : tasks) {
                listModel.addElement(task.getName());
            }
        });
    }
}
