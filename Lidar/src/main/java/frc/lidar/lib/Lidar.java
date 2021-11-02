package frc.lidar.lib;

import com.fazecast.jSerialComm.SerialPort;
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
public class Lidar {
    private static final String MODEL_NUM = "CP2102";

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
    }

    /**
     * Gets the device info of the lidar.
     * This requires sending a request to the lidar,
     * so it must return a future.
     *
     * @return future for device info
     */
    public CompletableFuture<LidarInfo> getInfo() {
        return null;
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
        return null;
    }

    /**
     * Signals the lidar to begin scanning the environment.
     * Will do nothing if the lidar is already scanning.
     *
     * @throws LidarBusyException if busy
     */
    public void startScanning() {

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
}
