package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClimbConstants;
import frc.robot.Constants.IntakeConstants;

public class Climb extends SubsystemBase {
    public TalonFX climbMotor;
    //extremely simple subsystem that just moves the climb motor up and down to climb, no automation needed
    //probably should mount a CANRange or other distance sensor to bottom of bellypan pointed down to verify climb? (if distance from sensor > some amount, let driver know that robot has climbed)
    //do automation on climb if time (not a big priority)
    public Climb(){
        climbMotor = new TalonFX(ClimbConstants.kClimbMotorCANID, IntakeConstants.kSuperStructureCanivore);
    }
    //very similar to algae bar command, just moves climber up when command is being executed and stops motor when command stops (this time it is maximum power though)
    public Command climbUp(){
        return new Command(){
            @Override
            public void execute() {
                climbMotor.set(1);
            }
            @Override
            public void end(boolean interrupted) {
                climbMotor.set(0);
            }
        };
    }
    //hopefully we dont have to do this
    public Command climbDown(){
        return new Command(){
            @Override
            public void execute() {
                climbMotor.set(-1);
            }
            @Override
            public void end(boolean interrupted) {
                climbMotor.set(0);
            }
        };
    }



}
