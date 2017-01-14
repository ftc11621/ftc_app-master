package org.firstinspires.ftc.teamcode.navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.core.BeaconColor;
import org.firstinspires.ftc.teamcode.core.ButtonPusher;
import org.firstinspires.ftc.teamcode.core.ColorSense;
import org.firstinspires.ftc.teamcode.core.Launcher;
import org.firstinspires.ftc.teamcode.core.ParticleDoor;
import org.firstinspires.ftc.teamcode.core.RobotDriver;
import org.firstinspires.ftc.teamcode.core.Speed;
import org.firstinspires.ftc.teamcode.core.VuforiaSensor;

/**
 * Created by HeavenDog on 11/19/2016.
 */

public abstract class BaseNavigation extends LinearOpMode {

    protected final double InchesToCentimeters = 2.54;
    protected RobotDriver robotDriver;
    protected VuforiaSensor vuforia;
    protected Launcher launcher;
    protected ButtonPusher buttonPusher;
    protected ElapsedTime runtime= new ElapsedTime();




    @Override public void runOpMode() {

        vuforia = new VuforiaSensor();
        robotDriver = new RobotDriver(hardwareMap);
        launcher = new Launcher(hardwareMap);
        buttonPusher = new ButtonPusher(hardwareMap);

        //ElapsedTime runtime = new ElapsedTime();



        baseLog(">", "Press Play to start tracking");
        waitForStart();
        vuforia.activate();
        navigate();







    }

    protected void baseLog(String key, String messasge) {
        telemetry.addData(key, messasge);
        telemetry.update();
    }

    protected abstract void navigate();

    public boolean moveToPosition(double destination_x, double destination_y, Speed speed) {   // in mm
        runtime.reset();

        while(runtime.seconds()< 5.0 && !vuforia.isWheel_visible() && !vuforia.isLego_visible() && !vuforia.isTools_visible() && !vuforia.isGears_visible()) { // 5 sec timeout to find a blue pattern
            telemetry.addData("Vuforia", "NOT visible");
            telemetry.update();
        }

        if (!vuforia.isWheel_visible() && !vuforia.isLego_visible() && !vuforia.isTools_visible() && !vuforia.isGears_visible()) {
            return false;
        }

        telemetry.addData("Vuforia picture", "Visible");
        telemetry.update();

        runtime.reset();    // 5 seconds timeout if it can't find location
//        while(runtime.seconds() < 5.0 && !vuforia.updateRobotLocation()) {
        if(vuforia.updateRobotLocation()) {
            vuforia.telemetryUpdate(telemetry);
            double toAngle = vuforia.getRobotNeedToTurnAngle(destination_x, destination_y);
            robotDriver.turnToAngle(0, toAngle);
            sleep(763);
            vuforia.updateRobotLocation();
            toAngle = vuforia.getRobotNeedToTurnAngle(destination_x, destination_y);
            robotDriver.turnToAngle(0, toAngle);
            vuforia.updateRobotLocation();
            //Turn to desired angle
            //check the angle to see how close it is to to the disired angle


            runtime.reset();

            double distance_CM = 0.1 * vuforia.getDestinationDistance(destination_x, destination_y); // in CM
            telemetry.addData("DistanceToTargetCM", distance_CM/2.54);
            vuforia.telemetryUpdate(telemetry);

            robotDriver.setSpeed(speed);
            robotDriver.go(speed, distance_CM);
            sleep(200);
        }
        if(runtime.seconds()>=5.0) { // fail to update location by timeout
            return false;
        }
        //stop();
        return true;
    }

    // to move a distance then shoot two particles
    public void moveAndShoot(double distance_to_move) {
        ParticleDoor partDoor = new ParticleDoor(hardwareMap); // on top to make sure it opens before interrupt
        robotDriver.go(Speed.speed7, distance_to_move * InchesToCentimeters); // negative for intake front

        launcher.shoot();   // shoot 1st particle
        partDoor.openDoor();
        runtime.reset();
        while (runtime.seconds() < 1.0) {    // wait for the chance the door is fully open
        }
        launcher.shoot();   // shoot 2nd particle
        partDoor.closeDoor();  // to close the door at the end
        runtime.reset();
        while (runtime.seconds() < 1.0) {    // wait for the chance the door to close
        }
    }

    protected void pushBeacon(BeaconColor beaconColor) {
        ColorSense colorSense = new ColorSense(hardwareMap);
        robotDriver.turnToAngle(0,-12);
        /*if (BeaconColor.neither.equals(colorSense.senseColor())) {
            robotDriver.turnToAngle();
        }*/
        if(!beaconColor.equals(colorSense.senseColor())){
            robotDriver.go(Speed.speed2, 10);
        } else {
            robotDriver.turnToAngle(0,24);
            robotDriver.go(Speed.speed2, 10);
        }
        sleep(1000);
        if(!beaconColor.equals(colorSense.senseColor())) {
            robotDriver.go(Speed.speed3, -5);
            robotDriver.go(Speed.speed3, 5);
        }
    }
}
