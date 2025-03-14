package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import dev.doglog.DogLog;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.IntakeConstants;

public class Elevator extends SubsystemBase {
    ElevatorState elevatorState;
    final MotionMagicVoltage m_request;
    public TalonFX elevatorMotor;
    public TalonFXConfiguration config;
    public Elevator(){
        configureTalon();
        elevatorState = ElevatorState.STOW;
        m_request = new MotionMagicVoltage(0);
    }
    private void configureTalon(){
        elevatorMotor = new TalonFX(ArmConstants.kPivotOneCANID, IntakeConstants.kSuperStructureCanivore);
        var slot0Configs = config.Slot0;
        slot0Configs.kS = 0.25; // Add 0.25 V output to overcome static friction
        slot0Configs.kV = 0.12; // A velocity target of 1 rps results in 0.12 V output
        slot0Configs.kA = 0.01; // An acceleration of 1 rps/s requires 0.01 V output
        slot0Configs.kP = 4.8; // A position error of 2.5 rotations results in 12 V output
        slot0Configs.kI = 0; // no output for integrated error
        slot0Configs.kD = 0.1; // A velocity error of 1 rps results in 0.1 V output
        var motionMagicConfigs = config.MotionMagic;
        motionMagicConfigs.MotionMagicCruiseVelocity = 80; // Target cruise velocity of 80 rps
        motionMagicConfigs.MotionMagicAcceleration = 160; // Target acceleration of 160 rps/s (0.5 seconds)
        motionMagicConfigs.MotionMagicJerk = 1600; // Target jerk of 1600 rps/s/s (0.1 seconds)
        var encoderConfigs = config.Feedback;
        encoderConfigs.SensorToMechanismRatio = 5;
        //gear ratio of arm is 5:1, so for every 5 rotations of encoder, arm sprocket rotates once
        elevatorMotor.getConfigurator().apply(config);
        
    }
    //elevator states, stow is used for intaking as well as l1
    public enum ElevatorState{
        STOW(ArmConstants.Stow),
        L2(ArmConstants.L2),
        L3(ArmConstants.L3),
        L4(ArmConstants.L4);
        double rotations;
        private ElevatorState(double rotations){
            this.rotations = rotations;
    }
    }
    //only for use in setting elevator state inside this class
    private void setStateInternal(ElevatorState state){
        this.elevatorState = state;
    }
    //this runs every 20ms
    @Override
    public void periodic() {
        //sets the position of the elevator in accordance with the current state's position
        elevatorMotor.setControl(m_request.withPosition(this.elevatorState.rotations));
    }
    // for use in robotcontainer
    public Command setState(ElevatorState state){
        return runOnce(()->{
            setStateInternal(state);
        });
    }


}
