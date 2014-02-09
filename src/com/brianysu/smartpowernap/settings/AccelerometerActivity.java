package com.brianysu.smartpowernap.settings;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.brianysu.smartpowernap.other.SingleFragmentActivity;

public class AccelerometerActivity extends SingleFragmentActivity {

	public Fragment createFragment() {
		return AccelerometerFragment.newInstance();
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {       
        startActivity(new Intent(AccelerometerActivity.this, SettingsActivity.class)); 
        return true;
    }

}
