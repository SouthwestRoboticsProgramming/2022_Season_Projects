package frc.lidar.lib;

/**
 * Enumerates the possible health states of the lidar.
 *
 * @author rmheuer
 */
public enum LidarHealth {
    /**
     * Indicates that the lidar is functioning normally
     * and is able to successfully scan the environment.
     */
    GOOD,

    /**
     * Indicates that the lidar is able to scan the
     * environment, but has detected that there is
     * potential risk of future hardware failure.
     */
    WARNING,

    /**
     * Indicates that a hardware failure has occurred
     * in the lidar, and that it is unable to scan
     * the environment.
     */
    ERROR;
}
