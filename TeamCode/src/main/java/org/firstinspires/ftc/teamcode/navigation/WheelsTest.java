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

        Speed speed = Speed.speed3;
        sleep(1000);

        //findPicture();

        moveToPosition(Picture.wheels.getX(24)  , Picture.wheels.getY(24), speed);

        //findPicture();
        moveToPosition(Picture.wheels.getX(6)  , Picture.wheels.getY(6), speed);


        pushBeacon(BeaconColor.blue);

        vuforia.telemetryUpdate(telemetry);

        sleep(10000);
    }

}
