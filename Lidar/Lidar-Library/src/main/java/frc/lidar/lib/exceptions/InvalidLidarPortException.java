package frc.lidar.lib.exceptions;

/**
 * An exception thrown when a device on a port
 * is not a lidar.
 *
 * @author rmheuer
 */
public class InvalidLidarPortException extends LidarException {
    public InvalidLidarPortException(String message) {
        super(message);
    }
}
