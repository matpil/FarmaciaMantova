package com.matpil.farmacia;

import java.text.SimpleDateFormat;
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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matpil.farmacia.model.Farmacia;
import com.matpil.farmacia.model.Intestazione;
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
public class FullscreenActivity extends Activity {
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

	private final static String INTESTAZIONE = "Intestazione";
	private final static String INTESTAZIONE_NOME_FARMACIA = "IntestazioneNomeFarmacia";
	private final static String INTESTAZIONE_INDIRIZZO = "IntestazioneIndirizzo";
	private final static String INTESTAZIONE_TELEFONO = "IntestazioneTelefono";

	private GridviewAdapter mAdapter;
	private List<Farmacia> dailyListPharm;
	private Map<String, Farmacia> pharmMap;
	private GridView gridView;
	private SottoMenu sottoMenu;

//	private static final String TAG = "BroadcastTest";
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		caricaIntestazione();
		boolean checkDataFile = checkDataFile();

		if (checkDataFile) {
			sottoMenu = new SottoMenu(this);

			sottoMenu.checkDefaultCallValue();
			sottoMenu.updatePreferencesData();

			loadDataFromFile();

		}

		otherConfig();

		intent = new Intent(this, BroadcastService.class);
		
	}// onCreate

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	private void updateUI(Intent intent) {
		if (checkTime()) {
			loadDataFromFile();
			Toast.makeText(this, "AGGIORNAMENTO COMPLETATO", Toast.LENGTH_LONG).show();
		} 
//		else {
//			Toast.makeText(this, "WAIT FOR UPDATE", Toast.LENGTH_LONG).show();
//		}
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
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", Locale.ITALIAN);
		String format = sdf.format(now);
		sdf = new SimpleDateFormat("ddMMyyyy HH:mm", Locale.ITALIAN);
		String formatHHMM = sdf.format(now);
		boolean isTime = formatHHMM.equals(format + " 07:30");
//		System.out.println(String.format("%s.equals(%s) = %s", formatHHMM, (format + " 23:48"), isTime));
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
		boolean exist = ImportaFarmacieDaFile.existPharmListFile();
		if (!exist) {
			createAlert("FILE CON ELENCO FARMACIE MANCANTE");
		}
		exist = ImportaFarmacieDaFile.existScheduleFile();
		if (!exist)
			createAlert("FILE CON ELENCO TURNI MANCANTE");

		return exist;
	}

	private void createAlert(String msg) {
		new AlertDialog.Builder(this).setTitle(msg).setNeutralButton("CONTINUA", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// loadDataFromFile();
			}
		}).create().show();
	}

	public void loadDataFromFile() {
		System.out.println("INIZIO CARICAMENTO DATI");
		if (checkDataFile()) {
			pharmMap = ImportaFarmacieDaFile.readTextFile(this);
			Map<String, List<Farmacia>> turniFile = ImportaFarmacieDaFile.readTurniFile(this, pharmMap);
			// Toast.makeText(this, "CARICAMENTO COMPLETATO",
			// Toast.LENGTH_LONG).show();
			prepareList(turniFile);
		}
		showList();
	}

	private void aggiornaIntestazione(Intestazione intestazione) {
		if (intestazione != null) {
			((TextView) findViewById(R.id.nomeFarmaciaTV)).setText(intestazione.getNomeFarmacia());
			((TextView) findViewById(R.id.indirizzoTV)).setText(intestazione.getIndirizzo());
			String tel = String.format("tel. %s", intestazione.getTelefono());
			((TextView) findViewById(R.id.telefonoTV)).setText(tel);
		}
	}

	private void salvaIntestazione(Intestazione intestazione) {
		// Otteniamo il riferimento alle Preferences
		SharedPreferences prefs = this.getSharedPreferences(INTESTAZIONE, Context.MODE_PRIVATE);
		// Otteniamo il corrispondente Editor
		SharedPreferences.Editor editor = prefs.edit();
		// Modifichiamo il valore con quello inserito nell'EditText
		if (intestazione.getNomeFarmacia() != null) {
			// Lo salviamo nelle Preferences
			editor.putString(INTESTAZIONE_NOME_FARMACIA, intestazione.getNomeFarmacia());
			editor.commit();
		}
		if (intestazione.getIndirizzo() != null) {
			// Lo salviamo nelle Preferences
			editor.putString(INTESTAZIONE_INDIRIZZO, intestazione.getIndirizzo());
			editor.commit();
		}
		if (intestazione.getTelefono() != null) {
			// Lo salviamo nelle Preferences
			editor.putString(INTESTAZIONE_TELEFONO, intestazione.getTelefono());
			editor.commit();
		}
		aggiornaIntestazione(intestazione);
	}

	private void caricaIntestazione() {
		SharedPreferences prefs = this.getSharedPreferences(INTESTAZIONE, Context.MODE_PRIVATE);
		String nomeFarmacia = prefs.getString(INTESTAZIONE_NOME_FARMACIA, null);
		String indirizzo = prefs.getString(INTESTAZIONE_INDIRIZZO, null);
		String telefono = prefs.getString(INTESTAZIONE_TELEFONO, null);
		Intestazione intestazione = new Intestazione();
		intestazione.setNomeFarmacia(nomeFarmacia);
		intestazione.setIndirizzo(indirizzo);
		intestazione.setTelefono(telefono);
		if (intestazione.isModificato())
			aggiornaIntestazione(intestazione);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// System.out.println("CODICE RICEVUTO DA ACTIVITY -> " + code);
		if (resultCode == Activity.RESULT_OK) {
			Intestazione intestazione = (Intestazione) data.getSerializableExtra("newCode");
			salvaIntestazione(intestazione);
		}
	}

	private void showList() {

		// prepared arraylist and passed it to the Adapter class
		mAdapter = new GridviewAdapter(this, dailyListPharm);

		// Set custom adapter to gridview
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(mAdapter);

		TextView dateText = (TextView) findViewById(R.id.dateTv);
		// GridView grid = (GridView) findViewById(R.id.gridView1);
		// SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy",
		// Locale.ITALIAN);
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.ITALIAN);
		String text = sdf.format(new Date());

		dateText.setText(text.toUpperCase(Locale.ITALY));

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
						// setTitle(titleText.getText());
						// valoreBuono.setText(titleText.getText());
						loadDataFromFile();
					}
				}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}

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

	private void prepareList(Map<String, List<Farmacia>> turniFile) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
		String today = sdf.format(new Date());
		dailyListPharm = turniFile.get(today);

	}

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
