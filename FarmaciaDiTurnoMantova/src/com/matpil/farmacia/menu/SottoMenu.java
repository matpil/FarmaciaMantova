package com.matpil.farmacia.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class SottoMenu {

	public static final int ABOUT_DIALOG = 0;
	public static final int CAMBIA_COSTO_CHIAMATA = 1;
	public static final int CAMBIA_INTESTAZIONE = 2;
	public static final int AGGIORNA_TURNI = 3;
	public static final int CAMBIA_NUMERO_GUARDIA_MEDICA = 4;
	// Identificatore delle preferenze dell'applicazione
	private final static String MY_PREFERENCES = "MyPref";
	// Costante relativa al nome della particolare preferenza
	private final static String TEXT_DATA_KEY = "textData";
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
		return new AlertDialog.Builder(this.act).setTitle("CONTATTI").setMessage(Html.fromHtml(message)).create();
	}

	public Dialog createChangeTitleDialog() {
		titleText = new EditText(this.act);
		titleText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		titleText.setKeyListener(DigitsKeyListener.getInstance(false, true));
		return new AlertDialog.Builder(this.act).setTitle("Inserire valore costo chiamata urgenza").setView(titleText)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						savePreferencesData(titleText);
					}
				}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
	}
	
	public Dialog cambiaNumeroGuardiaMedica() {
		numeroGuardiMedicaText = new EditText(this.act);
		numeroGuardiMedicaText.setInputType(InputType.TYPE_CLASS_PHONE);
		numeroGuardiMedicaText.setKeyListener(DigitsKeyListener.getInstance(false, true));
		return new AlertDialog.Builder(this.act).setTitle("Inserire numero della guardia medica").setView(numeroGuardiMedicaText)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveAndReloadData(numeroGuardiMedicaText);
					}
				}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();		
	}

	protected void saveAndReloadData(View view) {
		SharedPreferences prefs = this.act.getSharedPreferences(ImportaFarmacieDaFile.GUARDIA_MEDICA, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		// Modifichiamo il valore con quello inserito nell'EditText
		EditText outputView = (EditText) view;
		CharSequence textData = outputView.getText();
		if (textData != null) {
			// Lo salviamo nelle Preferences
			editor.putString(ImportaFarmacieDaFile.NUMERO_GUARDIA_MEDICA, textData.toString());
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
		case CAMBIA_NUMERO_GUARDIA_MEDICA:
			dialog.isShowing();
		}
	}

	public void savePreferencesData(View view) {
		// Otteniamo il riferimento alle Preferences
		SharedPreferences prefs = this.act.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
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
		SharedPreferences prefs = this.act.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		// Leggiamo l'informazione associata alla proprietà TEXT_DATA
		String textData = prefs.getString(TEXT_DATA_KEY, "3.87");
		// Lo impostiamo alla TextView
		TextView outputView = (TextView) this.act.findViewById(R.id.chiamateUrgenzaCostoTv);
		outputView.setText(textData);
	}

	public void checkDefaultCallValue() {
		// Leggiamo le Preferences
		SharedPreferences prefs = this.act.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		// Leggiamo l'informazione associata alla proprietà TEXT_DATA
		String textData = prefs.getString(TEXT_DATA_KEY, null);
		if (textData == null)
			this.act.showDialog(CAMBIA_COSTO_CHIAMATA);
	}

}
