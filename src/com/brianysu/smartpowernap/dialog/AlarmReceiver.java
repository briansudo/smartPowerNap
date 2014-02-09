package com.brianysu.smartpowernap.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** Receives the broadcast signal from AlarmFragment that
 * occurs when the alarm needs to ring. */
public class AlarmReceiver extends BroadcastReceiver {

	/** Tag. */
	private static final String TAG = "AlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received broadcast");
		Intent intentAlarm = new Intent(context, DismissDialogActivity.class);
		intentAlarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intentAlarm);
	}

}
