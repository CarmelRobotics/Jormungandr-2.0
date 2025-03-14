package frc.robot.subsystems;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdleConfiguration;
import com.ctre.phoenix.led.ColorFlowAnimation;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class EndEffector extends SubsystemBase{
    EndEffectorState currentEndEffectorState;
    TalonFX rollerMotor;
    TalonFX algaeSlap;
    DigitalInput beamBreak;
    CANdle led;
    static StrobeAnimation strobe;
    static ColorFlowAnimation red;
    static ColorFlowAnimation flow;
    public EndEffector(){
        //init motors and CANdle (LED driver) as well as the LED animations
        rollerMotor = new TalonFX(IntakeConstants.kIntakeCANID, IntakeConstants.kSuperStructureCanivore);
        algaeSlap = new TalonFX(IntakeConstants.kAlgaeSlapCANID, IntakeConstants.kSuperStructureCanivore);
        beamBreak = new DigitalInput(1);
        led = new CANdle(IntakeConstants.kCandleCANID, IntakeConstants.kSuperStructureCanivore);
        CANdleConfiguration config = new CANdleConfiguration();
        config.stripType = LEDStripType.RGB; // set the strip type to RGB
        led.configAllSettings(config);
        StrobeAnimation strobe = new StrobeAnimation(0, 255, 0);
        RainbowAnimation rainbow = new RainbowAnimation(1,.5,IntakeConstants.kLEDCount);
        led.animate(rainbow);
        ColorFlowAnimation flow = new ColorFlowAnimation(0, 255, 0);
        ColorFlowAnimation red = new ColorFlowAnimation(255, 0, 0, 0, 1, IntakeConstants.kLEDCount,ColorFlowAnimation.Direction.Forward );

      

    }
    public enum EndEffectorState{
        //holds states, each state holds the voltage and led pattern for that state
        INTAKING(LEDState.RED, 10),
        HOLDING(LEDState.FLASHGREEN,0),
        OUTTAKING(LEDState.SCORE, -10),
        NEUTRAL(LEDState.RED,0);
        public LEDState state;
        public double voltage;
        private EndEffectorState(LEDState state, double voltage){
            this.state = state;
            this.voltage = voltage;
        }
        public enum LEDState{
            RED(red),
            FLASHGREEN(strobe),
            SCORE(flow);
            Animation leds;
            private LEDState(Animation leds){
                this.leds = leds;
            }
        }
    }
    //runs every 20ms
    @Override
    public void periodic() {
        // if beam break sensor is triggered and trying to intake, set the state to the holding state
        if(beamBreak.get() && this.currentEndEffectorState == EndEffectorState.INTAKING){
            setStateInternal(EndEffectorState.HOLDING);
        }
        if(this.currentEndEffectorState == EndEffectorState.OUTTAKING && !beamBreak.get()){
            setStateInternal(EndEffectorState.NEUTRAL);
        }
        //animate leds based off current state
        this.led.animate(this.currentEndEffectorState.state.leds);
        this.rollerMotor.setVoltage(this.currentEndEffectorState.voltage);


        
    }
    //sets end effector state (only for internal class use)
    private void setStateInternal(EndEffectorState newState){
        this.currentEndEffectorState = newState;
    }
    //state setter command for use in robotcontainer
    public Command setState(EndEffectorState state){
        return runOnce(()->{
            setStateInternal(state);
        });
    }
    public Command moveAlgaeUp(){
        return new Command() {
            @Override
            //when command is executed, set the algae slap motor to 65% power
            public void execute() {
                algaeSlap.set(.65);
            }
            // when command ends (button released) stop algae slap motor
            @Override
            public void end(boolean interrupted) {
                algaeSlap.set(0);
            }
        };
    }
    //same as above but set power to 65% power the other way
    public Command moveAlgaeDown(){
        return new Command() {
            @Override
            public void execute() {
                algaeSlap.set(.65 * -1);
            }
            @Override
            public void end(boolean interrupted) {
                algaeSlap.set(0);
            }
        };
    }

}