package com.androiddev101.control.copy;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {

	public interface SuluComms{
		public boolean isServiceRunning();
	}

	private SuluComms mComms;
	private Button mToggleButton;

	public MainFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mComms = (SuluComms) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		//set button state depending on if the service is running or not
		mToggleButton = (Button) rootView.findViewById(R.id.toggleCopyPastaService);
		if(mComms.isServiceRunning()){
			mToggleButton.setText(R.string.button_toggle_off);
		}
		else{
			mToggleButton.setText(R.string.button_toggle_on);
		}
		return rootView;
	}
}