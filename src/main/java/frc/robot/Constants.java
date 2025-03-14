// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    //used to map the jumbled garbage of the guitar outputs to usable data. (the outputs are all over the place so it is nice to have them as actual variables so we know which is which)
    // these will be updated to their appropriate values once i figure out which buttonn is which output
    public static final int kGreen = 0;
    public static final int kRed = 0;
    public static final int kYellow = 0;
    public static final int kBlue = 0;
    public static final int kOrange = 0;


  } 
  public static final class ArmConstants{
    //set constants for arm
    public static final int kPivotOneCANID = 15;

    public static final double L2 = 150;
    public static final double L3 = 200;
    public static final double L4 = 300;
    public static final double Stow = 0;



  }
  public static final class IntakeConstants{
    public static final int kLEDCount = 80;
    public static final int kCandleCANID = 3;
    public static final int kAlgaeSlapCANID = 2;
    public static final int kIntakeCurrentLimit = 30;
    public static final int kIntakeCANID = 1;
    //should move this out of intake constants eventually
    public static final String kSuperStructureCanivore = "superstructure";
  }
  //no way i made a whole class for one constant :clown:
  public static final class ClimbConstants{
    public static final int kClimbMotorCANID = 21;
  }
}
