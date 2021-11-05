package frc.lidar.lib.exceptions;

/**
 * An exception thrown when a request is made
 * to the lidar but it is busy.
 *
 * @author rmheuer
 */
public class LidarBusyException extends LidarException {
    public LidarBusyException() {
        super("Lidar busy");
    }
}
