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
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
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
import com.matpil.farmacia.parser.AggiornamentoFileDaRemoto;
import com.matpil.farmacia.parser.AggiornamentoFileDaSdCard;
import com.matpil.farmacia.service.BroadcastService;
import com.matpil.farmacia.service.PopUpBroadcastService;
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
	private Intent intentPopUp;
	
	private CountDownTimer mPopUpDismissTimer; // instance variable, put in your activity class

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		init();

		displayInfo();

		otherConfig();

		intent = new Intent(this, BroadcastService.class);
		intentPopUp = new Intent(this, PopUpBroadcastService.class);

	}// onCreate

	private void displayInfo() {
		// Caricamento dati
		caricamentoDati();

		// imposto data
		TextView dateText = (TextView) findViewById(R.id.dateTv);
		Date toDisplay = loader.getDataCorrente();
		if (toDisplay == null)
			toDisplay = new Date();
		dateText.setText(TimeHelper.retrieveDateFormatted("EEEE dd MMMM yyyy",
				toDisplay));
		// imposto range orario
		String periodText = getString(R.string.period);
		TextView periodTV = (TextView) findViewById(R.id.period);
		periodTV.setText(TimeHelper.retrieveRangeHour(periodText, startHour,
				endHour));
	}

	private Date caricamentoDati() {
		boolean caricamentoEffettuato = loader.caricaDati();
		Date currentDate = null;
		if (!caricamentoEffettuato) {
			createAlert("Errore durante il caricamento file dati");
		} else {
			currentDate = showData();
		}
		return currentDate == null ? new Date() : currentDate;
	}

	public Date showData() {
		InfoFarmacie infoFarmacie;
		Date currentDate;
		infoFarmacie = loader.recuperaListaFarmaciePerGiorno(startHour);
		List<Farmacia> listPharm = infoFarmacie.getListPharm();
		currentDate = TimeHelper.retrieveDate(this.startHour);
		this.startHour = infoFarmacie.getTimeUpdate();
		this.endHour = loader.recuperaOrarioAperturaPerGiorno(TimeHelper
				.retrieveTomorrowRightDateFormatted("dd/MM/yyyy",
						this.startHour));
		showList(listPharm);
		return currentDate;
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
		GestioneIntestazione.caricaIntestazione(this);
	}

	private void manageActionBar() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		AdapterView<?> view = (AdapterView<?>) findViewById(R.id.gridView1);

		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
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
		new AlertDialog.Builder(this)
				.setTitle(msg)
				.setNeutralButton("CONTINUA",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing...
							}
						}).create().show();
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastService.BROADCAST_ACTION)) {
				updateUI();
			}
			if (intent.getAction().equals(PopUpBroadcastService.POPUP_BROADCAST_ACTION)) {
//				ImageView imageView = new ImageView(getApplicationContext());
//				imageView.setImageDrawable((getResources().getDrawable(R.drawable.farmacie_di_turno_popup)));
				loadPhoto();

			}
		}
	};

	PopupWindow popupWindow =null;
	
	private void loadPhoto() {
		getPopUpDismissTimer(3000, 1000); //3000 ms is the time when you want to dismiss popup
	
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layoutt = inflater.inflate(R.layout.custom_fullimage_dialog,(ViewGroup) findViewById(R.id.fullimage));
		popupWindow = new PopupWindow(layoutt, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		
		popupWindow.showAtLocation(layoutt, Gravity.NO_GRAVITY, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		mPopUpDismissTimer.start();
		
	}
	
	private void getPopUpDismissTimer(long millisInFuture, long countDownInterval) { 
		mPopUpDismissTimer = new CountDownTimer(millisInFuture, countDownInterval) {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				popupWindow.dismiss();
			}
			
		};
	}
	
	

	@Override
	public void onResume() {
		super.onResume();
		startService(intent);
		startService(intentPopUp);
		registerReceiver(broadcastReceiver, new IntentFilter(
				BroadcastService.BROADCAST_ACTION));
		registerReceiver(broadcastReceiver, new IntentFilter(
				PopUpBroadcastService.POPUP_BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		stopService(intent);
		stopService(intentPopUp);
	}

	public void updateUI() {
		if (this.endHour != null) {
			String dtFormat = TimeHelper
					.retrieveDateFormattedWithHourAndMin(endHour);
			String dtCheck = TimeHelper.retrieveDateFormatted(
					"dd/MM/yyyy HH:mm", new Date());
			System.out.println(String.format("%s - %s", dtFormat, dtCheck));
			if (dtFormat.equals(dtCheck)) {
				cleanData();
				displayInfo();
				Toast.makeText(this, "AGGIORNAMENTO COMPLETATO",
						Toast.LENGTH_LONG).show();
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
		mSystemUiHider = SystemUiHider.getInstance(this, tl,
				SystemUiHider.FLAG_FULLSCREEN);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
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
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
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
			Intestazione intestazione = (Intestazione) data
					.getSerializableExtra("newCode");
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
		case SottoMenu.MODIFICA_NUMERI_UTILI:
			return sottoMenu.modificaNumeriUtili();
		case SottoMenu.ABOUT_DIALOG:
			return sottoMenu.createAboutDialog();
		case SottoMenu.AGGIORNA_TURNI:
			return updateDataDialog();
		case SottoMenu.GESTIONE_POPUP:
			return sottoMenu.gestionePopup();
		}
		return null;
	}
	
	private Dialog updateDataDialog() {
		return new AlertDialog.Builder(this)
				.setTitle("AGGIORNARE GLI ARCHIVI DI FARMACIE E TURNI?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean copyFileFromRemote = copyFileFromRemote();
						// COPIA FILE DA SD A MEMORIA INTERNA
						if (!copyFileFromRemote){
							System.out.println("Aggiornamento remoto fallito, provo da scheda Sd");
							copyFileFromSdCard();
						}
						// AGGIORNARE FILE DATI
						displayInfo();
					}
				})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
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
	
	private boolean copyFileFromRemote() {
		boolean copied = AggiornamentoFileDaRemoto.downloadAndCopyFiles(this);
		if (copied)
			createAlert("AGGIORNAMENTO AVVENUTO CON SUCCESSO");
		else
			createAlert("ERRORE DURANTE L'AGGIORNAMENTO DEI DATI");
		
		return copied;
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
		case R.id.menu_mod_numeri_utili:
			showDialog(SottoMenu.MODIFICA_NUMERI_UTILI);
			return true;
		case R.id.menu_about:
			showDialog(SottoMenu.ABOUT_DIALOG);
			return true;
		case R.id.menu_cambia_file_turni:
			showDialog(SottoMenu.AGGIORNA_TURNI);
			return true;
//		case R.id.menu_gestione_popup:
//			showDialog(SottoMenu.GESTIONE_POPUP);
//			return true;
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
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			activity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	public DataLoader getLoader() {
		return loader;
	}

}
