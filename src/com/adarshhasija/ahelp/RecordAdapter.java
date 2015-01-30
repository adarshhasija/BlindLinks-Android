package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;

import com.adarshhasija.ahelp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordAdapter extends ArrayAdapter<ParseObject> implements Filterable {

	private final Context context;
	private List<ParseObject> recordList;
	private final List<ParseObject> backupList; //used when filtering is happening
	static class ViewHolderRecord {
	    TextView userView;
	    TextView subjectView;
	    TextView dateTimeView;
	    TextView lastActionView;
	    TextView updatedAtDateView;
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
	                    ParseUser creator = record.getParseUser("createdBy");
	                    try {
							creator.fetchFromLocalDatastore();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    String firstName = creator.getString("firstName").toLowerCase();
	                    String lastName = creator.getString("lastName").toLowerCase();
	                    String student = firstName + " " + lastName;
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
		viewHolder.subjectView.setVisibility(View.GONE);
		viewHolder.updatedAtDateView.setVisibility(View.GONE);
	}
	
	/*
	 * Private function to handle logic for setting dateTime value as string
	 * 
	 */
	private String getDateValueAsString(Date dateTime) 
	{
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		int record_date = c.get(Calendar.DATE);
		String monthString = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);

		String final_date;
	/*	if(cur_date == record_date) {
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
		}	*/
		final_date = monthString + " " + Integer.toString(record_date);
		
		return final_date;
	}
	
	private String examUpdateFormatted(ParseUser fromUser) {
		try {
			fromUser.fetchFromLocalDatastore();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(fromUser.equals(ParseUser.getCurrentUser())) {
			return "You changed the exam details";
		}
		
		return fromUser.getString("firstName") + " changed the exam details";
	}
	
	private String getLastActionFormatted(ParseObject action) {
		try {
			ParseUser currentUser = ParseUser.getCurrentUser();
			ParseUser fromUser = action.getParseUser("from");
			ParseUser toUser = action.getParseUser("to");
			String type = action.getString("type");
			String statusString = action.getString("statusString");
			if(type.equals("examUpdate")) {
				return examUpdateFormatted(fromUser);
			}
			fromUser.fetchFromLocalDatastore();
			toUser.fetchFromLocalDatastore();
			
			String sender = fromUser.getString("firstName");
			String receiver = toUser.getString("firstName");
			
			if(fromUser.equals(currentUser)) {
				sender = "You";
			}
			if(toUser.equals(currentUser)) {
				receiver = "you";
			}
			
			String finalResult = sender + " requested " + receiver;
			if(statusString != null) {
				if(statusString.equals("accepted")) {
					finalResult = sender + " " + statusString + " the request"; 
				}
			}
			
			return finalResult;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private int getStatusIcon(ParseObject record, ViewHolderRecord viewHolder)
	{
		int imageResource;
		
		if(record.getBoolean("status") == false) {
			imageResource = R.drawable.ic_action_event;
		}
		else if(record.getBoolean("status") == true) {
			imageResource = R.drawable.ic_action_accept;
		}
		else {
			imageResource = R.drawable.ic_action_event;
		}
	/*	if(record.getString("status").equals("accepted")) {
			imageResource = R.drawable.ic_action_accept;
		}
		else if(record.getString("status").equals("rejected")) {
			imageResource = R.drawable.ic_action_cancel;
		}
		else {
			imageResource = R.drawable.ic_action_event;
		}	*/
		return imageResource;
	}
	
	public RecordAdapter(Context context, int resource, List<ParseObject> values) {
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
			convertView = inflater.inflate(R.layout.record_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.userView = (TextView) convertView.findViewById(R.id.user);
			viewHolder.subjectView = (TextView) convertView.findViewById(R.id.subject);
			viewHolder.lastActionView = (TextView) convertView.findViewById(R.id.lastAction);
			viewHolder.dateTimeView = (TextView) convertView.findViewById(R.id.recordDateTime);
			viewHolder.updatedAtDateView = (TextView) convertView.findViewById(R.id.updatedAtDate);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolderRecord) convertView.getTag();
		}
		
		visibilitySettings(viewHolder);
		
	    ParseObject record = recordList.get(position);
	    ParseUser creator = record.getParseUser("createdBy");
	    List<ParseObject> actionList;
	    ParseObject lastAction=null;
	    try {
			creator.fetchFromLocalDatastore();
			ParseObject scribeRequestLocation = record.getParseObject("location");
			scribeRequestLocation.fetchFromLocalDatastore();
			actionList = record.getList("actions");
			lastAction = (ParseObject) actionList.get(actionList.size()-1); //GET THE MOST RECENT ACTION
			lastAction.fetchFromLocalDatastore();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(record != null) {
			if(lastAction != null) {
				int imageResource = getStatusIcon(record, viewHolder);
				viewHolder.iconView.setImageResource(imageResource);
			}
			
			viewHolder.userView.setText(creator.getString("firstName") + " " + creator.getString("lastName"));
			String recordDateTime = getDateValueAsString(record.getDate("dateTime"));
			viewHolder.dateTimeView.setText(recordDateTime);
			if(lastAction != null) {
				String lastActionString = getLastActionFormatted(lastAction);
				viewHolder.lastActionView.setText(lastActionString);
			}
			
			if(record.getObjectId() != null) {
				String updatedAt = getDateValueAsString(record.getUpdatedAt());
				viewHolder.updatedAtDateView.setText(updatedAt);
				viewHolder.updatedAtDateView.setContentDescription("Last modified: "+updatedAt);
			}
			String status = "needs scribe";
			if(record.getBoolean("status")) {
				status = "scribe found";
			}
			
			convertView.setContentDescription(creator.getString("firstName") + " " + 
												creator.getString("lastName") + " has an exam on " + 
													recordDateTime + ". Current status is: "+ status);
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
