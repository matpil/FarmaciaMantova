package com.matpil.farmacia.model;

import java.util.List;

public class InfoFarmacie {

	private String timeUpdate;
	private List<Farmacia> listPharm;

	public String getTimeUpdate() {
		return timeUpdate;
	}

	public void setTimeUpdate(String timeUpdate) {
		this.timeUpdate = timeUpdate;
	}

	public List<Farmacia> getListPharm() {
		return listPharm;
	}

	public void setListPharm(List<Farmacia> listPharm) {
		this.listPharm = listPharm;
	}

}
