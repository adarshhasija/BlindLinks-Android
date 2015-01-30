package com.adarshhasija.ahelp;

import java.util.List;

import com.adarshhasija.ahelp.RecordAdapter.ViewHolderRecord;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionListAdapter extends ArrayAdapter<ParseObject> {
	
	private final Context context;
	private List<ParseObject> recordList;
	static class ViewHolderRecord {
		String type;
		ImageView iconView;
		String parseId;
		String uuid;
	    TextView actionView;
	}
	
	/*
	 * private function to controller what is and isnt visible
	 * 
	 */
	private void visibilitySettings(ViewHolderRecord viewHolder)
	{

	}

	public ActionListAdapter(Context context, int resource, List<ParseObject> values) {
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
			convertView = inflater.inflate(R.layout.action_row_layout, parent, false);
			
			viewHolder = new ViewHolderRecord();
			viewHolder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.actionView = (TextView) convertView.findViewById(R.id.action);
			convertView.setTag(viewHolder);
			
		}
		else {
			viewHolder = (ViewHolderRecord) convertView.getTag();
		}
		
		visibilitySettings(viewHolder);
		
		ParseObject record = recordList.get(position);
		if(record != null) {
			ParseUser fromUser = record.getParseUser("from");
			ParseUser toUser = record.getParseUser("to");
			String type = record.getString("type");
			String status = record.getString("statusString");
			String typeString="";
			if(type.equals("request")) {
				typeString = "requested";
			}
			String actionString="";
			String contentDescription="";
			try {
				fromUser.fetchFromLocalDatastore();
				if(type.equals("examUpdate")) {
					if(fromUser.equals(ParseUser.getCurrentUser())) {
						actionString = "You changed the exam details";
					}
					else {
						actionString = fromUser.getString("firstName") + " changed the exam details";
						contentDescription = actionString;
					}
				}
				else {
					toUser.fetchFromLocalDatastore();
					String sender = fromUser.getString("firstName") + " " + fromUser.getString("lastName");
					String receiver = toUser.getString("lastName") + " " + toUser.getString("lastName");
					if(fromUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
						sender = "You";
					}
					if(toUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
						receiver = "you";
					}
					
					if(status != null) {
						if(status.equals("accepted")) {
							actionString = receiver + " accepted the request";
						}
						else if(status.equals("rejected")) {
							actionString = sender + " rejected the request";
						}
					}
					else {
						actionString = sender + " " + 
									typeString + " " + receiver;
					}
				
					if(record.getString("statusString") == null) {
						contentDescription = actionString + ". Currently waiting for response";
					}
					else {
						contentDescription = actionString + ". Current status is " + record.getString("statusString");
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			viewHolder.parseId = record.getObjectId();
			viewHolder.uuid = record.getString("uuid");
			viewHolder.type = record.getString("type");
			viewHolder.actionView.setText(actionString);
			String statusString = record.getString("statusString");
			if(statusString != null) {
				if(statusString.equals("accepted")) {
					viewHolder.iconView.setImageResource(R.drawable.ic_action_accept);
				}
				else if(statusString.equals("rejected")){
					viewHolder.iconView.setImageResource(R.drawable.ic_action_cancel);
				}
				else {
					viewHolder.iconView.setImageResource(R.drawable.ic_action_event);
				}
			}
			convertView.setContentDescription(contentDescription);
			//viewHolder.categoryView.setTag(record);
		}

		return convertView;
	}
	

}
