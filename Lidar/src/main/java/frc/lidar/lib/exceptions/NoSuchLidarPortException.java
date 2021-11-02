package frc.lidar.lib.exceptions;

/**
 * An exception thrown when no port is found by a given name
 * or when no ports are found.
 *
 * @author rmheuer
 */
public class NoSuchLidarPortException extends LidarException {
    public NoSuchLidarPortException(String message) {
        super(message);
    }
}
