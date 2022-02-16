package frc.robot.util;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import static frc.robot.constants.DriveConstants.*;


public class ShuffleBoard {
    public static ShuffleboardTab driveTab = Shuffleboard.getTab("Drive");
        private static ShuffleboardLayout drive = driveTab.getLayout("drive", BuiltInLayouts.kList);
        public static NetworkTableEntry wheelTurnKP = drive.addPersistent("Wheel Turn KP",WHEEL_TURN_KP).getEntry();

}
