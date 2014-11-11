package com.matpil.farmacia.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.matpil.farmacia.parser.AggiornamentoFileDaRemoto;

public class AlarmRecieverService extends IntentService {
	
	public static final String UPDATE_RESULT = "UPDATE_RESULT";
	
	LocalBroadcastManager broadcaster;
	
	public AlarmRecieverService() {
		super("AlarmRecieverService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		broadcaster = LocalBroadcastManager.getInstance(this);
	}
	
	public void sendResult(String message) {
	    Intent intent = new Intent(UPDATE_RESULT);
	    broadcaster.sendBroadcast(intent);
	}	

	@Override
	protected void onHandleIntent(Intent intent) {
		System.out.println("ALARM FIRED");
		new MyTask(this).execute();
	}	
	
    private class MyTask extends AsyncTask<String, Void, Boolean> {
    	
    	private Context ctx;
    	
    	MyTask(Context c){
    		ctx=c;
    	}
    	
        @Override
        protected Boolean doInBackground(String... strings) {
        	Log.d("AlarmRecieverService.MyTask", "Inizio aggiornamento da remoto");
        	Boolean updated = AggiornamentoFileDaRemoto.downloadAndCopyFiles(ctx);
        	sendResult(updated.toString());
        	return updated;
        }
    }	
}
