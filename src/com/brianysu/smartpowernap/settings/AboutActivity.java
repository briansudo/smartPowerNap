package com.brianysu.smartpowernap.settings;

import com.brianysu.smartpowernap.other.SingleFragmentActivity;

import android.support.v4.app.Fragment;

public class AboutActivity extends SingleFragmentActivity {

	@Override
	public Fragment createFragment() {
		return new AboutFragment();
	}

}
