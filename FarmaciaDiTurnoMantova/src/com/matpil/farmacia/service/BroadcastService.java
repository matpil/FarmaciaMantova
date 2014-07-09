package com.matpil.farmacia.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class BroadcastService  extends Service {
//	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "com.matpil.farmacia.displayevent";
	private final Handler handler = new Handler();
	Intent intent;
	int counter = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();

    	intent = new Intent(BROADCAST_ACTION);	
	}
	
    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
   
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run() {
    		DisplayLoggingInfo();    		
    	    handler.postDelayed(this, 30000); // 30 seconds
    	}
    };    
    
    private void DisplayLoggingInfo() {
    	sendBroadcast(intent);
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}		
}
