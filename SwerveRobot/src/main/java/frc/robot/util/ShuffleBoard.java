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
    
    public static ShuffleboardTab tuneTab = Shuffleboard.getTab("Tune");
        private static ShuffleboardLayout tune = tuneTab.getLayout("Tune", BuiltInLayouts.kList);
        
        public static NetworkTableEntry hoodPosition = tune.addPersistent("Hood Position (0-4)", 0).getEntry();
        public static NetworkTableEntry shooterFlywheelVelocity = tune.addPersistent("Shooter Flywheel Velocity", SHOOTER_IDLE_VELOCITY).getEntry();
}
