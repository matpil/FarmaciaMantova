package com.matpil.farmacia.model;

public class Farmacia {

	private String codice;
	private String nome;
	private String localitÓ;
	private String indirizzo;
	private String telefono;
	private String note;

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLocalitÓ() {
		return localitÓ;
	}

	public void setLocalitÓ(String localitÓ) {
		this.localitÓ = localitÓ;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	@Override
	public String toString() {
		return String.format("%s - %s ", getCodice(), getNote());
	}
}
