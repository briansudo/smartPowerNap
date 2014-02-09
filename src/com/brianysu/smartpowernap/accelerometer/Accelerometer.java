package com.brianysu.smartpowernap.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/** The accelerometer. */
public class Accelerometer implements SensorEventListener {
	
	/** Tag for the accelerometer. */
	private static final String TAG = "accelerometer";
	
	/** The X, Y, and Z values for the accelerometer. */
	private float mX, mY, mZ;
	
	/** Access the device's sensors. */
	private SensorManager mSensorManager;
	/** The accelerometer sensor. */
	private Sensor mAccelerometer;
	
	/** App Context. */
	private Context mAppContext;
	
	/** Create a new instance of Accelerometer. */
	public Accelerometer(Context appContext) {
		mAppContext = appContext;
		mSensorManager = (SensorManager) mAppContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
	}

	/** Start gathering data from the accelerometer. */
	public void start() {
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		Log.d(TAG, "Accelerometer started");
	}
	
	/** Stop gathering data from the accelerometer and release the listener. */
	public void stop() {
		mSensorManager.unregisterListener(this);
		Log.d(TAG, "Accelerometer stopped");
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do nothing here.
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		mX = event.values[0];
		mY = event.values[1];
		mZ = event.values[2];
	}

	public float getX() {
		return mX;
	}

	public float getY() {
		return mY;
	}

	public float getZ() {
		return mZ;
	}

	public Context getAppContext() {
		return mAppContext;
	}
	
	
}
