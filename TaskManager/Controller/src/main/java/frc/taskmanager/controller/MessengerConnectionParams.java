package frc.taskmanager.controller;

import java.util.Objects;

public class MessengerConnectionParams {
    private final String host;
    private final int port;

    public MessengerConnectionParams(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessengerConnectionParams that = (MessengerConnectionParams) o;
        return port == that.port &&
                host.equals(that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "MessengerConnectionParams{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
