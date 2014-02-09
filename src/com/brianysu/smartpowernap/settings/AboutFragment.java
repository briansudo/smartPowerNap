package com.brianysu.smartpowernap.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.brianysu.smartpowernap.R;

public class AboutFragment extends Fragment {

	private View mView; 
	private TextView mTextView;
	private Button mAccel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		getActivity().setTitle(R.string.about_title);
		mView = inflater.inflate(R.layout.fragment_about, parent, false);
		mTextView = (TextView) mView.findViewById(R.id.about_page);
		mTextView.setText(R.string.about_text);
		mAccel = (Button) mView.findViewById(R.id.accel);
		mAccel.setText("See the accelerometer in action");
		mAccel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), AccelerometerActivity.class);
				startActivity(i);
				
			}
		}); 
		return mView;
	}
}
