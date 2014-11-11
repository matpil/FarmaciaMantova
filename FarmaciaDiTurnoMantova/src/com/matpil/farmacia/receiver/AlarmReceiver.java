package com.matpil.farmacia.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.matpil.farmacia.service.AlarmRecieverService;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent weeklyUpdater = new Intent(context, AlarmRecieverService.class);
		context.startService(weeklyUpdater);
		Log.d("AlarmReceiver", "Called context.startService from AlarmReceiver.onReceive");
	}
}