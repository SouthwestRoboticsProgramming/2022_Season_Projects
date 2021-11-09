package frc.taskmanager.controller;

import java.util.Objects;

public class CoprocessorConnectionParams {
    private final String host;
    private final int port;

    public CoprocessorConnectionParams(String host, int port) {
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
        CoprocessorConnectionParams that = (CoprocessorConnectionParams) o;
        return port == that.port &&
                host.equals(that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "CoprocessorConnectionParams{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
