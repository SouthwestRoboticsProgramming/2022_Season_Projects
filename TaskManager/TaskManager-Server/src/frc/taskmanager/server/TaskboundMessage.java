package frc.taskmanager.server;

public class TaskboundMessage {
    private final String type;
    private final byte[] data;

    public TaskboundMessage(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }
}
