package com.brianysu.smartpowernap.settings;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.Log;

public class AlarmPreference extends RingtonePreference {

	/** Tag. */
	private static final String TAG = "onSaveRingtone";
	
	/** Media Player. */
	private MediaPlayer mMediaPlayer;
	
	public AlarmPreference(Context context) {
		super(context);
	}
	
	@Override
	protected void onSaveRingtone(Uri ringtoneUri) {
		super.onSaveRingtone(ringtoneUri);
		Log.d(TAG, "On save ringtone");
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setVolume(1.0f, 1.0f);
		try {
			mMediaPlayer.setDataSource(getContext(),
					ringtoneUri);
			mMediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		mMediaPlayer.start();
	}

}
