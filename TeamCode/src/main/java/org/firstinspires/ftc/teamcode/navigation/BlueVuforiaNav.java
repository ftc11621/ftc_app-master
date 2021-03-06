package org.firstinspires.ftc.teamcode.navigation;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.core.BeaconColor;
import org.firstinspires.ftc.teamcode.core.Picture;
import org.firstinspires.ftc.teamcode.core.Speed;

/**
 * Created by Marie on 1/3/2017.
 */
@Autonomous(name="BlueVuforia", group ="Competition")
public class BlueVuforiaNav extends BaseNavigation{

    @Override
    protected void navigate() {

        Speed speed = Speed.speed6;

       // moveAndShoot(27);

        robotDriver.go(speed, 13 * 2.54);

        robotDriver.turnToAngle(0,55);

        robotDriver.go(speed, 23 * 2.5 );

        sleep(200);

        moveAndShoot(1);

        robotDriver.turnToAngle(0, -55);

        robotDriver.go(speed,13 * 2.54 );

        robotDriver.turnToAngle(0,90);

        sleep(2000);
        speed = Speed.speed4;

        moveToPosition(Picture.wheels.getX(24)  , Picture.wheels.getY(24), speed);
        moveToPosition(Picture.wheels.getX(15), Picture.wheels.getY(15), speed);

        //findPicture();
        moveToPosition(Picture.wheels.getX(6)  , Picture.wheels.getY(6), speed);

        pushBeacon(BeaconColor.blue);

       /* robotDriver.go(speed, -20 * 2.5);

        robotDriver.turnToAngle(0, -95);
        robotDriver.go(speed, 48 * 2.5);
        robotDriver.turnToAngle(0,90);
        moveToPosition(Picture.legos.getX(6)  , Picture.legos.getY(6), speed);
        pushBeacon(BeaconColor.blue);

        vuforia.telemetryUpdate(telemetry);

        sleep(10000);
*/
    }

}
