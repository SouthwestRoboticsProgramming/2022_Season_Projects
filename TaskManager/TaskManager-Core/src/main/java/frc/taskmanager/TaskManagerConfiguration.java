package frc.taskmanager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class TaskManagerConfiguration {
    // Initialize default configuration
    private static final Properties defaultConfig = new Properties();
    static {
        defaultConfig.setProperty("messengerHost", "localhost");
        defaultConfig.setProperty("messengerPort", "8341");
        defaultConfig.setProperty("messagePrefix", "TaskManager");
        defaultConfig.setProperty("taskFolder", "tasks");
    }

    public static TaskManagerConfiguration loadFromFile(File file) {
        System.out.println("Loading configuration from " + file.toString());

        // Create the file with default configuration if it doesn't exist
        if (!file.exists()) {
            System.out.println("Configuration file does not exist, creating one");
            try {
                defaultConfig.store(new FileWriter(file), "TaskManager configuration file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load the config file
        Properties props = new Properties(defaultConfig);
        try {
            props.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get properties
        String host = props.getProperty("messengerHost");
        int port = Integer.parseInt(props.getProperty("messengerPort"));
        String prefix = props.getProperty("messagePrefix");
        File taskFolder = new File(props.getProperty("taskFolder"));

        System.out.println("Configuration successfully loaded");

        // Create config object
        return new TaskManagerConfiguration(host, port, prefix, taskFolder);
    }

    private final String messengerHost;
    private final int messengerPort;
    private final String messagePrefix;
    private final File taskFolder;

    private TaskManagerConfiguration(String messengerHost, int messengerPort, String messagePrefix, File taskFolder) {
        this.messengerHost = messengerHost;
        this.messengerPort = messengerPort;
        this.messagePrefix = messagePrefix;
        this.taskFolder = taskFolder;
    }

    public String getMessengerHost() {
        return messengerHost;
    }

    public int getMessengerPort() {
        return messengerPort;
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public File getTaskFolder() {
        return taskFolder;
    }
}