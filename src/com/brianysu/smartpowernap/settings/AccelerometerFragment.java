package com.brianysu.smartpowernap.settings;

import android.content.Context;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brianysu.smartpowernap.R;
import com.brianysu.smartpowernap.accelerometer.Accelerometer;

public class AccelerometerFragment extends Fragment {

	private static final String TAG = "AccelerometerFragment";
	/** The View. */
	private View mView;
	/** Accelerometer. */
	private Accelerometer mAccelerometer;
	
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		getActivity().setTitle(R.string.accelerometer_title);
		mView = inflater.inflate(R.layout.fragment_accelerometer, parent, false);
		mAccelerometer = new AccelerometerExample(getActivity(), mView);
		mAccelerometer.start();
		return mView;
	}
	
	public static Fragment newInstance() {
		Bundle args = new Bundle();
		AccelerometerFragment fragment = new AccelerometerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	class AccelerometerExample extends Accelerometer {

		/** TextViews. */
		private View mView;
		
		public AccelerometerExample(Context appContext, View view) {
			super(appContext);
			mView = view;
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			super.onSensorChanged(event);
			TextView tvX= (TextView) mView.findViewById(R.id.x_axis_textview);
			TextView tvY= (TextView) mView.findViewById(R.id.y_axis_textview);
			TextView tvZ= (TextView) mView.findViewById(R.id.z_axis_textview);
			tvX.setText(Float.toString(getX()));
			tvY.setText(Float.toString(getY()));
			tvZ.setText(Float.toString(getZ()));
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mAccelerometer.stop();
	}
}
