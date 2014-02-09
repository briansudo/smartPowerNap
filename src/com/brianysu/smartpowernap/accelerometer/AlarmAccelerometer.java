
package com.brianysu.smartpowernap.accelerometer;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

import com.brianysu.smartpowernap.AlarmFragment;
import com.brianysu.smartpowernap.timer.Timer;

/** A subclass of accelerometer that is used to detect when the user is
 * sleeping. */
public class AlarmAccelerometer extends Accelerometer {

	/** Tag for this class. */
	private static final String TAG = "alarmAccelerometer";
	
	/** Length of the period of inactivity. */
	private int mDuration;
	/** System time when the accelerometer begins recording data. */
	private long mStartTime;
	/** Thresholds for X, Y, and Z axis. */
	private static int sThresX = 2, sThresY = 2, sThresZ = 10;
	/** Must Wake Time. */
	private Date mMustWake;
	/** 1 if duration is in seconds. 60 if in minutes. */
	private int mMultiplier;
	/** Average X, Y, and Z values during calibration. */
	private float mTempX, mTempY, mTempZ;
	/** True if the user is calibrating. */
	private boolean mCalibrating;
	/** Timer. */
	private Timer mTimer;
	/** Must Wake Mode. */
	private boolean mMustWakeMode;
	
	/** App context. */
	private Context mAppContext;
	/** Alarm Fragment. */
	private AlarmFragment mAf;
	/** Don't start timer. */
	private boolean mDontStart, mShouldStart;
	
	/** Create a new instance with DURATION as the length of the period of
	 * inactivity before the alarm starts. */
	public AlarmAccelerometer(Context mAppContext, int duration, boolean minutes, AlarmFragment af) {
		super(mAppContext);
		mDuration = duration;
		mMultiplier = minutes ? 60000 : 1000;
		mMustWakeMode = false;
		mAppContext = mAppContext;
		mAf = af;
		mDontStart = false;
		mShouldStart = false;
	}
	
	@Override
	public void start() {
		super.start();
		mStartTime = System.currentTimeMillis();
		Log.d(TAG, "Thresholds: " + sThresX + " " + sThresY + " " + sThresZ);
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		super.onSensorChanged(event);
		
		long mCurrentTime = System.currentTimeMillis();
		float x = getX();
		float y = getY();
		float z = getZ();
		
		int diff = (int) (mCurrentTime - mStartTime) / 1000;
		Log.d(TAG, "X: " + x + " Y: " + y + " Z: " + z + " Time: " + diff + " MW: " + mMustWakeMode);
		
		
		
		if (mCurrentTime - mStartTime < mDuration * mMultiplier) {
			if (mCalibrating) {
				Log.d(TAG, "Calibrating...");
				mTempX = ((mTempX + x) / 2);
				mTempY = ((mTempY + y) / 2);
				mTempZ = ((mTempZ + z) / 2);
			} else {
				if (mMustWakeMode) {
					Calendar current = Calendar.getInstance();
					Calendar mustWake = Calendar.getInstance();
					mustWake.setTime(mMustWake);
					int hour = current.get(Calendar.HOUR_OF_DAY);
					int min = current.get(Calendar.MINUTE);
					int hour2 = mustWake.get(Calendar.HOUR_OF_DAY);
					int min2 = mustWake.get(Calendar.MINUTE);
					Log.d(TAG, "Time after: " + (hour > hour2 || (hour == hour2 && min > min2)));
					Log.d(TAG, "Time: " + hour + " " + hour2 + " " + min + " " + min2);
					if (hour > hour2 || (hour == hour2 && min > min2)) {
						stop();
						mDontStart = true;
						mAf.startAlarm();
						return;
					}
				}
				if (x > sThresX || y > sThresY || z > sThresZ) {
					resetStartTime();
				}
			}
		} else {
			stop();
			if (mCalibrating) {
				sThresX = (int) mTempX + 2;
				sThresY = (int) mTempY + 2;
				sThresZ = (int) mTempZ + 2;
				Log.d(TAG, "Temp Variables: " + mTempX + " " + mTempY + " " + mTempZ);
				Log.d(TAG, "Calibration: " + sThresX + " " + sThresY + " " + sThresZ);
				mCalibrating = false;
			} else {
				mShouldStart = true;
				startAlarmClock();
			}
		}
	}
	
	/** Calibrate the X, Y, Z thresholds. It'll take the average acceleration
	 * over 15 seconds, round down to an integer, and then add 2. */
	public static void calibrate(Context appContext) {
		AlarmAccelerometer a = new AlarmAccelerometer(appContext, 3, false, null);
		a.setCalibrating(true);
		a.resetTemp();
		a.start();
	}

	
	/** Start the alarm clock. */
	public void startAlarmClock() {
		Log.d(TAG, "Start Alarm Clock called");
		if (mDontStart || mShouldStart) {
			mTimer.start();
			Log.d(TAG, "Timer started.");
		}
	}
	
	/** Enable/disable calibrating mode. */
	public void setCalibrating(boolean calibrating) {
		mCalibrating = calibrating;
	}

	/** Reset the temporary X, Y, and Z variables to 0. */
	public void resetTemp() {
		mTempX = mTempY = mTempZ = 0;
	}

	/** Reset mStartTime to the current time. */
	public void resetStartTime() {
		mStartTime = System.currentTimeMillis();
		Log.d(TAG, "Start time reset.");
	}

	public Timer getTimer() {
		return mTimer;
	}

	public void setTimer(Timer timer) {
		mTimer = timer;
	}
	
	public void setMustWakeTime(Date mustWake) {
		mMustWake = mustWake;
	}

	public void setMustWakeMode(boolean on) {
		mMustWakeMode = on;
	}
	

}
