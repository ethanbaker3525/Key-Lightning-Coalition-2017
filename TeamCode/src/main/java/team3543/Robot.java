/*
 * Copyright (c) 2017 Titan Robotics Club (http://www.titanrobotics.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package team3543;

import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import ftclib.FtcAndroidTone;
import ftclib.FtcDcMotor;
import ftclib.FtcMRGyro;
import ftclib.FtcOpMode;
import ftclib.FtcRobotBattery;
import hallib.HalDashboard;
import trclib.TrcDbgTrace;
import trclib.TrcDriveBase;
import trclib.TrcEvent;
import trclib.TrcGyro;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot;
import trclib.TrcUtil;

public class Robot implements TrcPidController.PidInput
{
    private static final boolean USE_SPEECH = true;

    private static final String moduleName = "Robot";
    //
    // Global objects.
    //
    public HalDashboard dashboard;
    public TrcDbgTrace tracer;
    //
    // Text To Speech.
    //
    public TextToSpeech textToSpeech = null;
    //
    // Sensors.
    //
    TrcGyro gyro = null;
    double targetHeading = 0.0;
    //
    // DriveBase subsystem.
    //
    FtcDcMotor leftFrontWheel = null;
    FtcDcMotor rightFrontWheel = null;
    FtcDcMotor leftRearWheel = null;
    FtcDcMotor rightRearWheel = null;
    TrcDriveBase driveBase = null;
    FtcRobotBattery battery = null;

    TrcPidController encoderXPidCtrl = null;
    TrcPidController encoderYPidCtrl = null;
    TrcPidController gyroPidCtrl = null;
    TrcPidDrive pidDrive = null;
    //
    // Other subsystems.
    //

    public Robot(TrcRobot.RunMode runMode)
    {
        //
        // Initialize global objects.
        //
        HardwareMap hardwareMap = FtcOpMode.getInstance().hardwareMap;
        dashboard = HalDashboard.getInstance();
        FtcRobotControllerActivity activity = (FtcRobotControllerActivity)hardwareMap.appContext;
        hardwareMap.logDevices();
        dashboard.setTextView((TextView)activity.findViewById(R.id.textOpMode));
        tracer = FtcOpMode.getGlobalTracer();
        //
        // Text To Speech.
        //
        if (USE_SPEECH)
        {
            textToSpeech = FtcOpMode.getInstance().getTextToSpeech();
        }
        //
        // Initialize sensors.
        //
        gyro = new FtcMRGyro("gyroSensor");
        ((FtcMRGyro)gyro).calibrate();

        //
        // Initialize DriveBase.
        //
        leftFrontWheel = new FtcDcMotor("leftFrontWheel");
        rightFrontWheel = new FtcDcMotor("rightFrontWheel");
        leftRearWheel = new FtcDcMotor("leftRearWheel");
        rightRearWheel = new FtcDcMotor("rightRearWheel");

        leftFrontWheel.motor.setMode(RobotInfo.DRIVE_MOTOR_MODE);
        rightFrontWheel.motor.setMode(RobotInfo.DRIVE_MOTOR_MODE);
        leftRearWheel.motor.setMode(RobotInfo.DRIVE_MOTOR_MODE);
        rightRearWheel.motor.setMode(RobotInfo.DRIVE_MOTOR_MODE);

        leftFrontWheel.setInverted(true);
        leftRearWheel.setInverted(true);

        driveBase = new TrcDriveBase(leftFrontWheel, leftRearWheel, rightFrontWheel, rightRearWheel, gyro);
        driveBase.setXPositionScale(RobotInfo.ENCODER_X_INCHES_PER_COUNT);
        driveBase.setYPositionScale(RobotInfo.ENCODER_Y_INCHES_PER_COUNT);

        battery = new FtcRobotBattery();
        //
        // Initialize tone device.
        //
        FtcAndroidTone androidTone = new FtcAndroidTone("AndroidTone");
        //
        // Initialize PID drive.
        //
        encoderXPidCtrl = new TrcPidController(
                "encoderXPidCtrl",
                RobotInfo.ENCODER_X_KP, RobotInfo.ENCODER_X_KI, RobotInfo.ENCODER_X_KD, RobotInfo.ENCODER_X_KF,
                RobotInfo.ENCODER_X_TOLERANCE, RobotInfo.ENCODER_X_SETTLING,
                this);
        encoderYPidCtrl = new TrcPidController(
                "encoderYPidCtrl",
                RobotInfo.ENCODER_Y_KP, RobotInfo.ENCODER_Y_KI, RobotInfo.ENCODER_Y_KD, RobotInfo.ENCODER_Y_KF,
                RobotInfo.ENCODER_Y_TOLERANCE, RobotInfo.ENCODER_Y_SETTLING,
                this);
        gyroPidCtrl = new TrcPidController(
                "gyroPidCtrl",
                RobotInfo.GYRO_KP, RobotInfo.GYRO_KI, RobotInfo.GYRO_KD, RobotInfo.GYRO_KF,
                RobotInfo.GYRO_TOLERANCE, RobotInfo.GYRO_SETTLING,
                this);
        gyroPidCtrl.setAbsoluteSetPoint(true);
        gyroPidCtrl.setOutputRange(-RobotInfo.TURN_POWER_LIMIT, RobotInfo.TURN_POWER_LIMIT);

        pidDrive = new TrcPidDrive("pidDrive", driveBase, encoderXPidCtrl, encoderYPidCtrl, gyroPidCtrl);
        pidDrive.setStallTimeout(RobotInfo.PIDDRIVE_STALL_TIMEOUT);
        pidDrive.setBeep(androidTone);

        //
        // Initialize other subsystems.
        //

        //
        // Wait for gyro calibration to complete if not already.
        //
        while (gyro.isCalibrating())
        {
            TrcUtil.sleep(10);
        }
        //
        // Tell the driver initialization is complete.
        //
        if (textToSpeech != null)
        {
            textToSpeech.speak("Initialization complete!", TextToSpeech.QUEUE_FLUSH, null);
        }
    }   //Robot

    void startMode(TrcRobot.RunMode runMode)
    {
        //
        // Since our gyro is analog, we need to enable its integrator.
        //
        gyro.setEnabled(true);
        //
        // Reset all X, Y and heading values.
        //
        driveBase.resetPosition();
        targetHeading = 0.0;
    }   //startMode

    void stopMode(TrcRobot.RunMode runMode)
    {
        //
        // Disable the gyro integrator.
        //
        gyro.setEnabled(false);

        if (textToSpeech != null)
        {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }   //stopMode

    //
    // Implements TrcPidController.PidInput
    //

    @Override
    public double getInput(TrcPidController pidCtrl)
    {
        double input = 0.0;

        if (pidCtrl == encoderXPidCtrl)
        {
            input = driveBase.getXPosition();
        }
        else if (pidCtrl == encoderYPidCtrl)
        {
            input = driveBase.getYPosition();
        }
        else if (pidCtrl == gyroPidCtrl)
        {
            input = driveBase.getHeading();
        }

        return input;
    }   //getInput

    private void setDrivePID(double xDistance, double yDistance, double heading)
    {
        double degrees = Math.abs(heading - driveBase.getHeading());
        xDistance = Math.abs(xDistance);
        yDistance = Math.abs(yDistance);
        //
        // No oscillation if turn-only.
        //
        boolean noOscillation = degrees != 0.0 && xDistance == 0.0 && yDistance == 0.0;
        gyroPidCtrl.setNoOscillation(noOscillation);
        tracer.traceInfo("setDrivePID", "NoOscillation=%s", Boolean.toString(noOscillation));
        if (xDistance != 0.0 && xDistance < RobotInfo.SMALL_X_THRESHOLD)
        {
            //
            // Small X movement, use stronger X PID to overcome friction.
            //
            encoderXPidCtrl.setPID(
                    RobotInfo.ENCODER_SMALL_X_KP, RobotInfo.ENCODER_SMALL_X_KI, RobotInfo.ENCODER_SMALL_X_KD, 0.0);
        }
        else
        {
            //
            // Use normal X PID.
            //
            encoderXPidCtrl.setPID(RobotInfo.ENCODER_X_KP, RobotInfo.ENCODER_X_KI, RobotInfo.ENCODER_X_KD, 0.0);
        }

        if (yDistance != 0.0 && yDistance < RobotInfo.SMALL_Y_THRESHOLD)
        {
            //
            // Small Y movement, use stronger Y PID to overcome friction.
            //
            encoderYPidCtrl.setPID(
                    RobotInfo.ENCODER_SMALL_Y_KP, RobotInfo.ENCODER_SMALL_Y_KI, RobotInfo.ENCODER_SMALL_Y_KD, 0.0);
        }
        else
        {
            //
            // Use normal Y PID.
            //
            encoderYPidCtrl.setPID(RobotInfo.ENCODER_Y_KP, RobotInfo.ENCODER_Y_KI, RobotInfo.ENCODER_Y_KD, 0.0);
        }

        if (degrees != 0.0 && degrees < RobotInfo.SMALL_TURN_THRESHOLD)
        {
            //
            // Small turn, use stronger turn PID to overcome friction.
            //
            gyroPidCtrl.setPID(
                    RobotInfo.GYRO_SMALL_TURN_KP, RobotInfo.GYRO_SMALL_TURN_KI, RobotInfo.GYRO_SMALL_TURN_KD, 0.0);
        }
        else
        {
            //
            // Use normal Y PID.
            //
            gyroPidCtrl.setPID(RobotInfo.GYRO_KP, RobotInfo.GYRO_KI, RobotInfo.GYRO_KD, 0.0);
        }
    }   //setDrivePID

    void setPIDDriveTarget(
            double xDistance, double yDistance, double heading, boolean holdTarget, TrcEvent event)
    {
        setDrivePID(xDistance, yDistance, heading);
        pidDrive.setTarget(xDistance, yDistance, heading, holdTarget, event);
    }   //setPIDDriveTarget

    void traceStateInfo(double elapsedTime, String stateName, double xDistance, double yDistance, double heading)
    {
        tracer.traceInfo(
                moduleName,
                "[%5.3f] %17s: xPos=%6.2f/%6.2f,yPos=%6.2f/%6.2f,heading=%6.1f/%6.1f,volt=%5.2fV(%5.2fV)",
                elapsedTime, stateName,
                driveBase.getXPosition(), xDistance, driveBase.getYPosition(), yDistance,
                driveBase.getHeading(), heading,
                battery.getVoltage(), battery.getLowestVoltage());
    }   //traceStateInfo

}   //class Robot
