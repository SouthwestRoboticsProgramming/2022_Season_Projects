package frc.taskmanager.controller;

import frc.taskmanager.client.Coprocessor;
import frc.taskmanager.client.Task;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaskViewPanel extends JPanel {
    private final Coprocessor cp;
    private final DefaultListModel<String> listModel;

    public TaskViewPanel(Coprocessor cp) {
        super();
        this.cp = cp;

        setLayout(new BorderLayout(3, 3));

        // Set up task list panel
        JPanel taskListPanel = new JPanel();
        taskListPanel.setLayout(new BorderLayout(3, 3));
        {
            Dimension size = new Dimension(200, -1);

            // Set up the task list header
            JPanel listHeader = new JPanel();
            listHeader.setLayout(new BorderLayout(3, 3));
            {
                JLabel label = new JLabel("Tasks");
                listHeader.add(label, BorderLayout.CENTER);
            }
            taskListPanel.add(listHeader, BorderLayout.NORTH);

            // Set up the task list
            listModel = new DefaultListModel<>();
            JList<String> taskList = new JList<>(listModel);
            JScrollPane listScrollPane = new JScrollPane(taskList);
            listScrollPane.setMinimumSize(size);
            listScrollPane.setPreferredSize(size);
            listScrollPane.setMaximumSize(size);
            taskListPanel.add(listScrollPane, BorderLayout.CENTER);

            // Set up the task list footer
            JPanel listFooter = new JPanel();
            listFooter.setLayout(new BorderLayout(3, 3));
            {
                JButton addButton = new JButton("Add New");
                JButton removeButton = new JButton("Remove");

                listFooter.add(addButton, BorderLayout.WEST);
                listFooter.add(removeButton, BorderLayout.EAST);
            }
            taskListPanel.add(listFooter, BorderLayout.SOUTH);
        }

        // Add the elements to this
        add(taskListPanel, BorderLayout.WEST);

        refresh();
    }

    private void refresh() {
        // Clear the list
        listModel.clear();

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
