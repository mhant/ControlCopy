package com.androiddev101.control.copy;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.androiddev101.control.copy.MainFragment.SuluComms;

public class MainActivity extends Activity implements SuluComms{

	private AlertDialog mHelpDialog;
	
	private String BUTTON_ON_TEXT;
	private String BUTTON_OFF_TEXT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BUTTON_ON_TEXT = MainActivity.this.getString(R.string.button_toggle_on);
		BUTTON_OFF_TEXT = MainActivity.this.getString(R.string.button_toggle_off);

		mHelpDialog = new AlertDialog.Builder(this)
		.setMessage(getString(R.string.help_popup_text))
		.setPositiveButton(getString(R.string.button_help_popup_positive_text), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		}).create();
	}

	private void startService() {
		//start service if not started
		Intent serviceIntent = new Intent(this, ControlCopyService.class);
		this.startService(serviceIntent);
	}
	
	//helped pressed time to whip out the big gunes
	public void helpClicked(View v)
	{
		mHelpDialog.show();
	} 
	
	//user wants to show the notification
	public void showNotification(View v)
	{
		ControlCopyService.setNotification(v.getContext());
	}

	//Toggle service on or off
	public void toggleClicked(View v)
	{
		Button toggleButton = ((Button)v);
		String currentState = (String) toggleButton.getText();
		if(BUTTON_OFF_TEXT.equals(currentState)){
			stopService();
			toggleButton.setText(BUTTON_ON_TEXT);
		}else{
			startService();
			toggleButton.setText(BUTTON_OFF_TEXT);
		}
	} 

	private void stopService() {
		Intent serviceIntent = new Intent(this, ControlCopyService.class);
		serviceIntent.putExtra(ControlCopyService.KEY_BOOLEAN_STOP_RECORDING, true);
		this.startService(serviceIntent);

	}

	public boolean isServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (ControlCopyService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

}
