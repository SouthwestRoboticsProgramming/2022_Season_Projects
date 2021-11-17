package frc.lidar.lib;

/**
 * Represents one measurement from the lidar.
 *
 * @author rmheuer
 */
public class ScanEntry {
    private final int quality;
    private final double angle;
    private final double distance;

    /**
     * Creates a new instance of this class with the
     * given data.
     *
     * @param angle the angle in radians
     * @param distance the distance in millimeters
     */
    public ScanEntry(int quality, double angle, double distance) {
        this.quality = quality;
        this.angle = angle;
        this.distance = distance;
    }

    public int getQuality() {
        return quality;
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
     * @return the distance in millimeters
     */
    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "ScanEntry{" +
                "quality=" + quality +
                ", angle=" + angle +
                ", distance=" + distance +
                '}';
    }
}
