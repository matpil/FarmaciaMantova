package com.matpil.farmacia.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.matpil.farmacia.R;
import com.matpil.farmacia.model.Intestazione;

public class CreaIntestazione extends Activity {
	
	private final static String INTESTAZIONE = "Intestazione";
	private final static String INTESTAZIONE_NOME_FARMACIA = "IntestazioneNomeFarmacia";
	private final static String INTESTAZIONE_INDIRIZZO = "IntestazioneIndirizzo";
	private final static String INTESTAZIONE_TELEFONO = "IntestazioneTelefono";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intestazione);
		
		caricaDatiIntestazione();
		
		Button salva = (Button) findViewById(R.id.buttonSalva);
		Button annulla = (Button) findViewById(R.id.buttonAnnulla);
		
		final Intestazione intestazione = new Intestazione();
		
		salva.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText nome = (EditText) findViewById(R.id.editNomeFarmacia);
				EditText indirizzo = (EditText) findViewById(R.id.editIndirizzo);
				EditText telefono = (EditText) findViewById(R.id.editTelefono);
				intestazione.setNomeFarmacia(nome.getText().toString());
				intestazione.setIndirizzo(indirizzo.getText().toString());
				intestazione.setTelefono(telefono.getText().toString());
				intestazione.setModificato(true);
				
				
				Intent intent = new Intent();
				intent.putExtra("newCode", intestazione);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		
		annulla.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intestazione.setModificato(false);				
				
				
				Intent intent = new Intent();
				intent.putExtra("newCode", intestazione);
				setResult(Activity.RESULT_CANCELED, intent);
				finish();
			}
		});		
	}
	
	private void caricaDatiIntestazione() {
		SharedPreferences prefs = this.getSharedPreferences(INTESTAZIONE, Context.MODE_PRIVATE);
		String nomeFarmacia = prefs.getString(INTESTAZIONE_NOME_FARMACIA, null);
		String indirizzo = prefs.getString(INTESTAZIONE_INDIRIZZO, null);
		String telefono = prefs.getString(INTESTAZIONE_TELEFONO, null);
		Intestazione intestazione =  new Intestazione();
		intestazione.setNomeFarmacia(nomeFarmacia);
		intestazione.setIndirizzo(indirizzo);
		intestazione.setTelefono(telefono);
		if (intestazione.isModificato())
			visualizzaDatiIntestazione(intestazione);
	}

	private void visualizzaDatiIntestazione(Intestazione intestazione) {
		EditText nome = (EditText) findViewById(R.id.editNomeFarmacia);
		EditText indirizzo = (EditText) findViewById(R.id.editIndirizzo);
		EditText telefono = (EditText) findViewById(R.id.editTelefono);
		nome.setText(intestazione.getNomeFarmacia());
		indirizzo.setText(intestazione.getIndirizzo());
		telefono.setText(intestazione.getTelefono());
		
	}
}
