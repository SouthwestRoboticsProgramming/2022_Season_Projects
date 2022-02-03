package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class ShuffleboardManager {
    public static final ShuffleboardTab pathTab = Shuffleboard.getTab("Path");
    public static final NetworkTableEntry pathKP = pathTab.add("KP", 0).getEntry();
    public static final NetworkTableEntry pathKI = pathTab.add("KI", 0).getEntry();
    public static final NetworkTableEntry pathKD = pathTab.add("KD", 0).getEntry();
}
