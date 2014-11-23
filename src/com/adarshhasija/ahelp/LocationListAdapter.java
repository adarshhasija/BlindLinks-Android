package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.adarshhasija.ahelp.RecordAdapter.ViewHolderRecord;
import com.parse.ParseObject;

public class LocationListAdapter extends ArrayAdapter<ParseObject> implements Filterable {
	
	private final Context context;
	private List<ParseObject> recordList;
	private final List<ParseObject> backupList; //used when filtering is happening
	static class ViewHolderRecord {
	    TextView locationView;
	    ImageView iconView;
	    ImageView placeView;
	}
	
	/*
	 * private function to controller what is and isnt visible
	 * 
	 */
	private void visibilitySettings(ViewHolderRecord viewHolder)
	{
		viewHolder.placeView.setVisibility(View.GONE);
	}

	public LocationListAdapter(Context context, int resource, List<ParseObject> values) {
		super(context, resource, values);
		this.context = context;
		this.recordList = values;
		this.backupList = new ArrayList<ParseObject>(values);
	}
	
	@Override
	public int getCount() {
		return recordList != null?recordList.size() : 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolderRecord viewHolder;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.location_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.locationView = (TextView) convertView.findViewById(R.id.location);
			viewHolder.placeView = (ImageView) convertView.findViewById(R.id.place);
			convertView.setTag(viewHolder);
			
		}
		else {
			viewHolder = (ViewHolderRecord) convertView.getTag();
		}
		
		visibilitySettings(viewHolder);
		
	    ParseObject record = recordList.get(position);
		if(record != null) {
			viewHolder.locationView.setText(record.getString("name"));
			convertView.setContentDescription(record.getString("name"));
			//viewHolder.categoryView.setTag(record);
		}

		return convertView;
	}

}
