package frc.lidar.lib.exceptions;

/**
 * An exception from a lidar.
 *
 * @author rmheuer
 */
public class LidarException extends RuntimeException {
    public LidarException(String message) {
        super(message);
    }
}
