package frc.lidar.lib;

/**
 * Represents one measurement from the lidar.
 *
 * @author rmheuer
 */
public class ScanEntry {
    private final double angle;
    private final double distance;

    /**
     * Creates a new instance of this class with the
     * given data.
     *
     * @param angle the angle in radians
     * @param distance the distance in meters
     */
    public ScanEntry(double angle, double distance) {
        this.angle = angle;
        this.distance = distance;
    }

    /**
     * Gets tha angle of this measurement.
     *
     * @return the angle in radians
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Gets the distance of this measurement.
     *
     * @return the distance in meters
     */
    public double getDistance() {
        return distance;
    }
}
