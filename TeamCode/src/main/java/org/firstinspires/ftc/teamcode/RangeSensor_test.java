package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

@TeleOp(name="Range Sensors test", group="Test")  // @Autonomous(...) is the other common choice
//@Disabled
public class RangeSensor_test extends OpMode {

    private RangeSensor rangeSensors = null;

    @Override
    public void init() {
        rangeSensors = new RangeSensor(hardwareMap);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        telemetry.addData("Front distance", rangeSensors.get_Front_distance(256));
        telemetry.addData("Front Right distance",  rangeSensors.get_Front_Right_distance(255) );
        telemetry.addData("Right Front distance",  rangeSensors.get_Rear_Right_distance(256) );

        telemetry.update();
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}