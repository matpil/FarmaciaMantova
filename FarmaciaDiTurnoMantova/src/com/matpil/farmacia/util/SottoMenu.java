package com.matpil.farmacia.util;

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

public class SottoMenu {

	public static final int ABOUT_DIALOG = 0;
	public static final int CAMBIA_COSTO_CHIAMATA = 1;
	public static final int CAMBIA_INTESTAZIONE = 2;
	public static final int AGGIORNA_TURNI = 3;
	// Identificatore delle preferenze dell'applicazione
	private final static String MY_PREFERENCES = "MyPref";
	// Costante relativa al nome della particolare preferenza
	private final static String TEXT_DATA_KEY = "textData";
	private EditText titleText;
	private FullscreenActivity act;

	// private String[] mFileList;
	// private File mPath = Environment.getExternalStorageDirectory();
	// private String mChosenFile;
	// private static final String FTYPE = ".csv";
	// private static final int DIALOG_LOAD_FILE = 1000;

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
		String message = "Skype: <a> matpil</a><br>" + "Twitter: <a href='https://twitter.com/matpil81'>@matpil81</a><br>"
				+ "Email : <a href='mailto://matpil@gmail.com'>matpil@gmail.com</a>";
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
						// setTitle(titleText.getText());
						// valoreBuono.setText(titleText.getText());
						savePreferencesData(titleText);
					}
				}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case CAMBIA_INTESTAZIONE:
			dialog.isShowing();
		case CAMBIA_COSTO_CHIAMATA:
			dialog.isShowing();
		case AGGIORNA_TURNI:
			dialog.isShowing();
		}
	}

//	File fileSelected = null;
//	
//	public Dialog chooseFile() {
//		FileChooserDialog dialog = new FileChooserDialog(this.act);
//		
//		dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
//			public void onFileSelected(Dialog source, File file) {
//				source.hide();
//				Toast toast = Toast.makeText(source.getContext(), "File selected: " + file.getName(), Toast.LENGTH_LONG);
//				toast.show();
//				System.out.println("FILE SELEZIONATO " + file.getPath());
//				act.loadDataFromFile(file.getPath());
//			}
//
//			public void onFileSelected(Dialog source, File folder, String name) {
//				source.hide();
//				Toast toast = Toast.makeText(source.getContext(), "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
//				toast.show();
//			}
//		});
//		dialog.setShowConfirmation(true, false);
//		dialog.show();
//		return dialog;
//	}

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
		System.out.println("checkDefaultTicketValue -> " + textData);
		if (textData == null)
			this.act.showDialog(CAMBIA_COSTO_CHIAMATA);
	}

}
