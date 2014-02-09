package com.brianysu.smartpowernap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brianysu.smartpowernap.accelerometer.Accelerometer;
import com.brianysu.smartpowernap.accelerometer.AlarmAccelerometer;
import com.brianysu.smartpowernap.dialog.StaticWakeLock;
import com.brianysu.smartpowernap.dialog.TimePickerFragment;
import com.brianysu.smartpowernap.timer.Timer;

/** Main Fragment. */
public class AlarmFragment extends Fragment {
	
	/** Tag for this fragment. */
	private static final String TAG = "alarmFragment";
	/** TAG for the application colors. */
	private static final String COLOR = "appColors";
	
	/** Application Context. */
	private Context mContext;
	
	/** The background color for the user. The format is "0xFFFFFF 0xFFFFFF".
	 * The first hex represents the background color, while the second one
	 * represents the button color. Initially set to default value. */
	private String mColors = "#33B5E5 #0099CC";
	/** True if 'Must Wake Up By' feature is on. */
	private boolean mMustWakeEnabled = false;
	
	/** The view. */
	private View mView;
	/** Timer and MustWake TestView. */
	private TextView mTimerTextView, mMustWake, mCalibratedTextView;
	/** Set Alarm Button. */
	private Button mAlarmButton;
	
	/** The accelerometer. */
	private AlarmAccelerometer mAccelerometer;
	/** The Timer. */
	private Timer mTimer;
	/** Date. */
	private Date mDate;
	/** Constants. */
	private static final int REQUEST_DATE = 0;
	private static final String DIALOG_DATE = "date";
	
	/** True if the alarm is running. */
	private static boolean sRunning = false;
	
	/** 60000 if minutes mode. 1000 if seconds mode. */
	private int mMultiplier = 1000;
	/** Length of the main alarm. */
	private int mDuration = 5;
	/** Length of the accelerometer countdown. */
	private int mInactiveTime = 5;
	/** True if the the thresholds have been calibrated. */
	private boolean mCalibrated = false;
	/** True if vibration is on. */
	private boolean mVibrate = true;
	/** True if demo mode is on. */
	private boolean mDemoMode = false;
	
	/** A list of used timers. */
	private ArrayList<Timer> mTimers = new ArrayList<Timer>();
	/** A list of used accelerometers. */
	private ArrayList<Accelerometer> mAccelerometers = new ArrayList<Accelerometer>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "On create called");
		mContext = getActivity();
		mCalibrated = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_alarm, parent, false);
		
		configureTextViews();
		configureButton();
		configureColors();
		configureCalibrated();
		
		return mView;
	}

	/** Set the application colors. */
	public void configureColors() {
		String[] colors = mColors.split("\\s+");
		mAlarmButton.setBackgroundColor(Color.parseColor(colors[1]));
		mView.setBackgroundColor(Color.parseColor(colors[0]));	
	}
	
	/** Get views and give them their respective behaviors. */
	public void configureButton() {
		mAlarmButton = (Button) mView.findViewById(R.id.set_alarm_button);
		if (mCalibrated) {
			alarmButtonMode();
		} else {
			calibrateButtonMode();
		}
	}
	
	/** Calibrate Button mode. */
	private void calibrateButtonMode() {
		mAlarmButton.setText(R.string.start);
		mAlarmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				createCalibrateDialog();
				Log.d(TAG, "Calibrate dialog created.");
			}
			
		});
	}
	
	/** Alarm Button mode. */
	private void alarmButtonMode() {
		mAlarmButton.setText(alarmText());
		mAlarmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				createAlarmDialog();		
			}
		});
	}
	
	/** Cancel alarm mode. */
	private void cancelButtonMode() {
		mAlarmButton.setText("Cancel Alarm");
		mAlarmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (Accelerometer a : mAccelerometers) {
					try {
						a.stop();
					} catch (Exception e) {
						// Do nothing
					}
				}
				for (Timer t : mTimers) {
					try {
						mTimer.cancel();
					} catch (Exception e) {
						// Do nothing
					}
				}
				mAccelerometers = new ArrayList<Accelerometer>();
				mTimers = new ArrayList<Timer>();
				
				sRunning = false;
				calibrateButtonMode();
			}
		});
	}
	
	/** Create calibration alert dialog. */
	private void createCalibrateDialog() {
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
		ad.setTitle("Calibrate");
		ad.setMessage(R.string.calibrate_instructions);
		ad.setPositiveButton("Start", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlarmAccelerometer.calibrate(mContext);
				mCalibrated = true;
				configureCalibrated();
				Toast.makeText(mContext, "Calibrating...", Toast.LENGTH_LONG).show();
				configureButton();
			}
		});
		ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		ad.show();
	}
	
	/** Create alarm alert dialog. */
	private void createAlarmDialog() {
		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
		ad.setTitle("Start Power Nap");
		String minOrSec = mDemoMode ? "seconds." : "minutes.";
		ad.setMessage("Click to begin power napping. If the app does not " +
				"detect any major movement for " + mInactiveTime + " " +
				minOrSec + ", it will assume you have fallen asleep and then" +
						" start the " + mDuration + " " + minOrSec + " alarm.");
		ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				createTimer();
				if (mInactiveTime != 0) {
					mAccelerometer.setTimer(mTimer);
					mAccelerometer.start();
				} else {
					mTimer.start();
				}
				sRunning = true;
				Toast.makeText(mContext, "Starting Power Nap...", Toast.LENGTH_LONG).show();
				Log.d(TAG, "Accelerometer initialized");
				cancelButtonMode();
			}
		});
		ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		ad.show();
	}
	
	/** Get views and give them their respective behaviors. */
	public void configureTextViews() {
		mCalibratedTextView = (TextView) mView.findViewById(R.id.calibrated_textview);
		configureCalibrated();
		mCalibratedTextView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				createCalibrateDialog();
				return true;
			}
		});
		mTimerTextView = (TextView) mView.findViewById(R.id.alarm_time);
		resetCountdown();
		mMustWake = (TextView) mView.findViewById(R.id.must_wake_text);
		mMustWake.setTextColor(Color.GRAY);
		mMustWake.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mMustWakeEnabled = !mMustWakeEnabled;
				createTimer();
				createAccelerometer();
				Log.d(TAG, "Must wake enabled: " + mMustWakeEnabled);
				if (!mMustWakeEnabled) {
					mMustWake.setTextColor(Color.GRAY);
				} else {
					mMustWake.setTextColor(Color.WHITE);
				}
				return true;
			}
		});
		if (mDate == null) {
			mDate = new Date();
			updateDate();
			createAccelerometer();
			createTimer();
		}
		mMustWake.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment.newInstance(mDate);
				dialog.setTargetFragment(AlarmFragment.this, REQUEST_DATE);
				dialog.show(fm, DIALOG_DATE);
			}
		});
	}
	
	/** Reset the countdown timer. */
	private void resetCountdown() {
		int millisUntilFinished = mDuration * mMultiplier;
		if ((millisUntilFinished / 1000 % 60) < 10) {
            mTimerTextView.setText(millisUntilFinished / 60000 + ":0"
                    + (millisUntilFinished / 1000) % 60);
        } else {
            mTimerTextView.setText(millisUntilFinished / 60000 + ":"
                    + (millisUntilFinished / 1000) % 60);
        }
	}
	
	/** Create a new timer. */
	private void createTimer() {
		mTimer = new Timer(mDuration * mMultiplier, 1000,
				mTimerTextView, mContext, this);
		mTimers.add(mTimer);
		if (mMustWakeEnabled) {
			mTimer.setMustWakeTime(mDate);
			mTimer.setMustWakeMode(true);
		}
	}

	/** Create accelerometer. */
	private void createAccelerometer() {
		mAccelerometer = new AlarmAccelerometer(mContext, mInactiveTime,
				!mDemoMode, this);
		mAccelerometers.add(mAccelerometer);
		if (mMustWakeEnabled) {
			mAccelerometer.setMustWakeMode(true);
			mAccelerometer.setMustWakeTime(mDate);
		}
	}
	/** Start a new instance of the fragment with COLORS as the 
	 * application colors. */
	public static Fragment newInstance() {
		Bundle args = new Bundle();
		AlarmFragment fragment = new AlarmFragment();
		fragment.setArguments(args);
		return fragment;
	}

	/** Start the dismiss alarm dialog with the alarm ringing. 
	 * @throws InterruptedException */
	public void startAlarm() {
		sRunning = false;
		StaticWakeLock.lockOn(getActivity());
		Intent intent = new Intent();
		intent.setAction("com.brianysu.powernap.dialog.ALARM");
		getActivity().sendBroadcast(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "On resume called");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "On Pause called");
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!sRunning) {
			mCalibrated = false;
			configureCalibrated();
			configureButton();
			Log.d(TAG, "On start called");
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			mColors = sharedPrefs.getString("prefColors", "#33B5E5 #0099CC");
			mVibrate = sharedPrefs.getBoolean("prefVibrate", true);
			mDemoMode = sharedPrefs.getBoolean("prefDemo", false);
			String minOrSec = mDemoMode ? "seconds." : "minutes.";
			try {
				String time = sharedPrefs.getString("prefTimer", "20");
				mDuration = Integer.parseInt(time);
			} catch (NumberFormatException e) {
				Toast.makeText(
						mContext,
						"Duration is not an integer. Alarm duration"
								+ " is still " + mDuration + " " + minOrSec,
						Toast.LENGTH_SHORT).show();
			}
			try {
				String time = sharedPrefs.getString("prefSleep", "5");
				mInactiveTime = Integer.parseInt(time);
			} catch (NumberFormatException e) {
				Toast.makeText(
						mContext,
						"Inactive time is not an integer. Alarm duration"
								+ " is still " + mDuration + " " + minOrSec,
						Toast.LENGTH_SHORT).show();
			}
			mMultiplier = mDemoMode ? 1000 : 60000;
			Log.d(TAG, "Colors: " + mColors + " Vibrate: " + mVibrate
					+ " Demo: " + mDemoMode + " Duration: " + mDuration
					+ " Inactive: " + mInactiveTime);
			configureColors();
			resetCountdown();
			createAccelerometer();
		}
        
	}
	
	/** Print the alarm button text that changes based on DURATION. */
	private String alarmText() {
		String minOrSec = mDemoMode ? "seconds." : "minutes.";
		return String.format("Start alarm for %d " + minOrSec, mDuration);
	}

	/** Adjust the color depending on CALIBRATED. */
	public void configureCalibrated() {
		int color = mCalibrated ? Color.WHITE : Color.GRAY;
		mCalibratedTextView.setTextColor(color);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "On activity result called");
		if (resultCode != Activity.RESULT_OK) return;
		
		if (requestCode == REQUEST_DATE) {
			Log.d(TAG, "here");
			Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_DATE);
			mDate = date;
			Calendar current = Calendar.getInstance();
			current.setTime(mDate);
			int hour = current.get(Calendar.HOUR_OF_DAY);
			int min = current.get(Calendar.MINUTE);
			Log.d(TAG, "Time: " + hour + " " +  " " + min);
			updateDate();
			createAccelerometer();
			createTimer();
		}
	}
	
	/** Update the must wake textview. */
	private void updateDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		mMustWake.setText(String.format("Must Wake Up By: %02d:%02d", hour, min));
	}
}
