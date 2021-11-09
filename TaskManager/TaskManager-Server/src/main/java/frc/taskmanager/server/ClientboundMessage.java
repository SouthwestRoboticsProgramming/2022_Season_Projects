package frc.taskmanager.server;

public class ClientboundMessage {
    private final String origin;
    private final String type;
    private final byte[] data;

    public ClientboundMessage(String origin, String type, byte[] data) {
        this.origin = origin;
        this.type = type;
        this.data = data;
    }

    public String getOrigin() {
        return origin;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }
}
