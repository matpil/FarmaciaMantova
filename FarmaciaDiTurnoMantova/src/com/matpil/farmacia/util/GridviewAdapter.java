package com.matpil.farmacia.util;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.matpil.farmacia.R;
import com.matpil.farmacia.model.Farmacia;

public class GridviewAdapter extends BaseAdapter {
	private List<Farmacia> listPharm;
	private Activity activity;

	public GridviewAdapter(Activity activity, List<Farmacia> listPharm) {
		super();
		this.listPharm = listPharm;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		if (listPharm != null)
			return listPharm.size();
		return 0;
	}

	@Override
	public Farmacia getItem(int position) {
		return listPharm.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public static class ViewHolder {
		public TextView nome;
		public TextView località;
		public TextView indirizzo;
		public TextView telefono;
		public TextView note;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view;
		LayoutInflater inflator = activity.getLayoutInflater();

		if (convertView == null) {
			view = new ViewHolder();
			convertView = inflator.inflate(R.layout.post_it_farm, null);

			view.nome = (TextView) convertView.findViewById(R.id.nomeTv);
			view.località = (TextView) convertView.findViewById(R.id.localitaTv);
			view.indirizzo = (TextView) convertView.findViewById(R.id.addressTv);
			view.telefono = (TextView) convertView.findViewById(R.id.TelTv);
			view.note = (TextView) convertView.findViewById(R.id.noteTv);

			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}

		// view.txtViewTitle.setText(listCountry.get(position));
		// view.imgViewFlag.setImageResource(listFlag.get(position));
		Farmacia farmacia = listPharm.get(position);
		if (farmacia != null) {
			view.nome.setText(farmacia.getNome());
			view.località.setText(farmacia.getLocalità());
			view.indirizzo.setText(farmacia.getIndirizzo());
			view.telefono.setText(farmacia.getTelefono());
			view.note.setText(farmacia.getNote());
		}
		return convertView;
	}
}
