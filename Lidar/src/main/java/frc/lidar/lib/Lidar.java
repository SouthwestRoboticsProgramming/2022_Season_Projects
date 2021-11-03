package frc.lidar.lib;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import frc.lidar.lib.exceptions.InvalidLidarPortException;
import frc.lidar.lib.exceptions.LidarBusyException;
import frc.lidar.lib.exceptions.NoSuchLidarPortException;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Represents a connection to a lidar.
 *
 * @author rmheuer
 */
public class Lidar implements SerialPortDataListener {
    private static final String MODEL_NUM = "CP2102";

    // Start flags
    private static final byte START_FLAG = (byte) 0xA5;
    private static final byte START_FLAG_2 = 0x5A;

    // Request id constants
    private static final byte REQUEST_STOP = 0x25;
    private static final byte REQUEST_RESET = 0x40;
    private static final byte REQUEST_SCAN = 0x20;
    private static final byte REQUEST_GET_INFO = 0x50;
    private static final byte REQUEST_GET_HEALTH = 0x52;

    private enum ResponseType {
        INFO, HEALTH, SCAN
    }

    private final SerialPort port;
    private final Set<CompletableFuture<LidarInfo>> infoFutures;
    private final Set<CompletableFuture<LidarHealth>> healthFutures;

    /**
     * Gets a list of serial ports that could be a lidar.
     *
     * @return set of lidar ports
     */
    public static Set<SerialPort> findLidars() {
        Set<SerialPort> ports = new HashSet<>();

        for (SerialPort port : SerialPort.getCommPorts()) {
            String desc = port.getPortDescription();
            if (desc.contains(MODEL_NUM)) {
                ports.add(port);
            }
        }

        return ports;
    }

    /**
     * Creates a new instance of this class and connects
     * to the first lidar found. If multiple lidars are
     * present, it is undefined which one will be selected.
     *
     * @throws NoSuchLidarPortException if no lidar is found
     */
    public Lidar() {
        this(getDefaultPort());
    }

    /**
     * Creates a new instance of this class and connects
     * to a lidar at the serial port with the given name.
     *
     * @param portName name of port to connect to
     * @throws NoSuchLidarPortException if the port does
     *         not exist
     * @throws InvalidLidarPortException if the device on
     *         the port is not a lidar
     */
    public Lidar(String portName) {
        this(getAndCheckPort(portName));
    }

    /**
     * Creates a new instance of a lidar on a given serial
     * port.
     *
     * @param port lidar port
     * @throws InvalidLidarPortException if the device on
     *         the port is not a lidar
     */
    public Lidar(SerialPort port) {
        checkPort(port);
        System.out.println("Starting lidar on port " + port.getSystemPortName());
        this.port = port;

        infoFutures = new HashSet<>();
        healthFutures = new HashSet<>();

        port.setBaudRate(115200);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(1);
        port.setNumDataBits(8);
        if (!port.openPort()) {
            throw new RuntimeException("Failed to connect");
        }
        port.addDataListener(this);
    }

    /**
     * Gets the device info of the lidar.
     * This requires sending a request to the lidar,
     * so it must return a future.
     *
     * @return future for device info
     */
    public CompletableFuture<LidarInfo> getInfo() {
        CompletableFuture<LidarInfo> future = new CompletableFuture<>();
        infoFutures.add(future);

        sendRequest(REQUEST_GET_INFO, null);

        return future;
    }

    /**
     * Gets the health of the device. If this returns
     * {@link LidarHealth#ERROR}, the device is not
     * functioning normally and will not be able to
     * scan the environment.
     *
     * @return future for device health
     */
    public CompletableFuture<LidarHealth> getHealth() {
        CompletableFuture<LidarHealth> future = new CompletableFuture<>();
        healthFutures.add(future);

        sendRequest(REQUEST_GET_HEALTH, null);

        return future;
    }

    /**
     * Signals the lidar to begin scanning the environment.
     * Will do nothing if the lidar is already scanning.
     *
     * @throws LidarBusyException if busy
     */
    public void startScanning() {
        port.clearDTR();
        sendRequest(REQUEST_SCAN, null);
    }

    /**
     * Signals the lidar to stop scanning the environment.
     * Will do nothing if the lidar is not currently scanning.
     */
    public void stopScanning() {

    }

    /**
     * Sets a callback for when scan data is received.
     * This callback is called vary frequently! Tey to keep
     * the amount of processing in the function to
     * a minimum.
     *
     * @param callback callback function
     */
    public void setScanDataCallback(Consumer<ScanEntry> callback) {

    }

    /**
     * Resets the lidar.
     *
     * @throws LidarBusyException if busy
     */
    public void reset() {

    }

    /**
     * Gets whether the lidar is currently busy.
     *
     * @return whether the lidar is busy
     */
    public boolean isBusy() {
        return false;
    }

    /**
     * Closes the connection to the lidar. No other methods should
     * be called on this class after closing.
     */
    public void close() {
        port.closePort();
    }

    // Finds a serial port connected to a lidar
    private static SerialPort getDefaultPort() {
        Set<SerialPort> ports = findLidars();
        if (ports.isEmpty()) {
            throw new NoSuchLidarPortException("No lidar ports found");
        }
        return ports.iterator().next();
    }

    // Gets a port by name and checks it
    private static SerialPort getAndCheckPort(String name) {
        SerialPort port;
        try {
            port = SerialPort.getCommPort(name);
        } catch (SerialPortInvalidPortException e) {
            throw new NoSuchLidarPortException("Port not found: '" + name + "'");
        }

        checkPort(port);

        return port;
    }

    // Checks whether a lidar is present on a given port
    private static void checkPort(SerialPort port) {
        if (!port.getPortDescription().contains(MODEL_NUM)) {
            throw new InvalidLidarPortException("Device on port '" + port.getSystemPortName() + "' is not a lidar");
        }
    }

    // Sends a request with optional payload (no payload if null)
    // Request format:
    //     [Start flag] [Command] | [Payload Size] [Payload Data...] [Checksum]
    private void sendRequest(byte id, byte[] payload) {
        byte[] requestData = new byte[payload == null ? 2 : (payload.length + 4)];

        requestData[0] = START_FLAG;
        requestData[1] = id;
        if (payload != null) {
            if (payload.length > 255) {
                throw new IllegalArgumentException("Payload too large");
            }

            byte payloadLength = (byte) payload.length;
            requestData[2] = payloadLength;

            System.arraycopy(payload, 0, requestData, 3, payload.length);

            byte checksum = 0;
            for (int i = 0; i < payloadLength - 1; i++) {
                checksum ^= requestData[i];
            }
            requestData[requestData.length - 1] = checksum;
        }

        int written = port.writeBytes(requestData, requestData.length);
        if (written != requestData.length) {
            throw new RuntimeException("Failed to write all data: " + written);
        }
        System.out.println("Wrote data");

        System.out.println(toHex(requestData, requestData.length));
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        byte[] data = new byte[port.bytesAvailable()];
        int read = port.readBytes(data, data.length);

        System.out.println("Hex: " + toHex(data, read));
    }

    private String toHex(byte[] data, int limit) {
        final char[] CHARS = "0123456789abcdef".toCharArray();
        char[] hex = new char[limit * 2];

        for (int i = 0; i < limit; i++) {
            int hi = (data[i] & 0xF0) >>> 4;
            int lo = data[i] & 0x0F;

            hex[i * 2] = CHARS[hi];
            hex[i * 2 + 1] = CHARS[lo];
        }

        return String.valueOf(hex);
    }
}
