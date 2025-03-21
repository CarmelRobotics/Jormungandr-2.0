// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.Constants.OperatorConstants;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.Climb;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.EndEffector;
import frc.robot.subsystems.Elevator.ElevatorState;
import frc.robot.subsystems.EndEffector.EndEffectorState;


public class RobotContainer {

    // public PathPlannerPath oneCoral = PathPlannerPath.fromPathFile("onecoral");


    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second
                                                                                      // max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();
    private final SwerveRequest.FieldCentric auto = new SwerveRequest.FieldCentric();
    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController kController = new CommandXboxController(0);
    private final CommandJoystick kGuitar = new CommandJoystick(1);
    private final SendableChooser<Command> autoChooser;
    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    public final Elevator elevator = new Elevator();
    public final EndEffector endEffector = new EndEffector();
    public final Climb climb = new Climb();

    public RobotContainer() {
        
        // kController = new
        // CommandXboxController(OperatorConstants.kDriverControllerPort);

        configureBindings();
        //Named commands are commands we can use in pathplanner autos
        NamedCommands.registerCommand("Place",new ParallelDeadlineGroup(new WaitCommand(.25), endEffector.setState(EndEffectorState.OUTTAKING)));
        NamedCommands.registerCommand("Extend", new ParallelDeadlineGroup(new WaitCommand(1),elevator.setState(ElevatorState.L4)));
        NamedCommands.registerCommand("Retract", new ParallelDeadlineGroup(new WaitCommand(1),elevator.setState(ElevatorState.STOW)));
        NamedCommands.registerCommand("Intake", new ParallelDeadlineGroup(new WaitCommand(.35), endEffector.setState(EndEffectorState.INTAKING)));
        //creates a chooser on our network tables so we can choose our desired auto on elastic
        autoChooser = AutoBuilder.buildAutoChooser();
        autoChooser.addOption("4 Coral", AutoBuilder.buildAuto("4coral"));
        autoChooser.addOption("1 Coral Center", AutoBuilder.buildAuto("1coralCenter"));
    
    }


    
    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
                // Drivetrain will execute this command periodically
                drivetrain.applyRequest(() -> drive.withVelocityX(-kController.getLeftY() * MaxSpeed) // Drive forward
                                                                                                      // with negative Y
                                                                                                      // (forward)
                        .withVelocityY(-kController.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                        .withRotationalRate(-kController.getRightX() * MaxAngularRate * 1.25) // Drive counterclockwise
                                                                                              // with negative X (left)
                ));
        

        //bind controller buttons
        /*
         * A: stow elevator (intake, L1)
         * B: elevator l2
         * X: elevator l3
         * Y: elevator l4
         * Right Trigger: Intake
         * Left Trigger: Score
         * Left Bumper: move algae arm up
         * Right Bumper: move algae arm down
         */
        kController.a().onTrue(elevator.setState(ElevatorState.STOW));
        kController.b().onTrue(elevator.setState(ElevatorState.L2));
        kController.x().onTrue(elevator.setState(ElevatorState.L3));
        kController.y().onTrue(elevator.setState(ElevatorState.L4));
        kController.rightTrigger().onTrue(endEffector.setState(EndEffectorState.INTAKING));
        kController.leftTrigger().onTrue(endEffector.setState(EndEffectorState.OUTTAKING));
        kController.leftBumper().whileTrue(endEffector.moveAlgaeUp());
        kController.rightBumper().whileTrue(endEffector.moveAlgaeDown());

        //bind guitar buttons
        kGuitar.button(OperatorConstants.kGreen).whileTrue(climb.climbUp());
        kGuitar.button(OperatorConstants.kRed).whileTrue(climb.climbDown());

        

    }

    

    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
            

        // return
        // Commands.sequence(drivetrain.zeroGyro(),Commands.runOnce(()->drivetrain.resetPose(new
        // Pose2d(7.109,3.947,Rotation2d.fromDegrees(180)))),getAutoPath());
    }
}
