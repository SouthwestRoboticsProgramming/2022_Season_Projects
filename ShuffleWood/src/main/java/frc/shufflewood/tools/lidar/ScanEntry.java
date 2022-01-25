package frc.shufflewood.tools.lidar;

public class ScanEntry {
    private int quality;
    private double angle;
    private double distance;

    public ScanEntry(int quality, double angle, double distance) {
        this.quality = quality;
        this.angle = angle;
        this.distance = distance;
    }

    public int getQuality() {
        return quality;
    }

    public double getAngle() {
        return angle;
    }

    public double getDistance() {
        return distance;
    }
}
