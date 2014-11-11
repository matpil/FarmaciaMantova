package com.matpil.farmacia.util;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import android.content.Context;

import com.matpil.farmacia.model.Farmacia;
import com.matpil.farmacia.model.InfoFarmacie;
import com.matpil.farmacia.parser.ImportaFarmacieDaFile;

public class DataLoader {

	private Context context = null;
	private Date dataCorrente = null;
	
	private Map<String, Farmacia> farmacie = null;
	private Map<String, InfoFarmacie> turni = null;

	public DataLoader(Context context) {
		this.context = context;
	}

	public boolean caricaDati() {
		if (this.farmacie != null && this.turni != null)
			return true;		
		return ricaricaDati();
	}
	
	public boolean ricaricaDati() {
		boolean caricamentoOk = false;
		try {
			this.farmacie = ImportaFarmacieDaFile.readPharmFile(this.context);
			this.turni = ImportaFarmacieDaFile.readTurniFile(this.context, this.farmacie);
			caricamentoOk = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return caricamentoOk;
	}

	public String recuperaOrarioAperturaPerGiorno(String giorno) {
		System.out.println("GIORNO -> " + giorno);
		InfoFarmacie iF = getListPharm(giorno);
		return iF != null ? iF.getTimeUpdate() : null;
	}

	public InfoFarmacie recuperaListaFarmaciePerGiorno(String startHour) {		
		InfoFarmacie infoFarmacie = null;
		this.dataCorrente = TimeHelper.retrieveDate(startHour);
		String dateFormatted = TimeHelper.retrieveDateFormatted("dd/MM/yyyy", this.dataCorrente);
		infoFarmacie = getListPharm(dateFormatted);
		String timeUpdate = infoFarmacie.getTimeUpdate();
		Date verifyDate = TimeHelper.retrieveDate(timeUpdate);
		String verifiedDateFormatted = TimeHelper.retrieveDateFormatted("dd/MM/yyyy", verifyDate);
		if (!dateFormatted.equals(verifiedDateFormatted)) {
			infoFarmacie = getListPharm(verifiedDateFormatted);
			this.dataCorrente = verifyDate;
		}
		return infoFarmacie;
	}

	private InfoFarmacie getListPharm(String dateFormatted) {
		return this.turni.get(dateFormatted);
	}
	
	public Date getDataCorrente() {
		return dataCorrente;
	}

	public void cleanDataCorrente() {
		this.dataCorrente = null;	
	}

}
