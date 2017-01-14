package org.firstinspires.ftc.teamcode.navigation;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.core.BeaconColor;
import org.firstinspires.ftc.teamcode.core.Picture;
import org.firstinspires.ftc.teamcode.core.Speed;

/**
 * Created by Marie on 1/3/2017.
 */
@Autonomous(name="Wheels Test", group ="Test")
public class WheelsTest extends BaseNavigation{

    @Override
    protected void navigate() {

        Speed speed = Speed.speed7;
        sleep(1000);


        moveToPosition(Picture.wheels.getX(20)  , Picture.wheels.getY(20), speed);

        sleep(1000);
        moveToPosition(Picture.wheels.getX(5)  , Picture.wheels.getY(5), speed);


        pushBeacon(BeaconColor.red);

        vuforia.telemetryUpdate(telemetry);

        sleep(10000);
    }

}
