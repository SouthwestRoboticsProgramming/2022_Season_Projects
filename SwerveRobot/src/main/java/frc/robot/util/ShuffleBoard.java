package frc.robot.util;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

import static frc.robot.constants.DriveConstants.*;
import static frc.robot.constants.ShooterConstants.*;


public class ShuffleBoard {
    public static ShuffleboardTab driveTab = Shuffleboard.getTab("Drive");
        private static ShuffleboardLayout drive = driveTab.getLayout("drive", BuiltInLayouts.kList);
        public static NetworkTableEntry wheelTurnKP = drive.addPersistent("Wheel Turn KP",WHEEL_TURN_KP).getEntry();
    
    public static ShuffleboardTab tuneTab = Shuffleboard.getTab("tune it");
        private static ShuffleboardLayout tune = tuneTab.getLayout("tune", BuiltInLayouts.kList);
        public static NetworkTableEntry flywheelKS = tune.addPersistent("KS", SHOOTER_KS).getEntry();
        public static NetworkTableEntry flywheelKV = tune.addPersistent("KV", SHOOTER_KV).getEntry();
        public static NetworkTableEntry flywheelKA = tune.addPersistent("KA", SHOOTER_KA).getEntry();
}
