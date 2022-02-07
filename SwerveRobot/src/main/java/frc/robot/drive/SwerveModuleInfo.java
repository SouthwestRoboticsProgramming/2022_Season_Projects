package frc.robot.drive;

public final class SwerveModuleInfo {
    private final int driveId;
    private final int canCoderId;
    private final double canCoderOffset;

    public SwerveModuleInfo(int driveId, int canCoderId, double canCoderOffset) {
        this.driveId = driveId;
        this.canCoderId = canCoderId;
        this.canCoderOffset = canCoderOffset;
    }

    public int getDriveId() {
        return driveId;
    }

    public int getCanCoderId() {
        return canCoderId;
    }

    public double getCanCoderOffset() {
        return canCoderOffset;
    }
}
