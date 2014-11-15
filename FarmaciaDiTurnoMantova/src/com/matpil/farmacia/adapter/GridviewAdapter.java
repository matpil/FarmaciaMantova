package com.matpil.farmacia.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
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
		public TextView localitÓ;
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
			view.localitÓ = (TextView) convertView.findViewById(R.id.localitaTv);
			view.indirizzo = (TextView) convertView.findViewById(R.id.addressTv);
			view.telefono = (TextView) convertView.findViewById(R.id.TelTv);
			view.note = (TextView) convertView.findViewById(R.id.noteTv);

			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}

		Farmacia farmacia = listPharm.get(position);		
		if (farmacia != null) {
//			impostaTypeFace(view);
			view.nome.setText(farmacia.getNome());			
			view.localitÓ.setText(farmacia.getLocalitÓ());
			view.indirizzo.setText(farmacia.getIndirizzo());
			view.telefono.setText(farmacia.getTelefono());
			view.note.setText(farmacia.getNote());
			
		}
		return convertView;
	}
	
	private void impostaTypeFace(ViewHolder view) {
		Typeface tf = Typeface.createFromAsset(activity.getAssets(),"fonts/Agency_FB.ttf");	
		view.nome.setTypeface(tf);
		view.localitÓ.setTypeface(tf);
		view.indirizzo.setTypeface(tf);
		view.telefono.setTypeface(tf);
		view.note.setTypeface(tf);
	}
}
