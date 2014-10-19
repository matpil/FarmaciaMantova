package com.matpil.farmacia.menu;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.matpil.farmacia.FullscreenActivity;
import com.matpil.farmacia.R;
import com.matpil.farmacia.parser.ImportaFarmacieDaFile;
import com.matpil.farmacia.service.PopUpBroadcastService;

public class SottoMenu {

	public static final int ABOUT_DIALOG = 0;
	public static final int CAMBIA_COSTO_CHIAMATA = 1;
	public static final int CAMBIA_INTESTAZIONE = 2;
	public static final int AGGIORNA_TURNI = 3;
	public static final int MODIFICA_NUMERI_UTILI = 4;
	public static final int GESTIONE_POPUP = 5;
	// Identificatore delle preferenze dell'applicazione
	private final static String MY_PREFERENCES = "MyPref";
	// Costante relativa al nome della particolare preferenza
	private final static String TEXT_DATA_KEY = "textData";
	// Costante per recupero stato popup
	private final static String LABEL_GESTIONE_POPUP = "POPUP";
	private EditText titleText;
	private EditText numeroGuardiMedicaText;
	private FullscreenActivity act;

	public FullscreenActivity getAct() {
		return act;
	}

	public void setAct(FullscreenActivity act) {
		this.act = act;
	}

	public SottoMenu(FullscreenActivity act) {
		this.act = act;
	}

	public Dialog createAboutDialog() {
		StringBuilder sb = new StringBuilder();
		sb.append("Matteo Pileggi<br>");
		sb.append("Skype: <a> matpil</a><br>");
		sb.append("Email : <a href='mailto://matpil@gmail.com'>matpil@gmail.com</a><br><br>");
		sb.append("Marco Pistoni <br>");
		sb.append("Skype: <a> pistoni.marco</a><br>");
		sb.append("Email : <a href='mailto://marco.pistoni@tin.it'>marco.pistoni@tin.it</a><br>");
		sb.append("Telefono: <a> 0376-98127</a>");
		String message = sb.toString();
		return new AlertDialog.Builder(this.act).setTitle("CONTATTI")
				.setMessage(Html.fromHtml(message)).create();
	}

	public Dialog createChangeTitleDialog() {
		titleText = new EditText(this.act);
		titleText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		titleText.setKeyListener(DigitsKeyListener.getInstance(false, true));
		return new AlertDialog.Builder(this.act)
				.setTitle("Inserire valore costo chiamata urgenza")
				.setView(titleText)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						savePreferencesData(titleText);
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

	public Dialog modificaNumeriUtili() {
		numeroGuardiMedicaText = new EditText(this.act);
		numeroGuardiMedicaText.setInputType(InputType.TYPE_CLASS_PHONE);
		numeroGuardiMedicaText.setKeyListener(DigitsKeyListener.getInstance(
				false, true));
		 Dialog dialog = new Dialog(this.act);
		 dialog.setContentView(R.layout.post_it_numeri_utili);
		 dialog.setTitle("Modificatore campi per numeri utili");
		 
//		Dialog d = new AlertDialog.Builder(this.act)
//				.setTitle("Inserire numero della guardia medica")
//				.setView(R.layout.post_it_numeri_utili)
//				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						saveAndReloadData(numeroGuardiMedicaText);
//					}
//				})
//				.setNegativeButton("CANCEL",
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//							}
//						}).create();
//		d.setContentView(R.layout.post_it_numeri_utili);
		return dialog;
	}

	public Dialog gestionePopup() {
		SharedPreferences prefs = getAct().getSharedPreferences(
				"GESTIONE_POPUP", Context.MODE_PRIVATE);
		Boolean popupActive = prefs.getBoolean(LABEL_GESTIONE_POPUP, true);
		System.out.println("1 ---> " + popupActive);
		String message = null;
		if (popupActive) {
			getAct().stopService(
					new Intent(getAct(), PopUpBroadcastService.class));
			popupActive = false;
			message = "POPUP DISATTIVATI";
		} else {
			getAct().startService(
					new Intent(getAct(), PopUpBroadcastService.class));
			popupActive = true;
			message = "POPUP ATTIVATI";
		}
		System.out.println("2 ---> " + popupActive);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(LABEL_GESTIONE_POPUP, popupActive);
		edit.apply();
		edit.commit();
		return new AlertDialog.Builder(getAct())
				.setTitle(message)
				.setNeutralButton("CONTINUA",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing...
							}
						}).create();
	}

	protected void saveAndReloadData(View view) {
		SharedPreferences prefs = this.act.getSharedPreferences(
				ImportaFarmacieDaFile.GUARDIA_MEDICA, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		// Modifichiamo il valore con quello inserito nell'EditText
		EditText outputView = (EditText) view;
		CharSequence textData = outputView.getText();
		if (textData != null) {
			// Lo salviamo nelle Preferences
			editor.putString(ImportaFarmacieDaFile.NUMERO_GUARDIA_MEDICA,
					textData.toString());
			editor.commit();
		}
		this.act.getLoader().ricaricaDati();
		this.act.showData();
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case CAMBIA_INTESTAZIONE:
			dialog.isShowing();
		case CAMBIA_COSTO_CHIAMATA:
			dialog.isShowing();
		case AGGIORNA_TURNI:
			dialog.isShowing();
		case MODIFICA_NUMERI_UTILI:
			dialog.isShowing();
		}
	}

	public void savePreferencesData(View view) {
		// Otteniamo il riferimento alle Preferences
		SharedPreferences prefs = this.act.getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// Otteniamo il corrispondente Editor
		SharedPreferences.Editor editor = prefs.edit();
		// Modifichiamo il valore con quello inserito nell'EditText
		EditText outputView = (EditText) view;
		CharSequence textData = outputView.getText();
		if (textData != null) {
			// Lo salviamo nelle Preferences
			editor.putString(TEXT_DATA_KEY, textData.toString());
			editor.commit();
		}
		updatePreferencesData();
	}

	public void updatePreferencesData() {
		// Leggiamo le Preferences
		SharedPreferences prefs = this.act.getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// Leggiamo l'informazione associata alla proprietà TEXT_DATA
		String textData = prefs.getString(TEXT_DATA_KEY, "3.87");
		// Lo impostiamo alla TextView
		TextView outputView = (TextView) this.act
				.findViewById(R.id.chiamateUrgenzaCostoTv);
		outputView.setText(textData);
	}

	public void checkDefaultCallValue() {
		// Leggiamo le Preferences
		SharedPreferences prefs = this.act.getSharedPreferences(MY_PREFERENCES,
				Context.MODE_PRIVATE);
		// Leggiamo l'informazione associata alla proprietà TEXT_DATA
		String textData = prefs.getString(TEXT_DATA_KEY, null);
		if (textData == null)
			this.act.showDialog(CAMBIA_COSTO_CHIAMATA);
	}

}
