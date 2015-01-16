package com.adarshhasija.ahelp;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ExamChangesActivity extends Activity {
	
	private String parseId=null;
	private String uuid=null;
	
	/*
	 * private functions
	 * 
	 * 
	 */
	private ParseObject getActionObject() {
		ParseObject parseObject=null;
		ParseQuery query = ParseQuery.getQuery("Action");
		query.fromLocalDatastore();
		try {
			if(parseId != null) {
				parseObject = query.get(parseId);
			}
			else if(uuid != null) {
				query.whereEqualTo("uuid", uuid);
				parseObject = query.getFirst();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parseObject;
	}
	
	private String dateTimeFormatted(Calendar c) {
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if(hour > 12) hour = hour - 12;
		else if(hour == 0) hour = 12;
		int minute = c.get(Calendar.MINUTE);
		String minuteString = (minute < 10)?"0"+Integer.toString(minute):Integer.toString(minute);
		String dateTimeString = c.get(Calendar.DATE) + " " + 
				c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " +
				c.get(Calendar.YEAR) + " " +
				hour + ":" +
				minuteString + " " +
				c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.US);
		
		return dateTimeString;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.exam_changes_activity);
		
		Bundle extras = getIntent().getExtras();
		parseId = extras.getString("parseId");
		uuid = extras.getString("uuid");
		ParseObject parseObject = getActionObject();
		
		try {
			Date oldDate = parseObject.getDate("oldDate");
			Calendar oldCalendar = Calendar.getInstance();
			oldCalendar.setTime(oldDate);
			Date newDate = parseObject.getDate("newDate");
			Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(newDate);
			ParseObject oldLocation = parseObject.getParseObject("oldLocation");
			ParseObject newLocation = parseObject.getParseObject("newLocation");
			oldLocation.fetchFromLocalDatastore();
			newLocation.fetchFromLocalDatastore();
			String oldSubject = parseObject.getString("oldSubject");
			String newSubject = parseObject.getString("newSubject");
			String oldNotes = parseObject.getString("oldNotes");
			String newNotes = parseObject.getString("newNotes");
			
			TextView dateView = (TextView) findViewById(R.id.dateTime);
			TextView locationView = (TextView) findViewById(R.id.location);
			TextView subjectView = (TextView) findViewById(R.id.subject);
			TextView notesView = (TextView) findViewById(R.id.notes);
			
			if(oldCalendar.getTimeInMillis() != newCalendar.getTimeInMillis()) {
				String oldDateText = dateTimeFormatted(oldCalendar);
				String newDateText = dateTimeFormatted(newCalendar);
				dateView.setText("DATE\nFROM "+oldDateText+"\nTO "+newDateText);
				dateView.setContentDescription("DATE FROM "+oldDateText+" TO "+newDateText);
			}
			else {
				dateView.setVisibility(View.GONE);
			}
			
			if(!oldLocation.getString("title").equals(newLocation.getString("title"))) {
				String locationString = "LOCATION\nFROM " + oldLocation.getString("title") + "\nTO " +
										newLocation.getString("title");
				String locationContentDescription = "LOCATION FROM " + oldLocation.getString("title") + " TO " +
						newLocation.getString("title");
				locationView.setText(locationString);
				locationView.setContentDescription(locationContentDescription);
			}
			else {
				locationView.setVisibility(View.GONE);
			}
			
			if(!oldSubject.equalsIgnoreCase(newSubject)) {
				subjectView.setText("SUBJECT\nFROM " + oldSubject + "\nTO " + newSubject);
				subjectView.setContentDescription("SUBJECT FROM "+oldSubject + " TO " + newSubject);
			}
			else {
				subjectView.setVisibility(View.GONE);
			}
			
			if(oldNotes == null && newNotes == null) {
				notesView.setVisibility(View.GONE);
			}
			else if(oldNotes == null && newNotes != null) {
				notesView.setText("NOTES ADDED: " + newNotes);
				notesView.setContentDescription("NOTES ADDED: " + newNotes);
			}
			else if(oldNotes != null && newNotes == null) {
				notesView.setText("NOTES REMOVED: " + oldNotes);
				notesView.setContentDescription("NOTES REMOVED: " + oldNotes);
			}
			else if(!oldNotes.equalsIgnoreCase(newNotes)) {
				notesView.setText("NOTES\nFROM "+ oldNotes + "\nTO " + newNotes);
				notesView.setContentDescription("NOTES FROM "+ oldNotes + " TO " + newNotes);
			}
			else {
				notesView.setVisibility(View.GONE);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
