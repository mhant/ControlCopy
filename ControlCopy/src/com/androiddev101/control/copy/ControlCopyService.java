package com.androiddev101.control.copy;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class ControlCopyService extends Service {

	private static final int INTENT_REQUEST_CODE_PRESSED = 54321;
	private static final String CHANGED_CLIP_LABEL = "CHANGED_CLIP_LABEL";
	public static final String KEY_BOOLEAN_STOP_RECORDING = "KEY_BOOLEAN_STOP_RECORDING";
	public static final String KEY_ITEM_SELECTED = "KEY_ITEM_SELECTED";
	public static final String ACTION_ITEM_SELECTED = "ACTION_ITEM_SELECTED";
	
	private final IBinder mBinder = new ServiceBinder();

	private static List<String> mItems = new ArrayList<String>();
	
	private static int mSelected = -1;
	
	private ClipboardManager mClipboard;

	private CopyChangeListener mCCL;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class ServiceBinder extends Binder {
		public ControlCopyService getService() {
			return ControlCopyService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean stopRecording = intent.getBooleanExtra(KEY_BOOLEAN_STOP_RECORDING, false);
		mClipboard = (ClipboardManager)
				getSystemService(Context.CLIPBOARD_SERVICE);
		if(stopRecording){
			mClipboard.removePrimaryClipChangedListener(mCCL);
		}
		else if(intent != null && intent.getAction() != null && 
				ACTION_ITEM_SELECTED.equals(intent.getAction())){
			mSelected = intent.getIntExtra(KEY_ITEM_SELECTED, 0);
			String stringPressed = mItems.get(mSelected);
			mClipboard = (ClipboardManager)
					getSystemService(Context.CLIPBOARD_SERVICE);
			mClipboard.setPrimaryClip(ClipData.newPlainText(CHANGED_CLIP_LABEL, stringPressed));
			setNotification(this);
		}
		else{
			if(mCCL == null){
				mCCL = new CopyChangeListener();
			}
			mClipboard.addPrimaryClipChangedListener(mCCL);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public static void setNotification(Context context){
		// build notification
		Notification.Builder nb  = new Notification.Builder(context)
		.setContentTitle("Copy Pasta Board")
		.setContentText("Copy Pasta")
		.setSmallIcon(R.drawable.ic_launcher)
		.setAutoCancel(true);

		//add newest copied items first
		int itemSize = mItems.size();
		for(int i = 0; i < itemSize ; i++){
			String copyText = mItems.get(i);
			int resourceID = getIconResource(i);
			nb.addAction(resourceID, copyText, 
					ControlCopyService.getPressedIntent(context, i));
		}

		NotificationManager notificationManager = 
				(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, nb.build()); 
	}

	private static int getIconResource(int i) {
		switch (i) {
		case 0:
			return i==mSelected?R.drawable.icon_1_copy_selected:R.drawable.icon_1_copy;
		case 1:
			return i==mSelected?R.drawable.icon_2_copy_selected:R.drawable.icon_2_copy;
		case 2:
			return i==mSelected?R.drawable.icon_3_copy_selected:R.drawable.icon_3_copy;
		default:
			return R.drawable.icon_1_copy;

		}
	}

	private static PendingIntent getPressedIntent(Context context, int position){
		Intent pressedIntent = new Intent(context, ControlCopyService.class);
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_ITEM_SELECTED, position);
		pressedIntent.putExtras(bundle);
		pressedIntent.setAction(ACTION_ITEM_SELECTED);
		return PendingIntent.getService(context, INTENT_REQUEST_CODE_PRESSED + position, 
				pressedIntent,   PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
	}

	
	private class CopyChangeListener implements OnPrimaryClipChangedListener{

		@Override
		public void onPrimaryClipChanged() {
			ClipData clip = mClipboard.getPrimaryClip();
			if(clip != null && clip.getDescription() != null && 
					clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN) && 
					clip.getItemAt(0) != null && clip.getItemAt(0).getText() != null &&
					!CHANGED_CLIP_LABEL.equals(clip.getDescription().getLabel())
					){
				mItems.add(0, clip.getItemAt(0).getText().toString());
				mSelected = 0;
				ControlCopyService.setNotification(ControlCopyService.this);
			}
		}
	}
}
