package com.brianysu.smartpowernap.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.brianysu.smartpowernap.R;

public class DismissDialogActivity extends Activity {
	
	/** Tag. */
	private static final String TAG = "DismissDialog";
	/** MediaPlayer for alarms. */
    private MediaPlayer mMediaPlayer;
    /** Vibrator. */
    private Vibrator mVibrator;
    
	/** Ringtone. */
    private String mRingtone;
    private static String sDefaultRingtone = RingtoneManager
    		.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
    /** True if user wants to vibrate. */
    private boolean mVibrate = true;
    
    /** The view. */
	private View mView;
	/** Set Alarm Button. */
	private Button mAlarmButton;
	/** The background color for the user. The format is "0xFFFFFF 0xFFFFFF".
	 * The first hex represents the background color, while the second one
	 * represents the button color. Initially set to default value. */
	private String mColors = "#33B5E5 #0099CC";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.fragment_alarm);
		mView = getWindow().getDecorView().findViewById(android.R.id.content);
		
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("Alarm");
		ad.setCancelable(false);
		ad.setNeutralButton("Dismiss", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface d, int i){
				Log.d(TAG, "Dismiss button pressed");
				try {
					if (mVibrate) {
						mVibrator.cancel();
					}
					mMediaPlayer.stop();
					mMediaPlayer.release();
				} catch (Exception e) {
					// Do nothing
				}
				DismissDialogActivity.this.finish();
				StaticWakeLock.lockOff(DismissDialogActivity.this);
			}
		});
		ad.show();
	}
	
	/** Ring the alarm. */
	public void ring() {
		Log.d(TAG, "Starting media player");
		mMediaPlayer = new MediaPlayer();
		if (mVibrate) {
			mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			long[] pattern = { 1000, 200, 200, 200 };
			mVibrator.vibrate(pattern, 0);
		}
		try {
			mMediaPlayer.setVolume(1.0f, 1.0f);
			mMediaPlayer.setDataSource(this,
					Uri.parse(mRingtone));
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			mMediaPlayer.release();
		}
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
		mAlarmButton.setText("Alarm");
	}

	@Override
	public void onStart() {
		super.onStart();
		configureButton();
		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
		mColors = sharedPrefs.getString("prefColors", "#33B5E5 #0099CC");
		mRingtone = sharedPrefs.getString("prefRingtone", sDefaultRingtone);
		mVibrate = sharedPrefs.getBoolean("prefVibrate", true);
		Log.d(TAG, "Static ringtone: " + sDefaultRingtone + " Vibrate: " + mVibrate);
		Log.d(TAG, "Ringtone: " + mRingtone);
        configureColors();
        ring();
	}
}
