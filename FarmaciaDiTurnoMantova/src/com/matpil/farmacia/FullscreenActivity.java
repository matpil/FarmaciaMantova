package com.matpil.farmacia;

import java.util.Date;
import java.util.List;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matpil.farmacia.adapter.GridviewAdapter;
import com.matpil.farmacia.intestazione.CreaIntestazione;
import com.matpil.farmacia.intestazione.GestioneIntestazione;
import com.matpil.farmacia.menu.SottoMenu;
import com.matpil.farmacia.model.Farmacia;
import com.matpil.farmacia.model.InfoFarmacie;
import com.matpil.farmacia.model.Intestazione;
import com.matpil.farmacia.other.SystemUiHider;
import com.matpil.farmacia.parser.AggiornamentoFileDaSdCard;
import com.matpil.farmacia.service.BroadcastService;
import com.matpil.farmacia.util.DataLoader;
import com.matpil.farmacia.util.TimeHelper;

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

	private SottoMenu sottoMenu = new SottoMenu(this);
	private DataLoader loader = new DataLoader(this);
	private boolean actionBarShow = false;
	private String startHour = null;
	private String endHour = null;

	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		init();

		displayInfo();

		otherConfig();

		intent = new Intent(this, BroadcastService.class);

	}// onCreate

	private void displayInfo() {
		// Caricamento dati
		caricamentoDati();

		// imposto data
		TextView dateText = (TextView) findViewById(R.id.dateTv);
		Date toDisplay = loader.getDataCorrente();
		if (toDisplay == null)
			toDisplay = new Date();
		dateText.setText(TimeHelper.retrieveDateFormatted("EEEE dd MMMM yyyy", toDisplay));
		// imposto range orario
		String periodText = getString(R.string.period);
		TextView periodTV = (TextView) findViewById(R.id.period);
		periodTV.setText(TimeHelper.retrieveRangeHour(periodText, startHour, endHour));
	}

	private Date caricamentoDati() {
		boolean caricamentoEffettuato = loader.caricaDati();
		InfoFarmacie infoFarmacie = null;
		Date currentDate = null;
		if (!caricamentoEffettuato) {
			createAlert("Errore durante il caricamento file dati");
		} else {
			infoFarmacie = loader.recuperaListaFarmaciePerGiorno(startHour);
			List<Farmacia> listPharm = infoFarmacie.getListPharm();
			currentDate = TimeHelper.retrieveDate(this.startHour);
			this.startHour = infoFarmacie.getTimeUpdate();
			this.endHour = loader.recuperaOrarioAperturaPerGiorno(TimeHelper.retrieveTomorrowRightDateFormatted("dd/MM/yyyy", this.startHour));
			showList(listPharm);
		}
		return currentDate == null ? new Date() : currentDate;
	}

	private void showList(List<Farmacia> listPharm) {
		// prepared arraylist and passed it to the Adapter class
		GridviewAdapter mAdapter = new GridviewAdapter(this, listPharm);
		// Set custom adapter to gridview
		GridView gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(mAdapter);

		setKeepScreenOn(this, true);

	}

	private void init() {
		// Gestione action bar
		manageActionBar();
		// Recupero dati salvati in SharedPreferences
		sottoMenu.checkDefaultCallValue();
		sottoMenu.updatePreferencesData();
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

	private void createAlert(String msg) {
		new AlertDialog.Builder(this).setTitle(msg).setNeutralButton("CONTINUA", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing...
			}
		}).create().show();
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

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

	private void updateUI(Intent intent) {
		if (this.endHour != null) {
			String dtFormat = TimeHelper.retrieveDateFormattedWithHourAndMin(endHour);
			String dtCheck = TimeHelper.retrieveDateFormatted("dd/MM/yyyy HH:mm", new Date());
			System.out.println(String.format("%s - %s", dtFormat, dtCheck));
			if (dtFormat.equals(dtCheck)) {
				cleanData();
				displayInfo();
				Toast.makeText(this, "AGGIORNAMENTO COMPLETATO", Toast.LENGTH_LONG).show();
				// lastUpdate = currentUpdate;
			}
		}
	}

	private void cleanData() {
		this.startHour = null;
		this.endHour = null;
		this.loader.cleanDataCorrente();

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// System.out.println("CODICE RICEVUTO DA ACTIVITY -> " + code);
		if (resultCode == Activity.RESULT_OK) {
			Intestazione intestazione = (Intestazione) data.getSerializableExtra("newCode");
			GestioneIntestazione.salvaIntestazione(intestazione, this);
		}
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

	private Dialog updateDataDialog() {
		return new AlertDialog.Builder(this).setTitle("AGGIORNARE GLI ARCHIVI DI FARMACIE E TURNI?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// COPIA FILE DA SD A MEMORIA INTERNA
						copyFileFromSdCard();
						// AGGIORNARE FILE DATI
						caricamentoDati();
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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	public void setKeepScreenOn(Activity activity, boolean keepScreenOn) {
		if (keepScreenOn) {
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	// /**
	// * Touch listener to use for in-layout UI controls to delay hiding the
	// * system UI. This is to prevent the jarring behavior of controls going
	// away
	// * while interacting with activity UI.
	// */
	// View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
	// {
	// @Override
	// public boolean onTouch(View view, MotionEvent motionEvent) {
	// if (AUTO_HIDE) {
	// delayedHide(AUTO_HIDE_DELAY_MILLIS);
	// }
	// return false;
	// }
	// };

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

}
