package com.matpil.farmacia;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matpil.farmacia.intestazione.GestioneIntestazione;
import com.matpil.farmacia.model.Farmacia;
import com.matpil.farmacia.model.InfoFarmacie;
import com.matpil.farmacia.model.Intestazione;
import com.matpil.farmacia.parser.AggiornamentoFileDaSdCard;
import com.matpil.farmacia.parser.ImportaFarmacieDaFile;
import com.matpil.farmacia.util.CreaIntestazione;
import com.matpil.farmacia.util.GridviewAdapter;
import com.matpil.farmacia.util.SottoMenu;
import com.matpil.farmacia.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends ActionBarActivity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	private GridviewAdapter mAdapter;
	private List<Farmacia> dailyListPharm;
	private Map<String, Farmacia> pharmMap;
	private GridView gridView;
	private SottoMenu sottoMenu;
	private String timeUpdate = null;
	private String timeEndUpdate = null;
	private boolean actionBarShow = false;
	private Date currentDate = new Date();
	private String lastUpdate = null;

	// private static final String TAG = "BroadcastTest";
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		manageActionBar();

		boolean checkDataFile = checkDataFile();

		if (checkDataFile) {
			loadDataFromFile();
		}

		manageCurrentDate();

		managePeriod();

		GestioneIntestazione.caricaIntestazione(this);
		sottoMenu = new SottoMenu(this);
		sottoMenu.checkDefaultCallValue();
		sottoMenu.updatePreferencesData();

		otherConfig();

		intent = new Intent(this, BroadcastService.class);

	}// onCreate

	private void managePeriod() {
		if (timeUpdate != null && timeEndUpdate != null) {
			TextView periodTV = (TextView) findViewById(R.id.period);
			String periodText = getString(R.string.period);
			String msg = String.format("Aggiornamento %s con valori %s - %s", periodText, timeUpdate, timeEndUpdate);
			periodText = periodText.replace("start", timeUpdate);
			periodText = periodText.replace("end", timeEndUpdate);
			periodTV.setText(periodText);
			// int idx = timeUpdate.indexOf(".");
			// int hour = Integer.parseInt(timeEndUpdate.substring(0, idx));
			// int minute = Integer.parseInt(timeEndUpdate.substring(idx + 1));
			System.out.println(msg);
		}
	}

	private void manageCurrentDate() {
		Date now = new Date();
		Calendar instance = Calendar.getInstance();
		int idx = timeUpdate.indexOf(".");
		int hour = Integer.parseInt(timeUpdate.substring(0, idx));
		int minute = Integer.parseInt(timeUpdate.substring(idx + 1));
		instance.set(Calendar.HOUR_OF_DAY, hour);
		instance.set(Calendar.MINUTE, minute);
		instance.set(Calendar.SECOND, 0);
		System.out.println(now + " after " + instance.getTime() + ": " + (now.after(instance.getTime())));
		if (now.after(instance.getTime())) {
			currentDate = new Date();
		} else {
			instance.setTime(now);
			instance.set(Calendar.DAY_OF_MONTH, instance.get(Calendar.DAY_OF_MONTH) - 1);
			currentDate = new Date(instance.getTimeInMillis());
		}
		System.out.println("CHOOSE_DAY -> " + currentDate);
		setCurrentDate();
	}

	private void setCurrentDate() {
		TextView dateText = (TextView) findViewById(R.id.dateTv);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALIAN);
		if (currentDate == null)
			manageCurrentDate();
		String text = sdf.format(currentDate);
		dateText.setText(text.toUpperCase(Locale.ITALY));
	}

	private void manageActionBar() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		AdapterView<?> view = (AdapterView<?>) findViewById(R.id.gridView1);

		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!actionBarShow) {
					actionBar.show();
					actionBarShow = true;
				} else {
					actionBar.hide();
					actionBarShow = false;
				}
			}
		});

		View tLayout = findViewById(R.id.tableLayout);
		tLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!actionBarShow) {
					actionBar.show();
					actionBarShow = true;
				} else {
					actionBar.hide();
					actionBarShow = false;
				}
			}
		});
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {
//		boolean updated = false;
//		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", Locale.ITALIAN);
//		String currentUpdate = sdf.format(currentDate);
//		if (lastUpdate == null)
//			updated = false;
//		else {
//			updated = lastUpdate.equals(currentUpdate);
//		}
		if (checkTime()) {
			System.out.println("CURRENT_DATE -> " + currentDate);
			loadDataFromFile();
			Toast.makeText(this, "AGGIORNAMENTO COMPLETATO", Toast.LENGTH_LONG).show();
//			lastUpdate = currentUpdate;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		startService(intent);
		registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent);
	}

	private boolean checkTime() {
		if (timeUpdate == null) {
			currentDate = new Date();
			return true;
		}
		Date now = new Date();
		int idx = timeUpdate.indexOf(".");
		int hour = Integer.parseInt(timeEndUpdate.substring(0, idx));
		int minute = Integer.parseInt(timeEndUpdate.substring(idx + 1));

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", Locale.ITALIAN);
		String format = sdf.format(now);
		sdf = new SimpleDateFormat("ddMMyyyy H:mm", Locale.ITALIAN);
		String formatHHMM = sdf.format(now);
		boolean isTime = formatHHMM.equals(String.format("%s %s:%s", format, hour, minute));
		System.out.println(String.format("formatHHMM %s = format %s", formatHHMM, String.format("%s %s:%s", format, hour, minute)));
		if (isTime) {
			currentDate = new Date();
		} else {
			Calendar instance = Calendar.getInstance();
			instance.setTime(now);
			instance.set(Calendar.DAY_OF_MONTH, instance.get(Calendar.DAY_OF_MONTH) - 1);
			currentDate = instance.getTime();
		}
		return isTime;
	}

	private void otherConfig() {
		TableLayout tl = (TableLayout) findViewById(R.id.tableLayout);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, tl, SystemUiHider.FLAG_FULLSCREEN);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			// Cached values.

			int mShortAnimTime;

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.

					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
					}
				} else {
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
				}

				if (visible && AUTO_HIDE) {
					// Schedule a hide().
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});
	}

	private boolean checkDataFile() {
		boolean exist = ImportaFarmacieDaFile.existPharmListFile(this);
		if (!exist) {
			createAlert("FILE CON ELENCO FARMACIE MANCANTE");
		}
		exist = ImportaFarmacieDaFile.existScheduleFile(this);
		if (!exist)
			createAlert("FILE CON ELENCO TURNI MANCANTE");

		return exist;
	}

	private void createAlert(String msg) {
		new AlertDialog.Builder(this).setTitle(msg).setNeutralButton("CONTINUA", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing...
			}
		}).create().show();
	}

	private void loadDataFromFile() {
		// System.out.println("INIZIO CARICAMENTO DATI");
		// Date dataTurni = null;
		if (checkDataFile()) {
			try {
				pharmMap = ImportaFarmacieDaFile.readTextFile(this);
			} catch (IOException e) {
				e.printStackTrace();
				createAlert("ERRORE DURANTE IL CARICAMENTO DEI FILE:" + e);
			}
			Map<String, InfoFarmacie> turniFile = ImportaFarmacieDaFile.readTurniFile(this, pharmMap);
			// Toast.makeText(this, "CARICAMENTO COMPLETATO",
			// Toast.LENGTH_LONG).show();
			prepareList(turniFile);
		}
		showList();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// System.out.println("CODICE RICEVUTO DA ACTIVITY -> " + code);
		if (resultCode == Activity.RESULT_OK) {
			Intestazione intestazione = (Intestazione) data.getSerializableExtra("newCode");
			GestioneIntestazione.salvaIntestazione(intestazione, this);
		}
	}

	private void showList() {

		// prepared arraylist and passed it to the Adapter class
		mAdapter = new GridviewAdapter(this, dailyListPharm);

		// Set custom adapter to gridview
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(mAdapter);

		// manageCurrentDate();
		setCurrentDate();
		managePeriod();

		setKeepScreenOn(this, true);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SottoMenu.CAMBIA_INTESTAZIONE:
			return sottoMenu.createChangeTitleDialog();
		case SottoMenu.CAMBIA_COSTO_CHIAMATA:
			return sottoMenu.createChangeTitleDialog();
		case SottoMenu.ABOUT_DIALOG:
			return sottoMenu.createAboutDialog();
		case SottoMenu.AGGIORNA_TURNI:
			return updateDataDialog();
		}
		return null;
	}

	public Dialog updateDataDialog() {
		return new AlertDialog.Builder(this).setTitle("AGGIORNARE GLI ARCHIVI DI FARMACIE E TURNI?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						copyFileFromSdCard();
						loadDataFromFile();
					}

				}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
	}

	private void copyFileFromSdCard() {
		boolean copied = AggiornamentoFileDaSdCard.copyFiles(this);
		if (copied)
			createAlert("AGGIORNAMENTO AVVENUTO CON SUCCESSO");
		else
			createAlert("ERRORE DURANTE L'AGGIORNAMENTO DEI DATI");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_mod_intestazione:
			Intent intent = new Intent(this, CreaIntestazione.class);
			startActivityForResult(intent, 5);
			return true;
		case R.id.menu_mod_costo_chiamata:
			showDialog(SottoMenu.CAMBIA_COSTO_CHIAMATA);
			return true;
		case R.id.menu_about:
			showDialog(SottoMenu.ABOUT_DIALOG);
			return true;
		case R.id.menu_cambia_file_turni:
			showDialog(SottoMenu.AGGIORNA_TURNI);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void prepareList(Map<String, InfoFarmacie> turniFile) {
		// chooseDay();
		String day = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
		// Date dataTurni = chooseDay();
		day = sdf.format(currentDate);
		// System.out.println(day);
		InfoFarmacie info = turniFile.get(day);
		dailyListPharm = info.getListPharm();
		timeUpdate = info.getTimeUpdate();
		// recupero l'orario di aggiornamento per il giorno successivo
		Calendar instance = Calendar.getInstance();
		instance.setTime(currentDate);
		instance.set(Calendar.DAY_OF_MONTH, instance.get(Calendar.DAY_OF_MONTH) + 1);
		Date dataTurni = new Date(instance.getTimeInMillis());
		String format = sdf.format(dataTurni);
		timeEndUpdate = turniFile.get(format).getTimeUpdate();
		String msg = String.format("timeUdate %s per giorno %s; timeEndUpdate %s per giorno %s", timeUpdate, day, timeEndUpdate, format);
		System.out.println(msg);

		// return dataTurni;
	}

	// private void chooseDay() {
	// if (checkTime()) {
	// currentDate = new Date();
	// } else {
	// Calendar instance = Calendar.getInstance();
	// instance.set(Calendar.DAY_OF_MONTH, instance.get(Calendar.DAY_OF_MONTH) -
	// 1);
	// currentDate = new Date(instance.getTimeInMillis());
	// }
	// }

	public void setKeepScreenOn(Activity activity, boolean keepScreenOn) {
		if (keepScreenOn) {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

}
