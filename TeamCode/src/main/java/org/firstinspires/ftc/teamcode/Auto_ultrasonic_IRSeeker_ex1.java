/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 Autonomous example move each wheel by distance
 */

@Autonomous(name="Auto IRseeker example", group="Examples")  // @Autonomous(...) is the other common choice
@Disabled
public class Auto_ultrasonic_IRSeeker_ex1 extends LinearOpMode {

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_CM       = 9.15 ;     // For figuring circumference
    static final double     COUNTS_PER_CM           = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_CM * 3.1415);
    static final double     DRIVE_SPEED             = 0.5;
    static final double     TURN_SPEED              = 0.1;
    static final double     WHEELS_SPACING_CM       = 25.4;     // spacing between wheels


    ModernRoboticsI2cRangeSensor rangeSensor;
    IrSeekerSensor irSeeker;    // Hardware Device Object


    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftMotor = null;
    private DcMotor rightMotor = null;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        leftMotor  = hardwareMap.dcMotor.get("motor_2");
        rightMotor = hardwareMap.dcMotor.get("motor_1");

        rangeSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeSensor_1");
        irSeeker = hardwareMap.irSeekerSensor.get("sensor_ir");



        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        rightMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        leftMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0",  "Starting at %7d :%7d",
                leftMotor.getCurrentPosition(),
                rightMotor.getCurrentPosition());
        telemetry.addData("Range: ", "%.2f cm", rangeSensor.getDistance(DistanceUnit.CM));
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();



        /////////////////////////////////////////////////////////////////////
        // WRITE AUTONOMOUS sequence below ===========================================

        // Note: Reverse movement is obtained by setting a negative distance (not speed)
        double target_distance = 10.0;   // 10 cm target distance
        double motor_need_to_go_distance = rangeSensor.getDistance(DistanceUnit.CM) - target_distance;

        while (motor_need_to_go_distance > 0) {
            if (irSeeker.signalDetected()) {
                // Turn toward the beacon
                double wheels_turn_cm = 3.14*WHEELS_SPACING_CM * irSeeker.getAngle()/360.0; // wheels distance to turn to the angle
                if (motor_need_to_go_distance > 50.0) {  // Far a way no need to be precise
                    // add 10% to help it face perpendicular to the beacon, may need tweaking
                    encoderDrive(TURN_SPEED, 1.1*wheels_turn_cm , -1.1*wheels_turn_cm, 30.0);
                } else {
                    while (Math.abs( irSeeker.getAngle()) > 10) {
                        // Turn each wheel opposite direction to spin
                        wheels_turn_cm = 3.14*WHEELS_SPACING_CM * irSeeker.getAngle()/360.0; // wheels distance to turn to the angle
                        encoderDrive(TURN_SPEED, wheels_turn_cm, -wheels_turn_cm, 30.0);

                        // Display angle and strength
                        telemetry.addData("Angle", irSeeker.getAngle());
                        telemetry.addData("Strength", irSeeker.getStrength());
                        telemetry.update();
                    }
                }
            } else {
                // Display loss of signal
                telemetry.addData("Seeker", "Signal Lost");
            }

            if (motor_need_to_go_distance > 50.0) {  // to prevent overshoot
                motor_need_to_go_distance *= 0.5;    // cut the distance by half to allow better angle adjustment
            }
            encoderDrive(DRIVE_SPEED, motor_need_to_go_distance , motor_need_to_go_distance, 15.0);  // S1: Forward 48cm with 5 Sec timeout
            motor_need_to_go_distance = rangeSensor.getDistance(DistanceUnit.CM) - target_distance;

            telemetry.addData("Range: ", "%.2f cm", rangeSensor.getDistance(DistanceUnit.CM));
            telemetry.update();
        }


        ///////////// End of AUTONOMOUS sequence ================================================


        // run until the end of the match (driver presses STOP)
        //while (opModeIsActive()) {
        //    telemetry.addData("Status", "Run Time: " + runtime.toString());
        //    telemetry.update();

            // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
            // leftMotor.setPower(-gamepad1.left_stick_y);
            // rightMotor.setPower(-gamepad1.right_stick_y);

        //    idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        //}
    }

    /*
       *  Method to perfmorm a relative move, based on encoder counts.
       *  Encoders are not reset as the move is based on the current position.
       *  Move will stop if any of three conditions occur:
       *  1) Move gets to the desired position
       *  2) Move runs out of time
       *  3) Driver stops the opmode running.
       */
    public void encoderDrive(double speed,
                             double leftCM, double rightCM,
                             double timeoutS) {

        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = leftMotor.getCurrentPosition() + (int)(leftCM * COUNTS_PER_CM);
            newRightTarget = rightMotor.getCurrentPosition() + (int)(rightCM * COUNTS_PER_CM);
            leftMotor.setTargetPosition(newLeftTarget);
            rightMotor.setTargetPosition(newRightTarget);

            // Turn On RUN_TO_POSITION
            leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftMotor.setPower(Math.abs(speed));
            rightMotor.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftMotor.isBusy() && rightMotor.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        leftMotor.getCurrentPosition(),
                        rightMotor.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            leftMotor.setPower(0);
            rightMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // sleep(250);
        }
    }
}
