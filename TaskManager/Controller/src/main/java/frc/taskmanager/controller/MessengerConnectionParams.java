package frc.taskmanager.controller;

import java.util.Objects;

public class MessengerConnectionParams {
    private final String host;
    private final int port;
    private final String prefix;

    public MessengerConnectionParams(String host, int port, String prefix) {
        this.host = host;
        this.port = port;
        this.prefix = prefix;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPrefix() {
        return prefix;
    }
}
