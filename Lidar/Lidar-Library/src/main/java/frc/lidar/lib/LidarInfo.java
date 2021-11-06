package frc.lidar.lib;

/**
 * Represents the information relating to the
 * physical lidar connected to the computer.
 *
 * @author rmheuer
 */
public class LidarInfo {
    private final int modelId;
    private final int firmwareMinor;
    private final int firmwareMajor;
    private final int hardwareVersion;
    private final long serialNumberLow;
    private final long serialNumberHigh;

    /**
     * Creates a new instance of this class with the
     * given data.
     *
     * @param modelId numerical id of the lidar model
     * @param firmwareMinor decimal part of the version number
     * @param firmwareMajor integer part of the version number
     * @param hardwareVersion numerical hardware version number
     * @param serialNumberLow lower 64 bits of the serial number
     * @param serialNumberHigh upper 64 bits of the serial number
     */
    public LidarInfo(int modelId, int firmwareMinor, int firmwareMajor, int hardwareVersion, long serialNumberLow, long serialNumberHigh) {
        this.modelId = modelId;
        this.firmwareMinor = firmwareMinor;
        this.firmwareMajor = firmwareMajor;
        this.hardwareVersion = hardwareVersion;
        this.serialNumberLow = serialNumberLow;
        this.serialNumberHigh = serialNumberHigh;
    }

    /**
     * Gets the numerical model number of the lidar.
     *
     * @return numerical id of the lidar model
     */
    public int getModelId() {
        return modelId;
    }

    /**
     * Gets the minor part of the firmware version number.
     *
     * @return decimal part of the version number
     */
    public int getFirmwareMinor() {
        return firmwareMinor;
    }

    /**
     * Gets the major part of the firmware version number.
     *
     * @return integer part of the version number
     */
    public int getFirmwareMajor() {
        return firmwareMajor;
    }

    /**
     * Gets the lidar's hardware version number.
     *
     * @return numerical hardware version number
     */
    public int getHardwareVersion() {
        return hardwareVersion;
    }

    /**
     * Gets the lower 64 bits of the serial number.
     *
     * @return lower 64 bits of the serial number
     */
    public long getSerialNumberLow() {
        return serialNumberLow;
    }

    /**
     * Gets the upper 64 bits of the serial number.
     *
     * @return upper 64 bits of the serial number
     */
    public long getSerialNumberHigh() {
        return serialNumberHigh;
    }

    @Override
    public String toString() {
        return "LidarInfo{" +
                "modelId=" + modelId +
                ", firmwareMinor=" + firmwareMinor +
                ", firmwareMajor=" + firmwareMajor +
                ", hardwareVersion=" + hardwareVersion +
                ", serialNumberLow=" + serialNumberLow +
                ", serialNumberHigh=" + serialNumberHigh +
                '}';
    }
}
