package frc.messenger.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class MessengerConfig {
    private static final Properties defaultConfig = new Properties();
    static {
        defaultConfig.setProperty("port", "8341");
    }

    public static MessengerConfig loadFromFile(File file) {
        if (!file.exists()) {
            try {
                defaultConfig.store(new FileWriter(file), "Messenger Configuration");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Properties props = new Properties(defaultConfig);
        try {
            props.load(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int port = Integer.parseInt(props.getProperty("port"));

        return new MessengerConfig(port);
    }

    private final int port;

    public MessengerConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
