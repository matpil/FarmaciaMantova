package com.matpil.farmacia.intestazione;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.matpil.farmacia.R;
import com.matpil.farmacia.model.Intestazione;

public class GestioneIntestazione {
	
	private final static String INTESTAZIONE = "Intestazione";
	private final static String INTESTAZIONE_NOME_FARMACIA = "IntestazioneNomeFarmacia";
	private final static String INTESTAZIONE_INDIRIZZO = "IntestazioneIndirizzo";
	private final static String INTESTAZIONE_TELEFONO = "IntestazioneTelefono";
	
	
	public static void aggiornaIntestazione(Intestazione intestazione, Activity activity) {
		if (intestazione != null) {
			((TextView) activity.findViewById(R.id.nomeFarmaciaTV)).setText(intestazione.getNomeFarmacia());
			((TextView) activity.findViewById(R.id.indirizzoTV)).setText(intestazione.getIndirizzo());
			String tel = String.format("tel. %s", intestazione.getTelefono());
			((TextView) activity.findViewById(R.id.telefonoTV)).setText(tel);
		}
	}

	public static void salvaIntestazione(Intestazione intestazione, Activity activity) {
		// Otteniamo il riferimento alle Preferences
		SharedPreferences prefs = activity.getSharedPreferences(INTESTAZIONE, Context.MODE_PRIVATE);
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
		aggiornaIntestazione(intestazione, activity);
	}

	public static void caricaIntestazione(Activity activity) {
		SharedPreferences prefs = activity.getSharedPreferences(INTESTAZIONE, Context.MODE_PRIVATE);
		String nomeFarmacia = prefs.getString(INTESTAZIONE_NOME_FARMACIA, null);
		String indirizzo = prefs.getString(INTESTAZIONE_INDIRIZZO, null);
		String telefono = prefs.getString(INTESTAZIONE_TELEFONO, null);
		Intestazione intestazione = new Intestazione();
		intestazione.setNomeFarmacia(nomeFarmacia);
		intestazione.setIndirizzo(indirizzo);
		intestazione.setTelefono(telefono);
		if (intestazione.isModificato())
			aggiornaIntestazione(intestazione, activity);
	}	

}
