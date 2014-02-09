package com.brianysu.smartpowernap.dialog;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

/** Wakes the screen up if asleep and unlocks the lock screen if unlocked. Relocks
 * the screen after user has dismissed the alarm. */
public class StaticWakeLock {
	
	/** Tag. */
	private static final String TAG = "StaticWakeLock";
	private static PowerManager.WakeLock sWakeLock = null;
	private static KeyguardManager.KeyguardLock sKeyguardLock = null;

	/** Unlocks and wakes up the screen, if necessary. */
	public static void lockOn(Context context) {
		KeyguardManager km = (KeyguardManager) context
				.getSystemService(context.KEYGUARD_SERVICE);
		sKeyguardLock = km.newKeyguardLock(TAG);
		sKeyguardLock.disableKeyguard();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (sWakeLock == null)
			sWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "ALARM");
		sWakeLock.acquire();
	}

	/** Locks the screen. */
	public static void lockOff(Context context) {
		try {
			if (sWakeLock != null)
				sWakeLock.release();
			sKeyguardLock.reenableKeyguard();
		} catch (Exception e) {
			// Do nothing
		}
	}
}