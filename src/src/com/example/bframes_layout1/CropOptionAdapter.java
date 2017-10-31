package com.example.bframes_layout1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;

import java.util.ArrayList;


public class CropOptionAdapter extends ArrayAdapter<CropOption> {
	private ArrayList<CropOption> mOptions;
	private LayoutInflater mInflater;
	
	public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
		super(context, R.layout.rcrop_activity, options);
		
		mOptions 	= options;
		
		mInflater	= LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.rcrop_activity, null);
		
		CropOption item = mOptions.get(position);
		
		if (item != null) {
			((ImageView) convertView.findViewById(R.id.iv_icon)).setImageDrawable(item.icon);
			((TextView) convertView.findViewById(R.id.tv_name)).setText(item.title);
			
			return convertView;
		}
		
		return null;
	}
}