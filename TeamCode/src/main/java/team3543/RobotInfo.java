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

import com.qualcomm.robotcore.hardware.DcMotor;

class RobotInfo
{
    //
    // DriveBase subsystem.
    //
    static final float MM_PER_INCH                      = 25.4f;

    static final DcMotor.RunMode DRIVE_MOTOR_MODE       = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
    static final double TURN_POWER_LIMIT                = 0.5;

    //
    // INCHES_PER_COUNT: 0.0132166817227156
    // 12/06/2016: 0.2, 0.0, 0.0 [< 8] 0.2, 0.0, 0.0 (No speed control)
    //
    static final double ENCODER_X_KP                    = 0.2;
    static final double ENCODER_X_KI                    = 0.0;
    static final double ENCODER_X_KD                    = 0.0;
    static final double ENCODER_X_KF                    = 0.0;
    static final double ENCODER_X_TOLERANCE             = 2.0;
    static final double ENCODER_X_SETTLING              = 0.2;
    static final double ENCODER_X_INCHES_PER_COUNT      = 0.0132166817227156;

    static final double SMALL_X_THRESHOLD               = 8.0;
    static final double ENCODER_SMALL_X_KP              = 0.2;
    static final double ENCODER_SMALL_X_KI              = 0.0;
    static final double ENCODER_SMALL_X_KD              = 0.0;
    //
    // INCHES_PER_COUNT: 0.01667
    // 12/06/2016: 0.045, 0.0, 0.0045 (No speed control)
    // 12/22/2016: 0.046, 0.0, 0.0046 (No speed control)
    // 01/09/2017: 0.02, 0.0, 0.0022 [< 8] 0.045, 0.0, 0.001 (No speed control)
    //
    static final double ENCODER_Y_KP                    = 0.02;
    static final double ENCODER_Y_KI                    = 0.0;
    static final double ENCODER_Y_KD                    = 0.0022;
    static final double ENCODER_Y_KF                    = 0.0;
    static final double ENCODER_Y_TOLERANCE             = 2.0;
    static final double ENCODER_Y_SETTLING              = 0.2;
    static final double ENCODER_Y_INCHES_PER_COUNT      = 0.01667;

    static final double SMALL_Y_THRESHOLD               = 8.0;
    static final double ENCODER_SMALL_Y_KP              = 0.045;
    static final double ENCODER_SMALL_Y_KI              = 0.0;
    static final double ENCODER_SMALL_Y_KD              = 0.001;
    //
    // Accurate PID but slow (with speed control ON): 0.02, 0.0, 0.0028
    // Faster PID but may oscillate (with speed control ON): 0.022, 0.0, 0.0046 (limit to half power for mid-range)
    // 12/06/2016: 0.021, 0.0, 0.0021; [< 15] 0.024, 0.0, 0.002;    (No speed control, 0.75 power limit)
    // 12/07/2016: 0.021, 0.0, 0.0021; [< 15] 0.023, 0.0, 0.0023;   (No speed control, 0.75 power limit)
    // 12/07/2016: 0.02,  0.0, 0.0022; [< 15] 0.024, 0.0, 0.0024;   (No speed control, 0.75 power limit)
    // 12/08/2016: 0.02,  0.0, 0.0022; [< 15] 0.055, 0.0, 0.011;    (No speed control, 0.75 power limit)
    // 12/10/2016: 0.022, 0.0, 0.0024; [< 15] 0.028, 0.0, 0.0;      (No speed control, 0.75 power limit)
    // 01/09/2017: 0.024, 0.0, 0.024; [< 15] 0.03, 0.0, 0.001;        (No speed control, 0.75 power limit)
    //
    static final double ANALOG_GYRO_SCALE               = 1.0136;
    static final double ANALOG_GYRO_VOLT_PER_DEG_PER_SEC= 0.007;
    static final double GYRO_KP                         = 0.024;
    static final double GYRO_KI                         = 0.0;
    static final double GYRO_KD                         = 0.0024;
    static final double GYRO_KF                         = 0.0;
    static final double GYRO_TOLERANCE                  = 2.0;
    static final double GYRO_SETTLING                   = 0.2;

    static final double SMALL_TURN_THRESHOLD            = 15.0;
    static final double GYRO_SMALL_TURN_KP              = 0.03;
    static final double GYRO_SMALL_TURN_KI              = 0.0;
    static final double GYRO_SMALL_TURN_KD              = 0.001;

    static final double PIDDRIVE_STALL_TIMEOUT          = 0.25;     //in msec.

}   //class RobotInfo
