package com.adarshhasija.blindlinks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.adarshhasija.blindlinks.R;
import com.parse.ParseObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordAdapter extends ArrayAdapter<ParseObject> {

	private final Context context;
	private final List<ParseObject> recordList;
	static class ViewHolderRecord {
	    TextView studentView;
	    TextView subjectView;
	    TextView dateTimeView;
	    TextView updatedAtDateView;
	    ImageView iconView;
	}
	
	/*
	 * private function to controller what is and isnt visible
	 * 
	 */
	private void visibilitySettings(ViewHolderRecord viewHolder)
	{
		viewHolder.updatedAtDateView.setVisibility(View.GONE);
		viewHolder.subjectView.setVisibility(View.GONE);
	}
	
	/*
	 * Private function to handle logic for setting dateTime value as string
	 * 
	 */
	private String getDateValueAsString(Date dateTime) 
	{
		Calendar c = Calendar.getInstance();
		int cur_date = c.get(Calendar.DATE);
		c.setTime(dateTime);
		int record_date = c.get(Calendar.DATE);
		int month = c.get(Calendar.MONTH);
		String monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		int year = c.get(Calendar.YEAR);

		String final_date;
		if(cur_date == record_date) {
			final_date = "TODAY";
		}
		else if(record_date == cur_date - 1) {
			final_date = "YESTERDAY";
		}
		else if(record_date == cur_date + 1) {
			final_date = "TOMORROW";
		}
		else {
			final_date = monthString + " " + Integer.toString(record_date);
		}
		
		return final_date;
	}
	
	private int getStatusIcon(ParseObject record, ViewHolderRecord viewHolder)
	{
		int imageResource;
		
		if(record.getString("status").equals("accepted")) {
			imageResource = R.drawable.ic_action_accept;
		}
		else if(record.getString("status").equals("rejected")) {
			imageResource = R.drawable.ic_action_cancel;
		}
		else {
			imageResource = R.drawable.ic_action_event;
		}
		return imageResource;
	}
	
	public RecordAdapter(Context context, int resource, List<ParseObject> values) {
		super(context, resource, values);
		this.context = context;
		this.recordList = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolderRecord viewHolder;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.record_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.studentView = (TextView) convertView.findViewById(R.id.student);
			viewHolder.subjectView = (TextView) convertView.findViewById(R.id.subject);
			viewHolder.dateTimeView = (TextView) convertView.findViewById(R.id.recordDateTime);
			viewHolder.updatedAtDateView = (TextView) convertView.findViewById(R.id.updatedAtDate);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolderRecord) convertView.getTag();
		}
		
		visibilitySettings(viewHolder);
		
	    ParseObject record = recordList.get(position);
		if(record != null) {
			int imageResource = getStatusIcon(record, viewHolder);
			viewHolder.iconView.setImageResource(imageResource);
			viewHolder.iconView.setContentDescription("Icon: "+record.getString("status"));

			viewHolder.studentView.setText(record.getString("student"));
			viewHolder.studentView.setContentDescription("Student: "+record.getString("student"));
			viewHolder.subjectView.setText(record.getString("subject"));
			viewHolder.subjectView.setContentDescription("Subject: "+record.getString("subject"));
			String recordDateTime = getDateValueAsString(record.getDate("dateTime"));
			viewHolder.dateTimeView.setText(recordDateTime);
			viewHolder.dateTimeView.setContentDescription("Appointment on: "+recordDateTime);
			
			String updatedAt = getDateValueAsString(record.getUpdatedAt());
			viewHolder.updatedAtDateView.setText(updatedAt);
			viewHolder.updatedAtDateView.setContentDescription("Last modified: "+updatedAt);
			//viewHolder.categoryView.setTag(record);
		}
	    
	/*    String type = record.getType();
	    if(type != null) {
	    	if(type.equals("Debit")) {
	    		int id = getContext().getResources().getIdentifier(getContext().getPackageName()+":drawable/down_arrow", null, null);
	    		iconView.setImageResource(id);
	    	}
	    	else {
	    		int id = getContext().getResources().getIdentifier(getContext().getPackageName()+":drawable/up_arrow", null, null);
	    		iconView.setImageResource(id);
	    	}
	    	
	    } */

		return convertView;
	}

	
}
