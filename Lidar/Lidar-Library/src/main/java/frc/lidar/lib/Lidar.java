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

    // Response id constants
    private static final byte RESPONSE_SCAN = (byte) 0x81;
    private static final byte RESPONSE_INFO = 0x04;
    private static final byte RESPONSE_HEALTH = 0x06;

    private static final long BUSY_TIME = 2;

    private enum ResponseType {
        INFO, HEALTH, SCAN
    }

    private enum ReadState {
        READ_DESCRIPTOR,
        READ_RESPONSE,
        NO_READ
    }

    private enum BusyState {
        NOT_BUSY,
        BUSY_UNTIL_RESPONSE,
        BUSY_FOR_TIME
    }

    // Serial connection to lidar
    private final SerialPort port;

    // Storage for futures that have not yet been completed
    private final Set<CompletableFuture<LidarInfo>> infoFutures;
    private final Set<CompletableFuture<LidarHealth>> healthFutures;

    // Callbacks
    private Runnable scanStartCallback = () -> {};
    private Consumer<ScanEntry> scanDataCallback = (entry) -> {};

    // Busy info
    private long busyBegin;
    private BusyState busy;

    // Response reading
    private ReadState readState;
    private ResponseType expectedResponse;
    private byte[] readBuffer = null;
    private int readIndex = 0;

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

        busy = BusyState.NOT_BUSY;
        readState = ReadState.NO_READ;

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
     * @throws LidarBusyException if busy
     */
    public CompletableFuture<LidarInfo> getInfo() {
        if (isBusy()) {
            throw new LidarBusyException();
        }

        CompletableFuture<LidarInfo> future = new CompletableFuture<>();
        infoFutures.add(future);

        sendRequest(REQUEST_GET_INFO, null);
        busy = BusyState.BUSY_UNTIL_RESPONSE;

        readBuffer = new byte[7];
        readState = ReadState.READ_DESCRIPTOR;

        return future;
    }

    /**
     * Gets the health of the device. If this returns
     * {@link LidarHealth#ERROR}, the device is not
     * functioning normally and will not be able to
     * scan the environment.
     *
     * @return future for device health
     * @throws LidarBusyException if busy
     */
    public CompletableFuture<LidarHealth> getHealth() {
        if (isBusy()) {
            throw new LidarBusyException();
        }

        CompletableFuture<LidarHealth> future = new CompletableFuture<>();
        healthFutures.add(future);

        sendRequest(REQUEST_GET_HEALTH, null);
        busy = BusyState.BUSY_UNTIL_RESPONSE;

        readBuffer = new byte[7];
        readState = ReadState.READ_DESCRIPTOR;

        return future;
    }

    /**
     * Signals the lidar to begin scanning the environment.
     * Will do nothing if the lidar is already scanning.
     *
     * @throws LidarBusyException if busy
     */
    public void startScanning() {
        if (isBusy()) {
            throw new LidarBusyException();
        }

        port.clearDTR();
        sendRequest(REQUEST_SCAN, null);
        beginTimedBusy();

        readBuffer = new byte[7];
        readState = ReadState.READ_DESCRIPTOR;
    }

    /**
     * Signals the lidar to stop scanning the environment.
     * Will do nothing if the lidar is not currently scanning.
     */
    public void stopScanning() {
        sendRequest(REQUEST_STOP, null);
        port.setDTR();
        beginTimedBusy();
    }

    /**
     * Sets a callback for when a new round of scanning is
     * started. 
     *
     * @param callback callback function
     */
    public void setScanStartCallback(Runnable callback) {
        scanStartCallback = callback;
    }

    /**
     * Sets a callback for when scan data is received.
     * This callback is called very frequently! Try to keep
     * the amount of processing in the function to
     * a minimum.
     *
     * @param callback callback function
     */
    public void setScanDataCallback(Consumer<ScanEntry> callback) {
        scanDataCallback = callback;
    }

    /**
     * Resets the lidar.
     *
     * @throws LidarBusyException if busy
     */
    public void reset() {
        sendRequest(REQUEST_RESET, null);
        beginTimedBusy();
    }

    /**
     * Gets whether the lidar is currently busy.
     *
     * @return whether the lidar is busy
     */
    public boolean isBusy() {
        if (busy == BusyState.BUSY_FOR_TIME) {
            long now = System.currentTimeMillis();
            
            if (now - busyBegin > BUSY_TIME) {
                busy = BusyState.NOT_BUSY;
            }
        }

        return busy != BusyState.NOT_BUSY;
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
    }

    // Sets the busy state to BUSY_FOR_TIME and initializes the time
    private void beginTimedBusy() {
        busy = BusyState.BUSY_FOR_TIME;
        busyBegin = System.currentTimeMillis();
    }

    // Reads a response containing device info data
    private void readResponseInfo() {
        assert readBuffer.length == 20; // Length should always be 20

        int modelId = readBuffer[0] & 0xFF;
        int firmwareMinor = readBuffer[1] & 0xFF;
        int firmwareMajor = readBuffer[2] & 0xFF;
        int hardwareVer = readBuffer[3] & 0xFF;
        long serialNumberLow = 0;
        long serialNumberHigh = 0;
        for (int i = 0; i < 8; i++) {
            serialNumberLow  |= (long) (readBuffer[i +  4] & 0xFF) << (i * 8);
            serialNumberHigh |= (long) (readBuffer[i + 12] & 0xFF) << (i * 8);
        }

        LidarInfo info = new LidarInfo(modelId, firmwareMinor, firmwareMajor, hardwareVer, serialNumberLow, serialNumberHigh);

        for (CompletableFuture<LidarInfo> future : infoFutures) {
            future.complete(info);
        }
        infoFutures.clear();

        readState = ReadState.NO_READ;
        readBuffer = null;
    }

    // Reads a response containing response health data
    private void readResponseHealth() {
        assert readBuffer.length == 3; // Length should always be 3

        int status = readBuffer[0] & 0xFF;
        LidarHealth health;
        switch (status) {
            case 0:
                health = LidarHealth.GOOD;
                break;
            case 1:
                health = LidarHealth.WARNING;
                break;
            case 2:
                health = LidarHealth.ERROR;
                break;
            default:
                throw new RuntimeException("Unknown status id " + status);
        }

        for (CompletableFuture<LidarHealth> future : healthFutures) {
            future.complete(health);
        }
        healthFutures.clear();

        readState = ReadState.NO_READ;
        readBuffer = null;
    }

    // Reads a response containing scan response data
    private void readResponseScan() {
        assert readBuffer.length == 5; // Length should always be 5

        // This works, do not question it
      
        boolean start = (readBuffer[0] & 0x01) != 0;
        assert ((readBuffer[0] & 0x02) ^ (readBuffer[1] & 0x01)) == 1;
        int quality = (readBuffer[0] & 0xFC) >> 2;

        assert (readBuffer[1] & 0x01) == 1;
        int angleFixed = (readBuffer[1] & 0xFE) >> 2;
        angleFixed |= (readBuffer[2] & 0xFF) << 7;
        double angle = angleFixed / 64.0;

        int distanceFixed = (readBuffer[3] & 0xFF) | ((readBuffer[4] & 0xFF) << 8);
        double distance = distanceFixed / 4.0;

        if (start) {
            try {
                scanStartCallback.run();
            } catch (Throwable e) {
                System.err.println("Exception in scan start callback:");
                e.printStackTrace();
            }
        }

        ScanEntry entry = new ScanEntry(quality, angle, distance);
        try {
            scanDataCallback.accept(entry);
        } catch (Throwable e) {
            System.err.println("Exception in scan data callback:");
            e.printStackTrace();
        }
    }

    // Method called when the read buffer has been filled
    private void onReadBufferFilled() {
        switch (readState) {
            case NO_READ:
                return;
            case READ_DESCRIPTOR: {
                // Read descriptor format:
                // [Start Flag 1] [Start Flag 2] [30-bit response length] [2 bit send mode] [data type]

                assert readBuffer.length == 7;
                assert readBuffer[0] == START_FLAG; // For data validation
                assert readBuffer[1] == START_FLAG_2; // Also for data validation

                int responseLen = (readBuffer[2] & 0xFF) |
                        (readBuffer[3] & 0xFF) << 8 |
                        (readBuffer[4] & 0xFF) << 16 |
                        (readBuffer[5] & 0x3F) << 24; // Mask out the send mode bits
                //int sendMode = readBuffer[5] & 0xC0; // Probably not needed
                byte dataType = readBuffer[6];
                
                expectedResponse = getResponseTypeById(dataType);

                readState = ReadState.READ_RESPONSE;
                readBuffer = new byte[responseLen];

                return;
            }
            case READ_RESPONSE: {
                switch (expectedResponse) {
                    case INFO:
                        readResponseInfo();
                        break;
                    case HEALTH:
                        readResponseHealth();
                        break;
                    case SCAN:
                        readResponseScan();
                        break;
                    default:
                        System.out.println(toHex(readBuffer, readBuffer.length));
                        break;
                }

                if (busy == BusyState.BUSY_UNTIL_RESPONSE) {
                    busy = BusyState.NOT_BUSY;
                }
            }
        }
    }

    // Gets a response type from the protocol identifier
    private ResponseType getResponseTypeById(byte id) {
        switch (id) {
            case RESPONSE_SCAN:
                return ResponseType.SCAN;
            case RESPONSE_INFO:
                return ResponseType.INFO;
            case RESPONSE_HEALTH:
                return ResponseType.HEALTH;
            default:
                throw new IllegalArgumentException("Unknown id " + id);
        }
    }

    @Override
    public int getListeningEvents() {
        // Listen for when data becomes available
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    // Called when new data arrives from the lidar
    @Override
    public void serialEvent(SerialPortEvent event) {
        if (readBuffer == null) {
            return;
        }

        if (readState == ReadState.NO_READ) {
            // Skip input data if no input is expected
            byte[] skipped = new byte[port.bytesAvailable()];
            port.readBytes(skipped, skipped.length);
        }

        while (port.bytesAvailable() > 0) {
            byte[] buffer = new byte[readBuffer.length - readIndex];
            int read = port.readBytes(buffer, buffer.length);
            
            if (read > 0) {
                System.arraycopy(buffer, 0, readBuffer, readIndex, read);
                readIndex += read;

                if (readIndex == readBuffer.length) {
                    onReadBufferFilled();
                    readIndex = 0;
                }
            }
        }
    }

    // Converts a byte array to a hex string
    // Limit is the amount of data to read from the array
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
