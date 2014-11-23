package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.parse.ParseObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class TodayRecordAdapter extends ArrayAdapter<ParseObject> implements Filterable {
	
	private final Context context;
	private List<ParseObject> recordList;
	private final List<ParseObject> backupList; //used when filtering is happening
	static class ViewHolderRecord {
	    TextView studentView;
	    TextView locationView;
	    TextView timeView;
	    ImageView iconView;
	}
	
	/*
	 * my custom Filter
	 * 
	 * 
	 */
	Filter filter = new Filter() {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			//If recordList size is less, filtering has happened
			//restore original before continuing
			if(recordList.size() < backupList.size()) {
				restoreOriginalList();
			}
			FilterResults filterResults = new FilterResults();   
	         ArrayList<ParseObject> tempList=new ArrayList<ParseObject>();
	         //constraint is the result from text you want to filter against. 
	         //objects is your data set you will filter from
	         if(constraint != null && recordList !=null) {
	             int length=recordList.size();
	             int i=0;
	                while(i<length){
	                    ParseObject record=recordList.get(i);
	                    String student = record.getString("student").toLowerCase();
	                    if(student.contains(constraint)) {
	                    	tempList.add(record);
	                    }
	                    i++;
	                }
	                //following two lines is very important
	                //as publish result can only take FilterResults objects
	                filterResults.values = tempList;
	                filterResults.count = tempList.size();
	          }
	          return filterResults;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			recordList = (ArrayList<ParseObject>) results.values;
	          if (recordList.size() > 0) {
	           notifyDataSetChanged();
	          } else {
	              notifyDataSetInvalidated();
	          }
			
		}
		
	};
	
	/*
	 * private function to controller what is and isnt visible
	 * 
	 */
	private void visibilitySettings(ViewHolderRecord viewHolder)
	{
		
	}
	
	/*
	 * Private function to handle logic for setting dateTime value as string
	 * 
	 */
	private String getTimeValueAsString(Date dateTime) 
	{
		Calendar c = Calendar.getInstance();
		int cur_date = c.get(Calendar.DATE);
		c.setTime(dateTime);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if(hour > 12) hour = hour - 12;
		else if(hour == 0) hour = 12;
		int minute = c.get(Calendar.MINUTE);
		String minuteString = (minute < 10)?"0"+Integer.toString(minute):Integer.toString(minute);
		
		String final_time = hour + ":" + minute + " " + c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
		
		return final_time;
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
		
		

	public TodayRecordAdapter(Context context, int resource, List<ParseObject> values) {
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
			convertView = inflater.inflate(R.layout.today_record_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.studentView = (TextView) convertView.findViewById(R.id.student);
			viewHolder.locationView = (TextView) convertView.findViewById(R.id.subject);
			viewHolder.timeView = (TextView) convertView.findViewById(R.id.recordTime);
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
			viewHolder.locationView.setText(record.getString("location"));
			viewHolder.locationView.setContentDescription("Location: "+record.getString("location"));
			
			String recordTime = getTimeValueAsString(record.getDate("dateTime"));
			viewHolder.timeView.setText(recordTime);
			viewHolder.timeView.setContentDescription("Appointment at: "+recordTime);
			
			//viewHolder.categoryView.setTag(record);
		}

		return convertView;
	}
	
	@Override
	public Filter getFilter() {
		return filter;
	}
	
	private void restoreOriginalList() {
		recordList.clear();
		recordList.addAll(backupList);
	}

}
