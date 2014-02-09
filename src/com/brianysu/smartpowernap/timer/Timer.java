package com.brianysu.smartpowernap.timer;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.brianysu.smartpowernap.AlarmFragment;

/** Starts a timer that starts the alarm once finished. */
public class Timer extends CountDownTimer {

	/** Tag. */
	private static final String TAG = "Timer";
	
	/** Time remaining. */
	private long mTimeRemaining;
	/** Countdown textview. */
	private TextView mView;
	/** Application context. */
	private Context mContext;
	/** AlarmFragment. */
	private AlarmFragment mFragment;
	/** Must Wake Date. */
	private Date mMustWake;
	/** Must Wake Mode. */
	private boolean mMustWakeMode;
	/** Don't start alarm. */
	private boolean mDontStart, mShouldStart;
	
	/** Create a new countdown timer. */
	public Timer(long millisInFuture, long countDownInterval, TextView view,
            Context context, AlarmFragment fragment) {
		super(millisInFuture, countDownInterval);
		mTimeRemaining = millisInFuture;
		mView = view;
		mContext = context;
		mFragment = fragment;
		mMustWakeMode = false;
		mDontStart = true;
		mShouldStart = false;
	}

	/** Play the ringtone for the alarm. */
	public void startAlarm() {
		if (!mDontStart || mShouldStart) {
			Log.d(TAG, "Alarm rings.");
		    mFragment.startAlarm();
		}
	}

	@Override
	public void onFinish() {
		mView.setText("0:00");
		mShouldStart = true;
		Log.d(TAG, "Timer has ended.");
		if (!mDontStart && mShouldStart) {
			mDontStart = true;
			mShouldStart = false;
		}
		startAlarm();
		
	}

	@Override
	public void onTick(long millisUntilFinished) {
		Log.d(TAG, "Must wake enabled: " + mMustWakeMode);
		if (mMustWakeMode) {
			Calendar current = Calendar.getInstance();
			Calendar mustWake = Calendar.getInstance();
			mustWake.setTime(mMustWake);
			int hour = current.get(Calendar.HOUR_OF_DAY);
			int min = current.get(Calendar.MINUTE);
			int hour2 = mustWake.get(Calendar.HOUR_OF_DAY);
			int min2 = mustWake.get(Calendar.MINUTE);
			Log.d(TAG, "Time: " + hour + " " + hour2 + " " + min + " " + min2);
			if (hour > hour2 || (hour == hour2 && min > min2)) {
				mDontStart = false;
				this.cancel();
				startAlarm();
				return;
			}
		}
		mTimeRemaining = millisUntilFinished;
        if ((millisUntilFinished / 1000 % 60) < 10) {
            mView.setText(millisUntilFinished / 60000 + ":0"
                    + (millisUntilFinished / 1000) % 60);
        } else {
            mView.setText(millisUntilFinished / 60000 + ":"
                    + (millisUntilFinished / 1000) % 60);
        }
	}
	
	public void setMustWakeTime(Date mustWake) {
		mMustWake = mustWake;
	}
	
	public void setMustWakeMode(boolean on) {
		mMustWakeMode = on;
	}

}
