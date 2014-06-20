package com.matpil.farmacia.model;

import java.io.Serializable;

public class Intestazione implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nomeFarmacia;
	private String indirizzo;
	private String telefono;
	private boolean modificato; 

	public String getNomeFarmacia() {
		return nomeFarmacia;
	}

	public void setNomeFarmacia(String nomeFarmacia) {
		this.nomeFarmacia = nomeFarmacia;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public boolean isModificato() {
		modificato = getNomeFarmacia() != null && getIndirizzo() != null && getTelefono() != null;
		return modificato;
	}

	public void setModificato(boolean modificato) {
		this.modificato = modificato;
	}

}
